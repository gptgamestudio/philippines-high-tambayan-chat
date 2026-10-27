package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class SpeechAndSoundManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false
    private val toneGenerator = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 80)
    } catch (_: Exception) {
        null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            tts?.setPitch(1.1f)
            tts?.setSpeechRate(0.95f)
            isInitialized = (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED)
        } else {
            Log.e("SpeechAndSoundManager", "TTS Initialization failed")
        }
    }

    fun speak(text: String, flush: Boolean = true) {
        if (!isInitialized || tts == null) return
        val queueMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        tts?.speak(text, queueMode, null, "tambayan_utterance_${System.currentTimeMillis()}")
    }

    fun playPop() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
        } catch (_: Exception) {}
    }

    fun playFanfare() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 250)
        } catch (_: Exception) {}
    }

    fun playCheerfulTone() {
        playPop()
    }

    fun playRewardSound() {
        playFanfare()
        speak("Galing mo! Nakatanggap ka ng Tambay Stars!")
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            toneGenerator?.release()
        } catch (_: Exception) {}
    }
}
