package com.example.ituneplayer

import android.graphics.Bitmap
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.KeyEvent
import android.widget.MediaController.MediaPlayerControl
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.ituneplayer.databinding.ActivityPreviewBinding
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import java.util.Objects
import android.media.MediaPlayer as AndroidMediaPlayer

class PreviewActivity : YouTubeBaseActivity(), android.widget.MediaController.MediaPlayerControl {

    private val YOUTUBE_API_KEY = "AIzaSyBYrFGbsrdBrh3vbTYZa_12zq9VNpqYCjs"

    private val title = ObservableField<String>()
    private val description = ObservableField<String>()
    private var url: String? = null

    private var isPlaying = false
    private var bufferPercentage = 0
    private val mediaPlayer = AndroidMediaPlayer()
    private val mediaController:android.widget.MediaController by lazy {
        object :android.widget.MediaController(this) {
            override fun show() {
                super.show(0)
            }

            override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
                if (event!!.keyCode == KeyEvent.KEYCODE_BACK) {
                    onBackPressed()
                }
                return super.dispatchKeyEvent(event)
            }
        }
    }

    private val lifecycleObserver = object: DefaultLifecycleObserver{
        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            try {
                mediaPlayer.setDataSource(url)
                mediaPlayer.setOnPreparedListener{
                    Log.i("Mediaplaer", "I'm ready...")
                    mediaPlayer.setOnCompletionListener {
                        isPlaying = false
                        mediaController.show()
                    }
                    mediaController.setAnchorView(binding.anchorView)
                    mediaController.setMediaPlayer(owner as MediaPlayerControl)
                    mediaController.show()
                    if(position >0)mediaPlayer.seekTo(position)
                    if (isPlaying) {
                        mediaPlayer.start()
                    }
                }
                mediaPlayer.setOnBufferingUpdateListener{
                        mediaPlayer, i-> bufferPercentage = i
                }
                mediaPlayer.prepareAsync()
            }catch (e: Exception) {
                e.printStackTrace()        }


        }

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            if(isPlaying) mediaPlayer.start()
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            if(isPlaying) mediaPlayer.pause()
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            mediaPlayer.release()
            mediaController.hide()
        }
    }

    init{
//        lifecycle.addObserver(lifecycleObserver)
    }




    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isPlaying", isPlaying)
        outState.putInt("currentPosition", currentPosition)
    }

    private lateinit var binding: ActivityPreviewBinding
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_preview)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_preview)

        title.set(intent.getStringExtra("title"))
        description.set(intent.getStringExtra("description"))
        url = intent.getStringExtra("url")
        val urlParser = url?.takeLast(11)
        binding.title = title
        binding.description = description
        if (savedInstanceState != null) {
            isPlaying = savedInstanceState.getBoolean("isPlaying")
            position = savedInstanceState.getInt("currentPosition")

        }
        val playerView = binding.playerView

        playerView.initialize(YOUTUBE_API_KEY, object: YouTubePlayer.OnInitializedListener{
            override fun onInitializationSuccess(
                p0: YouTubePlayer.Provider?,
                p1: YouTubePlayer?,
                p2: Boolean
            ) {

                p1?.loadVideo("$urlParser")
                p1?.play()
            }

            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {
                Toast.makeText(applicationContext, "Youtube init failed", Toast.LENGTH_LONG).show()
            }

        })
    }


    override fun start() {
        mediaPlayer.start()
        isPlaying = true
    }

    override fun pause() {
        mediaPlayer.pause()
        isPlaying = false
    }

    override fun getDuration(): Int {
        return mediaPlayer.currentPosition
    }

    override fun getCurrentPosition(): Int {
        return bufferPercentage
    }

    override fun seekTo(p0: Int) {
        mediaPlayer.seekTo(p0)
    }

    override fun isPlaying(): Boolean {
        return isPlaying
    }

    override fun getBufferPercentage(): Int {
        TODO("Not yet implemented")
    }

    override fun canPause(): Boolean {
        return true
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun getAudioSessionId(): Int {
        return mediaPlayer.audioSessionId
    }
}