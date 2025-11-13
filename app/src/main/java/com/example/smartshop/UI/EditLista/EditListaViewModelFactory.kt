package com.example.smartshop.ui.editlista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartshop.data.repository.ShoppingRepository

class EditListaViewModelFactory(
    private val repository: ShoppingRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditListaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditListaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}