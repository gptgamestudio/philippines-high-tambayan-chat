package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.HighSchoolStrand
import com.example.model.PrivateChatMessage
import com.example.model.StudentProfile
import com.example.model.TambayanFriend
import com.example.ui.components.TactileButton
import com.example.ui.theme.TambayanBlue
import com.example.ui.theme.TambayanCyan
import com.example.ui.theme.TambayanGold
import com.example.ui.theme.TambayanGreen
import com.example.ui.theme.TambayanNavyBg
import com.example.ui.theme.TambayanNavyCard
import com.example.ui.theme.TambayanNavyElevated
import com.example.ui.theme.TambayanRed
import com.example.ui.theme.TambayanTextLight
import com.example.ui.theme.TambayanTextMuted

@Composable
fun FriendsPrivateChatScreen(
    friends: List<TambayanFriend>,
    activeFriend: TambayanFriend?,
    privateMessages: List<PrivateChatMessage>,
    studentProfile: StudentProfile,
    isPrivateVideoCallOpen: Boolean,
    onSelectFriend: (TambayanFriend) -> Unit,
    onBackFromChat: () -> Unit,
    onBackToDashboard: () -> Unit,
    onAddFriend: (String, String, HighSchoolStrand, String) -> Unit,
    onRemoveFriend: (String) -> Unit,
    onSendPrivateMessage: (String, String) -> Unit,
    onTogglePrivateVideoCall: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddFriendDialog by remember { mutableStateOf(false) }

    // Intercept hardware or gesture back button
    BackHandler {
        if (activeFriend != null) {
            onBackFromChat()
        } else {
            onBackToDashboard()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TambayanNavyBg)
            .navigationBarsPadding()
    ) {
        if (activeFriend == null) {
            // ==========================================
            // VIEW 1: FRIENDS LIST DIRECTORY & HUB
            // ==========================================
            Surface(
                color = TambayanNavyCard,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBackToDashboard,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(TambayanNavyElevated)
                                .testTag("btn_back_from_friends")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Dashboard",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "👥 Mga Kaibigan & Barkada",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Private",
                                    tint = TambayanGold,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Text(
                                text = "Pribadong 1-on-1 Chat at Video Call (Selected Friends Only)",
                                fontSize = 11.sp,
                                color = TambayanTextMuted
                            )
                        }
                    }

                    // Add Friend Button
                    TactileButton(
                        onClick = { showAddFriendDialog = true },
                        backgroundColor = TambayanBlue,
                        shadowColor = Color(0xFF1D4ED8),
                        modifier = Modifier.testTag("btn_add_friend_open")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Add Friend", tint = Color.White, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Add", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Info Notice Banner: Zero Public Leaks
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F291E))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = "Safe", tint = TambayanGreen, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🔒 Protektadong Komunikasyon: Ang chat at videocall ay eksklusibo lamang sa inyo ng iyong napiling kaibigan.",
                        fontSize = 11.sp,
                        color = Color(0xFF86EFAC)
                    )
                }
            }

            // List of Added Friends
            if (friends.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🤝", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Wala ka pang naka-save na kaibigan!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Mag-add ng mga kaklase o pumili sa mga inirerekomendang online students para makapag-1-on-1 chat at private video call!",
                            fontSize = 12.sp,
                            color = TambayanTextMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TactileButton(
                            onClick = { showAddFriendDialog = true },
                            backgroundColor = TambayanBlue,
                            shadowColor = Color(0xFF1D4ED8)
                        ) {
                            Text("Mag-add ng Kaibigan Ngayon 🚀", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(friends, key = { it.id }) { friend ->
                        FriendCardItem(
                            friend = friend,
                            onChatClick = { onSelectFriend(friend) },
                            onVideoCallClick = {
                                onSelectFriend(friend)
                                onTogglePrivateVideoCall(true)
                            },
                            onRemoveClick = { onRemoveFriend(friend.id) }
                        )
                    }
                }
            }

        } else {
            // ==========================================
            // VIEW 2: 1-ON-1 PRIVATE CHAT & VIDEO CALL
            // ==========================================
            var inputPrivateText by remember { mutableStateOf("") }
            val listState = rememberLazyListState()

            LaunchedEffect(privateMessages.size) {
                if (privateMessages.isNotEmpty()) {
                    listState.animateScrollToItem(privateMessages.size - 1)
                }
            }

            // Private Top Bar
            Surface(
                color = TambayanNavyCard,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onBackFromChat,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(TambayanNavyElevated)
                                .testTag("btn_back_to_friends_list")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Bumalik sa listahan",
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                                .border(1.dp, TambayanCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(activeFriend.avatar, fontSize = 18.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = activeFriend.alias,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (activeFriend.isOnline) Color(0xFF4ADE80) else Color(0xFF94A3B8))
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🔒 Pribadong Usapan • ${activeFriend.strand.badge}",
                                    fontSize = 10.sp,
                                    color = TambayanCyan
                                )
                            }
                        }
                    }

                    // Quick Call Actions
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Private Audio Call
                        IconButton(
                            onClick = {
                                Toast.makeText(context, "Kumokonekta ng Private Audio Call kay ${activeFriend.alias}...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0284C7))
                                .testTag("btn_private_audio_call")
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Audio Call", tint = Color.White, modifier = Modifier.size(15.dp))
                        }

                        // Private Video Call
                        Surface(
                            onClick = { onTogglePrivateVideoCall(true) },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF10B981),
                            modifier = Modifier.testTag("btn_private_video_call")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = Color.White, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Private Video", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // PRIVATE VIDEO CALL BOX (When Active)
            AnimatedVisibility(visible = isPrivateVideoCallOpen) {
                Private1on1VideoCallBox(
                    friend = activeFriend,
                    userProfile = studentProfile,
                    onClose = { onTogglePrivateVideoCall(false) }
                )
            }

            // Private Chat Messages
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                if (privateMessages.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔒", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Simulan ang Pribadong Chat kay ${activeFriend.alias}!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Kayo lamang dalawa ang makakakita sa mga mensahe at makakasali sa videocall.",
                            fontSize = 12.sp,
                            color = TambayanTextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(privateMessages, key = { it.id }) { pmsg ->
                            PrivateChatBubble(message = pmsg)
                        }
                    }
                }
            }

            // Quick Slang Suggestions for Private Chat
            val privateSlang = listOf("Kamusta friend! 👋", "G sa private video call! 📹", "Patulong sa exam reviewer 📚", "Sana all! ✨", "Sige pre, noted! 👍")
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TambayanNavyCard)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(privateSlang) { slang ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(TambayanNavyElevated)
                            .clickable {
                                onSendPrivateMessage(activeFriend.id, slang)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(slang, fontSize = 11.sp, color = TambayanTextLight)
                    }
                }
            }

            // Private Text Input Field
            Surface(
                color = Color(0xFF0F172A),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputPrivateText,
                        onValueChange = { inputPrivateText = it },
                        placeholder = {
                            Text("Pribadong mensahe kay ${activeFriend.alias}...", fontSize = 12.sp, color = Color(0xFF64748B))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_private_message"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF020617),
                            unfocusedContainerColor = Color(0xFF020617),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color(0xFF1E293B)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inputPrivateText.isNotBlank()) {
                                onSendPrivateMessage(activeFriend.id, inputPrivateText)
                                inputPrivateText = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                            .testTag("btn_send_private_message")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Private",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // ADD FRIEND DIALOG
    if (showAddFriendDialog) {
        AddFriendDialog(
            onDismiss = { showAddFriendDialog = false },
            onAdd = { alias, avatar, strand, bio ->
                onAddFriend(alias, avatar, strand, bio)
                Toast.makeText(context, "Si $alias ay naidagdag na bilang kaibigan!", Toast.LENGTH_SHORT).show()
                showAddFriendDialog = false
            }
        )
    }
}

@Composable
fun FriendCardItem(
    friend: TambayanFriend,
    onChatClick: () -> Unit,
    onVideoCallClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onChatClick() }
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .border(1.5.dp, if (friend.isOnline) Color(0xFF4ADE80) else Color(0xFF64748B), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(friend.avatar, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = friend.alias,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = Color(0xFF0F291E),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = friend.strand.badge,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4ADE80),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = friend.statusBio,
                        fontSize = 11.sp,
                        color = TambayanTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Action Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Chat Button
                IconButton(
                    onClick = onChatClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(TambayanBlue)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "Chat", tint = Color.White, modifier = Modifier.size(16.dp))
                }

                // Video Call Button
                IconButton(
                    onClick = onVideoCallClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF10B981))
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = "Private Video", tint = Color.White, modifier = Modifier.size(16.dp))
                }

                // Remove Friend
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(TambayanNavyElevated)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun PrivateChatBubble(message: PrivateChatMessage) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bgColor = if (isUser) Color(0xFF059669) else TambayanNavyCard
    val shape = if (isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(message.senderAvatar, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(6.dp))
            }

            Column(
                modifier = Modifier
                    .clip(shape)
                    .background(bgColor)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = message.messageText,
                    fontSize = 13.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
                Text(
                    text = message.timestamp,
                    fontSize = 9.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

/**
 * Dedicated 1-on-1 Private Video Call Box for Selected Friends Only
 */
@Composable
fun Private1on1VideoCallBox(
    friend: TambayanFriend,
    userProfile: StudentProfile,
    onClose: () -> Unit
) {
    var isMicOn by remember { mutableStateOf(true) }
    var isCameraOn by remember { mutableStateOf(true) }
    var callSeconds by remember { mutableStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            callSeconds++
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.5.dp, Color(0xFF10B981), RoundedCornerShape(18.dp))
            .testTag("private_1on1_video_box")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981).copy(alpha = pulseAlpha))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🔒 Private 1-on-1 Video Call",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34D399)
                    )
                }

                val mins = callSeconds / 60
                val secs = callSeconds % 60
                Text(
                    text = String.format(java.util.Locale.US, "%02d:%02d", mins, secs),
                    fontSize = 11.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Two 1-on-1 Video Streams: You & Selected Friend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tile 1: You
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF020617))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .clip(RoundedCornerShape(4.dp))
                            .background(TambayanBlue)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("You", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(TambayanBlue.copy(alpha = 0.3f))
                            .border(1.dp, Color(0xFF60A5FA), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (isCameraOn) userProfile.avatar else "📷❌", fontSize = 22.sp)
                    }

                    Text(
                        text = "1080p Private",
                        fontSize = 8.sp,
                        color = Color(0xFF34D399),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }

                // Tile 2: Friend
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF020617))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF059669))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(friend.alias, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF059669).copy(alpha = 0.3f))
                            .border(1.dp, Color(0xFF34D399), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(friend.avatar, fontSize = 22.sp)
                    }

                    Text(
                        text = "🟢 Live HD Stream",
                        fontSize = 8.sp,
                        color = Color(0xFF34D399),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Video Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { isCameraOn = !isCameraOn },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                ) {
                    Icon(
                        imageVector = if (isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        contentDescription = "Camera",
                        tint = if (isCameraOn) Color(0xFF38BDF8) else Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                IconButton(
                    onClick = { isMicOn = !isMicOn },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                ) {
                    Icon(
                        imageVector = if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Mic",
                        tint = if (isMicOn) Color(0xFF34D399) else Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                TactileButton(
                    onClick = onClose,
                    backgroundColor = Color(0xFFEF4444),
                    shadowColor = Color(0xFFB91C1C)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CallEnd, contentDescription = "End", tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("End Call", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun AddFriendDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, HighSchoolStrand, String) -> Unit
) {
    var newAlias by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf("😎") }
    var selectedStrand by remember { mutableStateOf(HighSchoolStrand.SHS_STEM) }
    var customBio by remember { mutableStateOf("Online ka-tambay ✨") }

    val recommendedClassmates = listOf(
        Triple("Alex_Gamer", "🎮", HighSchoolStrand.SHS_TVL),
        Triple("Bea_Valedictorian", "📚", HighSchoolStrand.SHS_STEM),
        Triple("Chris_Joker", "🎭", HighSchoolStrand.GRADE_10),
        Triple("Dianne_Music", "🎵", HighSchoolStrand.SHS_HUMSS),
        Triple("Ethan_Council", "⭐", HighSchoolStrand.SHS_GAS)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Mag-add ng Kaibigan (Friend)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Mag-type ng student alias o pumili sa mga inirerekomendang kaklase para sa private chat & videocall:",
                    fontSize = 11.sp,
                    color = TambayanTextMuted,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                // Recommended Quick Add Chips
                Text("Inirerekomendang Classmates:", fontSize = 11.sp, color = TambayanGold, fontWeight = FontWeight.SemiBold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    items(recommendedClassmates) { (recName, recAvatar, recStrand) ->
                        Surface(
                            onClick = {
                                newAlias = recName
                                selectedAvatar = recAvatar
                                selectedStrand = recStrand
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = TambayanNavyElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Text(
                                text = "$recAvatar $recName",
                                fontSize = 10.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = newAlias,
                    onValueChange = { newAlias = it },
                    label = { Text("Friend's Student Alias") },
                    placeholder = { Text("Hal. Juan_DelaCruz") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TambayanBlue,
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customBio,
                    onValueChange = { customBio = it },
                    label = { Text("Status / Note") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TambayanBlue,
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TactileButton(
                        onClick = onDismiss,
                        backgroundColor = TambayanNavyElevated,
                        shadowColor = Color(0xFF1E293B),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Kanselahin", fontSize = 12.sp, color = Color.White)
                    }

                    TactileButton(
                        onClick = {
                            if (newAlias.isNotBlank()) {
                                onAdd(newAlias.trim(), selectedAvatar, selectedStrand, customBio.trim())
                            }
                        },
                        backgroundColor = TambayanBlue,
                        shadowColor = Color(0xFF1D4ED8),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("I-add Friend ✨", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
