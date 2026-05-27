package com.example.androidprototype.ui.map

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidprototype.ui.profile.ProfileViewModel
import com.example.androidprototype.ui.theme.*

data class SurgeZone(
    val name: String,
    val surgeRate: Int,
    val status: String,
    val isHighSurge: Boolean,
    val coordinates: Offset // Mapped on Canvas
)

@Composable
fun MapScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    val profile by viewModel.userProfile.collectAsState()
    
    val zones = remember {
        listOf(
            SurgeZone("Kadıköy", 45, "+%45 surge aktif", true, Offset(0.35f, 0.65f)),
            SurgeZone("Beşiktaş", 30, "+%30 surge aktif", true, Offset(0.5f, 0.35f)),
            SurgeZone("Şişli", 12, "+%12 aktif", false, Offset(0.42f, 0.22f)),
            SurgeZone("Üsküdar", 8, "+%8 aktif", false, Offset(0.65f, 0.55f)),
            SurgeZone("Bakırköy", 0, "Sakin", false, Offset(0.15f, 0.5f))
        )
    }

    var selectedZone by remember { mutableStateOf<SurgeZone?>(zones[0]) }

    // Pulsating indicator animation for map hotspots
    val infiniteTransition = rememberInfiniteTransition(label = "hotspot")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    val pulseRadiusScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // TOP GREETING BAR
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Günaydın,",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = profile.displayName.split(" ").firstOrNull() ?: "Kurye",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PrimaryPurple.copy(alpha = 0.15f))
                                .border(0.5.dp, PrimaryPurple.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Lv.${profile.level}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryPurple
                            )
                        }
                    }
                }
                
                // Today Stats Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceDark)
                        .border(0.5.dp, GlassBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.OfflineBolt,
                            contentDescription = "XP",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+240 XP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "bugün",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // MAP CONTAINER - CANVAS HIGH-TECH GRID
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, GlassBorder),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Map background glow elements
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.Center)
                            .blur(30.dp)
                            .alpha(0.12f)
                            .background(PrimaryPurple, CircleShape)
                    )

                    // Draw Istanbul-like abstract delivery map lines
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        
                        // Drawing abstract waterways (Bosphorus bridge concept)
                        val riverPath = androidx.compose.ui.graphics.Path().apply {
                            moveTo(w * 0.4f, 0f)
                            cubicTo(w * 0.42f, h * 0.35f, w * 0.58f, h * 0.65f, w * 0.6f, h)
                        }
                        drawPath(
                            path = riverPath,
                            color = Color(0xFF1E293B),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 24.dp.toPx())
                        )

                        // Bridge lines
                        drawLine(
                            color = PrimaryPurple.copy(alpha = 0.5f),
                            start = Offset(w * 0.43f, h * 0.4f),
                            end = Offset(w * 0.57f, h * 0.55f),
                            strokeWidth = 2.dp.toPx()
                        )

                        // Grid gridlines for high-tech simulator overlay
                        val columns = 8
                        val rows = 5
                        for (i in 1 until columns) {
                            drawLine(
                                color = GlassBorder.copy(alpha = 0.05f),
                                start = Offset(w * (i.toFloat() / columns), 0f),
                                end = Offset(w * (i.toFloat() / columns), h),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                        for (i in 1 until rows) {
                            drawLine(
                                color = GlassBorder.copy(alpha = 0.05f),
                                start = Offset(0f, h * (i.toFloat() / rows)),
                                end = Offset(w, h * (i.toFloat() / rows)),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Hotspots nodes
                        zones.forEach { zone ->
                            val pos = Offset(w * zone.coordinates.x, h * zone.coordinates.y)
                            val themeColor = if (zone.surgeRate > 0) {
                                if (zone.isHighSurge) PrimaryPurple else PrimaryGreen
                            } else TextTertiary
                            
                            // Dynamic pulsing glow ring
                            if (zone.surgeRate > 0) {
                                drawCircle(
                                    color = themeColor.copy(alpha = 0.15f * (1f - (pulseRadiusScale - 1f) / 1.2f)),
                                    radius = 16.dp.toPx() * pulseRadiusScale,
                                    center = pos
                                )
                            }

                            // Inner dot
                            drawCircle(
                                color = themeColor,
                                radius = 6.dp.toPx(),
                                center = pos
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 2.5.dp.toPx(),
                                center = pos
                            )
                        }
                    }

                    // Floating Copilot Overlay Tag
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(GlassBg)
                            .border(0.5.dp, GlassBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(PrimaryGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Surge Modu: Aktif", fontSize = 10.sp, color = TextPrimary)
                        }
                    }

                    // Zone marker labels
                    zones.forEach { zone ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(
                                    x = (200 * zone.coordinates.x).dp,
                                    y = (180 * zone.coordinates.y - 12).dp
                                )
                                .clip(RoundedCornerShape(4.dp))
                                .background(SurfaceDark.copy(alpha = 0.85f))
                                .border(0.5.dp, GlassBorder, RoundedCornerShape(4.dp))
                                .clickable { selectedZone = zone }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${zone.name} ${if(zone.surgeRate > 0) "+%" + zone.surgeRate else ""}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (zone.isHighSurge) PrimaryPurple else if (zone.surgeRate > 0) PrimaryGreen else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // WEATHER AI FORECAST BANNER
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(0.5.dp, PrimaryPurple.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryPurple.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Cloud,
                            contentDescription = "Yağmur",
                            tint = PrimaryPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Ekonomi Öngörüsü",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryPurple,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "18 dakika sonra Kadıköy'de yağmur başlıyor sipariş oranlarında %35 artış öngörülüyor.",
                            fontSize = 12.sp,
                            color = TextPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // HEADING: ACTIVE REGIONS
        item {
            Text(
                text = "Surge Bölgeleri ve Kazanç Çarpanları",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(top = 16.dp, bottom = 10.dp)
            )
        }

        // LIST OF REGIONS (Interactive card elements)
        items(zones) { zone ->
            val isSelected = selectedZone?.name == zone.name
            val borderGlow = if (isSelected) {
                BorderStroke(1.dp, if (zone.isHighSurge) PrimaryPurple else PrimaryGreen)
            } else {
                BorderStroke(0.5.dp, GlassBorder)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { selectedZone = zone },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) SurfaceVariantDark else SurfaceDark
                ),
                border = borderGlow
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (zone.isHighSurge) PrimaryPurple 
                                    else if (zone.surgeRate > 0) PrimaryGreen 
                                    else TextTertiary
                                )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = zone.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = zone.status,
                                fontSize = 12.sp,
                                color = if (zone.surgeRate > 0) PrimaryGreen else TextSecondary
                            )
                        }
                    }

                    if (zone.surgeRate > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (zone.isHighSurge) PrimaryPurple.copy(alpha = 0.15f) 
                                    else PrimaryGreen.copy(alpha = 0.15f)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "+%${zone.surgeRate}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (zone.isHighSurge) PrimaryPurple else PrimaryGreen
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = "Go",
                            tint = TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
