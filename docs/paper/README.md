# DeliverAI Paper

This folder contains the LaTeX source for the DeliverAI applied dissemination paper.

Main file:

```text
docs/paper/main.tex
```

Course/source context:

```text
docs/paper/sources/rubric.md
docs/paper/sources/mod6-spec.md
```

The paper is written in Spanish, but this README is in English so every collaborator can follow the same setup and build steps.

## What This Paper Covers

The paper aligns DeliverAI with the final-course rubric:

- Business problem and use case.
- Proposed AI agent solution.
- Current MVP technical scope.
- Architecture and implementation state.
- Business value and pilot metrics.
- Risks, controls, governance and human oversight.
- Roadmap from prototype to controlled pilot.
- Main lessons learned.

The document intentionally separates:

- **Implemented/prototype:** Spring Boot API, PostgreSQL, Admin UI, frontend AWS static hosting and n8n workflow export.
- **Pilot conditions:** governed n8n hosting, product list endpoint, order-user association, Admin UI authentication, end-to-end traceability and HITL process.
- **Future/post-pilot:** payments, multi-tenancy, production MCP tooling, A2A and advanced automation.

## LaTeX Dependencies

`main.tex` currently uses these LaTeX packages:

- `babel` with Spanish support.
- `inputenc`.
- `geometry`.
- `amsmath`.
- `graphicx`.
- `todonotes`.
- `hyperref`.
- `booktabs`.
- `epigraph`.
- `tikz`.
- `pgfplots`.

The easiest setup is a medium/full TeX distribution plus `latexmk`.

## Linux Setup

### Ubuntu / Debian

Recommended full install:

```bash
sudo apt update
sudo apt install texlive-full latexmk
```

Smaller install that should cover this paper:

```bash
sudo apt update
sudo apt install \
  texlive-latex-recommended \
  texlive-latex-extra \
  texlive-lang-spanish \
  texlive-pictures \
  latexmk
```

### Arch / CachyOS / Manjaro

On current Arch/CachyOS package splits, `latexmk` is not a standalone `pacman`
target. It is installed through `texlive-binextra`, which provides
`texlive-latexmk`.

```bash
sudo pacman -Syu
sudo pacman -S --needed \
  texlive-basic \
  texlive-latex \
  texlive-latexrecommended \
  texlive-latexextra \
  texlive-pictures \
  texlive-langspanish \
  texlive-binextra
```

Verify that LaTeX Workshop will be able to find `latexmk`:

```bash
command -v latexmk
latexmk -v
```

If `pacman` previously failed with `error: target not found: latexmk`, rerun the
command above. The previous multi-package transaction may not have installed the
rest of the TeX packages after `pacman` rejected the unknown target.

If your distribution uses the older TeX Live package names, install the
equivalent packages for LaTeX extra packages, Spanish language support, TikZ/PGF
and `latexmk`.

### Fedora

```bash
sudo dnf install texlive-scheme-medium texlive-babel-spanish texlive-pgfplots latexmk
```

If a package name differs on your Fedora version, install `texlive-scheme-full` instead:

```bash
sudo dnf install texlive-scheme-full latexmk
```

## Windows Setup

### Option A: MiKTeX

Install MiKTeX with `winget`:

```powershell
winget install MiKTeX.MiKTeX
```

Then open **MiKTeX Console** and:

1. Update the package database.
2. Update installed packages.
3. Enable automatic package installation if prompted.

If `latexmk` is missing, install it from MiKTeX Console or run:

```powershell
mpm --install=latexmk
```

### Option B: TeX Live

Install TeX Live manually from:

```text
https://tug.org/texlive/windows.html
```

Choose the full scheme if disk space is not a problem. It avoids missing-package surprises.

### Recommended Windows Previewer

Any PDF viewer works, but SumatraPDF is lightweight and works well with LaTeX editors:

```powershell
winget install SumatraPDF.SumatraPDF
```

## Editor Setup

Recommended editor: VS Code.

Install:

- **LaTeX Workshop** extension.
- Optional: **LTeX** or another spell checker for Spanish.

Useful VS Code workflow:

1. Open the repo folder.
2. Open `docs/paper/main.tex`.
3. Use LaTeX Workshop command: `LaTeX Workshop: Build LaTeX project`.
4. Use LaTeX Workshop command: `LaTeX Workshop: View LaTeX PDF`.

### LaTeX Workshop: `spawn latexmk ENOENT`

This error means VS Code tried to run `latexmk`, but no `latexmk` binary was
available in its `PATH`.

On Arch/CachyOS/Manjaro, install the package that provides it:

```bash
sudo pacman -S --needed texlive-binextra
```

Then reload VS Code and verify from an integrated terminal:

```bash
command -v latexmk
latexmk -pdf -interaction=nonstopmode -file-line-error docs/paper/main.tex
```

