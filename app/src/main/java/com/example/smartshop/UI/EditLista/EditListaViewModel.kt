package com.example.smartshop.ui.editlista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshop.data.model.Lista
import com.example.smartshop.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditListaViewModel(private val repo: ShoppingRepository) : ViewModel() {

    private val _lista = MutableStateFlow<Lista?>(null)
    val lista: StateFlow<Lista?> = _lista.asStateFlow()

    private val _eventoConcluido = MutableStateFlow(false)
    val eventoConcluido: StateFlow<Boolean> = _eventoConcluido.asStateFlow()

    fun loadLista(title: String) {
        viewModelScope.launch {
            _lista.value = repo.getListaByTitle(title)
        }
    }

    fun updateLista(oldTitle: String, newTitle: String, newImageUri: String?) {
        viewModelScope.launch {
            repo.updateListaTitle(oldTitle, newTitle)
            _eventoConcluido.value = true
        }
    }

    fun removeLista(title: String) {
        viewModelScope.launch {
            repo.removeListaByTitle(title)
            _eventoConcluido.value = true
        }
    }
}
