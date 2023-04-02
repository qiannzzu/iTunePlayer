package com.example.ituneplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ituneplayer.databinding.ItuneListItemBinding

class iTuneRecyclerViewAdapter(data: List<SongData>, val listener: RecyclerViewClickListener): RecyclerView.Adapter<iTuneRecyclerViewAdapter.ViewHolder>() {
    var songs = data
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    interface RecyclerViewClickListener {
        fun onItemClick(view: View, position: Int)
    }

    class ViewHolder(val binding: ItuneListItemBinding): RecyclerView.ViewHolder(binding.root){
//        val textView = view.findViewById<TextView>(R.id.txtView)
//        val imageView = view.findViewById<ImageView>(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(
//            R.layout.itune_list_item, parent, false)
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItuneListItemBinding.inflate(inflater)
        val holder  = ViewHolder(binding)
        return holder
    }

    override fun getItemCount(): Int {
        return  songs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.textView.text = songs[position].title
//        holder.imageView.setImageBitmap((songs[position].cover))
        holder.binding.songData = songs[position]
        holder.itemView.setOnClickListener{
            listener.onItemClick(it, position)
        }
        holder.binding.executePendingBindings()
    }
}