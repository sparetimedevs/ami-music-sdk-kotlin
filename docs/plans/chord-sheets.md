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

- [x] Every `ValidationIdentifier` names the kind of its location: `score`, `part`, `measure`, `note` or `pitch`.
  - A `ValidationError` gives its location as a list of segments from the score inward. Each segment is a kind with an
    id or an index. `NoValidationIdentifier` is left out.
  - A location also renders as text, e.g. `part p-1, measure 3, note 2` (§4.3).
  - Tests cover a note-level error and a part-level error.
- [x] Add the serializable error shape (F8, §4.3):
  - Plain `@Serializable` classes with `message`, `property` and `location: [{ kind, id | index }]`.
  - Mapped from `ValidationError` and from `AccumulatedValidationErrors`, without serializing `ValidationIdentifier`
    polymorphically.
  - A test runs `fromJson` on invalid input, encodes the errors with `AmiMusicSdk.json` and asserts the JSON, locations
    included (§7).

#### Notes from task 2

- `ValidationIdentifier.kind` is a `ValidationLocationKind?` (`null` only for `NoValidationIdentifier`). Later tasks add
  enum entries (`HARMONY`, `LYRIC`, ...) with their `@SerialName`, and an identifier class that sets `kind`.
- The error shape lives in `music/input/validation/SerializableValidationErrors.kt`: `SerializableValidationError`,
  `SerializableValidationErrors`, `ValidationLocationSegment`, with `ValidationError.toSerializable()`,
  `AccumulatedValidationErrors.toSerializable()`, `locationSegments()` and `locationAsText()`.
- Indexes are rendered as stored, 0-based (`measure 0` is the first measure), and the text includes the score segment:
  `score <id>, part p-1, measure 3, note 2`. The spec's *"part p-1, measure 3"* examples leave out the score and read as
  if 1-based; decide when writing the §7 tests whether to render 1-based.
- Not wired into `fromJson`/`toJson`; consumers call `toSerializable()` on the `AccumulatedValidationErrors`.

### Task 3: Compatibility tests read, write and read again

Compatibility means that an SDK reads JSON from another version into the same `Score` (§9, 2026-11-04). It does not
mean writing that JSON back byte for byte:

- Backward: the current SDK writes canonical JSON (D11, §4.4), so frozen fixtures with `"attributes": {}` come back
  differently by design.
- Forward: an older SDK drops fields it doesn't know (D14), so it can't write them back.

Writer stability (the SDK writes exactly the JSON of `openapi/examples`) is tested in the main module by `ExamplesTest`,
not by the compatibility suites. This task prepares for task 4, which changes what the current SDK writes.

- [x] Change `AbstractCompatibilityTest` so that, for every file in `examples()`, it reads the JSON into the expected
  `Score`, writes that `Score`, reads the result again and expects an equal `Score`. It no longer compares the written
  JSON with the file. Every suite keeps its single `examples()` map, and all suites pass on today's JSON.
- [x] Update the compatibility sections of `README.md`, `CLAUDE.md` and `compatibility-tests/README.md` to describe
  this, including that writer stability is tested in `ExamplesTest`, and that until real releases exist the versioned
  suites can't show a real difference between versions.

#### Notes from task 3

None

### Task 4: Time signatures in measure attributes, in canonical form

- [x] Schema changes (§4.1, §4.4):
  - Add `TimeSignature { beats: int32; beatType: int32 }` and `MeasureAttributes.time?`.
  - In `examples.tsp`, rewrite the existing examples in canonical form: drop every `attributes: #{}`, and give each
    part's first measure `attributes: #{ time: #{ beats: 4, beatType: 4 } }`.
- [x] `MeasureAttributes` gains `time: TimeSignature?`.
  - `MeasureAttributes.validate` stops ignoring its input and validates `time` with the existing `Beats` and `BeatType`
    rules. An `int32` outside the `Byte` range is a validation error, not a wrapped value.
  - `"attributes": {}` reads the same as no `attributes`.
  - Tests cover each error and its measure location.
