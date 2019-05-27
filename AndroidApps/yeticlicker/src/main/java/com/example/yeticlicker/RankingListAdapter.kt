package com.example.yeticlicker

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class RankingListAdapter(private val collection: Ranking) : RecyclerView.Adapter<RankingListAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val li = LayoutInflater.from(parent.context)
        val view = li.inflate(R.layout.row_in_ranking, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.nr.text = (position+1).toString() + "."
        val player: Player = collection.getPlayer(position)
        holder.name.text = player.userName

        val hours = TimeUnit.NANOSECONDS.toHours(player.time) % 24
        val minutes = TimeUnit.NANOSECONDS.toMinutes(player.time) % 60
        val seconds = TimeUnit.NANOSECONDS.toSeconds(player.time) % 60

        holder.time.text = "$hours : $minutes : $seconds"
    }

    fun addItem(item: Player) {
        val idx = collection.addPlayer(item)
        notifyItemInserted(idx)
    }

    override fun getItemCount(): Int = collection.size()

    class CustomViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var nr: TextView
        var name: TextView
        var time: TextView
        init {
            nr = item.findViewById(R.id.position)
            name = item.findViewById(R.id.name)
            time = item.findViewById(R.id.time)
        }
    }

    fun getCollection(): ArrayList<Player> {
        return collection.getListPlayers()
    }
}