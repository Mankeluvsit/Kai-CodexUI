# Kai + CodexUI Merge Analysis and Decisions

## Phase 1: Source Code Analysis

### Repository documentation reviewed
- Kai README (SimonSchubert/Kai): Kai is a Kotlin Multiplatform project with Android + desktop targets, centered on a Compose-driven UI architecture.
- CodexUI README (friuns2/codexUI): codexUI is a Node.js web UI/service bridge for Codex workflows that exposes a browser UI and supports local/tunnel access and optional Telegram bridge.

### Existing Kai-CodexUI baseline in this repository
- `app` Android application module already existed.
- `codexui` Android library module already existed but was not wired through settings/navigation.
- Existing workflow `.github/workflows/build.yml` used legacy checkout and generic build command.

### Build and dependency audit (merged result target)
- Build system standardized to Android Gradle Plugin `8.7.3`, Kotlin `2.0.21`, Java 17.
- Compile/target SDK: 35.
- Min SDK: 26.
- Compose BOM unified to `2025.01.00` across `app` and `codexui`.
- Shared libraries aligned:
  - `androidx.core:core-ktx:1.15.0`
  - `androidx.activity:activity-compose:1.10.1` (app)
  - Lifecycle: `2.8.7`
  - Navigation Compose: `2.8.7` (app)
  - Coroutines Android: `1.8.1` (codexui)

### Module structure decisions
- **Chosen:** multi-module (`:app` + `:codexui`).
- **Reasoning:** preserves separation of responsibilities.
  - `:app` = Kai hub shell and navigation.
  - `:codexui` = embedded service management domain and screen implementation.
- This cleanly supports future extraction/reuse while keeping a unified app experience.

### Manifest/resource/code collision analysis
- No direct class/package collisions in current baseline because no source files existed.
- Resource collision risk mitigated by module-level namespacing and distinct string keys.
- Manifest consolidation done by keeping app launcher activity in `app` and library manifest minimal in `codexui`.

### Existing CI/CD review
- Replaced legacy `build.yml` with explicit debug build workflow on `main` push:
  - JDK 17
  - Gradle cache
  - `assembleDebug`
  - artifact upload with UTC timestamp
  - secret mapping via environment variables

## Phase 2: Integration Architecture

### Kai as primary hub
- `MainActivity` in `app` is the single entrypoint.
- Bottom navigation exposes:
  - Kai home (hub context)
  - CodexUI management screen (embedded)

### CodexUI server management panel features
Implemented in `codexui` module and embedded in Kai navigation:
- Gateway host/port configuration
- Proxy enabled/host/port configuration
- Public URL configuration
- Start/stop controls
- Trigger operation control (health check)
- Real-time state + rolling logs displayed in UI

### Functional preservation strategy
- The integrated Android app preserves Kai-as-hub UX while incorporating codex service controls in-app.
- CodexUI is represented as a managed subsystem (not standalone launcher app).

## Phase 3: Workflow Design
- `.github/workflows/debug-build.yml` runs on push to `main`.
- Uses `actions/setup-java@v4` with Temurin 17 and Gradle cache.
- Runs `./gradlew --stacktrace --no-daemon assembleDebug`.
- Uploads `app/build/outputs/apk/debug/*.apk` as timestamped artifact.
- Consumes secrets via `ORG_GRADLE_PROJECT_*` env mapping.
