package com.example.smartshop.ui.additem


import androidx.lifecycle.ViewModel
import com.example.smartshop.data.model.Item
import com.example.smartshop.data.repository.ShoppingRepository


class AddItemViewModel(private val repo: ShoppingRepository): ViewModel() {
    fun addItem(listTitle: String, item: Item) = repo.addItemToLista(listTitle, item)
}