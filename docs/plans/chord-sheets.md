# Plan: Chord sheets powered by Ami music SDK

Implements [the chord-sheets spec](../specs/chord-sheets.md). Measures gain harmonies, lyrics, text directions, line
breaks, section starts, time signature, key, tempo and pickups. The SDK writes canonical JSON, accumulates validation
errors in a serializable shape and exposes its version at runtime.

## Conventions for every task

- **Schema changes are made in `../ami-music-spec`.** When a task changes the schema:
  1. Edit `main.tsp`, and `examples.tsp` where the task says so.
  2. Compile with `npx tsp compile .`.
  3. Copy `tsp-output/schema/openapi.yaml` to `openapi/ami-music-spec.yaml`.
  4. Run `./gradlew openApiGenerate`, and also `./gradlew extractOpenApiExamples` when examples changed.
  5. Commit the TypeSpec change in `ami-music-spec`, next to the SDK commit that includes the regenerated `generated/`.

  Each task adds only the models it maps and validates, so the schema never gets ahead of the SDK.
- **A task is done when `./gradlew check` passes** (JVM, JS and the compatibility suites), with `spotlessCheck` clean.
- **New code goes in commonMain, and its tests in commonTest** (F12). The exception is tests that read files from the
  repository: they go in jvmTest, like `ExamplesTest`.
- **New model types are `@Serializable`,** like the existing ones, because `Measure` is.
- **Never edit `compatibility-tests/fixtures/`.**
- F…, D… and § references point into the spec.

## Tasks

Rules for the tasks below:

- Each task is a `### Task N: ...` heading followed by `- [ ]` subtasks. The checkboxes are the only progress signal.
- Every subtask is one completable thing that can be checked off. Integrate the change and its tests in one subtask.
  Do not describe changes in prose as a subtask.
- A task is independently completable and reviewable, leaves the app in a working state, and is as small as possible
  while still a meaningful step towards the goal. (This however does not mean the change has to be reviewed and merged
  independently before we can continue. Sometimes, it is ok to bundle a few together.)
- Subtasks are about code: changes, tests and local checks. Deploying is not a subtask. That happens when the merge
  request is merged to main, through the pipeline's guard rails (canary, monitoring, automated rollback).
- Notes for later tasks go in a `#### Notes from task N` section under the task, not in checkboxes.

### Task 1: `AmiMusicSdk.json` and `AmiMusicSdk.VERSION`

- [x] Add a public `AmiMusicSdk` object in commonMain with `json`: `ignoreUnknownKeys = true`, `encodeDefaults = true`,
  `explicitNulls = false` (F13, D14). A test reads score JSON that has unknown fields at score, part and measure level,
  and gets the same `Score` as from the JSON without them (§7). `ExamplesTest` uses `AmiMusicSdk.json` instead of its
  own copy, and the README shows how to read and write scores with it.
- [x] Generate `AmiMusicSdk.VERSION` from `libraryVersion` (F10, §4.5):
  - A Gradle task writes the version as a constant into a directory under `build/`.
  - The directory is registered with `commonMain`'s `kotlin.srcDir(<task provider>)`, so every target's compilation and
    sources jar depend on the task.
  - A jvmTest asserts that the constant equals `libraryVersion` in `gradle.properties` (§7).

#### Notes from task 1

- `AmiMusicSdk` lives in package `com.sparetimedevs.ami`. `VERSION` is `const val VERSION = GENERATED_SDK_VERSION`, where
  the internal constant is written by the `generateSdkVersion` Gradle task into
  `build/generated/sdk-version/commonMain/kotlin`.
- The compatibility-tests module keeps its own `Json` copy in `AbstractCompatibilityTest`, because the versioned suites
  pin older SDK artifacts that have no `AmiMusicSdk`. Keep that copy identical to `AmiMusicSdk.json`.
- Backticked test names in commonTest can't contain commas (Kotlin/Native rejects them).

### Task 2: Validation errors in a serializable shape

- [ ] Every `ValidationIdentifier` names the kind of its location: `score`, `part`, `measure`, `note` or `pitch`.
  - A `ValidationError` gives its location as a list of segments from the score inward. Each segment is a kind with an
    id or an index. `NoValidationIdentifier` is left out.
  - A location also renders as text, e.g. `part p-1, measure 3, note 2` (§4.3).
  - Tests cover a note-level error and a part-level error.
