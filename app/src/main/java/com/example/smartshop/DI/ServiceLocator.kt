package com.example.smartshop.di


import com.example.smartshop.data.local.FakeDataSource
import com.example.smartshop.data.repository.ShoppingRepository


object ServiceLocator {
    private val dataSource: FakeDataSource by lazy { FakeDataSource() }


    val repository: ShoppingRepository by lazy {
        ShoppingRepository(dataSource)
    }
}