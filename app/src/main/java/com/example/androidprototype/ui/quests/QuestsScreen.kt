package com.example.androidprototype.ui.quests

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidprototype.ui.profile.ProfileViewModel
import com.example.androidprototype.ui.theme.*

data class DeliveryQuest(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val target: Int,
    var current: Int,
    val xpReward: Int,
    val themeColor: Color,
    var isClaimed: Boolean = false
)

@Composable
fun QuestsScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    // Shared state from viewModel to increment XP when quests are claimed!
    var questsState = remember {
        mutableStateListOf(
            DeliveryQuest(
                "q1",
                "Günün Kahramanı",
                "Bugün toplamda 5 adet başarılı sipariş teslim et.",
                Icons.Rounded.Motorcycle,
                5,
                3,
                150,
                PrimaryPurple
            ),
            DeliveryQuest(
                "q2",
                "Yağmur Fatihi",
                "Yağmurlu hava dalgasında Kadıköy bölgesinde 2 sipariş tamamla.",
                Icons.Rounded.Cloud,
                2,
                2,
                200,
                PrimaryGreen
            ),
            DeliveryQuest(
                "q3",
                "Gece Ekspresi",
                "Gece 22:00 - 02:00 saatleri arasında 3 sipariş teslim et.",
                Icons.Rounded.Bedtime,
                3,
                1,
                100,
                Color(0xFFFFD600)
            )
        )
    }

    var showClaimToast by remember { mutableStateOf(false) }
    var lastClaimedXp by remember { mutableStateOf(0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // HEADER TITLE
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    text = "Dinamik Günlük Görevler",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Görevleri tamamla, anında XP ve seviye puanı kazan.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // SUMMARY METRICS BAR
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(0.5.dp, GlassBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TAMAMLANAN", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${questsState.count { it.current >= it.target }}/${questsState.size}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(GlassBorder)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TOPLAM ÖDÜL", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Text(
                            text = "+${questsState.sumOf { it.xpReward }} XP",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryPurple
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
        }

        // QUEST CARDS
        itemsIndexed(questsState) { index, quest ->
            val progress = quest.current.toFloat() / quest.target.toFloat()
            val isCompleted = quest.current >= quest.target
            
            val animatedProgress by animateFloatAsState(
                targetValue = progress.coerceIn(0f, 1f),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "QuestProgress"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (quest.isClaimed) SurfaceDark.copy(alpha = 0.5f) else SurfaceDark
                ),
                border = BorderStroke(
                    0.5.dp, 
                    if (isCompleted && !quest.isClaimed) quest.themeColor else GlassBorder
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(quest.themeColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = quest.icon,
                                    contentDescription = quest.title,
                                    tint = quest.themeColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = quest.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (quest.isClaimed) TextSecondary else TextPrimary
                                )
                                Text(
                                    text = "+${quest.xpReward} XP",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = quest.themeColor
                                )
                            }
                        }

                        // Simulation button to increment progress
                        if (!isCompleted) {
                            IconButton(
                                onClick = {
                                    if (quest.current < quest.target) {
                                        val updated = quest.copy(current = quest.current + 1)
                                        questsState[index] = updated
                                    }
                                },
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(GlassBg)
                                    .border(0.5.dp, GlassBorder, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = "Simulate delivery",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text(
                        text = quest.description,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Metric Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Custom styled linear indicator
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(ProgressTrackColor)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(animatedProgress)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(quest.themeColor)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = "${quest.current}/${quest.target}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) quest.themeColor else TextSecondary
                        )
                    }

                    // Claim Reward Button
                    if (isCompleted) {
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Button(
                            onClick = {
                                if (!quest.isClaimed) {
                                    questsState[index] = quest.copy(isClaimed = true)
                                    viewModel.addXp(quest.xpReward)
                                    lastClaimedXp = quest.xpReward
                                    showClaimToast = true
                                }
                            },
                            enabled = !quest.isClaimed,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (quest.isClaimed) GlassBg else quest.themeColor,
                                contentColor = if (quest.isClaimed) TextSecondary else Color.Black,
                                disabledContainerColor = GlassBg,
                                disabledContentColor = TextTertiary
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (quest.isClaimed) Icons.Rounded.CheckCircle else Icons.Rounded.EmojiEvents,
                                    contentDescription = "Kazan",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (quest.isClaimed) "ÖDÜL ALINDI" else "ÖDÜLÜ AL (+${quest.xpReward} XP)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Interactive Floating Toast Notification for Quest Claiming
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = showClaimToast,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it }
            ) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 96.dp, start = 16.dp, end = 16.dp)
        ) {
            LaunchedEffect(showClaimToast) {
                if (showClaimToast) {
                    kotlinx.coroutines.delay(2500)
                    showClaimToast = false
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Success",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Görev Ödülü Alındı! Cüzdanınıza +$lastClaimedXp XP eklendi.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
