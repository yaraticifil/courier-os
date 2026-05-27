package com.example.androidprototype.data.repository

import com.example.androidprototype.data.model.Badge
import com.example.androidprototype.data.model.BadgeCategory
import com.example.androidprototype.data.model.BadgeRarity
import com.example.androidprototype.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileRepository {

    private val _userProfile = MutableStateFlow(
        UserProfile(
            username = "@antigravity_dev",
            displayName = "Sanoi Antigravity",
            title = "Kıdemli Compose Geliştirici",
            avatarUrl = "", // We will draw an interactive canvas avatar
            level = 12,
            currentXp = 680,
            nextLevelXp = 1200,
            completedTasks = 84,
            activeDays = 42,
            globalRank = 284
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _badges = MutableStateFlow<List<Badge>>(initialBadges())
    val badges: StateFlow<List<Badge>> = _badges.asStateFlow()

    // Level up event broadcast. In a real app this might use shared flows or channels.
    private val _levelUpEvent = MutableStateFlow<Int?>(null)
    val levelUpEvent: StateFlow<Int?> = _levelUpEvent.asStateFlow()

    // Badge unlock event broadcast
    private val _badgeUnlockEvent = MutableStateFlow<Badge?>(null)
    val badgeUnlockEvent: StateFlow<Badge?> = _badgeUnlockEvent.asStateFlow()

    fun clearEvents() {
        _levelUpEvent.value = null
        _badgeUnlockEvent.value = null
    }

    fun addXp(amount: Int) {
        val currentProfile = _userProfile.value
        var newXp = currentProfile.currentXp + amount
        var newLevel = currentProfile.level
        var nextXp = currentProfile.nextLevelXp

        while (newXp >= nextXp) {
            newXp -= nextXp
            newLevel++
            nextXp = (nextXp * 1.25).toInt() // Level XP increases by 25% each level
            _levelUpEvent.value = newLevel
        }

        _userProfile.update {
            it.copy(
                currentXp = newXp,
                level = newLevel,
                nextLevelXp = nextXp
            )
        }
    }

    fun simulateBadgeProgress(badgeId: String, amount: Int) {
        _badges.update { list ->
            list.map { badge ->
                if (badge.id == badgeId && !badge.isUnlocked) {
                    val newProgress = (badge.currentProgress + amount).coerceAtMost(badge.maxProgress)
                    val isNowUnlocked = newProgress == badge.maxProgress
                    
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    val unlockDate = if (isNowUnlocked) dateFormat.format(Date()) else null

                    val updatedBadge = badge.copy(
                        currentProgress = newProgress,
                        isUnlocked = isNowUnlocked,
                        unlockedDate = unlockDate
                    )

                    if (isNowUnlocked) {
                        _badgeUnlockEvent.value = updatedBadge
                        // Give XP reward
                        addXp(badge.xpReward)
                    }

                    updatedBadge
                } else {
                    badge
                }
            }
        }
    }

    fun resetData() {
        _userProfile.value = UserProfile(
            username = "@antigravity_dev",
            displayName = "Sanoi Antigravity",
            title = "Kıdemli Compose Geliştirici",
            avatarUrl = "",
            level = 12,
            currentXp = 680,
            nextLevelXp = 1200,
            completedTasks = 84,
            activeDays = 42,
            globalRank = 284
        )
        _badges.value = initialBadges()
        clearEvents()
    }

    private fun initialBadges(): List<Badge> {
        return listOf(
            Badge(
                id = "badge_first_step",
                title = "İlk Adım",
                description = "İlk Kotlin ve Jetpack Compose kodunu başarıyla yazdın.",
                iconName = "Code",
                rarity = BadgeRarity.COMMON,
                category = BadgeCategory.CODING,
                isUnlocked = true,
                unlockedDate = "12.04.2026",
                xpReward = 150,
                currentProgress = 1,
                maxProgress = 1
            ),
            Badge(
                id = "badge_ui_wizard",
                title = "Arayüz Sihirbazı",
                description = "Custom Canvas ve Compose kullanarak 5 adet premium ekran tasarla.",
                iconName = "Palette",
                rarity = BadgeRarity.RARE,
                category = BadgeCategory.DESIGN,
                isUnlocked = false,
                xpReward = 350,
                currentProgress = 3,
                maxProgress = 5
            ),
            Badge(
                id = "badge_streak_master",
                title = "İstikrar Abidesi",
                description = "Günde en az bir commit ile ardışık 15 gün boyunca kod geliştir.",
                iconName = "Speed",
                rarity = BadgeRarity.EPIC,
                category = BadgeCategory.STREAK,
                isUnlocked = false,
                xpReward = 800,
                currentProgress = 12,
                maxProgress = 15
            ),
            Badge(
                id = "badge_legendary_architect",
                title = "Efsanevi Mimar",
                description = "Temiz Mimari standartlarında 10,000 satır modüler kod yaz.",
                iconName = "Architecture",
                rarity = BadgeRarity.LEGENDARY,
                category = BadgeCategory.CODING,
                isUnlocked = true,
                unlockedDate = "24.05.2026",
                xpReward = 1500,
                currentProgress = 10000,
                maxProgress = 10000
            ),
            Badge(
                id = "badge_bug_slayer",
                title = "Hata Avcısı",
                description = "Kritik seviyedeki 5 bellek sızıntısı veya ANR hatasını çöz.",
                iconName = "BugReport",
                rarity = BadgeRarity.COMMON,
                category = BadgeCategory.CODING,
                isUnlocked = true,
                unlockedDate = "05.05.2026",
                xpReward = 200,
                currentProgress = 5,
                maxProgress = 5
            ),
            Badge(
                id = "badge_community_star",
                title = "Topluluk Yıldızı",
                description = "StackOverflow veya Discord grubunda 10 adet Android sorusunu yanıtla.",
                iconName = "Groups",
                rarity = BadgeRarity.RARE,
                category = BadgeCategory.COMMUNITY,
                isUnlocked = false,
                xpReward = 400,
                currentProgress = 8,
                maxProgress = 10
            ),
            Badge(
                id = "badge_canvas_guru",
                title = "Canvas Gurusu",
                description = "Jetpack Compose Canvas API ile 3 adet özel grafik çiz.",
                iconName = "Brush",
                rarity = BadgeRarity.EPIC,
                category = BadgeCategory.DESIGN,
                isUnlocked = true,
                unlockedDate = "20.05.2026",
                xpReward = 750,
                currentProgress = 3,
                maxProgress = 3
            ),
            Badge(
                id = "badge_hackathon_champion",
                title = "Hackathon Fatihi",
                description = "Bölgesel veya küresel bir hackathona katılarak ilk 3 dereceye gir.",
                iconName = "EmojiEvents",
                rarity = BadgeRarity.LEGENDARY,
                category = BadgeCategory.COMMUNITY,
                isUnlocked = false,
                xpReward = 2000,
                currentProgress = 0,
                maxProgress = 1
            )
        )
    }
}
