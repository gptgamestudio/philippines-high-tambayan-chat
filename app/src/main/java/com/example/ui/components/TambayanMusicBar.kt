package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.FriendlySong
import com.example.audio.MusicPlayerState
import com.example.repository.AppUpdateInfo
import com.example.ui.theme.TambayanBlue
import com.example.ui.theme.TambayanCyan
import com.example.ui.theme.TambayanGold
import com.example.ui.theme.TambayanGreen
import com.example.ui.theme.TambayanNavyBg
import com.example.ui.theme.TambayanNavyCard
import com.example.ui.theme.TambayanNavyElevated
import com.example.ui.theme.TambayanTextLight
import com.example.ui.theme.TambayanTextMuted

@Composable
fun TambayanMusicBar(
    musicState: MusicPlayerState,
    playlist: List<FriendlySong>,
    onTogglePlayPause: () -> Unit,
    onNextSong: () -> Unit,
    onPreviousSong: () -> Unit,
    onSelectSong: (Int) -> Unit,
    onToggleMute: () -> Unit,
    isCompact: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showTrackList by remember { mutableStateOf(false) }

    Surface(
        color = Color(0xFF0F172A),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A8A)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = if (isCompact) 4.dp else 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Song Title & Mood
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showTrackList = !showTrackList }
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isCompact) 28.dp else 34.dp)
                            .clip(CircleShape)
                            .background(TambayanBlue.copy(alpha = 0.25f))
                            .border(1.dp, TambayanCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = musicState.currentMoodEmoji,
                            fontSize = if (isCompact) 14.sp else 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.padding(end = 4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = musicState.currentSongTitle,
                                fontSize = if (isCompact) 11.sp else 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TambayanTextLight,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (musicState.isPlaying && !musicState.isMuted) {
                                Spacer(modifier = Modifier.width(4.dp))
                                MusicSoundWaves()
                            }
                        }
                        Text(
                            text = "📻 Tambayan Campus FM • ${musicState.currentSongGenre} (I-tap para palitan)",
                            fontSize = if (isCompact) 9.sp else 10.sp,
                            color = TambayanGold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Controls: Previous, Play/Pause, Next, Mute
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Previous Button
                    IconButton(
                        onClick = onPreviousSong,
                        modifier = Modifier.size(if (isCompact) 26.dp else 30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous Song",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Play/Pause Button
                    Surface(
                        onClick = onTogglePlayPause,
                        shape = CircleShape,
                        color = if (musicState.isPlaying) TambayanGreen else TambayanBlue,
                        modifier = Modifier
                            .size(if (isCompact) 28.dp else 34.dp)
                            .testTag("btn_toggle_music")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (musicState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (musicState.isPlaying) "Pause Music" else "Play Music",
                                tint = Color.White,
                                modifier = Modifier.size(if (isCompact) 16.dp else 18.dp)
                            )
                        }
                    }

                    // Next Button
                    IconButton(
                        onClick = onNextSong,
                        modifier = Modifier.size(if (isCompact) 26.dp else 30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Song",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Mute / Unmute
                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier.size(if (isCompact) 26.dp else 30.dp)
                    ) {
                        Icon(
                            imageVector = if (musicState.isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            contentDescription = "Mute",
                            tint = if (musicState.isMuted) TambayanTextMuted else TambayanCyan,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // Expandable Track Selector List
            AnimatedVisibility(visible = showTrackList) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Pumili ng Friendly Song para sa Tambayan:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TambayanTextMuted,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(playlist) { index, song ->
                            val isCurrent = index == musicState.currentSongIndex
                            Surface(
                                onClick = {
                                    onSelectSong(index)
                                    showTrackList = false
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isCurrent) TambayanBlue else TambayanNavyElevated,
                                border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.dp, TambayanCyan) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(song.moodEmoji, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = song.title.split(' ').take(3).joinToString(" "),
                                        fontSize = 10.sp,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MusicSoundWaves() {
    val transition = rememberInfiniteTransition(label = "wave")
    val h1 by transition.animateFloat(
        initialValue = 4f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(300), RepeatMode.Reverse),
        label = "h1"
    )
    val h2 by transition.animateFloat(
        initialValue = 10f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(400), RepeatMode.Reverse),
        label = "h2"
    )
    val h3 by transition.animateFloat(
        initialValue = 6f,
        targetValue = 14f,
        animationSpec = infiniteRepeatable(tween(350), RepeatMode.Reverse),
        label = "h3"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(1.5.dp),
        modifier = Modifier.height(14.dp)
    ) {
        Box(modifier = Modifier.width(2.dp).height(h1.dp).background(TambayanGreen, RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.width(2.dp).height(h2.dp).background(TambayanCyan, RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.width(2.dp).height(h3.dp).background(TambayanGold, RoundedCornerShape(1.dp)))
    }
}

@Composable
fun AppUpdateNotificationCard(
    updateInfo: AppUpdateInfo,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF10B981)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🚀", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "MAY BAGONG UPDATE! (v${updateInfo.latestVersionName})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF6EE7B7)
                        )
                        Text(
                            text = "Awtomatikong na-detect mula sa Firebase backend",
                            fontSize = 10.sp,
                            color = Color(0xFFA7F3D0)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF047857))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("NEW", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = updateInfo.releaseNotes.ifBlank { "May bagong bersyon na ang Philippines High Tambayan na may friendly background music at upgraded chat!" },
                fontSize = 11.sp,
                color = Color.White
            )

            if (updateInfo.newFeaturesList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    updateInfo.newFeaturesList.take(3).forEach { feature ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text("• ", fontSize = 11.sp, color = TambayanGold, fontWeight = FontWeight.Bold)
                            Text(feature, fontSize = 10.sp, color = Color(0xFFD1FAE5))
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
                    onClick = {
                        try {
                            Toast.makeText(context, "Sinisimulan ang pag-download ng update APK... 📥", Toast.LENGTH_SHORT).show()
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateInfo.apkDownloadUrl))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Hindi mabuksan ang browser link", Toast.LENGTH_SHORT).show()
                        }
                    },
                    backgroundColor = Color(0xFF059669),
                    shadowColor = Color(0xFF047857),
                    modifier = Modifier.weight(1.5f).testTag("btn_install_apk_update")
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("I-DOWNLOAD ANG BAGONG APK 📥", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                TactileButton(
                    onClick = onDismiss,
                    backgroundColor = Color(0xFF065F46),
                    shadowColor = Color(0xFF047857),
                    modifier = Modifier.weight(0.7f)
                ) {
                    Text("Mamaya", fontSize = 10.sp, color = Color(0xFFD1FAE5))
                }
            }
        }
    }
}
