# Plan de Migration Étape par Étape - KMP Libraries

## 🎯 **Objectif**: Code fonctionnel après chaque étape

Ce plan garantit que l'application reste **100% fonctionnelle** après chaque modification, permettant de tester et valider chaque amélioration individuellement.

---

## **ÉTAPE 1: Ajouter Framework de Tests (2-3 jours)**
*Priorité: CRITIQUE - Base pour valider toutes les étapes suivantes*

### **1.1 - Ajouter Kotest (30 min)**
```kotlin
// gradle/libs.versions.toml
kotest = "5.9.1"
turbine = "1.1.0"

[libraries]
kotest-framework = { module = "io.kotest:kotest-framework-engine", version.ref = "kotest" }
kotest-assertions = { module = "io.kotest:kotest-assertions-core", version.ref = "kotest" }
turbine = { module = "app.cash.turbine:turbine", version.ref = "turbine" }
```

```kotlin
// composeApp/build.gradle.kts
commonTest.dependencies {
    implementation(libs.kotest.framework)
    implementation(libs.kotest.assertions)
    implementation(libs.turbine)
}
```

### **1.2 - Créer Tests de Base (2h)**
```kotlin
// composeApp/src/commonTest/kotlin/AuthViewModelTest.kt
class AuthViewModelTest : FunSpec({
    test("should initialize with default state") {
        val viewModel = AuthViewModel(mockLoginUseCase, mockRegisterUseCase, mockForgotPasswordUseCase, mockGetAuthTokenUseCase)
        
        viewModel.uiState.value shouldBe AuthUiState()
    }
    
    test("should emit loading state when login starts") {
        val viewModel = AuthViewModel(...)
        
        viewModel.uiState.test {
            viewModel.onEmailChanged("test@example.com")
            viewModel.onPasswordChanged("password123")
            viewModel.login()
            
            awaitItem().isLoading shouldBe true
        }
    }
})
```

### **1.3 - Valider les Tests Existants (1h)**
```bash
./gradlew allTests
```

✅ **Validation**: Tous les tests passent, application fonctionne normalement.

---

## **ÉTAPE 2: Ajouter Validation avec Konform (1-2 jours)**
*Impact: Remplace validation manuelle sans changer l'API*

### **2.1 - Ajouter Konform (15 min)**
```kotlin
// gradle/libs.versions.toml
konform = "0.4.0"

[libraries]
konform = { module = "io.konform:konform", version.ref = "konform" }
```

```kotlin
// composeApp/build.gradle.kts
commonMain.dependencies {
    implementation(libs.konform)
}
```

### **2.2 - Créer Validators (1h)**
```kotlin
// composeApp/src/commonMain/kotlin/core/validation/AuthValidators.kt
package org.jelarose.monalerte.core.validation

import io.konform.validation.Validation
import io.konform.validation.jsonschema.*

data class LoginData(val email: String, val password: String)
data class RegisterData(val email: String, val password: String, val confirmPassword: String, val firstName: String, val lastName: String, val phoneNumber: String)

val loginValidation = Validation<LoginData> {
    LoginData::email {
        pattern("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") hint "Format d'email invalide"
    }
    LoginData::password {
        minLength(1) hint "Le mot de passe est requis"
    }
}

val registerValidation = Validation<RegisterData> {
    RegisterData::email {
        pattern("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") hint "Format d'email invalide"
    }
    RegisterData::password {
        minLength(8) hint "Le mot de passe doit contenir au moins 8 caractères"
    }
    RegisterData::confirmPassword required {
        // Validation que confirmPassword = password sera faite manuellement
    }
    RegisterData::firstName {
        minLength(1) hint "Le prénom est requis"
    }
    RegisterData::lastName {
        minLength(1) hint "Le nom est requis"
    }
    RegisterData::phoneNumber {
        minLength(1) hint "Le numéro de téléphone est requis"
    }
}
```

