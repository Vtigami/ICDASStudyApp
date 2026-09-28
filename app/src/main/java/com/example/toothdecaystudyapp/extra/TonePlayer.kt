package com.example.toothdecaystudyapp.extra

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin

class TonePlayer {

    private val sampleRate = 44100

    // Precompute AudioTracks for each note
    private val notesFrequencies = listOf(261.63, 293.66, 329.63, 349.23, 392.0, 440.0, 493.88, 523.25)
    private val audioTracks = mutableMapOf<Double, AudioTrack>()

    init {
        notesFrequencies.forEach { freq ->
            val buffer = generateToneBuffer(freq, 400)
            val track = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                buffer.size * 2,
                AudioTrack.MODE_STATIC
            )
            track.write(buffer, 0, buffer.size)
            audioTracks[freq] = track
        }


    }

    private fun generateToneBuffer(freq: Double, durationMs: Int): ShortArray {
        val numSamples = durationMs * sampleRate / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val sample = sin(2.0 * PI * i * freq / sampleRate)
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    fun playTone(freq: Double,volume: Float) {
        val track = audioTracks[freq] ?: return

        track.setVolume(volume.coerceIn(0f, 1f))

        // Stop and reload if already playing
        if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
            track.stop()
            track.reloadStaticData()
        }

        track.play()
    }

    fun release() {
        audioTracks.values.forEach { it.release() }
    }
}