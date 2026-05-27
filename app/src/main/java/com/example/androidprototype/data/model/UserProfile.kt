package com.example.androidprototype.data.model

data class UserProfile(
    val username: String,
    val displayName: String,
    val title: String,
    val avatarUrl: String,
    val level: Int,
    val currentXp: Int,
    val nextLevelXp: Int,
    val completedTasks: Int,
    val activeDays: Int,
    val globalRank: Int
) {
    val xpProgressPercentage: Float
        get() = if (nextLevelXp > 0) currentXp.toFloat() / nextLevelXp.toFloat() else 0f
}
