package com.wuknow.pictionis

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Button
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_logged.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class LoggedInActivity : AppCompatActivity() {
    var fbAuth = FirebaseAuth.getInstance()

    /***
     * Creation de la vue draw et ajout des actions pour chaque bouton
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged)

        var name = findViewById<TextView>(R.id.Username)
        name.text = fbAuth.currentUser?.email

        var params : LayoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )


        val drawView: DrawView = DrawView(this)
        drawView.layoutParams = params

        root_layout.addView(drawView)
        var btnLogOut = findViewById<Button>(R.id.logout)

        btnLogOut.setOnClickListener{ view ->
            showMessage(view, "Logging Out...")
            signOut()
        }
        var btnClear = findViewById<Button>(R.id.clear)

        btnClear.setOnClickListener{ view ->
            showMessage(view, "Clear Out...")
            drawView.clearDessin()
        }

        var btnRandom = findViewById<Button>(R.id.random)

        btnRandom.setOnClickListener{ view ->
            showMessage(view, "Dessine le fruit")
            randomWord()
        }

        fbAuth.addAuthStateListener {
            if(fbAuth.currentUser == null){
                this.finish()
            }
        }

        var btnChat = findViewById<Button>(R.id.chat)
        btnChat.setOnClickListener{
            view ->  startActivity(ChatActivity.getLaunchIntent(this))
        }


    }

    /***
     * Requete sur une API de fruit, random pour dessiner
     */
    fun randomWord(){
        var randomString = findViewById<TextView>(R.id.Random)
        val rnds = (0..9).random()

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "https://api.predic8.de/shop/products/"
        // Request a string response from the provided URL.
        val stringReq = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->

                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                val jsonArray: JSONArray = jsonObj.getJSONArray("products")

                val fruit = jsonArray.getJSONObject(rnds).get("name")

                randomString!!.text = "Tu dois faire : $fruit "
            },
            Response.ErrorListener {"Probleme API" })
        queue.add(stringReq)
    }

    /***
     * Deconnexion
     */
    fun signOut(){
        startActivity(MainActivity.getLaunchIntent(this))
        fbAuth.signOut()

    }

    fun showMessage(view: View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
    }

    /***
     * Nommage de notre activité pour pouvoir faire des nav intent
     */
    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, LoggedInActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

}
