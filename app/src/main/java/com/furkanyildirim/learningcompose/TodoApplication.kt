package com.furkanyildirim.learningcompose

import android.app.Application
import com.furkanyildirim.learningcompose.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@HiltAndroidApp
class TodoApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
        ensureAnonymousAuth()
    }

    private fun ensureAnonymousAuth() {
        CoroutineScope(Dispatchers.IO).launch {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                runCatching { auth.signInAnonymously().await() }
            }
        }
    }
}
