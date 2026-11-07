package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AlarmEntity
import com.example.math.DifficultyLevel
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkBorder
import com.example.ui.theme.SurfaceDarkElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditAlarmDialog(
    initialAlarm: AlarmEntity? = null,
    onDismiss: () -> Unit,
    onSave: (AlarmEntity) -> Unit
) {
    // Current time or alarm time
    val defaultCal = Calendar.getInstance()
    var selectedHour24 by remember {
        mutableIntStateOf(initialAlarm?.hour ?: defaultCal.get(Calendar.HOUR_OF_DAY))
    }
    var selectedMinute by remember {
        mutableIntStateOf(initialAlarm?.minute ?: defaultCal.get(Calendar.MINUTE))
    }

    // Convert to 12h for friendly display
    var isPm by remember { mutableStateOf(selectedHour24 >= 12) }
    var displayHour12 by remember {
        mutableIntStateOf(
            if (selectedHour24 == 0) 12
            else if (selectedHour24 > 12) selectedHour24 - 12
            else selectedHour24
        )
    }

    var label by remember { mutableStateOf(initialAlarm?.label ?: "استيقاظ الصباح") }
    var difficulty by remember {
        mutableStateOf(
            DifficultyLevel.fromKey(initialAlarm?.difficulty ?: "MEDIUM")
        )
    }
    var vibrate by remember { mutableStateOf(initialAlarm?.vibrate ?: true) }

    // Selected repeat days set
    val initialDays = remember { initialAlarm?.getDaysList()?.toSet() ?: emptySet() }
    var selectedDays by remember { mutableStateOf(initialDays) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (initialAlarm == null) "إضافة منبه جديد" else "تعديل المنبه",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GoldLight
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Time Picker Controls (Luxury Digital Stepper)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated),
                    shape = RoundedCornerShape(18.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SurfaceDarkBorder))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "وقت المنبه",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Hour controls
                            TimeColumn(
                                value = displayHour12,
                                onIncrement = {
                                    displayHour12 = if (displayHour12 >= 12) 1 else displayHour12 + 1
                                },
                                onDecrement = {
                                    displayHour12 = if (displayHour12 <= 1) 12 else displayHour12 - 1
                                },
                                label = "الساعة"
                            )

                            Text(
                                text = ":",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )

                            // Minute controls
                            TimeColumn(
                                value = selectedMinute,
                                onIncrement = {
                                    selectedMinute = (selectedMinute + 1) % 60
                                },
                                onDecrement = {
                                    selectedMinute = if (selectedMinute == 0) 59 else selectedMinute - 1
                                },
                                label = "الدقيقة",
                                formatTwoDigits = true
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            // AM/PM Switcher
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                AmPmChip(
                                    text = "ص",
                                    isSelected = !isPm,
                                    onClick = { isPm = false }
                                )
                                AmPmChip(
                                    text = "م",
                                    isSelected = isPm,
                                    onClick = { isPm = true }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Alarm Label Input
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("تسمية المنبه") },
                    placeholder = { Text("مثال: صلاة الفجر، العمل...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("alarm_label_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = SurfaceDarkBorder,
                        focusedLabelColor = GoldLight,
                        unfocusedLabelColor = TextSecondary,
                        cursorColor = GoldPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Repeating Days of Week
                Text(
                    text = "أيام التكرار (اتركه فارغًا لرنين لمرة واحدة):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for ((calDay, dayName) in DAY_LABELS) {
                        val isSelected = selectedDays.contains(calDay)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) GoldPrimary else SurfaceDarkElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) GoldDark else SurfaceDarkBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    selectedDays = if (isSelected) {
                                        selectedDays - calDay
                                    } else {
                                        selectedDays + calDay
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                                .testTag("day_chip_$calDay"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) BackgroundDark else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Math Challenge Difficulty Selection
                Text(
                    text = "مستوى صعوبة المسائل الحسابية (5 مسائل):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (level in DifficultyLevel.entries) {
                        val isChosen = difficulty == level
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isChosen) GoldPrimary.copy(alpha = 0.2f) else SurfaceDarkElevated)
                                .border(
                                    1.dp,
                                    if (isChosen) GoldPrimary else SurfaceDarkBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { difficulty = level }
                                .padding(vertical = 10.dp, horizontal = 4.dp)
                                .testTag("difficulty_${level.key}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = level.labelArabic,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isChosen) GoldLight else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = difficulty.descriptionArabic,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary(difficulty)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Vibration Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "تشغيل الاهتزاز مع الرنين",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    Switch(
                        checked = vibrate,
                        onCheckedChange = { vibrate = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BackgroundDark,
                            checkedTrackColor = GoldPrimary,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = SurfaceDarkBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "إلغاء",
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            // Compute 24h
                            val calculated24 = when {
                                isPm && displayHour12 < 12 -> displayHour12 + 12
                                !isPm && displayHour12 == 12 -> 0
                                else -> displayHour12
                            }

                            val daysString = selectedDays.sorted().joinToString(",")

                            val alarmToSave = initialAlarm?.copy(
                                hour = calculated24,
                                minute = selectedMinute,
                                label = label.ifBlank { "المنبه" },
                                daysOfWeek = daysString,
                                difficulty = difficulty.key,
                                vibrate = vibrate,
                                isEnabled = true
                            ) ?: AlarmEntity(
                                hour = calculated24,
                                minute = selectedMinute,
                                label = label.ifBlank { "المنبه" },
                                daysOfWeek = daysString,
                                difficulty = difficulty.key,
                                vibrate = vibrate,
                                isEnabled = true
                            )

                            onSave(alarmToSave)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = BackgroundDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_alarm_button")
                    ) {
                        Text(
                            text = "حفظ المنبه",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TextTertiary(difficulty: DifficultyLevel): Color {
    return when (difficulty) {
        DifficultyLevel.EASY -> TextSecondary
        DifficultyLevel.MEDIUM -> GoldLight.copy(alpha = 0.8f)
        DifficultyLevel.HARD -> OrangeSecondary
    }
}

@Composable
fun TimeColumn(
    value: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    label: String,
    formatTwoDigits: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onIncrement,
            modifier = Modifier.clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "زيادة $label",
                tint = GoldLight
            )
        }

        val text = if (formatTwoDigits) String.format("%02d", value) else value.toString()
        Text(
            text = text,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )

        IconButton(
            onClick = onDecrement,
            modifier = Modifier.clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "إنقاص $label",
                tint = GoldLight
            )
        }
    }
}

@Composable
fun AmPmChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) GoldPrimary else SurfaceDark)
            .border(
                1.dp,
                if (isSelected) GoldDark else SurfaceDarkBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) BackgroundDark else TextSecondary
        )
    }
}
