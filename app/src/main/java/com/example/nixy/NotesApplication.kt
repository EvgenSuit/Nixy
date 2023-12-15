package com.example.nixy

import android.app.Application
import android.content.Context

class NotesApplication: Application() {
    lateinit var context: Context
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}