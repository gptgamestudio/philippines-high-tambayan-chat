package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.audio.SpeechAndSoundManager
import com.example.model.BlockedUser
import com.example.model.CampusSafetyPolicyItem
import com.example.model.ChatMessage
import com.example.model.HighSchoolStrand
import com.example.model.PinoyCampusTrivia
import com.example.model.ReportReason
import com.example.model.ReportedMessage
import com.example.model.StudentProfile
import com.example.model.TambayanRoom
import com.example.repository.TambayanChatRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ScreenState {
    SPLASH,
    CREATE_USERNAME,
    MAIN_MENU,
    CHAT_ROOM,
    PLAY_AND_EARN,
    ADMIN_DASHBOARD,
    SAFETY_POLICY,
    CAMPUS_TRIVIA
}

enum class AppLanguage(val label: String, val flag: String) {
    TAGALOG("Filipino", "🇵🇭"),
    ENGLISH("English", "🇺🇸")
}

data class TambayanUiState(
    val currentScreen: ScreenState = ScreenState.SPLASH,
    val previousScreen: ScreenState = ScreenState.MAIN_MENU,
    val language: AppLanguage = AppLanguage.TAGALOG,
    val starsCount: Int = 25,
    val isTtsEnabled: Boolean = true,
    val isSimulateLoading: Boolean = false,
    val isSimulateEmpty: Boolean = false,

    // Username Registration & Device Lock state
    val hasRegisteredUsername: Boolean = false,
    val isUsernameLocked: Boolean = true,

    // Optional Firebase Auth State
    val isAuthenticated: Boolean = true,
    val isAuthLoading: Boolean = false,
    val authErrorMessage: String? = null,
    val currentUserEmail: String? = null,
    val currentUserId: String? = null,

    // Tambayan Chat System
    val tambayanRooms: List<TambayanRoom> = emptyList(),
    val activeRoomId: String = "lobby",
    val studentProfile: StudentProfile = StudentProfile(),
    val systemAnnouncement: String = "",
    val allMessagesMap: Map<String, List<ChatMessage>> = emptyMap(),
    val blockedUsers: List<BlockedUser> = emptyList(),
    val reportedMessages: List<ReportedMessage> = emptyList(),
    val safetyPolicies: List<CampusSafetyPolicyItem> = emptyList(),
    val campusTriviaList: List<PinoyCampusTrivia> = emptyList(),
    val profanityFilterEnabled: Boolean = true,
    val totalChatsSent: Int = 0,

    // Ads System (Fixed Banner & Every 5 Chats Interstitial)
    val adSystemState: com.example.ads.AdSystemState = com.example.ads.AdSystemState(),
    val isAdminUnlocked: Boolean = false,
    val adminPinError: String? = null,

    // Background Music System (Tambayan Campus FM)
    val musicState: com.example.audio.MusicPlayerState = com.example.audio.MusicPlayerState(),

    // Real-Time Auto-Update & Features Distribution
    val appUpdateInfo: com.example.repository.AppUpdateInfo = com.example.repository.AppUpdateInfo(),
    val isUpdateDialogDismissed: Boolean = false
) {
    val activeRoomMessages: List<ChatMessage>
        get() {
            val blockedSet = blockedUsers.map { it.alias }.toSet()
            val raw = allMessagesMap[activeRoomId] ?: emptyList()
            return raw.filter { it.senderName !in blockedSet }
        }

    val currentRoom: TambayanRoom
        get() = tambayanRooms.firstOrNull { it.id == activeRoomId }
            ?: tambayanRooms.firstOrNull()
            ?: TambayanRoom("lobby", "General Tambayan", "Main Lobby", "🇵🇭", 1248)
}

// Retain alias for any existing reference
typealias PreschoolUiState = TambayanUiState

class TambayanViewModel(application: Application) : AndroidViewModel(application) {

    val soundManager = SpeechAndSoundManager(application)
    val adManager = com.example.ads.AdManager.getInstance(application)
    val musicManager = com.example.audio.TambayanMusicManager(application)

