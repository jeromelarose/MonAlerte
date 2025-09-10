package org.jelarose.monalerte.core.storage

/**
 * Interface pour le stockage sécurisé multiplateforme
 * Utilise le Keychain sur iOS et le Keystore sur Android
 * 
 * Compatible avec l'architecture existante - fonctionne en parallèle
 * avec DataStore et Room pour une sécurité améliorée des tokens
 */
expect class SecureStorage() {
    /**
     * Stocke une valeur de façon sécurisée
     * @param key Clé unique pour identifier la valeur
     * @param value Valeur à stocker de façon chiffrée
     */
    suspend fun store(key: String, value: String)
    
    /**
     * Récupère une valeur stockée de façon sécurisée
     * @param key Clé pour identifier la valeur
     * @return La valeur déchiffrée ou null si non trouvée
     */
    suspend fun retrieve(key: String): String?
    
    /**
     * Supprime une valeur du stockage sécurisé
     * @param key Clé de la valeur à supprimer
     */
    suspend fun remove(key: String)
    
    /**
     * Efface tout le stockage sécurisé
     * Utilisé lors de la déconnexion
     */
    suspend fun clear()
}