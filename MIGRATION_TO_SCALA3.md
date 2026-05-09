# ScalaLand → Scala 3 Migration Plan

Companion repo for the ScalaBook (https://github.com/oluies/ScalaBook).
Current state: Scala **2.8.1** on **sbt 0.7.4**, Eclipse `.classpath`,
`sbt-eclipsify` plugin, ~63 `.scala` files across 12 example packages
(`scalaland1`..`scalaland4`, `scalaland_trait`, `scalaland_immutable1..3`,
`scalaland_func_final`, `scalaland_generic`, `scalaland1a`).

The book is pedagogical: each package is a deliberately-different cut at the
same domain (avatars, creatures, professions, magical items). The migration
must keep that structure intact — we are not refactoring the *teaching*; we
are making it run on Scala 3 and, where Scala 3 has a clearly better idiom
for the lesson, adding a new `_scala3` variant alongside the original.

Reference: https://docs.scala-lang.org/scala3/guides/migration/compatibility-intro.html

---

## Strategy at a glance

```
┌──────────────────────────────────────────────────────────────┐
│ Phase 0  Tooling baseline       (sbt 1.x, JDK 17, mill?)    │
│ Phase 1  Compile on Scala 2.13  (-Xsource:3, deprecations)  │
│ Phase 2  Scalafix auto-rewrites (procedure syntax, etc.)    │
│ Phase 3  Cross-build 2.13 ↔ 3   (sbt-scala3-migrate)        │
│ Phase 4  Drop 2.13, Scala 3 only (fix residual breakage)    │
│ Phase 5  Idiomatic Scala 3 variants (enum, given, extension)│
│ Phase 6  Book-text updates + CI                             │
└──────────────────────────────────────────────────────────────┘
```

Branch `scala3` is already created from `task007/...`. Each phase below
becomes one PR onto `scala3`; the branch only merges to `master` after
Phase 4 is green.

---

## Phase 0 — Tooling baseline

Goal: get the project building with a modern sbt before touching Scala
itself. SBT 0.7 cannot resolve from any current repository.

**Actions**
- Replace `project/build.properties` with `sbt.version=1.10.x`.
- Delete `project/build/ScalaLandProject.scala` and
  `project/plugins/MySbtProjectPlugins.scala`. Replace with:
  - `build.sbt` declaring `scalaVersion := "2.13.14"`,
    `libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.x" % Test`.
  - `project/plugins.sbt` (empty initially).
- Delete `.classpath`, `.project`, `.settings/`, `.scala_dependencies`
  (Eclipse + sbt-eclipsify are dead). Replace with `.gitignore` entries
  for `target/`, `.bsp/`, `.metals/`, `.bloop/`.
- Pin JDK to 17 in a `.tool-versions` (asdf) or `.sdkmanrc`.
- Verify: `sbt compile` succeeds on Scala 2.13 (will fail — that's Phase 1).

**Exit criteria:** `sbt` starts, fetches Scala 2.13, attempts to compile.

---

## Phase 1 — Compile on Scala 2.13 (with `-Xsource:3`)

Goal: get every file compiling on the latest Scala 2 with the Scala 3
forward-compatibility flag on. Most syntactic differences surface here as
warnings or errors, with hints from the compiler.

**sbt settings**
```scala
scalacOptions ++= Seq(
  "-Xsource:3",
  "-deprecation",
  "-feature",
  "-Wunused:all",
  "-Xfatal-warnings"
)
```

**Expected breakage (high-confidence, based on file survey)**
| Symptom | Where | Fix |
|---|---|---|
| Procedure syntax `def f() { ... }` | TBD (grep first) | Add `: Unit =` |
| `new T() { ... }` constructor parens on traits w/o ctor | mixin sites | Drop parens |
| `Enumeration` value comparisons | `scalaland_immutable3.Avatar` (`CreatureType.Value`) | Keep on 2.13; flagged for Phase 5 |
| `package object scalaland4` re-exports | `scalaland4/package.scala` | Keep — `package object` still legal in 2.13 |
| Auto-application `obj.foo` on nullary defs | overrides like `override def strength` | Already explicit — fine |
| `case e: Elf =>` unchecked warnings | `scalaland_immutable3.Avatar.apply` | Add `@unchecked` or refactor in Phase 5 |
| `Predef.augmentString` removal of `+` for non-strings | unlikely here | — |

**Actions**
- Run `sbt compile`, fix in order: errors first, deprecations second.
- For each fix touch, **commit per package** so the book text can
  reference a specific commit if it teaches that idiom.

**Exit criteria:** `sbt clean compile test` is green on 2.13 with
`-Xsource:3 -Xfatal-warnings`.

---

## Phase 2 — Scalafix automated rewrites

Goal: apply the canonical mechanical rewrites Scalafix ships, so Phase 3
isn't fighting trivia.

**Plugin**
```scala
// project/plugins.sbt
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.13.x")
```

**Rules to run** (each as a separate commit)
1. `ProcedureSyntax`
2. `ExplicitResultTypes` (public API only — this is a teaching repo, types
   are pedagogical)
3. `LeakingImplicitClassVal`
4. `NoValInForComprehension`
5. The Scala 3 migration bundle: `fix.scala213.Any2StringAdd`,
   `fix.scala213.ConstructorProcedureSyntax`,
   `fix.scala213.ExplicitNonNullaryApply`,
   `fix.scala213.ParensAroundLambda`,
   `fix.scala213.ExplicitNullaryEtaExpansion`.

**Exit criteria:** scalafix reports no further rewrites; tests still pass
on 2.13.

---

## Phase 3 — Cross-build 2.13 ↔ 3 with `sbt-scala3-migrate`

Goal: prove the codebase compiles on Scala 3 *while still* compiling on
2.13. This is the safety net.

**Plugin**
```scala
addSbtPlugin("ch.epfl.scala" % "sbt-scala3-migrate" % "0.7.x")
```

**Workflow per sub-package**
1. `migrateDependencies` — confirm scalatest has a Scala 3 artifact (it
   does, 3.2.18+).
2. `migrateScalacOptions` — translate any leftover 2.x flags.
3. `migrateSyntax` — applies remaining rewrites.
4. `migrateTypes` — adds explicit types where 3 needs them.

**Set**
```scala
crossScalaVersions := Seq("2.13.14", "3.3.x")
```
and verify `+compile` is green on both.

**Expected residual breakage on 3 (high-confidence from the file survey)**

- **Mixin instantiation pattern** in `scalaland_immutable3.Avatar.apply`
  and `scalaland_trait` family:
  ```scala
  new Avatar(name, optFeats, optItems) with Dwarf with Thief
  ```
  Anonymous class instantiation with traits still works in Scala 3 *if*
  the traits take no parameters. All trait files here are nullary (e.g.
  `Dwarf`, `Thief`) — should compile, but verify per call site.

- **`scala.Enumeration`** in `scalaland_immutable3` (`CreatureType`,
  `ProfessionalType`). Still legal in Scala 3, but exhaustivity on
  `Value`-typed match is weak. Keep for now; replace in Phase 5.

- **`package object`** in `scalaland4/package.scala` and
  `scalaland_immutable2/package.scala` and
  `scalaland_immutable3/package.scala` and `scalaland_trait/package.scala`
  and `scalaland_immutable1/...` — Scala 3 deprecates `package object` in
  favor of top-level definitions. Mechanical translation:
  ```scala
  // before
  package com.programmera
  package object scalaland4 {
    type DieRoll = com.programmera.scalaland3.DieRoll
    val  DieRoll = com.programmera.scalaland3.DieRoll
  }
  // after
  package com.programmera.scalaland4
  type DieRoll = com.programmera.scalaland3.DieRoll
  val  DieRoll = com.programmera.scalaland3.DieRoll
  ```

- **Inferred return types on `override def`** with `super.x + ...` —
  Scala 3 is stricter; add explicit `: Int` where the compiler complains.

- **`throw new Exception(...)`** — fine, but pedagogically we now have
  `Throwable` in scope without import; no change needed.

**Exit criteria:** `sbt +compile +test` green on both 2.13 and 3.3.

---

## Phase 4 — Drop Scala 2.13

Goal: simplify build to Scala 3 only.

- Remove `crossScalaVersions`.
- Set `scalaVersion := "3.3.x"` (LTS).
- Remove `-Xsource:3`.
- Add Scala 3 specific options: `-source:3.3`, `-Wunused:all`,
  `-explain`.
- Re-run full test suite.

**Exit criteria:** Single Scala-3 build, all tests green. **Merge to
`master`** after this phase. Everything beyond is additive.

---

## Phase 5 — Idiomatic Scala 3 variants (the *book* upgrade)

The previous phases keep the lessons identical so existing book chapters
still match the code. This phase adds **new sibling packages** that show
the Scala 3 way for each chapter — the book gets a new section per topic.

| Existing package | New package | Idiom showcased |
|---|---|---|
| `scalaland_immutable3` (uses `Enumeration`) | `scalaland_scala3_enum` | `enum CreatureType { case Elf, Dwarf }` with exhaustive matching |
| `scalaland_trait` (mixin via `with`) | `scalaland_scala3_trait` | Trait parameters, `transparent trait`, `open class` discipline |
| `scalaland_func_final` | `scalaland_scala3_func` | `extension` methods, `given/using`, opaque types for `Hitpoints` |
| `scalaland_generic` | `scalaland_scala3_generic` | Match types, type classes via `given`, context bounds with `using` |
| `scalaland4/package.scala` | covered by Phase 3 rewrite | top-level definitions |
| `scalaland_immutable3.Avatar.apply` cascading mixin factory | new variant | `summon`-driven typeclass dispatch instead of trait stacking |

Each new package gets its own `*Spec.scala` test in `src/test/scala`
mirroring the lesson it teaches.

**Decision points to confirm with the author before starting:**
1. Do we want to *replace* `Enumeration`-based examples or *add a sibling
   chapter* showing `enum`? (Recommendation: add — the contrast is the
   lesson.)
2. Should `MagicalItem`/`Hitpoints`/`Strength` become opaque types, or
   stay plain `Int` to keep the early chapters approachable?
3. Cats/Cats-Effect appearance? The current code has none. Adding it
   would broaden the book's scope; recommend deferring to a separate
   "ScalaBook 2: Effects" track.

**Exit criteria:** `_scala3_*` packages compile and have unit tests; book
text drafts referencing them exist (Phase 6).

---

## Phase 6 — Book text & CI

**Book text (in https://github.com/oluies/ScalaBook)**
- Per chapter, add a "Scala 3 update" sidebar quoting the exact diff from
  Phases 1–4.
- Add new chapters for each `_scala3_*` package from Phase 5.
- Update the front matter: prerequisites now JDK 17, sbt 1.10, Scala 3.3.
- Update the README in this repo: drop `lib/`, drop `mysql/` reference if
  unused, drop Eclipse instructions.

**CI**
- Add `.github/workflows/ci.yml`: matrix on JDK 17/21, runs `sbt
  scalafmtCheckAll Test/compile test`.
- Add `scalafmt.conf` with version pinned and a default Scala 3 dialect.

**Exit criteria:** CI green on `master`; book text published referencing
the merged commits.

---

## Risks & open questions

1. **Lost dependencies.** `lib/` may contain jars no longer on Maven
   Central. Inventory it in Phase 0 — replace each with a Maven coordinate
   or remove if unused by `src/`.
2. **The `mysql/` directory.** Not referenced in any source file in the
   survey. Confirm it's dead, then delete in Phase 0.
3. **`scalaland1a`.** One-file package (`Avatar1a.scala`); confirm it's
   still pedagogically used or fold into `scalaland1`.
4. **Test coverage today: zero.** The build references scalatest but no
   `src/test/scala` exists. Phase 1 should add at least smoke tests
   (`new Avatar("x")` round-trips) per package so later phases have a
   green/red signal.
5. **Book repository sync.** This plan ships code; the book repo needs a
   parallel branch. Recommend tagging this repo `v2.x-final` before Phase 4
   so the book can still link to the Scala 2 era.

---

## Working with the book (DocBook XML)

The companion book lives at https://github.com/oluies/ScalaBook. It is
DocBook 5 XML (`src/main/resources/programmera_scala.xml` is the root,
chapters are pulled in via `<!ENTITY chap_X SYSTEM "chap/X/X.xml">`).

### The drift problem

Survey of `chap/trait/trait.xml`: **15 inline `<programlisting
language="scala">` blocks, 0 `<xi:include>`**. Code is **copy-pasted**
into the XML, not referenced. Every code change in this repo therefore
needs a corresponding XML edit, or the book diverges from what compiles.

That is the central workflow problem the migration must solve, alongside
the language version bump itself.

### Repo layout (sibling clone, no link)

```
~/projects/
├── ScalaLand/        ← this repo (Scala 3 code lives here)
└── ScalaBook/        ← clone separately, no submodule
    └── src/main/resources/chap/{trait,class,...}/X.xml
```

Tooling in `ScalaLand/scripts/book/` reaches `../ScalaBook` by relative
path. The book stays a fully separate repo with its own history and PRs.
To start:
```
cd ~/projects && git clone https://github.com/oluies/ScalaBook.git
```

### Chapter ↔ source-package mapping (best-guess, confirm in Phase 6)

| Book chapter | ScalaLand package(s) | Notes |
|---|---|---|
| `chap/class/class.xml` | `scalaland1`..`scalaland4`, `scalaland1a` | Iterative class examples |
| `chap/trait/trait.xml` | `scalaland_trait` | Mixin composition lesson |
| `chap/objfunc/objfunc.xml` | `scalaland_immutable1`..`3` | Immutable rewrite |
| `chap/func/func.xml` | `scalaland_func_final` | Functional final form |
| `chap/generics/generics.xml` | `scalaland_generic` | Generics |
| `chap/match/match.xml` | `scalaland_immutable3` (factories use match) | Pattern matching |
| `chap/{basics,types,flow,for,object,intro,akka,web,gui}` | standalone snippets | Not from the avatar example — handle ad hoc |

### Working modes (pick one per chapter)

**Mode A — Manual sync (default for prose-heavy chapters).**
Edit the XML by hand after each code change. Cheap, error-prone, fine
for chapters where the snippets are illustrative one-liners
(`chap/basics`, `chap/flow`, etc.).

**Mode B — Drift audit (recommended for the avatar chapters).**
Add `scripts/book/audit.sh` here that, for each `<programlisting>`
block whose first line starts with `package com.programmera.X`, diffs
the block against the matching `.scala` file in `src/main/scala/.../X/`.
Run it in CI; PRs that change code but not the book fail loudly.

```
# scripts/book/audit.sh — outline
for xml in ../ScalaBook/src/main/resources/chap/*/*.xml; do
  xmlstarlet sel -t -m '//*[local-name()="programlisting"]' -v '.' -n "$xml" \
    | awk '/^package com\.programmera\./ {pkg=$2; ...}' \
    | diff-against src/main/scala/$pkg/...
done
```

**Mode C — Full extract-from-source (recommended for the Scala 3 PR).**
One-shot script `scripts/book/sync-from-source.sh` that, for each
`<programlisting>` whose body starts with `package com.programmera.X`,
**replaces the body with the current contents of the corresponding
`.scala` file**. Run it once when Phase 4 lands (Scala 3 build green),
review the resulting book diff, commit to a `scala3` branch in
`ScalaBook`. From then on we either keep running it (book commit follows
code commit) or stop using it and revert to Mode A for ongoing edits.

The DocBook spec accepts `<![CDATA[ ... ]]>` inside `<programlisting>`,
so the replacement is safe even with `<` / `&` in code.

### Translation to Scala 3 idioms in book text

The book is written in Swedish. Phase 5 introduces sibling
`scalaland_scala3_*` packages — those need **new** chapter sections, not
rewrites. Suggested structure:

```xml
<sect1 xml:id="sect.trait.scala3">
  <title>Scala 3: enum, given, extension</title>
  <para>...prose contrasting with sect.trait...</para>
  <programlisting language="scala"><![CDATA[
  ...auto-extracted from src/main/scala/com/programmera/scalaland_scala3_trait/...
  ]]></programlisting>
</sect1>
```

The legacy sect1s stay intact so the book keeps working as a Scala 2
reference for readers on existing Scala 2 codebases.

### Concretely: what to do per phase

| Phase | Book action |
|---|---|
| 0–2 | None. The 2.13 syntax surface is identical to what's in the book. |
| 3 | Run Mode-B audit. Anywhere code drifts from book (e.g. `package object` removal in `scalaland4`), open a `book` PR with the fix. |
| 4 | Run Mode-C extract once. Open a single `book` PR titled "ScalaLand Scala 3 sync — auto-extracted from oluies/ScalaLand@<sha>". Tag book pre-state as `scala2-final`. |
| 5 | Per `_scala3_*` package, add a new sect1 in the relevant chapter, prose first then auto-extracted listing. |
| 6 | Wire the audit script into ScalaLand CI; book repo gets its own CI that runs `xmllint --noout --xinclude programmera_scala.xml` against the DocBook RNG. |

### Build/preview the book locally

The book repo has `project/build.properties` (sbt 0.7-era too) and a
`scripts/` dir, plus `docbook.rng`. To render without modernising the
book build:
```
xmllint --xinclude --noout --relaxng docbook.rng \
  src/main/resources/programmera_scala.xml         # validate
xsltproc /usr/share/xml/docbook/xsl-stylesheets-*/html/docbook.xsl \
  src/main/resources/programmera_scala.xml > /tmp/book.html
```
Modernising the book's own build (sbt 1.x or replacing with `pandoc`/
`asciidoctor`) is out of scope for the code migration but worth a
follow-up.

---

## Phase 7 — DocBook 5.0 → 5.2 + housekeeping

The book currently declares `<book version="5.0">` with two
non-conformances:

- Root uses legacy `lang="sv"`. DocBook 5 requires `xml:lang="sv"`.
- Chapter composition uses internal-DTD entity declarations
  (`<!ENTITY chap_X SYSTEM "...">`). XInclude (`<xi:include
  href="..."/>`) is the idiomatic DocBook 5 mechanism and is what
  `po4a`, `xmllint --xinclude`, and modern toolchains expect.

**Actions** (in `oluies/ScalaBook`, separate PR from the code repo)
1. Replace `docbook.rng` with the DocBook 5.2 RNG schema.
2. Bump `version="5.0"` → `version="5.2"` on `<book>`.
3. Rename root attribute `lang="sv"` → `xml:lang="sv"`.
4. Convert the entity-include block to XInclude:
   ```xml
   <!-- before -->
   <!ENTITY chap_trait SYSTEM "chap/trait/trait.xml">
   ...
   &chap_trait;

   <!-- after -->
   <xi:include href="chap/trait/trait.xml"/>
   ```
   Drop the entire internal subset `<!DOCTYPE book [...]>`.
5. Replace `docbook-xsl` references with **`docbook-xsl-ns`** (the
   namespace-aware fork required for DocBook 5).
6. Validate:
   ```
   xmllint --xinclude --noout --relaxng docbook.rng \
     src/main/resources/programmera_scala.xml
   ```
7. Smoke-render to HTML and PDF (FOP) to confirm no regression.

**Exit criteria:** validation green, HTML/PDF byte-similar to pre-change
(diff against `compiled/` baseline).

---

## Phase 8 — Translation pipeline (sv → en, de, uk) via po4a

Goal: produce `programmera_scala.{en,de,uk}.xml` from the Swedish
canonical source, with code listings preserved untouched.

### Why po4a
- Natively understands DocBook structure.
- Excludes code-bearing tags by default: `<programlisting>`, `<screen>`,
  `<filename>`, `<code>`, `<classname>`, `<command>`, `<computeroutput>`,
  `<userinput>`, `<varname>`. Code is never translated.
- Output: `.po` files per language — line-diffable, easy to review,
  trivial to feed to an LLM in batches.

### One-time setup
```
sudo apt install po4a            # or brew install po4a
cd ScalaBook
mkdir po
po4a-gettextize -f docbook -m src/main/resources/programmera_scala.xml \
                -p po/messages.pot
for lang in en de uk; do
  msginit -l "$lang" -i po/messages.pot -o "po/$lang.po" --no-translator
done
```

A `po4a.cfg` declares the build matrix:
```
[po_directory] po
[type:docbook] src/main/resources/programmera_scala.xml \
  $lang:src/main/resources/programmera_scala.$lang.xml \
  opt:"-k 0 -M UTF-8"
```

`po4a po4a.cfg` regenerates the per-language XML trees from `.po`
files. Wire into the book build alongside the validation step.

### LLM-assisted translation

Round-trip: extract empty `msgstr` blocks → translate via Claude → write
back. Per-batch prompt template:

```
You are translating a Swedish Scala programming textbook to <LANG>.
Glossary (DO NOT translate, keep verbatim):
  trait, case class, mixin, pattern matching, for-comprehension,
  Avatar, NPC, ScalaLand, Elf, Dwarf, Wizard, Thief, Warrior,
  Strength, Wisdom, Charisma, Hitpoints, MagicalItem
Style: technical reference, second person, no contractions.
Translate each msgid below into msgstr. Preserve <code>, <emphasis>,
<xref>, <link> XML tags exactly.

msgid: "..."
msgstr: ""
```

Suggested glossary file: `po/glossary.md`, fed into every batch so
terminology is consistent across chapters.

### Quality gates
- `xmllint --xinclude --noout --relaxng docbook.rng
  programmera_scala.<lang>.xml` for each language.
- Lint untranslated msgstrs with `msgfmt --statistics po/<lang>.po`.
- Spot-check: translator/reviewer signs off on first-chapter pass per
  language before mass translation.
- Code parity: a small script that diffs `<programlisting>` blocks
  across the four language trees — they must be byte-identical.

### Build matrix
```
make all-langs
  → programmera_scala.sv.html
  → programmera_scala.en.html  programmera_scala.en.pdf
  → programmera_scala.de.html  programmera_scala.de.pdf
  → programmera_scala.uk.html  programmera_scala.uk.pdf
```

**FOP font config for Ukrainian:** add DejaVu Sans (or Noto Sans) as a
fallback in the FOP `fop.xconf` so Cyrillic glyphs render in PDF.
One-time setup; needed for `uk` only.

### Exit criteria
- All four languages validate.
- All four PDFs render.
- Code listings are byte-identical across languages.
- First chapter (`chap/intro`) signed off in en/de/uk by a reviewer.

---

## Suggested PR sequence onto `scala3`

ScalaLand repo:
1. `chore: modernize build to sbt 1.10 + Scala 2.13` (Phase 0+1)
2. `refactor: scalafix mechanical rewrites` (Phase 2)
3. `build: cross-compile Scala 2.13 + 3.3` (Phase 3)
4. `build: Scala 3 only` (Phase 4) ← merge to `master`
5. `feat: scalaland_scala3_enum chapter` (Phase 5, one PR per package)
6. `feat: scalaland_scala3_trait chapter`
7. `feat: scalaland_scala3_func chapter`
8. `feat: scalaland_scala3_generic chapter`
9. `ci: GitHub Actions matrix + scalafmt + book audit` (Phase 6)

ScalaBook repo (parallel track):
- B1. `chore: DocBook 5.0 → 5.2, xi:include, xml:lang` (Phase 7)
- B2. `feat: po4a pipeline + en/de/uk skeletons` (Phase 8 setup)
- B3. `chore: book code listings re-extracted from ScalaLand@<sha>` (Phase 4 extract)
- B4. `feat: Scala 3 sect1 additions` (one per chapter, mirrors PRs 5–8)
- B5. `feat: en translation` / `de` / `uk` (Phase 8 content, per-chapter PRs)
