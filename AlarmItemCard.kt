package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AlarmEntity
import com.example.math.DifficultyLevel
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.SurfaceDarkBorder
import com.example.ui.theme.SurfaceDarkElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Calendar

val DAY_LABELS = listOf(
    Calendar.SATURDAY to "السبت",
    Calendar.SUNDAY to "الأحد",
    Calendar.MONDAY to "الإثنين",
    Calendar.TUESDAY to "الثلاثاء",
    Calendar.WEDNESDAY to "الأربعاء",
    Calendar.THURSDAY to "الخميس",
    Calendar.FRIDAY to "الجمعة"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlarmItemCard(
    alarm: AlarmEntity,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeDays = alarm.getDaysList()
    val difficulty = DifficultyLevel.fromKey(alarm.difficulty)

    val borderColor by animateColorAsState(
        targetValue = if (alarm.isEnabled) GoldPrimary.copy(alpha = 0.4f) else SurfaceDarkBorder,
        label = "alarm_card_border"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("alarm_card_${alarm.id}"),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceDarkElevated
        ),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderColor))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Top Row: Time, Label, and Toggle Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = alarm.getFormattedTime(),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = if (alarm.isEnabled) GoldLight else TextMuted,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = alarm.label.ifBlank { "منبه الاستيقاظ" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (alarm.isEnabled) TextSecondary else TextMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("delete_alarm_${alarm.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "حذف المنبه",
                            tint = ErrorRed.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Switch(
                        checked = alarm.isEnabled,
                        onCheckedChange = onToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BackgroundDark,
                            checkedTrackColor = GoldPrimary,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = SurfaceDarkBorder
                        ),
                        modifier = Modifier.testTag("toggle_alarm_${alarm.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Days of the week row
            if (alarm.isRepeating()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for ((calDay, dayName) in DAY_LABELS) {
                        val isDayActive = activeDays.contains(calDay)
                        DayPill(
                            name = dayName,
                            isActive = isDayActive && alarm.isEnabled
                        )
                    }
                }
            } else {
                Text(
                    text = "رنين لمرة واحدة فقط",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (alarm.isEnabled) OrangeSecondary else TextMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Badges: Difficulty & Math challenges note
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Difficulty Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoldDark.copy(alpha = 0.15f))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "مستوى التحدي",
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "تحدي: ${difficulty.labelArabic} (5 مسائل)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                }

                if (alarm.vibrate) {
                    Text(
                        text = "اهتزاز مفعل",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun DayPill(
    name: String,
    isActive: Boolean
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isActive) GoldPrimary.copy(alpha = 0.2f) else Color.Transparent)
            .border(
                1.dp,
                if (isActive) GoldPrimary else SurfaceDarkBorder,
                CircleShape
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) GoldLight else TextMuted,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}
