package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

/**
 * A chunky, tactile button designed specifically for preschool hands.
 * Mimics physical toy buttons with 3D depth and satisfying tactile press.
 */
@Composable
fun TactileButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF2979FF),
    shadowColor: Color = Color(0xFF1565C0),
    cornerRadius: Dp = 24.dp,
    testTag: String = "tactile_button",
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shape = RoundedCornerShape(cornerRadius)

    val shadowDepth = 6.dp
    val pressOffset = if (isPressed) 4.dp else 0.dp

    Box(
        modifier = modifier
            .testTag(testTag)
            .defaultMinSize(minWidth = 56.dp, minHeight = 52.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // Bottom 3D shadow layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = shadowDepth)
                .clip(shape)
                .background(shadowColor)
        )

        // Top button face
        Box(
            modifier = Modifier
                .offset(y = pressOffset)
                .clip(shape)
                .background(backgroundColor)
                .border(2.dp, Color.White.copy(alpha = 0.4f), shape)
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                content()
            }
        }
    }
}
