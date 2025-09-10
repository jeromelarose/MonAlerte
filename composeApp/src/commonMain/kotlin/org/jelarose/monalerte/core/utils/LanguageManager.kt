package org.jelarose.monalerte.core.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jelarose.monalerte.core.utils.SharedDataStore
import co.touchlab.kermit.Logger

/**
 * Language manager for handling language preferences
 */
class LanguageManager(
    private val sharedDataStore: SharedDataStore
) {
    private val logger = Logger.withTag("LanguageManager")
    
    companion object {
        const val LANGUAGE_FRENCH = "fr"
        const val LANGUAGE_ENGLISH = "en"
        private const val LANGUAGE_KEY = "selected_language"
    }
    
    private val _currentLanguage = MutableStateFlow(LANGUAGE_FRENCH)
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()
    
    init {
        // Load saved language preference
        loadSavedLanguage()
    }
    
    private fun loadSavedLanguage() {
        try {
            // Load language asynchronously using coroutine
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val savedLanguage = sharedDataStore.getString(LANGUAGE_KEY) 
                        ?: sharedDataStore.getString("app_language") // Fallback pour compatibilité
                        ?: LANGUAGE_FRENCH
                    
                    _currentLanguage.value = savedLanguage
                    logger.d { "Loaded saved language: $savedLanguage" }
                } catch (e: Exception) {
                    logger.w(e) { "Error loading saved language, using default" }
                    _currentLanguage.value = LANGUAGE_FRENCH
                }
            }
        } catch (e: Exception) {
            logger.e(e) { "Error initializing language loading" }
            _currentLanguage.value = LANGUAGE_FRENCH
        }
    }
    
    suspend fun setLanguage(languageCode: String) {
        try {
            logger.d { "Setting language to: $languageCode" }
            // Save with both keys for compatibility
            sharedDataStore.putString(LANGUAGE_KEY, languageCode)
            sharedDataStore.putString("app_language", languageCode) // Sync with AuthRepository
            _currentLanguage.value = languageCode
            logger.d { "Language preference saved successfully" }
        } catch (e: Exception) {
            logger.e(e) { "Error saving language preference" }
        }
    }
    
    fun getLanguageDisplayName(languageCode: String): String {
        return when (languageCode) {
            LANGUAGE_FRENCH -> "Français"
            LANGUAGE_ENGLISH -> "English"
            else -> "Français"
        }
    }
}