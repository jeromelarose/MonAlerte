package org.jelarose.monalerte.core.navigation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests pour valider la migration complète vers Voyager
 * 
 * MISE À JOUR: Tests mis à jour pour refléter la migration terminée
 */
class NavigationCoexistenceTest : FunSpec({
    
    test("Voyager should now be the default navigation system") {
        NavigationFeatureFlag.USE_VOYAGER shouldBe true
    }
    
    test("StableNavController should still work for backward compatibility") {
        // Test de compatibilité - le système legacy devrait encore fonctionner
        val controller = StableNavController()
        controller.currentScreen shouldBe Screen.PrivacyPolicy
        controller.canGoBack() shouldBe false
        
        // Test navigation de base
        controller.navigate(Screen.Login)
        controller.currentScreen shouldBe Screen.Login
        controller.canGoBack() shouldBe true
        
        val backResult = controller.popBackStack()
        backResult shouldBe true
        controller.currentScreen shouldBe Screen.PrivacyPolicy
    }
    
    test("Legacy Screen enum should still contain all expected screens") {
        // Test de rétrocompatibilité pour les écrans existants
        val expectedScreens = setOf(
            Screen.PrivacyPolicy,
            Screen.Home, 
            Screen.Test,
            Screen.Settings,
            Screen.Login,
            Screen.Register,
            Screen.ForgotPassword
        )
        
        Screen.values().toSet() shouldBe expectedScreens
    }
    
    test("Navigation migration should be successfully completed") {
        // Test global validant que la migration est terminée
        val migrationCompleted = NavigationFeatureFlag.USE_VOYAGER
        val hasLegacySupport = Screen.values().isNotEmpty()
        
        migrationCompleted shouldBe true
        hasLegacySupport shouldBe true // Backward compatibility maintained
    }
})