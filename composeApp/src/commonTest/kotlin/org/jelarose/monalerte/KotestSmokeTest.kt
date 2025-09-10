package org.jelarose.monalerte

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Test simple pour vérifier que Kotest fonctionne correctement
 */
class KotestSmokeTest : FunSpec({
    test("kotest should work correctly") {
        val result = 2 + 2
        result shouldBe 4
    }
    
    test("string operations should work") {
        val text = "Hello"
        val result = "$text World"
        result shouldBe "Hello World"
    }
})