package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.LoungePost
import com.example.model.ReportReason
import com.example.model.StudentProfile

private val DarkNavyBg = Color(0xFF0B1120)
private val CardNavyBg = Color(0xFF1E293B)
private val AccentBlue = Color(0xFF2563EB)
private val LightBlue = Color(0xFF38BDF8)
private val BorderNavy = Color(0xFF334155)
private val RedAccent = Color(0xFFEF4444)

@Composable
fun LoungeScreen(
    posts: List<LoungePost>,
    studentProfile: StudentProfile,
    onCreatePost: (category: String, title: String, content: String) -> Unit,
    onAddReply: (postId: String, content: String) -> Unit,
    onTogglePostLike: (postId: String) -> Unit,
    onToggleReplyLike: (postId: String, replyId: String) -> Unit,
    onReportPost: (postId: String, reason: ReportReason) -> Unit,
    onDeletePost: (postId: String) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Lahat") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var postToReport by remember { mutableStateOf<LoungePost?>(null) }
    var expandedPostId by remember { mutableStateOf<String?>(null) }
    var replyTextMap by remember { mutableStateOf(mapOf<String, String>()) }

    val categories = listOf("Lahat", "Study & Tips", "Campus Life", "Games & Hobbies", "Arts & Music")

    val filteredPosts = remember(posts, selectedCategory) {
        if (selectedCategory == "Lahat") {
            posts
        } else {
            posts.filter { it.categoryTag.equals(selectedCategory, ignoreCase = true) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("lounge_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("lounge_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Bumalik",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tambayan Lounge & Forum",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ibahagi ang kwento, tanong, o tips sa buong campus",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Categories Filter Bar
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = CardNavyBg,
                            selectedContainerColor = AccentBlue,
                            labelColor = Color(0xFFCBD5E1),
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = BorderNavy,
                            selectedBorderColor = LightBlue
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Posts Feed
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPosts, key = { it.id }) { post ->
                    val isExpanded = expandedPostId == post.id
                    val currentReplyText = replyTextMap[post.id] ?: ""

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardNavyBg),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("lounge_post_${post.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Author Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF334155)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = post.authorAvatar, fontSize = 18.sp)
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = post.authorName,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = AccentBlue.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = post.authorBadge,
                                                color = LightBlue,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${post.categoryTag} • ${post.timestamp}",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp
                                    )
                                }

                                // Actions (Report / Delete)
                                if (post.authorName == studentProfile.alias) {
                                    IconButton(
                                        onClick = { onDeletePost(post.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Burahin",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = { postToReport = post },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Flag,
                                            contentDescription = "I-report",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Post Title & Content
                            Text(
                                text = post.title,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = post.content,
                                color = Color(0xFFCBD5E1),
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Bottom Engagement Row (Like & Comments count)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onTogglePostLike(post.id) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Gusto ko",
                                            tint = if (post.isLiked) RedAccent else Color(0xFF94A3B8)
                                        )
                                    }
                                    Text(
                                        text = "${post.likesCount}",
                                        color = if (post.isLiked) RedAccent else Color(0xFF94A3B8),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Spacer(modifier = Modifier.width(16.dp))

                                    IconButton(
                                        onClick = {
                                            expandedPostId = if (isExpanded) null else post.id
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ChatBubbleOutline,
                                            contentDescription = "Mga Sagot",
                                            tint = LightBlue
                                        )
                                    }
                                    Text(
                                        text = "${post.replies.size} sagot",
                                        color = LightBlue,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        expandedPostId = if (isExpanded) null else post.id
                                    }
                                ) {
                                    Text(
                                        text = if (isExpanded) "Itago ang sagot" else "Tingnan ang usapan",
                                        color = LightBlue,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Expanded Replies Thread
                            if (isExpanded) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(BorderNavy)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Existing replies
                                if (post.replies.isEmpty()) {
                                    Text(
                                        text = "Wala pang sumasagot. Maging una sa pagkomento!",
                                        color = Color(0xFF64748B),
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                } else {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        post.replies.forEach { reply ->
                                            Surface(
                                                color = Color(0xFF0F172A),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(8.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(text = reply.authorAvatar, fontSize = 14.sp)
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = reply.authorName,
                                                            color = Color.White,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = reply.authorBadge,
                                                            color = LightBlue,
                                                            fontSize = 9.sp
                                                        )
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Text(
                                                            text = reply.timestamp,
                                                            color = Color(0xFF64748B),
                                                            fontSize = 10.sp
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = reply.content,
                                                        color = Color(0xFFE2E8F0),
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Add reply input
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = currentReplyText,
                                        onValueChange = { newText ->
                                            replyTextMap = replyTextMap.toMutableMap().apply { put(post.id, newText) }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("reply_input_${post.id}"),
                                        placeholder = {
                                            Text("Sumagot nang magalang...", color = Color(0xFF64748B), fontSize = 12.sp)
                                        },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = AccentBlue,
                                            unfocusedBorderColor = BorderNavy,
                                            focusedContainerColor = Color(0xFF0F172A),
                                            unfocusedContainerColor = Color(0xFF0F172A)
                                        ),
                                        shape = RoundedCornerShape(20.dp)
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    IconButton(
                                        onClick = {
                                            if (currentReplyText.isNotBlank()) {
                                                onAddReply(post.id, currentReplyText.trim())
                                                replyTextMap = replyTextMap.toMutableMap().apply { remove(post.id) }
                                            }
                                        },
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(AccentBlue)
                                            .testTag("send_reply_button_${post.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Send,
                                            contentDescription = "Ipadala ang sagot",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to Create New Lounge Post
        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
                .testTag("create_lounge_post_fab"),
            containerColor = AccentBlue,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Gumawa ng bagong kwento",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    // Create Lounge Post Dialog
    if (showCreateDialog) {
        var postTitle by remember { mutableStateOf("") }
        var postContent by remember { mutableStateOf("") }
        var postCategory by remember { mutableStateOf("Campus Life") }
        var formError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(
                    text = "Mag-post sa Tambayan Lounge",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Kategorya:",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("Campus Life", "Study & Tips", "Games & Hobbies", "Arts & Music")) { cat ->
                            Surface(
                                color = if (postCategory == cat) AccentBlue else Color(0xFF0F172A),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable { postCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = postTitle,
                        onValueChange = { postTitle = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_post_title_input"),
                        placeholder = { Text("Paksa / Pamagat", color = Color(0xFF64748B), fontSize = 13.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = BorderNavy,
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = postContent,
                        onValueChange = { postContent = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("new_post_content_input"),
                        placeholder = { Text("Isulat ang iyong kwento o tanong...", color = Color(0xFF64748B), fontSize = 13.sp) },
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = BorderNavy,
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    if (formError != null) {
                        Text(text = formError!!, color = RedAccent, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (postTitle.isBlank() || postContent.isBlank()) {
                            formError = "Punan ang pamagat at nilalaman"
                        } else {
                            onCreatePost(postCategory, postTitle.trim(), postContent.trim())
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    modifier = Modifier.testTag("submit_new_post_button")
                ) {
                    Text("I-post", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Kanselahin", color = Color(0xFF94A3B8))
                }
            },
            containerColor = CardNavyBg
        )
    }

    // Report Reason Dialog
    if (postToReport != null) {
        val target = postToReport!!
        var selectedReason by remember { mutableStateOf(ReportReason.BULLYING_HARASSMENT) }

        AlertDialog(
            onDismissRequest = { postToReport = null },
            title = {
                Text(
                    text = "I-report ang Kwento sa Lounge",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Isumbong si ${target.authorName} sa mga sumusunod na dahilan:",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )

                    ReportReason.values().forEach { reason ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReason = reason }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedReason == reason) AccentBlue else Color(0xFF334155))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = reason.displayName,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = reason.tagalogDesc,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReportPost(target.id, selectedReason)
                        postToReport = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedAccent)
                ) {
                    Text("Kumpirmahin ang Report", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { postToReport = null }) {
                    Text("Kanselahin", color = Color(0xFF94A3B8))
                }
            },
            containerColor = CardNavyBg
        )
    }
}
