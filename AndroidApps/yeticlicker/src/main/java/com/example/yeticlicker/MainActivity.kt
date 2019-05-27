package com.example.yeticlicker

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import java.io.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val CURRENT_SCORE: String = "current score"
    private val AUTO_CPS: String = "auto cps"
    private val AUTO_CPS_MS: String = "auto cps ms"
    private val CLICK: String = "click"
    private val START_TIMER: String = "start timer"
    private val BG: String = "BG"
    private val SP: String = "PRIVATE STORAGE"

    private var runInBg: Boolean = false
    private var isBackPressed: Boolean = false

    lateinit var imgToClick: ImageView
    lateinit var scoreTextView: TextView//tvPoint
    lateinit var cpsTextView: TextView

    val updateCpsMS: Long = 300 // co ile milisekund ma sie updatowac CPS
    var cps: Double = 0.0

    val gs by lazy { loadState() }

    var lastClick: Long = System.nanoTime()
    private val clickTimestamps: MutableList<Long> = mutableListOf()
    private val maxTimestamp: Long = 3 // sekundy co ile uśredniamy

    lateinit var bonusLabel: String
    var bonusValue: Double = 0.0
    var bonusPrice: Int = 0

    lateinit var shop: ImageView

    lateinit var progressBar: ProgressBar
    private var isUpward: Boolean = true
    private var multiplyClick: Int = 1
    private var progressStatus: Int = 0
    var handlerProgressBar: Handler? = null

    val handler: Handler = Handler()
    val cpsTask: Runnable = object: Runnable {
        override fun run() {
            runOnUiThread{ update()
                // Aktualizacja cps tylko jeśli cps jest większe od kupionego automatycznego kliknięcia
                if (cps > gs.autoCps)  handler.postDelayed(this, updateCpsMS)
            }
        }
    }
    val autoClickTask: Runnable = object: Runnable {
        override fun run() {
            runOnUiThread{
                autoUpdate()
                if(gs.currentScore < gs.maxScore) handler.postDelayed(this, gs.autoCpsMS)
            }
        }
    }

    lateinit var animation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yeti_main)

        filesDir.mkdir()

        val sp: SharedPreferences = getSharedPreferences(SP, Context.MODE_PRIVATE)
        runInBg = sp.getBoolean(BG, false)
        if(runInBg) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mChannel = NotificationChannel("com.example.clickerapp.DEFALUT", "General", NotificationManager.IMPORTANCE_DEFAULT)
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(mChannel)
            }

            registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    gs.currentScore = intent!!.getIntExtra(AutoClickService.CURRENT_SCORE, 0)
                    scoreTextView.text = gs.currentScore.toString()
                }
            }, IntentFilter("com.example.clickerapp.UPDATE"))
        }

        // Obsługa obracania ekranu (wczytanie zapisanego stanu)
        if(savedInstanceState != null) {
            gs.currentScore = savedInstanceState.getInt(CURRENT_SCORE)
            gs.autoCps = savedInstanceState.getDouble(AUTO_CPS)
            gs.autoCpsMS = savedInstanceState.getLong(AUTO_CPS_MS)
            gs.click = savedInstanceState.getInt(CLICK)
            gs.startTimer = savedInstanceState.getLong(START_TIMER)
        }

        scoreTextView = findViewById(R.id.score)
        cpsTextView = findViewById(R.id.cps)
        imgToClick = findViewById(R.id.imgToClick)
        shop = findViewById(R.id.shop)
        progressBar = findViewById(R.id.progressBar)

        scoreTextView.text = gs.currentScore.toString()
        cpsTextView.text = String.format("%.2f per second", Math.max(cps, gs.autoCps))

        animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.image_animation)

        imgToClick.setOnClickListener(View.OnClickListener {
            animation.setAnimationListener(object: CustomAnimationListener() {
            override fun onAnimationEnd(a: Animation?) {
                handler.removeCallbacks(cpsTask)
                mainImgClick()
                handler.postDelayed(cpsTask, updateCpsMS)
            }
        })
            imgToClick.startAnimation(animation)
        })

        shop.setOnClickListener{
            shop.isEnabled = false
            val intentShop : Intent = Intent(this@MainActivity, ShopActivity::class.java).apply {
                putExtra("SCORE", gs.currentScore)
            }
            startActivityForResult(intentShop, 0)
        }

        handlerProgressBar = Handler(Handler.Callback {
            if(isUpward) progressStatus++ else progressStatus--

            progressBar.progress = progressStatus

            if(progressStatus == progressBar.max) {
                isUpward = false
                multiplyClick = 10
            }
            if(progressStatus == 0) {
                isUpward = true
                multiplyClick = 1
            }

            if(isUpward) handlerProgressBar?.sendEmptyMessageDelayed(0, 400)
            else  handlerProgressBar?.sendEmptyMessageDelayed(0, 100)

            true
        })
        handlerProgressBar?.sendEmptyMessage(0)
    }

    private fun mainImgClick() {
        val now: Long = System.nanoTime()
        lastClick = now
        clickTimestamps.add(now)
        clickTimestamps.removeAll { c -> c < now.minus(maxTimestamp.times(1000000000)) }
        cps = clickTimestamps.size.toDouble().div(maxTimestamp)
        cpsTextView.text = String.format("%.2f per second", Math.max(cps, gs.autoCps))

        gs.currentScore += (gs.click.times(multiplyClick))
        scoreTextView.text = gs.currentScore.toString()
        if(gs.currentScore >= gs.maxScore) win()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        shop.isEnabled = true
        if(requestCode == 0 && resultCode == 1) {
            data?.let {
                bonusLabel = it.getStringExtra("bonusLabel")
                bonusValue = it.getDoubleExtra("bonusValue", 0.0)
                bonusPrice = it.getIntExtra("bonusPrice", 0)
                runOnUiThread {
                    handler.post(shopTask)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private var shopTask = object: TimerTask() {
        override fun run() {
            if(bonusLabel.compareTo("cps") == 0) {
                handler.removeCallbacks(autoClickTask)

                gs.autoCps += bonusValue
                gs.autoCpsMS = ((1.0.div(gs.autoCps)).times(1000.0)).toLong()
                cpsTextView.text = String.format("%.2f per second", Math.max(cps, gs.autoCps))

                handler.postDelayed(autoClickTask, gs.autoCpsMS) // co ile milisekund ma sie odswiezac
            } else {
                gs.click = bonusValue.toInt()
            }
            gs.currentScore -= bonusPrice
            scoreTextView.text = gs.currentScore.toString()
        }
    }

    private fun update() {
        val now: Long = System.nanoTime()
        clickTimestamps.removeAll { c -> c < now.minus(maxTimestamp.times(1000000000)) } // wyrzucamy starsze chwile > 3s
        cps = clickTimestamps.size.toDouble().div(maxTimestamp) // srednia
        cpsTextView.text = String.format("%.2f per second", Math.max(cps, gs.autoCps))
    }

    private fun autoUpdate() {
        val now = System.nanoTime()
        lastClick = now
        clickTimestamps.add(now)
        gs.currentScore += gs.click
        scoreTextView.text = gs.currentScore.toString()
        if(gs.currentScore >= gs.maxScore) win()
    }

    private fun win() {
        stopService(Intent(this@MainActivity, AutoClickService::class.java))
        handler.removeCallbacks(autoClickTask)
        handler.removeCallbacks(cpsTask)

        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity);
        val inflater: LayoutInflater = LayoutInflater.from(this@MainActivity)

        val v: View = inflater.inflate(R.layout.winner_dialog, null)
        val userName: EditText = v.findViewById(R.id.userName) as EditText
        val userTimeSend: Long = System.nanoTime() - gs.startTimer

        builder.setView(v)
        builder.setPositiveButton(R.string.save, ({ _: DialogInterface, _: Int -> //DialogInterface.OnClickListener
            val userNameSend: String = userName.text.toString()
            val intent: Intent = Intent(this@MainActivity, RankingActivity::class.java).apply {
                action = Intent.ACTION_SEND
                putExtra("userName", userNameSend)
                putExtra("timeSend", userTimeSend)
                type = "*/*"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            gs.delete(this)
            startActivity(intent)
            finish()
        }))
        builder.setNegativeButton(R.string.cancel, ({ _: DialogInterface, _: Int ->
            finish()
            overridePendingTransition(
                R.anim.back_in_new_activity,
                R.anim.back_out_new_activity
            )
        }))
        builder.setCancelable(false).create().show()
    }

    override fun onBackPressed() {
        isBackPressed = true

        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity);
        val inflater: LayoutInflater = LayoutInflater.from(this@MainActivity)

        val v: View = inflater.inflate(R.layout.save_state_dialog, null)

        builder.setView(v)
        builder.setPositiveButton(R.string.yes, ({ _: DialogInterface, _: Int -> //DialogInterface.OnClickListener
            gs.saveState(this)
            super.onBackPressed()
            overridePendingTransition(
                R.anim.back_in_new_activity,
                R.anim.back_out_new_activity
            )
        }))
        builder.setNegativeButton(R.string.no, ({ _: DialogInterface, _: Int ->
            gs.delete(this)
            super.onBackPressed()
            overridePendingTransition(
                R.anim.back_in_new_activity,
                R.anim.back_out_new_activity
            )
        }))
        builder.setCancelable(false).create().show()
    }

    private fun loadState(): GameState {
        try {
            ObjectInputStream(FileInputStream(File(filesDir.absolutePath + "/state.bin"))).use {
                return (it.readObject() as GameState)
            }
        } catch (e: Exception) {
            return GameState()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt(CURRENT_SCORE, gs.currentScore)
        outState?.putDouble(AUTO_CPS, gs.autoCps)
        outState?.putLong(AUTO_CPS_MS, gs.autoCpsMS)
        outState?.putInt(CLICK, gs.click)
        outState?.putLong(START_TIMER, gs.startTimer)
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        if (gs.autoCps > 0) {
            handler.removeCallbacks(autoClickTask)
            if(runInBg && !isBackPressed && gs.currentScore != gs.maxScore) {
                val intent = Intent(this@MainActivity, AutoClickService::class.java)
                intent.putExtra(AutoClickService.AUTO_CLICK_MS, gs.autoCpsMS)
                intent.putExtra(AutoClickService.CURRENT_SCORE, gs.currentScore)
                intent.putExtra(AutoClickService.MAX_SCORE, gs.maxScore)
                intent.putExtra(AutoClickService.CLICK, gs.click)
                startService(intent)
            }
        }
        super.onStop()
    }

    override fun onStart() {
        if (gs.autoCpsMS > 0) {
            if (runInBg) {
                if (gs.currentScore < gs.maxScore) {
                    stopService(Intent(this@MainActivity, AutoClickService::class.java))
                    handler.postDelayed(autoClickTask, gs.autoCpsMS)
                } else {
                    win()
                }
            } else if (gs.currentScore < gs.maxScore){
                handler.postDelayed(autoClickTask, gs.autoCpsMS)
            }
        }
        super.onStart()
    }

    override fun onDestroy() {
        if(gs.autoCpsMS > 0) {
            stopService(Intent(this@MainActivity, AutoClickService::class.java))
        }
        super.onDestroy()
    }
}