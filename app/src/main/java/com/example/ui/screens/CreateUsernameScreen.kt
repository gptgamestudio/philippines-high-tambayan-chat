package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HighSchoolStrand
import com.example.ui.theme.TambayanBlue
import com.example.ui.theme.TambayanGold
import com.example.ui.theme.TambayanGreen
import com.example.ui.theme.TambayanNavyBg
import com.example.ui.theme.TambayanNavyCard
import com.example.ui.theme.TambayanNavyElevated
import com.example.ui.theme.TambayanRed
import com.example.ui.theme.TambayanTextLight
import com.example.ui.theme.TambayanTextMuted

@Composable
fun CreateUsernameScreen(
    initialAlias: String = "",
    initialAvatar: String = "😎",
    initialStrand: HighSchoolStrand = HighSchoolStrand.SHS_STEM,
    onEnterDashboard: (alias: String, avatar: String, strand: HighSchoolStrand) -> Unit,
    modifier: Modifier = Modifier
) {
    var aliasInput by remember { mutableStateOf(initialAlias.ifBlank { "PinoyHighStudent" }) }
    var selectedAvatar by remember { mutableStateOf(initialAvatar) }
    var selectedStrand by remember { mutableStateOf(initialStrand) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    val availableAvatars = listOf(
        "😎", "🎓", "🎒", "👑", "🌸", "⚡", "🎧", "🎮", "💼", "🛠️", "📚", "🏀"
    )

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
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        TambayanNavyBg,
                        Color(0xFF0D1B2A),
                        Color(0xFF0B1420)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Brand Header
            Surface(
                shape = CircleShape,
                color = TambayanGold.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(2.dp, TambayanGold),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "🇵🇭",
                        fontSize = 40.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "PHILIPPINES HIGH",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TambayanGold,
                letterSpacing = 2.sp
            )

            Text(
                text = "TAMBAYAN CHAT",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Text(
                text = "Piliin ang iyong Student Alias at Avatar para pumasok sa tambayan. Naka-lock at naka-save ito sa iyong device!",
                style = MaterialTheme.typography.bodyMedium,
                color = TambayanTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Main Student Profile Creator Card
            Card(
                colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF2563EB)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar Live Preview
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(TambayanBlue)
                            .border(2.dp, TambayanGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(selectedAvatar, fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "1. PUMILI NG AVATAR",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TambayanGold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(availableAvatars) { avatar ->
                            val isSelected = avatar == selectedAvatar
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) TambayanGold.copy(alpha = 0.3f) else TambayanNavyElevated
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) TambayanGold else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedAvatar = avatar },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(avatar, fontSize = 22.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "2. ILAGAY ANG IYONG USERNAME / ALIAS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TambayanGold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = aliasInput,
                        onValueChange = {
                            aliasInput = it
                            errorMessage = null
                        },
                        placeholder = { Text("Halimbawa: Mark_STEM12 o Bea_HUMSS") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = TambayanGold
                            )
                        },
                        singleLine = true,
                        maxLines = 1,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TambayanGold,
                            unfocusedBorderColor = TambayanNavyElevated,
                            focusedContainerColor = TambayanNavyBg,
                            unfocusedContainerColor = TambayanNavyBg,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (aliasInput.isNotBlank()) {
                                    onEnterDashboard(aliasInput.trim(), selectedAvatar, selectedStrand)
                                }
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "3. PILIIN ANG IYONG GRADE O STRAND",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TambayanGold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(strands) { strand ->
                            val isSelected = strand == selectedStrand
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) TambayanBlue else TambayanNavyElevated
                                    )
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) TambayanGold else Color(0xFF475569),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedStrand = strand }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = strand.badge,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else TambayanTextMuted
                                    )
                                }
                            }
                        }
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = TambayanRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Big Enter Dashboard Button
                    Button(
                        onClick = {
                            val cleanAlias = aliasInput.trim()
                            if (cleanAlias.isBlank()) {
                                errorMessage = "Mangyaring maglagay ng username o alias."
                            } else if (cleanAlias.length < 3) {
                                errorMessage = "Dapat hindi bababa sa 3 letra ang username."
                            } else {
                                onEnterDashboard(cleanAlias, selectedAvatar, selectedStrand)
                            }
                        },
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TambayanGold,
                            contentColor = Color(0xFF1E1B4B)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("enter_dashboard_button")
                    ) {
                        Text(
                            text = "PUMASOK SA TAMBAYAN! 🚀",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Safe & Direct Info Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = TambayanGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Naka-lock sa Device",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Hindi mawawala",
                            fontSize = 10.sp,
                            color = TambayanTextMuted
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = TambayanGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "100% Libreng Chat",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Walang login hassle",
                            fontSize = 10.sp,
                            color = TambayanTextMuted
                        )
                    }
                }
            }
        }
    }
}
