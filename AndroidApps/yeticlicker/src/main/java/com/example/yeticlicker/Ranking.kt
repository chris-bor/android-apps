package com.example.yeticlicker

class Ranking(private var listPlayers: ArrayList<Player>) {

    fun addPlayer(player: Player): Int {
        listPlayers.add(player)
        listPlayers.sortBy { p -> p.time }
        return listPlayers.firstIndex { it.time == player.time}
    }

    fun getPlayer(id: Int): Player {
        return listPlayers[id]
    }

    private inline fun <T> Iterable<T>.firstIndex(predicate: (T) -> Boolean): Int {
        return this.mapIndexed { index, item -> Pair(index, item) }
            .first(){ predicate(it.second) }
            .first
    }

    fun size(): Int {
        return listPlayers.size
    }

    fun getListPlayers(): ArrayList<Player> {
        return listPlayers
    }
}