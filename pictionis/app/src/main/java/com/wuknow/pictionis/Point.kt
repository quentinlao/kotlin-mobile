package com.wuknow.pictionis

class Point{
    var x = 0f
    var y = 0f
    var move = ""

    /**
     * Constructeur vide pour l'appel firebase d'un point
     */
    constructor(){
    }
    constructor( x: Float, y: Float, move : String){
        this.x = x
        this.y = y
        this.move = move
    }

}