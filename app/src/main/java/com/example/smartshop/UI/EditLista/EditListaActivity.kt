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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditListaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditListaBinding
    private val vm: EditListaViewModel by viewModels {
        EditListaViewModelFactory(ServiceLocator.repository)
    }

    private var currentList: Lista? = null
    private var imagemSelecionada: Uri? = null

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

        vm.loadLista(listTitle)

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

            // CORREÇÃO: Chamando a função 'updateLista' (em vez de 'renameLista')
            vm.updateLista(listaParaSalvar.titulo, novoTitulo, imagemSelecionada?.toString())
            Toast.makeText(this, "Lista Salva!", Toast.LENGTH_SHORT).show()
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
                    // Chama a função 'removeLista' correta
                    vm.removeLista(listaParaExcluir.titulo)
                    Toast.makeText(this, "Lista excluída", Toast.LENGTH_SHORT).show()
                }
                .show()
        }

        binding.fabSelecionarImagem.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            startActivityForResult(intent, 100)
        }

        lifecycleScope.launch {
            vm.lista.collectLatest { lista ->
                if (lista != null) {
                    currentList = lista
                    binding.inputNomeLista.setText(lista.titulo)
                    if (!lista.imagemUri.isNullOrEmpty()) {
                        val uri = Uri.parse(lista.imagemUri)
                        imagemSelecionada = uri
                        binding.imgPreviewLista.setImageURI(uri)
                    } else {
                        binding.imgPreviewLista.setImageResource(R.drawable.iconeimg)
                        imagemSelecionada = null
                    }
                }
            }
        }

        lifecycleScope.launch {
            vm.eventoConcluido.collectLatest { concluido ->
                if (concluido) {
                    finish()
                }
            }
        }
    }

    @Deprecated("Deprecated")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imagemSelecionada = data?.data
            binding.imgPreviewLista.setImageURI(imagemSelecionada)
        }
    }
}