package com.example.smartshop.ui.addlista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshop.data.repository.ShoppingRepository
import kotlinx.coroutines.launch

class AddListaViewModel(private val repo: ShoppingRepository): ViewModel() {

    fun addLista(titulo: String, imagemUri: String?) {
        viewModelScope.launch {
            repo.addLista(titulo, imagemUri)
        }
    }
}
