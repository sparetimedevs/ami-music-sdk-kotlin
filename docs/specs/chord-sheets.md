# Spec: Chord sheets powered by Ami music SDK

> **Status: READY** (2026-11-04). No open questions. Decisions, and later revisions with their reasons, are logged in §9.

## 1. Request

A requestor wants to store and retrieve scores and represent them as chord sheets, lyrics with chords:

```
C               Am
This is a song, with some chords
C               D
This is a song, with some words
```

**What the SDK delivers**
- **The model:** chord symbols, lyrics, text directions, line breaks, section starts, time signature, key, tempo and pickup measures.
- **JSON serialization** in one canonical form, with the SDK's own `Json` configuration (`AmiMusicSdk.json`).
- **Accumulated validation**, with error locations, and the errors in a shape that serializes to JSON.
- **The SDK's version at runtime** (`AmiMusicSdk.VERSION`).

## 2. Current state (facts that shape the design)

**How a model change flows**
1. TypeSpec in the separate `ami-music-spec` repository (`main.tsp`, `examples.tsp`) is compiled to OpenAPI.
2. The result is copied to `openapi/ami-music-spec.yaml` here.
3. `./gradlew openApiGenerate` generates the serialization classes in `generated/` (package `com.sparetimedevs.ami.music.serialization`).
4. The hand-written model lives in `music/data/kotlin/`.
5. `music/serialization/ToSerializable.kt` maps the model to the serialization classes. `music/input/validation/` maps the serialization classes back to the model, accumulating errors.
6. `extractOpenApiExamples` writes `openapi/examples/*.json` from the spec's examples. Their Kotlin twins are in `music/example/`.

**Measures**
- In the schema, `Measure` is `{ notes: Pitched[] (required), attributes?: MeasureAttributes }`. `MeasureAttributes` is `{ key?: Key }`, and `Key` is a placeholder `{ todo?: string }`.
- In Kotlin, `Measure` is sealed: `CompleteMeasure`, `IncompleteMeasure` and `OverfloodedMeasure`, each with `timeSignature`, `attributes` and `notes`. Classification is stubbed: `notesShouldBeInAccordanceWithTimeSignature` returns `true`, so every measure is complete.
- **The time signature is not serialized.**
  - `Measure.validate` builds `Measure(attributes, notes)`, which falls back to the default 4/4.
  - `MeasureAttributes.validate` ignores its input and returns `null`.
  - `toSerializable` writes `key = null`, so every measure carries `"attributes": {}`.

**Notes and pitches**
- Only `Pitched` notes serialize. `Chord`, `Rest` and `Unpitched` hit `TODO()` in `toSerializable`.
- `NoteDuration` is `noteValue` (MAXIMA … _4096TH) times `modifier` (NONE, DOTTED, …). Its `value: Double` is built from binary fractions, so it is exact today; there are no tuplets.
- `NoteName` has 17 spellings and lacks C♭, F♭, E♯ and B♯.
- `Part.instrument` (name, MIDI channel, MIDI program) already exists.

**Validation**
- `fromJson(json, input)` decodes, then validates, returning `Either<DomainError, Score>`.
- Errors accumulate as `ValidationError(message, validationErrorForProperty, validationIdentifier)`. The identifier is a chain: score → part → measure index → note index.

**Serialization:** consumers pass their own `Json` to `fromJson` and `toJson`, so the configuration is theirs. One consumer, for example, uses `encodeDefaults = true` and `explicitNulls = false`, which writes a different (still readable) form than `Json.Default`.

**Version, publishing and consumers**
- `libraryVersion = 0.0.2-SNAPSHOT`.
- A few known consumers use `0.0.1-SNAPSHOT`, which no longer changes.
- The publication targets only the Sonatype Central **snapshots** repository. Release publishing isn't configured.

**Toolchain:** Kotlin 2.4.10. Targets are JVM, JS (browser) and native. The JS klib is published as `ami-music-sdk-kotlin-js`.

## 3. Decisions

