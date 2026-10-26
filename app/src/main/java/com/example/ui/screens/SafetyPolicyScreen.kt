package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.model.BlockedUser
import com.example.model.CampusSafetyPolicyItem
import com.example.model.ReportedMessage
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyPolicyScreen(
    policies: List<CampusSafetyPolicyItem>,
    blockedUsers: List<BlockedUser>,
    reportedMessages: List<ReportedMessage>,
    isPolicyAccepted: Boolean,
    onAcceptPolicy: () -> Unit,
    onUnblockUser: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Policies, 1: Blocked Users, 2: Helplines

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
                        .testTag("btn_back_safety")
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
                        text = "Campus Safety & Policy Center",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = TambayanTextLight
                    )
                    Text(
                        text = "Proteksyon sa Kabataan at Alituntunin ng Tambayan",
                        fontSize = 11.sp,
                        color = TambayanTextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(TambayanGreen.copy(alpha = 0.2f))
                        .border(1.dp, TambayanGreen, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Verified Policy",
                            tint = TambayanGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SECURE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = TambayanGreen
                        )
                    }
                }
            }
        }

        // TAB SWITCHER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TambayanNavyCard)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                label = { Text("📜 Alituntunin at Batas", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TambayanBlue,
                    selectedLabelColor = Color.White,
                    containerColor = TambayanNavyElevated,
                    labelColor = TambayanTextMuted
                )
            )
            FilterChip(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                label = { Text("🚫 Blocked (${blockedUsers.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TambayanBlue,
                    selectedLabelColor = Color.White,
                    containerColor = TambayanNavyElevated,
                    labelColor = TambayanTextMuted
                )
            )
            FilterChip(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                label = { Text("📞 Helplines", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TambayanBlue,
                    selectedLabelColor = Color.White,
                    containerColor = TambayanNavyElevated,
                    labelColor = TambayanTextMuted
                )
            )
        }

        // BACK & NEXT ARROW NAVIGATION FOR POLICY TABS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TambayanNavyCard)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = { selectedTab = if (selectedTab > 0) selectedTab - 1 else 2 },
                shape = RoundedCornerShape(10.dp),
                color = TambayanNavyElevated,
                modifier = Modifier.testTag("btn_prev_safety_tab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Nakaraang Seksyon",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "◀ Nakaraan",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Text(
                text = when (selectedTab) {
                    0 -> "Seksyon 1/3: Alituntunin"
                    1 -> "Seksyon 2/3: Blocked Users"
                    else -> "Seksyon 3/3: Helplines"
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TambayanGold
            )

            Surface(
                onClick = { selectedTab = (selectedTab + 1) % 3 },
                shape = RoundedCornerShape(10.dp),
                color = TambayanNavyElevated,
                modifier = Modifier.testTag("btn_next_safety_tab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Susunod ▶",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Susunod na Seksyon",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }

        // CONTENT SECTION
        when (selectedTab) {
            0 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // POLICY ACCEPTANCE STATUS BANNER
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isPolicyAccepted) Color(0xFF064E3B) else Color(0xFF78350F)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isPolicyAccepted) Icons.Default.CheckCircle else Icons.Default.Security,
                                        contentDescription = null,
                                        tint = if (isPolicyAccepted) TambayanGreen else TambayanGold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (isPolicyAccepted) "Kasunduan: Tinanggap ang Campus Safety Policy" else "Kailangan Tanggapin ang Alituntunin",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (isPolicyAccepted) "Aktibo ang proteksyon laban sa cyberbullying at harassment." else "Pindutin sa ibaba upang lagdaan ang patakaran sa ligtas na tambayan.",
                                            fontSize = 11.sp,
                                            color = Color(0xFFFDE68A)
                                        )
                                    }
                                }

                                if (!isPolicyAccepted) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    TactileButton(
                                        onClick = onAcceptPolicy,
                                        backgroundColor = TambayanGold,
                                        shadowColor = Color(0xFFB45309),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("btn_accept_policy")
                                    ) {
                                        Text(
                                            text = "Tanggapin at Sumang-ayon sa Patakaran ✍️",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF451A03)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // POLICY CARDS
                    items(policies) { policy ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, TambayanNavyElevated, RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(policy.iconEmoji, fontSize = 28.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = policy.title,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TambayanTextLight
                                        )
                                        Text(
                                            text = policy.legalBasis,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TambayanGold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = policy.description,
                                    fontSize = 12.sp,
                                    color = TambayanTextMuted,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }

                    // UGC MODERATION NOTICE
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Paano Gumagana ang Pag-uulat (Reporting) at Pag-block:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE0E7FF)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "• Pindutin nang matagal o tapikin ang anumang bubble sa chat para sa mga opsyon.\n" +
                                            "• Piliin ang 'I-report ang Mensahe' kung may cyberbullying o bastos na komento.\n" +
                                            "• Piliin ang 'I-block ang User' upang agad na itago ang lahat ng mensahe mula sa taong iyon.\n" +
                                            "• Awtomatikong sinasala ng system ang mga masasamang salita para sa kabutihan ng lahat.",
                                    fontSize = 12.sp,
                                    color = Color(0xFFC7D2FE),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
            1 -> {
                // BLOCKED USERS LIST
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Listahan ng mga Naka-Block na Profile",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TambayanTextLight
                        )
                        Text(
                            text = "Ang mga mensahe mula sa mga taong ito ay hindi lalabas sa iyong tambayan feed.",
                            fontSize = 12.sp,
                            color = TambayanTextMuted
                        )
                    }

                    if (blockedUsers.isEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("🕊️", fontSize = 44.sp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Walang Naka-Block na User",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TambayanTextLight
                                    )
                                    Text(
                                        text = "Mabuting makisama ang mga tambayers! Kung may makatagpo kang pasaway, maaari mo silang i-block sa chat.",
                                        fontSize = 12.sp,
                                        color = TambayanTextMuted,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        items(blockedUsers) { user ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(user.avatar, fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = user.alias,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TambayanTextLight
                                            )
                                            Text(
                                                text = "Na-block noong: ${user.blockedAt}",
                                                fontSize = 11.sp,
                                                color = TambayanTextMuted
                                            )
                                        }
                                    }

                                    TactileButton(
                                        onClick = { onUnblockUser(user.alias) },
                                        backgroundColor = TambayanNavyElevated,
                                        shadowColor = Color(0xFF1E293B)
                                    ) {
                                        Text(
                                            text = "I-unblock",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // PHILIPPINES YOUTH & CHILD PROTECTION HELPLINES
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "🇵🇭 Mga Opisyal na Emergency & Child Protection Hotlines",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TambayanTextLight
                        )
                        Text(
                            text = "Kung kailangan mo o ng kaklase mo ng tulong, narito ang mga libreng ahensya ng pamahalaan.",
                            fontSize = 12.sp,
                            color = TambayanTextMuted
                        )
                    }

                    item {
                        HelplineCard(
                            agency = "DepEd Learner Rights and Protection Office (LRPO)",
                            purpose = "Tulong para sa pambu-bully, karahasan sa paaralan, o abuso",
                            contact = "Hotline: (02) 8637-2306 | Email: lrpo@deped.gov.ph",
                            badge = "DepEd Official"
                        )
                    }
                    item {
                        HelplineCard(
                            agency = "National Center for Mental Health (NCMH) Crisis Helpline",
                            purpose = "24/7 Libreng gabay sa kalusugan ng kaisipan at pag-uusap",
                            contact = "Hotline: 1553 | Cel: 0917-899-USAP (8727)",
                            badge = "24/7 Free"
                        )
                    }
                    item {
                        HelplineCard(
                            agency = "Bantay Bata 163 Helpline",
                            purpose = "Proteksyon at agarang tulong para sa mga kabataan",
                            contact = "Dial 163 (Libre sa buong Pilipinas)",
                            badge = "Bantay Bata"
                        )
                    }
                    item {
                        HelplineCard(
                            agency = "PNP Women and Children Protection Center (WCPC)",
                            purpose = "Pagsugpo sa cyberbullying, online threats, at krimen laban sa kabataan",
                            contact = "Hotline: (02) 8532-6690 / Aling Pulis: 0919-777-7377",
                            badge = "PNP Safety"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HelplineCard(
    agency: String,
    purpose: String,
    contact: String,
    badge: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = TambayanNavyCard),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, TambayanNavyElevated, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = agency,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TambayanTextLight,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TambayanBlue.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TambayanBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = purpose,
                fontSize = 12.sp,
                color = TambayanTextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Contact",
                    tint = TambayanGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = contact,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TambayanGreen
                )
            }
        }
    }
}