- [ ] Add the serializable error shape (F8, §4.3):
  - Plain `@Serializable` classes with `message`, `property` and `location: [{ kind, id | index }]`.
  - Mapped from `ValidationError` and from `AccumulatedValidationErrors`, without serializing `ValidationIdentifier`
    polymorphically.
  - A test runs `fromJson` on invalid input, encodes the errors with `AmiMusicSdk.json` and asserts the JSON, locations
    included (§7).

#### Notes from task 2

None

### Task 3: Compatibility tests round-trip only a version's own JSON

- [ ] Change `AbstractCompatibilityTest` so that a suite runs the full round trip (read, then write back identical
  JSON) only on JSON that its own SDK version wrote. For JSON from other versions it checks only that the JSON reads
  into the expected `Score`.
  - `compatibilitySdkCurrent`: full round trip on `openapi/examples`. Reading only on every frozen fixture, including
    the `v0.0.2/` placeholders, which can't follow the canonical form.
  - The versioned suites: full round trip on their own fixtures. Reading only on older fixtures, newer fixtures and
    `openapi/examples`.
  - All suites still pass on today's JSON. Task 4 changes what the current SDK writes, and frozen fixtures can't change
    with it (spec §9, 2026-11-04).
- [ ] Update the compatibility sections of `README.md`, `CLAUDE.md` and `compatibility-tests/README.md` to describe this
  split.

#### Notes from task 3

None

### Task 4: Time signatures in measure attributes, in canonical form

- [ ] Schema changes (§4.1, §4.4):
  - Add `TimeSignature { beats: int32; beatType: int32 }` and `MeasureAttributes.time?`.
  - In `examples.tsp`, rewrite the existing examples in canonical form: drop every `attributes: #{}`, and give each
    part's first measure `attributes: #{ time: #{ beats: 4, beatType: 4 } }`.
- [ ] `MeasureAttributes` gains `time: TimeSignature?`.
  - `MeasureAttributes.validate` stops ignoring its input and validates `time` with the existing `Beats` and `BeatType`
    rules. An `int32` outside the `Byte` range is a validation error, not a wrapped value.
  - `"attributes": {}` reads the same as no `attributes`.
  - Tests cover each error and its measure location.
- [ ] Resolve the effective time signature per part (D4, F4):
  - A first measure without `attributes.time` gets 4/4 in its `attributes`.
  - Each measure's `timeSignature` carries forward from the last measure that stated one.
  - A measure depends on the ones before it, so part validation validates the attributes of all measures first and
    then folds over them.
  - Tests: a missing first time signature, and a 3/4 change in measure 3 that carries to measures 4 and 5.
- [ ] Kotlin producers can't build a measure whose `timeSignature` contradicts its `attributes.time`.
  - `Measure.invoke` derives `timeSignature` from `attributes.time`, and otherwise from the previous measure's, which
    the caller passes in. This replaces the 4/4 default marked `TODO remove this default`, also in `createMeasure`.
  - The Kotlin examples state 4/4 on each part's first measure, matching the rewritten JSON examples.
- [ ] Write canonical attributes (§4.4, F9):
  - `toSerializable` writes `attributes` only when it holds something.
  - It always writes the first measure's `time`, taken from its `timeSignature` when `attributes.time` is empty.
  - Update the expected JSON in `JsonTest`.
- [ ] Older JSON reads into the same `Score` as its canonical form (§7). A commonTest covers this:
  - JSON in the `0.0.1-SNAPSHOT` form (`"attributes": {}` everywhere, no `time`) and its canonical form read into equal
    `Score`s.
  - Writing that `Score` with `AmiMusicSdk.json` gives the canonical JSON.
  - Reading and then writing canonical JSON gives identical JSON.

#### Notes from task 4

None

### Task 5: Key and tempo

- [ ] Schema: replace the `Key` placeholder with `Key { fifths: int32; mode: string }`, and add
  `Tempo { quarterNotesPerMinute: int32 }` and `MeasureAttributes.tempo?` (§4.1).
- [ ] Add `Key` with `KeyMode` (`MAJOR`, `MINOR`) to the model, replacing the placeholder in `core/NotYetImplemented.kt`.
  Remove that file if nothing else uses it.
  - Validation: `fifths` −7…7, and `mode` is a known `KeyMode` (D9).
  - Mapped both ways, and written only on the measures that state it.
  - Tests for each rule, with the measure location.
