package com.example.testwidetech

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Image
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testwidetech.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth_google_id = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
        activeSessionComprobation ()
    }

    private fun setup(){
        binding.signUpButton.setOnClickListener{
            if (binding.userEditText.text.isNotEmpty() &&  binding.passwordEditText.text.isNotEmpty()){
                Firebase.auth.createUserWithEmailAndPassword(binding.userEditText.text.toString(), binding.passwordEditText.text.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showProfile(task.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert("0")
                    }
                }
            }
        }

        binding.loginButton.setOnClickListener{
            if (binding.userEditText.text.isNotEmpty() &&  binding.passwordEditText.text.isNotEmpty()){
                Firebase.auth.signInWithEmailAndPassword(binding.userEditText.text.toString(), binding.passwordEditText.text.toString()).addOnCompleteListener(){ task ->
                    if (task.isSuccessful){
                        showProfile(task.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert("1")
                    }
                }
            }
        }

        binding.googleSignInButton.setOnClickListener{
            val googleConfig: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleClient: GoogleSignInClient = GoogleSignIn.getClient(this, googleConfig)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, auth_google_id)
        }
    }

    private fun showAlert(cases: String){
        if (cases.equals("0"))
            Toast.makeText(this, "Se ha producido un error al crear el usuario, es posible que ya exista", Toast.LENGTH_SHORT).show()

        if (cases.equals("1"))
            Toast.makeText(this, "Contraseña incorrecta o usuario inexistente", Toast.LENGTH_SHORT).show()
    }

    private fun showProfile(email: String, provider: ProviderType){
        val profileIntent: Intent = Intent (this, ProfileActivity::class.java).apply{
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(profileIntent)
        finish()
    }

    private fun activeSessionComprobation (){
        val preferences: SharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email : String? = preferences.getString("email", null)
        val provider : String? = preferences.getString("provider", null)
        if ( email != null && provider != null){
            showProfile(provider, ProviderType.valueOf(provider))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode.equals(auth_google_id)){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                if (account != null){
                    val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{ task ->
                        if (task.isSuccessful){
                            showProfile(account.email ?: "", ProviderType.GOOGLE)
                        } else showAlert("0")

                    }
                }
            } catch (e: ApiException){
                showAlert("0")
            }
        }
    }

}