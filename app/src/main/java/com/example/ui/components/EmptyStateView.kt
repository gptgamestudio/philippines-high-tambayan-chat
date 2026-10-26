package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.PreschoolBlue
import com.example.ui.theme.PreschoolTextDark
import com.example.ui.theme.PreschoolTextSoft
import com.example.ui.theme.PreschoolYellow

/**
 * Cute Empty & Loading State component as required by Technical Constraints.
 * Used when assets are loading or network connectivity is unavailable.
 */
@Composable
fun EmptyStateView(
    isLoading: Boolean = false,
    title: String = "Uh-oh! Cloud is Sleeping",
    subtitle: String = "We are waiting for friendly animal pictures to load!",
    onRetryClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bouncing_cloud")
    val bounceScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("empty_state_container")
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .border(3.dp, PreschoolYellow.copy(alpha = 0.6f), RoundedCornerShape(28.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Cute animated graphic
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD))
                    .scale(bounceScale),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = PreschoolBlue,
                        strokeWidth = 6.dp,
                        modifier = Modifier.size(68.dp)
                    )
                    Text(text = "🎈", fontSize = 32.sp)
                } else {
                    Text(text = "☁️", fontSize = 56.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (isLoading) "Loading Fun…" else title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = PreschoolTextDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                fontSize = 16.sp,
                color = PreschoolTextSoft,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            if (!isLoading && onRetryClick != null) {
                Spacer(modifier = Modifier.height(24.dp))
                TactileButton(
                    onClick = onRetryClick,
                    backgroundColor = PreschoolYellow,
                    shadowColor = Color(0xFFF57F17),
                    testTag = "btn_retry_loading"
                ) {
                    Text(
                        text = "Try Again! 🔄",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = PreschoolTextDark
                    )
                }
            }
        }
    }
}
