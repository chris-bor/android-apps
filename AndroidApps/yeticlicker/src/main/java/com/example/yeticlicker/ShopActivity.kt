package com.example.yeticlicker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView

class ShopActivity : AppCompatActivity() {
    lateinit var closeShopButton: TextView

    private val bonusLabel = listOf("click x 2", "+0.1 cps", "click x 3", "+5 cps", "+20 cps", "+6 cps", "click x 24", "+10 cps")
    private val bonusValue = listOf(2.0, 0.1, 3.0, 5.0, 20.0, 6.0, 24.0, 10.0)
    private val bonusPrice = listOf(4, 13, 18, 20, 34, 45, 55, 67)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
        setActivityParams()

        val bonusValueArray = listOf(
            findViewById<TextView>(R.id.bonusTV1),
            findViewById<TextView>(R.id.bonusTV2),
            findViewById<TextView>(R.id.bonusTV3),
            findViewById<TextView>(R.id.bonusTV4),
            findViewById<TextView>(R.id.bonusTV5),
            findViewById<TextView>(R.id.bonusTV6),
            findViewById<TextView>(R.id.bonusTV7),
            findViewById<TextView>(R.id.bonusTV8)
        )

        val bonusArray: HashMap<Int, ImageView> = hashMapOf(
            0 to findViewById<ImageView>(R.id.bonusView1),
            1 to findViewById<ImageView>(R.id.bonusView2),
            2 to findViewById<ImageView>(R.id.bonusView3),
            3 to findViewById<ImageView>(R.id.bonusView4),
            4 to findViewById<ImageView>(R.id.bonusView5),
            5 to findViewById<ImageView>(R.id.bonusView6),
            6 to findViewById<ImageView>(R.id.bonusView7),
            7 to findViewById<ImageView>(R.id.bonusView8)
        )

        val bonusPriceArray = listOf(
            findViewById<TextView>(R.id.bonusPrice1),
            findViewById<TextView>(R.id.bonusPrice2),
            findViewById<TextView>(R.id.bonusPrice3),
            findViewById<TextView>(R.id.bonusPrice4),
            findViewById<TextView>(R.id.bonusPrice5),
            findViewById<TextView>(R.id.bonusPrice6),
            findViewById<TextView>(R.id.bonusPrice7),
            findViewById<TextView>(R.id.bonusPrice8)
        )

        closeShopButton = findViewById(R.id.closeShop)

        bonusValueArray.forEachIndexed { idx, b -> b.text = bonusLabel[idx] }
        bonusPriceArray.forEachIndexed { idx, b -> b.text = (bonusPrice[idx]).toString() }

        var currentScore: Int = 0
        intent?.let {
            currentScore = intent.getIntExtra("SCORE", 0)
        }

        bonusArray.forEach{(key, value) ->
            if(bonusPrice[key] <= currentScore) {
                value.isEnabled = true
                value.setOnClickListener{
                    val viewId = it.id
                    val bonus = bonusArray.filter { (_, v) -> viewId == v.id }
                    if (bonus.size != 1) return@setOnClickListener
                    val bonusId = bonus.keys.first()
                    var bLabel: String = bonusLabel[bonusId]
                    bLabel = if(bLabel.contains("cps")) "cps" else "click"

                    val bValue = bonusValue[bonusId]
                    val bPrice = bonusPrice[bonusId]

                    val i = Intent()
                    i.putExtra("bonusLabel", bLabel)
                    i.putExtra("bonusValue", bValue)
                    i.putExtra("bonusPrice", bPrice)
                    setResult(1, i)
                    finish()
                }
            } else {
                value.isEnabled = false
                value.alpha = 0.5f
            }
        }
        closeShopButton.setOnClickListener{
            finish()
        }
    }

    private fun setActivityParams() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

//        var width: Int = dm.widthPixels
        val height: Int = dm.heightPixels

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (height * 0.8).toInt())
        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.CENTER
    }
}