- [x] Resolve the effective time signature per part (D4, F4):
  - A first measure without `attributes.time` gets 4/4 in its `attributes`.
  - Each measure's `timeSignature` carries forward from the last measure that stated one.
  - A measure depends on the ones before it, so part validation validates the attributes of all measures first and
    then folds over them.
  - Tests: a missing first time signature, and a 3/4 change in measure 3 that carries to measures 4 and 5.
- [x] Kotlin producers can't build a measure whose `timeSignature` contradicts its `attributes.time`.
  - `Measure.invoke` derives `timeSignature` from `attributes.time`, and otherwise from the previous measure's, which
    the caller passes in. This replaces the 4/4 default marked `TODO remove this default`, also in `createMeasure`.
  - The Kotlin examples state 4/4 on each part's first measure, matching the rewritten JSON examples.
- [x] Write canonical attributes (§4.4, F9):
  - `toSerializable` writes `attributes` only when it holds something.
  - It always writes the first measure's `time`, taken from its `timeSignature` when `attributes.time` is empty.
  - Update the expected JSON in `JsonTest`.
- [x] Older JSON reads into the same `Score` as its canonical form (§7). A commonTest covers this:
  - JSON in the `0.0.1-SNAPSHOT` form (`"attributes": {}` everywhere, no `time`) and its canonical form read into equal
    `Score`s.
  - Writing that `Score` with `AmiMusicSdk.json` gives the canonical JSON.
  - Reading and then writing canonical JSON gives identical JSON.

#### Notes from task 4

- `Measure(attributes, notes, previousTimeSignature)` has no default for the previous time signature. With `attributes.time`
  missing and no previous measure it fills 4/4 into the attributes (D4), and it turns empty attributes into `null`. So
  the "fill in 4/4" rule lives in one place, and part validation only folds over the measures.
- Validation: `validateMeasures` in `ValidateScore.kt` validates all attributes and notes first, then folds. The public
  `serialization.Measure.validate(index, ...)` is gone. `MeasureAttributes?.validate` returns `null` for empty attributes.
- `Measure.toSerializable(isFirstOfPart)` and `MeasureAttributes?.toSerializable(firstMeasureTimeSignature)` write the
  canonical form. `Part.toSerializable` passes `isFirstOfPart` for index 0.
- `MeasureAttributes(key, time)` has defaults. The `Key` placeholder is still not mapped (task 5).
- Examples: `createMeasure` (follows a 4/4 measure) and `createFirstMeasure` (states 4/4) in `MeasureHelper.kt`.
- Kotlin/Native crashed with a `ClassCastException` (value class `Beats` cast to `NonEmptyList`) when a `flatMap` over
  `EitherNel<_, Byte>` fed `Either.zipOrAccumulate`. Validate directly into `Beats`/`BeatType` instead. JVM tests alone
  don't show this, run `./gradlew check`.
- The schema change is committed in `ami-music-spec` (`TimeSignature`, `MeasureAttributes.time`, canonical examples).

### Task 5: Key and tempo

- [x] Schema: replace the `Key` placeholder with `Key { fifths: int32; mode: string }`, and add
  `Tempo { quarterNotesPerMinute: int32 }` and `MeasureAttributes.tempo?` (§4.1).
- [x] Add `Key` with `KeyMode` (`MAJOR`, `MINOR`) to the model, replacing the placeholder in `core/NotYetImplemented.kt`.
  Remove that file if nothing else uses it.
  - Validation: `fifths` −7…7, and `mode` is a known `KeyMode` (D9).
  - Mapped both ways, and written only on the measures that state it.
  - Tests for each rule, with the measure location.
- [x] Add `Tempo` (`quarterNotesPerMinute` 20…400) in the same way, with tests.
- [x] Add `Part.keyAt(measureIndex)` and `Part.tempoAt(measureIndex)` (§4.2).
  - Each returns the value from the last measure at or before that index that stated one, or `null` if none did.
  - Tests, plus a round trip of a part with a key change and a tempo change mid-piece, giving identical JSON.

#### Notes from task 5

- `Key(fifths: Fifths, mode: KeyMode)` lives in `music/data/kotlin/key/Key.kt`; `Tempo(quarterNotesPerMinute:
  QuarterNotesPerMinute)` in `music/data/kotlin/tempo/Tempo.kt`. `Fifths` and `QuarterNotesPerMinute` are validated value
  classes (`validate(Int, ...)`, `unsafeCreate`), so errors carry the property `Fifths`, `KeyMode` or
  `QuarterNotesPerMinute`. `core/NotYetImplemented.kt` is removed, nothing else used it.
