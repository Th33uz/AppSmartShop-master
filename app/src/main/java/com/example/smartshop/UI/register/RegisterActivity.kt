package com.example.smartshop.ui.register


import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels // <-- IMPORTANTE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle // <-- IMPORTANTE
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle // <-- IMPORTANTE
import com.example.smartshop.databinding.ActivityRegisterBinding
import com.example.smartshop.di.ServiceLocator
import com.example.smartshop.ui.main.MainActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch // <-- IMPORTANTE


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val vm: RegisterViewModel by viewModels {
        RegisterViewModelFactory(ServiceLocator.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.registerOk.collectLatest { ok ->
                    when (ok) {
                        true -> {
                            Toast.makeText(this@RegisterActivity, "Usuário registrado com sucesso!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                            finish()
                        }
                        false -> Toast.makeText(this@RegisterActivity, "Já existe um usuário com este e-mail", Toast.LENGTH_SHORT).show()
                        null -> Unit // Estado inicial
                    }
                }
            }
        }

        binding.btnVoltar.setOnClickListener {
            finish()
        }

        binding.btnCadastrar.setOnClickListener {

            val nome = binding.inputNome.text.toString()
            val email = binding.inputEmail.text.toString()
            val senha = binding.inputSenha.text.toString()
            val confirma = binding.inputConfirmarSenha.text.toString()

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirma.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            if (senha != confirma) { Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { Toast.makeText(this, "Digite um e-mail válido", Toast.LENGTH_SHORT).show(); return@setOnClickListener }

            vm.register(nome, email, senha)
        }
    }
}