    // Local Persistence for locked username & preferences
    private val prefs = application.getSharedPreferences("tambayan_user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val PREF_HAS_USERNAME = "pref_has_registered_username"
        private const val PREF_USERNAME = "pref_locked_username"
        private const val PREF_AVATAR = "pref_locked_avatar"
        private const val PREF_STRAND = "pref_locked_strand"
        private const val PREF_STARS = "pref_locked_stars"
    }

    // Mandatory custom database ID resolution
    private val databaseId = application.getString(R.string.firestore_database_id)
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(databaseId)
    val auth: FirebaseAuth = Firebase.auth

    val tambayanRepo = TambayanChatRepository(db, auth)

    private val _uiState: MutableStateFlow<TambayanUiState>
    val uiState: StateFlow<TambayanUiState>

    init {
        // Read persisted username on device
        val savedHasUsername = prefs.getBoolean(PREF_HAS_USERNAME, false)
        val savedUsername = prefs.getString(PREF_USERNAME, "") ?: ""
        val savedAvatar = prefs.getString(PREF_AVATAR, "😎") ?: "😎"
        val savedStrandName = prefs.getString(PREF_STRAND, HighSchoolStrand.SHS_STEM.name)
        val savedStrand = try {
            HighSchoolStrand.valueOf(savedStrandName ?: HighSchoolStrand.SHS_STEM.name)
        } catch (_: Exception) {
            HighSchoolStrand.SHS_STEM
        }
        val savedStars = prefs.getInt(PREF_STARS, 25)

        val hasValidUser = savedHasUsername && savedUsername.isNotBlank()
        val initialProfile = if (hasValidUser) {
            StudentProfile(
                alias = savedUsername,
                avatar = savedAvatar,
                strand = savedStrand,
                tambayStars = savedStars
            )
        } else {
            StudentProfile(alias = "", avatar = "😎", strand = HighSchoolStrand.SHS_STEM, tambayStars = 25)
        }

        _uiState = MutableStateFlow(
            TambayanUiState(
                currentScreen = ScreenState.SPLASH,
                hasRegisteredUsername = hasValidUser,
                isUsernameLocked = hasValidUser,
                isAuthenticated = true,
                studentProfile = initialProfile,
                starsCount = savedStars,
                tambayanRooms = tambayanRepo.rooms,
                activeRoomId = "lobby",
                safetyPolicies = tambayanRepo.safetyPolicies,
                campusTriviaList = tambayanRepo.campusTriviaList,
                adSystemState = adManager.state.value
            )
        )
        uiState = _uiState.asStateFlow()

        // If user already registered, attach room listener immediately
        if (hasValidUser) {
            tambayanRepo.attachRoomListener("lobby", savedUsername)
        }

        // Collect repository messages
        viewModelScope.launch {
            tambayanRepo.messagesFlow.collect { map ->
                _uiState.update { it.copy(allMessagesMap = map) }
            }
        }

        // Collect repository announcements
        viewModelScope.launch {
            tambayanRepo.systemAnnouncement.collect { notice ->
                _uiState.update { it.copy(systemAnnouncement = notice) }
            }
        }

        // Collect blocked users
        viewModelScope.launch {
            tambayanRepo.blockedUsers.collect { blocked ->
                _uiState.update { it.copy(blockedUsers = blocked) }
            }
        }

        // Collect reported messages
        viewModelScope.launch {
            tambayanRepo.reportedMessages.collect { reports ->
                _uiState.update { it.copy(reportedMessages = reports) }
            }
        }

        // Collect profanity filter state
        viewModelScope.launch {
            tambayanRepo.profanityFilterEnabled.collect { enabled ->
                _uiState.update { it.copy(profanityFilterEnabled = enabled) }
            }
        }

        // Collect ad manager state updates
        viewModelScope.launch {
            adManager.state.collect { adState: com.example.ads.AdSystemState ->
                _uiState.update { it.copy(adSystemState = adState) }
            }
        }

        // Collect music player state updates (Tambayan Campus FM)
        viewModelScope.launch {
            musicManager.state.collect { mState ->
                _uiState.update { it.copy(musicState = mState) }
            }
        }

        // Collect real-time backend app config and auto-update info
        viewModelScope.launch {
            tambayanRepo.appUpdateInfo.collect { updateInfo ->
                _uiState.update { it.copy(appUpdateInfo = updateInfo) }
            }
        }
    }

