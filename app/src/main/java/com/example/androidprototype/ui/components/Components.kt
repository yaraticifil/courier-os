package com.example.androidprototype.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Architecture
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.androidprototype.data.model.Badge
import com.example.androidprototype.data.model.BadgeRarity
import com.example.androidprototype.ui.theme.BackgroundDark
import com.example.androidprototype.ui.theme.GlassBg
import com.example.androidprototype.ui.theme.GlassBorder
import com.example.androidprototype.ui.theme.PrimaryGreen
import com.example.androidprototype.ui.theme.PrimaryPurple
import com.example.androidprototype.ui.theme.ProgressTrackColor
import com.example.androidprototype.ui.theme.SurfaceDark
import com.example.androidprototype.ui.theme.SurfaceVariantDark
import com.example.androidprototype.ui.theme.TextPrimary
import com.example.androidprototype.ui.theme.TextSecondary
import com.example.androidprototype.ui.theme.TextTertiary
import kotlinx.coroutines.delay
import kotlin.random.Random

// Maps string name to appropriate Material Vector Icon
@Composable
fun getBadgeIcon(iconName: String): ImageVector {
    return when (iconName) {
        "Code" -> Icons.Rounded.Code
        "Palette" -> Icons.Rounded.Palette
        "Speed" -> Icons.Rounded.Speed
        "Architecture" -> Icons.Rounded.Architecture
        "BugReport" -> Icons.Rounded.BugReport
        "Groups" -> Icons.Rounded.Groups
        "Brush" -> Icons.Rounded.Brush
        "EmojiEvents" -> Icons.Rounded.EmojiEvents
        else -> Icons.Rounded.Star
    }
}

/**
 * Custom-styled XP Progress Bar with glowing neon filling and micro-animations
 */
@Composable
fun XPProgressBar(
    currentXp: Int,
    nextLevelXp: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (nextLevelXp > 0) currentXp.toFloat() / nextLevelXp.toFloat() else 0f
    
    // Animate progress changes smoothly
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "XPProgress"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SEVİYE İLERLEMESİ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Text(
                text = "$currentXp / $nextLevelXp XP",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Premium Progress Bar Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(ProgressTrackColor)
                .border(1.dp, GlassBorder, RoundedCornerShape(9.dp))
        ) {
            // Neon glowing bar fill
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(9.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(PrimaryPurple, PrimaryGreen)
                        )
                    )
            ) {
                // Subtle shine/glow effect inside the progress fill
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.15f),
                                    Color.Transparent,
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }
    }
}

/**
 * High-fidelity animated Badge Card
 */
