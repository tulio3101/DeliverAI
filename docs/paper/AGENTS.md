# DeliverAI Paper Contribution Guide

This folder contains the LaTeX source for the DeliverAI paper.

## Entry Point

- Paper root: `docs/paper/`
- Main file: `docs/paper/main.tex`
- `main.tex` is the orchestrator only. Do not place long-form content there.
- Section content lives in `docs/paper/pages/`.
- Shared LaTeX configuration lives in `docs/paper/config/`.
- References live in `docs/paper/pages/references.tex`.

## Structure Rules

- Keep the modular structure intact.
- Add or edit content in `pages/`, not directly in `main.tex`.
- If a new section is needed, create a new file under `pages/` and include it from `main.tex`.
- Preserve existing labels, figure/table naming style, and citation keys.
- Do not move generated build files into source control.

## Writing Rules

- Use a concise academic-professional tone.
- Keep claims concrete and evidence-backed.
- Do not invent technical status, metrics, ROI, deployment details, or references.
- Distinguish implemented MVP, pilot conditions, and production gaps.
- Align the paper with the current DeliverAI repo and the harness context.
- Maintain the existing look and feel of figures, tables, captions, and prose.

## Citations And References

- Every new `\cite{...}` must have a matching `\bibitem{...}` in `pages/references.tex`.
- Every new `\bibitem{...}` should be cited from the paper.
- Prefer primary or high-quality sources: peer-reviewed papers, official docs, standards, or verified reports.
- Do not cite weak sources when a stronger source is available.

## Figures And Tables

- Keep figures and tables compact enough for A4 layout.
- Use stable labels:
  - Figures: `fig:...`
  - Tables: `tab:...`
- Avoid visual overlap, overwide diagrams, or unreadable labels.
- If using TikZ or pgfplots, compile and visually inspect the generated PDF.

## Preview In VS Code

1. Install the dependencies listed in `docs/paper/README.md`.
2. Install VS Code extensions:
   - LaTeX
   - LaTeX Workshop
3. Open `docs/paper/main.tex`.
4. Press the green build/play button.
5. `main.pdf` is generated next to `main.tex`.

## Manual Build

Always run commands from the paper folder:

```bash
cd docs/paper
latexmk -pdf -interaction=nonstopmode -file-line-error main.tex
```

Fallback:

```bash
pdflatex -interaction=nonstopmode -file-line-error main.tex
pdflatex -interaction=nonstopmode -file-line-error main.tex
```

## Verification Before Finishing

Run at least:

```bash
latexmk -pdf -interaction=nonstopmode -file-line-error main.tex
```

Recommended checks:

```bash
chktex -q pages/<changed-file>.tex
chktex -q pages/references.tex
```

Before handing off, confirm:

- No missing `\input` files.
- No undefined labels or citations.
- No new LaTeX warnings caused by the change.
- No new `Overfull \hbox` or `Underfull \hbox` issues.
- Generated PDF renders the changed section correctly.

## Agent Guardrails

- Do not rewrite the whole paper unless explicitly requested.
- Do not change unrelated sections.
- Do not modify generated files such as `.aux`, `.log`, `.out`, `.pdf`, `.fdb_latexmk`, `.fls`, or `.synctex.gz`.
- Do not run deploys, Terraform, cloud commands, or repository-wide destructive commands.
- If the repo state contradicts older notes, verify against the actual files in the repo before editing.
- Keep edits reviewable: focused prose improvements, precise citations, and small structural changes.
