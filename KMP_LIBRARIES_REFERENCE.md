# Référence Complète des Bibliothèques Kotlin Multiplatform

Ce document compile toutes les bibliothèques KMP disponibles analysées depuis les sources officielles et communautaires les plus importantes.

## Sources Analysées

1. **JetBrains Documentation Officielle** - Documentation officielle des bibliothèques KMP
2. **Android Jetpack KMP** - Bibliothèques Jetpack avec support KMP officiel
3. **libs.kmp.icerock.dev** - Catalogue communautaire dynamique
4. **AAkira/Kotlin-Multiplatform-Libraries** - Liste organisée manuellement sur GitHub
5. **terrakok/kmp-awesome** - Grande collection de ressources diversifiées

## Bibliothèques Jetpack Android avec Support KMP Officiel

### Base de Données et Stockage
- **Room Database** - ORM avec support SQLite multiplateforme
  - Versions: Stable 2.7.2, RC 2.8.0-rc02
  - Plateformes: Android, JVM, iOS
  - Usage: Base de données relationnelle avec API type-safe

- **DataStore** - Stockage de données persistant clé-valeur
  - Version: 1.1.7 (Stable), 1.2.0-alpha02
  - Plateformes: Android, JVM, iOS
  - Usage: Remplacement moderne de SharedPreferences

- **SQLite** - API SQLite de bas niveau
  - Versions: Stable 2.5.2, RC 2.6.0-rc02
  - Plateformes: Android, JVM, iOS
  - Usage: Accès direct à SQLite multiplateforme

### Architecture et Navigation
- **Lifecycle** - Gestion du cycle de vie des composants
  - Versions: Stable 2.9.3, Alpha 2.10.0-alpha03
  - Plateformes: Android, JVM, iOS
  - Usage: Gestion du cycle de vie cross-platform

- **SavedState** - Préservation d'état lors de changements de configuration
  - Versions: Stable 1.3.2, Alpha 1.4.0-alpha03
  - Plateformes: Android, JVM, iOS
  - Usage: État persistant entre reconfigurations

### UI et Collections
- **Paging** - Chargement paginé de données
  - Versions: Stable 3.3.6, Alpha 3.4.0-alpha03
  - Plateformes: Android, JVM, iOS
  - Usage: Gestion de listes avec pagination

- **Collection** - Extensions de collections Kotlin
  - Versions: Stable 1.5.0, Alpha 1.6.0-alpha01
  - Plateformes: Android, JVM, iOS
  - Usage: Collections optimisées multiplateforme

- **Annotation** - Support d'annotations
  - Version: 1.9.1
  - Plateformes: Android, JVM, iOS
  - Usage: Annotations communes multiplateforme

## Bibliothèques Réseau

### Clients HTTP
- **Ktor** - Framework pour applications connectées
  - Plateformes: Android, iOS, JS, JVM, Linux, Windows, macOS
  - Usage: Client/serveur HTTP avec coroutines
  - Repository: https://github.com/ktorio/ktor

- **Ktorfit** - Client HTTP type-safe utilisant KSP
  - Plateformes: Android, iOS, JS, JVM, Linux, Windows, macOS
  - Usage: Interface REST type-safe style Retrofit
  - Repository: https://github.com/Foso/Ktorfit

- **Apollo GraphQL** - Client GraphQL multiplateforme
  - Plateformes: Android, iOS, JS, JVM
  - Usage: Requêtes GraphQL avec génération de code
  - Repository: https://github.com/apollographql/apollo-kotlin

### Protocoles Spécialisés
- **RSocket Kotlin** - Implémentation RSocket
  - Plateformes: Android, iOS, JS, JVM, Linux, Windows, macOS, watchOS, tvOS
  - Usage: Protocole binaire bidirectionnel
  - Repository: https://github.com/rsocket/rsocket-kotlin

- **WebRTC KMP** - Client WebRTC
  - Plateformes: Android, iOS
  - Usage: Communication temps réel P2P
  - Repository: https://github.com/shepeliev/webrtc-kmp

- **Krossbow** - Client WebSocket STOMP
  - Plateformes: Android, iOS, JS, JVM
  - Usage: Messaging WebSocket avec protocol STOMP
  - Repository: https://github.com/joffrey-bion/krossbow

## Base de Données et Stockage

