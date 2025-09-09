package org.jelarose.monalerte.core.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

/**
 * Modèles de données pour l'API de test
 */
@Serializable
data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val website: String
)

/**
 * Service API utilisant JSONPlaceholder pour les tests
 */
class ApiService(private val httpClient: HttpClient) {
    
    private val baseUrl = "https://jsonplaceholder.typicode.com"
    
    /**
     * Récupère la liste des posts
     */
    suspend fun getPosts(): List<Post> {
        return httpClient.get("$baseUrl/posts").body()
    }
    
    /**
     * Récupère un post spécifique par ID
     */
    suspend fun getPost(id: Int): Post {
        return httpClient.get("$baseUrl/posts/$id").body()
    }
    
    /**
     * Récupère les informations d'un utilisateur
     */
    suspend fun getUser(id: Int): User {
        return httpClient.get("$baseUrl/users/$id").body()
    }
    
    /**
     * Crée un nouveau post
     */
    suspend fun createPost(title: String, body: String, userId: Int): Post {
        return httpClient.post("$baseUrl/posts") {
            setBody(
                mapOf(
                    "title" to title,
                    "body" to body,
                    "userId" to userId
                )
            )
        }.body()
    }
}