| # | Decision | Why |
|---|---|---|
| D1 | **One model.** Harmonies, lyrics, text directions, line breaks and section starts are time-positioned tracks on `Measure`. Notes stay optional. There is no separate lead-sheet type. | A chords-only song is a score with fewer things filled in. |
| D2 | **Chord symbols copy MusicXML's `<harmony>`:** root, kind (MusicXML's 33 kind values), degrees, bass, and the kind as written. Roman numerals, function, inversion and fretboard frames are left out. | Lossless MusicXML import and export later. "N.C." is kind `NONE`. |
| D3 | **Time positions are exact fractions of a whole note** (`Fraction`, always reduced), measured from the start of the measure. Never a `Double`. | The same unit as `NoteValue` (WHOLE = 1, QUARTER = 1/4). Exact like MusicXML's divisions, and independent of the time signature. |
| D4 | **Time signature, key and tempo are measure attributes,** written only on the measure where they are set or change, as in MusicXML `<attributes>`. The writer always states the first measure's time signature; a reader fills in 4/4 when it is missing, today's behaviour. The Kotlin model resolves the effective time signature for every measure. | Changes mid-piece stay possible, and older JSON stays readable. |
| D5 | **A pickup** (*opmaat*) is the first measure with its own shorter length, `pickup: Fraction` (MusicXML `implicit="yes"`). It is allowed only on the first measure for now. | A measure without notes can't imply its length, so it is stated. |
| D6 | **Line breaks are offsets inside a measure** (`lineBreaks`), not a flag per measure. MusicXML export maps a break at offset 0 to `<print new-system="yes">` and moves any other break to the next measure. That loses layout only. | Lyric lines in children's songs often start on an upbeat, in the middle of the previous measure. |
| D7 | **A lyric fragment** is `offset`, `text` and `verse` (default 1). Its text can be a syllable or a whole phrase, and whitespace is significant. There is no `syllabic` field yet. | Text input produces runs; MusicXML import will produce syllables. An optional field can be added later without breaking stored data. |
| D8 | **The model holds only valid chord symbols.** A producer stores a chord name it can't parse as a text direction. | No "unparsed chord" state in the stored format. |
| D9 | **Enumerated values are strings in the schema** (`kind`, `mode`, degree `type`, note names), validated in Kotlin, as `noteName` and `noteValue` are today. | An unknown value becomes a validation error with a location, not a parse exception. |
| D10 | **`NoteName` gains `C_FLAT`, `F_FLAT`, `E_SHARP` and `B_SHARP`.** | Completes the single-accidental spellings, needed to spell chords in flat and sharp keys, and later for transposition. |
| D11 | **Validation is strict; writing is canonical.** Unsorted tracks, unreduced fractions and offsets outside the measure are errors (§4.3), never silently fixed. Readers accept other forms of the same model value, such as explicit defaults or a missing first time signature (D4). Writers write one form: they omit empty tracks and default values (§4.4). | One model value per score, written as one JSON: exact round trips, stable equality, and clear producer bugs. |
| D12 | **The note union is out of scope.** `notes` stays `Pitched[]`, and a chords-only song writes `"notes": []`. | Songs have no notes in this phase; the union needs its own design (`ToSerializable.kt`'s `TODO()`s). |
| D13 | **Versioning.** Work happens on `0.0.2-SNAPSHOT`. An official release is not part of this spec, nor is the backward-compatibility guarantee that comes with it. | `0.0.1-SNAPSHOT` no longer changes, so its consumers keep working. The model change doesn't depend on a release. |
| D14 | **Forward compatibility means reading with `ignoreUnknownKeys = true`.** An older SDK must read newer JSON without errors, dropping what it doesn't know. The SDK offers its configuration as `AmiMusicSdk.json` (§4.4), for consumers that store or exchange SDK JSON. | With `Json.Default`, every new field breaks every older reader. The cost: a dropped field is lost if an older client writes the score back. |
| D15 | **Section starts mark where a new section of the song begins,** such as a verse or a chorus (`sectionStarts`: an `offset` and an optional `label`). This is meaning, not layout: chord sheets show a blank line before each one. MusicXML has no section element, so export writes a double bar line (`location="middle"` inside a measure), and a label (*Couplet*, *Refrein*) as a rehearsal mark. A section start at the song's own start is only allowed with a label (§4.3). | Keeps the blank line between verse and chorus, and later serves playback structure and repeats. |

## 4. Design

### 4.1 Schema (TypeSpec, in `ami-music-spec`)

New and changed models. Everything not shown is unchanged.

```tsp
// A point or a length in time, in whole notes: {1, 4} is a quarter note. Always reduced; zero is {0, 1}.
model Fraction {
  numerator: int32;     // ≥ 0
  denominator: int32;   // > 0
}

model Measure {
  attributes?: MeasureAttributes;
  notes: Pitched[];                 // unchanged; [] in a chords-only song
  pickup?: Fraction;                // first measure only: its real length
  harmonies?: Harmony[];
  lyrics?: LyricFragment[];
  textDirections?: TextDirection[];
  lineBreaks?: Fraction[];          // a new line starts at each of these offsets
  sectionStarts?: SectionStart[];   // a new section (verse, chorus) starts at each of these offsets
}

model MeasureAttributes {
  time?: TimeSignature;             // new
  key?: Key;                        // reshaped, was { todo?: string }
  tempo?: Tempo;                    // new
}

model TimeSignature { beats: int32; beatType: int32; }
model Key { fifths: int32; mode: string; }                 // fifths −7…7 (C major/A minor = 0); mode MAJOR | MINOR
model Tempo { quarterNotesPerMinute: int32; }              // as MusicXML <sound tempo>; see the note below

model Harmony { offset: Fraction; chord: ChordSymbol; }

model ChordSymbol {
  root?: string;                    // NoteName; absent only when kind is NONE
  kind: string;                     // ChordKind, e.g. MAJOR, MINOR, DOMINANT, MAJOR_SEVENTH, SUSPENDED_FOURTH, NONE
  kindText?: string;                // the kind as written, e.g. "m7" or "-7" (MusicXML <kind text>)
  degrees?: ChordDegree[];          // e.g. add9: { value: 9, alter: 0, type: ADD }
  bass?: string;                    // NoteName, slash chords: C/G
}

model ChordDegree { value: int32; alter: int32; type: string; }   // type ADD | ALTER | SUBTRACT

model LyricFragment { offset: Fraction; text: string; verse?: int32; }   // verse defaults to 1
model SectionStart { offset: Fraction; label?: string; }                  // label: "Couplet", "Refrein"; MusicXML <rehearsal>
model TextDirection { offset: Fraction; text: string; }                  // MusicXML <direction><words>; may sit at the very end of the measure
```

`examples.tsp` gains a song example (F11).

**Tempo, as in MusicXML's `<sound tempo>`,** counts quarter notes per minute, whatever the time signature.
- **In 2/4, 3/4 and 4/4** that equals beats per minute, which covers most children's songs. When the beat isn't a quarter, it differs: in 6/8 (dotted-quarter beat) 60 beats per minute is 90, and in 2/2 (half-note beat) it is 120.
- **Only the playback tempo is stored.** A marking on the page like ♩. = 60 is a separate MusicXML element (`<metronome>`) and can be derived from it.
- **Integers only.** MusicXML allows decimal tempos; whole numbers lose nothing in practice.

### 4.2 Kotlin model

- **`Fraction`** (commonMain): a validated value type with comparison, addition and subtraction, and a reducing factory for Kotlin producers. `TimeSignature.measureLength` (`beats / beatType`) returns one.
- **`Measure`.** All three variants gain `harmonies: List<Harmony>`, `lyrics: List<LyricFragment>`, `textDirections: List<TextDirection>`, `lineBreaks: List<Fraction>` and `sectionStarts: List<SectionStart>`, all default empty, and `pickup: Fraction?`, default `null`.
  - **`length`** is `pickup ?: timeSignature.measureLength`.
  - **`timeSignature`** stays the effective value, carried forward from the last measure that stated one.
  - **`attributes`** holds what was written on this measure: `time`, `key` and `tempo`. On the first measure `time` is always set, to 4/4 when the JSON leaves it out (D4).
  - **The effective key and tempo** come from helpers on `Part`, e.g. `part.keyAt(measureIndex)`.
- **Classification.** A measure with no notes is a `CompleteMeasure` by definition, of length `length`. Classification by notes stays stubbed (§4.7).
- **New types:**
  - `ChordSymbol`;
  - `ChordKind`: 33 entries, each carrying its MusicXML kind-value, e.g. `MINOR_SEVENTH("minor-seventh")`;
  - `ChordDegree` with `DegreeType`;
  - `Harmony`;
  - `LyricFragment`;
  - `TextDirection`;
  - `SectionStart`;
  - `Key` with `KeyMode`, replacing the `NotYetImplemented` placeholder in `core/NotYetImplemented.kt`;
  - `Tempo`.
- **`NoteName`** gains its four spellings (D10).
- **Mapping and validation:** `toSerializable` and the validation in `input/validation/` cover every new type. `MeasureAttributes.validate` stops ignoring its input.

### 4.3 Validation rules

Every rule produces a `ValidationError` with a location, and all errors of one score accumulate in one result.

| Subject | Rule |
|---|---|
| `Fraction` | Denominator > 0; numerator ≥ 0; reduced, i.e. gcd = 1 (zero is `0/1`). |
| Offsets | `0 ≤ offset < measure.length` for harmonies, lyrics, line breaks and section starts. Text directions may also sit at the very end of the measure: `0 ≤ offset ≤ measure.length`. |
| Song start | In a part's first measure, offset 0 holds no line break, and a section start there has a label. |
| Order | Harmony offsets strictly increase. Lyric offsets strictly increase within each verse. Text direction offsets don't decrease (several at one offset keep their order). Line breaks strictly increase, and so do section starts. |
| `ChordSymbol` | `kind` is a known `ChordKind`. `root` is present exactly when `kind` isn't `NONE`. `bass` is absent when `kind` is `NONE`. `root` and `bass` are known `NoteName`s. `kindText` is at most 32 characters. |
| `ChordDegree` | `value` 2…13; `alter` −2…2; `type` is a known `DegreeType`. |
| `LyricFragment` | `text` isn't empty and is at most 512 characters; `verse` is 1…99. |
| `TextDirection` | `text` isn't empty and is at most 512 characters. |
| `SectionStart` | `label`, when present, isn't empty after trimming and is at most 64 characters. |
| `TimeSignature` | The existing `Beats` and `BeatType` rules. |
| `Key` | `fifths` −7…7; `mode` is a known `KeyMode`. |
| `Tempo` | `quarterNotesPerMinute` 20…400. |
| `pickup` | Only on the first measure of a part; `0 < pickup < timeSignature.measureLength`. |

**Locations.** The identifier chain (§2) gains harmony, lyric, text direction, line break and section start indexes. An error then reads like *"part p-1, measure 3, harmony 2: offset 5/4 is outside the measure (length 1)"*. A chord degree gets no index of its own: the harmony's location and the property name are enough.

**A serializable error shape.** Consumers need errors they can turn into JSON. The SDK provides that shape: message, property, and the location as a list of `{ kind, id | index }` segments. It must not depend on serializing the `ValidationIdentifier` interface polymorphically.

### 4.4 Canonical JSON

- **`AmiMusicSdk.json` defines the form** (F13). Canonical JSON is what `toJson(AmiMusicSdk.json, score)` writes. Another configuration writes a different form, still readable.
- **Writers omit defaults:** empty tracks (`harmonies`, `lyrics`, `textDirections`, `lineBreaks`, `sectionStarts`), `verse` 1, absent `kindText`, empty `degrees`, and `attributes` when it holds nothing. The mapping writes them as `null`, which `explicitNulls = false` leaves out. Readers accept the explicit forms too.
- **Attributes appear only where set or changed,** and the first measure always carries `attributes.time` (D4).
- **Existing scores change form.** Their measures lose `"attributes": {}`, and the first measure gains `attributes.time`. The existing examples are rewritten in this form (§6).
- **Text is stored exactly as written,** including leading and trailing spaces in lyric fragments.

The measure that starts the line *"This is a song, with some words"* from §1, in 4/4: C on beat 1, D on beat 3, and a new line starting here:

```json
{
  "notes": [],
  "harmonies": [
    { "offset": { "numerator": 0, "denominator": 1 }, "chord": { "root": "C", "kind": "MAJOR" } },
    { "offset": { "numerator": 1, "denominator": 2 }, "chord": { "root": "D", "kind": "MAJOR" } }
  ],
  "lyrics": [
    { "offset": { "numerator": 0, "denominator": 1 }, "text": "This is a song, " },
    { "offset": { "numerator": 1, "denominator": 2 }, "text": "with some words" }
  ],
  "lineBreaks": [ { "numerator": 0, "denominator": 1 } ]
}
```

### 4.5 Runtime version

The SDK exposes its version at runtime as `AmiMusicSdk.VERSION`, generated from `libraryVersion` into commonMain at build time. Consumers can store it with a score, e.g. as `sdk_version`.

### 4.6 JavaScript

- **No new platform code** (F12).
- **No `@JsExport` here.** Consumers use a wrapper project that declares its own facade on top of `fromJson`, `toJson` and the error shape of §4.3.

### 4.7 Out of scope

- **Notation and views:** text notation (parse and format), the chord-sheet view and the measure strip (consumers' responsibility, for now).
- **Using the model:** playback and chord realisation, transposition, MusicXML import and export.
- **Notes:** the note union (D12), classification by notes, tuplets, note durations as `Fraction`.
- **Further structure:**
  - repeats and endings;
  - `syllabic` and `extend`;
  - pickups anywhere but the first measure;
  - time, key and tempo changes in the middle of a measure;
  - Roman numerals;
  - how several parts relate to each other.
- **Releasing:** release publishing to Maven Central, an official release, and the backward-compatibility guarantee from then on.

## 5. Functional requirements

| # | Requirement |
|---|---|
| F1 | `Fraction` in the schema and in Kotlin, with validation (§4.3) and arithmetic. |
| F2 | `Measure` holds harmonies, lyrics, text directions, line breaks, section starts and an optional pickup length, serialized and validated. |
| F3 | `ChordSymbol` with `ChordKind` (all 33 MusicXML kinds, with their MusicXML names), `ChordDegree`, bass and kind text. |
| F4 | `MeasureAttributes` serializes and validates `time`, `key` and `tempo`. The effective time signature carries forward, 4/4 when none is stated. |
| F5 | A measure without notes is a `CompleteMeasure` of length `pickup ?: measureLength`. |
| F6 | `NoteName` has 21 spellings (D10). |
| F7 | Every rule of §4.3 is enforced, with its location; errors accumulate. |
| F8 | Validation errors are available in a serializable shape (§4.3). |
| F9 | Writers produce canonical JSON (§4.4), and reading then writing canonical JSON gives identical JSON. |
| F10 | `AmiMusicSdk.VERSION` (§4.5). |
| F11 | A song example in `examples.tsp`, `openapi/examples/` and `music/example/`, covering: a pickup, a half-measure change, a beat-4 change, chords-only measures, a text direction, a line break inside a measure, a labelled section start, two verses, an "N.C.", a slash chord and an add9. Examples are public: traditional songs or invented text only, never copyrighted lyrics. |
| F12 | All new code is in commonMain, with tests in commonTest that run on JVM and JS. |
| F13 | `AmiMusicSdk.json`, the SDK's own `Json` instance: `ignoreUnknownKeys = true` (D14), `encodeDefaults = true`, `explicitNulls = false`. |

## 6. Rollout

1. **`ami-music-spec`:** change `main.tsp` and `examples.tsp` (the song example, and the existing examples in canonical form, §4.4), compile, copy the YAML here, run `openApiGenerate`.
2. **Kotlin:** model, mapping, validation, examples and tests (F1–F13). Each subject can be its own pull request, e.g. `Fraction` and attributes first, then harmony, then lyrics, directions, line breaks and section starts.

## 7. Acceptance criteria

- [ ] `./gradlew check` passes: all tests, on JVM and JS. A test may be changed, replaced or removed when the new behaviour requires it; the tests are there to build confidence that the implementation works.
- [ ] The song example (F11) deserializes, validates, and serializes back to identical JSON.
- [ ] Each rule in §4.3 has a test with invalid input that asserts the error and its location. One test shows several errors accumulating in a single result.
- [ ] A score whose measures have no notes validates, and all its measures are `CompleteMeasure`s.
- [ ] JSON without `attributes.time`, such as JSON written by `0.0.1-SNAPSHOT`, still reads as 4/4, into the same `Score` as its canonical form.
- [ ] Validation errors serialize to JSON with their location (F8).
- [ ] `AmiMusicSdk.VERSION` matches `libraryVersion`.
- [ ] `AmiMusicSdk.json` reads JSON that contains an unknown field, and ignores that field.

## 8. Open questions

None.

## 9. Decision log

| Date | Decision |
|---|---|
| 2026-11-03 | Chord symbols follow MusicXML's `<harmony>`; harmonies and lyrics are time-positioned tracks on measures, with notes optional (D1, D2). |
| 2026-11-03 | The full model and timing are built now, even though phase 1 only shows chords above lyrics with bar lines. |
| 2026-11-03 | Notation and views are the consumers' responsibility for now; the SDK provides the model, serialization and validation (§4.7). |
| 2026-11-03 | **Q2:** forward compatibility means reading with `ignoreUnknownKeys = true`. The SDK also offers `AmiMusicSdk.json` (D14, §4.4). |
| 2026-11-03 | **Q3:** tempo as in MusicXML, in quarter notes per minute (§4.1). |
| 2026-11-03 | **Revised (§4.3):** a text direction may sit at the very end of a measure (`offset ≤ length`), so text after a song's last line, such as `2x`, has a place. MusicXML allows the same for directions: the "current musical location is always within the current measure, even at the end of a measure". |
| 2026-11-03 | **Added (D15):** section starts, so the blank line between verse and chorus survives as meaning rather than layout. |
| 2026-11-03 | **Revised (D15):** section starts get their optional `label` now, not later, because the requestor's structured editor has a section header to type it in. |
| 2026-11-03 | **Revised (F13, §4.4):** `AmiMusicSdk.json` encodes defaults (`encodeDefaults = true`, `explicitNulls = false`) instead of leaving them out. Canonical JSON still omits defaults, because the mapping writes them as `null`. |
| 2026-11-03 | **Revised (D13):** work happens on `0.0.2-SNAPSHOT` instead of `0.1.0-SNAPSHOT`. |
| 2026-11-03 | **Revised (D13):** an official release, with its backward-compatibility guarantee, is decoupled from this spec (§4.7). |
| 2026-11-03 | Chord degrees get no index in the error location; the harmony's location and the property name are enough, until practice shows otherwise (§4.3). |
| 2026-11-03 | **Added (§4.3, D15):** in a part's first measure, a line break at offset 0 and a section start there without a label are errors. The song's start is always the start of a line and of a section, so both would give the same score a second model value (D11). |
| 2026-11-03 | **Revised:** how the compatibility tests work is not part of this spec, so Q1 about them is withdrawn. |
| 2026-11-04 | **Revised (§7):** all tests must pass. A test may be changed, replaced or removed when the new behaviour requires it, because the tests are there to build confidence that the implementation works. The canonical form changes existing scores (§4.4), so tests that round-trip older JSON have to change. |
| 2026-11-04 | **Revised (D4, D11, §4.2):** a reader fills in 4/4 on the first measure when `attributes.time` is missing, so older JSON and its canonical form give the same `Score`. D11 now asks for one model value per score; readers accept other forms of it. |
