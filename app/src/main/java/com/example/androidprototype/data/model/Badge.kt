package com.example.androidprototype.data.model

import androidx.compose.ui.graphics.Color

enum class BadgeRarity(
    val displayName: String,
    val primaryColor: Color,
    val gradientColors: List<Color>
) {
    COMMON(
        displayName = "Yaygın",
        primaryColor = Color(0xFF00BFA5), // Teal
        gradientColors = listOf(Color(0xFF00BFA5), Color(0xFF00E676))
    ),
    RARE(
        displayName = "Nadir",
        primaryColor = Color(0xFF2979FF), // Blue
        gradientColors = listOf(Color(0xFF2979FF), Color(0xFF00E5FF))
    ),
    EPIC(
        displayName = "Destansı",
        primaryColor = Color(0xFFD500F9), // Purple
        gradientColors = listOf(Color(0xFFD500F9), Color(0xFF7C4DFF))
    ),
    LEGENDARY(
        displayName = "Efsanevi",
        primaryColor = Color(0xFFFFD600), // Gold
        gradientColors = listOf(Color(0xFFFFD600), Color(0xFFFF6D00))
    )
}

enum class BadgeCategory(val displayName: String) {
    CODING("Kodlama"),
    DESIGN("Tasarım"),
    COMMUNITY("Topluluk"),
    STREAK("İstikrar")
}

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String, // Maps to vector asset name or system icon
    val rarity: BadgeRarity,
    val category: BadgeCategory,
    val isUnlocked: Boolean = false,
    val unlockedDate: String? = null,
    val xpReward: Int = 100,
    val currentProgress: Int = 0,
    val maxProgress: Int = 1
) {
    val progressPercentage: Float
        get() = if (maxProgress > 0) currentProgress.toFloat() / maxProgress.toFloat() else 0f
}
