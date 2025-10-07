package com.example.makip

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.FrameLayout // <-- 1. IMPORTACIÓN NECESARIA
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class SplashActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)

        // 1. Preparamos el reproductor ExoPlayer
        setupPlayer()

        // 2. Lógica de retrasos que ya conoces
        // Espera 1 segundo antes de empezar a reproducir
        Handler(Looper.getMainLooper()).postDelayed({
            player?.play()
        }, 1000)
    }

    private fun setupPlayer() {
        // Busca el PlayerView en el layout
        val playerView = findViewById<PlayerView>(R.id.player_view)

        // LÍNEA CLAVE PARA OCULTAR LOS CONTROLES
        playerView.useController = false

        // --- 2. CÓDIGO AÑADIDO PARA REDUCIR Y CENTRAR EL VIDEO ---
        val marginInDp = 100 // <-- ¡Puedes cambiar este valor! (ej. 80, 120, etc.)
        val marginInPixels = (marginInDp * resources.displayMetrics.density).toInt()
        val params = playerView.layoutParams as FrameLayout.LayoutParams
        params.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels)
        playerView.layoutParams = params
        // --- FIN DEL CÓDIGO AÑADIDO ---

        // Crea una instancia de ExoPlayer
        player = ExoPlayer.Builder(this).build()
        playerView.player = player // Vincula el reproductor a la vista

        // Crea el item de video que se va a reproducir
        val path = "android.resource://$packageName/${R.raw.video_inicio}"
        val mediaItem = MediaItem.fromUri(Uri.parse(path))
        player?.setMediaItem(mediaItem)

        // Establece la velocidad de reproducción
        player?.setPlaybackSpeed(0.88f)

        // Prepara el reproductor
        player?.prepare()

        // Añade un "oyente" para saber cuándo termina el video
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                // Comprueba si el video ha terminado
                if (playbackState == Player.STATE_ENDED) {
                    // Espera 1 segundo después de terminar
                    Handler(Looper.getMainLooper()).postDelayed({
                        navigateToMain()
                    }, 1000)
                }
            }
        })
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