@Composable
fun BadgeCard(
    badge: Badge,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }
    val scaleFactor by animateFloatAsState(
        targetValue = if (isHovered) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "ScaleFactor"
    )

    val cardAlpha = if (badge.isUnlocked) 1f else 0.6f
    val borderGradient = if (badge.isUnlocked) {
        Brush.sweepGradient(badge.rarity.gradientColors)
    } else {
        Brush.sweepGradient(listOf(TextTertiary, TextTertiary))
    }

    Card(
        modifier = modifier
            .padding(6.dp)
            .scale(scaleFactor)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) SurfaceDark else SurfaceDark.copy(alpha = 0.5f)
        ),
        border = BorderStroke(
            1.5.dp, 
            if (badge.isUnlocked) borderGradient else Brush.linearGradient(listOf(GlassBorder, GlassBorder))
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (badge.isUnlocked) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            // Unlocked background glow reflection
            if (badge.isUnlocked) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .blur(30.dp)
                        .alpha(0.15f)
                        .background(badge.rarity.primaryColor, CircleShape)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Badge Icon with Rarity Circle
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            if (badge.isUnlocked) {
                                Brush.radialGradient(
                                    colors = listOf(
                                        badge.rarity.primaryColor.copy(alpha = 0.25f),
                                        Color.Transparent
                                    )
                                )
                            } else {
                                Brush.radialGradient(colors = listOf(GlassBg, Color.Transparent))
                            }
                        )
                        .border(
                            1.dp,
                            if (badge.isUnlocked) badge.rarity.primaryColor.copy(alpha = 0.5f) else GlassBorder,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (badge.isUnlocked) {
                        Icon(
                            imageVector = getBadgeIcon(badge.iconName),
                            contentDescription = badge.title,
                            tint = badge.rarity.primaryColor,
                            modifier = Modifier.size(28.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = "Kilitli",
                            tint = TextTertiary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Badge Title
                Text(
                    text = badge.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (badge.isUnlocked) TextPrimary else TextSecondary.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Progress Indicators or Rarity Pill
                if (badge.isUnlocked) {
                    Text(
                        text = badge.rarity.displayName,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = badge.rarity.primaryColor,
                        modifier = Modifier
                            .background(
                                badge.rarity.primaryColor.copy(alpha = 0.1f),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                } else {
                    // Small progress bar for locked badges
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = badge.progressPercentage,
                            color = TextSecondary.copy(alpha = 0.4f),
                            trackColor = ProgressTrackColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${badge.currentProgress}/${badge.maxProgress}",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextTertiary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom detailed bottom overlay card which works cleanly without compose-material Sheet dependencies.
 */
@Composable
fun BadgeDetailCard(
    badge: Badge?,
    onDismiss: () -> Unit,
    onFeatureEquip: (Badge) -> Unit,
    isFeatured: Boolean
) {
    AnimatedVisibility(
        visible = badge != null,
        enter = fadeIn() + scaleIn(initialScale = 0.9f),
        exit = fadeOut() + scaleOut(targetScale = 0.9f)
    ) {
        if (badge == null) return@AnimatedVisibility

        // Dynamic gradient outline
        val rarityGradient = Brush.verticalGradient(
            colors = listOf(badge.rarity.primaryColor, badge.rarity.primaryColor.copy(alpha = 0.2f))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.BottomCenter
        ) {
            // Elegant bottom slide up popup container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {} // block click propagation
                    .border(BorderStroke(1.dp, GlassBorder), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Close bar indicator
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(TextTertiary)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Kapat", tint = TextSecondary)
                        }
                    }

                    // Giant Badge representation
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        badge.rarity.primaryColor.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(1.5.dp, rarityGradient, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Halo glow behind
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .blur(20.dp)
                                .alpha(0.3f)
                                .background(badge.rarity.primaryColor, CircleShape)
                        )

                        Icon(
                            imageVector = getBadgeIcon(badge.iconName),
                            contentDescription = badge.title,
                            tint = badge.rarity.primaryColor,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Category & Rarity Row
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = badge.rarity.displayName.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = badge.rarity.primaryColor,
                            modifier = Modifier
                                .background(
                                    badge.rarity.primaryColor.copy(alpha = 0.1f),
                                    RoundedCornerShape(4.dp)
                                )
                                    .border(0.5.dp, badge.rarity.primaryColor.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = badge.category.displayName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            modifier = Modifier
                                .background(GlassBg, RoundedCornerShape(4.dp))
                                .border(0.5.dp, GlassBorder, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Title
                    Text(
                        text = badge.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description
                    Text(
                        text = badge.description,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Unlock details panel
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BackgroundDark.copy(alpha = 0.5f)),
                        border = BorderStroke(1.dp, GlassBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "ÖDÜL", fontSize = 10.sp, color = TextTertiary, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "+${badge.xpReward} XP",
                                    fontSize = 15.sp,
                                    color = PrimaryGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Divider
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(30.dp)
                                    .background(GlassBorder)
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "DURUM", fontSize = 10.sp, color = TextTertiary, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                if (badge.isUnlocked) {
                                    Text(
                                        text = "Kazanıldı (${badge.unlockedDate})",
                                        fontSize = 13.sp,
                                        color = Color(0xFF00E676),
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Text(
                                        text = "Kilitli (${badge.currentProgress}/${badge.maxProgress})",
                                        fontSize = 13.sp,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Dynamic button actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Feature Equip Toggle Button
                        Button(
                            onClick = {
                                if (badge.isUnlocked) {
                                    onFeatureEquip(badge)
                                    onDismiss()
                                }
                            },
                            enabled = badge.isUnlocked,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFeatured) Color(0xFFF44336).copy(alpha = 0.2f) else PrimaryPurple,
                                contentColor = if (isFeatured) Color(0xFFEF5350) else TextPrimary,
                                disabledContainerColor = GlassBg,
                                disabledContentColor = TextTertiary
                            ),
                            border = if (isFeatured) BorderStroke(1.dp, Color(0xFFEF5350).copy(alpha = 0.5f)) else null
                        ) {
                            Text(
                                text = if (!badge.isUnlocked) "Kilitli Rozet" else if (isFeatured) "Vitrin dışı yap" else "Vitrinde Göster",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        // Share action simulator
                        Button(
                            onClick = {
                                // Simulate sharing
                            },
                            modifier = Modifier.width(60.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceVariantDark,
                                contentColor = TextPrimary
                            ),
                            border = BorderStroke(1.dp, GlassBorder),
                            contentPadding = ButtonDefaults.ContentPadding
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = "Paylaş",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Level Up Celebration Modal Dialog with custom Confetti graphics and scaling animations
 */
@Composable
fun LevelUpDialog(
    level: Int?,
    onDismiss: () -> Unit
) {
    if (level == null) return

    val confettiParticles = remember {
        mutableStateListOf<ConfettiParticle>().apply {
            repeat(40) {
                add(
                    ConfettiParticle(
                        x = Random.nextFloat(),
                        y = Random.nextFloat() * -0.5f, // start above
                        speed = Random.nextFloat() * 8f + 4f,
                        size = Random.nextFloat() * 12.dp.value + 6.dp.value,
                        color = listOf(PrimaryPurple, PrimaryGreen, Color(0xFFFFD600), Color(0xFFD500F9)).random()
                    )
                )
            }
        }
    }

    // Custom animation ticker for confetti falling
    LaunchedEffect(key1 = level) {
        while (true) {
            delay(16)
            for (i in confettiParticles.indices) {
                val p = confettiParticles[i]
                var newY = p.y + p.speed * 0.015f
                if (newY > 1.2f) {
                    newY = -0.1f // loop
                }
                confettiParticles[i] = p.copy(y = newY)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.85f)
        ) {
            // Draw falling confetti
            Box(modifier = Modifier.fillMaxSize()) {
                confettiParticles.forEach { p ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize(p.size / 1000f) // tiny box scale
                            .align(
                                Alignment.TopStart
                            )
                            .padding(
                                start = (p.x * 350).dp,
                                top = (p.y * 650).dp
                            )
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(p.color)
                    )
                }

                // Core dialog body
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Golden shining glow card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .border(BorderStroke(2.dp, Brush.radialGradient(listOf(Color(0xFFFFD600), Color.Transparent))), RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Sparkling dynamic icon
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(
                                                Color(0xFFFFD600).copy(alpha = 0.2f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                    .border(2.dp, Color(0xFFFFD600), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.EmojiEvents,
                                    contentDescription = "Kupa",
                                    tint = Color(0xFFFFD600),
                                    modifier = Modifier.size(50.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "TEBRİKLER!",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD600),
                                letterSpacing = 2.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "SEVİYE ATLADIN",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Large level display
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "LVL ${level - 1}",
                                    fontSize = 18.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = "Yıldız",
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = "LVL $level",
                                    fontSize = 24.sp,
                                    color = PrimaryGreen,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Mükemmel gidiyorsun! Yeni hedeflere odaklan ve yeni rozetlerin kilidini açmaya devam et.",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD600), contentColor = BackgroundDark),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Harika! Devam Et",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val speed: Float,
    val size: Float,
    val color: Color
)
