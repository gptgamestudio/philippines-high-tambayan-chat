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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Top Navigation Switcher matching HTML reference:
 * - App Title: Campus Hub PH
 * - Subtitle: {alias} • Grade 10 Completer
 * - Buttons: [Main Dashboard] [Student Dash & Chatrooms]
 */
@Composable
fun CampusHubTopBar(
    currentAlias: String,
    strandOrGrade: String = "Grade 10 Completer",
    isMainDashboardActive: Boolean,
    onMainDashboardClick: () -> Unit,
    onStudentDashClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF0F172A), // bg-slate-900
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)), // border-slate-800
        shadowElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Branding & User Alias
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(6.dp, RoundedCornerShape(12.dp), spotColor = Color(0xFF3B82F6))
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2563EB)), // bg-blue-600
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Campus Hub",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Campus Hub PH",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${currentAlias.ifBlank { "benjie1978" }} • $strandOrGrade",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8) // text-slate-400
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Switcher Buttons (Main Dashboard vs Student Dash)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Main Dashboard Button
                val mainBg = if (isMainDashboardActive) Color(0xFF2563EB) else Color(0xFF1E293B)
                val mainText = if (isMainDashboardActive) Color.White else Color(0xFFCBD5E1)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(mainBg)
                        .clickable(onClick = onMainDashboardClick)
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                        .testTag("btn_top_switcher_main_dash"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Main Dashboard",
                            tint = mainText,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Main Dash",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = mainText
                        )
                    }
                }

                // Student Dash & Chatrooms Button
                val chatBg = if (!isMainDashboardActive) Color(0xFF2563EB) else Color(0xFF1E293B)
                val chatText = if (!isMainDashboardActive) Color.White else Color(0xFFCBD5E1)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(chatBg)
                        .clickable(onClick = onStudentDashClick)
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                        .testTag("btn_top_switcher_chatrooms"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Comment,
                            contentDescription = "Student Dash",
                            tint = chatText,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Chatrooms",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = chatText
                        )
                    }
                }
            }
        }
    }
}
