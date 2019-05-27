package com.example.androidapps

import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class MenuListAdapter : RecyclerView.Adapter<MenuListAdapter.CustomViewHolder>() {
    private val stringList = listOf(
            R.string.yeticlicker_button,
            R.string.shopping_list_button,
            R.string.draw_button,
            R.string.api_button,
            R.string.view_button
    )
    private val classList = listOf(
            com.example.yeticlicker.MenuActivity::class.java,
            ShoppingListActivity::class.java,
            DrawActivity::class.java,
            ApiActivity::class.java,
            ViewActivity::class.java)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val li = LayoutInflater.from(parent.context)
        val view = li.inflate(R.layout.row_in_menu, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.menuButton.text = holder.menuButton.context.getString(stringList[position])
        holder.menuButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(it.context, classList[position])
            it.context.startActivity(intent)
        })
    }

    override fun getItemCount(): Int = classList.size

    class CustomViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var menuButton: Button
        init {
            menuButton = item.findViewById(R.id.menuButton)
        }
    }
}