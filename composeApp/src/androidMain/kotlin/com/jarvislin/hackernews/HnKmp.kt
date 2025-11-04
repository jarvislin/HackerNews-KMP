package com.jarvislin.hackernews

import android.app.Application


class HnKmp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: HnKmp
            private set
    }
}