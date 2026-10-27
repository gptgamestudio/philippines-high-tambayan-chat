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
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Jetpack Compose implementation of user's Flutter quick bar:
 * - Square Button 1: General Tambayan (Chat)
 * - Square Button 2: Campus Lounge / Safety (Lounge)
 * - Square Button 3: Profile / Settings (Profile)
 *
 * Matching exact dimensions:
 * - 60dp x 60dp container
 * - 12dp rounded corner
 * - Blue with 0.2 opacity background
 * - Blue accent 1dp border
 * - 24dp white icon
 * - 4dp spacing
 * - 10sp white text
 */
@Composable
fun TambayanSquareQuickBar(
    onChatClick: () -> Unit,
    onLoungeClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeTab: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag("tambayan_square_quick_bar"),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Square Button 1: General Tambayan
        SquareNavigationButton(
            icon = Icons.Outlined.ChatBubbleOutline,
            label = "Chat",
            testTag = "square_btn_chat",
            isActive = activeTab == "Chat",
            onTap = onChatClick
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Square Button 2: Campus Lounge / Safety
        SquareNavigationButton(
            icon = Icons.Outlined.School,
            label = "Lounge",
            testTag = "square_btn_lounge",
            isActive = activeTab == "Lounge",
            onTap = onLoungeClick
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Square Button 3: Profile / Settings
        SquareNavigationButton(
            icon = Icons.Outlined.Person,
            label = "Profile",
            testTag = "square_btn_profile",
            isActive = activeTab == "Profile",
            onTap = onProfileClick
        )
    }
}

/**
 * Reusable square button helper matching Flutter `_buildSquareButton`
 */
@Composable
fun SquareNavigationButton(
    icon: ImageVector,
    label: String,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "square_nav_button",
    isActive: Boolean = false,
    borderColor: Color = Color(0xFF448AFF), // Colors.blueAccent
    bgColor: Color = Color(0xFF2196F3).copy(alpha = 0.2f) // Colors.blue.withOpacity(0.2)
) {
    val shape = RoundedCornerShape(12.dp)
    val background = if (isActive) Color(0xFF2196F3).copy(alpha = 0.45f) else bgColor
    val border = if (isActive) Color(0xFF90CAF9) else borderColor

    Box(
        modifier = modifier
            .size(60.dp)
            .clip(shape)
            .background(background)
            .border(width = 1.dp, color = border, shape = shape)
            .clickable(onClick = onTap)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}
