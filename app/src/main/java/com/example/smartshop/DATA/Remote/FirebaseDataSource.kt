package com.example.smartshop.data.remote

import android.util.Log
import com.example.smartshop.data.model.Item
import com.example.smartshop.data.model.Lista
import com.example.smartshop.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirebaseDataSource {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _userLogged = MutableStateFlow<User?>(null)
    val userLogged: StateFlow<User?> = _userLogged.asStateFlow()

    // ============ AUTENTICAÇÃO ============

    suspend fun register(user: User): Boolean = try {
        Log.d("FirebaseDataSource", "Tentando registrar: ${user.email}")

        auth.createUserWithEmailAndPassword(user.email, user.senha).await()

        Log.d("FirebaseDataSource", "Usuário criado no Auth, salvando no Firestore...")

        firestore.collection("users").document(user.email).set(
            mapOf(
                "nome" to user.nome,
                "email" to user.email,
                "criadoEm" to com.google.firebase.Timestamp.now()
            )
        ).await()

        Log.d("FirebaseDataSource", "Registro completo com sucesso!")
        true
    } catch (e: FirebaseAuthUserCollisionException) {
        Log.e("FirebaseDataSource", "Email já existe: ${e.message}")
        false
    } catch (e: Exception) {
        Log.e("FirebaseDataSource", "Erro ao registrar: ${e.message}", e)
        false
    }

    suspend fun login(email: String, senha: String): Boolean = try {
        Log.d("FirebaseDataSource", "Tentando login: $email")

        auth.signInWithEmailAndPassword(email, senha).await()

        Log.d("FirebaseDataSource", "Login no Auth OK, buscando dados do Firestore...")

        val userDoc = firestore.collection("users").document(email).get().await()
        val nome = userDoc.getString("nome") ?: ""
        _userLogged.value = User(nome = nome, email = email, senha = senha)

        Log.d("FirebaseDataSource", "Login completo!")
        true
    } catch (e: Exception) {
        Log.e("FirebaseDataSource", "Erro no login: ${e.message}", e)
        false
    }

    fun logout() {
        auth.signOut()
        _userLogged.value = null
    }

    fun currentUser(): User? = _userLogged.value

    // ============ LISTAS ============
    // ... resto do código permanece igual

    suspend fun getAllListas(): List<Lista> = try {
        val currentEmail = auth.currentUser?.email ?: return emptyList()

        val snapshot = firestore.collection("listas")
            .whereEqualTo("dono", currentEmail)
            .get()
            .await()

        snapshot.documents.mapNotNull { doc ->
            val titulo = doc.getString("titulo") ?: return@mapNotNull null
            val dono = doc.getString("dono") ?: ""
            val imagemUri = doc.getString("imagemUri")
            val itens = mutableListOf<Item>()

            val itensSnapshot = doc.reference.collection("itens").get().await()
            itensSnapshot.documents.forEach { itemDoc ->
                val nome = itemDoc.getString("nome") ?: return@forEach
                val quantidade = itemDoc.getLong("quantidade")?.toInt() ?: 0
                val unidade = itemDoc.getString("unidade") ?: ""
                val categoria = itemDoc.getString("categoria") ?: ""
                val comprado = itemDoc.getBoolean("comprado") ?: false

                itens.add(Item(nome, quantidade, unidade, categoria, comprado))
            }

            Lista(titulo = titulo, dono = dono, imagemUri = imagemUri, itens = itens)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }

    suspend fun addLista(lista: Lista): Boolean = try {
        val docRef = firestore.collection("listas").document()
        docRef.set(
            mapOf(
                "titulo" to lista.titulo,
                "dono" to lista.dono,
                "imagemUri" to lista.imagemUri,
                "criadoEm" to com.google.firebase.Timestamp.now()
            )
        ).await()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    suspend fun getListaByTitle(title: String): Lista? = try {
        val currentEmail = auth.currentUser?.email ?: return null
        val snapshot = firestore.collection("listas")
            .whereEqualTo("titulo", title)
            .whereEqualTo("dono", currentEmail)
            .get()
            .await()

        val doc = snapshot.documents.firstOrNull() ?: return null
        val titulo = doc.getString("titulo") ?: return null
        val dono = doc.getString("dono") ?: ""
        val imagemUri = doc.getString("imagemUri")
        val itens = mutableListOf<Item>()

        val itensSnapshot = doc.reference.collection("itens").get().await()
        itensSnapshot.documents.forEach { itemDoc ->
            val nome = itemDoc.getString("nome") ?: return@forEach
            val quantidade = itemDoc.getLong("quantidade")?.toInt() ?: 0
            val unidade = itemDoc.getString("unidade") ?: ""
            val categoria = itemDoc.getString("categoria") ?: ""
            val comprado = itemDoc.getBoolean("comprado") ?: false

            itens.add(Item(nome, quantidade, unidade, categoria, comprado))
        }

        Lista(titulo = titulo, dono = dono, imagemUri = imagemUri, itens = itens)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    suspend fun updateListaTitle(oldTitle: String, newTitle: String): Boolean = try {
        val currentEmail = auth.currentUser?.email ?: return false
        val snapshot = firestore.collection("listas")
            .whereEqualTo("titulo", oldTitle)
            .whereEqualTo("dono", currentEmail)
            .get()
            .await()

        snapshot.documents.firstOrNull()?.reference?.update("titulo", newTitle)?.await()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    suspend fun removeListaByTitle(title: String): Boolean = try {
        val currentEmail = auth.currentUser?.email ?: return false
        val snapshot = firestore.collection("listas")
            .whereEqualTo("titulo", title)
            .whereEqualTo("dono", currentEmail)
            .get()
            .await()

        snapshot.documents.firstOrNull()?.reference?.delete()?.await()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    // ============ ITENS ============

    suspend fun addItemToLista(listaTitle: String, item: Item): Boolean = try {
        val currentEmail = auth.currentUser?.email ?: return false
        val snapshot = firestore.collection("listas")
            .whereEqualTo("titulo", listaTitle)
            .whereEqualTo("dono", currentEmail)
            .get()
            .await()

        val listaDoc = snapshot.documents.firstOrNull() ?: return false
        listaDoc.reference.collection("itens").document().set(
            mapOf(
                "nome" to item.nome,
                "quantidade" to item.quantidade,
                "unidade" to item.unidade,
                "categoria" to item.categoria,
                "comprado" to item.comprado
            )
        ).await()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    suspend fun updateItem(listaTitle: String, oldItem: Item, newItem: Item): Boolean = try {
        val currentEmail = auth.currentUser?.email ?: return false
        val snapshot = firestore.collection("listas")
            .whereEqualTo("titulo", listaTitle)
            .whereEqualTo("dono", currentEmail)
            .get()
            .await()

        val listaDoc = snapshot.documents.firstOrNull() ?: return false
        val itensSnapshot = listaDoc.reference.collection("itens")
            .whereEqualTo("nome", oldItem.nome)
            .whereEqualTo("categoria", oldItem.categoria)
            .get()
            .await()

        val itemDoc = itensSnapshot.documents.firstOrNull() ?: return false
        itemDoc.reference.update(
            mapOf(
                "nome" to newItem.nome,
                "quantidade" to newItem.quantidade,
                "unidade" to newItem.unidade,
                "categoria" to newItem.categoria,
                "comprado" to newItem.comprado
            )
        ).await()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    suspend fun removeItem(listaTitle: String, item: Item): Boolean = try {
        val currentEmail = auth.currentUser?.email ?: return false
        val snapshot = firestore.collection("listas")
            .whereEqualTo("titulo", listaTitle)
            .whereEqualTo("dono", currentEmail)
            .get()
            .await()

        val listaDoc = snapshot.documents.firstOrNull() ?: return false
        val itensSnapshot = listaDoc.reference.collection("itens")
            .whereEqualTo("nome", item.nome)
            .whereEqualTo("categoria", item.categoria)
            .get()
            .await()

        itensSnapshot.documents.firstOrNull()?.reference?.delete()?.await()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    suspend fun toggleItemComprado(listaTitle: String, item: Item, comprado: Boolean): Boolean = try {
        val currentEmail = auth.currentUser?.email ?: return false
        val snapshot = firestore.collection("listas")
            .whereEqualTo("titulo", listaTitle)
            .whereEqualTo("dono", currentEmail)
            .get()
            .await()

        val listaDoc = snapshot.documents.firstOrNull() ?: return false
        val itensSnapshot = listaDoc.reference.collection("itens")
            .whereEqualTo("nome", item.nome)
            .whereEqualTo("categoria", item.categoria)
            .get()
            .await()

        itensSnapshot.documents.firstOrNull()?.reference?.update("comprado", comprado)?.await()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
