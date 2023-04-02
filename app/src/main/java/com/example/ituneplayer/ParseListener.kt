package com.example.ituneplayer

interface ParseListener {
    fun start()
    fun finish(songs:  List<SongData>)
}