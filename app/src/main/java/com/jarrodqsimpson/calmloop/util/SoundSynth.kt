package com.jarrodqsimpson.calmloop.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

object SoundSynth {
    suspend fun playChime(frequencyHz: Double, durationMs: Int = 1200) = withContext(Dispatchers.Default) {
        val sampleRate = 44100
        val numSamples = sampleRate * durationMs / 1000
        val sample = DoubleArray(numSamples)
        val generatedSnd = ByteArray(2 * numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            // Sine wave with exponential decay envelope (like a chime/bell)
            val envelope = Math.exp(-3.0 * t / (durationMs / 1000.0))
            val sineVal = sin(2.0 * Math.PI * frequencyHz * t) * envelope
            sample[i] = sineVal
        }

        var idx = 0
        for (dVal in sample) {
            val valInt = (dVal * 32767).toInt().coerceIn(-32768, 32767)
            generatedSnd[idx++] = (valInt and 0x00ff).toByte()
            generatedSnd[idx++] = (valInt and 0xff00 shr 8).toByte()
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(generatedSnd.size)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(generatedSnd, 0, generatedSnd.size)
            audioTrack.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
