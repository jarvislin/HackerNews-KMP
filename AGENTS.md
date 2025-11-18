# Repository Guidelines

## Project Structure & Module Organization
HackerNews-KMP centralizes shared Kotlin Multiplatform code inside `composeApp/`. Business logic lives in `composeApp/src/commonMain/kotlin` split into `data/` (network + persistence), `domain/` (use cases), `presentation/` (view models), and `ui/` (screens + components). Platform wrappers sit in `composeApp/src/androidMain` and `composeApp/src/iosMain`. The Swift host is under `iosApp/iosApp`, with per-target settings in `iosApp/Configuration`. Marketing assets and store art remain in `resources/`, while Gradle build logic stays at the repo root (`build.gradle.kts`, `gradle/`).

## Build, Test, and Development Commands
- `./gradlew :composeApp:assembleDebug` — builds the Android debug APK in `composeApp/build/outputs/apk`.
- `./gradlew :composeApp:bundleRelease` — produces the Play-ready AAB and runs release code shrinkage.
- `./gradlew :composeApp:check` — executes all unit tests and multiplatform verifications.
- `open iosApp/iosApp.xcodeproj` — launch Xcode, pick a simulator or device, and hit `⌘R` to build/run the iOS app (Gradle-generated `ComposeApp.framework` is already linked).

## Coding Style & Naming Conventions
Follow the official Kotlin style guide: 4-space indents, trailing commas for multiline literals, and prefer `val` for immutability. Compose functions use PascalCase nouns (`HnStoryList`, `StoryToolbar`). View-models live under `presentation/.../viewmodel` and end with `ViewModel`. Keep files focused per feature; cross-cutting helpers belong in `extensions/` or `utils/`. When editing Swift, mirror Kotlin naming and keep modules namespaced `HN...` to avoid collisions.

## Testing Guidelines
Use `kotlin.test` for common logic and platform runners for target-specific code. Add suites under `composeApp/src/commonTest/kotlin` (shared) or `composeApp/src/androidUnitTest`. Name classes `FeatureScenarioTest` and mirror the package under test. Run `./gradlew :composeApp:testDebugUnitTest` for Android JVM tests and `./gradlew :composeApp:iosSimulatorArm64Test` before pushing to validate iOS frameworks. Aim to cover new view-model branches and data mappers; document flaky test exclusions in the PR.

## Commit & Pull Request Guidelines
Work on feature branches (`feature/<topic>` or `fix/<topic>`) off `develop`, mirroring the repo’s Git Flow history. Keep commit subjects under 72 characters, imperative, and optionally prefix with the scope (`Feature:`, `Fix:`) already in the log. Each PR should explain the user impact, list key Gradle/Xcode commands you ran, attach screenshots or screen recordings for UI changes, and link related issues or store checklist items. Request review only after CI (`:composeApp:check`) is green.

## Security & Configuration Tips
Never commit personal API tokens; shared configuration stays in `local.properties` and `iosApp/Configuration/Config.xcconfig`, which are gitignored. If you need new secrets, document placeholder names and update `privacy.md`/`terms.md` when behavior changes. Review bundles for accidental debug logging before tagging releases.
