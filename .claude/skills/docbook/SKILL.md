---
name: docbook
description: Validate, render, upgrade, and translate DocBook 5 books — specifically the ScalaBook (oluies/ScalaBook) at ../ScalaBook. Covers DocBook 5.0→5.2 upgrade, xi:include conversion, po4a-based translation to en/de/uk with code-listing preservation, and HTML/PDF builds via xsltproc + FOP.
---

# DocBook skill (ScalaBook companion)

The ScalaBook (https://github.com/oluies/ScalaBook) is a Swedish Scala
textbook in DocBook 5 XML. This skill captures the working commands.
The book is expected at **`../ScalaBook`** relative to ScalaLand
(sibling clone, no submodule).

## Layout reminders

```
../ScalaBook/
├── docbook.rng                              # schema (currently 5.0)
├── src/main/resources/
│   ├── programmera_scala.xml                # book root
│   ├── chap/<topic>/<topic>.xml             # chapter files
│   └── apdx/<topic>/<topic>.xml             # appendix files
└── po/                                      # (Phase 8) translations
    ├── messages.pot
    ├── en.po  de.po  uk.po
    └── glossary.md
```

Code in chapters is embedded inline as `<programlisting language="scala">…</programlisting>`. The trait chapter alone has 15 such blocks and **zero `xi:include`**. Treat the inline-paste pattern as the default; never assume `xi:include` for code.

## Validate

```bash
xmllint --xinclude --noout --relaxng ../ScalaBook/docbook.rng \
  ../ScalaBook/src/main/resources/programmera_scala.xml
```

If validation fails after the 5.2 upgrade, common causes: missing
`xml:lang` namespace prefix on root attribute, leftover internal-DTD
entity references, `<sect1>` without `xml:id` when cross-referenced.

## Render

HTML (single file):
```bash
xsltproc --xinclude \
  /usr/share/xml/docbook/stylesheet/docbook-xsl-ns/html/docbook.xsl \
  ../ScalaBook/src/main/resources/programmera_scala.xml \
  > /tmp/book.html
```

PDF via FOP:
```bash
xsltproc --xinclude \
  /usr/share/xml/docbook/stylesheet/docbook-xsl-ns/fo/docbook.xsl \
  ../ScalaBook/src/main/resources/programmera_scala.xml > /tmp/book.fo
fop -fo /tmp/book.fo -pdf /tmp/book.pdf
```

For Ukrainian PDFs configure DejaVu Sans (or Noto Sans) in `fop.xconf`
so Cyrillic glyphs render. One-time setup.

## Upgrade 5.0 → 5.2

Mechanical changes on `programmera_scala.xml`:
1. Drop the entire `<!DOCTYPE book [ ... ]>` internal subset.
2. Replace `version="5.0"` → `version="5.2"`.
3. Replace `lang="sv"` → `xml:lang="sv"`.
4. Replace each `&chap_X;` reference with `<xi:include href="chap/X/X.xml"/>`.
5. Drop `docbook.rng` and replace with the 5.2 RNG (download from docbook.org).

Validate after each step rather than batching.

## Translate (po4a, sv → en/de/uk)

Initial extraction:
```bash
cd ../ScalaBook
mkdir -p po
po4a-gettextize -f docbook -m src/main/resources/programmera_scala.xml \
                -p po/messages.pot
for lang in en de uk; do
  msginit -l "$lang" -i po/messages.pot -o "po/$lang.po" --no-translator
done
```

`po4a.cfg`:
```
[po_directory] po
[type:docbook] src/main/resources/programmera_scala.xml \
  $lang:src/main/resources/programmera_scala.$lang.xml \
  opt:"-k 0 -M UTF-8"
```

Update from changed source / regenerate translated XML:
```bash
po4a po4a.cfg
```

po4a excludes from translation by default: `<programlisting>`,
`<screen>`, `<filename>`, `<code>`, `<classname>`, `<command>`,
`<computeroutput>`, `<userinput>`, `<varname>`. Code stays Swedish-free
by construction.

### LLM translation prompt (per batch of msgids)

```
You are translating a Swedish Scala programming textbook to <LANG>.

GLOSSARY (KEEP VERBATIM, do not translate):
  trait, case class, mixin, pattern matching, for-comprehension,
  Avatar, NPC, ScalaLand, Elf, Dwarf, Wizard, Thief, Warrior,
  Strength, Wisdom, Charisma, Hitpoints, MagicalItem,
  scala, sbt, scalac, val, var, def, object, class, package

STYLE: technical reference, second person, no contractions.
TAGS: preserve <code>, <emphasis>, <xref linkend="…"/>, <link>, <filename>
      exactly as they appear in msgid.

For each msgid below, write the msgstr. Output ONLY the msgstr lines.
```

Save the glossary at `po/glossary.md` and feed it on every batch.

### Quality gates

```bash
# stats per language
for lang in en de uk; do msgfmt --statistics po/$lang.po; done

# validate generated XML
for lang in sv en de uk; do
  xmllint --xinclude --noout --relaxng docbook.rng \
    src/main/resources/programmera_scala.$lang.xml
done

# code parity: programlistings must be byte-identical across langs
diff <(xmlstarlet sel -t -m '//d:programlisting' -v . -n \
        src/main/resources/programmera_scala.sv.xml) \
     <(xmlstarlet sel -t -m '//d:programlisting' -v . -n \
        src/main/resources/programmera_scala.en.xml)
```

## Sync code from ScalaLand → DocBook

For each `<programlisting>` whose first non-blank line matches
`^package com\.programmera\.([a-zA-Z0-9_]+)`, replace the body with
the concatenated contents of
`../ScalaLand/src/main/scala/com/programmera/<pkg>/*.scala`.

Use CDATA wrapping in the replacement so `<` and `&` in code are safe:
```xml
<programlisting language="scala"><![CDATA[
…file contents…
]]></programlisting>
```

The `xml:lang` of the surrounding chapter is irrelevant — code listings
are language-agnostic and must be identical across all language trees.

## Anti-patterns

- Do **not** add `xi:include` for code listings (e.g.
  `<xi:include parse="text" href="../ScalaLand/.../Avatar.scala"/>`).
  Tempting, but it couples the book build to the code repo's path,
  breaks po4a's code-exclusion guarantee in some renderers, and makes
  the book non-self-contained for archives.
- Do **not** translate `<programlisting>` content even manually —
  Scala identifiers must match the code repo character-for-character.
- Do **not** edit `programmera_scala.<lang>.xml` directly; it is
  regenerated from `.po`. Edit `po/<lang>.po` instead.
