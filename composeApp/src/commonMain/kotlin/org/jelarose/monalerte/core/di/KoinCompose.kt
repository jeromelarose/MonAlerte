package org.jelarose.monalerte.core.di

import androidx.compose.runtime.Composable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Helper pour injection Koin dans Compose - compatible multiplateforme
 */
object KoinComposeHelper : KoinComponent {
    inline fun <reified T> get(): T {
        return getKoin().get()
    }
}

/**
 * Fonction multiplateforme pour injection dans Compose
 */
@Composable
inline fun <reified T> koinInject(): T {
    return KoinComposeHelper.get<T>()
}