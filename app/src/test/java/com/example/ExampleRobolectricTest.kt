package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.HighSchoolStrand
import com.example.model.ReportReason
import com.example.model.StudentProfile
import com.example.repository.TambayanChatRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var tambayanRepo: TambayanChatRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        if (FirebaseApp.getApps(context).isEmpty()) {
            val options = FirebaseOptions.Builder()
                .setApplicationId("1:482355621819:android:cc8fa750eb5782bc22de9e")
                .setProjectId("gen-lang-client-0971067656")
                .setApiKey("AIzaSyBlOM74gcxt_1n8pYVA48GzXijTdYBMCXk")
                .build()
            FirebaseApp.initializeApp(context, options)
        }
        tambayanRepo = TambayanChatRepository(context)
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("PHILIPPINES HIGH TAMBAYAN CHAT", appName)
    }

    @Test
    fun `verify tambayan chat rooms and messaging`() {
        val rooms = tambayanRepo.rooms
        assertTrue("Must have tambayan rooms", rooms.isNotEmpty())
        assertEquals(7, rooms.size)

        val lobbyMessages = tambayanRepo.getFilteredMessagesForRoom("lobby")
        assertTrue("Lobby should have initial messages", lobbyMessages.isNotEmpty())

        val profile = StudentProfile("TestUser", "😎", HighSchoolStrand.SHS_STEM)
        val success = tambayanRepo.sendMessage("lobby", profile, "Hello Tambayan!")
        assertTrue("Send message should succeed", success)

        val updatedMessages = tambayanRepo.getFilteredMessagesForRoom("lobby")
        val lastMsg = updatedMessages.lastOrNull()
        assertNotNull(lastMsg)
        assertEquals("Hello Tambayan!", lastMsg?.messageText)
        assertEquals("TestUser", lastMsg?.senderName)
    }

    @Test
    fun `verify campus safety and anti-bullying policies are configured`() {
        val policies = tambayanRepo.safetyPolicies
        assertTrue("Policies list must not be empty", policies.isNotEmpty())

        val hasAntiBullying = policies.any { it.id == "anti_bullying" }
        assertTrue("Must have Anti-Bullying policy (RA 10627)", hasAntiBullying)

        val hasDataPrivacy = policies.any { it.id == "data_privacy" }
        assertTrue("Must have Data Privacy policy (RA 10173)", hasDataPrivacy)

        val hasUgc = policies.any { it.id == "ugc_moderation" }
        assertTrue("Must have UGC Moderation policy", hasUgc)
    }

    @Test
    fun `verify user blocking hides messages from blocked user`() {
        val badUserAlias = "PasawayStudent"
        val profile = StudentProfile(badUserAlias, "😈", HighSchoolStrand.GRADE_9)
        tambayanRepo.sendMessage("lobby", profile, "Sample message before block")

        val beforeBlock = tambayanRepo.getFilteredMessagesForRoom("lobby")
        assertTrue(beforeBlock.any { it.senderName == badUserAlias })

        // Block user
        tambayanRepo.blockUser(badUserAlias, "😈")
        assertTrue(tambayanRepo.blockedUsers.value.any { it.alias == badUserAlias })

        // Check messages filtered
        val afterBlock = tambayanRepo.getFilteredMessagesForRoom("lobby")
        assertFalse("Messages from blocked user should be hidden", afterBlock.any { it.senderName == badUserAlias })

        // Unblock user
        tambayanRepo.unblockUser(badUserAlias)
        val afterUnblock = tambayanRepo.getFilteredMessagesForRoom("lobby")
        assertTrue("Messages should reappear after unblocking", afterUnblock.any { it.senderName == badUserAlias })
    }

    @Test
    fun `verify message reporting mechanism flags message in moderation queue`() {
        val lobbyMessages = tambayanRepo.getFilteredMessagesForRoom("lobby")
        val target = lobbyMessages.first()

        tambayanRepo.reportMessage("lobby", target.id, ReportReason.BULLYING_HARASSMENT)

        val reports = tambayanRepo.reportedMessages.value
        assertTrue("Reported messages list should not be empty", reports.isNotEmpty())
        assertEquals(target.id, reports.first().messageId)
        assertEquals(ReportReason.BULLYING_HARASSMENT, reports.first().reason)
        assertEquals("PENDING_REVIEW", reports.first().status)
    }

    @Test
    fun `verify content profanity filter censors offensive language`() {
        val cleanSample = "Magandang hapon sa lahat!"
        assertFalse(tambayanRepo.hasProfanity(cleanSample))

        val profaneSample = "Bakit ang gago mo naman"
        assertTrue(tambayanRepo.hasProfanity(profaneSample))

        val filtered = tambayanRepo.filterProfanity(profaneSample)
        assertFalse("Filtered text should not contain original offensive word", filtered.contains("gago", ignoreCase = true))
        assertTrue("Filtered text should contain respect replacement", filtered.contains("[respeto]"))
    }

    @Test
    fun `verify campus trivia questions are educational and valid`() {
        val trivia = tambayanRepo.campusTriviaList
        assertTrue("Campus trivia should not be empty", trivia.isNotEmpty())
        trivia.forEach { item ->
            assertTrue("Question must not be blank", item.question.isNotBlank())
            assertTrue("Must have at least 2 options", item.options.size >= 2)
            assertTrue("CorrectIndex must be within options range", item.correctIndex in item.options.indices)
        }
    }

    @Test
    fun `verify student username registration and lock persistence`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.viewmodel.TambayanViewModel(app)

        vm.registerUsernameAndEnter("Bea_HUMSS12", "🌸", HighSchoolStrand.SHS_HUMSS)

        val state = vm.uiState.value
        assertTrue("User must be registered", state.hasRegisteredUsername)
        assertTrue("Username must be locked", state.isUsernameLocked)
        assertEquals("Bea_HUMSS12", state.studentProfile.alias)
        assertEquals("🌸", state.studentProfile.avatar)
        assertEquals(HighSchoolStrand.SHS_HUMSS, state.studentProfile.strand)
        assertEquals(com.example.viewmodel.ScreenState.MAIN_MENU, state.currentScreen)

        // Verify another ViewModel instance on same context loads the locked username
        val vm2 = com.example.viewmodel.TambayanViewModel(app)
        val state2 = vm2.uiState.value
        assertTrue("Persisted username must be loaded on new instance", state2.hasRegisteredUsername)
        assertEquals("Bea_HUMSS12", state2.studentProfile.alias)
    }

    @Test
    fun `verify Admin Benjie Landingin security passcode 100877 unlocks and denies others`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.viewmodel.TambayanViewModel(app)

        // Attempt unauthorized entry
        vm.verifyAdminPin("9999")
        val deniedState = vm.uiState.value
        assertFalse("Admin should be locked for unauthorized code", deniedState.isAdminUnlocked)
        assertNotNull("Error message should be present", deniedState.adminPinError)
        assertTrue(deniedState.adminPinError!!.contains("ACCESS DENIED"))

        // Attempt authorized entry with Benjie Landingin's birthday code 100877
        vm.verifyAdminPin("100877")
        val authorizedState = vm.uiState.value
        assertTrue("Admin must be unlocked for Admin Benjie Landingin", authorizedState.isAdminUnlocked)
        assertNull("Error message must be cleared", authorizedState.adminPinError)

        // Lock again
        vm.lockAdmin()
        assertFalse("Admin must be locked again", vm.uiState.value.isAdminUnlocked)
    }

    @Test
    fun `verify exact 7 tambayan chatrooms are present in directory`() {
        val rooms = tambayanRepo.rooms
        assertEquals("Must contain 7 dedicated rooms", 7, rooms.size)
        val roomIds = rooms.map { it.id }
        assertTrue("Must have lobby", roomIds.contains("lobby"))
        assertTrue("Must have filipino_community", roomIds.contains("filipino_community"))
        assertTrue("Must have study_learning", roomIds.contains("study_learning"))
        assertTrue("Must have entertainment", roomIds.contains("entertainment"))
        assertTrue("Must have games_hobbies", roomIds.contains("games_hobbies"))
        assertTrue("Must have campus_life", roomIds.contains("campus_life"))
        assertTrue("Must have open_discussion", roomIds.contains("open_discussion"))
    }

    @Test
    fun `verify lounge post creation, replies, and like reactions`() {
        val initialPosts = tambayanRepo.loungePosts.value
        assertTrue("Lounge must have initial community posts", initialPosts.isNotEmpty())

        val author = StudentProfile("LoungeTester", "😎", HighSchoolStrand.SHS_STEM)
        val ok = tambayanRepo.createLoungePost(author, "Study & Tips", "How to prepare for chemistry finals", "Study periodic trends and practice balancing redox reactions.")
        assertTrue("Lounge post creation should succeed", ok)

        val updated = tambayanRepo.loungePosts.value
        assertEquals("Post count should increase by 1", initialPosts.size + 1, updated.size)
        val newest = updated.first()
        assertEquals("How to prepare for chemistry finals", newest.title)

        // Add reply
        val replyOk = tambayanRepo.addLoungeReply(newest.id, author, "Salamat dito sa tip!")
        assertTrue("Reply should succeed", replyOk)

        val postAfterReply = tambayanRepo.loungePosts.value.first { it.id == newest.id }
        assertEquals("Reply count should be 1", 1, postAfterReply.replies.size)

        // Toggle like on post
        tambayanRepo.toggleLoungePostLike(newest.id)
        val likedPost = tambayanRepo.loungePosts.value.first { it.id == newest.id }
        assertTrue(likedPost.isLiked)
        assertEquals(1, likedPost.likesCount)
    }

    @Test
    fun `verify user roles least-privilege enforcement`() {
        val member = com.example.model.UserRole.MEMBER
        assertFalse("Ordinary member cannot moderate content", member.canModerateContent())
        assertFalse("Ordinary member cannot manage settings", member.canManageSettings())

        val moderator = com.example.model.UserRole.MODERATOR
        assertTrue("Moderator can moderate content", moderator.canModerateContent())
        assertFalse("Moderator cannot manage system settings", moderator.canManageSettings())

        val admin = com.example.model.UserRole.ADMIN
        assertTrue("Admin can moderate content", admin.canModerateContent())
        assertTrue("Admin can manage system settings", admin.canManageSettings())
    }
}