### Bases de Données SQL
- **SQLDelight** - Génère APIs Kotlin type-safe depuis SQL
  - Plateformes: Android, iOS, JS, JVM, Linux, macOS, tvOS, watchOS, Windows
  - Usage: ORM avec requêtes SQL pure et génération de code
  - Repository: https://github.com/square/sqldelight

### Bases de Données NoSQL
- **Realm Kotlin** - Base de données objet multiplateforme
  - Plateformes: Android, iOS
  - Usage: Base de données objet avec sync cloud optionnel
  - Repository: https://github.com/realm/realm-kotlin

- **Kotbase** - Port de Couchbase Lite
  - Plateformes: Android, iOS, JVM, Linux, macOS, Windows
  - Usage: Base de données document avec sync
  - Repository: https://github.com/jeffdgr8/kotbase

### Stockage Clé-Valeur
- **Multiplatform-Settings** - Preferences multiplateforme
  - Plateformes: Android, iOS, JS, JVM, macOS, tvOS, watchOS
  - Usage: Stockage clé-valeur style SharedPreferences
  - Repository: https://github.com/russhwolf/multiplatform-settings

- **KVault** - Stockage sécurisé multiplateforme
  - Plateformes: Android, iOS
  - Usage: Stockage chiffré pour données sensibles
  - Repository: https://github.com/Liftric/KVault

- **KStore** - Stockage d'objets basé fichiers
  - Plateformes: Android, iOS, JVM, JS, Linux, macOS, tvOS, watchOS, Windows
  - Usage: Sérialisation/désérialisation automatique d'objets
  - Repository: https://github.com/xxfast/KStore

## Injection de Dépendances

### Frameworks DI
- **Koin** - Framework DI pragmatique et léger
  - Plateformes: Android, iOS, JS, JVM, Linux, Windows, macOS, watchOS, tvOS
  - Usage: DI déclaratif sans génération de code
  - Repository: https://github.com/InsertKoinIO/koin

- **Kodein-DI** - Framework DI Kotlin
  - Plateformes: Android, iOS, JS, JVM, Linux, macOS, tvOS, WASM, watchOS, Windows
  - Usage: DI avec DSL Kotlin flexible
  - Repository: https://github.com/Kodein-Framework/Kodein-DI

## Architecture et Navigation

### Architecture Components
- **Decompose** - Composants lifecycle-aware
  - Plateformes: Android, iOS, JS, JVM, WASM
  - Usage: Architecture basée composants avec lifecycle
  - Repository: https://github.com/arkivanov/Decompose

- **MVIKotlin** - Framework MVI multiplateforme
  - Plateformes: Android, iOS, JS, JVM, WASM
  - Usage: Architecture MVI avec gestion d'état
  - Repository: https://github.com/arkivanov/MVIKotlin

- **Voyager** - Navigation multiplateforme
  - Plateformes: Android, iOS, Desktop, Web
  - Usage: Navigation type-safe pour Compose Multiplatform
  - Repository: https://github.com/adrielcafe/voyager

### State Management
- **Redux-Kotlin** - Implémentation Redux
  - Plateformes: Android, iOS, JS, JVM, Linux, macOS, Windows
  - Usage: Gestion d'état prévisible
  - Repository: https://github.com/reduxkotlin/redux-kotlin

- **Store** - Bibliothèque pour applications résistantes au réseau
  - Plateformes: Android, iOS, JS, JVM
  - Usage: Cache et synchronisation de données
  - Repository: https://github.com/MobileNativeFoundation/Store

## Sérialisation

### Formats JSON/Binary
- **kotlinx.serialization** - Sérialisation multi-format officielle
  - Plateformes: Android, iOS, JS, JVM, Linux, macOS, tvOS, watchOS, Windows, WASM
  - Usage: Sérialisation JSON, ProtoBuf, CBOR, etc.
  - Repository: https://github.com/Kotlin/kotlinx.serialization

- **kotlinx-serialization-msgpack** - Support MessagePack
  - Plateformes: Toutes supportées par kotlinx.serialization
  - Usage: Format binaire MessagePack
  - Repository: https://github.com/esensar/kotlinx-serialization-msgpack

### Formats Spécialisés
- **YAKL** - Processeur YAML 1.2
  - Plateformes: macOS, Linux
  - Usage: Parsing/génération YAML
  - Repository: https://github.com/charleskorn/kaml

## UI et Compose

