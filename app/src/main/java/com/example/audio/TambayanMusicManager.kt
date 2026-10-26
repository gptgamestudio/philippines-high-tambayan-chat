package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

data class FriendlySong(
    val id: String,
    val title: String,
    val genre: String,
    val moodEmoji: String,
    val tempoBpm: Int,
    val chords: List<ChordProgressionStep>
)

data class ChordProgressionStep(
    val rootFreq: Double,
    val thirdFreq: Double,
    val fifthFreq: Double,
    val melodyNotes: List<Double>
)

data class MusicPlayerState(
    val isPlaying: Boolean = false,
    val currentSongIndex: Int = 0,
    val currentSongTitle: String = "Pinoy High School Barkada Jam",
    val currentSongGenre: String = "Acoustic Pop Jam",
    val currentMoodEmoji: String = "🎸",
    val isMuted: Boolean = false,
    val volume: Float = 0.5f
)

class TambayanMusicManager(private val context: Context) {

    companion object {
        private const val TAG = "TambayanMusicManager"
        private const val SAMPLE_RATE = 22050

        // Standard Note Frequencies (Hz)
        private const val C4 = 261.63
        private const val D4 = 293.66
        private const val E4 = 329.63
        private const val F4 = 349.23
        private const val G4 = 392.00
        private const val A4 = 440.00
        private const val B4 = 493.88
        private const val C5 = 523.25
        private const val D5 = 587.33
        private const val E5 = 659.25
        private const val G5 = 783.99
    }

    val playlist: List<FriendlySong> = listOf(
        FriendlySong(
            id = "barkada_jam",
            title = "Pinoy High School Barkada Jam",
            genre = "Acoustic Pop Jam",
            moodEmoji = "🎸",
            tempoBpm = 110,
            chords = listOf(
                ChordProgressionStep(C4, E4, G4, listOf(C5, E5, G5, E5)),
                ChordProgressionStep(G4, B4, D5, listOf(D5, G5, B4, D5)),
                ChordProgressionStep(A4, C5, E5, listOf(E5, C5, A4, C5)),
                ChordProgressionStep(F4, A4, C5, listOf(F4, A4, C5, F4))
            )
        ),
        FriendlySong(
            id = "canteen_harana",
            title = "Harana sa Canteen & Kilig Chords",
            genre = "Sweet Acoustic OPM",
            moodEmoji = "💌",
            tempoBpm = 95,
            chords = listOf(
                ChordProgressionStep(G4, B4, D5, listOf(B4, D5, G5, D5)),
                ChordProgressionStep(E4, G4, B4, listOf(G4, B4, E5, B4)),
                ChordProgressionStep(C4, E4, G4, listOf(E4, G4, C5, G4)),
                ChordProgressionStep(D4, F4 * 1.06, A4, listOf(F4 * 1.06, A4, D5, A4))
            )
        ),
        FriendlySong(
            id = "study_lofi",
            title = "Study Hall Lofi Beats & Math Focus",
            genre = "Chillout Lofi Study",
            moodEmoji = "🎧",
            tempoBpm = 80,
            chords = listOf(
                ChordProgressionStep(D4, F4, A4, listOf(A4, C5, D5, A4)),
                ChordProgressionStep(G4, B4, D5, listOf(B4, D5, G5, B4)),
                ChordProgressionStep(C4, E4, G4, listOf(G4, C5, E5, C5)),
                ChordProgressionStep(A4, C5, E5, listOf(C5, E5, A4, E5))
            )
        ),
        FriendlySong(
            id = "flag_ceremony",
            title = "Flag Ceremony & Campus Sunrise",
            genre = "Inspiring Highschool Anthem",
            moodEmoji = "☀️",
            tempoBpm = 115,
            chords = listOf(
                ChordProgressionStep(F4, A4, C5, listOf(C5, F4, A4, C5)),
                ChordProgressionStep(C4, E4, G4, listOf(G4, C4, E4, G4)),
                ChordProgressionStep(D4, F4, A4, listOf(A4, D4, F4, A4)),
                ChordProgressionStep(B4 * 0.94, D4, F4, listOf(F4, D4, F4, D4))
            )
        ),
        FriendlySong(
            id = "gamer_chiptune",
            title = "Gamer Tambayan 8-Bit Chiptune",
            genre = "Retro Arcade Fun",
            moodEmoji = "🎮",
            tempoBpm = 128,
            chords = listOf(
                ChordProgressionStep(C4, G4, C5, listOf(E5, G5, C5, G5)),
                ChordProgressionStep(A4, E5, A4, listOf(C5, E5, A4, E5)),
                ChordProgressionStep(F4, C5, F4, listOf(A4, C5, F4, C5)),
                ChordProgressionStep(G4, D5, G4, listOf(B4, D5, G4, D5))
            )
        )
    )

