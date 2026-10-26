package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TambayanGold
import com.example.ui.theme.TambayanNavyElevated
import com.example.ui.theme.TambayanTextLight

@Composable
fun TopKidsBar(
    title: String,
    starsCount: Int,
    onBackClick: (() -> Unit)? = null,
    onSpeakClick: (() -> Unit)? = null,
    onLanguageToggle: (() -> Unit)? = null,
    languageLabel: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button or Mascot Badge
            if (onBackClick != null) {
                Box(
                    modifier = Modifier
                        .testTag("btn_back")
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(TambayanNavyElevated)
                        .border(1.5.dp, Color(0xFF475569), CircleShape)
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Bumalik",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                Text(
                    text = "🇵🇭",
                    fontSize = 28.sp
                )
            }

            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                ),
                color = TambayanTextLight,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            )

            // Right Action Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Language Toggle Pill
                if (onLanguageToggle != null && languageLabel != null) {
                    Box(
                        modifier = Modifier
                            .testTag("btn_language_toggle")
                            .clip(RoundedCornerShape(14.dp))
                            .background(TambayanNavyElevated)
                            .border(1.5.dp, Color(0xFF475569), RoundedCornerShape(14.dp))
                            .clickable { onLanguageToggle() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = languageLabel,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TambayanTextLight
                        )
                    }
                }

                // Audio Read Button
                if (onSpeakClick != null) {
                    Box(
                        modifier = Modifier
                            .testTag("btn_speak")
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(TambayanNavyElevated)
                            .border(1.5.dp, Color(0xFF475569), CircleShape)
                            .clickable { onSpeakClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Pakinggan ang Boses",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Star Reward Badge
                if (starsCount >= 0) {
                    Box(
                        modifier = Modifier
                            .testTag("star_counter_badge")
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF312E81))
                            .border(1.5.dp, Color(0xFF4338CA), RoundedCornerShape(16.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Mga Bituin",
                                tint = TambayanGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "$starsCount",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = TambayanGold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TambayanTopBar(
    title: String,
    starsCount: Int,
    onBackClick: (() -> Unit)? = null,
    onSpeakClick: (() -> Unit)? = null,
    onLanguageToggle: (() -> Unit)? = null,
    languageLabel: String? = null,
    modifier: Modifier = Modifier
) {
    TopKidsBar(
        title = title,
        starsCount = starsCount,
        onBackClick = onBackClick,
        onSpeakClick = onSpeakClick,
        onLanguageToggle = onLanguageToggle,
        languageLabel = languageLabel,
        modifier = modifier
    )
}
