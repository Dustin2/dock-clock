# DockClock

> Working title. If it changes, update this file, the app name, and the package name.

## Language

Everything in this repository must be in English:
- Code, including identifiers and comments
- UI text and default string resources
- Documentation
- Branch names and commit messages
- Pull requests, issues, and code reviews

## What we are building

A native Android app that turns a phone into a desk clock, similar to an Echo Dot with a clock. When the phone is charging, in landscape, and still, it shows a full-screen clock along with recent notifications.

This is a learning and portfolio project. Monetization will be decided later, so do not add payments, ads, or analytics for now.

## Scope

### MVP
1. Landscape clock screen with a black background and the screen kept on.
2. Manual launch from the app.
3. Notification reading with `NotificationListenerService`.
4. Permissions screen that explains why notification access is needed and links to Settings.
5. Active notifications view with the app icon, title, and text.
6. Selection of which apps' notifications are shown.
7. Option to hide notification content.

### After the MVP
1. Automatic launch when three conditions are met: charging, in landscape, and still for a few seconds.
2. Screen saver using `DreamService`.
3. OLED burn-in protection by periodically shifting content a few pixels.
4. Night mode with reduced brightness.
5. More clock styles.

### Out of scope for now
- iOS
- Monetization
- User accounts, backend services, or sync

## Stack

- Kotlin
- Jetpack Compose with Material 3
- Coroutines and Flow
- ViewModel and StateFlow
- Hilt for dependency injection
- DataStore for preferences
- Gradle with Kotlin DSL and a version catalog (`gradle/libs.versions.toml`)
- JUnit, Turbine, and Compose UI Test
- ktlint and detekt

Use the latest stable versions that are compatible with each other. Before adding a new dependency, justify why it is needed.

## Android standards

Follow the [official Android architecture guide](https://developer.android.com/topic/architecture) and the [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html).

### Architecture
- Separate the UI and data layers. Add a domain layer only when the logic justifies it.
- Keep a unidirectional data flow: the UI sends events and the ViewModel exposes a single state.
- Organize code by feature, not by file type.
- Use repositories as the single source of truth for each type of data.
- Do not put business logic in Composables, Activities, or Services.
- Keep Android APIs behind interfaces when that allows logic to be tested without a device.

Reference structure:

```text
app/src/main/java/<package>/
├── core/
│   ├── designsystem/
│   └── di/
└── feature/
    ├── clock/
    ├── notifications/
    ├── permissions/
    └── settings/
```

### Compose
- Separate screen Composables, which connect to the ViewModel, from stateless Composables.
- Collect state with `collectAsStateWithLifecycle()`.
- Add `@Preview` to relevant visual components.
- Use colors, typography, and text from the theme and resources. Do not hardcode these values.

### Permissions, battery, and privacy
- Request only the permissions the app needs, and explain each one before requesting it.
- Make sure the app keeps working if the user denies or revokes a permission.
- Process notifications only on the device. Do not send them off the phone or write them to logs.
- Avoid unnecessary background work, and stop sensors when they are not in use.
- Before using foreground services or launching screens from the background, check the current Android restrictions and Google Play policies.

### Quality
- Add unit tests for ViewModels, repositories, and activation rules.
- These checks must pass before opening a PR:

```bash
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug
```

- Keep functions and classes small, with clear names and a single responsibility.
- Use comments to explain why a decision was made, not what the code already makes clear.
- Do not leave dead code or TODOs without context.

## Git workflow

### Branches
- `main`: stable code, ready to release.
- `develop`: integration of ongoing work.
- Create each branch from `develop` and use a prefix:
  - `feature/<short-description>`
  - `fix/<short-description>`
  - `refactor/<short-description>`
  - `docs/<short-description>`
  - `chore/<short-description>`
- Never commit directly to `main` or `develop`.
- Open PRs against `develop`. Changes reach `main` only through a PR from `develop` to `main`.
- Keep each branch and PR focused on a single change.

### Commits
Use [Conventional Commits](https://www.conventionalcommits.org/):

```text
feat(clock): add fullscreen landscape clock
fix(notifications): handle revoked listener permission
```

### Pull requests
Each PR must include:
- What changed.
- Why it changed.
- How it was tested.
- Screenshots or video if the UI changed.

**Mandatory rule:** PRs and commits must not include links to the Claude conversation or session, references to the chat, "Generated with Claude Code", or Claude `Co-Authored-By` lines. Before creating or updating a PR, check that the title, description, and commits follow this rule.

## How we work

- Before implementing anything non-trivial, propose a short plan and wait for confirmation.
- Work in small increments that can be tested.
- Explain important Android decisions, since this project is also for learning native development.
- Do not expand the scope without confirmation.
- Test anything that depends on sensors, charging, permissions, or notifications on a real device.