- `KeyMode` is written and read as `MAJOR` / `MINOR` (`name`), case sensitive.
- `Part.keyAt` / `Part.tempoAt` are member functions on `Part` and only look at `attributes`, so they return `null`
  before the first stated value (unlike the time signature, there is no default).
- Tests are in `KeyAndTempoInMeasureAttributesTest`. The validation, in `ValidateScore.kt`, uses `zipOrAccumulate` on
  key, time and tempo, so errors from all three accumulate for one measure.
- The schema change is committed in `ami-music-spec` (`Key`, `Tempo`, `MeasureAttributes.tempo`). No example there uses
  key or tempo yet, task 13's song example can.

### Task 6: `Fraction`

- [x] Schema: add `Fraction { numerator: int32; denominator: int32 }` (§4.1).
- [x] Add `Fraction` to the Kotlin model (F1, D3):
  - Validation of a numerator and a denominator: denominator > 0, numerator ≥ 0, and reduced (zero is `0/1`).
  - A validating extension on the generated `Fraction`.
  - A reducing factory for Kotlin producers: `Fraction.of(2, 4)` is `1/2`.
  - A test for each rule, with the property in the error.
- [x] Add comparison, addition and subtraction to `Fraction`.
  - Compute in `Long` and reduce.
  - Subtracting below zero fails, as `unsafeCreate` does.
  - Tests include 1/4096 steps and sums past a whole note.
- [x] Add `TimeSignature.measureLength`: `beats / beatType` as a reduced `Fraction`. Tests for 4/4 (1), 3/4 (3/4), 6/8
  (3/4) and 2/2 (1).

#### Notes from task 6

- `Fraction` lives in `music/data/kotlin/fraction/Fraction.kt`: a class with a private constructor (`numerator`, `denominator`
  as `Int`), `Comparable`, `plus`, `minus`, `ZERO`, `ONE`. Create with `Fraction.validate(n, d, identifier)`,
  `unsafeCreate` (no reducing) or `of` (reducing). `of`, `plus` and `minus` compute in `Long`, reduce, and throw
  `ValidationException` for a negative result and `ArithmeticException` if the result doesn't fit in an `Int`.
- Error properties: `Denominator`, `Numerator` (these two accumulate) and `Fraction` for "not in lowest terms".
- The validating extension `serialization.Fraction.validate(identifier)` is in `ValidateScore.kt`. No `toSerializable`
  yet, task 7 adds the mapping when `Measure.pickup` needs it.
- `TimeSignature.measureLength` is a computed property, not part of the serialized form.
- The schema change is committed in `ami-music-spec` (`Fraction`).

### Task 7: Pickups and measures without notes

- [x] Schema: add `Measure.pickup?: Fraction` (§4.1).
- [x] All three `Measure` variants gain `pickup: Fraction?` (default `null`) and `length`
  (`pickup ?: timeSignature.measureLength`, §4.2). `pickup` is mapped both ways and written only when set. Tests.
- [x] Validate `pickup` (D5, §4.3): it is allowed only on a part's first measure, and
  `0 < pickup < timeSignature.measureLength`. Tests with the measure location, including a pickup on measure 2 and a
  pickup as long as the whole measure.
- [x] A measure without notes is a `CompleteMeasure` of length `length`, whatever the stubbed classification says (F5).
  A test reads a score whose measures all have `"notes": []`, and asserts that it validates and that every measure is a
  `CompleteMeasure` (§7).

#### Notes from task 7

- `pickup: Fraction? = null` is the last property of all three `Measure` variants and the last parameter of
  `Measure(attributes, notes, previousTimeSignature, pickup)`. `length` is a computed property on the interface
  (`pickup ?: timeSignature.measureLength`), not constructor state.
- `Fraction` is now `@Serializable` through a surrogate serializer (`{ numerator, denominator }`), because `Measure` is
  `@Serializable`. Reading through it uses `unsafeCreate`; JSON input goes through the validating path, not this one.
