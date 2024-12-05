package com.project.diacheck.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.project.diacheck.R
import com.project.diacheck.data.Result
import com.project.diacheck.data.preference.UserModel
import com.project.diacheck.databinding.ActivityLoginBinding
import com.project.diacheck.ui.ViewModelFactory
import com.project.diacheck.ui.main.MainActivity
import com.project.diacheck.ui.signup.SignupActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun playAnimation() {

        val title = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val message2 =
            ObjectAnimator.ofFloat(binding.messageTextView2, View.ALPHA, 1f).setDuration(100)
        val message3 =
            ObjectAnimator.ofFloat(binding.messageTextView3, View.ALPHA, 1f).setDuration(100)
        val message4 =
            ObjectAnimator.ofFloat(binding.notRegisteredText, View.ALPHA, 1f).setDuration(100)
        val message5 =
            ObjectAnimator.ofFloat(binding.createAccountText, View.ALPHA, 1f).setDuration(100)
        val register = ObjectAnimator.ofFloat(binding.googleButton, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)
        val emailText =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEdit =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordText =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEdit =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(login, register)
        }

        AnimatorSet().apply {
            playSequentially(
                title,
                message2,
                message3,
                message4,
                message5,
                emailText,
                emailEdit,
                passwordText,
                passwordEdit,
                together
            )
            start()
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditTextLayout.text.toString()
            val password = binding.passwordEditTextLayout.text.toString()

            viewModel.login(email, password).observe(this) { result ->
                when (result) {
                    is Result.Error -> {
                        binding.linearProgressBar.visibility = View.GONE
                        Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    }

                    is Result.Loading -> {
                        binding.linearProgressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.linearProgressBar.visibility = View.GONE
                        if (result.data.error == true) {
                            Toast.makeText(this, result.data.message, Toast.LENGTH_SHORT).show()
                        } else {
                            result.data.loginResult?.let {
                                Toast.makeText(
                                    this,
                                    getString(R.string.login_success),
                                    Toast.LENGTH_SHORT
                                ).show()

                                viewModel.saveSession(UserModel(it.name, email, it.token, true, it.id_users, it.avatar))

                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                        }

                    }
                }
            }
        }
        binding.createAccountText.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

}