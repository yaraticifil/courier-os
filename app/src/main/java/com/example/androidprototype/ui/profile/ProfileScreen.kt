package com.example.androidprototype.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidprototype.data.model.Badge
import com.example.androidprototype.ui.components.BadgeCard
import com.example.androidprototype.ui.components.BadgeDetailCard
import com.example.androidprototype.ui.components.LevelUpDialog
import com.example.androidprototype.ui.components.XPProgressBar
import com.example.androidprototype.ui.components.getBadgeIcon
import com.example.androidprototype.ui.theme.BackgroundDark
import com.example.androidprototype.ui.theme.GlassBg
import com.example.androidprototype.ui.theme.GlassBorder
import com.example.androidprototype.ui.theme.PrimaryGreen
import com.example.androidprototype.ui.theme.PrimaryPurple
import com.example.androidprototype.ui.theme.ProfileRingGradient
import com.example.androidprototype.ui.theme.SurfaceDark
import com.example.androidprototype.ui.theme.SurfaceVariantDark
import com.example.androidprototype.ui.theme.TextPrimary
import com.example.androidprototype.ui.theme.TextSecondary
import com.example.androidprototype.ui.theme.TextTertiary
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    val profile by viewModel.userProfile.collectAsState()
    val badges by viewModel.badges.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val selectedSort by viewModel.selectedSort.collectAsState()
    val featuredBadge by viewModel.featuredBadge.collectAsState()

    val levelUpEvent by viewModel.levelUpEvent.collectAsState()
    val badgeUnlockEvent by viewModel.badgeUnlockEvent.collectAsState()

    var activeDetailBadge by remember { mutableStateOf<Badge?>(null) }
    var isSimulatorExpanded by remember { mutableStateOf(false) }

    // Auto dismiss unlock notifications after 3 seconds
    var showUnlockBanner by remember { mutableStateOf(false) }
    var unlockedBadgeName by remember { mutableStateOf("") }
    
    LaunchedEffect(badgeUnlockEvent) {
        badgeUnlockEvent?.let {
            unlockedBadgeName = it.title
            showUnlockBanner = true
            delay(3500)
            showUnlockBanner = false
            viewModel.dismissBadgeUnlock()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 80.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Profile Header Item
            item(span = { GridItemSpan(2) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Avatar with glowing gradient circle
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(116.dp)
                    ) {
                        // Halo glow
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .blur(20.dp)
                                .alpha(0.35f)
                                .background(PrimaryPurple, CircleShape)
                        )

                        // Animated gradient ring
                        Canvas(modifier = Modifier.size(108.dp)) {
                            drawCircle(
                                brush = Brush.sweepGradient(ProfileRingGradient),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
                            )
                        }

                        // Inner avatar drawing
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(SurfaceVariantDark)
                                .border(1.dp, GlassBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            // Canvas-drawn abstract user avatar
                            Canvas(modifier = Modifier.size(60.dp)) {
                                // Head
                                drawCircle(
                                    color = PrimaryGreen.copy(alpha = 0.8f),
                                    radius = 18.dp.toPx(),
                                    center = center.copy(y = center.y - 10.dp.toPx())
                                )
                                // Shoulder curve
                                drawArc(
                                    color = PrimaryPurple.copy(alpha = 0.8f),
                                    startAngle = 180f,
                                    sweepAngle = 180f,
                                    useCenter = true,
                                    size = size.copy(width = 48.dp.toPx(), height = 48.dp.toPx()),
                                    topLeft = androidx.compose.ui.geometry.Offset(
                                        x = center.x - 24.dp.toPx(),
                                        y = center.y + 6.dp.toPx()
                                    )
                                )
                            }
                        }

                        // Level badge attached at bottom
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 6.dp, bottom = 4.dp)
                                .background(
                                    Brush.horizontalGradient(listOf(Color(0xFFFFD600), Color(0xFFFF6D00))),
                                    RoundedCornerShape(10.dp)
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Lvl ${profile.level}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = BackgroundDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Names & Handle
                    Text(
                        text = profile.displayName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = profile.username,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryGreen
                    )
                    Text(
                        text = profile.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    // Featured Badge (Vitrin) if exists
                    featuredBadge?.let { badge ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(badge.rarity.primaryColor.copy(alpha = 0.12f))
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        Brush.horizontalGradient(
                                            listOf(
                                                badge.rarity.primaryColor,
                                                badge.rarity.primaryColor.copy(alpha = 0.2f)
                                            )
                                        )
                                    ),
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .clickable { activeDetailBadge = badge }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = "Featured",
                                    tint = badge.rarity.primaryColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = getBadgeIcon(badge.iconName),
                                    contentDescription = badge.title,
                                    tint = badge.rarity.primaryColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "VİTRİN: ${badge.title}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // XP Progress bar
                    XPProgressBar(
                        currentXp = profile.currentXp,
                        nextLevelXp = profile.nextLevelXp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            // Stats Dashboard
            item(span = { GridItemSpan(2) }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassBg),
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "KARİYER İSTATİSTİKLERİ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatItem(
                                value = profile.completedTasks.toString(),
                                label = "Görev",
                                icon = Icons.Rounded.TaskAlt,
                                color = PrimaryPurple,
                                modifier = Modifier.weight(1f)
                            )
                            StatItem(
                                value = profile.activeDays.toString(),
                                label = "Aktif Gün",
                                icon = Icons.Rounded.Schedule,
                                color = PrimaryGreen,
                                modifier = Modifier.weight(1f)
                            )
                            StatItem(
                                value = badges.count { it.isUnlocked }.toString(),
                                label = "Rozet",
                                icon = Icons.Rounded.EmojiEvents,
                                color = Color(0xFFFFD600),
                                modifier = Modifier.weight(1f)
                            )
                            StatItem(
                                value = "#${profile.globalRank}",
                                label = "Sıralama",
                                icon = Icons.Rounded.TrendingUp,
                                color = Color(0xFFD500F9),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Simulator Dashboard Controls (collapsible)
            item(span = { GridItemSpan(2) }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, GlassBorder.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isSimulatorExpanded = !isSimulatorExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Casino,
                                    contentDescription = "Simülatör",
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PROTOTİP SİMÜLATÖRÜ",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Icon(
                                imageVector = if (isSimulatorExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                                contentDescription = "Expand",
                                tint = TextSecondary
                            )
                        }

                        if (isSimulatorExpanded) {
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            // XP Simulator Buttons
                            Text(
                                text = "Deneyim Puanı Kazan (XP Simülasyonu):",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.addXp(150) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GlassBg, contentColor = PrimaryGreen),
                                    border = BorderStroke(0.5.dp, PrimaryGreen.copy(alpha = 0.5f))
                                ) {
                                    Text("+150 XP", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = { viewModel.addXp(400) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GlassBg, contentColor = PrimaryPurple),
                                    border = BorderStroke(0.5.dp, PrimaryPurple.copy(alpha = 0.5f))
                                ) {
                                    Text("+400 XP", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Badges Increments
                            Text(
                                text = "Kilitli Rozet İlerlemelerini Simüle Et:",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                maxItemsInEachRow = 2,
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Find locked badges
                                val lockedList = badges.filter { !it.isUnlocked }
                                if (lockedList.isEmpty()) {
                                    Text(
                                        text = "Tüm rozetler kazanıldı!",
                                        fontSize = 12.sp,
                                        color = Color(0xFF00E676),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                } else {
                                    lockedList.take(2).forEach { badge ->
                                        Button(
                                            onClick = { viewModel.incrementBadgeProgress(badge.id, 1) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = TextPrimary),
                                            border = BorderStroke(0.5.dp, GlassBorder)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Add,
                                                    contentDescription = "Add",
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = badge.title,
                                                    fontSize = 11.sp,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Reset Button
                            Button(
                                onClick = { viewModel.resetData() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350).copy(alpha = 0.12f), contentColor = Color(0xFFEF5350)),
                                border = BorderStroke(0.5.dp, Color(0xFFEF5350).copy(alpha = 0.5f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Refresh,
                                        contentDescription = "Sıfırla",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Verileri Sıfırla", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Divider & Collection Title
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ROZET KOLEKSİYONU",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            letterSpacing = 0.5.sp
                        )
                        
                        FilterSortControls(
                            currentFilter = selectedFilter,
                            currentSort = selectedSort,
                            onFilterChange = { viewModel.setFilter(it) },
                            onSortChange = { viewModel.setSort(it) }
                        )
                    }
                }
            }

            // Badges Grid
            if (badges.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Filtreye uygun rozet bulunamadı.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                items(badges) { badge ->
                    BadgeCard(
                        badge = badge,
                        onClick = { activeDetailBadge = badge }
                    )
                }
            }
        }

        // Floating Toast Notification for Badge Unlock
        AnimatedVisibility(
            visible = showUnlockBanner,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 12.dp, start = 16.dp, end = 16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(Color(0xFFFFD600), Color(0xFFFF6D00)))), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD600).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = "Unlock",
                            tint = Color(0xFFFFD600),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "YENİ ROZET KAZANILDI!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD600),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = unlockedBadgeName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // Level Up Celebration overlay
        levelUpEvent?.let { level ->
            LevelUpDialog(
                level = level,
                onDismiss = { viewModel.dismissLevelUp() }
            )
        }

        // Detail Bottom Sheet Overlay
        BadgeDetailCard(
            badge = activeDetailBadge,
            onDismiss = { activeDetailBadge = null },
            onFeatureEquip = { viewModel.toggleFeaturedBadge(it) },
            isFeatured = featuredBadge?.id == activeDetailBadge?.id
        )
    }
}

/**
 * Micro-animated stats card within dashboard
 */
@Composable
fun StatItem(
    value: String,
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(horizontal = 4.dp)
            .background(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color.copy(alpha = 0.1f))
                .border(0.5.dp, color.copy(alpha = 0.25f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
    }
}

/**
 * Floating filter and sort popup control triggers
 */
@Composable
fun FilterSortControls(
    currentFilter: BadgeFilter,
    currentSort: BadgeSort,
    onFilterChange: (BadgeFilter) -> Unit,
    onSortChange: (BadgeSort) -> Unit
) {
    var isFilterMenuExpanded by remember { mutableStateOf(false) }
    var isSortMenuExpanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Filter Trigger Button
        Box {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceDark)
                    .border(0.5.dp, GlassBorder, RoundedCornerShape(8.dp))
                    .clickable { isFilterMenuExpanded = true }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.FilterList,
                    contentDescription = "Filtrele",
                    tint = PrimaryPurple,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when (currentFilter) {
                        BadgeFilter.ALL -> "Tümü"
                        BadgeFilter.UNLOCKED -> "Kazanılanlar"
                        BadgeFilter.LOCKED -> "Kilitliler"
                    },
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }

            DropdownMenu(
                expanded = isFilterMenuExpanded,
                onDismissRequest = { isFilterMenuExpanded = false },
                modifier = Modifier.background(SurfaceDark).border(0.5.dp, GlassBorder)
            ) {
                DropdownMenuItem(
                    text = { Text("Tümü", color = TextPrimary, fontSize = 12.sp) },
                    onClick = {
                        onFilterChange(BadgeFilter.ALL)
                        isFilterMenuExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Kazanılanlar", color = TextPrimary, fontSize = 12.sp) },
                    onClick = {
                        onFilterChange(BadgeFilter.UNLOCKED)
                        isFilterMenuExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Kilitliler", color = TextPrimary, fontSize = 12.sp) },
                    onClick = {
                        onFilterChange(BadgeFilter.LOCKED)
                        isFilterMenuExpanded = false
                    }
                )
            }
        }

        // Sort Trigger Button
        Box {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceDark)
                    .border(0.5.dp, GlassBorder, RoundedCornerShape(8.dp))
                    .clickable { isSortMenuExpanded = true }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.DirectionsRun,
                    contentDescription = "Sırala",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when (currentSort) {
                        BadgeSort.DEFAULT -> "Varsayılan"
                        BadgeSort.RARITY -> "Nadirlik"
                        BadgeSort.PROGRESS -> "İlerleme"
                    },
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }

            DropdownMenu(
                expanded = isSortMenuExpanded,
                onDismissRequest = { isSortMenuExpanded = false },
                modifier = Modifier.background(SurfaceDark).border(0.5.dp, GlassBorder)
            ) {
                DropdownMenuItem(
                    text = { Text("Varsayılan", color = TextPrimary, fontSize = 12.sp) },
                    onClick = {
                        onSortChange(BadgeSort.DEFAULT)
                        isSortMenuExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Nadirlik Derecesi", color = TextPrimary, fontSize = 12.sp) },
                    onClick = {
                        onSortChange(BadgeSort.RARITY)
                        isSortMenuExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Kazanım İlerlemesi", color = TextPrimary, fontSize = 12.sp) },
                    onClick = {
                        onSortChange(BadgeSort.PROGRESS)
                        isSortMenuExpanded = false
                    }
                )
            }
        }
    }
}
