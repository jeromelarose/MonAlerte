package org.jelarose.monalerte.core.navigation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests pour valider que les deux systèmes de navigation coexistent
 */
class NavigationCoexistenceTest : FunSpec({
    
    test("StableNavigation should work with default feature flag") {
        NavigationFeatureFlag.USE_VOYAGER shouldBe false
    }
    
    test("StableNavController should be initializable") {
        val controller = StableNavController()
        controller.currentScreen shouldBe Screen.PrivacyPolicy
        controller.canGoBack() shouldBe false
    }
    
    test("StableNavController should navigate correctly") {
        val controller = StableNavController()
        
        controller.navigate(Screen.Login)
        controller.currentScreen shouldBe Screen.Login
        controller.canGoBack() shouldBe true
        
        val backResult = controller.popBackStack()
        backResult shouldBe true
        controller.currentScreen shouldBe Screen.PrivacyPolicy
    }
    
    test("Screen enum should contain all expected screens") {
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
})