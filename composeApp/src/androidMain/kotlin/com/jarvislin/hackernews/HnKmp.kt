package com.jarvislin.hackernews

import initKoin
import android.app.Application


class HnKmp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
        instance = this
    }

    companion object {
        lateinit var instance: HnKmp
            private set
    }
}
