package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AlarmEntity
import com.example.math.DifficultyLevel
import com.example.scheduler.AlarmScheduler
import com.example.ui.AlarmRingingActivity
import com.example.ui.MainViewModel
import com.example.ui.components.AddEditAlarmDialog
import com.example.ui.components.AlarmItemCard
import com.example.ui.components.ApkInstructionsDialog
import com.example.ui.components.PermissionsCard
import com.example.ui.components.SystemLimitsDialog
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkBorder
import com.example.ui.theme.SurfaceDarkElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    MainScreen(
                        viewModel = viewModel,
                        onOpenTestChallenge = { difficulty ->
                            val intent = Intent(this, AlarmRingingActivity::class.java).apply {
                                putExtra(AlarmRingingActivity.EXTRA_ALARM_LABEL, "تجربة تحدي الحساب")
                                putExtra(AlarmRingingActivity.EXTRA_DIFFICULTY, difficulty)
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onOpenTestChallenge: (String) -> Unit
) {
    val context = LocalContext.current
    val alarms by viewModel.alarms.collectAsStateWithLifecycle()
    val nextAlarmInfo by viewModel.nextAlarmInfo.collectAsStateWithLifecycle()
    val isTestingSound by viewModel.isTestingSound.collectAsStateWithLifecycle()

    var editingAlarm by remember { mutableStateOf<AlarmEntity?>(null) }
    var isAddingAlarm by remember { mutableStateOf(false) }
    var showSystemLimits by remember { mutableStateOf(false) }
    var showApkGuide by remember { mutableStateOf(false) }

    // Permission tracking
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }

    val hasExactAlarmPermission = remember {
        AlarmScheduler.canScheduleExactAlarms(context)
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary.copy(alpha = 0.2f))
                                .border(1.dp, GoldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = "شعار المنبه",
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "WAKE UP",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = GoldLight
                            )
                            Text(
                                text = "المنبه الذكي الخارق",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showSystemLimits = true },
                        modifier = Modifier.testTag("system_limits_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "حدود وضمانات أندرويد",
                            tint = GoldLight
                        )
                    }

                    IconButton(
                        onClick = { showApkGuide = true },
                        modifier = Modifier.testTag("apk_guide_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "دليل تثبيت APK",
                            tint = OrangeSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = TextPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddingAlarm = true },
                containerColor = GoldPrimary,
                contentColor = BackgroundDark,
                shape = CircleShape,
                modifier = Modifier
                    .size(64.dp)
                    .testTag("add_alarm_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "إضافة منبه جديد",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Next Upcoming Alarm Hero Card
            item {
                NextAlarmHeroCard(
                    nextAlarm = nextAlarmInfo.first,
                    remainingText = nextAlarmInfo.second
                )
            }

            // Quick Actions: Test Alarm Sound & Try Math Challenge
            item {
                QuickActionsRow(
                    isTestingSound = isTestingSound,
                    onToggleTestSound = { viewModel.toggleTestSound() },
                    onTryChallenge = { onOpenTestChallenge("MEDIUM") }
                )
            }

            // Permissions Check Card
            item {
                PermissionsCard(
                    hasExactAlarmPermission = hasExactAlarmPermission,
                    hasNotificationPermission = hasNotificationPermission,
                    onRequestNotificationPermission = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                )
            }

            // Alarms Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "منبهاتي (${alarms.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = "يتطلب حل 5 مسائل لإيقافه",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldLight
                    )
                }
            }

            // Empty state
            if (alarms.isEmpty()) {
                item {
                    EmptyAlarmsState(onAddClick = { isAddingAlarm = true })
                }
            } else {
                items(alarms, key = { it.id }) { alarm ->
                    AlarmItemCard(
                        alarm = alarm,
                        onToggle = { isEnabled -> viewModel.toggleAlarm(alarm, isEnabled) },
                        onClick = { editingAlarm = alarm },
                        onDelete = { viewModel.deleteAlarm(alarm) }
                    )
                }
            }

            // Bottom spacing for FAB
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    // Add Alarm Dialog
    if (isAddingAlarm) {
        AddEditAlarmDialog(
            initialAlarm = null,
            onDismiss = { isAddingAlarm = false },
            onSave = { newAlarm ->
                viewModel.saveAlarm(newAlarm)
                isAddingAlarm = false
            }
        )
    }

    // Edit Alarm Dialog
    editingAlarm?.let { alarm ->
        AddEditAlarmDialog(
            initialAlarm = alarm,
            onDismiss = { editingAlarm = null },
            onSave = { updated ->
                viewModel.saveAlarm(updated)
                editingAlarm = null
            }
        )
    }

    // System Limits Dialog
    if (showSystemLimits) {
        SystemLimitsDialog(onDismiss = { showSystemLimits = false })
    }

    // APK Guide Dialog
    if (showApkGuide) {
        ApkInstructionsDialog(onDismiss = { showApkGuide = false })
    }
}

@Composable
fun NextAlarmHeroCard(
    nextAlarm: AlarmEntity?,
    remainingText: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(
                1.dp,
                Brush.horizontalGradient(listOf(GoldPrimary.copy(alpha = 0.5f), OrangeSecondary.copy(alpha = 0.3f))),
                RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(SurfaceDarkBorder.copy(alpha = 0.5f), SurfaceDarkElevated),
                        radius = 800f
                    )
                )
                .padding(20.dp)
        ) {
            if (nextAlarm != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AlarmOn,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "المنبه القادم",
                                style = MaterialTheme.typography.labelMedium,
                                color = GoldLight,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = nextAlarm.getFormattedTime(),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "سيرن $remainingText (${nextAlarm.label.ifBlank { "المنبه" }})",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.15f))
                            .border(1.dp, GoldPrimary.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "لا يوجد منبه نشط حالياً",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "اضغط على زر (+) في الأسفل لضبط منبه ذكي بمهمات حسابية",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionsRow(
    isTestingSound: Boolean,
    onToggleTestSound: () -> Unit,
    onTryChallenge: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sound_pulse")
    val soundPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Test Alarm Sound Card (Requirement 10)
        Card(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onToggleTestSound)
                .testTag("test_alarm_sound_button"),
            colors = CardDefaults.cardColors(
                containerColor = if (isTestingSound) ErrorRed.copy(alpha = 0.2f) else SurfaceDarkElevated
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(
                    if (isTestingSound) ErrorRed else SurfaceDarkBorder
                )
            ),
            shape = RoundedCornerShape(16.dp)
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
                        .scale(if (isTestingSound) soundPulse else 1f)
                        .clip(CircleShape)
                        .background(if (isTestingSound) ErrorRed else GoldPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isTestingSound) Icons.Default.Stop else Icons.Default.VolumeUp,
                        contentDescription = "اختبار الصوت",
                        tint = if (isTestingSound) Color.White else GoldPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = if (isTestingSound) "إيقاف الصوت" else "اختبار الصوت",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isTestingSound) ErrorRed else TextPrimary
                    )
                    Text(
                        text = if (isTestingSound) "يرن الآن..." else "جرّب الرنين",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Try Math Challenge Card
        Card(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onTryChallenge)
                .testTag("try_math_challenge_button"),
            colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(SurfaceDarkBorder)
            ),
            shape = RoundedCornerShape(16.dp)
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
                        .background(OrangeSecondary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "تجربة المسائل",
                        tint = OrangeSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "تجربة التحدي",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "حل 5 مسائل",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyAlarmsState(onAddClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(SurfaceDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "لا توجد منبهات مضافة",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "أضف منبهك الأول الآن وابدأ يومك بنشاط وتركيز كامل بعد حل المسائل الحسابية.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}
