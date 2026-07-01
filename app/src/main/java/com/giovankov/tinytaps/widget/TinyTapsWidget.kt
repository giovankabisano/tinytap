package com.giovankov.tinytaps.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.giovankov.tinytaps.MainActivity
import com.giovankov.tinytaps.util.TimeFormatter

class TinyTapsWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val state = context.getWidgetState()

        provideContent {
            WidgetContent(state = state)
        }
    }

    @Composable
    private fun WidgetContent(state: WidgetDisplayState) {
        val coralColor = Color(0xFFE8614D)
        val creamBg = Color(0xFFFFF8F4)
        val darkText = Color(0xFF2D1F1A)
        val lightText = Color(0xFF8A7A72)

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(creamBg)
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Header
            Text(
                text = "Tiny Taps",
                style = TextStyle(
                    color = androidx.glance.color.ColorProvider(coralColor),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            if (state.isRecording) {
                // Recording state
                Text(
                    text = "Sedang merekam gerakan...",
                    style = TextStyle(
                        color = androidx.glance.color.ColorProvider(darkText),
                        fontSize = 13.sp
                    )
                )

                Spacer(modifier = GlanceModifier.height(4.dp))

                val elapsed = System.currentTimeMillis() - state.recordingStartTime
                Text(
                    text = TimeFormatter.formatTimer(elapsed),
                    style = TextStyle(
                        color = androidx.glance.color.ColorProvider(coralColor),
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                )

                Spacer(modifier = GlanceModifier.height(12.dp))

                // Stop button
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(coralColor)
                        .clickable(actionRunCallback<WidgetStopRecordingCallback>()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Bayi berhenti gerak",
                        style = TextStyle(
                            color = androidx.glance.color.ColorProvider(Color.White),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    )
                }
            } else {
                // Idle state
                Text(
                    text = "Terakhir bergerak",
                    style = TextStyle(
                        color = androidx.glance.color.ColorProvider(lightText),
                        fontSize = 12.sp
                    )
                )

                Spacer(modifier = GlanceModifier.height(2.dp))

                val relativeTime = state.lastMovementTime?.let {
                    TimeFormatter.formatRelativeTime(it)
                } ?: "Belum ada catatan"

                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = relativeTime,
                        style = TextStyle(
                            color = androidx.glance.color.ColorProvider(darkText),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                    state.lastMovementTime?.let {
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        Text(
                            text = TimeFormatter.formatTime(it),
                            style = TextStyle(
                                color = androidx.glance.color.ColorProvider(lightText),
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(12.dp))

                // Start button
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(coralColor)
                        .clickable(actionRunCallback<WidgetStartRecordingCallback>()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Bayi mulai gerak",
                        style = TextStyle(
                            color = androidx.glance.color.ColorProvider(Color.White),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                // Kick count shortcut
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .clickable(actionStartActivity<MainActivity>()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⏱ Hitung Tendangan",
                        style = TextStyle(
                            color = androidx.glance.color.ColorProvider(coralColor),
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}