    fun setActivity(activity: androidx.activity.ComponentActivity) {
        adManager.bindActivity(activity)
    }

    // ==========================================
    // USERNAME REGISTRATION & ENTER (LOCKED ON DEVICE)
    // ==========================================
    fun registerUsernameAndEnter(alias: String, avatar: String, strand: HighSchoolStrand) {
        val cleanAlias = alias.trim()
        val currentStars = _uiState.value.starsCount

        // Persist to SharedPreferences permanently so username is never lost
        prefs.edit()
            .putBoolean(PREF_HAS_USERNAME, true)
            .putString(PREF_USERNAME, cleanAlias)
            .putString(PREF_AVATAR, avatar)
            .putString(PREF_STRAND, strand.name)
            .putInt(PREF_STARS, currentStars)
            .apply()

        val updated = _uiState.value.studentProfile.copy(
            alias = cleanAlias,
            avatar = avatar,
            strand = strand,
            tambayStars = currentStars
        )

        _uiState.update {
            it.copy(
                hasRegisteredUsername = true,
                isUsernameLocked = true,
                studentProfile = updated,
                currentScreen = ScreenState.MAIN_MENU
            )
        }

        tambayanRepo.syncUserProfile(updated)
        tambayanRepo.attachRoomListener(_uiState.value.activeRoomId, cleanAlias)
        soundManager.speak("Maligayang pagdating sa Tambayan, $cleanAlias!")
        soundManager.playFanfare()
    }

    // ==========================================
    // NAVIGATION
    // ==========================================
    fun navigateTo(screen: ScreenState) {
        if (screen == ScreenState.ADMIN_DASHBOARD) {
            val email = _uiState.value.currentUserEmail
            val alias = _uiState.value.studentProfile.alias
            val hasOtherEmail = email != null && !email.equals("insularleann@gmail.com", ignoreCase = true)
            val hasOtherAlias = alias.isNotBlank() && !alias.equals("Benjie Landingin", ignoreCase = true)

            if (hasOtherEmail || hasOtherAlias) {
                soundManager.speak("Access Denied. Ang Owner Dashboard ay para lamang kay Benjie Landingin.")
                _uiState.update {
                    it.copy(
                        isAdminUnlocked = false,
                        adminPinError = "ACCESS DENIED! Ang account na ito ($alias / ${email ?: "Walang Gmail"}) ay walang pahintulot sa Owner Dashboard. Tanging si Owner Benjie Landingin (insularleann@gmail.com) lamang ang may access."
                    )
                }
                return
            }
        }
        _uiState.update {
            it.copy(
                previousScreen = it.currentScreen,
                currentScreen = screen
            )
        }
    }

    fun navigateBack() {
        val current = _uiState.value.currentScreen
        if (current == ScreenState.MAIN_MENU || current == ScreenState.SPLASH || current == ScreenState.CREATE_USERNAME) {
            return
        }
        _uiState.update { it.copy(currentScreen = ScreenState.MAIN_MENU) }
    }

    fun toggleLanguage() {
        val nextLang = if (_uiState.value.language == AppLanguage.TAGALOG) AppLanguage.ENGLISH else AppLanguage.TAGALOG
        _uiState.update { it.copy(language = nextLang) }
    }

    // ==========================================
    // TAMBAYAN CHATROOMS & MESSAGING
    // ==========================================
    fun openTambayanRoom(roomId: String) {
        _uiState.update {
            it.copy(
                activeRoomId = roomId,
                currentScreen = ScreenState.CHAT_ROOM
            )
        }
        val room = _uiState.value.tambayanRooms.firstOrNull { it.id == roomId }
        if (room != null) {
            soundManager.speak("Pumasok sa ${room.title}")
        }
        val userAlias = _uiState.value.studentProfile.alias
        if (userAlias.isNotBlank()) {
            tambayanRepo.attachRoomListener(roomId, userAlias)
        }
    }

