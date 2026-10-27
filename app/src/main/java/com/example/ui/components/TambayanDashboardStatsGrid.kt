package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Main Dashboard Welcome Banner & 4-card Stats Grid matching HTML reference:
 * - Aktibong Rooms: 7 Rooms (Lahat online & libre)
 * - Online Students: 1,428 (Active ngayon)
 * - Video Calls: 34 Rooms (Tuloy-tuloy ang aral)
 * - Files / APK Shared: 520+ (Na-download na)
 */
@Composable
fun TambayanWelcomeBanner(
    userAlias: String,
    onOpenChatRoomsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0x661E3A8A), // from-blue-900/40
                            Color(0xFF0F172A), // via-slate-900
                            Color(0x66312E81)  // to-indigo-950/40
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Free Tambayan Pill Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF3B82F6).copy(alpha = 0.2f))
                        .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "100% Libreng Chat Tambayan",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF60A5FA)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Maligayang Pagbabalik, ${userAlias.ifBlank { "Benjie" }}!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Pumili ng room o pamahalaan ang iyong mga aktibidad sa iba't ibang chat rooms nang walang bayad.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF2563EB), Color(0xFF4F46E5))
                            )
                        )
                        .clickable(onClick = onOpenChatRoomsClick)
                        .padding(vertical = 12.dp)
                        .testTag("btn_welcome_open_rooms"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = "Buksan ang Chat Rooms",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Buksan ang Chat Rooms",
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

@Composable
fun TambayanStatsGrid(
    modifier: Modifier = Modifier,
    activeRoomsCount: String = "7 Rooms",
    onlineStudentsCount: String = "1,428",
    videoCallsCount: String = "34 Rooms",
    apkSharedCount: String = "520+"
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Aktibong Rooms",
                stat = activeRoomsCount,
                subtitle = "Lahat online & libre",
                icon = Icons.Default.MeetingRoom,
                accentColor = Color(0xFF60A5FA),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Online Students",
                stat = onlineStudentsCount,
                subtitle = "Active ngayon",
                icon = Icons.Default.People,
                accentColor = Color(0xFF34D399),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Video Calls",
                stat = videoCallsCount,
                subtitle = "Tuloy-tuloy ang aral",
                icon = Icons.Default.Videocam,
                accentColor = Color(0xFFFBBF24),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Files / APK Shared",
                stat = apkSharedCount,
                subtitle = "Na-download na",
                icon = Icons.Default.Download,
                accentColor = Color(0xFFC084FC),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    stat: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.85f)),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor
                )
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stat,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}
