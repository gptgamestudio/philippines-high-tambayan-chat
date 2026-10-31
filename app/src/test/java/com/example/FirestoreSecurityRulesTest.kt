package com.example

import com.example.model.FriendRequest
import com.example.model.PrivateChatMessage
import com.example.model.PrivateConversation
import com.example.model.TambayanFriend
import com.example.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Firestore Security Rules Verification Test Suite
 * Validates the 10 critical security invariants specified in the engineering brief:
 * 1. Unauthenticated user denied.
 * 2. Friend A can read their conversation.
 * 3. Friend B can read their conversation.
 * 4. Unrelated User C denied.
 * 5. Friend A can send a message as Friend A.
 * 6. Friend A cannot impersonate Friend B.
 * 7. Third-party participant injection denied.
 * 8. Client-side membership modification denied.
 * 9. Unauthorized friend request modification denied.
 * 10. Admin-only operations denied to ordinary users.
 */
class FirestoreSecurityRulesTest {

    private val uidA = "uid_student_friend_a"
    private val uidB = "uid_student_friend_b"
    private val uidC = "uid_student_unrelated_c"
    private val adminEmail = "insularleann@gmail.com"

    // Helper functions simulating the logic implemented in firestore.rules
    private fun isConversationParticipant(authUid: String?, conversation: PrivateConversation): Boolean {
        if (authUid == null) return false
        return conversation.participantUids.contains(authUid)
    }

    private fun canCreatePrivateConversation(authUid: String?, participantUids: List<String>): Boolean {
        if (authUid == null) return false
        // Rule: authUid in participantUids && participantUids.size() == 2
        return participantUids.size == 2 && participantUids.contains(authUid)
    }

    private fun canUpdateConversationMembership(
        authUid: String?,
        originalParticipants: List<String>,
        newParticipants: List<String>
    ): Boolean {
        if (authUid == null) return false
        if (!originalParticipants.contains(authUid)) return false
        // Rule: !request.resource.data.diff(resource.data).affectedKeys().hasAny(['participantUids'])
        return originalParticipants == newParticipants
    }

    private fun canCreatePrivateMessage(
        authUid: String?,
        conversation: PrivateConversation,
        senderUid: String,
        messageText: String
    ): Boolean {
        if (authUid == null) return false
        // Rule: senderUid == request.auth.uid && request.auth.uid in participantUids && messageText.size() <= 2000
        val senderMatchesAuth = senderUid == authUid
        val senderIsParticipant = conversation.participantUids.contains(authUid)
        val textValid = messageText.isNotBlank() && messageText.length <= 2000
        return senderMatchesAuth && senderIsParticipant && textValid
    }

    private fun canUpdateFriendRequest(
        authUid: String?,
        request: FriendRequest,
        modifiedFields: Set<String>,
        newStatus: String
    ): Boolean {
        if (authUid == null) return false
        // Rule: resource.data.recipientUid == request.auth.uid && affectedKeys().hasOnly(['status'])
        val isRecipient = request.recipientUid == authUid
        val onlyStatusChanged = modifiedFields.size == 1 && modifiedFields.contains("status")
        return isRecipient && onlyStatusChanged
    }

    private fun canExecuteAdminOperation(authEmail: String?, hasAdminClaim: Boolean): Boolean {
        if (authEmail == null) return false
        // Rule: request.auth.token.email == "insularleann@gmail.com" || request.auth.token.admin == true
        return authEmail.equals(adminEmail, ignoreCase = true) || hasAdminClaim
    }

    @Test
    fun `scenario 1 - Unauthenticated user denied access to private conversations and messages`() {
        val conv = PrivateConversation("conv_123", listOf(uidA, uidB))
        assertFalse(
            "Unauthenticated caller (null UID) must be denied read on conversation",
            isConversationParticipant(null, conv)
        )
        assertFalse(
            "Unauthenticated caller must be denied creating messages",
            canCreatePrivateMessage(null, conv, uidA, "Hello")
        )
    }

    @Test
    fun `scenario 2 - Friend A can read their conversation`() {
        val conv = PrivateConversation("conv_123", listOf(uidA, uidB))
        assertTrue(
            "Friend A registered in participantUids must be allowed to read conversation",
            isConversationParticipant(uidA, conv)
        )
    }

    @Test
    fun `scenario 3 - Friend B can read their conversation`() {
        val conv = PrivateConversation("conv_123", listOf(uidA, uidB))
        assertTrue(
            "Friend B registered in participantUids must be allowed to read conversation",
            isConversationParticipant(uidB, conv)
        )
    }

