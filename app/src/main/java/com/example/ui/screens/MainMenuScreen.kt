package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.model.HighSchoolStrand
import com.example.model.StudentProfile
import com.example.model.TambayanRoom
import com.example.ui.components.TactileButton
import com.example.ui.theme.TambayanBlue
import com.example.ui.theme.TambayanCyan
import com.example.ui.theme.TambayanGold
import com.example.ui.theme.TambayanGoldDark
import com.example.ui.theme.TambayanGreen
import com.example.ui.theme.TambayanNavyBg
import com.example.ui.theme.TambayanNavyCard
import com.example.ui.theme.TambayanNavyElevated
import com.example.ui.theme.TambayanPurple
import com.example.ui.theme.TambayanRed
import com.example.ui.theme.TambayanTextLight
import com.example.ui.theme.TambayanTextMuted
import com.example.viewmodel.AppLanguage
import com.example.viewmodel.ScreenState

@Composable
fun MainMenuScreen(
    starsCount: Int,
    currentLanguage: AppLanguage,
    studentProfile: StudentProfile,
    systemAnnouncement: String,
    rooms: List<TambayanRoom>,
    totalChatsSent: Int = 0,
    currentUserEmail: String? = null,
    onEnterRoom: (String) -> Unit,
    onNavigateTo: (ScreenState) -> Unit,
    onAdminDashboardClick: () -> Unit,
    onSafetyPolicyClick: () -> Unit,
    onCampusTriviaClick: () -> Unit,
    onAcceptPolicy: () -> Unit,
    onSpeakClick: () -> Unit,
    onLanguageToggle: () -> Unit,
    onUpdateProfile: (String, String, HighSchoolStrand) -> Unit,
    onSignOut: () -> Unit = {},
    isSimulateEmpty: Boolean = false,
    isSimulateLoading: Boolean = false,
    onToggleSimulateEmpty: () -> Unit = {},
    onToggleSimulateLoading: () -> Unit = {},
    musicState: com.example.audio.MusicPlayerState? = null,
    playlist: List<com.example.audio.FriendlySong> = emptyList(),
    onTogglePlayPauseMusic: () -> Unit = {},
    onNextMusic: () -> Unit = {},
    onPreviousMusic: () -> Unit = {},
    onSelectMusic: (Int) -> Unit = {},
    onToggleMuteMusic: () -> Unit = {},
    appUpdateInfo: com.example.repository.AppUpdateInfo = com.example.repository.AppUpdateInfo(),
    isUpdateDialogDismissed: Boolean = false,
    onDismissUpdate: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showProfileModal by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TambayanNavyBg)
            .navigationBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AUTO-UPDATE & NEW FEATURES NOTIFICATION CARD
        if (appUpdateInfo.hasUpdate && !isUpdateDialogDismissed) {
            item {
                com.example.ui.components.AppUpdateNotificationCard(
                    updateInfo = appUpdateInfo,
                    onDismiss = onDismissUpdate
                )
            }
        }

        // TAMBAYAN CAMPUS FM MUSIC PLAYER BAR (Main Dashboard)
        if (musicState != null) {
            item {
                com.example.ui.components.TambayanMusicBar(
                    musicState = musicState,
                    playlist = playlist,
                    onTogglePlayPause = onTogglePlayPauseMusic,
                    onNextSong = onNextMusic,
                    onPreviousSong = onPreviousMusic,
                    onSelectSong = onSelectMusic,
                    onToggleMute = onToggleMuteMusic,
                    isCompact = false
                )
            }
        }
        // TOP APP HEADER
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Language Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(TambayanNavyElevated)
                                .clickable { onLanguageToggle() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${currentLanguage.flag} ${currentLanguage.label}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TambayanTextLight
                            )
                        }

                        // App Title Banner
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🇵🇭", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TAMBAYAN",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = TambayanGold
                            )
                        }

                        // Audio button
                        IconButton(
                            onClick = onSpeakClick,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(TambayanNavyElevated)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Pakinggan",
                                tint = TambayanCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "PHILIPPINES HIGH TAMBAYAN CHAT",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Pambansang Tambayan ng High School Barkada • Ligtas at Magalang na Chat Lounge",
                        fontSize = 11.sp,
                        color = TambayanTextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // HIGH SCHOOL BARKADA CAMPUS HERO IMAGE
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, Color(0xFF2563EB), RoundedCornerShape(16.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_highschool_tambayan_1792444027473),
                            contentDescription = "High School Tambayan Barkada sa Canteen",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0xB30B132B)
                                        )
                                    )
                                )
                                .padding(10.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF2563EB))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "🏫 CAMPUS LOUNGE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "High School Barkada & Canteen Tambayan",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TambayanGold
                                )
                            }
                        }
                    }
                }
            }
        }

        // CAMPUS SAFETY & POLICY BADGE
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2B3E)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF1E4E70), RoundedCornerShape(16.dp))
                    .clickable { onSafetyPolicyClick() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(TambayanGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = "Safety Policy",
                                tint = TambayanGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Campus Safety & Anti-Bullying",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TambayanTextLight
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(TambayanGreen)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("RA 10627", fontSize = 9.sp, color = Color.Black, fontWeight = FontWeight.Black)
                                }
                            }
                            Text(
                                text = "Zero-tolerance sa cyberbullying, protektado ang privacy. I-tap para sa buong patakaran.",
                                fontSize = 11.sp,
                                color = TambayanTextMuted
                            )
                        }
                    }

                    Text("Tingnan ➡️", fontSize = 11.sp, color = TambayanGold, fontWeight = FontWeight.Bold)
                }
            }
        }

        // CAMPUS ANNOUNCEMENT NOTICE
        if (systemAnnouncement.isNotBlank()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Announcement",
                            tint = TambayanGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = systemAnnouncement,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFFDE68A),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // STUDENT ID CARD (Avatar, Alias, Strand, Stars, VIP Badge)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TambayanNavyElevated, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(TambayanBlue)
                                    .clickable { showProfileModal = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(studentProfile.avatar, fontSize = 28.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = studentProfile.alias,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TambayanTextLight
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(TambayanGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "🔒 Naka-lock",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TambayanGreen
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Profile",
                                        tint = TambayanGold,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable { showProfileModal = true }
                                    )
                                }

                                Text(
                                    text = studentProfile.strand.displayName,
                                    fontSize = 12.sp,
                                    color = TambayanTextMuted
                                )

                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(TambayanNavyElevated)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "⭐ ${studentProfile.vipLevel}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TambayanGold
                                    )
                                }
                            }
                        }

                        // Wallet / Tambay Stars counter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF312E81))
                                .border(1.dp, Color(0xFF4338CA), RoundedCornerShape(14.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "TAMBAY STARS",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFA5B4FC)
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Stars",
                                        tint = TambayanGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$starsCount",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TambayanGold
                                    )
                                }
                            }
                        }
                    }

                    if (currentUserEmail != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(TambayanNavyElevated)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "☁️ $currentUserEmail",
                                fontSize = 11.sp,
                                color = TambayanTextLight
                            )
                            Text(
                                text = "🚪 Sign Out",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TambayanRed,
                                modifier = Modifier
                                    .clickable { onSignOut() }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }

        // 100% FREE CHAT HERO CARD & ROOM ARROW NAVIGATOR
        item {
            var selectedRoomIndex by remember { mutableStateOf(0) }
            val currentSelectedRoom = if (rooms.isNotEmpty()) {
                rooms[selectedRoomIndex.coerceIn(0, rooms.size - 1)]
            } else null

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, Color(0xFF4338CA), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💬", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "100% LIBRENG CHAT TAMBAYAN",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TambayanGold
                                )
                            }
                            Text(
                                text = "Walang credits o bayad • Libre para sa lahat!",
                                fontSize = 11.sp,
                                color = Color(0xFFC7D2FE)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF065F46))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "UNLIMITED FREE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFA7F3D0)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Mag-usap, magbahagi ng homework help, at mag-enjoy kasama ang mga kapwa high school tambayers. Interstitial sponsor ad bawat 5 mensahe sa lahat ng chat rooms.",
                        fontSize = 12.sp,
                        color = Color(0xFFE0E7FF),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Room Fast Selector with Back & Next Arrow Navigation
                    if (currentSelectedRoom != null) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF172554),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (rooms.isNotEmpty()) {
                                                selectedRoomIndex = if (selectedRoomIndex > 0) selectedRoomIndex - 1 else rooms.size - 1
                                            }
                                        },
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF1E3A8A))
                                            .testTag("btn_prev_room_dashboard")
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Nakaraang Kwarto",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(currentSelectedRoom.emoji, fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = currentSelectedRoom.title,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        }
                                        Text(
                                            text = "${currentSelectedRoom.onlineCount} online tambayers",
                                            fontSize = 11.sp,
                                            color = TambayanGreen,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            if (rooms.isNotEmpty()) {
                                                selectedRoomIndex = (selectedRoomIndex + 1) % rooms.size
                                            }
                                        },
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF1E3A8A))
                                            .testTag("btn_next_room_dashboard")
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Susunod na Kwarto",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                TactileButton(
                                    onClick = { onEnterRoom(currentSelectedRoom.id) },
                                    backgroundColor = TambayanGold,
                                    shadowColor = TambayanGoldDark,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("btn_enter_selected_room")
                                ) {
                                    Text(
                                        text = "Pumasok sa ${currentSelectedRoom.title} 💬",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF451A03)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // SECTION HEADER: TAMBAYAN CHAT ROOMS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🇵🇭 Mga Tambayan Rooms",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TambayanTextLight
                )
                Text(
                    text = "Pumili ng Kwarto",
                    fontSize = 12.sp,
                    color = TambayanTextMuted
                )
            }
        }

        // CHAT ROOMS LIST
        items(rooms) { room ->
            Card(
                colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TambayanNavyElevated, RoundedCornerShape(18.dp))
                    .clickable { onEnterRoom(room.id) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(room.emoji, fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = room.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = TambayanTextLight
                            )
                            Text(
                                text = room.subtitle,
                                fontSize = 12.sp,
                                color = TambayanTextMuted,
                                maxLines = 1
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(TambayanGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${room.onlineCount} online",
                                    fontSize = 10.sp,
                                    color = TambayanGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    TactileButton(
                        onClick = { onEnterRoom(room.id) },
                        backgroundColor = TambayanBlue,
                        shadowColor = Color(0xFF1D4ED8),
                        modifier = Modifier.testTag("btn_enter_room_${room.id}")
                    ) {
                        Text(
                            text = "Tambay 💬",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // SECTION: CAMPUS ACTIVITIES & SECURITY POLICY
        item {
            Text(
                text = "⚡ Campus Lounge & Safe Hub",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = TambayanTextLight
            )
        }

        // CAMPUS TRIVIA & SAFETY TILES (Replacing early childhood learning games)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Campus Trivia Challenge
                    Card(
                        colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, TambayanNavyElevated, RoundedCornerShape(16.dp))
                            .clickable { onCampusTriviaClick() }
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🧠", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Campus Trivia",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TambayanTextLight
                            )
                            Text(
                                text = "Earn Tambay Stars 🌟",
                                fontSize = 10.sp,
                                color = TambayanGold
                            )
                        }
                    }

                    // Security & Safety Policy Tile
                    Card(
                        colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, TambayanNavyElevated, RoundedCornerShape(16.dp))
                            .clickable { onSafetyPolicyClick() }
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🛡️", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Safety & Policy",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TambayanTextLight
                            )
                            Text(
                                text = "Anti-Bullying Guidelines",
                                fontSize = 10.sp,
                                color = TambayanGreen
                            )
                        }
                    }
                }
            }
        }

        // OWNER DASHBOARD ENTRY (STRICTLY SHOWN ONLY TO OWNER BENJIE LANDINGIN / insularleann@gmail.com)
        val isAuthorizedOwner = (currentUserEmail != null && currentUserEmail.equals("insularleann@gmail.com", ignoreCase = true)) ||
                studentProfile.alias.equals("Benjie Landingin", ignoreCase = true)

        if (isAuthorizedOwner) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, TambayanGold, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("👑", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Owner Dashboard",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TambayanGold
                                    )
                                    Text(
                                        text = "Eksklusibo kay Benjie Landingin",
                                        fontSize = 11.sp,
                                        color = TambayanGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            TactileButton(
                                onClick = onAdminDashboardClick,
                                backgroundColor = TambayanGold,
                                shadowColor = TambayanGoldDark,
                                modifier = Modifier.testTag("btn_admin_dashboard")
                            ) {
                                Text(
                                    text = "Buksan 🔐",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // STUDENT PROFILE DIALOG
    if (showProfileModal) {
        StudentProfileDialog(
            currentProfile = studentProfile,
            onDismiss = { showProfileModal = false },
            onSave = { alias, avatar, strand ->
                onUpdateProfile(alias, avatar, strand)
                showProfileModal = false
            }
        )
    }
}
