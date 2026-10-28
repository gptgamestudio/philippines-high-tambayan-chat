package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RoomActiveMember
import com.example.model.TambayanRoom

/**
 * Pro Video Call Square Box & Controls matching HTML reference:
 * - Room Title & Online Badge
 * - 2 Video Slots (Live Host Stream + Student Maria)
 * - Controls Toolbar (Camera, Mic, Zoom, Pause/Play, Invite)
 */
@Composable
fun TambayanVideoCallBox(
    currentRoom: TambayanRoom,
    hostAlias: String = "benjie1978",
    modifier: Modifier = Modifier,
    onInviteClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var isCameraOn by remember { mutableStateOf(true) }
    var isMicOn by remember { mutableStateOf(true) }
    var isZoomed by remember { mutableStateOf(false) }
    var isStreamPaused by remember { mutableStateOf(false) }

    // Pulsing animation for LIVE indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // bg-slate-900
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
            .testTag("video_call_square_box")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Live dot, "Video Call Grid (4 Students Active)", Pause and Zoom/Fullscreen actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444).copy(alpha = pulseAlpha))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Video Call Grid (4 Students Active)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = { isStreamPaused = !isStreamPaused },
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(
                            imageVector = if (isStreamPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    IconButton(
                        onClick = { isZoomed = !isZoomed },
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(
                            imageVector = if (isZoomed) Icons.Default.ZoomOut else Icons.Default.ZoomIn,
                            contentDescription = "Zoom/Expand",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2x2 Video Tile Grid (4 Students Active)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF020617)) // bg-slate-950
                    .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.8f), RoundedCornerShape(14.dp))
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val tileHeight = if (isZoomed) 120.dp else 95.dp

                // Row 1: You (benjie1978) & Maria_C
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Tile 1: You
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(tileHeight)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                            .padding(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF0D6EFD))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text("You", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xCC000000))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("1080p", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                            }
                        }

                        // Avatar
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2563EB).copy(alpha = 0.3f))
                                .border(1.5.dp, Color(0xFF60A5FA), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("😎", fontSize = 20.sp)
                        }

                        // Bottom row: Name & status
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(hostAlias, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(
                                    imageVector = if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff,
                                    contentDescription = "Mic",
                                    tint = if (isMicOn) Color(0xFF34D399) else Color(0xFFEF4444),
                                    modifier = Modifier.size(10.dp)
                                )
                                Icon(
                                    imageVector = if (isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                    contentDescription = "Cam",
                                    tint = if (isCameraOn) Color(0xFF34D399) else Color(0xFFEF4444),
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }

                    // Tile 2: Maria_C
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(tileHeight)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                            .padding(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF1E293B))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text("Grade 10", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFCBD5E1))
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF9333EA).copy(alpha = 0.3f))
                                .border(1.5.dp, Color(0xFFC084FC), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👩‍🎓", fontSize = 20.sp)
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Maria_C", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Mic",
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }

                // Row 2: Prof_Mark (Instructor) & Alex_Dev
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Tile 3: Prof_Mark (Instructor)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(tileHeight)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                            .padding(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFD97706)) // Amber-600
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text("Instructor", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD97706).copy(alpha = 0.3f))
                                .border(1.5.dp, Color(0xFFFBBF24), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👨‍🏫", fontSize = 20.sp)
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Prof_Mark", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Icon(
                                imageVector = Icons.Default.MicOff,
                                contentDescription = "Mic Muted",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }

                    // Tile 4: Alex_Dev
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(tileHeight)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                            .padding(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF1E293B))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text("Grade 10", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFCBD5E1))
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF059669).copy(alpha = 0.3f))
                                .border(1.5.dp, Color(0xFF34D399), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👨‍💻", fontSize = 20.sp)
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Alex_Dev", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Mic",
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Video Controls Toolbar (Camera, Mic, Zoom, Pause/Play, Invite)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Camera Toggle
                IconButton(
                    onClick = {
                        isCameraOn = !isCameraOn
                        Toast.makeText(context, if (isCameraOn) "Binuksan ang Camera" else "Pinatay ang Camera", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B))
                        .testTag("btn_toggle_camera")
                ) {
                    Icon(
                        imageVector = if (isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        contentDescription = "Toggle Camera",
                        tint = if (isCameraOn) Color(0xFF60A5FA) else Color(0xFFEF4444),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Mic Toggle
                IconButton(
                    onClick = {
                        isMicOn = !isMicOn
                        Toast.makeText(context, if (isMicOn) "Naka-unmute ang Mikropono" else "Naka-mute ang Mikropono", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B))
                        .testTag("btn_toggle_mic")
                ) {
                    Icon(
                        imageVector = if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Toggle Mic",
                        tint = if (isMicOn) Color(0xFF34D399) else Color(0xFFEF4444),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Zoom Toggle
                IconButton(
                    onClick = { isZoomed = !isZoomed },
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B))
                        .testTag("btn_toggle_zoom")
                ) {
                    Icon(
                        imageVector = if (isZoomed) Icons.Default.ZoomOut else Icons.Default.ZoomIn,
                        contentDescription = "Zoom View",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Pause/Play Stream
                IconButton(
                    onClick = {
                        isStreamPaused = !isStreamPaused
                        Toast.makeText(context, if (isStreamPaused) "Naka-pause ang video stream" else "Nagpapatuloy ang video stream", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B))
                        .testTag("btn_toggle_pause")
                ) {
                    Icon(
                        imageVector = if (isStreamPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "Pause/Play Stream",
                        tint = Color(0xFFC084FC),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Invite Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2563EB))
                        .clickable {
                            onInviteClick()
                            Toast.makeText(context, "Na-kopya na ang link ng room para i-share sa mga kaibigan!", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("btn_invite_share"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Invite",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Invite",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Room Information & Active Members cards matching HTML sidebar
 */
@Composable
fun TambayanRoomInfoAndMembers(
    currentRoom: TambayanRoom,
    members: List<RoomActiveMember> = listOf(
        RoomActiveMember("benjie1978", "Host / Moderator", isVideo = false, isOnline = true),
        RoomActiveMember("Maria Santos", "Student", isVideo = true, isOnline = true),
        RoomActiveMember("Juan Dela Cruz", "Student", isVideo = false, isOnline = true),
        RoomActiveMember("Ana Reyes", "Mentor", isVideo = false, isOnline = true)
    ),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Room Info Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Impormasyon",
                        tint = Color(0xFF60A5FA),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Impormasyon ng Room",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = currentRoom.subtitle.ifBlank {
                        "Isang desenteng tambayan para sa mga estudyante upang magbahagi ng homework help, talakayan, at live video sessions. Zero-tolerance sa cyberbullying."
                    },
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Uri ng Tambayan:", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                    Text(currentRoom.roomType, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Interstitial Ads:", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                    Text(currentRoom.adFrequency, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                }
            }
        }

        // Active Members Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = "Members",
                            tint = Color(0xFFC084FC),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mga Kasama sa Room",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFC084FC).copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Active",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD8B4FE)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    members.forEach { member ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF020617).copy(alpha = 0.5f))
                                .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1E293B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = member.name.firstOrNull()?.toString() ?: "U",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF60A5FA)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = member.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFE2E8F0)
                                    )
                                    Text(
                                        text = member.role,
                                        fontSize = 9.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (member.isVideo) Color(0xFF10B981) else Color(0xFF3B82F6))
                            )
                        }
                    }
                }
            }
        }
    }
}