- Validation is in `validateMeasures` (`ValidateScore.kt`): each pickup is validated as a `Fraction` first (errors
  accumulate with the attribute and note errors), then the range and first-measure rules run after the time signatures
  are resolved. Both rule errors have property `Pickup` and the measure as location.
- `Measure.toSerializable` writes `pickup` only when set. `Fraction.toSerializable()` is added in `ToSerializable.kt`.
- F5 already held: the classification stub returns `CompleteMeasure` for everything, so a measure without notes is
  complete. The test locks that in; when the stub is implemented it must keep empty measures complete.
- Tests are in `PickupInMeasureTest`. No example uses a pickup yet (task 13). The schema change is committed in
  `ami-music-spec` (`Measure.pickup`).

### Task 8: Chord symbols

- [x] Schema: add `ChordSymbol` and `ChordDegree` (§4.1). `Measure` doesn't use them yet.
- [x] `NoteName` gains `C_FLAT`, `F_FLAT`, `E_SHARP` and `B_SHARP`, and `NoteName.validate` accepts them (D10, F6). A
  test checks that all 21 spellings validate.
- [x] Add `ChordKind` with MusicXML's 33 kind values. Each entry carries its MusicXML name, e.g.
  `MINOR_SEVENTH("minor-seventh")`. The 33 values:
  - major, minor, augmented, diminished, dominant, major-seventh, minor-seventh, diminished-seventh,
    augmented-seventh, half-diminished, major-minor, major-sixth, minor-sixth;
  - dominant-ninth, major-ninth, minor-ninth, dominant-11th, major-11th, minor-11th, dominant-13th, major-13th,
    minor-13th;
  - suspended-second, suspended-fourth, Neapolitan, Italian, French, German, pedal, power, Tristan, other, none.

  `ChordKind` is validated from its entry name as a string (D9). Tests: 33 distinct entries with their MusicXML names,
  and an unknown kind is a validation error.
- [x] Add `ChordDegree` (`value` 2…13, `alter` −2…2) with `DegreeType` (`ADD`, `ALTER`, `SUBTRACT`), validated, with a
  test per rule.
- [x] Add `ChordSymbol` (`root`, `kind`, `kindText`, `degrees`, `bass`; D2).
  - Validation (§4.3): `root` is present exactly when `kind` isn't `NONE`; `bass` is absent for `NONE`; `root` and
    `bass` are known `NoteName`s; `kindText` is at most 32 characters; the degrees are valid.
  - Mapped both ways. An absent `kindText` and empty `degrees` are written as `null` (§4.4).
  - A test per rule, and round trips of C, Am7 with kind text `-7`, C/G, Cadd9 and N.C.

#### Notes from task 8

- Types live in `music/data/kotlin/chord/`: `ChordKind` (33 entries, `musicXmlName`), `ChordDegree` with value classes
  `DegreeValue`, `DegreeAlter` and enum `DegreeType`, and `ChordSymbol` with value class `KindText`. All are
  `@Serializable`. `NoteName` now has 21 entries.
- Enums are written and read by entry `name` (`MINOR_SEVENTH`), case sensitive, like `KeyMode`; `musicXmlName` is only
  for a later MusicXML export.
- Error properties: `ChordKind`, `NoteName`, `KindText`, `DegreeValue`, `DegreeAlter`, `DegreeType`, and `ChordSymbol` for
  the root/bass presence rules. The presence rules only run when root, kind, kind text and bass are individually valid.
- `ChordSymbol.validate(root, kind, kindText, degrees, bass, identifier)` takes already validated degrees; the extension
  `serialization.ChordSymbol.validate(identifier)` in `ValidateScore.kt` validates the degrees too, with the same
  identifier, so task 9 only has to pass the harmony's identifier. `ChordSymbol.toSerializable()` writes `null` for
  absent kind text and empty degrees.
- Tests are in `ChordSymbolTest` and round-trip through `serialization.ChordSymbol` JSON, since `Measure` doesn't use
  chords yet. The generated `ChordSymbol` writes required `kind` before `root` in JSON.
- The schema change is committed in `ami-music-spec` (`ChordSymbol`, `ChordDegree`).

### Task 9: Harmonies on measures

