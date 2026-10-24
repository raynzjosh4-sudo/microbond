---
name: file-modularity-and-widget-extraction
description: 300-line code ceiling, modular new file creation, and folder grouping for new code.
---

# File Modularity & Code Sizing Guidelines

### 1. 300-Line Code Ceiling
- **Do not bloat large files**: If any file is **over 300 lines of code**, do NOT add new features, state logic, or components directly into it.
- **Separate New File**: Always create a **separate new file** for the new code, components, dialogs, helpers, or mappers to keep files compact and maintainable.

### 2. Folder Organization (> 2 Files)
- When new functionality or extracted components comprise **more than 2 new files**, create a dedicated folder/sub-package (e.g., `widgets/`, `dialogs/`, `sheets/`, `mappers/`, or domain subfolders) and place the files inside instead of leaving them loose.

### 3. Compose Widget & Component Extraction
- When developing UI screens in Jetpack Compose, always extract independent, self-contained components—such as bottom sheets, custom top bars, dialogs, cards, or complex list items—into their own files.
- Place screen widgets inside a `widgets` directory next to the screen file (e.g., `feature_groups/ui/newgroup/widgets/` or `threadscreen/threadscreenwidgets/`).
- Keep screen root files focused purely on high-level layout orchestration, scaffold setup, and state wiring.
