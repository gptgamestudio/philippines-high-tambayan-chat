package com.example.model

/**
 * Role-Based Access Control (RBAC) model for Philippine High Tambayan Chatroom.
 * Enforces strict least-privilege security. Ordinary members cannot escalate themselves
 * to moderator or administrator roles through client-side modifications.
 */
enum class UserRole(val displayName: String, val level: Int, val badgeEmoji: String) {
    MEMBER("Member", 1, "🎒"),
    MODERATOR("Moderator", 2, "🛡️"),
    ADMIN("Administrator", 3, "👑");

    fun canModerateContent(): Boolean = level >= MODERATOR.level
    fun canManageSettings(): Boolean = level >= ADMIN.level
}