### **2.3 - Intégrer dans AuthViewModel (2h)**
```kotlin
// features/auth/presentation/viewmodels/AuthViewModel.kt
import org.jelarose.monalerte.core.validation.*

class AuthViewModel(/* ... */) : ViewModel() {
    
    // Garder les méthodes existantes, remplacer seulement l'implémentation
    private fun validateLoginInputs(): Boolean {
        val loginData = LoginData(_uiState.value.email, _uiState.value.password)
        val result = loginValidation(loginData)
        
        return if (result.errors.isEmpty()) {
            true
        } else {
            val firstError = result.errors.first()
            _uiState.value = _uiState.value.copy(
                errorMessage = firstError.message
            )
            false
        }
    }
    
    private fun validateRegisterInputs(): Boolean {
        val state = _uiState.value
        val registerData = RegisterData(
            state.email, state.password, state.confirmPassword,
            state.firstName, state.lastName, state.phoneNumber
        )
        
        val result = registerValidation(registerData)
        
        // Validation custom pour confirmPassword
        if (state.password != state.confirmPassword) {
            _uiState.value = _uiState.value.copy(errorMessage = "Les mots de passe ne correspondent pas")
            return false
        }
        
        return if (result.errors.isEmpty()) {
            true
        } else {
            val firstError = result.errors.first()
            _uiState.value = _uiState.value.copy(
                errorMessage = firstError.message
            )
            false
        }
    }
    
    // Garder isValidEmail pour compatibilité
    private fun isValidEmail(email: String): Boolean {
        val loginData = LoginData(email, "dummy")
        return loginValidation(loginData).errors.none { it.dataPath == ".email" }
    }
}
```

### **2.4 - Tests de Validation (1h)**
```kotlin
// composeApp/src/commonTest/kotlin/ValidationTest.kt
class ValidationTest : FunSpec({
    test("should validate correct email") {
        val result = loginValidation(LoginData("test@example.com", "password"))
        result.errors.shouldBeEmpty()
    }
    
    test("should reject invalid email") {
        val result = loginValidation(LoginData("invalid-email", "password"))
        result.errors.shouldNotBeEmpty()
    }
})
```

✅ **Validation**: Tests passent, validation fonctionne mieux avec messages plus précis.

---

## **ÉTAPE 3: Améliorer Stockage Sécurisé avec KVault (1-2 jours)**
*Impact: Améliore sécurité sans changer l'API du repository*

### **3.1 - Ajouter KVault (15 min)**
```kotlin
// gradle/libs.versions.toml
kvault = "1.12.0"

[libraries]
kvault = { module = "com.liftric:kvault", version.ref = "kvault" }
```

### **3.2 - Créer SecureStorage (1h)**
```kotlin
// composeApp/src/commonMain/kotlin/core/storage/SecureStorage.kt
expect class SecureStorage() {
    suspend fun store(key: String, value: String)
    suspend fun retrieve(key: String): String?
    suspend fun remove(key: String)
    suspend fun clear()
}
```

```kotlin
// composeApp/src/androidMain/kotlin/core/storage/SecureStorage.android.kt
import com.liftric.kvault.KVault

actual class SecureStorage {
    private val kVault = KVault()
    
    actual suspend fun store(key: String, value: String) {
        kVault.set(key, value)
    }
    
    actual suspend fun retrieve(key: String): String? {
        return kVault.string(key)
    }
    
    actual suspend fun remove(key: String) {
        kVault.deleteObject(key)
    }
    
    actual suspend fun clear() {
        kVault.clear()
    }
}
```

```kotlin
// composeApp/src/iosMain/kotlin/core/storage/SecureStorage.ios.kt
import com.liftric.kvault.KVault

actual class SecureStorage {
    private val kVault = KVault()
    
    actual suspend fun store(key: String, value: String) {
        kVault.set(key, value)
    }
    
    actual suspend fun retrieve(key: String): String? {
        return kVault.string(key)
    }
    
    actual suspend fun remove(key: String) {
        kVault.deleteObject(key)
    }
    
    actual suspend fun clear() {
        kVault.clear()
    }
}
```

### **3.3 - Intégrer dans AppModule (30 min)**
```kotlin
// core/di/AppModule.kt
val appModule = module {
    // Existant...
    
    // Nouveau
    single { SecureStorage() }
}
```

