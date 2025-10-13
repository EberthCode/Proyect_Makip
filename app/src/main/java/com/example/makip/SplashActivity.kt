package com.example.makip

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class SplashActivity : AppCompatActivity() {

    // Constantes claras para la gestión de tiempos y diseño.
    private companion object {
        const val DELAY_BEFORE_PLAYBACK_MS = 1000L
        const val DELAY_AFTER_VIDEO_END_MS = 1000L
        const val VIDEO_MARGIN_DP = 100
        const val PLAYBACK_SPEED = 0.95f
    }

    private var videoPlayer: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)

        initializePlayerAndVideo()

        // Inicia la reproducción del video después de un breve retraso.
        Handler(Looper.getMainLooper()).postDelayed({
            videoPlayer?.play()
        }, DELAY_BEFORE_PLAYBACK_MS)
    }

    /**
     * Configura el ExoPlayer, el recurso de video y sus oyentes.
     */
    private fun initializePlayerAndVideo() {
        val playerView = findViewById<PlayerView>(R.id.player_view)

        // Deshabilita los controles de reproducción por defecto de la vista.
        playerView.useController = false

        // Ajusta el margen del PlayerView para reducir y centrar el video.
        applyVideoMargins(playerView)

        // Crea y vincula el reproductor.
        videoPlayer = ExoPlayer.Builder(this).build()
        playerView.player = videoPlayer

        setupMediaItem()
        addVideoPlaybackListener()

        videoPlayer?.prepare()
    }

    /**
     * Aplica márgenes a la vista del reproductor para su posicionamiento y tamaño.
     */
    private fun applyVideoMargins(playerView: PlayerView) {
        val marginInPixels = (VIDEO_MARGIN_DP * resources.displayMetrics.density).toInt()
        val params = playerView.layoutParams as FrameLayout.LayoutParams
        params.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels)
        playerView.layoutParams = params
    }

    /**
     * Define el recurso de video a reproducir y la velocidad.
     */
    private fun setupMediaItem() {
        val videoPath = "android.resource://$packageName/${R.raw.video_inicio}"
        val mediaItem = MediaItem.fromUri(Uri.parse(videoPath))

        videoPlayer?.setMediaItem(mediaItem)
        videoPlayer?.setPlaybackSpeed(PLAYBACK_SPEED)
    }

    /**
     * Agrega un Listener para detectar el final de la reproducción del video.
     */
    private fun addVideoPlaybackListener() {
        videoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    // Navega al Login después de una pequeña pausa final.
                    Handler(Looper.getMainLooper()).postDelayed({
                        navigateToLogin()
                    }, DELAY_AFTER_VIDEO_END_MS)
                }
            }
        })
    }

    /**
     * Lanza la LoginActivity y finaliza la SplashActivity.
     */
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Libera los recursos del reproductor para evitar pérdidas de memoria.
        videoPlayer?.release()
        videoPlayer = null
    }
}