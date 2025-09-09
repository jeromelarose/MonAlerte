package org.jelarose.monalerte.core.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.jelarose.monalerte.core.database.AppDatabase
import org.jelarose.monalerte.core.database.getDatabaseBuilder
import org.jelarose.monalerte.core.repository.ToggleRepository
import org.jelarose.monalerte.core.network.ApiService
import org.jelarose.monalerte.core.network.createHttpClient
import org.jelarose.monalerte.features.test.presentation.TestViewModel
import io.ktor.client.*

/**
 * Module Koin principal pour les dépendances partagées
 */
val appModule = module {
    
    // Network - Singleton
    single<HttpClient> { 
        createHttpClient() 
    }
    
    single<ApiService> { 
        ApiService(get()) 
    }
    
    // Database - Singleton
    single<AppDatabase> { 
        getDatabaseBuilder().build() 
    }
    
    // Repository - Singleton  
    singleOf(::ToggleRepository)
    
    // ViewModels - Factory (nouvelle instance à chaque injection)
    factory { TestViewModel(get(), get(), get()) }
}