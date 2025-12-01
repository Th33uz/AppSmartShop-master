package com.example.smartshop.ui.addlista

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.smartshop.databinding.ActivityAddListaBinding
import com.example.smartshop.di.ServiceLocator

class AddListaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddListaBinding
    private var imagemSelecionada: Uri? = null

    val viewModel: AddListaViewModel by viewModels { AddListaViewModelFactory(ServiceLocator.provideRepository()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddListaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVoltar.setOnClickListener {
            finish()
        }

        binding.fabSelecionarImagem.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            startActivityForResult(intent, 100)
        }

        binding.btnAdicionarLista.setOnClickListener {
            val nomeLista = binding.inputNomeLista.text.toString().trim()
            if (nomeLista.isEmpty()) {
                Toast.makeText(this, "Digite o nome da lista", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            viewModel.addLista(nomeLista, imagemSelecionada?.toString())
            Toast.makeText(this, "Lista adicionada!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    @Deprecated("startActivityForResult está deprecated; considere Activity Result API futuramente")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imagemSelecionada = data?.data
            binding.imgPreviewLista.setImageURI(imagemSelecionada)
        }
    }
}