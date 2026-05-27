package com.example.androidprototype.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidprototype.data.model.Badge
import com.example.androidprototype.data.model.UserProfile
import com.example.androidprototype.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class BadgeFilter {
    ALL, UNLOCKED, LOCKED
}

enum class BadgeSort {
    DEFAULT, RARITY, PROGRESS
}

class ProfileViewModel(
    private val repository: ProfileRepository = ProfileRepository()
) : ViewModel() {

    val userProfile: StateFlow<UserProfile> = repository.userProfile
    
    private val _selectedFilter = MutableStateFlow(BadgeFilter.ALL)
    val selectedFilter: StateFlow<BadgeFilter> = _selectedFilter.asStateFlow()

    private val _selectedSort = MutableStateFlow(BadgeSort.DEFAULT)
    val selectedSort: StateFlow<BadgeSort> = _selectedSort.asStateFlow()

    private val _featuredBadge = MutableStateFlow<Badge?>(null)
    val featuredBadge: StateFlow<Badge?> = _featuredBadge.asStateFlow()

    // Combining repository badges flow with filter and sorting states
    val badges: StateFlow<List<Badge>> = combine(
        repository.badges,
        _selectedFilter,
        _selectedSort
    ) { list, filter, sort ->
        var result = when (filter) {
            BadgeFilter.ALL -> list
            BadgeFilter.UNLOCKED -> list.filter { it.isUnlocked }
            BadgeFilter.LOCKED -> list.filter { !it.isUnlocked }
        }

        result = when (sort) {
            BadgeSort.DEFAULT -> result
            BadgeSort.RARITY -> result.sortedByDescending { it.rarity.ordinal }
            BadgeSort.PROGRESS -> result.sortedByDescending { it.progressPercentage }
        }

        // Auto-assign first legendary or epic badge to featured on start if none set yet
        if (_featuredBadge.value == null) {
            val autoFeatured = list.firstOrNull { it.isUnlocked && it.rarity.ordinal >= 2 }
            _featuredBadge.value = autoFeatured
        }

        result
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val levelUpEvent: StateFlow<Int?> = repository.levelUpEvent
    val badgeUnlockEvent: StateFlow<Badge?> = repository.badgeUnlockEvent

    fun setFilter(filter: BadgeFilter) {
        _selectedFilter.value = filter
    }

    fun setSort(sort: BadgeSort) {
        _selectedSort.value = sort
    }

    fun toggleFeaturedBadge(badge: Badge) {
        if (_featuredBadge.value?.id == badge.id) {
            _featuredBadge.value = null
        } else {
            _featuredBadge.value = badge
        }
    }

    fun addXp(amount: Int) {
        repository.addXp(amount)
    }

    fun incrementBadgeProgress(badgeId: String, amount: Int = 1) {
        repository.simulateBadgeProgress(badgeId, amount)
    }

    fun resetData() {
        repository.resetData()
        _featuredBadge.value = null
    }

    fun dismissLevelUp() {
        repository.clearEvents()
    }

    fun dismissBadgeUnlock() {
        repository.clearEvents()
    }
}
