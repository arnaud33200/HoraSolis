# Claude Rules

- Keep chat responses minimal. Do not write summaries of changes — the user reads the file diffs directly.
- Do not produce Markdown reports/recaps of work performed unless explicitly asked.
- Never use `first()` or `last()` on collections — always use `firstOrNull()` or `lastOrNull()` and handle the null case explicitly.
- Never use `while (true)` — for timer/polling loops, always use `while (isActive)` instead.
