package com.example.androidprototype.ui.main

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidprototype.ui.map.MapScreen
import com.example.androidprototype.ui.profile.ProfileScreen
import com.example.androidprototype.ui.profile.ProfileViewModel
import com.example.androidprototype.ui.quests.QuestsScreen
import com.example.androidprototype.ui.squad.SquadScreen
import com.example.androidprototype.ui.theme.*

enum class AppTab(val title: String, val icon: ImageVector) {
    HARITA("harita", Icons.Rounded.Map),
    GOREVLER("görevler", Icons.Rounded.Casino),
    SQUAD("squad", Icons.Rounded.Group),
    PROFIL("profil", Icons.Rounded.Person)
}

@Composable
fun MainScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    var activeTab by remember { mutableStateOf(AppTab.HARITA) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        bottomBar = {
            GlassBottomNavigation(
                activeTab = activeTab,
                onTabSelected = { activeTab = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Crossfade transition when switching screens
            Crossfade(
                targetState = activeTab,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "ScreenSwitch"
            ) { tab ->
                when (tab) {
                    AppTab.HARITA -> MapScreen(viewModel = viewModel)
                    AppTab.GOREVLER -> QuestsScreen(viewModel = viewModel)
                    AppTab.SQUAD -> SquadScreen()
                    AppTab.PROFIL -> ProfileScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun GlassBottomNavigation(
    activeTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .height(68.dp)
            .clip(RoundedCornerShape(34.dp))
            .background(GlassBg)
            .border(
                width = 0.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.White.copy(alpha = 0.02f)
                    )
                ),
                shape = RoundedCornerShape(34.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTab.values().forEach { tab ->
                val isSelected = tab == activeTab
                
                // Micro-animations on selection
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
                    label = "TabScale"
                )
                val alpha by animateFloatAsState(
                    targetValue = if (isSelected) 1.0f else 0.5f,
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "TabAlpha"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null, // Disable default material ripple to fit our custom indicator
                            onClick = { onTabSelected(tab) }
                        )
                        .scale(scale)
                        .alpha(alpha)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) PrimaryGreen else TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )

                        // Notification Badge for Quests
                        if (tab == AppTab.GOREVLER) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 6.dp, y = (-4).dp)
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryPurple)
                                    .border(1.dp, BackgroundDark, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "2",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = tab.title,
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) PrimaryGreen else TextPrimary
                    )

                    // Small neon active glow dot beneath the tab label
                    if (isSelected) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen)
                                .blur(1.dp)
                        )
                    }
                }
            }
        }
    }
}
