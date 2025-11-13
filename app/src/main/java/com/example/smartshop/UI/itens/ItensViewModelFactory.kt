package com.example.smartshop.ui.itens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartshop.data.repository.ShoppingRepository

class ItensViewModelFactory(
    private val repository: ShoppingRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItensViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItensViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}