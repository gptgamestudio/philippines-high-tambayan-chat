package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.AdBannerView
import com.example.ui.components.EmptyStateView
import com.example.ui.components.KidAdDialog
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CampusTriviaScreen
import com.example.ui.screens.ChatRoomScreen
import com.example.ui.screens.CreateUsernameScreen
import com.example.ui.screens.LoungeScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.PlayAndEarnScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RoomDirectoryScreen
import com.example.ui.screens.SafetyPolicyScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.TinyLearnerTheme
import com.example.viewmodel.ScreenState
import com.example.viewmodel.TambayanViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TambayanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.setActivity(this)

        setContent {
            TinyLearnerTheme {
                val uiState by viewModel.uiState.collectAsState()

                // Intercept hardware or gesture back navigation
                BackHandler(
                    enabled = uiState.currentScreen != ScreenState.MAIN_MENU &&
                            uiState.currentScreen != ScreenState.SPLASH &&
                            uiState.currentScreen != ScreenState.CREATE_USERNAME &&
                            uiState.currentScreen != ScreenState.AUTH
                ) {
                    viewModel.navigateBack()
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // User requirement: fixed banner ads sa main dashboard na hindi nawawala kahit maglipat lipat ng navigation
                        val showBanner = uiState.adSystemState.isBannerEnabled &&
                                uiState.currentScreen != ScreenState.SPLASH &&
                                uiState.currentScreen != ScreenState.CREATE_USERNAME &&
                                uiState.currentScreen != ScreenState.AUTH
                        if (showBanner) {
                            AdBannerView()
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when {
                            uiState.isSimulateLoading -> {
                                EmptyStateView(
                                    isLoading = true,
                                    title = "Kumokonekta sa Tambayan Server…",
                                    subtitle = "Ikinakarga ang mga mensahe, rooms, at mga online tambayers!",
                                    onRetryClick = { viewModel.retryFetch() }
                                )
                            }
                            uiState.isSimulateEmpty -> {
                                EmptyStateView(
                                    isLoading = false,
                                    title = "Walang Signal sa Canteen!",
                                    subtitle = "Pakisuri ang iyong koneksyon o pindutin para mag-reconnect sa tambayan!",
                                    onRetryClick = { viewModel.retryFetch() }
                                )
                            }
                            else -> {
                                when (uiState.currentScreen) {
                                    ScreenState.SPLASH -> {
                                        SplashScreen(
                                            starsCount = uiState.starsCount,
                                            onStartClick = {
                                                if (uiState.hasRegisteredUsername) {
                                                    viewModel.navigateTo(ScreenState.MAIN_MENU)
                                                } else {
                                                    viewModel.navigateTo(ScreenState.AUTH)
                                                }
                                            }
                                        )
                                    }
                                    ScreenState.AUTH -> {
                                        AuthScreen(
                                            onSignInSuccess = { email, alias ->
                                                viewModel.demoSignIn(email, alias)
                                            },
                                            onNavigateToCreateUsername = {
                                                viewModel.navigateTo(ScreenState.CREATE_USERNAME)
                                            },
                                            onBackClick = if (uiState.hasRegisteredUsername) {
                                                { viewModel.navigateBack() }
                                            } else null
                                        )
                                    }
                                    ScreenState.CREATE_USERNAME -> {
                                        CreateUsernameScreen(
                                            initialAlias = uiState.studentProfile.alias,
                                            initialAvatar = uiState.studentProfile.avatar,
                                            initialStrand = uiState.studentProfile.strand,
                                            onEnterDashboard = { alias, avatar, strand ->
                                                viewModel.registerUsernameAndEnter(alias, avatar, strand)
                                            }
                                        )
                                    }
                                    ScreenState.MAIN_MENU -> {
                                        MainMenuScreen(
                                            starsCount = uiState.starsCount,
                                            currentLanguage = uiState.language,
                                            studentProfile = uiState.studentProfile,
                                            systemAnnouncement = uiState.systemAnnouncement,
                                            rooms = uiState.tambayanRooms,
                                            totalChatsSent = uiState.totalChatsSent,
                                            currentUserEmail = uiState.currentUserEmail,
                                            onEnterRoom = { viewModel.openTambayanRoom(it) },
                                            onNavigateTo = { viewModel.navigateTo(it) },
                                            onAdminDashboardClick = { viewModel.navigateTo(ScreenState.ADMIN_DASHBOARD) },
                                            onSafetyPolicyClick = { viewModel.navigateTo(ScreenState.SAFETY_POLICY) },
                                            onCampusTriviaClick = { viewModel.navigateTo(ScreenState.CAMPUS_TRIVIA) },
                                            onAcceptPolicy = { viewModel.acceptSafetyPolicy() },
                                            onSpeakClick = {
                                                if (uiState.language == com.example.viewmodel.AppLanguage.TAGALOG) {
                                                    viewModel.soundManager.speak("Maligayang pagdating sa Philippines High Tambayan Chat! Ang ligtas at pambansang tambayan ng kabataan!")
                                                } else {
                                                    viewModel.soundManager.speak("Welcome to Philippines High Tambayan Chat! Safe and friendly hangout lounge!")
                                                }
                                            },
                                            onLanguageToggle = { viewModel.toggleLanguage() },
                                            onUpdateProfile = { alias, avatar, strand ->
                                                viewModel.updateStudentProfile(alias, avatar, strand)
                                            },
                                            onSignOut = { viewModel.signOut() },
                                            isSimulateEmpty = uiState.isSimulateEmpty,
                                            isSimulateLoading = uiState.isSimulateLoading,
                                            onToggleSimulateEmpty = { viewModel.toggleSimulationEmpty() },
                                            onToggleSimulateLoading = { viewModel.toggleSimulationLoading() },
                                            musicState = uiState.musicState,
                                            playlist = viewModel.musicManager.playlist,
                                            onTogglePlayPauseMusic = { viewModel.togglePlayPauseMusic() },
                                            onNextMusic = { viewModel.nextMusic() },
                                            onPreviousMusic = { viewModel.previousMusic() },
                                            onSelectMusic = { viewModel.selectMusic(it) },
                                            onToggleMuteMusic = { viewModel.toggleMuteMusic() },
                                            appUpdateInfo = uiState.appUpdateInfo,
                                            isUpdateDialogDismissed = uiState.isUpdateDialogDismissed,
                                            onDismissUpdate = { viewModel.dismissUpdateDialog() }
                                        )
                                    }
                                    ScreenState.CHAT_ROOM -> {
                                        ChatRoomScreen(
                                            currentRoom = uiState.currentRoom,
                                            allRooms = uiState.tambayanRooms,
                                            messages = uiState.activeRoomMessages,
                                            studentProfile = uiState.studentProfile,
                                            systemAnnouncement = uiState.systemAnnouncement,
                                            starsCount = uiState.starsCount,
                                            totalChatsSent = uiState.totalChatsSent,
                                            onSendMessage = { viewModel.sendTambayanMessage(it) },
                                            onToggleLike = { viewModel.toggleMessageLike(it) },
                                            onSwitchRoom = { viewModel.switchActiveRoom(it) },
                                            onPreviousRoomClick = { viewModel.switchToPreviousRoom() },
                                            onNextRoomClick = { viewModel.switchToNextRoom() },
                                            onDeleteMessage = { viewModel.deleteMessage(it) },
                                            onReportMessage = { msgId, reason -> viewModel.reportMessage(msgId, reason) },
                                            onBlockUser = { alias, avatar -> viewModel.blockUser(alias, avatar) },
                                            onOpenSafetyPolicy = { viewModel.navigateTo(ScreenState.SAFETY_POLICY) },
                                            onUpdateProfile = { alias, avatar, strand ->
                                                viewModel.updateStudentProfile(alias, avatar, strand)
                                            },
                                            onBackClick = { viewModel.navigateBack() },
                                            onSpeakClick = {
                                                viewModel.soundManager.speak("Nasa kwartong ${uiState.currentRoom.title}. ${uiState.currentRoom.subtitle}")
                                            },
                                            musicState = uiState.musicState,
                                            playlist = viewModel.musicManager.playlist,
                                            onTogglePlayPauseMusic = { viewModel.togglePlayPauseMusic() },
                                            onNextMusic = { viewModel.nextMusic() },
                                            onPreviousMusic = { viewModel.previousMusic() },
                                            onSelectMusic = { viewModel.selectMusic(it) },
                                            onToggleMuteMusic = { viewModel.toggleMuteMusic() }
                                        )
                                    }
                                    ScreenState.SAFETY_POLICY -> {
                                        SafetyPolicyScreen(
                                            policies = uiState.safetyPolicies,
                                            blockedUsers = uiState.blockedUsers,
                                            reportedMessages = uiState.reportedMessages,
                                            isPolicyAccepted = uiState.studentProfile.isPolicyAccepted,
                                            onAcceptPolicy = { viewModel.acceptSafetyPolicy() },
                                            onUnblockUser = { viewModel.unblockUser(it) },
                                            onBackClick = { viewModel.navigateBack() }
                                        )
                                    }
                                    ScreenState.CAMPUS_TRIVIA -> {
                                        CampusTriviaScreen(
                                            triviaList = uiState.campusTriviaList,
                                            starsCount = uiState.starsCount,
                                            onEarnStars = { viewModel.addStars(it) },
                                            onBackClick = { viewModel.navigateBack() }
                                        )
                                    }
                                    ScreenState.PLAY_AND_EARN -> {
                                        PlayAndEarnScreen(
                                            adState = uiState.adSystemState,
                                            starsCount = uiState.starsCount,
                                            onWatchAdClick = { viewModel.watchRewardedAd() },
                                            onBackClick = { viewModel.navigateBack() },
                                            onSpeakClick = {
                                                viewModel.soundManager.speak("Manood ng hanggang sampung ads kada dalawampu't apat na oras para sa mga gintong bituin!")
                                            }
                                        )
                                    }
                                    ScreenState.ADMIN_DASHBOARD -> {
                                        AdminDashboardScreen(
                                            adManager = viewModel.adManager,
                                            adState = uiState.adSystemState,
                                            isAdminUnlocked = uiState.isAdminUnlocked,
                                            adminPinError = uiState.adminPinError,
                                            reportedMessages = uiState.reportedMessages,
                                            rooms = uiState.tambayanRooms,
                                            profanityFilterEnabled = uiState.profanityFilterEnabled,
                                            onVerifyPin = { viewModel.verifyAdminPin(it) },
                                            onLockAdmin = { viewModel.lockAdmin() },
                                            onToggleProfanityFilter = { viewModel.toggleProfanityFilter(it) },
                                            onClearRoomChat = { viewModel.clearRoomChat(it) },
                                            onUpdateAnnouncement = { viewModel.updateSystemAnnouncement(it) },
                                            onTakeActionOnReport = { reportId, action -> viewModel.takeActionOnReport(reportId, action) },
                                            onDismissReport = { viewModel.dismissReport(it) },
                                            onBackClick = { viewModel.navigateBack() },
                                            onSpeakClick = {
                                                viewModel.soundManager.speak("Admin Dashboard. Pamahalaan ang moderation, ads, announcements, at security settings.")
                                            },
                                            onPublishAppUpdate = { vCode, vName, apkUrl, title, notes, features ->
                                                viewModel.publishAppUpdate(vCode, vName, apkUrl, title, notes, features)
                                            },
                                            currentUserEmail = uiState.currentUserEmail,
                                            currentUsername = uiState.studentProfile.alias,
                                            isSimulateEmpty = uiState.isSimulateEmpty,
                                            isSimulateLoading = uiState.isSimulateLoading,
                                            onToggleSimulateEmpty = { viewModel.toggleSimulationEmpty() },
                                            onToggleSimulateLoading = { viewModel.toggleSimulationLoading() }
                                        )
                                    }
                                    ScreenState.ROOM_DIRECTORY -> {
                                        RoomDirectoryScreen(
                                            rooms = uiState.tambayanRooms,
                                            activeRoomId = uiState.activeRoomId,
                                            onEnterRoom = { viewModel.openTambayanRoom(it) },
                                            onBackClick = { viewModel.navigateBack() }
                                        )
                                    }
                                    ScreenState.LOUNGE -> {
                                        LoungeScreen(
                                            posts = uiState.loungePosts,
                                            studentProfile = uiState.studentProfile,
                                            onCreatePost = { cat, title, content ->
                                                viewModel.createLoungePost(cat, title, content)
                                            },
                                            onAddReply = { postId, content ->
                                                viewModel.addLoungeReply(postId, content)
                                            },
                                            onTogglePostLike = { viewModel.toggleLoungePostLike(it) },
                                            onToggleReplyLike = { postId, replyId ->
                                                viewModel.toggleLoungeReplyLike(postId, replyId)
                                            },
                                            onReportPost = { postId, reason ->
                                                viewModel.reportLoungePost(postId, reason)
                                            },
                                            onDeletePost = { viewModel.deleteLoungePost(it) },
                                            onBackClick = { viewModel.navigateBack() }
                                        )
                                    }
                                    ScreenState.PROFILE -> {
                                        ProfileScreen(
                                            profile = uiState.studentProfile,
                                            blockedUsers = uiState.blockedUsers,
                                            onUpdateProfile = { alias, avatar, strand ->
                                                viewModel.updateStudentProfile(alias, avatar, strand)
                                            },
                                            onUnblockUser = { viewModel.unblockUser(it) },
                                            onBackClick = { viewModel.navigateBack() }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.adSystemState.isAdShowing && uiState.adSystemState.adCountdownSeconds > 0) {
                        KidAdDialog(
                            adType = uiState.adSystemState.adTypeShowing,
                            countdownSeconds = uiState.adSystemState.adCountdownSeconds,
                            rewardPerAd = uiState.adSystemState.rewardPerAd,
                            onDismiss = { viewModel.adManager.dismissCurrentAdManually() }
                        )
                    }
                }
            }
        }
    }
}
