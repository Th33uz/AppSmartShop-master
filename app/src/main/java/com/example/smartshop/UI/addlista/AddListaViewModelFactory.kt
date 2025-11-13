package com.example.smartshop.ui.addlista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartshop.data.repository.ShoppingRepository

class AddListaViewModelFactory(
    private val repository: ShoppingRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddListaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddListaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}