### **3.4 - Modifier AuthRepositoryImpl (1h)**
```kotlin
// features/auth/data/repository/AuthRepositoryImpl.kt
class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val authDao: AuthDao,
    private val sharedDataStore: SharedDataStore,
    private val secureStorage: SecureStorage // Nouveau paramètre
) : AuthRepository {
    
    // Garder toutes les méthodes existantes, ajouter le stockage sécurisé en parallèle
    override suspend fun saveAuthToken(token: String, userEmail: String) {
        // Conserver l'existant
        val authEntity = AuthTokenEntity(
            jwtToken = token,
            userEmail = userEmail,
            lastUpdated = Clock.System.now().toEpochMilliseconds()
        )
        authDao.insertAuthToken(authEntity)
        
        // Ajouter stockage sécurisé
        secureStorage.store(KEY_JWT_TOKEN, token)
        secureStorage.store(KEY_USER_EMAIL, userEmail)
    }
    
    override suspend fun getAuthToken(): String? {
        // Priorité: Stockage sécurisé > Room > DataStore
        return secureStorage.retrieve(KEY_JWT_TOKEN)?.takeIf { it.isNotEmpty() }
            ?: authDao.getAuthToken()?.jwtToken?.takeIf { it.isNotEmpty() }
            ?: sharedDataStore.getString(KEY_JWT_TOKEN)?.takeIf { it.isNotEmpty() }
    }
    
    override suspend fun clearAuthToken() {
        // Nettoyer tous les stockages
        secureStorage.remove(KEY_JWT_TOKEN)
        secureStorage.remove(KEY_USER_EMAIL)
        authDao.clearAuthToken()
        sharedDataStore.removeKey(KEY_JWT_TOKEN)
        sharedDataStore.removeKey(KEY_USER_EMAIL)
        sharedDataStore.removeKey(KEY_USER_ID)
    }
}
```

### **3.5 - Mettre à jour l'injection (30 min)**
```kotlin
// core/di/AppModule.kt
single<AuthRepository> { 
    AuthRepositoryImpl(
        get<AuthApiService>(), 
        get<AppDatabase>().authDao(), 
        get<SharedDataStore>(),
        get<SecureStorage>() // Nouveau
    ) 
}

single<AuthRepositoryImpl> { 
    AuthRepositoryImpl(
        get<AuthApiService>(), 
        get<AppDatabase>().authDao(), 
        get<SharedDataStore>(),
        get<SecureStorage>() // Nouveau
    ) 
}
```

✅ **Validation**: Application fonctionne identiquement, mais tokens sont maintenant stockés de façon sécurisée.

---

## **ÉTAPE 4: Ajouter Navigation Voyager (2-3 jours)**
*Impact: Remplace progressivement la navigation manuelle*

### **4.1 - Ajouter Voyager (15 min)**
```kotlin
// gradle/libs.versions.toml
voyager = "1.1.0"

[libraries]
voyager-navigator = { module = "cafe.adriel.voyager:voyager-navigator", version.ref = "voyager" }
voyager-screenmodel = { module = "cafe.adriel.voyager:voyager-screenmodel", version.ref = "voyager" }
voyager-transitions = { module = "cafe.adriel.voyager:voyager-transitions", version.ref = "voyager" }
```

### **4.2 - Créer Screens Voyager (2h)**
```kotlin
// composeApp/src/commonMain/kotlin/core/navigation/VoyagerScreens.kt
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

// Créer les écrans Voyager en parallèle des existants
object PrivacyPolicyScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        // Utiliser le composable existant
        SharedPrivacyPolicyScreen(
            onAccept = { navigator.replace(LoginScreen) },
            onDecline = { /* handle decline */ }
        )
    }
}

object LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        SharedLoginScreen(
            onLoginSuccess = { navigator.replace(HomeScreen) },
            onNavigateToRegister = { navigator.push(RegisterScreen) },
            onNavigateToForgotPassword = { navigator.push(ForgotPasswordScreen) }
        )
    }
}

object RegisterScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        SharedRegisterScreen(
            onRegisterSuccess = { navigator.replace(HomeScreen) },
            onNavigateToLogin = { navigator.pop() }
        )
    }
}

object ForgotPasswordScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        SharedForgotPasswordScreen(
            onBackToLogin = { navigator.pop() }
        )
    }
}

object HomeScreen : Screen {
    @Composable
    override fun Content() {
        // Composable temporaire ou réutilisation existante
        TestScreen()
    }
}
```

