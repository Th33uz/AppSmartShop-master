package com.example.smartshop.data.model

data class Item(
    var nome: String,
    var quantidade: Int,
    var unidade: String,
    var categoria: String,
    var comprado: Boolean = false
)