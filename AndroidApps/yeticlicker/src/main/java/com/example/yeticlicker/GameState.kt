package com.example.yeticlicker

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.io.*


class GameState : Serializable {
    companion object {
        const val SP: String = "PRIVATE STORAGE"
        const val ISGAMESAVED: String = "game saved"
    }

    private val fileName: String = "/state.bin"
    var currentScore: Int = 0
    var maxScore: Int = 1000
    var click: Int = 1
    var autoCps: Double = 0.0
    var autoCpsMS: Long = 0 // w milisekundach
    var startTimer: Long = System.nanoTime()

    fun saveState(context: Context) {
        val sp: SharedPreferences.Editor? = context.getSharedPreferences(SP, Context.MODE_PRIVATE).edit()
        sp?.putBoolean(ISGAMESAVED, true)
        sp?.apply()

        ObjectOutputStream(FileOutputStream(File(context.filesDir.absolutePath + fileName))).use {
            it.writeObject(this)
        }
    }

    fun delete(context: Context) {
        val sp: SharedPreferences.Editor? = context.getSharedPreferences(SP, Context.MODE_PRIVATE).edit()
        sp?.putBoolean(ISGAMESAVED, false)
        sp?.apply()

        File(context.filesDir.absolutePath + fileName).delete()
    }
}