package com.example.androidprototype.ui.squad

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidprototype.ui.theme.*

data class SquadMember(
    val name: String,
    val rank: Int,
    val xpContributed: Int,
    val isOnline: Boolean,
    val avatarInitial: String
)

@Composable
fun SquadScreen() {
    val mySquadName = "Karaköy Arıları"
    val squadCoverage = 0.68f // 68% district coverage
    
    val members = remember {
        listOf(
            SquadMember("Mert Kaya", 1, 1420, true, "M"),
            SquadMember("Ahmet Y.", 2, 1150, true, "A"),
            SquadMember("Caner D.", 3, 980, false, "C"),
            SquadMember("Selin K.", 4, 870, true, "S"),
            SquadMember("Burak T.", 5, 620, false, "B")
        )
    }

    var chatMessageText by remember { mutableStateOf("") }
    val mockChatList = remember {
        mutableStateListOf(
            "Ahmet Y." to "Kadıköy rıhtımda yoğun sipariş var, surge oranı +%45 oldu!",
            "Selin K." to "Beşiktaş meydanındayım, yağmur başladı.",
            "Caner D." to "Bugün teslimat hedefini tamamladım, akşam squad liderliğini alacağız."
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // SQUAD MAIN HEADER
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
                        text = "Aktif Kurye Grubu",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = mySquadName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PrimaryPurple.copy(alpha = 0.15f))
                        .border(0.5.dp, PrimaryPurple.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Kademe: Altın I",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPurple
                    )
                }
            }
        }

        // AREA DOMINATION METRIC RING CARD
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(0.5.dp, GlassBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Domain Circle ring representation via Canvas
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Canvas(modifier = Modifier.size(72.dp)) {
                            drawCircle(
                                color = ProgressTrackColor,
                                style = Stroke(width = 6.dp.toPx())
                            )
                            drawArc(
                                color = PrimaryGreen,
                                startAngle = -90f,
                                sweepAngle = 360f * squadCoverage,
                                useCenter = false,
                                style = Stroke(width = 6.dp.toPx())
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%68",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                            Text(
                                text = "Bölge",
                                fontSize = 9.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bölge Hakimiyeti: Karaköy",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Karaköy ve çevresindeki paket teslimat payımızın dağılım oranı. Diğer gruplarla kıyasıya rekabettesiniz!",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // MEMBERS LEADERBOARD TITLE
        item {
            Text(
                text = "Grup Sıralaması ve Katkıları",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        // LIST OF SQUAD MEMBERS
        items(members) { member ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(0.5.dp, GlassBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Custom avatar icon with online dot
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (member.name == "Mert Kaya") PrimaryPurple 
                                        else SurfaceVariantDark
                                    )
                                    .border(1.dp, GlassBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = member.avatarInitial,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            if (member.isOnline) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryGreen)
                                        .border(1.5.dp, SurfaceDark, CircleShape)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = member.name + if(member.name == "Mert Kaya") " (Sen)" else "",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${member.xpContributed} XP Katkısı",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = "Rank",
                            tint = when(member.rank) {
                                1 -> Color(0xFFFFD600) // Gold
                                2 -> Color(0xFFC0C0C0) // Silver
                                3 -> Color(0xFFCD7F32) // Bronze
                                else -> TextTertiary
                            },
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "#${member.rank}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // SQUAD COLLABORATIVE MOCK CHAT
        item {
            Text(
                text = "Grup Taktik Kanalı",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(0.5.dp, GlassBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Chat messages scroller mock
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceVariantDark)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        mockChatList.forEach { chat ->
                            Column {
                                Text(
                                    text = chat.first,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if(chat.first == "Mert Kaya") PrimaryPurple else PrimaryGreen
                                )
                                Text(
                                    text = chat.second,
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Text Field input mock row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = chatMessageText,
                            onValueChange = { chatMessageText = it },
                            placeholder = { Text("Taktik mesajı yaz...", fontSize = 11.sp, color = TextTertiary) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(0.5.dp, GlassBorder, RoundedCornerShape(8.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = SurfaceVariantDark,
                                unfocusedContainerColor = SurfaceVariantDark,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (chatMessageText.isNotBlank()) {
                                    mockChatList.add("Mert Kaya" to chatMessageText)
                                    chatMessageText = ""
                                    // limit mock chat size
                                    if (mockChatList.size > 4) {
                                        mockChatList.removeAt(0)
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryGreen)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Send,
                                contentDescription = "Gönder",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