### Compose Multiplatform
- **Compose Multiplatform** - UI déclarative multiplateforme (officiel JetBrains)
  - Plateformes: Android, iOS, Desktop, Web
  - Usage: UI moderne basée sur Compose

### Composants UI
- **Compose-Icons** - Collection d'icônes pour Compose
  - Plateformes: Android, iOS, Desktop, Web
  - Usage: Icônes vectorielles dans Compose
  - Repository: https://github.com/DevSrSouza/compose-icons

- **Compose-Shimmer** - Effet shimmer pour loading
  - Plateformes: Android, iOS, Desktop, Web
  - Usage: Animations de loading
  - Repository: https://github.com/valentinilk/compose-shimmer

## Capacités Dispositif

### Permissions et Sécurité
- **MOKO Permissions** - Gestionnaire de permissions système
  - Plateformes: Android, iOS
  - Usage: API unifiée pour permissions
  - Repository: https://github.com/icerockdev/moko-permissions

- **MOKO Biometry** - Authentification biométrique
  - Plateformes: Android, iOS
  - Usage: TouchID, FaceID, empreintes digitales
  - Repository: https://github.com/icerockdev/moko-biometry

### Média et Notifications
- **MOKO Media** - Gestion médias système
  - Plateformes: Android, iOS
  - Usage: Caméra, galerie, enregistrement audio
  - Repository: https://github.com/icerockdev/moko-media

- **Alert-KMP** - Notifications locales/natives
  - Plateformes: Android, iOS
  - Usage: Alertes et notifications système
  - Repository: https://github.com/firstmarek/Alert-KMP

### Localisation et Capteurs
- **MOKO Geo** - Services de géolocalisation
  - Plateformes: Android, iOS
  - Usage: GPS et services de location
  - Repository: https://github.com/icerockdev/moko-geo

## Logging et Debugging

### Bibliothèques de Logging
- **Napier** - Logger multiplateforme
  - Plateformes: Android, iOS, JS, JVM, Linux, macOS, tvOS, watchOS, Windows
  - Usage: Logging unifié cross-platform
  - Repository: https://github.com/AAkira/Napier

- **Kermit** - Logger multiplateforme par Touchlab
  - Plateformes: Android, iOS, JS, JVM, Linux, macOS, tvOS, WASM, watchOS, Windows
  - Usage: Logger simple avec tags
  - Repository: https://github.com/touchlab/Kermit

- **KmLogging** - Façade de logging
  - Plateformes: Android, iOS, JS, JVM, Linux, macOS, Windows
  - Usage: Interface commune pour différents loggers
  - Repository: https://github.com/LighthouseGames/KmLogging

## Date et Temps

- **kotlinx-datetime** - API date/temps multiplateforme officielle
  - Plateformes: Android, iOS, JS, JVM, Linux, macOS, tvOS, watchOS, Windows, WASM
  - Usage: Manipulation de dates cross-platform
  - Repository: https://github.com/Kotlin/kotlinx-datetime

- **Island Time** - API temps type-safe
  - Plateformes: Android, iOS, JVM, Linux, macOS, Windows
  - Usage: API temps immutable et type-safe
  - Repository: https://github.com/erikc5000/island-time

## Coroutines et Asynchrone

### Extensions Coroutines
- **kotlinx.coroutines** - Programmation asynchrone officielle
  - Plateformes: Toutes les plateformes KMP
  - Usage: Concurrence et programmation asynchrone
  - Repository: https://github.com/Kotlin/kotlinx.coroutines

- **FlowExt** - Extensions pour Kotlin Flow
  - Plateformes: Android, iOS, JS, JVM, Linux, macOS, Windows
  - Usage: Opérateurs additionnels pour Flow
  - Repository: https://github.com/hoc081098/FlowExt

## Tests

### Frameworks de Test
- **Kotest** - Framework de test multiplateforme
  - Plateformes: Android, iOS, JS, JVM, Linux, macOS, tvOS, watchOS, Windows
  - Usage: Tests avec DSL expressif
  - Repository: https://github.com/kotest/kotest

- **Turbine** - Test de Flow Kotlin
  - Plateformes: Android, iOS, JVM, Linux, macOS, Windows
  - Usage: Tests d'API Flow
  - Repository: https://github.com/cashapp/turbine

### Mock et Stub
- **MockK** - Framework de mock multiplateforme
  - Plateformes: Android, JVM
  - Usage: Création de mocks pour tests
  - Repository: https://github.com/mockk/mockk

