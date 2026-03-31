# Kai + CodexUI Integration Report

## Scope and assumptions

Because the container cannot fetch Git repositories over HTTPS, this implementation uses a practical merge strategy:

- Keep Kai-CodexUI as an Android app shell.
- Surface CodexUI through an embedded WebView-backed Compose screen.
- Add CodexUI as an internal Android library module (`:codexui`) to keep integration boundaries clean.

## Repository analysis summary

### Kai (target architecture assumptions)

- Kotlin + Gradle based Android build.
- Android entry point is an Activity with UI navigation.
- Suitable for adding a feature module and wiring a launch action.

### CodexUI (integration assumptions)

- Delivered as a UI frontend that can be rendered in a web context.
- No direct Android Activity export required if embedded via WebView.

## Integration points

1. `settings.gradle.kts` includes `:codexui` beside `:app`.
2. `app/build.gradle.kts` depends on `project(":codexui")`.
3. `MainActivity` adds a button to open CodexUI.
4. `CodexUiScreen` composes a WebView wrapper with back navigation.
5. `codexui` manifest adds `INTERNET` permission.
6. GitHub Actions workflow compiles release APK and uploads artifact.

## Conflict and dependency handling

- Unified Kotlin (`2.0.21`) and Android Gradle Plugin (`8.7.3`) versions in root.
- Unified compile/target SDK (`35`) across app and library.
- Compose dependencies aligned through BOM (`2025.01.01`).
- CodexUI-specific dependency isolated in `:codexui` (`androidx.webkit`).

## Follow-up for production readiness

- Replace placeholder URL in `CodexUiScreen` with your deployed CodexUI endpoint.
- If offline/local execution is required, package static assets or run a local HTTP server.
- Add signing config and secrets to produce signed release APKs in CI.
