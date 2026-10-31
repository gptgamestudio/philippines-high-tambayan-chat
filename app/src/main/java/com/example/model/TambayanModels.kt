package com.example.model

enum class HighSchoolStrand(val displayName: String, val badge: String) {
    GRADE_7("Grade 7 (Freshie)", "🎒 G7"),
    GRADE_8("Grade 8", "📘 G8"),
    GRADE_9("Grade 9", "📙 G9"),
    GRADE_10("Grade 10 (Completer)", "🎓 G10"),
    SHS_STEM("SHS STEM (Sci & Math)", "🔬 STEM"),
    SHS_ABM("SHS ABM (Business)", "💼 ABM"),
    SHS_HUMSS("SHS HUMSS (Social Sci)", "📜 HUMSS"),
    SHS_TVL("SHS TVL (Tech-Voc)", "🛠️ TVL"),
    SHS_GAS("SHS GAS (General Acad)", "📚 GAS"),
    ADMIN("Campus Admin & Moderation", "👑 ADMIN")
}

data class TambayanRoom(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val onlineCount: Int,
    val unreadBadge: Int = 0,
    val roomType: String = "Public & Free",
    val adFrequency: String = "Bawat 5 Mensahe"
)

data class RoomActiveMember(
    val name: String,
    val role: String,
    val isVideo: Boolean = false,
    val isOnline: Boolean = true
)

data class ChatMessage(
    val id: String,
    val roomId: String,
    val senderName: String,
    val senderAvatar: String,
    val senderGradeBadge: String,
    val messageText: String,
    val timestamp: String,
    val isUser: Boolean = false,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val isReported: Boolean = false
)

data class StudentProfile(
    val alias: String = "PinoyHighStudent_01",
    val avatar: String = "😎",
    val strand: HighSchoolStrand = HighSchoolStrand.SHS_STEM,
    val tambayStars: Int = 25,
    val vipLevel: String = "Bronze Tambayer",
    val isPolicyAccepted: Boolean = false,
    val role: UserRole = UserRole.MEMBER,
    val email: String? = null
)

data class LoungeReply(
    val id: String,
    val postId: String,
    val authorName: String,
    val authorAvatar: String,
    val authorBadge: String,
    val content: String,
    val timestamp: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)

data class LoungePost(
    val id: String,
    val authorName: String,
    val authorAvatar: String,
    val authorBadge: String,
    val categoryTag: String,
    val title: String,
    val content: String,
    val timestamp: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val replies: List<LoungeReply> = emptyList(),
    val isReported: Boolean = false
)

// ==========================================
// APP SECURITY, POLICY & USER PROTECTION
// ==========================================

enum class ReportReason(val displayName: String, val tagalogDesc: String) {
    BULLYING_HARASSMENT("Cyberbullying & Harassment", "Pambu-bully, panunukso, o pamimilit"),
    INAPPROPRIATE_CONTENT("Inappropriate / NSFW", "Maseselang paksa, kabastusan, o hindi pambata"),
    HATE_SPEECH("Hate Speech & Discrimination", "Pangungutya sa lahi, relihiyon, o kasarian"),
    SPAM_SCAM("Spam & Commercial Scams", "Paulit-ulit na mensahe, scam links, o pagtitinda"),
    PERSONAL_INFO_LEAK("Doxxing / Personal Info", "Pagbabahagi ng address, phone number, o school ID"),
    IMPERSONATION("Fake Account / Impersonation", "Panggagaya sa ibang estudyante o guro"),
    OTHER("Iba Pang Reklamo", "Iba pang paglabag sa alituntunin")
}

data class ReportedMessage(
    val id: String,
    val messageId: String,
    val senderName: String,
    val messageText: String,
    val roomId: String,
    val reason: ReportReason,
    val reportedAt: String,
    val status: String = "PENDING_REVIEW" // PENDING_REVIEW, DISMISSED, REMOVED, USER_BANNED
)

data class BlockedUser(
    val alias: String,
    val avatar: String,
    val blockedAt: String
)

data class CampusSafetyPolicyItem(
    val id: String,
    val title: String,
    val legalBasis: String,
    val description: String,
    val iconEmoji: String
)

// Fun High School Campus Trivia (replaces preschool ABC/Math games)
data class PinoyCampusTrivia(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val category: String
)

// ==========================================
// PRIVATE FRIENDS & 1-ON-1 CHAT / VIDEO CALL
// ==========================================

data class TambayanFriend(
    val id: String = "",
    val alias: String = "",
    val avatar: String = "😎",
    val strand: HighSchoolStrand = HighSchoolStrand.SHS_STEM,
    val isOnline: Boolean = false,
    val statusBio: String = "Kasama sa barkada ✨",
    val unreadCount: Int = 0,
    val addedAt: Long = System.currentTimeMillis()
)

data class FriendRequest(
    val id: String = "",
    val senderUid: String = "",
    val senderAlias: String = "",
    val senderAvatar: String = "😎",
    val senderStrand: String = "SHS_STEM",
    val recipientUid: String = "",
    val recipientAlias: String = "",
    val status: String = "PENDING", // PENDING, ACCEPTED, REJECTED
    val createdAt: Long = System.currentTimeMillis()
)

data class PrivateConversation(
    val conversationId: String = "",
    val participantUids: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastMessage: String = "",
    val lastSenderUid: String = ""
)

data class PrivateChatMessage(
    val id: String = "",
    val friendId: String = "",
    val conversationId: String = "",
    val senderUid: String = "",
    val senderName: String = "",
    val senderAvatar: String = "😎",
    val messageText: String = "",
    val timestamp: String = "",
    val timestampMillis: Long = System.currentTimeMillis(),
    val messageType: String = "TEXT", // TEXT, REACTION, SYSTEM
    val isUser: Boolean = false,
    val isLiked: Boolean = false
)

data class PrivateCallSession(
    val callId: String = "",
    val callerUid: String = "",
    val callerName: String = "",
    val callerAvatar: String = "😎",
    val calleeUid: String = "",
    val calleeName: String = "",
    val status: String = "IDLE", // IDLE, CALLING, CONNECTED, ENDED, REJECTED
    val type: String = "VIDEO", // VIDEO, AUDIO
    val isCameraEnabled: Boolean = true,
    val isMicMuted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
