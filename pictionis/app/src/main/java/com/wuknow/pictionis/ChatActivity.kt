package com.wuknow.pictionis

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.ArrayList


class ChatActivity : AppCompatActivity() {
    // l'endroit precis pour l'écriture dans la db
    private lateinit var database: DatabaseReference
    var fbAuth = FirebaseAuth.getInstance()

    /***
     * precise dans la collection messages
     */
    init {
        database = FirebaseDatabase.getInstance().reference.child("messages")
    }

    /***
     * Recupere les messages  et envoie le message
     */
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        var message = findViewById<EditText>(R.id.message)
        var send = findViewById<ImageView>(R.id.sendbutton)
        send.setOnClickListener { view ->

           if(message.text.isNotEmpty()){
               val m = Message("${message.text}","${fbAuth.currentUser?.email}")
               val dbId = database.push()
               val id = dbId.key
               if (id != null) {
                   database.child(id).setValue(m)
               }
           } else {
               Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
           }
        }

        var back = findViewById<Button>(R.id.back)
        back.setOnClickListener{
            finish()
            startActivity(LoggedInActivity.getLaunchIntent(this))
        }
        val messageListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val toReturn: ArrayList<Message> = ArrayList()

                for(data in dataSnapshot.children){
                    val messageData = data.getValue<Message>(Message::class.java)

                    //unwrap
                    val message = messageData?.let { it } ?: continue

                    toReturn.add(message)
                }

                toReturn.sortBy { message ->
                    message.timestamp
                }

                setupAdapter(toReturn)



            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        database.addValueEventListener(messageListener)


    }

    /***
     * affichage de chaque ligne pour le scroll des messages
     */
    private fun setupAdapter(data: ArrayList<Message>){
        val linearLayoutManager = LinearLayoutManager(this)
        mainActivityRecyclerView.layoutManager = linearLayoutManager
        mainActivityRecyclerView.adapter = MessageAdapter(data)

        mainActivityRecyclerView.scrollToPosition(data.size - 1)
    }
    /***
     * Nommage de notre activité pour pouvoir faire des nav intent
     */
    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, ChatActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }
}