    fun switchActiveRoom(roomId: String) {
        _uiState.update { it.copy(activeRoomId = roomId) }
        val userAlias = _uiState.value.studentProfile.alias
        if (userAlias.isNotBlank()) {
            tambayanRepo.attachRoomListener(roomId, userAlias)
        }
    }

    fun switchToPreviousRoom() {
        val rooms = _uiState.value.tambayanRooms
        if (rooms.isEmpty()) return
        val currentIndex = rooms.indexOfFirst { it.id == _uiState.value.activeRoomId }
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else rooms.size - 1
        val newRoom = rooms[prevIndex]
        _uiState.update { it.copy(activeRoomId = newRoom.id) }
        soundManager.speak("Lumipat sa ${newRoom.title}")
        val userAlias = _uiState.value.studentProfile.alias
        if (userAlias.isNotBlank()) {
            tambayanRepo.attachRoomListener(newRoom.id, userAlias)
        }
    }

    fun switchToNextRoom() {
        val rooms = _uiState.value.tambayanRooms
        if (rooms.isEmpty()) return
        val currentIndex = rooms.indexOfFirst { it.id == _uiState.value.activeRoomId }
        val nextIndex = if (currentIndex in 0 until rooms.size - 1) currentIndex + 1 else 0
        val newRoom = rooms[nextIndex]
        _uiState.update { it.copy(activeRoomId = newRoom.id) }
        soundManager.speak("Lumipat sa ${newRoom.title}")
        val userAlias = _uiState.value.studentProfile.alias
        if (userAlias.isNotBlank()) {
            tambayanRepo.attachRoomListener(newRoom.id, userAlias)
        }
    }

    fun sendTambayanMessage(text: String) {
        val state = _uiState.value
        val success = tambayanRepo.sendMessage(state.activeRoomId, state.studentProfile, text)
        if (success) {
            soundManager.playPop()
            // 100% Free chat engagement: earn stars for fun student rank
            addStars(1)

            val newTotalChats = state.totalChatsSent + 1
            _uiState.update { it.copy(totalChatsSent = newTotalChats) }

            // Interstitial ads every 5 chats
            if (newTotalChats % 5 == 0) {
                adManager.showInterstitial {
                    // Ad completed/dismissed, resume free chat
                }
            }

            // 8 AI Online Students Human-Like Response (Alex, Bea, Chris, Dianne, Ethan, Fiona, Gelo, Hannah)
            viewModelScope.launch {
                kotlinx.coroutines.delay(1200)
                val aiStudents = listOf(
                    Triple("Student #100 (Alex)", "🎮", "Tara ML mamayang uwian!"),
                    Triple("Student #92 (Bea)", "🔬", "Tapos mo na ba reviewer sa Math?"),
                    Triple("Student #36 (Chris)", "😎", "Haha sana all walang pasok bukas!"),
                    Triple("Student #50 (Dianne)", "🎵", "LSS ako sa bagong BINI at OPM acoustic chords!"),
                    Triple("Student #48 (Ethan)", "🎒", "Kamusta mate! Libre kita ng kwek-kwek mamaya."),
                    Triple("Student #79 (Fiona)", "🌸", "May chismis ako sa section 2, alam mo na ba?"),
                    Triple("Student #84 (Gelo)", "🏀", "May practice kami sa gym mamayang 4pm!"),
                    Triple("Student #66 (Hannah)", "🎧", "Chill lang muna habang break time.")
                )
                val responder = aiStudents.random()
                val lower = text.lowercase()
                val reply = when {
                    lower.contains("kamusta") || lower.contains("hello") || lower.contains("hi") || lower.contains("uy") ->
                        "Uy kamusta ${state.studentProfile.alias}! Sakto online ako. ${responder.third}"
                    lower.contains("assign") || lower.contains("project") || lower.contains("doc") || lower.contains("file") ->
                        "Sige mate! Gamitin mo lang 'yung Save / Download buttons sa Docs Tools para ma-share natin 'yung file sa iba."
                    lower.contains("call") || lower.contains("video") || lower.contains("tawag") ->
                        "G ako mate! Pindutin mo lang 'yung Audio Call o Video Call button sa taas para mag-connect tayo!"
                    lower.contains("music") || lower.contains("kanta") || lower.contains("sound") ->
                        "Naka-play yung Campus FM sa taas mate! Pwede mo i-pause o i-next kung gusto mo ng ibang acoustic tune."
                    else ->
                        listOf(
                            "Oo nga mate, sang-ayon ako diyan! Ganyan din sabi sa amin sa klase kanina haha.",
                            "Haha totoong-totoo yan! Ano sa tingin mo, video call muna tayo mamaya?",
                            "Sige mate, noted! I-chat mo lang sa akin dito sa lounge anytime.",
                            "Medyo busy sa school ngayong linggo pero tambay muna ko dito konti kasama mo."
                        ).random()
                }

                val aiProfile = StudentProfile(
                    alias = responder.first,
                    avatar = responder.second,
                    strand = HighSchoolStrand.SHS_STEM
                )
                tambayanRepo.sendMessage(state.activeRoomId, aiProfile, reply)
            }
        }
    }

