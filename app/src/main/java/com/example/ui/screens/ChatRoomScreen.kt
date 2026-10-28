package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import com.example.ui.components.CampusHubTopBar
import com.example.ui.components.TambayanVideoCallBox
import com.example.ui.components.TambayanRoomInfoAndMembers
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ChatMessage
import com.example.model.HighSchoolStrand
import com.example.model.ReportReason
import com.example.model.StudentProfile
import com.example.model.TambayanRoom
import com.example.ui.components.TactileButton
import com.example.ui.theme.TambayanBlue
import com.example.ui.theme.TambayanCyan
import com.example.ui.theme.TambayanGold
import com.example.ui.theme.TambayanGreen
import com.example.ui.theme.TambayanNavyBg
import com.example.ui.theme.TambayanNavyCard
import com.example.ui.theme.TambayanNavyElevated
import com.example.ui.theme.TambayanPurple
import com.example.ui.theme.TambayanRed
import com.example.ui.theme.TambayanTextLight
import com.example.ui.theme.TambayanTextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    currentRoom: TambayanRoom,
    allRooms: List<TambayanRoom>,
    messages: List<ChatMessage>,
    studentProfile: StudentProfile,
    systemAnnouncement: String,
    starsCount: Int,
    totalChatsSent: Int = 0,
    onSendMessage: (String) -> Unit,
    onToggleLike: (String) -> Unit,
    onSwitchRoom: (String) -> Unit,
    onPreviousRoomClick: () -> Unit = {},
    onNextRoomClick: () -> Unit = {},
    onDeleteMessage: (String) -> Unit,
    onReportMessage: (String, ReportReason) -> Unit,
    onBlockUser: (String, String) -> Unit,
    onOpenSafetyPolicy: () -> Unit,
    onUpdateProfile: (String, String, HighSchoolStrand) -> Unit,
    onBackClick: () -> Unit,
    onSpeakClick: () -> Unit,
    musicState: com.example.audio.MusicPlayerState? = null,
    playlist: List<com.example.audio.FriendlySong> = emptyList(),
    onTogglePlayPauseMusic: () -> Unit = {},
    onNextMusic: () -> Unit = {},
    onPreviousMusic: () -> Unit = {},
    onSelectMusic: (Int) -> Unit = {},
    onToggleMuteMusic: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showRoomInfoDialog by remember { mutableStateOf(false) }
    var showVideoCallBox by remember { mutableStateOf(true) }
    var selectedMessageForAction by remember { mutableStateOf<ChatMessage?>(null) }
    var messageToReport by remember { mutableStateOf<ChatMessage?>(null) }
    var moderationWarning by remember { mutableStateOf<String?>(null) }

    // Video & Audio Call Simulation State
    var activeCallType by remember { mutableStateOf<String?>(null) }
    var isCallConnected by remember { mutableStateOf(false) }
    var isMicMuted by remember { mutableStateOf(false) }
    var isCameraOff by remember { mutableStateOf(false) }
    var callSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(activeCallType) {
        if (activeCallType != null) {
            isCallConnected = false
            callSeconds = 0
            kotlinx.coroutines.delay(1500)
            isCallConnected = true
            while (activeCallType != null) {
                kotlinx.coroutines.delay(1000)
                callSeconds++
            }
        }
    }

    val listState = rememberLazyListState()

    // Auto scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickSlangList = remember {
        listOf(
            "Kamusta mga lodi! 👋",
            "Sana all! ✨",
            "Walang pasok! 🌧️",
            "Canteen trip tara! 🥪",
            "Patulong sa homework! 📐",
            "G mamayang uwian! 🎮",
            "Awit lods! 🎶",
            "Good luck sa exam! 💯",
            "Galing mo idol! ⭐"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TambayanNavyBg)
            .navigationBarsPadding()
    ) {
        // TOP NAVIGATION SWITCHER (Campus Hub PH • benjie1978 • Grade 10 Completer)
        CampusHubTopBar(
            currentAlias = studentProfile.alias,
            strandOrGrade = studentProfile.strand.displayName,
            isMainDashboardActive = false,
            onMainDashboardClick = onBackClick,
            onStudentDashClick = { /* already on Student Dash & Chatrooms */ }
        )

        // TOP APP BAR
        Surface(
            color = TambayanNavyCard,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                // ROW 1: TOP NAVIGATION BAR (Student Dash, 8 AI Students Online, Profile)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        // Dashboard Button sa Taas (btn-dashboard)
                        Surface(
                            onClick = onBackClick,
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0D6EFD),
                            shadowElevation = 2.dp,
                            modifier = Modifier.testTag("btn_dashboard")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Student Dash",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Student Dash",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // 8 AI Students Online Indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F291E))
                                .border(1.dp, Color(0xFF059669), RoundedCornerShape(12.dp))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4ADE80))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "8 AI Students Online",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4ADE80)
                            )
                        }
                    }

                    // User Profile and Quick Action buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // User Profile Badge (Clickable)
                        Surface(
                            onClick = { showProfileDialog = true },
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF263248),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B4D68)),
                            modifier = Modifier.testTag("btn_user_profile")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(studentProfile.avatar, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = studentProfile.alias,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE2E8F0),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.widthIn(max = 75.dp)
                                )
                            }
                        }

                        // Safety Policy button
                        IconButton(
                            onClick = onOpenSafetyPolicy,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(TambayanNavyElevated)
                                .testTag("btn_safety_policy")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Safety Policy",
                                tint = TambayanGold,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        // Text-to-speech speak button
                        IconButton(
                            onClick = onSpeakClick,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(TambayanNavyElevated)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Pakinggan",
                                tint = TambayanCyan,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                // CALL TOOLBAR: Audio Call & Video Call
                Surface(
                    color = Color(0xFF1A2436),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A3447)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 4.dp)
                        .testTag("call_toolbar")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${currentRoom.emoji} ${currentRoom.title}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00FF88),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Audio Call Button
                            Surface(
                                onClick = { activeCallType = "Audio" },
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF0EA5E9),
                                modifier = Modifier.testTag("btn_audio_call")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "Audio Call",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Audio Call",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Video Call Button
                            Surface(
                                onClick = { activeCallType = "Video" },
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF10B981),
                                modifier = Modifier.testTag("btn_video_call")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Videocam,
                                        contentDescription = "Video Call",
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Video Call",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // ROW 2: BACK & NEXT ARROW NAVIGATION BAR (Moved to the Top as requested!)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Arrow Button
                    Surface(
                        onClick = onPreviousRoomClick,
                        shape = RoundedCornerShape(10.dp),
                        color = TambayanNavyElevated,
                        modifier = Modifier.testTag("btn_prev_room")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Nakaraang Kwarto",
                                tint = TambayanCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "◀ Nakaraan",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TambayanCyan
                            )
                        }
                    }

                    // 100% Free Chat & 5-Chat Interstitial Status Badge
                    val remainder = totalChatsSent % 5
                    val untilAd = if (remainder == 0) 5 else (5 - remainder)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "💬 Libreng Chat • Ad sa $untilAd chats",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TambayanGold
                        )
                    }

                    // Next Arrow Button
                    Surface(
                        onClick = onNextRoomClick,
                        shape = RoundedCornerShape(10.dp),
                        color = TambayanNavyElevated,
                        modifier = Modifier.testTag("btn_next_room")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Susunod ▶",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TambayanCyan
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Susunod na Kwarto",
                                tint = TambayanCyan,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }

                // ROW 3: HORIZONTAL ROOM PICKER TABS (7 Rooms Pill Switcher)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp),
                    modifier = Modifier.testTag("room_tabs_container")
                ) {
                    items(allRooms) { room ->
                        val isSelected = room.id == currentRoom.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B))
                                .clickable { onSwitchRoom(room.id) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("tab_room_${room.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(room.emoji, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = room.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) Color(0x33FFFFFF) else Color(0x33000000))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "${room.onlineCount}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFF34D399)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // VIDEO CALL SQUARE BOX (Main Video + Controls Toolbar: Camera, Mic, Zoom, Pause, Invite)
        AnimatedVisibility(visible = showVideoCallBox) {
            Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                TambayanVideoCallBox(
                    currentRoom = currentRoom,
                    hostAlias = studentProfile.alias
                )
            }
        }

        // PRO CHATROOM FEED HEADER & ROOM INFO TOGGLE
        Surface(
            color = Color(0xFF0F172A),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF1E293B))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💬", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Pro Chatroom Feed",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Secure & Encrypted",
                            fontSize = 8.sp,
                            color = Color(0xFF34D399),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Video Box Toggle Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E293B))
                            .clickable { showVideoCallBox = !showVideoCallBox }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (showVideoCallBox) "Hide Video ▲" else "Show Video ▼",
                            fontSize = 9.sp,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Room Info Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E293B))
                            .clickable { showRoomInfoDialog = true }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Room Info",
                                tint = Color(0xFF60A5FA),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Info & Members",
                                fontSize = 9.sp,
                                color = Color(0xFF60A5FA),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // TAMBAYAN CAMPUS FM MUSIC PLAYER (Friendly songs sa lahat ng rooms)
        if (musicState != null) {
            com.example.ui.components.TambayanMusicBar(
                musicState = musicState,
                playlist = playlist,
                onTogglePlayPause = onTogglePlayPauseMusic,
                onNextSong = onNextMusic,
                onPreviousSong = onPreviousMusic,
                onSelectSong = onSelectMusic,
                onToggleMute = onToggleMuteMusic,
                isCompact = true,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }

        // CAMPUS SAFETY & ANNOUNCEMENT BANNER
        if (systemAnnouncement.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1B4B))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Safety Announcement",
                        tint = TambayanGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = systemAnnouncement,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFDE68A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // ==========================================
        // DOCUMENT ACTION BUTTONS (Docs Tools: Save, Share, Download)
        // ==========================================
        val context = LocalContext.current

        Surface(
            color = Color(0xFF182030),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 3.dp),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A3447))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = "Docs",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Docs Tools:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF94A3B8)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Save Button
                    Surface(
                        onClick = {
                            Toast.makeText(context, "Document Saved! 💾", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF27354A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B4D68)),
                        modifier = Modifier.testTag("btn_doc_save")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Save",
                                tint = Color(0xFFE2E8F0),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Save",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    }

                    // Share Button
                    Surface(
                        onClick = {
                            try {
                                val transcript = buildString {
                                    appendLine("=== PHILIPPINES HIGH TAMBAYAN: ${currentRoom.title} ===")
                                    appendLine("Student: ${studentProfile.alias}")
                                    appendLine("----------------------------------------")
                                    messages.takeLast(10).forEach { m ->
                                        appendLine("[${m.senderName}]: ${m.messageText}")
                                    }
                                }
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "Share link - ${currentRoom.title}")
                                    putExtra(Intent.EXTRA_TEXT, transcript)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share document via:"))
                                Toast.makeText(context, "Share link copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Log.w("ChatRoomScreen", "Error sharing doc", e)
                            }
                        },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF27354A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B4D68)),
                        modifier = Modifier.testTag("btn_doc_share")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color(0xFFE2E8F0),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Share",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    }

                    // Download Button
                    Surface(
                        onClick = {
                            Toast.makeText(context, "Downloading document... 📥", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF27354A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B4D68)),
                        modifier = Modifier.testTag("btn_doc_download")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download",
                                tint = Color(0xFFE2E8F0),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Download",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    }
                }
            }
        }

        // MODERATION WARNING IF TRIGGERED
        if (moderationWarning != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF7F1D1D))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = moderationWarning ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "OK",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = TambayanGold,
                        modifier = Modifier.clickable { moderationWarning = null }
                    )
                }
            }
        }

        // CHAT MESSAGE TIMELINE
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            if (messages.isEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("💬", fontSize = 44.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "💬 CHAT HERE! Sulat ka na sa ${currentRoom.title}!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = TambayanGold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Ligtas na tambayan ng mga estudyante. Mag-text sa ibaba o pumili sa mga quick reactions para makapag-sulat agad!",
                        fontSize = 12.sp,
                        color = TambayanTextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        TactileButton(
                            onClick = { onSendMessage("Kamusta mga lodi! 👋") },
                            backgroundColor = TambayanBlue,
                            shadowColor = Color(0xFF1D4ED8)
                        ) {
                            Text("👋 Hello Barkada!", fontSize = 11.sp, color = Color.White)
                        }
                        TactileButton(
                            onClick = { onSendMessage("Patulong po sa homework! 📐") },
                            backgroundColor = Color(0xFF0D9488),
                            shadowColor = Color(0xFF0F766E)
                        ) {
                            Text("📚 Homework Help!", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item(key = "header_chat_here") {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A8A)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💬", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CHAT HERE sa ${currentRoom.title}! Pambansang tambayan ng kabataang estudyante.",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TambayanCyan
                                )
                            }
                        }
                    }

                    items(messages, key = { it.id }) { msg ->
                        ChatBubbleItem(
                            message = msg,
                            onToggleLike = { onToggleLike(msg.id) },
                            onClick = { selectedMessageForAction = msg }
                        )
                    }
                }
            }
        }

        // QUICK SLANG REACTION BAR
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(TambayanNavyCard)
                .padding(horizontal = 8.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickSlangList) { slang ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(TambayanNavyElevated)
                        .clickable {
                            onSendMessage(slang)
                        }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = slang,
                        fontSize = 12.sp,
                        color = TambayanTextLight,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // CHAT HERE PROMPT BAR (para sa lahat ng room chat mate para makapag-sulat ang mga estudyante)
        Surface(
            color = Color(0xFF0F1A34),
            modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF1E3264))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("✍️", fontSize = 11.sp)
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "CHAT HERE • Sulat dito para sa lahat sa ${currentRoom.title}:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TambayanCyan,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // PRO CHAT INPUT TOOLBAR MATCHING SCREENSHOT
        Surface(
            color = Color(0xFF0F172A),
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                // Tier 1: Action Toolbar Row [Gift] [Like] [Center Sparkle] [Camera] [Mic] [Upload APK]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gift Button (Gold/Amber styled with border)
                    Surface(
                        onClick = { Toast.makeText(context, "Nagbubukas ng Gift Store para sa mga ka-tambay!", Toast.LENGTH_SHORT).show() },
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("btn_gift_store")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = "Gift", tint = Color(0xFFFBBF24), modifier = Modifier.size(15.dp))
                            Text("Gift", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                        }
                    }

                    // Like Button
                    IconButton(
                        onClick = {
                            onSendMessage("👍 (Thumbs up mula kay ${studentProfile.alias})")
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B))
                            .testTag("btn_like_action")
                    ) {
                        Icon(Icons.Default.ThumbUp, contentDescription = "Like", tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                    }

                    // Center Prominent Sparkle Button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, Color(0xFF60A5FA), CircleShape)
                            .clickable {
                                Toast.makeText(context, "✨ Campus Magic: Active 4-Student Study Session on High Definition!", Toast.LENGTH_SHORT).show()
                            }
                            .testTag("btn_center_sparkle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Campus Magic",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Camera Button
                    IconButton(
                        onClick = { Toast.makeText(context, "Nagbubukas ng Camera photo attachment...", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B))
                            .testTag("btn_attach_camera")
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", tint = Color(0xFF60A5FA), modifier = Modifier.size(16.dp))
                    }

                    // Mic Voice Note Button
                    IconButton(
                        onClick = { Toast.makeText(context, "🎙️ Voice note recording...", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B))
                            .testTag("btn_attach_mic")
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Mic Voice Note", tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
                    }

                    // Paperclip / APK Upload Button
                    IconButton(
                        onClick = { Toast.makeText(context, "Nagbubukas ng APK / File attachment selector...", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B))
                            .testTag("btn_attach_apk")
                    ) {
                        Icon(Icons.Default.AttachFile, contentDescription = "Attach File", tint = Color(0xFFC084FC), modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Tier 2: Text Input Field & Send Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = "Chat here... (Type message)",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_chat_message"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF020617),
                            unfocusedContainerColor = Color(0xFF020617),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0xFF1E293B)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                onSendMessage(inputText)
                                inputText = ""
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0D6EFD),
                        shadowElevation = 4.dp,
                        modifier = Modifier.testTag("btn_send_chat")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Send",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Fixed AdMob Banner at bottom
        Surface(
            color = Color(0xFF020617),
            modifier = Modifier
                .fillMaxWidth()
                .border(androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)))
                .testTag("admob_banner")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ADMOB BANNER",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = "ca-app-pub-9786472984542330/4596337589",
                    fontSize = 9.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = Color(0xFF60A5FA)
                )
                Text(
                    text = "100% Free",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF34D399)
                )
            }
        }
    }

    // VIDEO / AUDIO CALL MODAL WINDOW
    if (activeCallType != null) {
        Dialog(onDismissRequest = { activeCallType = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF3B82F6)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("call_modal")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F172A))
                            .border(2.dp, Color(0xFF38BDF8), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎓", fontSize = 34.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Public Lounge Group Call (8 AI Students)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isCallConnected) "🟢 ${activeCallType} Call Connected • HD Audio" else "Connecting ${activeCallType} Call...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCallConnected) Color(0xFF4ADE80) else Color(0xFFFBBF24)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val mins = callSeconds / 60
                    val secs = callSeconds % 60
                    Text(
                        text = String.format(java.util.Locale.US, "%02d:%02d", mins, secs),
                        fontSize = 14.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { isMicMuted = !isMicMuted },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isMicMuted) Color(0xFFEF4444) else Color(0xFF334155))
                        ) {
                            Icon(
                                imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mute Mic",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        if (activeCallType == "Video") {
                            IconButton(
                                onClick = { isCameraOff = !isCameraOff },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isCameraOff) Color(0xFFEF4444) else Color(0xFF334155))
                            ) {
                                Icon(
                                    imageVector = if (isCameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                                    contentDescription = "Toggle Video",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        androidx.compose.material3.Button(
                            onClick = { activeCallType = null },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "End Call",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("End Call", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // MESSAGE ACTIONS MODAL (Report, Block, Delete, Like)
    if (selectedMessageForAction != null) {
        val msg = selectedMessageForAction!!
        Dialog(onDismissRequest = { selectedMessageForAction = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Opsyon para sa Mensahe",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TambayanTextLight
                    )
                    Text(
                        text = "\"${msg.messageText}\"",
                        fontSize = 12.sp,
                        color = TambayanTextMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option 1: Like / React
                    MessageOptionRow(
                        icon = Icons.Default.Favorite,
                        iconTint = TambayanRed,
                        title = if (msg.isLiked) "Alisin ang Puso (Unlike)" else "Pusuan ang Mensahe (Like)",
                        onClick = {
                            onToggleLike(msg.id)
                            selectedMessageForAction = null
                        }
                    )

                    // Option 2: Report Message
                    MessageOptionRow(
                        icon = Icons.Default.Flag,
                        iconTint = TambayanGold,
                        title = "I-report ang Mensahe (Report Abuse)",
                        onClick = {
                            messageToReport = msg
                            selectedMessageForAction = null
                        }
                    )

                    // Option 3: Block User (if not self)
                    if (!msg.isUser) {
                        MessageOptionRow(
                            icon = Icons.Default.Block,
                            iconTint = TambayanRed,
                            title = "I-block si ${msg.senderName}",
                            onClick = {
                                onBlockUser(msg.senderName, msg.senderAvatar)
                                selectedMessageForAction = null
                            }
                        )
                    }

                    // Option 4: Delete Message (if self)
                    if (msg.isUser) {
                        MessageOptionRow(
                            icon = Icons.Default.Delete,
                            iconTint = Color(0xFFEF4444),
                            title = "Burahin ang Aking Mensahe",
                            onClick = {
                                onDeleteMessage(msg.id)
                                selectedMessageForAction = null
                            }
                        )
                    }
                }
            }
        }
    }

    // REPORT REASONS MODAL
    if (messageToReport != null) {
        val targetMsg = messageToReport!!
        var selectedReason by remember { mutableStateOf(ReportReason.BULLYING_HARASSMENT) }

        Dialog(onDismissRequest = { messageToReport = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Report",
                            tint = TambayanRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "I-report ang Mensahe",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TambayanTextLight
                        )
                    }

                    Text(
                        text = "Sender: ${targetMsg.senderName}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TambayanGold,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Pumili ng dahilan ng paglabag:",
                        fontSize = 12.sp,
                        color = TambayanTextMuted
                    )

                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        ReportReason.values().forEach { reason ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedReason = reason }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = selectedReason == reason,
                                    onClick = { selectedReason = reason },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = TambayanBlue,
                                        unselectedColor = TambayanTextMuted
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text(
                                        text = reason.displayName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TambayanTextLight
                                    )
                                    Text(
                                        text = reason.tagalogDesc,
                                        fontSize = 10.sp,
                                        color = TambayanTextMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TactileButton(
                            onClick = { messageToReport = null },
                            backgroundColor = TambayanNavyElevated,
                            shadowColor = Color(0xFF1E293B),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Kanselahin", fontSize = 12.sp, color = Color.White)
                        }

                        TactileButton(
                            onClick = {
                                onReportMessage(targetMsg.id, selectedReason)
                                messageToReport = null
                            },
                            backgroundColor = TambayanRed,
                            shadowColor = Color(0xFF991B1B),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("I-submit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // STUDENT PROFILE DIALOG
    if (showProfileDialog) {
        StudentProfileDialog(
            currentProfile = studentProfile,
            onDismiss = { showProfileDialog = false },
            onSave = { alias, avatar, strand ->
                onUpdateProfile(alias, avatar, strand)
                showProfileDialog = false
            }
        )
    }

    // ROOM INFO & ACTIVE MEMBERS DIALOG
    if (showRoomInfoDialog) {
        Dialog(onDismissRequest = { showRoomInfoDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    TambayanRoomInfoAndMembers(currentRoom = currentRoom)
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B))
                            .clickable { showRoomInfoDialog = false }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Isara (Close)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun MessageOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TambayanTextLight
        )
    }
}

@Composable
fun ChatBubbleItem(
    message: ChatMessage,
    onToggleLike: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (isUser) TambayanBlue else TambayanNavyCard
    val shape = if (isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(message.senderAvatar, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = message.senderName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TambayanTextMuted
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF334155))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = message.senderGradeBadge,
                    fontSize = 9.sp,
                    color = TambayanGold,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(shape)
                .background(bubbleColor)
                .border(
                    width = 1.dp,
                    color = if (isUser) Color(0xFF3B82F6) else TambayanNavyElevated,
                    shape = shape
                )
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                Text(
                    text = message.messageText,
                    fontSize = 14.sp,
                    color = Color.White,
                    lineHeight = 19.sp
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.timestamp,
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onToggleLike() }
                    ) {
                        Icon(
                            imageVector = if (message.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (message.isLiked) Color(0xFFF43F5E) else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(12.dp)
                        )
                        if (message.likesCount > 0) {
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${message.likesCount}",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (message.isReported) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Reported",
                            tint = TambayanGold,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudentProfileDialog(
    currentProfile: StudentProfile,
    onDismiss: () -> Unit,
    onSave: (String, String, HighSchoolStrand) -> Unit
) {
    var alias by remember { mutableStateOf(currentProfile.alias) }
    var selectedAvatar by remember { mutableStateOf(currentProfile.avatar) }
    var selectedStrand by remember { mutableStateOf(currentProfile.strand) }

    val avatarOptions = listOf("😎", "🎓", "🎀", "🧢", "🌸", "⚡", "🎮", "🎸", "📚", "💼", "🏀", "✨")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🆔", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I-edit ang Student ID",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = TambayanTextLight
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Alias / Nickname:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TambayanTextMuted
                )
                OutlinedTextField(
                    value = alias,
                    onValueChange = { alias = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = TambayanNavyElevated,
                        unfocusedContainerColor = TambayanNavyElevated,
                        focusedTextColor = TambayanTextLight,
                        unfocusedTextColor = TambayanTextLight,
                        focusedBorderColor = TambayanBlue
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Pumili ng Pinoy Avatar:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TambayanTextMuted
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    items(avatarOptions) { emoji ->
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (selectedAvatar == emoji) TambayanBlue else TambayanNavyElevated)
                                .clickable { selectedAvatar = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "High School Grade o SHS Strand:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TambayanTextMuted
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    items(HighSchoolStrand.values()) { strand ->
                        val isSelected = selectedStrand == strand
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) TambayanGold else TambayanNavyElevated)
                                .clickable { selectedStrand = strand }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = strand.badge,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color(0xFF451A03) else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TactileButton(
                        onClick = onDismiss,
                        backgroundColor = TambayanNavyElevated,
                        shadowColor = Color(0xFF1E293B),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Kanselahin", fontSize = 13.sp, color = Color.White)
                    }

                    TactileButton(
                        onClick = {
                            if (alias.isNotBlank()) {
                                onSave(alias.trim(), selectedAvatar, selectedStrand)
                            }
                        },
                        backgroundColor = TambayanBlue,
                        shadowColor = Color(0xFF1D4ED8),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "I-save",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
