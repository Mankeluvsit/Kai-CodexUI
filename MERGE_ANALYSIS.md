# Kai + CodexUI integration analysis and assumptions

## Source repository analysis

Due to environment network restrictions in this execution container, I could not clone GitHub repositories directly over git/curl.
I analyzed both repositories using GitHub HTML snapshots available through the browsing tool.

### Kai (SimonSchubert/Kai)
- Multi-module Kotlin project with `androidApp`, `composeApp`, `iosApp`, and shared tooling.
- Uses Gradle Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`).
- Android entry appears to be under `androidApp` with shared Compose architecture.

### CodexUI (friuns2/codexUI)
- Node.js + TypeScript + Vue web app (`package.json`, `src`, `vite.config.ts`).
- Distributed as `codexapp` CLI and browser UI, not as an Android library/module.
- Natural Android integration method is WebView to a hosted or local CodexUI endpoint.

## Practical integration decision

Because CodexUI is not an Android-native module, I implemented an Android WebView-based integration shell that can open CodexUI from Kai-CodexUI.

Assumptions:
1. CodexUI is run separately (local/LAN/cloud) and reachable by URL.
2. Android app should provide a direct launch surface for CodexUI.
3. CI should compile APK without requiring private signing secrets.

## Expected follow-up in a full upstream merge

If you can provide direct source access in this environment, the next step would be to:
- Rebase these changes onto upstream Kai project structure (`androidApp` module).
- Add a new navigation destination in Kai's existing UI architecture.
- Wire CodexUI endpoint settings into Kai settings storage.
- Optionally add embedded local server bootstrap logic (Termux/proot bridge if required).