    fun toggleMessageLike(messageId: String) {
        val state = _uiState.value
        tambayanRepo.toggleLike(state.activeRoomId, messageId)
    }

    fun deleteMessage(messageId: String) {
        val state = _uiState.value
        tambayanRepo.deleteMessage(state.activeRoomId, messageId)
        soundManager.playPop()
    }

    fun reportMessage(messageId: String, reason: ReportReason) {
        val state = _uiState.value
        tambayanRepo.reportMessage(state.activeRoomId, messageId, reason)
        soundManager.speak("Nai-report na ang mensahe. Salamat sa pagtulong na mapanatiling ligtas ang tambayan!")
    }

    fun blockUser(alias: String, avatar: String) {
        tambayanRepo.blockUser(alias, avatar)
        soundManager.speak("Na-block na si $alias. Hindi mo na makikita ang kanyang mga mensahe.")
    }

    fun unblockUser(alias: String) {
        tambayanRepo.unblockUser(alias)
    }

    fun updateStudentProfile(newAlias: String, newAvatar: String, newStrand: HighSchoolStrand) {
        val cleanAlias = newAlias.trim().ifBlank { _uiState.value.studentProfile.alias }
        val updated = _uiState.value.studentProfile.copy(
            alias = cleanAlias,
            avatar = newAvatar,
            strand = newStrand
        )
        prefs.edit()
            .putString(PREF_USERNAME, cleanAlias)
            .putString(PREF_AVATAR, newAvatar)
            .putString(PREF_STRAND, newStrand.name)
            .apply()

        _uiState.update { it.copy(studentProfile = updated) }
        tambayanRepo.syncUserProfile(updated)
    }

    fun acceptSafetyPolicy() {
        val updated = _uiState.value.studentProfile.copy(isPolicyAccepted = true)
        _uiState.update { it.copy(studentProfile = updated) }
        soundManager.speak("Salamat sa pagtanggap ng Campus Safety Policy!")
        tambayanRepo.syncUserProfile(updated)
    }

    // ==========================================
    // REWARDS & PLAY AND EARN
    // ==========================================
    fun addStars(amount: Int) {
        val currentStars = _uiState.value.starsCount + amount
        val newVip = when {
            currentStars >= 200 -> "👑 Legend Tambayer"
            currentStars >= 100 -> "🥇 Gold Tambayer"
            currentStars >= 50 -> "🥈 Silver Tambayer"
            else -> "⭐ Bronze Tambayer"
        }
        val updatedProfile = _uiState.value.studentProfile.copy(tambayStars = currentStars, vipLevel = newVip)
        prefs.edit().putInt(PREF_STARS, currentStars).apply()
        _uiState.update {
            it.copy(
                starsCount = currentStars,
                studentProfile = updatedProfile
            )
        }
        tambayanRepo.syncUserProfile(updatedProfile)
    }

    fun watchRewardedAd() {
        adManager.showRewardedAd { rewardCoins: Int ->
            addStars(rewardCoins)
            soundManager.playFanfare()
        }
    }

