package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.model.PinoyCampusTrivia
import com.example.ui.components.TactileButton
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
fun CampusTriviaScreen(
    triviaList: List<PinoyCampusTrivia>,
    starsCount: Int,
    onEarnStars: (Int) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    val currentTrivia = triviaList.getOrNull(currentIndex) ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TambayanNavyBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // TOP APP BAR
        Surface(
            color = TambayanNavyCard,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TambayanNavyElevated)
                        .testTag("btn_back_trivia")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Campus Brain & Trivia Challenge 🧠",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = TambayanTextLight
                    )
                    Text(
                        text = "Tanong ${currentIndex + 1} ng ${triviaList.size} • ${currentTrivia.category}",
                        fontSize = 11.sp,
                        color = TambayanTextMuted
                    )
                }

                // Stars Counter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF312E81))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Stars",
                            tint = TambayanGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$starsCount",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = TambayanGold
                        )
                    }
                }
            }
        }

        // TRIVIA CONTENT
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // QUESTION CARD
            Card(
                colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TambayanNavyElevated, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(TambayanBlue.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = currentTrivia.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TambayanBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentTrivia.question,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TambayanTextLight,
                        lineHeight = 24.sp
                    )
                }
            }

            // OPTIONS
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                currentTrivia.options.forEachIndexed { index, optionText ->
                    val isSelected = selectedOption == index
                    val isCorrect = index == currentTrivia.correctIndex

                    val borderColor = when {
                        !isAnswered -> if (isSelected) TambayanBlue else TambayanNavyElevated
                        isCorrect -> TambayanGreen
                        isSelected -> TambayanRed
                        else -> TambayanNavyElevated
                    }

                    val containerColor = when {
                        !isAnswered -> if (isSelected) TambayanNavyElevated else TambayanNavyCard
                        isCorrect -> Color(0xFF064E3B)
                        isSelected -> Color(0xFF7F1D1D)
                        else -> TambayanNavyCard
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                            .clickable(enabled = !isAnswered) {
                                selectedOption = index
                                isAnswered = true
                                if (index == currentTrivia.correctIndex) {
                                    score += 1
                                    onEarnStars(5)
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = optionText,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = TambayanTextLight,
                                modifier = Modifier.weight(1f)
                            )

                            if (isAnswered) {
                                if (isCorrect) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Correct",
                                        tint = TambayanGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Wrong",
                                        tint = TambayanRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // EXPLANATION
            AnimatedVisibility(visible = isAnswered) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💡", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedOption == currentTrivia.correctIndex) "Tama ang iyong sagot! (+5 Stars 🌟)" else "Mali, pero okay lang! Natututo ka!",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedOption == currentTrivia.correctIndex) TambayanGold else Color(0xFFFCA5A5)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentTrivia.explanation,
                            fontSize = 12.sp,
                            color = Color(0xFFE0E7FF),
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // BACK AND NEXT ARROW NAVIGATION FOR TRIVIA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = {
                        if (triviaList.isNotEmpty()) {
                            currentIndex = if (currentIndex > 0) currentIndex - 1 else triviaList.size - 1
                            selectedOption = null
                            isAnswered = false
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = TambayanNavyElevated,
                    modifier = Modifier.testTag("btn_prev_trivia")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Nakaraang Tanong",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "◀ Nakaraan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Text(
                    text = "${currentIndex + 1} / ${triviaList.size}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = TambayanGold
                )

                Surface(
                    onClick = {
                        if (triviaList.isNotEmpty()) {
                            currentIndex = (currentIndex + 1) % triviaList.size
                            selectedOption = null
                            isAnswered = false
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = TambayanNavyElevated,
                    modifier = Modifier.testTag("btn_next_trivia_arrow")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Susunod ▶",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Susunod na Tanong",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // NEXT QUESTION CONFIRMATION BUTTON
            if (isAnswered) {
                TactileButton(
                    onClick = {
                        if (currentIndex < triviaList.size - 1) {
                            currentIndex += 1
                            selectedOption = null
                            isAnswered = false
                        } else {
                            // Loop or finish
                            currentIndex = 0
                            selectedOption = null
                            isAnswered = false
                        }
                    },
                    backgroundColor = TambayanBlue,
                    shadowColor = Color(0xFF1D4ED8),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_next_trivia")
                ) {
                    Text(
                        text = if (currentIndex < triviaList.size - 1) "Susunod na Tanong ➡️" else "Magsimula Muli 🔄",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
