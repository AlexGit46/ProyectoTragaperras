package net.azarquiel.tragaperras

import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {
    private var isGaming: Boolean = false
    private var credito=4
    private lateinit var ivs: Array<ImageView>
    var anisIDs = arrayOf(R.drawable.ani1,R.drawable.ani2,R.drawable.ani3)
    private lateinit var anis: Array<AnimationDrawable?>
    private var jugada = IntArray(3) {0}
    private var figuras = arrayListOf("campana","cereza","dolar","siete","fresa","limon")
    private lateinit var mp: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadResources()
        ibsa.setOnClickListener(View.OnClickListener { v -> ibsaOnClick(v) })

        title="Tragaperras"
    }

    private fun loadResources() {
        ivs = arrayOf(iv1,iv2,iv3)
        anis = arrayOfNulls<AnimationDrawable>(3)
        for ((i,iv) in ivs.withIndex()){
            iv.setBackgroundResource(anisIDs[i])
            anis[i]=iv.background as AnimationDrawable
            anis[i]!!.start()
        }
        mp = MediaPlayer.create(this,R.raw.maquina)
        showCredito()
    }

    private fun ibsaOnClick(v: View) {
        if (isGaming) return
        isGaming = true
        mp.start()
        for ((i,iv) in ivs.withIndex()){
            iv.setImageResource(android.R.color.transparent)
            jugada[i]=(Math.random()*6).toInt()
            doAsync {
                var result = runLongTask(400*(i+1),i)
                uiThread {
                    tapa(result)
                }
            }
        }
    }

    private fun runLongTask(time:Int, i:Int): Int {
        SystemClock.sleep(time.toLong())
        return i
    }

    private fun tapa(i: Int) {
        val id = resources.getIdentifier(figuras[jugada[i]],"drawable",packageName)
        ivs[i].setImageResource(id)
        if (i==ivs.size-1) {
            mp.pause()
            updateCredito()
            isGaming = false
        }
    }

    private fun updateCredito() {
        if (isIguales()){
            if (jugada[0]==2) // dolar
                credito +=50
            else
                credito+=10
        }
        else
            credito-=1
        showCredito()
        checkGameOver()
    }

    private fun checkGameOver() {
        if (credito==0){
            alert(R.string.bancarota,R.string.gameover) {
                yesButton { }
                noButton { }
            }.show()
            toast(R.string.bancarota)
            ibsa.setOnClickListener(null)
        }
    }

    private fun showCredito() {
        tvcredito.setText("${credito}")
    }

    private fun isIguales(): Boolean {
        val n = jugada[0]
        for (i in 1 until jugada.size) {
            if (n!=jugada[i])
                return false
        }
        return true
    }
}
