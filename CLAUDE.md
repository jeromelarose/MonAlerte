# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Kotlin Multiplatform (KMP) project targeting Android and iOS, using Compose Multiplatform for the UI layer. The project namespace is `org.jelarose.monalerte`.

## Project Structure

- `/composeApp/src/` - Shared code across platforms
  - `commonMain/` - Common code for all targets
  - `androidMain/` - Android-specific implementations
  - `iosMain/` - iOS-specific implementations
  - `commonTest/` - Shared tests
- `/iosApp/` - iOS application entry point and SwiftUI code

## Build Commands

### Android
```shell
# Build debug APK
./gradlew :composeApp:assembleDebug

# Install debug build on connected device
./gradlew installDebug

# Run all Android tests
./gradlew connectedAndroidTest
```

### iOS
```shell
# Run iOS tests on simulator
./gradlew iosSimulatorArm64Test

# Build iOS framework
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

### General Development
```shell
# Build entire project
./gradlew build

# Run all tests
./gradlew allTests

# Run lint checks
./gradlew lint

# Clean build artifacts
./gradlew clean
```

## Architecture

The app follows Kotlin Multiplatform architecture patterns:

- **Shared Business Logic**: All core logic resides in `commonMain` using Kotlin Multiplatform
- **UI Layer**: Compose Multiplatform provides shared UI components across Android and iOS
- **Platform-Specific Code**: Platform implementations use expect/actual mechanism for platform-specific APIs
- **Dependencies**: Managed via Gradle Version Catalog (`gradle/libs.versions.toml`)

Key technical details:
- Kotlin version: 2.2.10
- Compose Multiplatform version: 1.8.2
- Android compile SDK: 36
- Android min SDK: 24
- JVM target: 11