    // ==========================================
    // ADMIN DASHBOARD & SECURITY POLICY
    // Master Administrator: Benjie Landingin
    // Secret Code / Birthday: 100877
    // Other access: Strictly Denied
    // ==========================================
    fun verifyAdminPin(enteredPin: String) {
        val email = _uiState.value.currentUserEmail
        val alias = _uiState.value.studentProfile.alias
        val hasOtherEmail = email != null && !email.equals("insularleann@gmail.com", ignoreCase = true)
        val hasOtherAlias = alias.isNotBlank() && !alias.equals("Benjie Landingin", ignoreCase = true)

        if (hasOtherEmail || hasOtherAlias) {
            _uiState.update {
                it.copy(
                    isAdminUnlocked = false,
                    adminPinError = "ACCESS DENIED! Ang account na ito ($alias / ${email ?: "Walang Gmail"}) ay walang pahintulot sa Owner Dashboard. Tanging si Owner Benjie Landingin (insularleann@gmail.com) lamang ang may access."
                )
            }
            return
        }

        if (enteredPin.trim() == "100877") {
            _uiState.update {
                it.copy(
                    isAdminUnlocked = true,
                    adminPinError = null
                )
            }
            soundManager.playFanfare()
        } else {
            _uiState.update {
                it.copy(adminPinError = "ACCESS DENIED! Tanging si Admin Benjie Landingin lamang ang may pahintulot na magbago o magdagdag ng features sa proyektong ito gamit ang kanyang sikretong code (100877).")
            }
        }
    }

    fun lockAdmin() {
        _uiState.update { it.copy(isAdminUnlocked = false, adminPinError = null) }
    }

    fun toggleProfanityFilter(enabled: Boolean) {
        tambayanRepo.toggleProfanityFilter(enabled)
    }

    fun clearRoomChat(roomId: String) {
        tambayanRepo.clearRoomMessages(roomId)
    }

    fun updateSystemAnnouncement(notice: String) {
        tambayanRepo.setSystemAnnouncement(notice)
    }

    fun takeActionOnReport(reportId: String, action: String) {
        tambayanRepo.takeActionOnReport(reportId, action)
    }

    fun dismissReport(reportId: String) {
        tambayanRepo.dismissReport(reportId)
    }

    // ==========================================
    // SIMULATION TOGGLES (for resilience testing)
    // ==========================================
    fun toggleSimulationEmpty() {
        _uiState.update { it.copy(isSimulateEmpty = !it.isSimulateEmpty) }
    }

    fun toggleSimulationLoading() {
        _uiState.update { it.copy(isSimulateLoading = !it.isSimulateLoading) }
    }

    fun retryFetch() {
        _uiState.update {
            it.copy(
                isSimulateLoading = false,
                isSimulateEmpty = false
            )
        }
    }

    // Optional stubs for backwards compatibility
    fun signOut() {
        _uiState.update { it.copy(currentScreen = ScreenState.SPLASH) }
    }

    fun setAuthLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isAuthLoading = isLoading) }
    }

    // ==========================================
    // TAMBAYAN CAMPUS FM MUSIC CONTROLS
    // ==========================================
    fun togglePlayPauseMusic() = musicManager.togglePlayPause()
    fun nextMusic() = musicManager.nextSong()
    fun previousMusic() = musicManager.previousSong()
    fun selectMusic(index: Int) = musicManager.selectSong(index)
    fun toggleMuteMusic() = musicManager.toggleMute()

    // ==========================================
    // AUTO-UPDATE & BACKEND CONFIG
    // ==========================================
    fun dismissUpdateDialog() {
        _uiState.update { it.copy(isUpdateDialogDismissed = true) }
    }

    fun publishAppUpdate(
        versionCode: Int,
        versionName: String,
        apkUrl: String,
        title: String,
        notes: String,
        features: List<String>
    ) {
        tambayanRepo.publishAppUpdate(versionCode, versionName, apkUrl, title, notes, features)
    }

    override fun onCleared() {
        super.onCleared()
        tambayanRepo.detachRoomListener()
        musicManager.release()
        soundManager.shutdown()
    }
}

// Backwards compatibility alias
typealias PreschoolViewModel = TambayanViewModel
