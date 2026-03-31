# Kai + CodexUI Integration Analysis

## Scope and assumptions
- Analysis date: 2026-03-31.
- Upstream Kai repository analyzed: `SimonSchubert/Kai` (branch `main`).
- Upstream CodexUI repository analyzed: `friuns2/codexUI` (branch `main`).
- This target repository did not contain the full upstream Kai source tree, so integration is implemented in the existing `app` module with a practical Android-native host shell.
- CodexUI is not an Android module; it is a Node/Vite web application. Android integration is therefore performed through a `WebView` host activity that loads CodexUI's served URL.

## Kai repository findings
- Architecture: Kotlin Multiplatform with Android app entry under `androidApp` and shared Compose code in `composeApp`.
- Build system: Gradle Kotlin DSL with version catalog (`gradle/libs.versions.toml`), AGP 9.0.1, Kotlin 2.3.20, compile/target SDK 36, min SDK 26.
- Package and entry points: Android launcher activity resides in `androidApp/src/main` and delegates into shared Compose UI.
- Navigation structure: Compose navigation stack in shared code (no legacy XML navigation drawer detected in upstream root layout pattern).

## CodexUI repository findings
- Build output: Browser-based Vite bundle and Node bridge middleware; no Android APK/AAR output.
- Exported components: Vue application with server middleware (`vite.config.ts`, `src/server/...`) and web endpoints/websockets.
- Android interface model: Operates over HTTP in browser; on Android, intended to be reached through browser or embedded `WebView` if hosted locally.

## Integration points
1. **UI surfacing in Kai app module**
   - Added a navigation drawer menu item `CodexUI` in `app/src/main/res/menu/activity_main_drawer.xml`.
   - Added trigger in `MainActivity` to launch `CodexUiActivity` when selected.

2. **Android host for CodexUI**
   - Added `CodexUiActivity` with `WebView` loading `http://127.0.0.1:18923` (configurable string resource).
   - Added `INTERNET` permission and activity declaration in `AndroidManifest.xml`.

3. **Build/dependency alignment**
   - Kept a single `app` module (no separate `codexui` Gradle module).
   - Added AppCompat/Material/DrawerLayout + Navigation libraries for drawer integration.

## Version overlap and conflict resolution
- **Kotlin/AGP mismatch with original repo skeleton**:
  - Original root used AGP 7.0.4 + Kotlin 1.5.31 but module configured Java 17 and modern AndroidX libs.
  - Resolved by upgrading root plugin versions to AGP 8.7.3 + Kotlin 2.0.21.
- **SDK mismatch with upstream Kai**:
  - Upstream Kai uses compile/target SDK 36.
  - Target repo is aligned on SDK 35; retained 35 for practical compatibility with current project baseline.
- **Namespace/package conflicts**:
  - Upstream Kai package names differ from this target repo.
  - Resolved by namespacing integration classes under `com.mankeluvsit.kaicodexui`.

## CI/CD implications
- Added GitHub Actions workflow to:
  - Run on `push` to `main` and `pull_request`.
  - Use JDK 17 and Gradle cache.
  - Generate a temporary self-signed keystore via `keytool`.
  - Build signed release APK via `assembleRelease`.
  - Upload APK artifact.
