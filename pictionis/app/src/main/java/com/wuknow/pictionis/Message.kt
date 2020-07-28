package com.wuknow.pictionis

class Message {

    constructor()

    constructor(messageText: String,username: String){
        text = messageText
        name = username
    }
    constructor(messageText: String,username: String, time: Long ){
        text = messageText
        name = username
        timestamp = time
    }
    var text: String? = null
    var name: String? = null
    var timestamp: Long = System.currentTimeMillis()
}
