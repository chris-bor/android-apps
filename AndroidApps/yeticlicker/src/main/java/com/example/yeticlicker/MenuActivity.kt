package com.example.yeticlicker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout

class MenuActivity : AppCompatActivity() {

    private lateinit var linearLayoutUp: LinearLayout
    private lateinit var linearLayoutDown: LinearLayout

    private lateinit var btnNewGame: Button
    private lateinit var btnRanking: Button
    private lateinit var btnOptions: Button
    private lateinit var btnExit: Button

    private lateinit var animUpToDown: Animation
    private lateinit var animDownToUp: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        linearLayoutUp = findViewById(R.id.lLayout1)
        linearLayoutDown = findViewById(R.id.lLayout2)

        btnNewGame = findViewById(R.id.button_NewGame)
        btnRanking = findViewById(R.id.button_Ranking)
        btnOptions = findViewById(R.id.button_Options)
        btnExit = findViewById(R.id.button_Exit)

        animUpToDown = AnimationUtils.loadAnimation(this, R.anim.welcome_up_to_down)
        animDownToUp = AnimationUtils.loadAnimation(this, R.anim.welcome_down_to_up)

        linearLayoutUp.animation = animUpToDown
        linearLayoutDown.animation = animDownToUp

        btnNewGame.setOnClickListener( {startNewGame()} )
        btnRanking.setOnClickListener( {showRanking()} )
        btnOptions.setOnClickListener( {showOptions()} )
        btnExit.setOnClickListener( {exitGame()} )
    }

    private fun startNewGame() {
        var intentStartGame : Intent = Intent(this@MenuActivity, MainActivity::class.java)
        btnNewGame.isEnabled = false
        startActivity(intentStartGame)
        customTransitionActivity()
    }

    private fun showRanking() {
        var intentRanking : Intent = Intent(this@MenuActivity, RankingActivity::class.java)
        btnRanking.isEnabled = false
        startActivity(intentRanking)
        customTransitionActivity()
    }

    private fun showOptions() {
        var intentOptions : Intent = Intent(this@MenuActivity, OptionsActivity::class.java)
        btnOptions.isEnabled = false
        startActivity(intentOptions)
        customTransitionActivity()
    }

    private fun exitGame() {
        finishAffinity()
    }

    private fun customTransitionActivity() {
        overridePendingTransition(
            R.anim.slide_in_new_activity,
            R.anim.slide_out_new_activity
        )
    }

    override fun onResume() {
        super.onResume()
        btnNewGame.isEnabled = true
        btnRanking.isEnabled = true
        btnOptions.isEnabled = true

        val sp: SharedPreferences = getSharedPreferences(GameState.SP, Context.MODE_PRIVATE)
        val isGameSaved = sp.getBoolean(GameState.ISGAMESAVED, false)
        if(isGameSaved) btnNewGame.text = "CONTINUE" else btnNewGame.text = "NEW GAME"
    }
}