- [ ] Add `Tempo` (`quarterNotesPerMinute` 20…400) in the same way, with tests.
- [ ] Add `Part.keyAt(measureIndex)` and `Part.tempoAt(measureIndex)` (§4.2).
  - Each returns the value from the last measure at or before that index that stated one, or `null` if none did.
  - Tests, plus a round trip of a part with a key change and a tempo change mid-piece, giving identical JSON.

#### Notes from task 5

None

### Task 6: `Fraction`

- [ ] Schema: add `Fraction { numerator: int32; denominator: int32 }` (§4.1).
- [ ] Add `Fraction` to the Kotlin model (F1, D3):
  - Validation of a numerator and a denominator: denominator > 0, numerator ≥ 0, and reduced (zero is `0/1`).
  - A validating extension on the generated `Fraction`.
  - A reducing factory for Kotlin producers: `Fraction.of(2, 4)` is `1/2`.
  - A test for each rule, with the property in the error.
- [ ] Add comparison, addition and subtraction to `Fraction`.
  - Compute in `Long` and reduce.
  - Subtracting below zero fails, as `unsafeCreate` does.
  - Tests include 1/4096 steps and sums past a whole note.
- [ ] Add `TimeSignature.measureLength`: `beats / beatType` as a reduced `Fraction`. Tests for 4/4 (1), 3/4 (3/4), 6/8
  (3/4) and 2/2 (1).

#### Notes from task 6

None

### Task 7: Pickups and measures without notes

- [ ] Schema: add `Measure.pickup?: Fraction` (§4.1).
- [ ] All three `Measure` variants gain `pickup: Fraction?` (default `null`) and `length`
  (`pickup ?: timeSignature.measureLength`, §4.2). `pickup` is mapped both ways and written only when set. Tests.
- [ ] Validate `pickup` (D5, §4.3): it is allowed only on a part's first measure, and
  `0 < pickup < timeSignature.measureLength`. Tests with the measure location, including a pickup on measure 2 and a
  pickup as long as the whole measure.
- [ ] A measure without notes is a `CompleteMeasure` of length `length`, whatever the stubbed classification says (F5).
  A test reads a score whose measures all have `"notes": []`, and asserts that it validates and that every measure is a
  `CompleteMeasure` (§7).

#### Notes from task 7

None

### Task 8: Chord symbols

- [ ] Schema: add `ChordSymbol` and `ChordDegree` (§4.1). `Measure` doesn't use them yet.
- [ ] `NoteName` gains `C_FLAT`, `F_FLAT`, `E_SHARP` and `B_SHARP`, and `NoteName.validate` accepts them (D10, F6). A
  test checks that all 21 spellings validate.
- [ ] Add `ChordKind` with MusicXML's 33 kind values. Each entry carries its MusicXML name, e.g.
  `MINOR_SEVENTH("minor-seventh")`. The 33 values:
  - major, minor, augmented, diminished, dominant, major-seventh, minor-seventh, diminished-seventh,
    augmented-seventh, half-diminished, major-minor, major-sixth, minor-sixth;
  - dominant-ninth, major-ninth, minor-ninth, dominant-11th, major-11th, minor-11th, dominant-13th, major-13th,
    minor-13th;
  - suspended-second, suspended-fourth, Neapolitan, Italian, French, German, pedal, power, Tristan, other, none.

  `ChordKind` is validated from its entry name as a string (D9). Tests: 33 distinct entries with their MusicXML names,
  and an unknown kind is a validation error.
- [ ] Add `ChordDegree` (`value` 2…13, `alter` −2…2) with `DegreeType` (`ADD`, `ALTER`, `SUBTRACT`), validated, with a
  test per rule.
- [ ] Add `ChordSymbol` (`root`, `kind`, `kindText`, `degrees`, `bass`; D2).
  - Validation (§4.3): `root` is present exactly when `kind` isn't `NONE`; `bass` is absent for `NONE`; `root` and
    `bass` are known `NoteName`s; `kindText` is at most 32 characters; the degrees are valid.
  - Mapped both ways. An absent `kindText` and empty `degrees` are written as `null` (§4.4).
  - A test per rule, and round trips of C, Am7 with kind text `-7`, C/G, Cadd9 and N.C.

#### Notes from task 8

None

### Task 9: Harmonies on measures

- [ ] Schema: add `Harmony { offset: Fraction; chord: ChordSymbol }` and `Measure.harmonies?` (§4.1).
- [ ] The `Measure` variants gain `harmonies: List<Harmony>` (default empty), mapped both ways. An empty list is
  written as `null`, and readers accept `[]` too (§4.4). Tests.
