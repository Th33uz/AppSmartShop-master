package com.example.smartshop.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.smartshop.R
import com.example.smartshop.databinding.ActivityMainBinding
import com.example.smartshop.di.ServiceLocator
import com.example.smartshop.ui.home.HomeActivity
import com.example.smartshop.ui.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.text.InputType


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(ServiceLocator.provideRepository()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collectLatest { isLoggedIn ->
                    when (isLoggedIn) {
                        true -> {
                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                            finish()
                        }
                        false -> {
                            Toast.makeText(this@MainActivity, "E-mail ou senha incorretos", Toast.LENGTH_SHORT).show()
                            viewModel.resetLoginState()
                        }
                        null -> { }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.BtnLogin.setOnClickListener {
            val email = binding.inputEmail.text.toString().trim()
            val senha = binding.inputSenha.text.toString().trim()

            if (email.isNotBlank() && senha.isNotBlank()) {
                viewModel.tryLogin(email, senha)
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.BtnRegistrar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.txtEsqueciSenha.setOnClickListener {
            mostrarDialogRecuperarSenha()
        }
    }


    private fun mostrarDialogRecuperarSenha() {
        val builder = AlertDialog.Builder(this, R.style.Theme_SmartShop_AlertDialog)
        builder.setTitle("Recuperar Senha")

        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        container.setPadding(50, 20, 50, 20)

        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        input.hint = "Digite seu email"
        input.textSize = 16f
        input.setTextColor(resources.getColor(android.R.color.white, null))
        input.setHintTextColor(resources.getColor(android.R.color.darker_gray, null))
        container.addView(input)

        builder.setView(container)

        builder.setPositiveButton("Enviar") { dialog, _ ->
            val email = input.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Digite um email válido", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "✅ Email enviado! Confira sua caixa de entrada.",
                        Toast.LENGTH_LONG
                    ).show()
                    dialog.dismiss()
                }
                .addOnFailureListener { e ->
                    val mensagem = when {
                        e.message?.contains("no user record", ignoreCase = true) == true ->
                            "Email não cadastrado"
                        e.message?.contains("badly formatted", ignoreCase = true) == true ->
                            "Email inválido"
                        e.message?.contains("network", ignoreCase = true) == true ->
                            "Erro de conexão. Verifique sua internet."
                        else -> "Erro: ${e.message}"
                    }
                    Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
                }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }
}