### **4.3 - Adapter les Composables Existants (2h)**
```kotlin
// features/auth/presentation/screens/SharedLoginScreen.kt
@Composable
fun SharedLoginScreen(
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {},
    viewModel: SharedAuthViewModel = getKoin().get()
) {
    // Garder tout le code existant, remplacer seulement les callbacks de navigation
    
    // Au lieu de navController.navigate(Screen.Register)
    Button(onClick = onNavigateToRegister) {
        Text("S'inscrire")
    }
    
    // Au lieu de navController.navigate(Screen.ForgotPassword)  
    Button(onClick = onNavigateToForgotPassword) {
        Text("Mot de passe oublié")
    }
    
    // Gérer le succès de login
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onLoginSuccess()
        }
    }
}
```

### **4.4 - Créer Navigation Hybride (1h)**
```kotlin
// App.kt - Navigation hybride pendant la transition
@Composable
fun App() {
    val useVoyager = false // Feature flag pour basculer progressivement
    
    MonAlerteTheme {
        if (useVoyager) {
            Navigator(PrivacyPolicyScreen)
        } else {
            // Navigation existante
            val navController = rememberStableNavController()
            AppNavHost(navController)
        }
    }
}
```

### **4.5 - Tests de Navigation (1h)**
```kotlin
// Test que les deux systèmes fonctionnent
class NavigationTest : FunSpec({
    test("should navigate with stable controller") {
        val navController = StableNavController()
        navController.navigate(Screen.Login)
        navController.currentScreen shouldBe Screen.Login
    }
    
    test("voyager screens should render without errors") {
        // Test que les nouveaux écrans se rendent correctement
    }
})
```

✅ **Validation**: Les deux systèmes de navigation coexistent, possibilité de basculer avec feature flag.

---

## **ÉTAPE 5: Migrer vers Cache Intelligent avec Store 5 (2-3 jours)**
*Impact: Améliore performance et gestion des données*

### **5.1 - Ajouter Store 5 (15 min)**
```kotlin
// gradle/libs.versions.toml
store = "5.1.0"

[libraries]
store = { module = "org.mobilenativefoundation.store:store5", version.ref = "store" }
```

### **5.2 - Créer AuthTokenStore (2h)**
```kotlin
// composeApp/src/commonMain/kotlin/features/auth/data/store/AuthTokenStore.kt
import org.mobilenativefoundation.store.store5.*

sealed class AuthStoreKey {
    object AuthToken : AuthStoreKey()
}

class AuthTokenStoreFactory(
    private val authApiService: AuthApiService,
    private val authDao: AuthDao,
    private val secureStorage: SecureStorage
) {
    
    fun createAuthTokenStore(): Store<AuthStoreKey, String?> {
        return StoreBuilder
            .from(
                fetcher = Fetcher.of { key: AuthStoreKey ->
                    when (key) {
                        AuthStoreKey.AuthToken -> {
                            // Pas de fetching réseau pour les tokens (ils sont fournis par login)
                            throw IllegalStateException("Auth token must be provided via login")
                        }
                    }
                },
                sourceOfTruth = SourceOfTruth.of(
                    reader = { key: AuthStoreKey ->
                        when (key) {
                            AuthStoreKey.AuthToken -> {
                                // Lire depuis les sources existantes
                                kotlinx.coroutines.flow.flow {
                                    val token = secureStorage.retrieve("jwt_token")
                                        ?: authDao.getAuthToken()?.jwtToken
                                    emit(token)
                                }
                            }
                        }
                    },
                    writer = { key: AuthStoreKey, value: String? ->
                        when (key) {
                            AuthStoreKey.AuthToken -> {
                                if (value != null) {
                                    secureStorage.store("jwt_token", value)
                                    // Optionnel: sauvegarder aussi en DB pour compatibilité
                                } else {
                                    secureStorage.remove("jwt_token")
                                    authDao.clearAuthToken()
                                }
                            }
                        }
                    }
                )
            )
            .build()
    }
}
```

### **5.3 - Intégrer dans AppModule (30 min)**
```kotlin
// core/di/AppModule.kt
val appModule = module {
    // ...existant
    
    // Store
    single { AuthTokenStoreFactory(get(), get<AppDatabase>().authDao(), get()) }
    single { get<AuthTokenStoreFactory>().createAuthTokenStore() }
}
```

