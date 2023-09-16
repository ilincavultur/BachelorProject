package com.example.digitaldiaryba.dependencyinjection

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DigitalDiaryApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}