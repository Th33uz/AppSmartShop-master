package com.example.smartshop.data.repository

import com.example.smartshop.data.remote.FirebaseDataSource
import com.example.smartshop.data.model.Item
import com.example.smartshop.data.model.Lista
import com.example.smartshop.data.model.User
import kotlinx.coroutines.flow.StateFlow

class ShoppingRepository(private val firebaseDataSource: FirebaseDataSource) {

    val userLogged: StateFlow<User?> = firebaseDataSource.userLogged

    suspend fun login(email: String, senha: String): Boolean =
        firebaseDataSource.login(email, senha)

    fun logout() = firebaseDataSource.logout()

    suspend fun register(user: User): Boolean =
        firebaseDataSource.register(user)

    fun currentUser(): User? = firebaseDataSource.currentUser()

    suspend fun minhasListas(): List<Lista> {
        val u = currentUser() ?: return emptyList()
        return firebaseDataSource.getAllListas().filter { it.dono == u.email }
    }

    suspend fun addLista(titulo: String, imagemUri: String?) {
        val u = currentUser() ?: return
        firebaseDataSource.addLista(Lista(titulo = titulo, dono = u.email, imagemUri = imagemUri))
    }

    suspend fun updateListaTitle(oldTitle: String, newTitle: String) =
        firebaseDataSource.updateListaTitle(oldTitle, newTitle)

    suspend fun removeListaByTitle(title: String) =
        firebaseDataSource.removeListaByTitle(title)

    suspend fun getListaByTitle(title: String): Lista? =
        firebaseDataSource.getListaByTitle(title)

    suspend fun addItemToLista(title: String, item: Item) =
        firebaseDataSource.addItemToLista(title, item)

    suspend fun updateItem(listaTitle: String, oldItem: Item, newItem: Item) =
        firebaseDataSource.updateItem(listaTitle, oldItem, newItem)

    suspend fun removeItem(listaTitle: String, item: Item) =
        firebaseDataSource.removeItem(listaTitle, item)

    suspend fun toggleItemComprado(listaTitle: String, item: Item, comprado: Boolean) =
        firebaseDataSource.toggleItemComprado(listaTitle, item, comprado)
}
