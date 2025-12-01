package com.example.smartshop.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshop.data.model.Lista
import com.example.smartshop.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: ShoppingRepository) : ViewModel() {

    private val _listas = MutableStateFlow<List<Lista>>(emptyList())
    val listas: StateFlow<List<Lista>> = _listas.asStateFlow()

    private var listaCompleta: List<Lista> = emptyList()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            listaCompleta = repo.minhasListas()
            _listas.value = listaCompleta
        }
    }

    fun logout() = repo.logout()

    fun removeLista(title: String) {
        viewModelScope.launch {
            repo.removeListaByTitle(title)
            load()
        }
    }

    fun renameLista(oldTitle: String, newTitle: String) {
        viewModelScope.launch {
            repo.updateListaTitle(oldTitle, newTitle)
            load()
        }
    }

    fun filterLists(query: String) {
        if (query.isEmpty()) {
            _listas.value = listaCompleta
        } else {
            val listaFiltrada = listaCompleta.filter {
                it.titulo.contains(query, ignoreCase = true)
            }
            _listas.value = listaFiltrada
        }
    }
}