## Cryptographie et Sécurité

- **KotlinCrypto** - Primitives cryptographiques
  - Plateformes: Android, iOS, JS, JVM, Linux, macOS, tvOS, watchOS, Windows
  - Usage: Chiffrement, hash, signatures
  - Repository: https://github.com/KotlinCrypto

## Utilitaires et Extensions

### UUID et Identifiants
- **UUID** - Génération d'UUID multiplateforme
  - Plateformes: Android, iOS, JS, JVM, Linux, macOS, tvOS, watchOS, Windows
  - Usage: Génération d'identifiants uniques
  - Repository: https://github.com/benasher44/uuid

### Validation
- **Kvalidation** - Validation de données
  - Plateformes: Android, iOS, JVM
  - Usage: Règles de validation fluides
  - Repository: https://github.com/konform-kt/konform

## Resources et Internationalisation

- **MOKO Resources** - Gestion de ressources multiplateforme
  - Plateformes: Android, iOS
  - Usage: Strings, images, couleurs cross-platform
  - Repository: https://github.com/icerockdev/moko-resources

## Outils de Build et Développement

### Configuration Projet
- **Amper** - Configuration projet simple (JetBrains)
  - Usage: Alternative à Gradle pour projets KMP
  - Status: Expérimental

### Documentation
- **Dokka** - Générateur de documentation
  - Usage: Documentation API à partir du code source
  - Repository: https://github.com/Kotlin/dokka

### Diagnostic
- **KDoctor** - Outil de diagnostic environnement KMP
  - Usage: Vérification setup développement KMP
  - Repository: https://github.com/Kotlin/kdoctor

## Support Plateformes

### Niveaux de Support (Jetpack)
- **Tier 1** (Entièrement testé): Android, JVM, iOS
- **Tier 2** (Partiellement testé): macOS, Linux  
- **Tier 3** (Non testé): watchOS, tvOS, Windows, JavaScript, WASM

### Plateformes Communes
La plupart des bibliothèques supportent:
- Android
- iOS  
- Desktop (JVM)
- Web (JS/WASM)
- Serveur (JVM/Native)

## Recommandations par Cas d'Usage

### Application Mobile Cross-Platform Basique
- **UI**: Compose Multiplatform
- **Navigation**: Voyager
- **Injection**: Koin
- **Réseau**: Ktor + kotlinx.serialization
- **Stockage**: Multiplatform-Settings
- **Logging**: Napier

### Application avec Base de Données
- **Base de données**: SQLDelight ou Room (Jetpack)
- **Cache**: Store
- **Migration**: Multiplatform-Settings → Room/SQLDelight

### Application Enterprise/Complex
- **Architecture**: Decompose + MVIKotlin
- **Réseau**: Apollo GraphQL ou Ktor
- **État**: Redux-Kotlin
- **Tests**: Kotest + Turbine
- **Sécurité**: KotlinCrypto + KVault

### Application avec Fonctions Natives
- **Permissions**: MOKO Permissions
- **Média**: MOKO Media  
- **Géolocalisation**: MOKO Geo
- **Biométrie**: MOKO Biometry
- **Resources**: MOKO Resources

## Notes de Migration

### De Android vers KMP
1. Remplacer SharedPreferences → Multiplatform-Settings ou DataStore
2. Remplacer Room Android → Room KMP ou SQLDelight
3. Remplacer Retrofit → Ktor + Ktorfit
4. Remplacer Gson/Moshi → kotlinx.serialization
5. Ajouter gestion permissions cross-platform avec MOKO

### Versions et Compatibilité
- Kotlin: Minimum 1.9.0 pour la plupart des bibliothèques récentes
- Compose Multiplatform: Version 1.8.2 recommandée
- Gradle: 8.0+ recommandé
- Android SDK: Minimum 24 pour la plupart des bibliothèques

## Écosystème en Croissance

L'écosystème KMP compte maintenant **plus de 2500 bibliothèques** avec une croissance rapide. Les domaines les plus actifs sont:
- Architecture et navigation
- Networking et API
- Base de données et stockage  
- UI et Compose Multiplatform
- Capacités natives des appareils

La compatibilité rétrograde devient cruciale avec cette expansion, nécessitant une collaboration étroite entre JetBrains et les créateurs de bibliothèques.