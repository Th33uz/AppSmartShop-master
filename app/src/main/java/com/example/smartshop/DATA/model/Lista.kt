package com.example.smartshop.data.model


data class Lista(
    var titulo: String,
    val dono: String,
    var imagemUri: String? = null,
    val itens: MutableList<Item> = mutableListOf()
)