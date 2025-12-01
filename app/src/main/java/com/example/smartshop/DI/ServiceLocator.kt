package com.example.smartshop.di

import com.example.smartshop.data.repository.ShoppingRepository
import com.example.smartshop.data.remote.FirebaseDataSource

object ServiceLocator {
    private var repository: ShoppingRepository? = null

    fun provideRepository(): ShoppingRepository {
        return repository ?: ShoppingRepository(FirebaseDataSource()).also {
            repository = it
        }
    }
}