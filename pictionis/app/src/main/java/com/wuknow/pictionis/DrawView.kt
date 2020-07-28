package com.wuknow.pictionis

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.google.firebase.database.*


class DrawView(context: Context) : View(context) {
    // l'endroit precis pour l'écriture dans la db
    private lateinit var database: DatabaseReference

    private var mPaint = Paint()
    private var mPath = Path()

    private var mCurX = 0f
    private var mCurY = 0f
    private var mStartX = 0f
    private var mStartY = 0f

    /***
     * initilisation de fb, du pinceau, listener fb pour le changement redraw
     */
    init {
        database = FirebaseDatabase.getInstance().reference

        mPaint.apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 8f
            isAntiAlias = true
        }

        val dessinListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    val tasks = dataSnapshot.children.iterator()

                    if (tasks.hasNext()) {


                        val listIndex = tasks.next()
                        val itemsIterator = listIndex.children.iterator()

                        //verifie si la collection à une task
                        while (itemsIterator.hasNext()) {

                            val currentItem = itemsIterator.next()

                            val map = currentItem.getValue() as Map<*, *>
                            if (map != null) {
                                for ((key,value) in map){
                                    Log.d("Dessine","$key = $value")
                                    val x : Float = map.get("x").toString().toFloat()
                                    val y : Float = map.get("y").toString().toFloat()
                                    val move : String = map.get("move").toString()
                                    if(move == "up"){
                                        actionUp()
                                    } else if (move == "down") {
                                        mStartX = x
                                        mStartY = y
                                        actionDown(x, y)
                                    } else if (move == "move") {
                                        actionMove(x, y)
                                    }
                                    invalidate()
                                }
                            }
                        }
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        database.addValueEventListener(dessinListener)

    }

    /***
     * Dessine sur le canva
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawPath(mPath, mPaint)
    }

    /***
     * quand on appuie rajoute le mouvement de x à y
     */
    private fun actionDown(x: Float, y: Float) {
        mPath.moveTo(x, y)
        mCurX = x
        mCurY = y
    }

    /***
     * quand on fait un mouvement quadratic curve pour faire lisse
     */
    private fun actionMove(x: Float, y: Float) {
        mPath.quadTo(mCurX, mCurY, (x + mCurX) / 2, (y + mCurY) / 2)
        mCurX = x
        mCurY = y
    }

    /***
     * fait le deplacement jusqu'a la ligne de mCurX à mCurY
     */
    private fun actionUp() {
        mPath.lineTo(mCurX, mCurY)

        // draw a dot on click
        if (mStartX == mCurX && mStartY == mCurY) {
            mPath.lineTo(mCurX, mCurY + 2)
            mPath.lineTo(mCurX + 1, mCurY + 2)
            mPath.lineTo(mCurX + 1, mCurY)
        }
    }

    /**
     * Supprime tout dans la collection
     */
    fun clearDessin(){
        mPath.reset()
        database.removeValue()
        invalidate()
    }

    /***
     * action sur l'ecran
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                val p = Point(x,y,"down")
                Log.d("TAG X", "$x")

                val dbId = database.child("dessin").push()
                val id = dbId.key
                if (id != null) {
                    database.child("dessin").child(id).setValue(p)
                }
                mStartX = x
                mStartY = y
                actionDown(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                val p = Point(x,y,"move")
                Log.d("TAG X", "$x")

                val dbId = database.child("dessin").push()
                val id = dbId.key
                if (id != null) {
                    database.child("dessin").child(id).setValue(p)
                }
                actionMove(x, y)
            }
            MotionEvent.ACTION_UP -> {
                val p = Point(x,y,"up")
                Log.d("TAG X", "$x")

                val dbId = database.child("dessin").push()
                val id = dbId.key
                if (id != null) {
                    database.child("dessin").child(id).setValue(p)
                }
                actionUp()
            }

        }
        invalidate()
        return true
    }
}