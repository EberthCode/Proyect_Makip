package com.example.makip

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Application class que asegura que las barras del sistema siempre sean visibles
 */
class MakipApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {
                try {
                    // SOLO forzar que las barras sean visibles
                    // El tema y fitsSystemWindows en XML manejan el resto
                    val controller = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
                    controller.show(WindowInsetsCompat.Type.systemBars())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}
