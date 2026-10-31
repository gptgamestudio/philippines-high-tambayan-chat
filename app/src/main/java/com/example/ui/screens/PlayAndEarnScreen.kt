package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.AdSystemState
import com.example.ui.components.TactileButton
import com.example.ui.components.TopKidsBar
import com.example.ui.theme.TambayanGold
import com.example.ui.theme.TambayanGoldDark
import com.example.ui.theme.TambayanGreen
import com.example.ui.theme.TambayanNavyBg
import com.example.ui.theme.TambayanNavyCard
import com.example.ui.theme.TambayanNavyElevated
import com.example.ui.theme.TambayanRed
import com.example.ui.theme.TambayanTextLight
import com.example.ui.theme.TambayanTextMuted

@Composable
fun PlayAndEarnScreen(
    adState: AdSystemState,
    starsCount: Int,
    onWatchAdClick: () -> Unit,
    onBackClick: () -> Unit,
    onSpeakClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val maxDaily = adState.maxDailyRewardedAds
    val watched = adState.adsWatchedToday
    val canWatch = watched < maxDaily
    val remainingHours = (adState.millisUntilReset / (1000 * 60 * 60)).coerceAtLeast(0)
    val remainingMins = ((adState.millisUntilReset / (1000 * 60)) % 60).coerceAtLeast(0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TambayanNavyBg)
            .navigationBarsPadding()
    ) {
        TopKidsBar(
            title = "Play & Earn (Tambay Stars)",
            starsCount = starsCount,
            onBackClick = onBackClick,
            onSpeakClick = onSpeakClick
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stars Wallet Balance Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(TambayanNavyCard)
                    .border(2.dp, TambayanGold, RoundedCornerShape(26.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "🎁", fontSize = 42.sp)
                    Column {
                        Text(
                            text = "Aking Tambay Stars Wallet",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TambayanTextMuted
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = TambayanGold,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "$starsCount Tambay Stars",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = TambayanGold
                            )
                        }
                    }
                }
            }

            // 10-Ads Daily Progress Card (Resets every 24 hours)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(TambayanNavyCard)
                    .border(1.dp, TambayanNavyElevated, RoundedCornerShape(26.dp))
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "10 Ads bawat 24 Oras",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TambayanTextLight
                    )

                    Text(
                        text = "Napanood Ngayong Araw: $watched / $maxDaily",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (canWatch) TambayanGreen else TambayanRed
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 10 Slots Grid for visual representation
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                    ) {
                        items(maxDaily) { index ->
                            val isWatchedSlot = index < watched
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isWatchedSlot) Color(0xFF78350F) else TambayanNavyElevated)
                                    .border(
                                        2.dp,
                                        if (isWatchedSlot) TambayanGold else Color(0xFF475569),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isWatchedSlot) "⭐" else "${index + 1}",
                                    fontSize = if (isWatchedSlot) 20.sp else 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isWatchedSlot) TambayanGold else TambayanTextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 24-Hour Reset Countdown
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF064E3B))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "⏰ Magre-reset sa loob ng: ${remainingHours}h ${remainingMins}m",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA7F3D0)
                        )
                    }
                }
            }

            // Big Tactile Watch Ad Button
            if (canWatch) {
                TactileButton(
                    onClick = onWatchAdClick,
                    backgroundColor = TambayanGold,
                    shadowColor = TambayanGoldDark,
                    cornerRadius = 24.dp,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_watch_rewarded_ad"
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color(0xFF451A03),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Manood ng Ad (+${adState.rewardPerAd} Stars) 🎁",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF451A03)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(TambayanNavyCard)
                        .border(1.dp, TambayanNavyElevated, RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎉 Napanood mo na ang lahat ng 10 ads ngayon!\nBumalik bukas para sa mga bagong gantimpala!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TambayanTextLight,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Notification / Status Message
            AnimatedVisibility(visible = adState.lastAdMessage.isNotEmpty()) {
                Text(
                    text = adState.lastAdMessage,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TambayanGold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
