# Kai + CodexUI Integration Analysis and Decisions

## Source analysis inputs

- The local `Kai-CodexUI` repository initially contained only Gradle skeleton files, no Android source code.
- `CodexUI` upstream README and `package.json` were inspected via raw GitHub endpoints.
- `CodexUI` is distributed primarily as a Node/Vite/Vue server+web UI package (`codexapp`) rather than an Android app module.

## Architecture decision

- **Primary hub:** Android `app` module acts as Kai hub and global navigation shell.
- **Embedded CodexUI controls:** `codexui` Android library module provides a management panel composable and controller state machine.
- **Integration mode:** CodexUI is treated as a managed service endpoint (gateway/proxy/port/url + lifecycle controls + logs) surfaced inside Kai navigation.

## Structural choice

- **Chosen:** multi-module (`:app` + `:codexui`).
- **Why:** preserves boundary between host shell and CodexUI-management feature while keeping one APK and one launcher activity.

## Dependency and version choices

- Android Gradle Plugin `8.7.3`, Kotlin `2.0.21`, Java 17 toolchain.
- Compose BOM `2025.01.01` and lifecycle/navigation versions aligned across modules.
- These values are compatible with `compileSdk = 35`, and modern Compose/lifecycle APIs used by the management panel.

## Manifest consolidation

- `app` manifest defines launcher activity and app shell.
- `codexui` manifest contributes `INTERNET` permission so server control and health checks can access endpoints.

## Functional preservation mapping

- **Kai-origin role (host/hub):** Main navigation shell and app entrypoint.
- **CodexUI-origin role (service UI):** browser/server workflow concepts represented as gateway/proxy/port/url controls, service state, and logs panel.

## CI/CD workflow decision

- Added `.github/workflows/debug-build.yml` for pushes to `main`.
- Uses JDK 17, Gradle caching, `assembleDebug`, and APK artifact upload.
- Secrets are consumed from GitHub Secrets (`KAI_CODEXUI_API_KEY`, `CODEXUI_GATEWAY_TOKEN`) and never hardcoded.
