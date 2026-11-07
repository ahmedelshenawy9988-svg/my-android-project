package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SystemLimitsDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .testTag("system_limits_dialog"),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "حدود وضمانات نظام أندرويد",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GoldLight
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "شفافية وتوضيح تقني مهم للمستخدم:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "1. آلية عمل المنبه القوي:\n" +
                            "يستخدم التطبيق أعلى آليات أندرويد الرسمية (AlarmClockInfo و FullScreenIntent)، مما يضمن تشغيل الشاشة وإصدار الرنين حتى في وضع توفير الطاقة (Doze Mode) والشاشة مغلقة، ويمنع إيقافه من شاشة الرنين إلا بحل المسائل الخمس.\n\n" +
                            "2. الإيقاف الإجباري (Force Stop):\n" +
                            "في نظام أندرويد، يُعد خيار 'الإيقاف الإجباري' من شاشة إعدادات النظام ميزة أمان أساسية للنظام لحماية خصوصية المستخدمين ومنع البرمجيات الخبيثة. لا يمكن لأي تطبيق أندرويد غير مروّت (Non-Root) منع المستخدم من فتح إعدادات النظام وعمل Force Stop للتطبيق.\n\n" +
                            "3. إعادة تشغيل الهاتف:\n" +
                            "يتضمن التطبيق مستقبل إشارة إعادة التشغيل (BootReceiver) لإعادة جدولة جميع المنبهات المفعلة تلقائيًا بمجرد إقلاع الهاتف.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = BackgroundDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "فهمت ذلك",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
