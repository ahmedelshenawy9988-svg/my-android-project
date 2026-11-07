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
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ApkInstructionsDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .testTag("apk_instructions_dialog"),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "دليل تثبيت ملف APK على هاتفك",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GoldLight
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "خطوات تثبيت وتشغيل WAKE UP على هاتفك الأندرويد:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "1. تحميل حزمة التطبيق (APK):\n" +
                            "• من خلال قائمة الإعدادات في منصة AI Studio، اضغط على زر 'Download APK' أو 'Export Project'.\n" +
                            "• أو يمكنك بناء الـ APK عبر الأمر: gradle assembleRelease أو gradle assembleDebug وسيتوفر الملف في المجلد:\n" +
                            "app/build/outputs/apk/release/app-release.apk\n\n" +
                            "2. نقل الملف إلى هاتفك:\n" +
                            "• انقل ملف الـ APK إلى هاتفك عبر كابل USB أو البلوتوث أو إرساله لنفسك عبر تيليجرام / جوجل درايف.\n\n" +
                            "3. تثبيت التطبيق:\n" +
                            "• افتح مدير الملفات على هاتفك واضغط على ملف APK.\n" +
                            "• في حال ظهور رسالة 'تثبيت تطبيقات غير معروفة' (Install Unknown Apps)، اختر 'السماح من هذا المصدر'.\n\n" +
                            "4. ضبط الصلاحيات لأفضل أداء:\n" +
                            "• عند فتح التطبيق لأول مرة، وافق على صلاحية الإشعارات والمنبهات الدقيقة.\n" +
                            "• يُنصح بضبط خيار تحسين البطارية للتطبيق على 'غير مقيد' (Unrestricted) من إعدادات البطارية في هاتفك لضمان عدم إيقاف الخدمة بواسطة واجهات الشركات المخصصة (مثل Xiaomi أو Samsung).",
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
                        text = "تم وفهمت الخطوات",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
