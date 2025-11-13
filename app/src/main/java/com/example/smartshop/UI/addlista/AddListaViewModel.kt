package com.example.smartshop.ui.addlista


import androidx.lifecycle.ViewModel
import com.example.smartshop.data.repository.ShoppingRepository


class AddListaViewModel(private val repo: ShoppingRepository): ViewModel() {
    fun addLista(titulo: String, imagemUri: String?) = repo.addLista(titulo, imagemUri)
}