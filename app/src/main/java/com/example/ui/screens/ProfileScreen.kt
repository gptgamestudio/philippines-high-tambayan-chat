package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BlockedUser
import com.example.model.HighSchoolStrand
import com.example.model.StudentProfile
import com.example.model.UserRole

private val DarkNavyBg = Color(0xFF0B1120)
private val CardNavyBg = Color(0xFF1E293B)
private val AccentBlue = Color(0xFF2563EB)
private val LightBlue = Color(0xFF38BDF8)
private val BorderNavy = Color(0xFF334155)
private val GoldStar = Color(0xFFFBBF24)

@Composable
fun ProfileScreen(
    profile: StudentProfile,
    blockedUsers: List<BlockedUser>,
    onUpdateProfile: (alias: String, avatar: String, strand: HighSchoolStrand) -> Unit,
    onUnblockUser: (alias: String) -> Unit,
    onBackClick: () -> Unit
) {
    var editedAlias by remember { mutableStateOf(profile.alias) }
    var selectedAvatar by remember { mutableStateOf(profile.avatar) }
    var selectedStrand by remember { mutableStateOf(profile.strand) }
    var isSavedNotificationShowing by remember { mutableStateOf(false) }

    var soundNotificationsEnabled by remember { mutableStateOf(true) }
    var loungeNotificationsEnabled by remember { mutableStateOf(true) }

    val avatarOptions = listOf("😎", "🎀", "🧢", "💼", "📐", "💻", "🌸", "🎮", "🏀", "🎧")

    val strands = listOf(
        HighSchoolStrand.GRADE_7,
        HighSchoolStrand.GRADE_8,
        HighSchoolStrand.GRADE_9,
        HighSchoolStrand.GRADE_10,
        HighSchoolStrand.SHS_STEM,
        HighSchoolStrand.SHS_ABM,
        HighSchoolStrand.SHS_HUMSS,
        HighSchoolStrand.SHS_TVL,
        HighSchoolStrand.SHS_GAS
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("profile_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("profile_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Bumalik sa Dashboard",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Aking Profile at Mga Setting",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Profile Summary Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavyBg),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF334155))
                            .border(2.dp, LightBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = selectedAvatar, fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = profile.alias.ifBlank { "PinoyHighStudent" },
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = AccentBlue.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = selectedStrand.badge,
                                color = LightBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "⭐ ${profile.tambayStars} Bituin",
                            color = GoldStar,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Role Transparency Card (Crucial Security Check)
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavyBg),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Role Policy",
                            tint = LightBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Katayuan ng Account & Role Transparency",
                            color = LightBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Kasalukuyang Role:",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp
                        )
                        Surface(
                            color = Color(0xFF0F172A),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy)
                        ) {
                            Text(
                                text = "${profile.role.badgeEmoji} ${profile.role.displayName}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "ℹ️ Ang mga karapatang Moderator o Admin ay hindi maaaring piliin o baguhin sa client-side ng user. Ang mga ito ay protektado ng server rules upang maiwasan ang privilege escalation alinsunod sa Campus Safety Policy.",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Edit Profile Form
            Text(
                text = "Baguhin ang Impormasyon",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = editedAlias,
                onValueChange = { editedAlias = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_alias_input"),
                label = { Text("Student Alias / Palayaw", color = Color(0xFF94A3B8)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = BorderNavy,
                    focusedContainerColor = CardNavyBg,
                    unfocusedContainerColor = CardNavyBg
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Avatar Choices
            Text(
                text = "Pumili ng Avatar Emoji:",
                color = Color(0xFFCBD5E1),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                avatarOptions.take(5).forEach { av ->
                    val isSelected = selectedAvatar == av
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) AccentBlue else CardNavyBg)
                            .border(1.dp, if (isSelected) LightBlue else BorderNavy, CircleShape)
                            .clickable { selectedAvatar = av },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = av, fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                avatarOptions.drop(5).forEach { av ->
                    val isSelected = selectedAvatar == av
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) AccentBlue else CardNavyBg)
                            .border(1.dp, if (isSelected) LightBlue else BorderNavy, CircleShape)
                            .clickable { selectedAvatar = av },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = av, fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Strand Choices
            Text(
                text = "Antas o Senior High Strand:",
                color = Color(0xFFCBD5E1),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                strands.chunked(3).forEach { chunk ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        chunk.forEach { strand ->
                            val isSelected = selectedStrand == strand
                            Surface(
                                color = if (isSelected) AccentBlue else CardNavyBg,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) LightBlue else BorderNavy
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedStrand = strand }
                            ) {
                                Text(
                                    text = strand.badge,
                                    color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    if (editedAlias.isNotBlank()) {
                        onUpdateProfile(editedAlias.trim(), selectedAvatar, selectedStrand)
                        isSavedNotificationShowing = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_profile_button"),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("I-save ang Profile", color = Color.White, fontWeight = FontWeight.Bold)
            }

            if (isSavedNotificationShowing) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "✓ Matagumpay na na-update ang iyong profile!",
                    color = Color(0xFF22C55E),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Notification Preferences
            Text(
                text = "Mga Kagustuhan sa Notification",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavyBg),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tunog ng Mensahe", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Magpatunog kapag may bagong chat", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                        Switch(
                            checked = soundNotificationsEnabled,
                            onCheckedChange = { soundNotificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = LightBlue, checkedTrackColor = AccentBlue)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderNavy))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Mga Kwento sa Lounge", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Ipaalam ang bagong mga post sa tambayan", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                        Switch(
                            checked = loungeNotificationsEnabled,
                            onCheckedChange = { loungeNotificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = LightBlue, checkedTrackColor = AccentBlue)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Blocked Users Section
            Text(
                text = "Naka-block na mga User (${blockedUsers.size})",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (blockedUsers.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardNavyBg),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Wala kang naka-block na user sa kasalukuyan. Ang mga mensahe mula sa lahat ng mag-aaral ay makikita.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    blockedUsers.forEach { blocked ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardNavyBg),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = blocked.avatar, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = blocked.alias,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Na-block noong ${blocked.blockedAt}",
                                            color = Color(0xFF64748B),
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                OutlinedButton(
                                    onClick = { onUnblockUser(blocked.alias) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LightBlue),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, LightBlue)
                                ) {
                                    Text("I-unblock", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
