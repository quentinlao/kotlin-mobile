package com.wuknow.pictionis

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var fbAuth = FirebaseAuth.getInstance()

    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions

    /***
     * Initilisation de l'activité
     * Lance la configuration de la connexion Google
     * Lance la 2e activité si l'email et le pass est bon
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureGoogleSignIn()
        setupUI()

        var email = findViewById<EditText>(R.id.email)
        var password = findViewById<EditText>(R.id.password)
        var btnLog = findViewById<Button>(R.id.connexion)
        btnLog.setOnClickListener { view ->
            if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
                signIn(view, "${email.text}", "${password.text}")
            }
        }

      /*  var btnLogin = findViewById<Button>(R.id.button)
        btnLogin.setOnClickListener {view ->
            signIn(view,"user@company.com", "pass123456")
        }*/

    }

    /***
     * Nommage de notre activité pour pouvoir faire des nav intent
     */
    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    /***
     * Quand l'applicaition a été arreté ou quand il a eu un redemarrage rallume la page logg si deja log
     */
    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            startActivity(LoggedInActivity.getLaunchIntent(this))
            finish()
        }
    }

    /***
     * Configure la connexion google avec le token Google
     */
    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    /***
     * Appel la connexion du bouton google_button
     */
    private fun setupUI() {
        google_button.setOnClickListener {
            signInGoogle()
        }
    }

    /***
     * Demarre la prochaine activité
     */
    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /***
     *  Retour des informations de l'intent avec la page de connexion avec google
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)

            } catch (e: ApiException) {
                Toast.makeText(this, "Probleme de connexion google", Toast.LENGTH_LONG).show()
            }
        }
    }

    /***
     * Connexion a firebase avec google ok
     */
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        fbAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity(LoggedInActivity.getLaunchIntent(this))
            } else {
                Toast.makeText(this, "Probleme de connexion google", Toast.LENGTH_LONG).show()
            }
        }
    }

    /***
     * Connexion sur firebase avec les champs email et mot de passe
     */
    fun signIn(view: View, email: String, password: String){
        showMessage(view,"Connexion...")
        fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
            if(task.isSuccessful){
                var intent = Intent(this, LoggedInActivity::class.java)
                intent.putExtra("id", fbAuth.currentUser?.email)
                startActivity(intent)

            }else{
                showMessage(view,"Probleme: ${task.exception?.message}")
            }
        })
    }
    fun showMessage(view:View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
    }


}
