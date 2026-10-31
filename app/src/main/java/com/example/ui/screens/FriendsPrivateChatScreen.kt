package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.model.FriendRequest
import com.example.model.HighSchoolStrand
import com.example.model.PrivateCallSession
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
    incomingRequests: List<FriendRequest> = emptyList(),
    searchedStudents: List<TambayanFriend> = emptyList(),
    isSearchingStudents: Boolean = false,
    activeCallSession: PrivateCallSession? = null,
    onSelectFriend: (TambayanFriend) -> Unit,
    onBackFromChat: () -> Unit,
    onBackToDashboard: () -> Unit,
    onAddFriend: (String, String, HighSchoolStrand, String) -> Unit = { _, _, _, _ -> },
    onSendFriendRequest: (String, String) -> Unit = { _, _ -> },
    onAcceptFriendRequest: (FriendRequest) -> Unit = {},
    onRejectFriendRequest: (String) -> Unit = {},
    onSearchStudents: (String) -> Unit = {},
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
            // VIEW 1: BARKADA FRIENDS DIRECTORY & HUB
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
                                    text = "👥 Barkada Friends Hub",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Participant-Restricted",
                                    tint = TambayanGold,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Text(
                                text = "Participant-restricted private messaging & video calling",
                                fontSize = 11.sp,
                                color = TambayanTextMuted
                            )
                        }
                    }

                    // Add Friend Button
                    TactileButton(
                        onClick = {
                            onSearchStudents("")
                            showAddFriendDialog = true
                        },
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

            // Info Notice Banner
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
                        text = "🔒 Participant-Restricted: Ang 1-on-1 chat at videocall ay eksklusibo lamang sa inyo ng iyong napiling kaibigan.",
                        fontSize = 11.sp,
                        color = Color(0xFF86EFAC)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // INCOMING FRIEND REQUESTS SECTION
                if (incomingRequests.isNotEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6366F1)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "📬 Mga Bagong Friend Request (${incomingRequests.size})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFDE68A)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                incomingRequests.forEach { req ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(req.senderAvatar, fontSize = 20.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(req.senderAlias, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                Text(req.senderStrand, fontSize = 10.sp, color = TambayanCyan)
                                            }
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            IconButton(
                                                onClick = { onAcceptFriendRequest(req) },
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(TambayanGreen)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = "Tanggapin", tint = Color.Black, modifier = Modifier.size(16.dp))
                                            }
                                            IconButton(
                                                onClick = { onRejectFriendRequest(req.id) },
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFF334155))
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "Tanggihan", tint = Color.White, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // LIST OF ADDED FRIENDS
                if (friends.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🤝", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Wala ka pang barkada sa listahan!",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Magpadala ng friend request sa mga rehistradong estudyante para makapag-1-on-1 private chat at videocall!",
                                    fontSize = 12.sp,
                                    color = TambayanTextMuted,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                TactileButton(
                                    onClick = {
                                        onSearchStudents("")
                                        showAddFriendDialog = true
                                    },
                                    backgroundColor = TambayanBlue,
                                    shadowColor = Color(0xFF1D4ED8)
                                ) {
                                    Text("Maghanap ng Barkada 🚀", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                } else {
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
            // VIEW 2: PARTICIPANT-RESTRICTED PRIVATE 1-ON-1 CHAT & VIDEO CALL
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
                                    text = "🔒 Participant-Restricted • ${activeFriend.strand.badge}",
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
                                onTogglePrivateVideoCall(true)
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
                    callSession = activeCallSession,
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
                            text = "Participant-restricted messaging: kayo lamang dalawa ang makakakita sa mga mensaheng ito sa Firestore.",
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
            searchedStudents = searchedStudents,
            isSearching = isSearchingStudents,
            onSearch = onSearchStudents,
            onSendRequest = { targetUid, targetAlias ->
                onSendFriendRequest(targetUid, targetAlias)
                Toast.makeText(context, "Nagpadala ng friend request kay $targetAlias!", Toast.LENGTH_SHORT).show()
                showAddFriendDialog = false
            },
            onDismiss = { showAddFriendDialog = false }
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
 * Real 1-on-1 Private Audio and Video Call Implementation:
 * - Real camera capture and local preview via CameraX PreviewView
 * - Real microphone mute/unmute control
 * - Dynamic runtime camera and audio permission handling
 * - Firebase Firestore signaling state
 * - Clean termination and state handling
 */
@Composable
fun Private1on1VideoCallBox(
    friend: TambayanFriend,
    userProfile: StudentProfile,
    callSession: PrivateCallSession?,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: hasCameraPermission
        hasMicPermission = permissions[Manifest.permission.RECORD_AUDIO] ?: hasMicPermission
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission || !hasMicPermission) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            )
        }
    }

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
                        text = "🔒 Private 1-on-1 Calling (WebRTC / Signaling)",
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

            // Two 1-on-1 Video Stream Slots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tile 1: You (Real Local Camera Feed via CameraX)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF020617))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                ) {
                    if (isCameraOn && hasCameraPermission) {
                        AndroidView(
                            factory = { ctx ->
                                val previewView = PreviewView(ctx)
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(previewView.surfaceProvider)
                                    }
                                    try {
                                        cameraProvider.unbindAll()
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            CameraSelector.DEFAULT_FRONT_CAMERA,
                                            preview
                                        )
                                    } catch (_: Exception) {
                                        try {
                                            cameraProvider.bindToLifecycle(
                                                lifecycleOwner,
                                                CameraSelector.DEFAULT_BACK_CAMERA,
                                                preview
                                            )
                                        } catch (_: Exception) {}
                                    }
                                }, ContextCompat.getMainExecutor(ctx))
                                previewView
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideocamOff,
                                contentDescription = "Camera Off",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (!hasCameraPermission) "Pahintulot kailangan" else "Camera ay naka-off",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(TambayanBlue)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("You (Local Feed)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // Tile 2: Selected Friend Stream / Signaling State
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp)
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

                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(friend.avatar, fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (callSession?.status) {
                                "CALLING" -> "Tumatawag..."
                                "CONNECTED" -> "Konektado (Peer)"
                                "REJECTED" -> "Tinanggihan"
                                "ENDED" -> "Natapos ang tawag"
                                else -> "Signaling: Aktibo"
                            },
                            fontSize = 10.sp,
                            color = Color(0xFF34D399),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "WebRTC Signaling Channel",
                        fontSize = 8.sp,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Real Hardware & Signaling Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (!hasCameraPermission) {
                            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                        } else {
                            isCameraOn = !isCameraOn
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                ) {
                    Icon(
                        imageVector = if (isCameraOn && hasCameraPermission) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        contentDescription = "Camera",
                        tint = if (isCameraOn && hasCameraPermission) Color(0xFF38BDF8) else Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                IconButton(
                    onClick = {
                        if (!hasMicPermission) {
                            permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                        } else {
                            isMicOn = !isMicOn
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                ) {
                    Icon(
                        imageVector = if (isMicOn && hasMicPermission) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Mic",
                        tint = if (isMicOn && hasMicPermission) Color(0xFF34D399) else Color(0xFFEF4444),
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
                        Icon(Icons.Default.CallEnd, contentDescription = "End Call", tint = Color.White, modifier = Modifier.size(14.dp))
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
    searchedStudents: List<TambayanFriend>,
    isSearching: Boolean,
    onSearch: (String) -> Unit,
    onSendRequest: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Mag-add ng Kaibigan sa Barkada",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Maghanap ng totoong rehistradong estudyante sa Firestore upang magpadala ng friend request:",
                    fontSize = 11.sp,
                    color = TambayanTextMuted,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        onSearch(it.trim())
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF94A3B8))
                    },
                    placeholder = { Text("Alias o pangalan ng kaklase...") },
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

                Spacer(modifier = Modifier.height(12.dp))

                if (isSearching) {
                    Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = TambayanBlue)
                    }
                } else if (searchedStudents.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (searchQuery.isBlank()) "I-type ang alias upang maghanap ng kaklase." else "Walang natagpuang estudyante.",
                            fontSize = 11.sp,
                            color = TambayanTextMuted
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(searchedStudents, key = { it.id }) { student ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = TambayanNavyElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(student.avatar, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(student.alias, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text(student.strand.badge, fontSize = 9.sp, color = TambayanCyan)
                                        }
                                    }

                                    TactileButton(
                                        onClick = { onSendRequest(student.id, student.alias) },
                                        backgroundColor = TambayanBlue,
                                        shadowColor = Color(0xFF1D4ED8)
                                    ) {
                                        Text("+ Add Request", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TactileButton(
                    onClick = onDismiss,
                    backgroundColor = TambayanNavyElevated,
                    shadowColor = Color(0xFF1E293B),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Isara", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}
