package org.jelarose.monalerte.core.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.jelarose.monalerte.core.database.AppDatabase
import org.jelarose.monalerte.core.database.getDatabaseBuilder
import org.jelarose.monalerte.core.repository.ToggleRepository
import org.jelarose.monalerte.core.network.ApiService
import org.jelarose.monalerte.core.network.createHttpClient
import org.jelarose.monalerte.core.utils.SharedDataStore
import org.jelarose.monalerte.features.test.presentation.TestViewModel
import org.jelarose.monalerte.features.auth.data.api.AuthApiService
import org.jelarose.monalerte.features.auth.data.repository.AuthRepositoryImpl
import org.jelarose.monalerte.features.auth.domain.repository.AuthRepository
import org.jelarose.monalerte.features.auth.domain.usecases.LoginUseCase
import org.jelarose.monalerte.features.auth.domain.usecases.RegisterUseCase
import org.jelarose.monalerte.features.auth.domain.usecases.ForgotPasswordUseCase
import org.jelarose.monalerte.features.auth.domain.usecases.GetAuthTokenUseCase
import org.jelarose.monalerte.features.auth.presentation.viewmodels.AuthViewModel
import org.jelarose.monalerte.features.auth.presentation.viewmodels.SharedAuthViewModel
import org.jelarose.monalerte.core.utils.LanguageManager
import org.jelarose.monalerte.core.policy.PolicyManager
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
    
    // Language Manager - Singleton
    single { LanguageManager(get()) }
    
    // Policy Manager - Singleton
    single { PolicyManager(get()) }
    
    // Authentication - API Service
    single<AuthApiService> { AuthApiService(get()) }
    
    // Authentication - Repository
    single<AuthRepository> { 
        AuthRepositoryImpl(get<AuthApiService>(), get<AppDatabase>().authDao(), get<SharedDataStore>()) 
    }
    
    // Also register AuthRepositoryImpl directly for cases where it's needed
    single<AuthRepositoryImpl> { 
        AuthRepositoryImpl(get<AuthApiService>(), get<AppDatabase>().authDao(), get<SharedDataStore>()) 
    }
    
    // Authentication - Use Cases
    singleOf(::LoginUseCase)
    singleOf(::RegisterUseCase)
    singleOf(::ForgotPasswordUseCase)
    singleOf(::GetAuthTokenUseCase)
    
    // ViewModels - Factory (nouvelle instance à chaque injection)
    factory { TestViewModel(get(), get(), get()) }
    factory { AuthViewModel(get(), get(), get(), get()) }
    factory { SharedAuthViewModel(get(), get(), get(), get(), get()) }
}