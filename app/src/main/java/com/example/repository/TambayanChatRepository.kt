package com.example.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.BlockedUser
import com.example.model.CampusSafetyPolicyItem
import com.example.model.ChatMessage
import com.example.model.HighSchoolStrand
import com.example.model.LoungePost
import com.example.model.LoungeReply
import com.example.model.PinoyCampusTrivia
import com.example.model.ReportReason
import com.example.model.ReportedMessage
import com.example.model.StudentProfile
import com.example.model.TambayanRoom
import com.example.model.FriendRequest
import com.example.model.PrivateCallSession
import com.example.model.PrivateChatMessage
import com.example.model.TambayanFriend
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TambayanChatRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    // Convenience constructor: resolves named database ID from string resources
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth
    )

    companion object {
        private const val TAG = "TambayanChatRepo"
    }

    val rooms = listOf(
        TambayanRoom(
            id = "lobby",
            title = "High School Life & Chismis",
            subtitle = "Pangkalahatang kwentuhan, balita at chismis sa campus.",
            emoji = "💬",
            onlineCount = 42
        ),
        TambayanRoom(
            id = "filipino_community",
            title = "Hugot, Kilig & Confessions",
            subtitle = "Campus crush, lihim na pagtingin at kwentong puso.",
            emoji = "🇵🇭",
            onlineCount = 38
        ),
        TambayanRoom(
            id = "study_learning",
            title = "Study Hall & Homework Help",
            subtitle = "Reviewers, Math, Science at takdang-aralin tulungan.",
            emoji = "📚",
            onlineCount = 55
        ),
        TambayanRoom(
            id = "entertainment",
            title = "OPM, BINI & Jamming Hub",
            subtitle = "Musika, Pinoy pop, trending clips at kantahan.",
            emoji = "🎵",
            onlineCount = 29
        ),
        TambayanRoom(
            id = "games_hobbies",
            title = "Pinoy Gaming & MLBB Hub",
            subtitle = "MLBB, Roblox, Genshin, esports at laro ng barkada.",
            emoji = "🎮",
            onlineCount = 45
        ),
        TambayanRoom(
            id = "campus_life",
            title = "School & Campus Life",
            subtitle = "Kwentong canteen, kaklase, guro at school events.",
            emoji = "🏫",
            onlineCount = 32
        ),
        TambayanRoom(
            id = "open_discussion",
            title = "Open Discussion & Ideas",
            subtitle = "Malayang tambayan, sariling opinyon at malikhaing ideya.",
            emoji = "💡",
            onlineCount = 20
        )
    )

    private val _systemAnnouncement = MutableStateFlow(
        "📢 CAMPUS SAFETY NOTICE: Ligtas at magalang na tambayan! Bawal ang pambu-bully, paninira, o pagbabahagi ng personal info. I-report ang pasaway."
    )
    val systemAnnouncement: StateFlow<String> = _systemAnnouncement.asStateFlow()

    // Blocked users set & reports list
    private val _blockedUsers = MutableStateFlow<List<BlockedUser>>(emptyList())
    val blockedUsers: StateFlow<List<BlockedUser>> = _blockedUsers.asStateFlow()

    private val _reportedMessages = MutableStateFlow<List<ReportedMessage>>(emptyList())
    val reportedMessages: StateFlow<List<ReportedMessage>> = _reportedMessages.asStateFlow()

    private val _profanityFilterEnabled = MutableStateFlow(true)
    val profanityFilterEnabled: StateFlow<Boolean> = _profanityFilterEnabled.asStateFlow()

    // Profanity list in Tagalog and English for cyberbullying protection
    private val offensiveKeywords = listOf(
        "gago", "tanga", "inutil", "bobo", "puta", "tarantado", "ulol", "bwisit", "leche",
        "pakyu", "kupal", "hayop ka", "pokpok", "tangina", "kingina", "punyeta", "hudas",
        "fuck", "bitch", "bastard", "idiot", "moron", "asshole", "retard"
    )

    private val initialMessages = mapOf(
        "lobby" to mutableListOf(
            ChatMessage("0", "lobby", "benjie1978", "😎", "👑 Moderator", "Mabuhay mga ka-tambay! Maligayang pagdating sa ating bagong pro chatroom system.", "12:30 PM", false, 48),
            ChatMessage("1", "lobby", "Maria Santos", "🎀", "🎓 G10", "Magandang hapon mga ka-Tambay! Kumusta ang video stream natin?", "12:31 PM", false, 14),
            ChatMessage("2", "lobby", "Juan Dela Cruz", "🧢", "🔬 STEM", "Malinaw at mabilis ang chat! Nakakatuwa itong bagong campus hub.", "12:32 PM", false, 19),
            ChatMessage("3", "lobby", "Ana Reyes", "🌸", "📚 Mentor", "Welcome sa lahat! Kung may kailangan kayong tulong, tanong lang kayo.", "12:33 PM", false, 25)
        ),
        "lounge" to mutableListOf(
            ChatMessage("101", "lounge", "Chloe_ABM", "💼", "💼 ABM", "Grabe yung mainit na banana cue sa canteen kanina, panalo!", "09:40 AM", false, 18),
            ChatMessage("102", "lounge", "Paolo_G9", "🏃", "📙 G9", "Tara chill muna dito sa lounge bago mag-start ang susunod na klase!", "09:45 AM", false, 24),
            ChatMessage("103", "lounge", "Sarah_SHS", "🌸", "🔬 STEM", "Bilis ng oras, senior high completer na kami this school year!", "09:50 AM", false, 15)
        ),
        "homework" to mutableListOf(
            ChatMessage("201", "homework", "Nathan_MathWhiz", "📐", "🔬 STEM", "Sa may kailangan ng reviewer sa Physics at Pre-Cal, share ko reviewers mamaya!", "08:30 AM", false, 55),
            ChatMessage("202", "homework", "Angel_G8", "📖", "📘 G8", "Sino may notes para sa Araling Panlipunan tungkol sa Kasaysayan ng Pilipinas?", "08:35 AM", false, 12),
            ChatMessage("203", "homework", "Nathan_MathWhiz", "📐", "🔬 STEM", "Meron ako Angel! Padala ko list ng mga bayani at mahalagang petsa!", "08:37 AM", false, 28)
        ),
        "barkada" to mutableListOf(
            ChatMessage("301", "barkada", "Secret_Admirer", "💌", "📜 HUMSS", "Crush ko yung nasa Section Diamond na laging may hawak na gitara...", "11:00 AM", false, 42),
            ChatMessage("302", "barkada", "Miggy_Kilig", "🌹", "🎓 G10", "Yung nginitian ka niya habang naglalakad sa corridor, grabe kilig!", "11:05 AM", false, 35),
            ChatMessage("303", "barkada", "Ella_Quotes", "💭", "🎒 G7", "Ang pag-aaral ay parang barkada, kapag sama-sama, kayang-kaya!", "11:10 AM", false, 29)
        ),
        "scitech" to mutableListOf(
            ChatMessage("401", "scitech", "Alex_Coder", "💻", "🔬 STEM", "Sino nag-aaral ng Kotlin at Android development dito? Ang galing gumawa ng app!", "10:15 AM", false, 31),
            ChatMessage("402", "scitech", "Kim_Robotics", "🤖", "🛠️ TVL", "Katatapos lang ng robot simulation namin kanina sa lab!", "10:20 AM", false, 22)
        ),
        "math" to mutableListOf(
            ChatMessage("501", "math", "Carlo_Calculus", "📊", "🔬 STEM", "Tip para sa Quadratic Formula: Tandaan lang ang b^2 - 4ac para sa discriminant!", "11:20 AM", false, 38),
            ChatMessage("502", "math", "Grace_G9", "📐", "📙 G9", "Salamat Kuya Carlo, malaking tulong sa quiz mamaya!", "11:25 AM", false, 16)
        ),
        "arts" to mutableListOf(
            ChatMessage("601", "arts", "Blooms_Pinoy", "🌸", "📜 HUMSS", "Walang kupas ang Salamin, Salamin at Pantropiko ng BINI! LSS buong araw!", "10:30 AM", false, 67),
            ChatMessage("602", "arts", "Acoustic_Dave", "🎸", "🔬 STEM", "Jamming tayo mamaya sa grandstand, tugtugan tayo ng gitara!", "10:35 AM", false, 33)
        ),
        // Fallback backward compatibility keys
        "hs_life" to mutableListOf(),
        "hugot" to mutableListOf(),
        "study" to mutableListOf(),
        "gaming" to mutableListOf(),
        "opm" to mutableListOf()
    )

    private val _messagesFlow = MutableStateFlow<Map<String, List<ChatMessage>>>(initialMessages)
    val messagesFlow: StateFlow<Map<String, List<ChatMessage>>> = _messagesFlow.asStateFlow()

    // Tambayan Lounge Community Feed
    private val initialLoungePosts = listOf(
        LoungePost(
            id = "post_1",
            authorName = "Nathan_MathWhiz",
            authorAvatar = "📐",
            authorBadge = "🔬 STEM",
            categoryTag = "Study & Tips",
            title = "Tips para sa darating na Midterm Exams sa SHS STEM & Pre-Cal",
            content = "Mga ka-tambay, gumawa ako ng summary notes para sa conic sections at trigonometric identities. Tandaan: practice mag-solve ng 3 problems araw-araw bago matulog para ma-master ang patterns!",
            timestamp = "10:15 AM",
            likesCount = 34,
            isLiked = false,
            replies = listOf(
                LoungeReply(
                    id = "rep_1_1",
                    postId = "post_1",
                    authorName = "Sarah_SHS",
                    authorAvatar = "🌸",
                    authorBadge = "🔬 STEM",
                    content = "Super helpful nito Kuya Nathan! Pwede bang paki-share din ng formula sheet sa Gen Bio?",
                    timestamp = "10:20 AM",
                    likesCount = 8
                ),
                LoungeReply(
                    id = "rep_1_2",
                    postId = "post_1",
                    authorName = "Angel_G8",
                    authorAvatar = "📖",
                    authorBadge = "📘 G8",
                    content = "Kahit Grade 8 pa lang ako nakaka-inspire mag-aral pag nakikita ko sipag ninyo!",
                    timestamp = "10:25 AM",
                    likesCount = 5
                )
            )
        ),
        LoungePost(
            id = "post_2",
            authorName = "Chloe_ABM",
            authorAvatar = "💼",
            authorBadge = "💼 ABM",
            categoryTag = "Campus Life",
            title = "Top 3 pinakamasarap na meryenda sa gilid ng school gate tuwing recess",
            content = "1. Mainit na banana cue na makunat ang caramel\n2. Kwek-kwek na may matamis at maanghang na sawsawan\n3. Malamig na melon samalamig! Kayo ano favorite niyo?",
            timestamp = "11:30 AM",
            likesCount = 47,
            isLiked = false,
            replies = listOf(
                LoungeReply(
                    id = "rep_2_1",
                    postId = "post_2",
                    authorName = "Paolo_G9",
                    authorAvatar = "🏃",
                    authorBadge = "📙 G9",
                    content = "Dagdag mo yung fishball at kikiam ni Manong sa tapat ng gate 2!",
                    timestamp = "11:35 AM",
                    likesCount = 12
                )
            )
        ),
        LoungePost(
            id = "post_3",
            authorName = "Alex_Coder",
            authorAvatar = "💻",
            authorBadge = "🔬 STEM",
            categoryTag = "Games & Hobbies",
            title = "Sino gusto sumali sa friendly Mobile Legends / Wild Rift scrim ngayong Sabado?",
            content = "Naghahanap kami ng 2 players (Mage at Roam) para sa friendly match ng High School Tambayan squad. Bawal toxic, for fun at campus camaraderie lang!",
            timestamp = "1:00 PM",
            likesCount = 29,
            isLiked = false,
            replies = listOf(
                LoungeReply(
                    id = "rep_3_1",
                    postId = "post_3",
                    authorName = "Miggy_Kilig",
                    authorAvatar = "🌹",
                    authorBadge = "🎓 G10",
                    content = "Pasali Roam Tigreal/Angela main ako! Add kita mamaya.",
                    timestamp = "1:15 PM",
                    likesCount = 6
                )
            )
        ),
        LoungePost(
            id = "post_4",
            authorName = "Blooms_Pinoy",
            authorAvatar = "🌸",
            authorBadge = "📜 HUMSS",
            categoryTag = "Arts & Music",
            title = "Graduation at Moving-Up Song Suggestions 2026",
            content = "Bukod sa 'Salamat' ng The Dawn at 'Mapagpalang Araw', anong mga bagong OPM songs ang maganda para sa moving up ceremony ngayong taon?",
            timestamp = "2:45 PM",
            likesCount = 52,
            isLiked = false,
            replies = emptyList()
        )
    )

    private val _loungePosts = MutableStateFlow<List<LoungePost>>(initialLoungePosts)
    val loungePosts: StateFlow<List<LoungePost>> = _loungePosts.asStateFlow()

    // Firestore listener tracking
    private var activeRoomListener: ListenerRegistration? = null
    private var listeningRoomId: String? = null

    // Real Firestore Friends & Private 1-on-1 Chat Flows
    private val _friendsFlow = MutableStateFlow<List<TambayanFriend>>(emptyList())
    val friendsFlow: StateFlow<List<TambayanFriend>> = _friendsFlow.asStateFlow()

    private val _incomingFriendRequestsFlow = MutableStateFlow<List<FriendRequest>>(emptyList())
    val incomingFriendRequestsFlow: StateFlow<List<FriendRequest>> = _incomingFriendRequestsFlow.asStateFlow()

    private val _privateMessagesFlow = MutableStateFlow<Map<String, List<PrivateChatMessage>>>(emptyMap())
    val privateMessagesFlow: StateFlow<Map<String, List<PrivateChatMessage>>> = _privateMessagesFlow.asStateFlow()

    private val _activeCallSessionFlow = MutableStateFlow<PrivateCallSession?>(null)
    val activeCallSessionFlow: StateFlow<PrivateCallSession?> = _activeCallSessionFlow.asStateFlow()

    private var friendsListenerRegistration: ListenerRegistration? = null
    private var friendRequestsListenerRegistration: ListenerRegistration? = null
    private var privateChatListenerRegistration: ListenerRegistration? = null
    private var callSignalingListenerRegistration: ListenerRegistration? = null
    private var activeConversationId: String? = null

    // Campus Safety & Legal Policies (DepEd, Anti-Bullying, Data Privacy, UGC)
    val safetyPolicies = listOf(
        CampusSafetyPolicyItem(
            id = "anti_bullying",
            title = "Zero Tolerance sa Cyberbullying",
            legalBasis = "Republic Act No. 10627 (Anti-Bullying Act of 2013) & DepEd Order No. 40, s. 2012",
            description = "Mahigpit na ipinagbabawal ang anumang uri ng paninira, panlilibak, pananakot, o pambu-bully sa mga kapwa estudyante. Ang bawat mag-aaral ay may karapatan sa isang ligtas at positibong komunidad.",
            iconEmoji = "🛡️"
        ),
        CampusSafetyPolicyItem(
            id = "data_privacy",
            title = "Proteksyon sa Impormasyon ng Mag-aaral",
            legalBasis = "Republic Act No. 10173 (Data Privacy Act of 2012)",
            description = "Hindi nangongolekta ang app ng totoong pangalan, tirahan, o contact number. Ipinagbabawal ang pagbabahagi ng personal na impormasyon (cellphone, tirahan, ID number) sa pampublikong chat para sa kaligtasan ng kabataan.",
            iconEmoji = "🔒"
        ),
        CampusSafetyPolicyItem(
            id = "ugc_moderation",
            title = "User Generated Content (UGC) Policy",
            legalBasis = "Google Play Developer Policy & Community Standards",
            description = "Lahat ng mensahe ay dumadaan sa automated profanity filtering. May kakayahan ang bawat mag-aaral na mag-report ng pasaway na mensahe at mag-block ng hindi kaaya-ayang profile.",
            iconEmoji = "⚖️"
        ),
        CampusSafetyPolicyItem(
            id = "child_protection",
            title = "DepEd Child Protection Policy Aligned",
            legalBasis = "DepEd Child Protection Policy & Safe Spaces Act (RA 11313)",
            description = "Layunin ng Philippines High Tambayan na panatilihing magiliw, nakakatulong sa pag-aaral, at malinis ang kapaligiran. Bawal ang sekswal na nilalaman, banta ng karahasan, at iligal na gawain.",
            iconEmoji = "🤝"
        ),
        CampusSafetyPolicyItem(
            id = "master_admin_benjie_landingin",
            title = "Master Admin & System Security Policy",
            legalBasis = "Project Governance & Security Protocol (Lead Admin: Benjie Landingin)",
            description = "Eksklusibong pagmamay-ari at pamamahala ni Master Admin Benjie Landingin (Admin Security Code: 100877). Siya lamang ang may solong awtorisasyon na magbago ng features, magdagdag ng kagamitan, at magpatupad ng administrative controls. Ang lahat ng ibang access ay mahigpit na ACCESS DENIED.",
            iconEmoji = "👑"
        )
    )

    // Fun high school campus trivia (safe, inspiring, educational)
    val campusTriviaList = listOf(
        PinoyCampusTrivia(
            id = "t1",
            question = "Ano ang pambansang ibon ng Pilipinas na sumasagisag sa lakas at kalayaan ng kabataan?",
            options = listOf("Agila ng Pilipinas (Philippine Eagle)", "Maya", "Kalapati", "Lawin"),
            correctIndex = 0,
            explanation = "Ang Philippine Eagle (Pithecophaga jefferyi) ang opisyal na pambansang ibon alinsunod sa Proclamation No. 615.",
            category = "Pambansang Sagisag"
        ),
        PinoyCampusTrivia(
            id = "t2",
            question = "Sino ang pambansang bayani ng Pilipinas na sumulat ng Noli Me Tangere at El Filibusterismo?",
            options = listOf("Andres Bonifacio", "Dr. Jose Rizal", "Apolinario Mabini", "Emilio Aguinaldo"),
            correctIndex = 1,
            explanation = "Si Dr. Jose Rizal ang dakilang bayani at manunulat na nagbigay inspirasyon sa rebolusyong Pilipino.",
            category = "Kasaysayan"
        ),
        PinoyCampusTrivia(
            id = "t3",
            question = "Ilang taon ang itinatagal ng Senior High School (SHS) sa ilalim ng K-12 Program sa Pilipinas?",
            options = listOf("1 Taon", "2 Taon (Grade 11 & 12)", "3 Taon", "4 na Taon"),
            correctIndex = 1,
            explanation = "Binubuo ang Senior High School ng dalawang taon: Grade 11 at Grade 12 na may iba't ibang strands tulad ng STEM, ABM, at HUMSS.",
            category = "High School Life"
        ),
        PinoyCampusTrivia(
            id = "t4",
            question = "Anong popular na meryenda sa canteen ang gawa sa pritong saba na may asukal na caramelized?",
            options = listOf("Banana Cue", "Turon", "Maruya", "Kwek-Kwek"),
            correctIndex = 0,
            explanation = "Ang Banana Cue ay paboritong pampalipas-gutom ng mga estudyante tuwing recess at uwian!",
            category = "Campus Culture"
        ),
        PinoyCampusTrivia(
            id = "t5",
            question = "Aling batas sa Pilipinas ang nagtatakda ng Anti-Bullying Policy sa lahat ng mga paaralan?",
            options = listOf("Republic Act 10627", "Republic Act 9003", "Republic Act 8424", "Republic Act 1425"),
            correctIndex = 0,
            explanation = "Ang RA 10627 o Anti-Bullying Act of 2013 ang nag-aatas sa mga paaralan na protektahan ang mga mag-aaral laban sa pambu-bully.",
            category = "Batas at Karapatan"
        )
    )

    fun attachRoomListener(roomId: String, currentUserId: String) {
        if (listeningRoomId == roomId && activeRoomListener != null) return
        activeRoomListener?.remove()
        listeningRoomId = roomId

        try {
            val roomCol = db.collection("rooms").document(roomId).collection("messages")
            activeRoomListener = roomCol
                .orderBy("timestampMillis", Query.Direction.ASCENDING)
                .limit(100)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Snapshot listener error for room $roomId", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val firestoreMessages = snapshot.documents.mapNotNull { doc ->
                            val senderName = doc.getString("senderName") ?: "Tambayer"
                            val senderId = doc.getString("senderId") ?: senderName
                            val likesCount = doc.getLong("likesCount")?.toInt() ?: 0
                            val likedBy = (doc.get("likedBy") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                            val isUser = senderName.equals(currentUserId, ignoreCase = true) || senderId == currentUserId
                            val isLiked = likedBy.contains(currentUserId)
                            ChatMessage(
                                id = doc.id,
                                roomId = roomId,
                                senderName = senderName,
                                senderAvatar = doc.getString("senderAvatar") ?: "😎",
                                senderGradeBadge = doc.getString("senderGradeBadge") ?: "🎒 G7",
                                messageText = doc.getString("messageText") ?: "",
                                timestamp = doc.getString("timestamp") ?: "Ngayon",
                                isUser = isUser,
                                likesCount = likesCount,
                                isLiked = isLiked,
                                isReported = doc.getBoolean("isReported") ?: false
                            )
                        }

                        val initialForRoom = initialMessages[roomId] ?: emptyList()
                        val combined = (initialForRoom + firestoreMessages).distinctBy { it.id }

                        val map = _messagesFlow.value.toMutableMap()
                        map[roomId] = combined
                        _messagesFlow.value = map
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Error attaching Firestore listener", e)
        }
    }

    fun detachRoomListener() {
        activeRoomListener?.remove()
        activeRoomListener = null
        listeningRoomId = null
    }

    fun getFilteredMessagesForRoom(roomId: String): List<ChatMessage> {
        val blockedSet = _blockedUsers.value.map { it.alias }.toSet()
        val rawMessages = _messagesFlow.value[roomId] ?: emptyList()
        return rawMessages.filter { it.senderName !in blockedSet }
    }

    fun filterProfanity(text: String): String {
        if (!_profanityFilterEnabled.value) return text
        var filtered = text
        for (badWord in offensiveKeywords) {
            val regex = Regex("(?i)\\b${Regex.escape(badWord)}\\b")
            filtered = filtered.replace(regex, "❤️ [respeto]")
        }
        return filtered
    }

    fun hasProfanity(text: String): Boolean {
        for (badWord in offensiveKeywords) {
            if (text.contains(badWord, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    fun sendMessage(roomId: String, senderProfile: StudentProfile, text: String): Boolean {
        if (text.isBlank()) return false
        val cleanText = filterProfanity(text.trim())
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val timeStr = timeFormat.format(Date())
        val timestampMillis = System.currentTimeMillis()
        val msgId = "msg_${timestampMillis}_${(1000..9999).random()}"
        val currentUserId = auth.currentUser?.uid ?: senderProfile.alias

        val newMsg = ChatMessage(
            id = msgId,
            roomId = roomId,
            senderName = senderProfile.alias,
            senderAvatar = senderProfile.avatar,
            senderGradeBadge = senderProfile.strand.badge,
            messageText = cleanText,
            timestamp = timeStr,
            isUser = true,
            likesCount = 0,
            isLiked = false
        )

        // Optimistic local update
        val currentMap = _messagesFlow.value.toMutableMap()
        val currentList = (currentMap[roomId] ?: emptyList()).toMutableList()
        currentList.add(newMsg)
        currentMap[roomId] = currentList
        _messagesFlow.value = currentMap

        // Sync to cloud Firestore directly
        try {
            val payload = mapOf(
                "id" to msgId,
                "roomId" to roomId,
                "senderId" to currentUserId,
                "senderName" to senderProfile.alias,
                "senderAvatar" to senderProfile.avatar,
                "senderGradeBadge" to senderProfile.strand.badge,
                "messageText" to cleanText,
                "timestamp" to timeStr,
                "timestampMillis" to timestampMillis,
                "likesCount" to 0L,
                "likedBy" to emptyList<String>(),
                "isReported" to false
            )
            db.collection("rooms").document(roomId).collection("messages").document(msgId)
                .set(payload)
                .addOnFailureListener { e ->
                    Log.w(TAG, "Failed to write message to Firestore", e)
                }

            // Also dual-write to root 'messages' collection for HTML/JS web client sync
            val webPayload = mapOf(
                "username" to senderProfile.alias,
                "text" to cleanText,
                "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                "avatar" to senderProfile.avatar,
                "strand" to senderProfile.strand.badge,
                "roomId" to roomId
            )
            db.collection("messages").document(msgId).set(webPayload)
                .addOnFailureListener { e ->
                    Log.w(TAG, "Failed to write web message to Firestore", e)
                }
        } catch (e: Exception) {
            Log.w(TAG, "Error in Firestore sendMessage", e)
        }
        return true
    }

    fun toggleLike(roomId: String, messageId: String) {
        val currentMap = _messagesFlow.value.toMutableMap()
        val currentList = (currentMap[roomId] ?: emptyList()).toMutableList()
        val index = currentList.indexOfFirst { it.id == messageId }
        if (index != -1) {
            val msg = currentList[index]
            val newLiked = !msg.isLiked
            val newCount = if (newLiked) msg.likesCount + 1 else (msg.likesCount - 1).coerceAtLeast(0)
            currentList[index] = msg.copy(isLiked = newLiked, likesCount = newCount)
            currentMap[roomId] = currentList
            _messagesFlow.value = currentMap
        }

        try {
            val docRef = db.collection("rooms").document(roomId).collection("messages").document(messageId)
            db.runTransaction { tx ->
                val snapshot = tx.get(docRef)
                if (snapshot.exists()) {
                    val currentLikes = snapshot.getLong("likesCount") ?: 0L
                    tx.update(docRef, "likesCount", currentLikes + 1L)
                }
            }.addOnFailureListener { e ->
                Log.w(TAG, "Transaction toggleLike failed", e)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error in toggleLike", e)
        }
    }

    fun deleteMessage(roomId: String, messageId: String) {
        val currentMap = _messagesFlow.value.toMutableMap()
        val currentList = (currentMap[roomId] ?: emptyList()).toMutableList()
        currentList.removeAll { it.id == messageId }
        currentMap[roomId] = currentList
        _messagesFlow.value = currentMap

        try {
            db.collection("rooms").document(roomId).collection("messages").document(messageId)
                .delete()
                .addOnFailureListener { e ->
                    Log.w(TAG, "Failed to delete message from Firestore", e)
                }
        } catch (e: Exception) {
            Log.w(TAG, "Error in deleteMessage", e)
        }
    }

    fun reportMessage(
        roomId: String,
        messageId: String,
        reason: ReportReason
    ) {
        val messages = _messagesFlow.value[roomId] ?: emptyList()
        val targetMsg = messages.firstOrNull { it.id == messageId } ?: return

        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val reportedAt = timeFormat.format(Date())
        val reportId = "rep_${System.currentTimeMillis()}"

        val report = ReportedMessage(
            id = reportId,
            messageId = messageId,
            senderName = targetMsg.senderName,
            messageText = targetMsg.messageText,
            roomId = roomId,
            reason = reason,
            reportedAt = reportedAt,
            status = "PENDING_REVIEW"
        )
        val currentReports = _reportedMessages.value.toMutableList()
        currentReports.add(0, report)
        _reportedMessages.value = currentReports

        // Mark message as reported locally
        val currentMap = _messagesFlow.value.toMutableMap()
        val currentList = (currentMap[roomId] ?: emptyList()).toMutableList()
        val index = currentList.indexOfFirst { it.id == messageId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(isReported = true)
            currentMap[roomId] = currentList
            _messagesFlow.value = currentMap
        }

        try {
            val payload = mapOf(
                "id" to reportId,
                "messageId" to messageId,
                "roomId" to roomId,
                "senderName" to targetMsg.senderName,
                "messageText" to targetMsg.messageText,
                "reason" to reason.name,
                "reportedBy" to targetMsg.senderName,
                "reportedAt" to reportedAt,
                "status" to "PENDING_REVIEW"
            )
            db.collection("reports").document(reportId).set(payload)
            db.collection("rooms").document(roomId).collection("messages").document(messageId)
                .update("isReported", true)
        } catch (e: Exception) {
            Log.w(TAG, "Error in reportMessage", e)
        }
    }

    fun blockUser(alias: String, avatar: String) {
        val timeFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
        val current = _blockedUsers.value.toMutableList()
        if (current.none { it.alias == alias }) {
            current.add(BlockedUser(alias, avatar, timeFormat.format(Date())))
            _blockedUsers.value = current
        }
    }

    fun unblockUser(alias: String) {
        val current = _blockedUsers.value.toMutableList()
        current.removeAll { it.alias == alias }
        _blockedUsers.value = current
    }

    fun clearRoomMessages(roomId: String) {
        val currentMap = _messagesFlow.value.toMutableMap()
        currentMap[roomId] = emptyList()
        _messagesFlow.value = currentMap
    }

    fun toggleProfanityFilter(enabled: Boolean) {
        _profanityFilterEnabled.value = enabled
    }

    fun setSystemAnnouncement(newNotice: String) {
        _systemAnnouncement.value = newNotice
        try {
            db.collection("settings").document("announcement")
                .set(mapOf("notice" to newNotice, "updatedAt" to Date().toString()), SetOptions.merge())
        } catch (e: Exception) {
            Log.w(TAG, "Error setting announcement", e)
        }
    }

    fun dismissReport(reportId: String) {
        val list = _reportedMessages.value.toMutableList()
        val index = list.indexOfFirst { it.id == reportId }
        if (index != -1) {
            list[index] = list[index].copy(status = "DISMISSED")
            _reportedMessages.value = list
        }
    }

    fun takeActionOnReport(reportId: String, action: String) {
        val list = _reportedMessages.value.toMutableList()
        val index = list.indexOfFirst { it.id == reportId }
        if (index != -1) {
            val report = list[index]
            list[index] = report.copy(status = action)
            _reportedMessages.value = list
            if (action == "USER_BANNED") {
                blockUser(report.senderName, "🚫")
            }
            if (action == "MESSAGE_REMOVED" || action == "USER_BANNED") {
                deleteMessage(report.roomId, report.messageId)
            }
        }
    }

    fun syncUserProfile(profile: StudentProfile) {
        val uid = auth.currentUser?.uid ?: return
        try {
            val userDoc = db.collection("users").document(uid)
            val data = mapOf(
                "userId" to uid,
                "alias" to profile.alias.ifBlank { "PinoyHighStudent" },
                "avatar" to profile.avatar,
                "strand" to profile.strand.name,
                "statusBio" to "Kasama sa barkada ✨",
                "tambayStars" to profile.tambayStars.toLong(),
                "vipLevel" to profile.vipLevel,
                "isPolicyAccepted" to profile.isPolicyAccepted,
                "email" to (auth.currentUser?.email ?: profile.email),
                "role" to profile.role.name,
                "isOnline" to true,
                "lastSeen" to System.currentTimeMillis()
            )
            userDoc.set(data, SetOptions.merge())
        } catch (e: Exception) {
            Log.w(TAG, "Error syncing user profile", e)
        }
    }

    // ==========================================
    // BARKADA FRIENDS & REAL FIRESTORE BACKEND
    // ==========================================
    fun attachFriendsListener(currentUid: String) {
        if (currentUid.isBlank()) return
        friendsListenerRegistration?.remove()
        friendsListenerRegistration = db.collection("users").document(currentUid)
            .collection("friends")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Notice in friends listener: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        val friendUid = doc.id
                        val alias = doc.getString("alias") ?: "Ka-Tambay"
                        val avatar = doc.getString("avatar") ?: "😎"
                        val strandName = doc.getString("strand") ?: HighSchoolStrand.SHS_STEM.name
                        val strand = try { HighSchoolStrand.valueOf(strandName) } catch (_: Exception) { HighSchoolStrand.SHS_STEM }
                        val isOnline = doc.getBoolean("isOnline") ?: false
                        val statusBio = doc.getString("statusBio") ?: "Kasama sa barkada ✨"
                        val addedAt = doc.getLong("addedAt") ?: System.currentTimeMillis()
                        TambayanFriend(
                            id = friendUid,
                            alias = alias,
                            avatar = avatar,
                            strand = strand,
                            isOnline = isOnline,
                            statusBio = statusBio,
                            addedAt = addedAt
                        )
                    }
                    _friendsFlow.value = list
                }
            }
    }

    fun attachFriendRequestsListener(currentUid: String) {
        if (currentUid.isBlank()) return
        friendRequestsListenerRegistration?.remove()
        friendRequestsListenerRegistration = db.collection("friend_requests")
            .whereEqualTo("recipientUid", currentUid)
            .whereEqualTo("status", "PENDING")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Notice in friend requests listener: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val requests = snapshot.documents.mapNotNull { doc ->
                        FriendRequest(
                            id = doc.id,
                            senderUid = doc.getString("senderUid") ?: "",
                            senderAlias = doc.getString("senderAlias") ?: "",
                            senderAvatar = doc.getString("senderAvatar") ?: "😎",
                            senderStrand = doc.getString("senderStrand") ?: "SHS_STEM",
                            recipientUid = doc.getString("recipientUid") ?: "",
                            recipientAlias = doc.getString("recipientAlias") ?: "",
                            status = doc.getString("status") ?: "PENDING",
                            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                        )
                    }
                    _incomingFriendRequestsFlow.value = requests
                }
            }
    }

    fun sendFriendRequest(
        targetUid: String,
        targetAlias: String,
        senderProfile: StudentProfile,
        onResult: (Boolean, String?) -> Unit
    ) {
        val currentUid = auth.currentUser?.uid
        if (currentUid == null) {
            onResult(false, "Kailangan munang mag-sign in upang makapag-add ng kaibigan.")
            return
        }
        if (currentUid == targetUid) {
            onResult(false, "Hindi maaaring i-add ang sarili.")
            return
        }
        val requestId = "req_${currentUid}_${targetUid}"
        val requestData = mapOf(
            "id" to requestId,
            "senderUid" to currentUid,
            "senderAlias" to senderProfile.alias,
            "senderAvatar" to senderProfile.avatar,
            "senderStrand" to senderProfile.strand.name,
            "recipientUid" to targetUid,
            "recipientAlias" to targetAlias,
            "status" to "PENDING",
            "createdAt" to System.currentTimeMillis()
        )
        db.collection("friend_requests").document(requestId)
            .set(requestData)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.localizedMessage) }
    }

    fun acceptFriendRequest(
        request: FriendRequest,
        currentProfile: StudentProfile,
        onResult: (Boolean, String?) -> Unit
    ) {
        val currentUid = auth.currentUser?.uid ?: return onResult(false, "Not authenticated")
        if (request.recipientUid != currentUid) return onResult(false, "Not authorized")

        db.collection("friend_requests").document(request.id)
            .update("status", "ACCEPTED")
            .addOnSuccessListener {
                val friendDataForMe = mapOf(
                    "id" to request.senderUid,
                    "alias" to request.senderAlias,
                    "avatar" to request.senderAvatar,
                    "strand" to request.senderStrand,
                    "statusBio" to "Kasama sa barkada ✨",
                    "addedAt" to System.currentTimeMillis(),
                    "isOnline" to true
                )
                val friendDataForSender = mapOf(
                    "id" to currentUid,
                    "alias" to currentProfile.alias,
                    "avatar" to currentProfile.avatar,
                    "strand" to currentProfile.strand.name,
                    "statusBio" to "Kasama sa barkada ✨",
                    "addedAt" to System.currentTimeMillis(),
                    "isOnline" to true
                )

                val batch = db.batch()
                val myFriendRef = db.collection("users").document(currentUid).collection("friends").document(request.senderUid)
                val senderFriendRef = db.collection("users").document(request.senderUid).collection("friends").document(currentUid)
                batch.set(myFriendRef, friendDataForMe)
                batch.set(senderFriendRef, friendDataForSender)

                batch.commit()
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { e -> onResult(false, e.localizedMessage) }
            }
            .addOnFailureListener { e -> onResult(false, e.localizedMessage) }
    }

    fun rejectFriendRequest(requestId: String, onResult: (Boolean, String?) -> Unit) {
        db.collection("friend_requests").document(requestId)
            .update("status", "REJECTED")
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.localizedMessage) }
    }

    fun removeFriend(friendUid: String, onResult: (Boolean, String?) -> Unit) {
        val currentUid = auth.currentUser?.uid ?: return onResult(false, "Not authenticated")
        db.collection("users").document(currentUid).collection("friends").document(friendUid)
            .delete()
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.localizedMessage) }
    }

    fun searchRegisteredStudents(query: String, onResult: (List<TambayanFriend>) -> Unit) {
        val currentUid = auth.currentUser?.uid ?: ""
        db.collection("users")
            .limit(20)
            .get()
            .addOnSuccessListener { snapshot ->
                val results = snapshot.documents.mapNotNull { doc ->
                    val uid = doc.id
                    if (uid == currentUid) return@mapNotNull null
                    val alias = doc.getString("alias") ?: return@mapNotNull null
                    if (query.isNotBlank() && !alias.contains(query, ignoreCase = true)) return@mapNotNull null
                    val avatar = doc.getString("avatar") ?: "😎"
                    val strandName = doc.getString("strand") ?: HighSchoolStrand.SHS_STEM.name
                    val strand = try { HighSchoolStrand.valueOf(strandName) } catch (_: Exception) { HighSchoolStrand.SHS_STEM }
                    val isOnline = doc.getBoolean("isOnline") ?: false
                    val statusBio = doc.getString("statusBio") ?: "Estudyante sa Tambayan"
                    TambayanFriend(
                        id = uid,
                        alias = alias,
                        avatar = avatar,
                        strand = strand,
                        isOnline = isOnline,
                        statusBio = statusBio
                    )
                }
                onResult(results)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    // ==========================================
    // PARTICIPANT-RESTRICTED PRIVATE MESSAGING
    // ==========================================
    fun getConversationId(uid1: String, uid2: String): String {
        return if (uid1 < uid2) "${uid1}_${uid2}" else "${uid2}_${uid1}"
    }

    fun attachPrivateChatListener(conversationId: String) {
        if (conversationId.isBlank()) return
        if (activeConversationId == conversationId && privateChatListenerRegistration != null) return
        privateChatListenerRegistration?.remove()
        activeConversationId = conversationId

        privateChatListenerRegistration = db.collection("private_chats")
            .document(conversationId)
            .collection("messages")
            .orderBy("timestampMillis", Query.Direction.ASCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Notice in private chat listener: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val currentUid = auth.currentUser?.uid ?: ""
                    val messages = snapshot.documents.mapNotNull { doc ->
                        val senderUid = doc.getString("senderUid") ?: ""
                        PrivateChatMessage(
                            id = doc.id,
                            friendId = doc.getString("friendId") ?: "",
                            conversationId = conversationId,
                            senderUid = senderUid,
                            senderName = doc.getString("senderName") ?: "Ka-Tambay",
                            senderAvatar = doc.getString("senderAvatar") ?: "😎",
                            messageText = doc.getString("messageText") ?: "",
                            timestamp = doc.getString("timestamp") ?: "",
                            timestampMillis = doc.getLong("timestampMillis") ?: System.currentTimeMillis(),
                            messageType = doc.getString("messageType") ?: "TEXT",
                            isUser = senderUid == currentUid,
                            isLiked = doc.getBoolean("isLiked") ?: false
                        )
                    }
                    val currentMap = _privateMessagesFlow.value.toMutableMap()
                    currentMap[conversationId] = messages
                    _privateMessagesFlow.value = currentMap
                }
            }
    }

    fun detachPrivateChatListener() {
        privateChatListenerRegistration?.remove()
        privateChatListenerRegistration = null
        activeConversationId = null
    }

    fun sendPrivateMessage(
        conversationId: String,
        recipientUid: String,
        senderProfile: StudentProfile,
        text: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val currentUid = auth.currentUser?.uid ?: return onResult(false, "Kailangan munang mag-sign in.")
        if (text.isBlank()) return onResult(false, "Walang nilalamang mensahe.")

        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val timeStr = timeFormat.format(Date())
        val timestampMillis = System.currentTimeMillis()
        val msgId = "pmsg_${timestampMillis}_${(1000..9999).random()}"

        val convRef = db.collection("private_chats").document(conversationId)
        val participants = listOf(currentUid, recipientUid).sorted()
        val convData = mapOf(
            "conversationId" to conversationId,
            "participantUids" to participants,
            "createdAt" to timestampMillis,
            "updatedAt" to timestampMillis,
            "lastMessage" to text.trim(),
            "lastSenderUid" to currentUid
        )
        convRef.set(convData, SetOptions.merge())

        val msgData = mapOf(
            "id" to msgId,
            "conversationId" to conversationId,
            "friendId" to recipientUid,
            "senderUid" to currentUid,
            "senderName" to senderProfile.alias,
            "senderAvatar" to senderProfile.avatar,
            "messageText" to text.trim(),
            "timestamp" to timeStr,
            "timestampMillis" to timestampMillis,
            "messageType" to "TEXT"
        )

        convRef.collection("messages").document(msgId)
            .set(msgData)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.localizedMessage) }
    }

    // ==========================================
    // PRIVATE 1-ON-1 AUDIO/VIDEO CALL SIGNALING
    // ==========================================
    fun attachCallSignalingListener(callId: String, onUpdate: (PrivateCallSession?) -> Unit) {
        callSignalingListenerRegistration?.remove()
        callSignalingListenerRegistration = db.collection("calls").document(callId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Notice in call signaling: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val session = PrivateCallSession(
                        callId = callId,
                        callerUid = snapshot.getString("callerUid") ?: "",
                        callerName = snapshot.getString("callerName") ?: "",
                        callerAvatar = snapshot.getString("callerAvatar") ?: "😎",
                        calleeUid = snapshot.getString("calleeUid") ?: "",
                        calleeName = snapshot.getString("calleeName") ?: "",
                        status = snapshot.getString("status") ?: "IDLE",
                        type = snapshot.getString("type") ?: "VIDEO",
                        isCameraEnabled = snapshot.getBoolean("isCameraEnabled") ?: true,
                        isMicMuted = snapshot.getBoolean("isMicMuted") ?: false,
                        createdAt = snapshot.getLong("createdAt") ?: System.currentTimeMillis()
                    )
                    _activeCallSessionFlow.value = session
                    onUpdate(session)
                } else {
                    _activeCallSessionFlow.value = null
                    onUpdate(null)
                }
            }
    }

    fun initiateCall(
        callId: String,
        calleeUid: String,
        calleeName: String,
        senderProfile: StudentProfile,
        type: String = "VIDEO",
        onResult: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        val currentUid = auth.currentUser?.uid ?: return onResult(false, "Not authenticated")
        val callData = mapOf(
            "callId" to callId,
            "callerUid" to currentUid,
            "callerName" to senderProfile.alias,
            "callerAvatar" to senderProfile.avatar,
            "calleeUid" to calleeUid,
            "calleeName" to calleeName,
            "status" to "CALLING",
            "type" to type,
            "isCameraEnabled" to true,
            "isMicMuted" to false,
            "createdAt" to System.currentTimeMillis()
        )
        db.collection("calls").document(callId)
            .set(callData)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.localizedMessage) }
    }

    fun updateCallStatus(callId: String, newStatus: String) {
        try {
            db.collection("calls").document(callId).update("status", newStatus)
        } catch (_: Exception) {}
    }

    fun updateCallMediaControls(callId: String, isCameraEnabled: Boolean, isMicMuted: Boolean) {
        try {
            db.collection("calls").document(callId).update(
                mapOf(
                    "isCameraEnabled" to isCameraEnabled,
                    "isMicMuted" to isMicMuted
                )
            )
        } catch (_: Exception) {}
    }

    // ==========================================
    // TAMBAYAN LOUNGE FEED FUNCTIONS
    // ==========================================
    fun createLoungePost(
        author: StudentProfile,
        categoryTag: String,
        title: String,
        content: String
    ): Boolean {
        if (title.isBlank() || content.isBlank()) return false
        val cleanTitle = filterProfanity(title.trim())
        val cleanContent = filterProfanity(content.trim())
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val timeStr = timeFormat.format(Date())
        val postId = "post_${System.currentTimeMillis()}"

        val newPost = LoungePost(
            id = postId,
            authorName = author.alias.ifBlank { "PinoyHighStudent" },
            authorAvatar = author.avatar,
            authorBadge = author.strand.badge,
            categoryTag = categoryTag.ifBlank { "General" },
            title = cleanTitle,
            content = cleanContent,
            timestamp = timeStr,
            likesCount = 0,
            isLiked = false,
            replies = emptyList()
        )

        val currentList = _loungePosts.value.toMutableList()
        currentList.add(0, newPost)
        _loungePosts.value = currentList
        return true
    }

    fun addLoungeReply(
        postId: String,
        author: StudentProfile,
        replyContent: String
    ): Boolean {
        if (replyContent.isBlank()) return false
        val cleanReply = filterProfanity(replyContent.trim())
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val timeStr = timeFormat.format(Date())
        val replyId = "reply_${System.currentTimeMillis()}"

        val newReply = LoungeReply(
            id = replyId,
            postId = postId,
            authorName = author.alias.ifBlank { "PinoyHighStudent" },
            authorAvatar = author.avatar,
            authorBadge = author.strand.badge,
            content = cleanReply,
            timestamp = timeStr,
            likesCount = 0,
            isLiked = false
        )

        val currentList = _loungePosts.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == postId }
        if (index != -1) {
            val post = currentList[index]
            val updatedReplies = post.replies + newReply
            currentList[index] = post.copy(replies = updatedReplies)
            _loungePosts.value = currentList
            return true
        }
        return false
    }

    fun toggleLoungePostLike(postId: String) {
        val currentList = _loungePosts.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == postId }
        if (index != -1) {
            val post = currentList[index]
            val newLiked = !post.isLiked
            val newCount = if (newLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
            currentList[index] = post.copy(isLiked = newLiked, likesCount = newCount)
            _loungePosts.value = currentList
        }
    }

    fun toggleLoungeReplyLike(postId: String, replyId: String) {
        val currentList = _loungePosts.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == postId }
        if (index != -1) {
            val post = currentList[index]
            val repIndex = post.replies.indexOfFirst { it.id == replyId }
            if (repIndex != -1) {
                val updatedReplies = post.replies.toMutableList()
                val rep = updatedReplies[repIndex]
                val newLiked = !rep.isLiked
                val newCount = if (newLiked) rep.likesCount + 1 else (rep.likesCount - 1).coerceAtLeast(0)
                updatedReplies[repIndex] = rep.copy(isLiked = newLiked, likesCount = newCount)
                currentList[index] = post.copy(replies = updatedReplies)
                _loungePosts.value = currentList
            }
        }
    }

    fun reportLoungePost(postId: String, reason: ReportReason) {
        val post = _loungePosts.value.firstOrNull { it.id == postId } ?: return
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val report = ReportedMessage(
            id = "rep_lounge_${System.currentTimeMillis()}",
            messageId = postId,
            senderName = post.authorName,
            messageText = "[LOUNGE POST] ${post.title}: ${post.content}",
            roomId = "lounge_feed",
            reason = reason,
            reportedAt = timeFormat.format(Date()),
            status = "PENDING_REVIEW"
        )
        val currentReports = _reportedMessages.value.toMutableList()
        currentReports.add(0, report)
        _reportedMessages.value = currentReports

        val currentList = _loungePosts.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == postId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(isReported = true)
            _loungePosts.value = currentList
        }
    }

    fun deleteLoungePost(postId: String) {
        val currentList = _loungePosts.value.toMutableList()
        currentList.removeAll { it.id == postId }
        _loungePosts.value = currentList
    }

    // ============================================================
    // BACKEND AUTO-UPDATE & APK DISTRIBUTION REAL-TIME ENGINE
    // ============================================================
    private val _appUpdateInfo = MutableStateFlow(
        AppUpdateInfo(
            hasUpdate = false,
            latestVersionCode = 2,
            latestVersionName = "1.2 (Music & Real-time Update)",
            apkDownloadUrl = "https://ais-pre-ibpnq7ytehnxsfqxtnjrzt-41500935643.asia-southeast1.run.app/philippines-high-tambayan.apk",
            releaseTitle = "Bagong Features & Tambayan FM Music Update! 🎵🚀",
            newFeaturesList = listOf(
                "🎵 Tambayan Campus FM Background Music (5 friendly songs) na may Play/Pause at Next!",
                "💬 Mas pinalakas na CHAT HERE bar sa lahat ng chat rooms!",
                "🔄 Awtomatikong Real-time Sync sa bagong Firebase Firestore backend!",
                "📦 Direct 1-Click APK Download & Share para sa mga kaklase!"
            ),
            releaseNotes = "May bagong bersyon na ang Philippines High Tambayan! Ma-enjoy ang magandang background music habang nakikipag-chat at mas mabilis na server updates."
        )
    )
    val appUpdateInfo: StateFlow<AppUpdateInfo> = _appUpdateInfo.asStateFlow()

    init {
        initAppConfigListener()
    }

    fun initAppConfigListener() {
        try {
            db.collection("settings").document("app_config")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                    val code = snapshot.getLong("latestVersionCode")?.toInt() ?: 2
                    val name = snapshot.getString("latestVersionName") ?: "1.4"
                    val url = snapshot.getString("apkDownloadUrl") ?: "https://github.com/gptgamestudio/philippines-high-tambayan-chat/releases/download/v1.0.4/philippines-high-tambayan-chat-v1.0.4.apk"
                    val title = snapshot.getString("releaseTitle") ?: "Bagong Features sa Tambayan! 🚀"
                    val notes = snapshot.getString("releaseNotes") ?: "May bagong update sa tambayan na may friendly background music!"
                    val isMandatory = snapshot.getBoolean("isMandatory") ?: false
                    val features = (snapshot.get("newFeaturesList") as? List<*>)?.mapNotNull { it?.toString() }
                        ?: listOf(
                            "🎵 Tambayan Campus FM Background Music",
                            "💬 Mas pinalakas na CHAT HERE sa lahat ng rooms",
                            "📦 Direct APK download & update"
                        )

                    val currentInstalledVersion = 1
                    _appUpdateInfo.value = AppUpdateInfo(
                        hasUpdate = code > currentInstalledVersion,
                        latestVersionCode = code,
                        latestVersionName = name,
                        apkDownloadUrl = url,
                        releaseTitle = title,
                        newFeaturesList = features,
                        releaseNotes = notes,
                        isMandatory = isMandatory
                    )
                }
        } catch (e: Exception) {
            Log.w(TAG, "Notice on app config listener: ${e.message}")
        }
    }

    fun publishAppUpdate(
        versionCode: Int,
        versionName: String,
        apkUrl: String,
        title: String,
        notes: String,
        features: List<String>
    ) {
        try {
            val payload = mapOf(
                "latestVersionCode" to versionCode,
                "latestVersionName" to versionName,
                "apkDownloadUrl" to apkUrl,
                "releaseTitle" to title,
                "releaseNotes" to notes,
                "newFeaturesList" to features,
                "updatedAt" to Date().toString()
            )
            db.collection("settings").document("app_config").set(payload, SetOptions.merge())
            _appUpdateInfo.value = AppUpdateInfo(
                hasUpdate = versionCode > 1,
                latestVersionCode = versionCode,
                latestVersionName = versionName,
                apkDownloadUrl = apkUrl,
                releaseTitle = title,
                newFeaturesList = features,
                releaseNotes = notes,
                isMandatory = false
            )
        } catch (e: Exception) {
            Log.w(TAG, "Error publishing app update", e)
        }
    }
}

data class AppUpdateInfo(
    val hasUpdate: Boolean = false,
    val latestVersionCode: Int = 1,
    val latestVersionName: String = "1.0",
    val apkDownloadUrl: String = "https://github.com/gptgamestudio/philippines-high-tambayan-chat/releases/download/v1.0.4/philippines-high-tambayan-chat-v1.0.4.apk",
    val releaseTitle: String = "Bagong Update at Features para sa Tambayan!",
    val newFeaturesList: List<String> = emptyList(),
    val releaseNotes: String = "",
    val isMandatory: Boolean = false
)