- [x] Schema: add `Harmony { offset: Fraction; chord: ChordSymbol }` and `Measure.harmonies?` (§4.1).
- [x] The `Measure` variants gain `harmonies: List<Harmony>` (default empty), mapped both ways. An empty list is
  written as `null`, and readers accept `[]` too (§4.4). Tests.
- [x] Add the offset and order checks that every track reuses (§4.3):
  - An offset is a valid `Fraction` with `0 ≤ offset < length`, and harmony offsets strictly increase.
  - The checks need the measure's `length`, so they run after the part's time signatures and pickup are resolved.
  - Errors are located at `harmony i`, through a new identifier that the error shape from task 2 renders as kind
    `harmony`.
  - Tests include *"part p-1, measure 3, harmony 2: offset 5/4 is outside the measure (length 1)"*, an offset in a
    pickup measure, and a chord degree error located at its harmony.
- [x] A part with chords-only measures (`"notes": []`), C on beat 1 and D on beat 3, round-trips with
  `AmiMusicSdk.json` to identical JSON (F9).

#### Notes from task 9

- `Harmony(offset: Fraction, chord: ChordSymbol)` lives in `music/data/kotlin/harmony/Harmony.kt`. `harmonies: List<Harmony>`
  is the last property of all three `Measure` variants and the last parameter of `Measure(...)`; `toSerializable` writes
  `null` for an empty list.
- `ValidationLocationKind.HARMONY` and `ValidationIdentifierForHarmony(index, parent)` are added. Each harmony's offset and
  chord are validated with that identifier, so a chord degree error is located at its harmony.
- The reusable check is the private `checkOffsets(offsets, length, measureIdentifier, identifierFor)` in `ValidateScore.kt`:
  `0 ≤ offset < length` and strictly increasing, both with property `Offset`. Tasks 10 and 12 call it with their own
  identifier (lyrics need it per verse). Task 11 needs `≤ length` and non-decreasing, so it needs a variant of it.
- `validateMeasures` collects the checks that need `length` (pickup and offsets) after resolving the time signatures, and
  reports them together. They only run when attributes, notes, pickups and harmonies themselves are valid.
- Messages render a whole-number length without denominator (`length 1`), as in the spec. `Fraction.toString()` still
  gives `1/1`.
- Tests are in `HarmonyInMeasureTest`. The schema change is committed in `ami-music-spec` (`Harmony`,
  `Measure.harmonies`). No example uses harmonies yet (task 13).


### Task 10: Lyrics

- [x] Schema: add `LyricFragment { offset: Fraction; text: string; verse?: int32 }` and `Measure.lyrics?` (§4.1).
- [x] The `Measure` variants gain `lyrics: List<LyricFragment>` (default empty), mapped both ways (D7, §4.4):
  - Verse 1 is written as `null`, and an explicit `verse: 1` reads the same as none.
  - Text is kept exactly as written, including leading and trailing spaces.
  - Tests.
- [x] Validate lyrics (§4.3):
  - Offsets are checked as for harmonies, and strictly increase within each verse.
  - `text` isn't empty and is at most 512 characters; `verse` is 1…99.
  - Errors are located at `lyric i`.
  - A test per rule, including two verses that share offsets.

#### Notes from task 10

- `LyricFragment(offset, text, verse = 1)` lives in `music/data/kotlin/lyric/LyricFragment.kt`, with `validateText` (1…512
  characters, property `LyricText`) and `validateVerse` (1…99, absent is 1, property `LyricVerse`). Whitespace-only text
  is valid, only the empty string is not. `lyrics: List<LyricFragment>` is the last property of all three `Measure`
  variants and the last parameter of `Measure(...)`. `toSerializable` writes verse 1 as `null` and an empty list as `null`.
- `ValidationLocationKind.LYRIC` and `ValidationIdentifierForLyric(index, parent)` are added. The index is the position
  in the measure's `lyrics` list, not within the verse.
- `checkOffsets` in `ValidateScore.kt` gained `groups` (default: one group). Offsets must strictly increase only among
  items with the same group value; lyrics pass their verse. The range check applies to all lyrics. Task 11 still needs
  its own `≤ length` / non-decreasing variant.
