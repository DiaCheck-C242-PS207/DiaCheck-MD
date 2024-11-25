package com.capstone.diacheck.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.capstone.diacheck.R
import com.capstone.diacheck.databinding.ActivitySignupBinding
import com.capstone.diacheck.data.Result
import com.capstone.diacheck.ui.ViewModelFactory
import com.capstone.diacheck.ui.login.LoginActivity
import com.capstone.diacheck.ui.main.MainActivity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {
    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        setupView()
        setupAction()
        playAnimation()
    }

    private fun playAnimation() {

        val title = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val nameText = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.googleButton, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)
        val message2 = ObjectAnimator.ofFloat(binding.messageTextView2, View.ALPHA, 1f).setDuration(100)
        val message3 = ObjectAnimator.ofFloat(binding.notRegisteredText, View.ALPHA, 1f).setDuration(100)
        val message4 = ObjectAnimator.ofFloat(binding.haveAnAccount, View.ALPHA, 1f).setDuration(100)
        val emailText = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailEdit = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordText = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEdit = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(signup, login)
        }

        AnimatorSet().apply {
            playSequentially(title,
                message2,
                message3,
                message4,
                nameText,
                emailText,
                nameEditTextLayout,
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
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditTextLayout.text.toString()
            val email = binding.emailEditTextLayout.text.toString()
            val password = binding.passwordEditTextLayout.text.toString()

            viewModel.signup(name, email, password).observe(this) { result ->
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
                            Toast.makeText(this, result.data.message, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
        }
        binding.googleButton.setOnClickListener {
            val credentialManager = CredentialManager.create(this) //import from androidx.CredentialManager
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.web_client_id))
                .build()
            val request = GetCredentialRequest.Builder() //import from androidx.CredentialManager
                .addCredentialOption(googleIdOption)
                .build()

            lifecycleScope.launch {
                try {
                    val result: GetCredentialResponse = credentialManager.getCredential( //import from androidx.CredentialManager
                        request = request,
                        context = this@SignupActivity,
                    )
                    handleSignIn(result)
                } catch (e: GetCredentialException) { //import from androidx.CredentialManager
                    Log.d("Error", e.message.toString())
                }
            }
        }
        binding.haveAnAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user: FirebaseUser? = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this@SignupActivity, MainActivity::class.java))
            finish()
        }
    }

    companion object {
        private const val TAG = "SignupActivity"
    }
}