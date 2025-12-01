package com.example.smartshop.ui.edititem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshop.data.model.Item
import com.example.smartshop.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditItemViewModel(private val repo: ShoppingRepository) : ViewModel() {

    private val _item = MutableStateFlow<Item?>(null)
    val item: StateFlow<Item?> = _item.asStateFlow()

    private var currentListTitle: String = ""

    private val _eventoConcluido = MutableStateFlow(false)
    val eventoConcluido: StateFlow<Boolean> = _eventoConcluido.asStateFlow()

    fun loadItem(listTitle: String, itemName: String, itemCategory: String) {
        viewModelScope.launch {
            currentListTitle = listTitle
            val currentList = repo.getListaByTitle(listTitle)
            _item.value = currentList?.itens?.find {
                it.nome == itemName && it.categoria == itemCategory
            }
        }
    }

    fun updateItem(
        itemAntigo: Item,
        nomeNovo: String,
        qtdNova: Int,
        unidadeNova: String,
        categoriaNova: String
    ) {
        viewModelScope.launch {
            val newItem = Item(
                nome = nomeNovo,
                quantidade = qtdNova,
                unidade = unidadeNova,
                categoria = categoriaNova,
                comprado = itemAntigo.comprado
            )
            repo.updateItem(currentListTitle, itemAntigo, newItem)
            _eventoConcluido.value = true
        }
    }

    fun removeItem(item: Item) {
        viewModelScope.launch {
            repo.removeItem(currentListTitle, item)
            _eventoConcluido.value = true
        }
    }
}
