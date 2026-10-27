package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PreschoolCoral
import com.example.ui.theme.PreschoolGreen
import com.example.ui.theme.PreschoolTextDark
import com.example.ui.theme.PreschoolYellow

@Composable
fun RewardStarCelebration(
    visible: Boolean,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(tween(400, easing = FastOutSlowInEasing)) + fadeIn(),
        exit = scaleOut(tween(300)) + fadeOut(),
        modifier = modifier
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "star_pulse")
        val rotation by infiniteTransition.animateFloat(
            initialValue = -12f,
            targetValue = 12f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "star_rotation"
        )
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(450, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "star_scale"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White)
                    .border(6.dp, PreschoolYellow, RoundedCornerShape(32.dp))
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Bursting Stars & Emojis
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🎉", fontSize = 38.sp)
                    Text(
                        text = "⭐",
                        fontSize = 80.sp,
                        modifier = Modifier
                            .scale(scale)
                            .rotate(rotation)
                    )
                    Text(text = "🌟", fontSize = 38.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "YEHEY!",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = PreschoolCoral
                )

                Text(
                    text = "Super Star Answer!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PreschoolTextDark
                )

                Spacer(modifier = Modifier.height(24.dp))

                TactileButton(
                    onClick = onContinue,
                    backgroundColor = PreschoolGreen,
                    shadowColor = Color(0xFF1B5E20),
                    testTag = "btn_reward_continue"
                ) {
                    Text(
                        text = "Keep Playing! 🚀",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }
    }
}