## Compile The Paper

Always run commands from this folder:

```bash
cd docs/paper
```

On Windows PowerShell, use the repo path on your machine, for example:

```powershell
cd C:\path\to\DeliverAI\docs\paper
```

### Recommended Build

Linux/macOS:

```bash
latexmk -pdf -interaction=nonstopmode -file-line-error main.tex
```

Windows PowerShell:

```powershell
latexmk -pdf -interaction=nonstopmode -file-line-error main.tex
```

Output:

```text
main.pdf
```

### Fallback Build Without `latexmk`

Run `pdflatex` twice so references and table/figure numbers resolve:

Linux/macOS:

```bash
pdflatex -interaction=nonstopmode -file-line-error main.tex
pdflatex -interaction=nonstopmode -file-line-error main.tex
```

Windows PowerShell:

```powershell
pdflatex -interaction=nonstopmode -file-line-error main.tex
pdflatex -interaction=nonstopmode -file-line-error main.tex
```

## Preview The PDF

### Linux

Generic:

```bash
xdg-open main.pdf
```

Common alternatives:

```bash
evince main.pdf
okular main.pdf
zathura main.pdf
```

### Windows

PowerShell:

```powershell
start .\main.pdf
```

If using SumatraPDF:

```powershell
SumatraPDF.exe .\main.pdf
```

## Clean Build Files

`latexmk` cleanup:

```bash
latexmk -C
```

Manual cleanup if needed:

```bash
rm -f main.aux main.fdb_latexmk main.fls main.log main.out main.pdf main.synctex.gz
```

Windows PowerShell:

```powershell
Remove-Item main.aux,main.fdb_latexmk,main.fls,main.log,main.out,main.pdf,main.synctex.gz -ErrorAction SilentlyContinue
```

## Collaboration Rules

- Edit `main.tex` for paper content.
- Keep source context in `sources/` read-only unless the team intentionally updates the assignment/rubric notes.
- Do not commit generated build files such as `.aux`, `.log`, `.out`, `.fls`, `.fdb_latexmk` or `.synctex.gz`.
- Decide as a team whether `main.pdf` should be committed. For normal collaboration, each person can build it locally.
- Keep technical claims aligned with the real repo state. If a feature is external, pending or only a pilot condition, label it that way.
- After major edits, compile locally and skim the PDF before opening a PR or sharing the document.

## Troubleshooting

### `babel` Spanish error

Install Spanish language support:

```bash
sudo apt install texlive-lang-spanish
```

On MiKTeX, install the missing `babel-spanish` package from MiKTeX Console.

### `tikz.sty` or `pgfplots.sty` not found

Install TikZ/PGF packages:

```bash
sudo apt install texlive-pictures
```

On MiKTeX, allow automatic package installation or install `pgf` / `pgfplots` from MiKTeX Console.

### `todonotes.sty` or `epigraph.sty` not found

Install LaTeX extra packages:

```bash
sudo apt install texlive-latex-extra
```

On MiKTeX, install the missing package from MiKTeX Console.

### References show as `??`

Compile again, or use `latexmk`:

```bash
latexmk -pdf main.tex
```

### Accented characters look wrong

Ensure the file is saved as UTF-8. `main.tex` uses:

```latex
\usepackage[utf8]{inputenc}
```

### PDF viewer locks the file on Windows

Some viewers lock `main.pdf`, preventing recompilation. Close the PDF or use SumatraPDF, which handles LaTeX rebuilds more smoothly.

## Style & Collaboration Guide

- **One file per section.** Each `\section` lives in exactly one `pages/NN-slug.tex`; never split a section across files or merge two sections into one.
- **Adding a section.** Create `pages/NN-slug.tex` (next free zero-padded number), add `\input{pages/NN-slug.tex}` to `main.tex` in reading order, and open the new file with the standard header (section, rubric criterion, style rules).
- **Citations.** IEEE-ish numbered style via `references.tex`'s `thebibliography`. Every `\bibitem` must be `\cite`d at least once; every `\cite` must resolve to a `\bibitem`. Diff bibitem keys vs cite keys before opening a PR.
- **Word budget.** Rubric target is 2000-3000 words for the body (excludes front matter/references). Check with `wc -w` before submitting; trim rather than pad.
- **Diagram palette.** Keep the fixed TikZ color code: orange = conversational/agent layer, blue = transactional/core layer, green = operational/done, gray = future/pending. Reuse these fills; don't add new colors without team agreement.
- **Claims discipline.** Tag every technical claim as implemented, demo/prototype, or pending/future — never present as production-ready.
- **Before opening a PR.** Run `chktex pages/*.tex` and `latexmk -pdf -interaction=nonstopmode -file-line-error main.tex`; both must be clean (or only pre-existing warnings), and skim the rendered PDF section you touched.