- Tests are in `LyricsInMeasureTest`. No example uses lyrics yet (task 13). The schema change is committed in
  `ami-music-spec` (`LyricFragment`, `Measure.lyrics`).

### Task 11: Text directions

- [x] Schema: add `TextDirection { offset: Fraction; text: string }` and `Measure.textDirections?` (§4.1).
- [x] The `Measure` variants gain `textDirections: List<TextDirection>` (default empty), mapped both ways, with an empty
  list written as `null`. Tests.
- [x] Validate text directions (§4.3):
  - `0 ≤ offset ≤ length`, so the very end of a measure is allowed.
  - Offsets don't decrease, and several directions at one offset keep their order.
  - `text` isn't empty and is at most 512 characters.
  - Errors are located at `text direction i`.
  - Tests, including `2x` at the end of the last measure.

#### Notes from task 11

- `TextDirection(offset, text)` lives in `music/data/kotlin/textdirection/TextDirection.kt`, with `validateText` (1…512
  characters, property `TextDirectionText`). `textDirections: List<TextDirection>` is the last property of all three
  `Measure` variants and the last parameter of `Measure(...)`. `toSerializable` writes an empty list as `null`.
- `ValidationLocationKind.TEXT_DIRECTION` (serial name `text direction`) and `ValidationIdentifierForTextDirection` are
  added; the location renders as `text direction 1`.
- `checkOffsets` in `ValidateScore.kt` gained `allowEnd` (`offset ≤ length`) and `allowEqual` (non-decreasing). Only
  text directions use them. Task 12 uses the default strict checks for line breaks and section starts.
- Tests are in `TextDirectionsInMeasureTest`. No example uses text directions yet (task 13). The schema change is
  committed in `ami-music-spec` (`TextDirection`, `Measure.textDirections`).


### Task 12: Line breaks and section starts

- [x] Schema: add `SectionStart { offset: Fraction; label?: string }`, `Measure.lineBreaks?: Fraction[]` and
  `Measure.sectionStarts?` (§4.1).
- [x] The `Measure` variants gain `lineBreaks: List<Fraction>` and `sectionStarts: List<SectionStart>` (default empty),
  mapped both ways, with empty lists written as `null`. Tests.
- [x] Validate line breaks and section starts (§4.3, D15):
  - Offsets are checked as for harmonies, and each list strictly increases.
  - A `label` isn't empty after trimming and is at most 64 characters.
  - In a part's first measure, offset 0 holds no line break, and a section start there has a label.
  - Errors are located at `line break i` and `section start i`.
  - A test per rule.
- [x] The §4.4 sample measure (harmonies, lyrics and a line break at 0, in a measure after the first) round-trips with
  `AmiMusicSdk.json` to identical JSON.

#### Notes from task 12

- `SectionStart(offset, label: String? = null)` lives in `music/data/kotlin/section/SectionStart.kt`, with `validateLabel`
  (not blank, at most 64 characters, property `SectionStartLabel`). The label is kept as written, not trimmed.
  `lineBreaks: List<Fraction>` and `sectionStarts: List<SectionStart>` are the last two properties of all three
  `Measure` variants and the last two parameters of `Measure(...)`. `toSerializable` writes empty lists as `null`.
- `ValidationLocationKind.LINE_BREAK` (`line break`) and `SECTION_START` (`section start`) are added, with
  `ValidationIdentifierForLineBreak` and `ValidationIdentifierForSectionStart`.
- Both lists use the default strict `checkOffsets`. The song-start rule is the private `checkSongStart` in
  `ValidateScore.kt` and runs for measure index 0 of each part: a line break at 0 is an `Offset` error at its line break,
  a label-less section start at 0 is a `SectionStartLabel` error at its section start. It also fires when the same
  offset is already reported as out of range or out of order, but that can't happen at offset 0.
- Tests are in `LineBreaksAndSectionStartsInMeasureTest`, including the §4.4 sample measure (it follows a first measure
  that states 4/4, because a line break at 0 is not allowed in the first measure). No example uses these yet (task 13).
  The schema change is committed in `ami-music-spec` (`SectionStart`, `Measure.lineBreaks`, `Measure.sectionStarts`).

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
