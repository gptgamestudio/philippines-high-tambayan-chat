package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.TambayanBlue
import com.example.ui.theme.TambayanCyan
import com.example.ui.theme.TambayanGold
import com.example.ui.theme.TambayanGreen
import com.example.ui.theme.TambayanNavyCard
import com.example.ui.theme.TambayanNavyElevated
import com.example.ui.theme.TambayanTextLight
import com.example.ui.theme.TambayanTextMuted

@Composable
fun KidAdDialog(
    adType: String, // "INTERSTITIAL" or "REWARDED"
    countdownSeconds: Int,
    rewardPerAd: Int = 10,
    onDismiss: () -> Unit
) {
    val scaleAnim = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1.12f,
            animationSpec = infiniteRepeatable(
                animation = tween(700, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Dialog(
        onDismissRequest = {
            if (countdownSeconds <= 1) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .background(TambayanNavyCard)
                .border(2.dp, TambayanGold, RoundedCornerShape(24.dp))
                .padding(22.dp)
                .testTag("dialog_kid_ad"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF064E3B))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = TambayanGreen,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Google AdMob Verified • Safe Sponsor",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TambayanGreen
                        )
                    }

                    if (countdownSeconds <= 0) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(TambayanNavyElevated)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Ad",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // App & Ad Icon
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .scale(scaleAnim.value)
                        .clip(CircleShape)
                        .background(TambayanBlue)
                        .border(3.dp, TambayanGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🇵🇭", fontSize = 38.sp)
                }

                Text(
                    text = "PHILIPPINES HIGH TAMBAYAN CHAT",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = TambayanGold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "LIBRENG CHAT PARA SA LAHAT\nSponsor Break (Lalabas Bawat 5 Mensahe)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Salamat sa pagsuporta sa ating libreng pambansang tambayan! Walang bayad o credits na kailangan mag-chat.",
                    fontSize = 12.sp,
                    color = TambayanTextMuted,
                    textAlign = TextAlign.Center
                )

                // Ad Unit ID reference matching user AdMob setup
                Text(
                    text = "Ad Unit ID:\nca-app-pub-9786472984542330/7506482083",
                    fontSize = 10.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = Color(0xFF60A5FA),
                    textAlign = TextAlign.Center
                )

                // Countdown Timer Box
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E1B4B))
                        .border(1.5.dp, TambayanGold, RoundedCornerShape(16.dp))
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            tint = TambayanCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (countdownSeconds > 0) "Babalik sa chat: ${countdownSeconds}s" else "Handa na! Mag-chat muli!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = TambayanGold,
                            modifier = Modifier.testTag("text_ad_countdown")
                        )
                    }
                }

                // Progress Indicator
                val maxSeconds = 3f
                val progress = (maxSeconds - countdownSeconds.coerceAtLeast(0)) / maxSeconds
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = TambayanGold,
                    trackColor = TambayanNavyElevated
                )

                // Close / Continue Button
                Surface(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF2563EB),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .testTag("btn_close_interstitial_ad")
                ) {
                    Text(
                        text = "Ipasara ang Ad / Magpatuloy sa Chat",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }

                Text(
                    text = "🔒 Safe & Secure Campus Environment • RA 10627 Compliant",
                    fontSize = 10.sp,
                    color = TambayanTextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
