package com.example.yeticlicker

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_ranking.*

class
RankingActivity : AppCompatActivity() {
    private lateinit var rankingListAdapter: RankingListAdapter

    private val SP: String = "PRIVATE STORAGE"
    private val RANKING: String = "RANKING"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        val rankingList = readRanking()

        rankingListAdapter = RankingListAdapter(rankingList)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@RankingActivity)
            adapter = rankingListAdapter
        }

        val bundle: Bundle? = intent.extras
        bundle?.let {
            bundle.apply {
                val userName: String = getString("userName")
                val userTime: Long = getLong("timeSend")
                rankingListAdapter.addItem(Player(userTime, userName))
                saveRanking()
            }
        }
    }

    private fun readRanking(): Ranking {
        val sp: SharedPreferences = getSharedPreferences(SP, Context.MODE_PRIVATE)
        val json = sp.getString(RANKING, null)
        if(json != null) {
            val gson = Gson()
            val listType = object: TypeToken<ArrayList<Player>>() {}.type
            val listRanking: ArrayList<Player> = gson.fromJson(json, listType)
            return Ranking(listRanking)
        }
        return Ranking(ArrayList<Player>(0))
    }

    private fun saveRanking() {
        val sp: SharedPreferences.Editor? = getSharedPreferences(SP, Context.MODE_PRIVATE).edit()
        val gson = Gson()
        val json: String = gson.toJson(rankingListAdapter.getCollection())
        sp?.putString(RANKING, json)
        sp?.apply()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.back_in_new_activity,
            R.anim.back_out_new_activity
        )
    }
}