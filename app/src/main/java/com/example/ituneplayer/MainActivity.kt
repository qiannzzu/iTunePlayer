package com.example.ituneplayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.ituneplayer.databinding.ActivitySwipeFreshBinding

class MainActivity : AppCompatActivity(), iTuneRecyclerViewAdapter.RecyclerViewClickListener {
    val adapter: iTuneRecyclerViewAdapter by lazy {
        iTuneRecyclerViewAdapter(listOf<SongData>(), this)
    }

    val swipeRefreshLayout by lazy {
//        findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        binding.swipeRefreshLayout
    }


    private fun loadList(){
        iTuneXMLParser(object  : ParseListener{
            override fun start() {
                swipeRefreshLayout.isRefreshing = true
            }

            override fun finish(songs: List<SongData>) {
                adapter.songs = songs
                swipeRefreshLayout.isRefreshing = false
            }

        }).parseURL(
            "https://www.youtube.com/feeds/videos.xml?channel_id=UCupvZG-5ko_eiXAupbDfxWw")
    }

    lateinit var binding: ActivitySwipeFreshBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_swipe_fresh)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_swipe_fresh)

        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(
            recyclerView.context,
            DividerItemDecoration.VERTICAL
        ))

        swipeRefreshLayout.setOnRefreshListener{
            loadList()
        }
        loadList()

    }

    override fun onItemClick(view: View, position: Int) {
        Toast.makeText(this, adapter.songs[position].title, Toast.LENGTH_LONG).show()
        val intent = Intent(this, PreviewActivity::class.java)
        val song = adapter.songs[position]
        intent.putExtra("title", song.title)
//        intent.putExtra("cover", song.cover)
        intent.putExtra("url", song.url)
        startActivity(intent)
    }
}