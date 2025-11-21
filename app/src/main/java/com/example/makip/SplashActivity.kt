package com.example.makip

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
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
    private lateinit var authManager: AuthManager // Instancia del gestor de autenticación
    private var isNavigationPending = false // Bandera para evitar doble navegación

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge() // Borde a Borde
        setContentView(R.layout.activity_splash)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- CORRECCIÓN: Iconos de barra de estado en BLANCO para fondo negro ---
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false // false = Iconos Blancos
        // ----------------------------------------------------------------------

        // Inicializar el gestor de autenticación
        authManager = AuthManager(this)

        // Inicializar managers de persistencia
        ProductManager.initialize(this)
        OrderManager.initialize(this)

        initializePlayerAndVideo()

        // Inicia la reproducción del video después de un breve retraso.
        Handler(Looper.getMainLooper())
                .postDelayed({ videoPlayer?.play() }, DELAY_BEFORE_PLAYBACK_MS)
    }

    /** Configura el ExoPlayer, el recurso de video y sus oyentes. */
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

    /** Aplica márgenes a la vista del reproductor para su posicionamiento y tamaño. */
    private fun applyVideoMargins(playerView: PlayerView) {
        val marginInPixels = (VIDEO_MARGIN_DP * resources.displayMetrics.density).toInt()
        val params = playerView.layoutParams as FrameLayout.LayoutParams
        params.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels)
        playerView.layoutParams = params
    }

    /** Define el recurso de video a reproducir y la velocidad. */
    private fun setupMediaItem() {
        val videoPath = "android.resource://$packageName/${R.raw.video_inicio}"
        val mediaItem = MediaItem.fromUri(Uri.parse(videoPath))

        videoPlayer?.setMediaItem(mediaItem)
        videoPlayer?.setPlaybackSpeed(PLAYBACK_SPEED)
    }

    /** Agrega un Listener para detectar el final de la reproducción del video. */
    private fun addVideoPlaybackListener() {
        videoPlayer?.addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            // Evitar múltiples llamadas si el evento se dispara varias veces.
                            if (isNavigationPending) return

                            // Navega a la siguiente pantalla después de una pequeña pausa final.
                            Handler(Looper.getMainLooper())
                                    .postDelayed(
                                            {
                                                // Llama a la nueva función que decide a dónde ir.
                                                decideNextActivity()
                                            },
                                            DELAY_AFTER_VIDEO_END_MS
                                    )

                            isNavigationPending =
                                    true // Marca la navegación como pendiente/en curso
                        }
                    }
                }
        )
    }

    /**
     * Decide si ir al Catálogo (MainActivity) o a la pantalla de Login, basándose en el estado de
     * autenticación.
     */
    private fun decideNextActivity() {
        val nextActivityClass =
                if (authManager.isLoggedIn()) {
                    CatalogoActivity::class.java // Usuario logueado: ir al catálogo
                } else {
                    LoginActivity::class.java // Usuario no logueado: ir a login
                }

        val intent = Intent(this, nextActivityClass)
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
