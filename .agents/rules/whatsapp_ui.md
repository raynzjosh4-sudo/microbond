---
description: Design principles and UI guidelines for the Microbond project.
---

# UI & Brand Guidelines: Modern Messaging UX for Microbond

This project emulates the familiar, frictionless modern messaging UX (2024) while strictly adhering to **Microbond's own brand identity, theme tokens, and assets**.

### 1. Strict Brand & Asset Invariants
- **NEVER use the word "WhatsApp"** in any user-facing text, disclaimers, titles, dialogs, or documentation. The application is **Microbond**.
- **NEVER import or draw WhatsApp logos or trademarked icons**.
- **ALWAYS use Microbond's official branding and assets**:
  - App Logo: `R.drawable.ic_launcher_custom` (clean transparent background, do not use `appstore.png` which has an unwanted white background border).
  - Splash: `R.drawable.splash_dark`.
  - App Name: `Microbond`.

### 2. Zero Hard-Coded Colors
- **NEVER declare inline hex colors** like `Color(0xFF...)`, `Color(0x...)`, or stock constants (`Color.White`, `Color.Black`, `Color.Red`, etc.) in Composables, screens, or widget files.
- **ALL colors must be centralized in a single file**: `com.microbond.app.core.ui.theme.Color.kt`.
- **ALWAYS import colors from `Color.kt`** (e.g., `MicrobondPureWhite`, `MicrobondDestructiveRed`, `MicrobondDestructiveDisabledBg`, `MicrobondActiveGreen`, `MicrobondDarkBackground`, `MicrobondTextPrimary`, `MicrobondInactiveGray`, etc.).
- If a screen or widget requires a new semantic, accent, or background color, define it in `Color.kt` first, then import it.

### 3. Sizing, Spacing & Layout
- Use consistent dimension tokens (e.g., `4.dp`, `8.dp`, `12.dp`, `16.dp`, `24.dp`, `32.dp`, `48.dp`).
- Use standard Material 3 / messaging typography scales (`12.sp` caption, `13.5.sp` - `14.sp` secondary/body, `15.sp` - `16.sp` primary body/button, `20.sp` - `26.sp` titles).
- Maintain edge-to-edge system insets using `statusBarsPadding()` and `navigationBarsPadding()`.
- Extract independent, self-contained components into a `widgets` directory per `widget_extraction.md`.