    private val _state = MutableStateFlow(
        MusicPlayerState(
            isPlaying = true,
            currentSongIndex = 0,
            currentSongTitle = playlist[0].title,
            currentSongGenre = playlist[0].genre,
            currentMoodEmoji = playlist[0].moodEmoji
        )
    )
    val state: StateFlow<MusicPlayerState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)
    private var playbackJob: Job? = null
    private var audioTrack: AudioTrack? = null

    init {
        startMusicPlayback()
    }

    fun togglePlayPause() {
        if (_state.value.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        _state.update { it.copy(isPlaying = true) }
        startMusicPlayback()
    }

    fun pause() {
        _state.update { it.copy(isPlaying = false) }
        playbackJob?.cancel()
        playbackJob = null
        stopAudioTrack()
    }

    fun nextSong() {
        val nextIdx = (_state.value.currentSongIndex + 1) % playlist.size
        selectSong(nextIdx)
    }

    fun previousSong() {
        val prevIdx = if (_state.value.currentSongIndex - 1 < 0) playlist.size - 1 else _state.value.currentSongIndex - 1
        selectSong(prevIdx)
    }

    fun selectSong(index: Int) {
        val validIdx = index.coerceIn(0, playlist.size - 1)
        val song = playlist[validIdx]
        _state.update {
            it.copy(
                currentSongIndex = validIdx,
                currentSongTitle = song.title,
                currentSongGenre = song.genre,
                currentMoodEmoji = song.moodEmoji,
                isPlaying = true
            )
        }
        startMusicPlayback()
    }

    fun toggleMute() {
        _state.update { it.copy(isMuted = !it.isMuted) }
    }

    private fun startMusicPlayback() {
        playbackJob?.cancel()
        playbackJob = scope.launch {
            try {
                initAudioTrack()
                while (isActive && _state.value.isPlaying) {
                    val currentSong = playlist[_state.value.currentSongIndex]
                    val stepDurationMs = (60_000L / currentSong.tempoBpm)

                    for (chordStep in currentSong.chords) {
                        if (!isActive || !_state.value.isPlaying) break

                        // Synthesize smooth harmonic chord buffer
                        val numSamples = ((stepDurationMs / 1000.0) * SAMPLE_RATE).toInt()
                        val buffer = ShortArray(numSamples)
                        val vol = if (_state.value.isMuted) 0f else _state.value.volume

                        for (i in 0 until numSamples) {
                            val time = i.toDouble() / SAMPLE_RATE
                            // Envelope for warm acoustic pluck decay
                            val env = kotlin.math.max(0.0, 1.0 - (i.toDouble() / numSamples))

                            // Harmonic synthesis: Root + Third + Fifth + Melody
                            val root = sin(2.0 * PI * chordStep.rootFreq * time) * 0.35
                            val third = sin(2.0 * PI * chordStep.thirdFreq * time) * 0.25
                            val fifth = sin(2.0 * PI * chordStep.fifthFreq * time) * 0.20

                            // Melody note based on quarter of the chord duration
                            val melodyIdx = ((i.toDouble() / numSamples) * chordStep.melodyNotes.size).toInt().coerceIn(0, chordStep.melodyNotes.size - 1)
                            val melodyFreq = chordStep.melodyNotes[melodyIdx]
                            val melody = sin(2.0 * PI * melodyFreq * time) * 0.35

                            val sample = ((root + third + fifth + melody) * env * vol * 16384.0).toInt().coerceIn(-32768, 32767)
                            buffer[i] = sample.toShort()
                        }

                        audioTrack?.write(buffer, 0, buffer.size)
                        delay(stepDurationMs)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "AudioTrack synthesis notice: ${e.message}")
            }
        }
    }

    private fun initAudioTrack() {
        stopAudioTrack()
        try {
            val minBuf = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuf * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
            audioTrack?.play()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize AudioTrack: ${e.message}")
        }
    }

    private fun stopAudioTrack() {
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }

    fun release() {
        playbackJob?.cancel()
        playbackJob = null
        stopAudioTrack()
    }
}
