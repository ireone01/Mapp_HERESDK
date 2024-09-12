package com.example.app.ui.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.here.sdk.search.Place

class SearchResultAdapter(
    private var places: List<Place>,
    private var query: String = "",
    private val onPlaceClick: (Place) -> Unit
            ) :
    RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    fun updateData(newPlaces: List<Place>, newQuery: String) {
        places = newPlaces
        query = newQuery
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = places[position]
        holder.bind(place.title, query)

        holder.itemView.setOnClickListener{
            Log.e("setOnClickListener","oke click")
            onPlaceClick(place)
        }
    }

    override fun getItemCount(): Int {
        return places.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textView: TextView = itemView.findViewById(R.id.textview)
        fun bind(placeTitle: String, query: String) {
            val regex = "(?i)($query)"
            val highlightedText = placeTitle.replace(Regex(regex), "<b>$1</b>")
            Log.e("highlightedText","true")
            textView.text = HtmlCompat.fromHtml(highlightedText, HtmlCompat.FROM_HTML_MODE_COMPACT)

        }
    }
}
