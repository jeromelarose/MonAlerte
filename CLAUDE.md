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

The app follows a clean, feature-based architecture with Kotlin Multiplatform patterns:

### Code Organization
- **Feature-based modules**: Each feature has `data/`, `domain/`, and `presentation/` layers
- **Core infrastructure**: Located in `core/` with cross-cutting concerns
- **Shared Business Logic**: All core logic resides in `commonMain`
- **UI Layer**: Compose Multiplatform provides shared UI components across Android and iOS
- **Platform-Specific Code**: Platform implementations use expect/actual mechanism for platform-specific APIs

### Key Technical Stack
- **Navigation**: Voyager Navigator (preferred) with StableNavigation as legacy fallback
- **Dependency Injection**: Koin (configured in `core/di/AppModule.kt`)
- **Database**: Room KMP with KSP compilation for cross-platform support
- **Storage**: Three-tier system (SecureStorage/KVault → Room → DataStore)
- **Networking**: Ktor HttpClient with kotlinx.serialization
- **Validation**: Konform for declarative input validation
- **Testing**: Kotest with Turbine for Flow testing

### Important Architecture Details
- **Navigation Migration**: Project uses Voyager as primary navigation (controlled by `NavigationFeatureFlag.USE_VOYAGER`)
- **Smart Caching**: Multi-level authentication cache with graceful fallback
- **Bilingual Support**: Complete localization system with dynamic language switching
- **Dependencies**: Managed via Gradle Version Catalog (`gradle/libs.versions.toml`)

Key technical details:
- Kotlin version: 2.2.10
- Compose Multiplatform version: 1.8.2
- Android compile SDK: 36
- Android min SDK: 24
- JVM target: 11

## Development Workflow

### Key Files for Understanding the Codebase
- `core/di/AppModule.kt` - Dependency injection configuration
- `core/navigation/VoyagerScreens.kt` - Navigation setup and screen definitions
- `gradle/libs.versions.toml` - Complete dependency catalog
- `PROJECT_SUMMARY.md` - Detailed migration history and architecture overview

### Testing
```shell
# Run specific test class (Kotest)
./gradlew test --tests "*NavigationTest*"

# Run all common tests
./gradlew :composeApp:commonMainTest

# Run platform-specific tests
./gradlew :composeApp:androidUnitTest
./gradlew :composeApp:iosSimulatorArm64Test
```

### Working with Features
When adding new features:
1. Follow the feature-based structure: `features/[feature-name]/data|domain|presentation/`
2. Register ViewModels in `AppModule.kt` using appropriate scope (factory vs singleton)
3. Add screen definitions to `VoyagerScreens.kt` for navigation
4. Use Konform validators for input validation (see `AuthValidators.kt`)
5. Follow the three-tier storage pattern for data persistence

### Navigation System
- **Current**: Voyager Navigator is the primary navigation system
- **Legacy**: StableNavigation exists as deprecated fallback
- **Control**: Navigation system controlled by `NavigationFeatureFlag.USE_VOYAGER`
- **Screens**: All screens defined in `VoyagerScreens.kt` with fade transitions