### **5.4 - Adapter AuthRepositoryImpl (1h)**
```kotlin
// features/auth/data/repository/AuthRepositoryImpl.kt
class AuthRepositoryImpl(
    // ...paramètres existants
    private val authTokenStore: Store<AuthStoreKey, String?> // Nouveau
) : AuthRepository {
    
    // Garder toutes les méthodes existantes pour compatibilité
    // Ajouter les nouvelles méthodes utilisant Store
    
    override suspend fun getAuthToken(): String? {
        return try {
            authTokenStore.get(AuthStoreKey.AuthToken)
        } catch (e: Exception) {
            // Fallback vers l'ancienne méthode
            secureStorage.retrieve(KEY_JWT_TOKEN)?.takeIf { it.isNotEmpty() }
                ?: authDao.getAuthToken()?.jwtToken?.takeIf { it.isNotEmpty() }
        }
    }
    
    override suspend fun saveAuthToken(token: String, userEmail: String) {
        // Utiliser Store pour le token
        authTokenStore.write(StoreWriteRequest.of(AuthStoreKey.AuthToken, token))
        
        // Garder l'ancienne méthode pour l'email et autres données
        secureStorage.store(KEY_USER_EMAIL, userEmail)
        val authEntity = AuthTokenEntity(
            jwtToken = token,
            userEmail = userEmail,
            lastUpdated = Clock.System.now().toEpochMilliseconds()
        )
        authDao.insertAuthToken(authEntity)
    }
    
    override fun getAuthTokenFlow(): Flow<AuthTokenEntity?> {
        // Continuer à utiliser l'ancienne méthode pour le Flow
        return authDao.getAuthTokenFlow()
    }
}
```

✅ **Validation**: Cache intelligent fonctionne avec fallback vers ancienne méthode si problème.

---

## **ÉTAPE 6: Finaliser Migration Navigation (1 jour)**
*Impact: Basculer complètement vers Voyager*

### **6.1 - Activer Voyager par Défaut (30 min)**
```kotlin
// App.kt
@Composable
fun App() {
    MonAlerteTheme {
        Navigator(PrivacyPolicyScreen) {
            FadeTransition(navigator = it)
        }
    }
}
```

### **6.2 - Supprimer Ancien Code Navigation (1h)**
```kotlin
// Supprimer StableNavigation.kt
// Nettoyer les AppNavHost
// Nettoyer les imports non utilisés
```

### **6.3 - Mettre à jour Tests (30 min)**

✅ **Validation**: Navigation complètement migrée vers Voyager avec transitions fluides.

---

## **ÉTAPE 7: Error Handling avec Arrow (Optionnel - 2-3 jours)**
*Impact: Gestion d'erreurs plus robuste*

### **7.1 - Ajouter Arrow (15 min)**
### **7.2 - Créer Error Types (1h)**
### **7.3 - Migrer Repository (2h)**
### **7.4 - Adapter ViewModels (2h)**

---

## **ÉTAPE 8: Resources avec MOKO (Optionnel - 2-3 jours)**
*Impact: Resources type-safe*

---

## **Commandes de Validation à Chaque Étape**

```bash
# Tests
./gradlew allTests

# Build Android
./gradlew :composeApp:assembleDebug

# Build iOS
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

# Lint
./gradlew lint

# Installer et tester
./gradlew installDebug
```

---

## **Rollback Plan**

À chaque étape, possibilité de rollback en:
1. **Commentant le nouveau code**
2. **Remettant l'ancien code actif**
3. **Retirant les nouvelles dépendances**

Exemple:
```kotlin
// Rollback navigation
@Composable
fun App() {
    val useVoyager = false // <- Passer à false pour rollback
    
    MonAlerteTheme {
        if (useVoyager) {
            Navigator(PrivacyPolicyScreen)
        } else {
            val navController = rememberStableNavController()
            AppNavHost(navController) // <- Code original
        }
    }
}
```

## **Durée Totale Estimée**

- **Étapes 1-3 (critiques)**: 5-7 jours
- **Étapes 4-6 (importantes)**: 5-7 jours  
- **Étapes 7-8 (optionnelles)**: 4-6 jours

**Total**: 10-14 jours pour migration complète avec validation à chaque étape.