- [ ] Add the offset and order checks that every track reuses (§4.3):
  - An offset is a valid `Fraction` with `0 ≤ offset < length`, and harmony offsets strictly increase.
  - The checks need the measure's `length`, so they run after the part's time signatures and pickup are resolved.
  - Errors are located at `harmony i`, through a new identifier that the error shape from task 2 renders as kind
    `harmony`.
  - Tests include *"part p-1, measure 3, harmony 2: offset 5/4 is outside the measure (length 1)"*, an offset in a
    pickup measure, and a chord degree error located at its harmony.
- [ ] A part with chords-only measures (`"notes": []`), C on beat 1 and D on beat 3, round-trips with
  `AmiMusicSdk.json` to identical JSON (F9).

#### Notes from task 9

None

### Task 10: Lyrics

- [ ] Schema: add `LyricFragment { offset: Fraction; text: string; verse?: int32 }` and `Measure.lyrics?` (§4.1).
- [ ] The `Measure` variants gain `lyrics: List<LyricFragment>` (default empty), mapped both ways (D7, §4.4):
  - Verse 1 is written as `null`, and an explicit `verse: 1` reads the same as none.
  - Text is kept exactly as written, including leading and trailing spaces.
  - Tests.
- [ ] Validate lyrics (§4.3):
  - Offsets are checked as for harmonies, and strictly increase within each verse.
  - `text` isn't empty and is at most 512 characters; `verse` is 1…99.
  - Errors are located at `lyric i`.
  - A test per rule, including two verses that share offsets.

#### Notes from task 10

None

### Task 11: Text directions

- [ ] Schema: add `TextDirection { offset: Fraction; text: string }` and `Measure.textDirections?` (§4.1).
- [ ] The `Measure` variants gain `textDirections: List<TextDirection>` (default empty), mapped both ways, with an empty
  list written as `null`. Tests.
- [ ] Validate text directions (§4.3):
  - `0 ≤ offset ≤ length`, so the very end of a measure is allowed.
  - Offsets don't decrease, and several directions at one offset keep their order.
  - `text` isn't empty and is at most 512 characters.
  - Errors are located at `text direction i`.
  - Tests, including `2x` at the end of the last measure.

#### Notes from task 11

None

### Task 12: Line breaks and section starts

- [ ] Schema: add `SectionStart { offset: Fraction; label?: string }`, `Measure.lineBreaks?: Fraction[]` and
  `Measure.sectionStarts?` (§4.1).
- [ ] The `Measure` variants gain `lineBreaks: List<Fraction>` and `sectionStarts: List<SectionStart>` (default empty),
  mapped both ways, with empty lists written as `null`. Tests.
- [ ] Validate line breaks and section starts (§4.3, D15):
  - Offsets are checked as for harmonies, and each list strictly increases.
  - A `label` isn't empty after trimming and is at most 64 characters.
  - In a part's first measure, offset 0 holds no line break, and a section start there has a label.
  - Errors are located at `line break i` and `section start i`.
  - A test per rule.
- [ ] The §4.4 sample measure (harmonies, lyrics and a line break at 0, in a measure after the first) round-trips with
  `AmiMusicSdk.json` to identical JSON.

#### Notes from task 12

None

### Task 13: The song example

- [ ] Schema: add the song example to `examples.tsp` with `@example` on `Score`, in canonical form (F11).
  - A traditional song or invented text, never copyrighted lyrics, in 4/4.
  - It covers a pickup, a half-measure change, a beat-4 change, chords-only measures, a text direction, a line break
    inside a measure, a labelled section start, two verses, an N.C., a slash chord and an add9.
- [ ] Add its Kotlin twin in `music/example/`, and list the song in the example README with its source.
  - Add it to `ExamplesTest` and `compatibilitySdkCurrent`.
  - The JSON reads into the Kotlin twin, and the twin writes back to identical JSON (§7).
- [ ] The older-SDK suites read the song's JSON without errors (D14). This needs a reading-only check in
  `AbstractCompatibilityTest` that takes no expected `Score`.
- [ ] One commonTest shows errors from several new rules accumulating in a single result, each with its location (§7).
  For example: an unreduced offset, an unknown chord kind and an empty lyric in different measures, plus an invalid
  tempo.
- [ ] Check every acceptance criterion in §7 against a test, adding any that are missing, including one test per §4.3
  rule that asserts its error and location. Then tick the criteria in the spec.

#### Notes from task 13

None