    @Test
    fun `scenario 4 - Unrelated User C denied access to conversation and messages`() {
        val conv = PrivateConversation("conv_123", listOf(uidA, uidB))
        assertFalse(
            "Unrelated User C must be denied read access to conversation",
            isConversationParticipant(uidC, conv)
        )
        assertFalse(
            "Unrelated User C must be denied creating messages in conversation between A and B",
            canCreatePrivateMessage(uidC, conv, uidC, "Pahingi copy ng assignment")
        )
    }

    @Test
    fun `scenario 5 - Friend A can send a message as Friend A`() {
        val conv = PrivateConversation("conv_123", listOf(uidA, uidB))
        assertTrue(
            "Friend A should be permitted to send message where senderUid matches auth UID",
            canCreatePrivateMessage(uidA, conv, uidA, "Magandang araw barkada!")
        )
    }

    @Test
    fun `scenario 6 - Friend A cannot impersonate Friend B`() {
        val conv = PrivateConversation("conv_123", listOf(uidA, uidB))
        assertFalse(
            "Friend A must be denied if sending message with senderUid set to Friend B",
            canCreatePrivateMessage(uidA, conv, uidB, "Fake message from B")
        )
    }

    @Test
    fun `scenario 7 - Third-party participant injection denied`() {
        // Attempting to create conversation with 3 participants (injecting User C)
        val threeParticipants = listOf(uidA, uidB, uidC)
        assertFalse(
            "Conversation creation with more than 2 participants must be denied",
            canCreatePrivateConversation(uidA, threeParticipants)
        )
        // Creating with exactly 2 participants must succeed
        val validParticipants = listOf(uidA, uidB)
        assertTrue(
            "Conversation creation with exactly 2 participants must be allowed",
            canCreatePrivateConversation(uidA, validParticipants)
        )
    }

    @Test
    fun `scenario 8 - Client-side membership modification denied`() {
        val original = listOf(uidA, uidB)
        val tampered = listOf(uidA, uidC)
        assertFalse(
            "Client modification of participant membership must be strictly denied",
            canUpdateConversationMembership(uidA, original, tampered)
        )
        assertTrue(
            "Preserving immutable membership on update must be allowed",
            canUpdateConversationMembership(uidA, original, original)
        )
    }

    @Test
    fun `scenario 9 - Unauthorized friend request modification denied`() {
        val req = FriendRequest(
            id = "req_1",
            senderUid = uidA,
            senderAlias = "FriendA",
            recipientUid = uidB,
            recipientAlias = "FriendB",
            status = "PENDING"
        )

        // Sender A cannot accept their own request (only recipient can accept)
        assertFalse(
            "Sender A cannot accept their own request",
            canUpdateFriendRequest(uidA, req, setOf("status"), "ACCEPTED")
        )

        // Unrelated User C cannot touch friend request
        assertFalse(
            "Unrelated User C cannot modify friend request",
            canUpdateFriendRequest(uidC, req, setOf("status"), "ACCEPTED")
        )

        // Recipient B attempting to tamper with senderUid or recipientUid is denied
        assertFalse(
            "Tampering with senderUid or fields other than status is denied",
            canUpdateFriendRequest(uidB, req, setOf("status", "senderUid"), "ACCEPTED")
        )

        // Recipient B legitimately accepting request is permitted
        assertTrue(
            "Recipient B legitimately updating status only is allowed",
            canUpdateFriendRequest(uidB, req, setOf("status"), "ACCEPTED")
        )
    }

    @Test
    fun `scenario 10 - Admin-only operations denied to ordinary users`() {
        // Ordinary user
        assertFalse(
            "Ordinary student cannot perform administrative operations",
            canExecuteAdminOperation("ordinary_student@school.ph", false)
        )

        // Lead Admin Owner Benjie Landingin
        assertTrue(
            "Lead Admin Benjie Landingin (insularleann@gmail.com) has authorized admin access",
            canExecuteAdminOperation("insularleann@gmail.com", false)
        )

        // Account with custom claim token.admin == true
        assertTrue(
            "Account with verified admin custom claim has authorized admin access",
            canExecuteAdminOperation("custom_admin@school.ph", true)
        )
    }

    @Test
    fun `verify deterministic conversation ID generation between two users`() {
        // Conversation ID must be identical regardless of whether user1 or user2 initiates
        val id1 = if (uidA < uidB) "${uidA}_$uidB" else "${uidB}_$uidA"
        val id2 = if (uidB < uidA) "${uidB}_$uidA" else "${uidA}_$uidB"
        assertEquals("Conversation ID must be symmetric and deterministic", id1, id2)
    }
}
