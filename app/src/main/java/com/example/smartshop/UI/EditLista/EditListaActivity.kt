package com.example.smartshop.ui.editlista

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartshop.R
import com.example.smartshop.databinding.ActivityEditListaBinding
import com.example.smartshop.di.ServiceLocator
import com.example.smartshop.data.model.Lista
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class EditListaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditListaBinding
    val viewModel: EditListaViewModel by viewModels { EditListaViewModelFactory(ServiceLocator.provideRepository()) }

    private var currentList: Lista? = null
    private var novaImagemUri: Uri? = null
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditListaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listTitle = intent.getStringExtra("TITULO_LISTA")

        if (listTitle == null) {
            Toast.makeText(this, "Erro: Lista não encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel.loadLista(listTitle)

        binding.btnVoltar.setOnClickListener {
            finish()
        }

        binding.btnSalvar.setOnClickListener {
            val listaParaSalvar = currentList
            val novoTitulo = binding.inputNomeLista.text.toString().trim()

            if (listaParaSalvar == null) {
                Toast.makeText(this, "Erro ao salvar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (novoTitulo.isEmpty()) {
                Toast.makeText(this, "O nome não pode ser vazio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (novaImagemUri != null) {
                uploadImagemEAtualizarLista(listaParaSalvar.titulo, novoTitulo, novaImagemUri!!)
            } else {
                viewModel.updateLista(listaParaSalvar.titulo, novoTitulo, listaParaSalvar.imagemUri)
                Toast.makeText(this, "Lista Salva!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnExcluir.setOnClickListener {
            val listaParaExcluir = currentList
            if (listaParaExcluir == null) {
                Toast.makeText(this, "Erro ao excluir", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this, R.style.Theme_SmartShop_AlertDialog)
                .setTitle("Excluir Lista")
                .setMessage("Tem certeza que deseja excluir a lista \"${listaParaExcluir.titulo}\"? Isso apagará todos os itens.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Excluir") { _, _ ->
                    viewModel.removeLista(listaParaExcluir.titulo)
                    Toast.makeText(this, "Lista excluída", Toast.LENGTH_SHORT).show()
                }
                .show()
        }

        binding.fabSelecionarImagem.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            startActivityForResult(intent, 100)
        }

        lifecycleScope.launch {
            viewModel.lista.collectLatest { lista ->
                if (lista != null) {
                    currentList = lista
                    binding.inputNomeLista.setText(lista.titulo)
                    if (!lista.imagemUri.isNullOrEmpty()) {
                        com.bumptech.glide.Glide.with(this@EditListaActivity)
                            .load(lista.imagemUri)
                            .placeholder(R.drawable.iconeimg)
                            .error(R.drawable.iconeimg)
                            .into(binding.imgPreviewLista)
                    } else {
                        binding.imgPreviewLista.setImageResource(R.drawable.iconeimg)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.eventoConcluido.collectLatest { concluido ->
                if (concluido) {
                    finish()
                }
            }
        }
    }

    private fun uploadImagemEAtualizarLista(tituloAntigo: String, novoTitulo: String, imageUri: Uri) {
        binding.btnSalvar.isEnabled = false
        Toast.makeText(this, "Enviando imagem...", Toast.LENGTH_SHORT).show()

        val nomeArquivo = "listas/${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child(nomeArquivo)

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val imageUrl = downloadUrl.toString()
                    viewModel.updateLista(tituloAntigo, novoTitulo, imageUrl)
                    Toast.makeText(this, "Lista atualizada!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                binding.btnSalvar.isEnabled = true
                Toast.makeText(this, "Erro ao enviar imagem: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    @Deprecated("Deprecated")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            novaImagemUri = data?.data
            binding.imgPreviewLista.setImageURI(novaImagemUri)
        }
    }
}
