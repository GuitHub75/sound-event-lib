package com.example.soundeventlib

import android.app.Application
import io.github.eescobar.soundeventlib.PlaybackMode
import io.github.eescobar.soundeventlib.SoundConfig
import io.github.eescobar.soundeventlib.SoundManager

class SoundDemoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        SoundManager.init(
            context = this,
            soundConfig = SoundConfig.Builder()
                .volume(0.9f)
                .playbackMode(PlaybackMode.OVERLAP)
                .build()
        )
    }

    override fun onTerminate() {
        SoundManager.release()
        super.onTerminate()
    }
}
