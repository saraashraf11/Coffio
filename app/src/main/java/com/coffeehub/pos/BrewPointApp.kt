package com.coffeehub.pos

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BrewPointApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
