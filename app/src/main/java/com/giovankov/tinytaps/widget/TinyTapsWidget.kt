package com.giovankov.tinytaps.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.giovankov.tinytaps.util.TimeFormatter

class TinyTapsWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val state = context.applicationContext.getWidgetState()

        provideContent {
            WidgetContent(state = state)
        }
    }

    @Composable
    private fun WidgetContent(state: WidgetDisplayState) {
        val coralColor = Color(0xFFE8614D)
        val creamBg = Color(0xFFFFF8F4)
        val darkBg = Color(0xFF1A1210)
        val darkText = Color(0xFF2D1F1A)
        val lightText = Color(0xFF8A7A72)
        val lightOnDark = Color(0xFFF5E8E0)
        val mutedOnDark = Color(0xFF9A8A82)

        val coralProvider = ColorProvider(coralColor, coralColor)
        val darkTextProvider = ColorProvider(darkText, lightOnDark)
        val lightTextProvider = ColorProvider(lightText, mutedOnDark)
        val whiteProvider = ColorProvider(Color.White, Color.White)

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(creamBg, darkBg))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.Start
        ) {
            if (state.isRecording) {
                // Recording state
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = GlanceModifier.defaultWeight()) {
                        Text(
                            text = "Sedang merekam...",
                            style = TextStyle(
                                color = darkTextProvider,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = "Mulai ${TimeFormatter.formatTime(state.recordingStartTime)}",
                            style = TextStyle(
                                color = lightTextProvider,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Spacer(modifier = GlanceModifier.width(8.dp))

                    Box(
                        modifier = GlanceModifier
                            .height(36.dp)
                            .width(130.dp)
                            .background(coralColor)
                            .clickable(actionRunCallback<WidgetStopRecordingCallback>()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Berhenti",
                            style = TextStyle(
                                color = whiteProvider,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            } else {
                // Idle state
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = GlanceModifier.defaultWeight()) {
                        val relativeTime = state.lastMovementTime?.let {
                            TimeFormatter.formatRelativeTime(it)
                        } ?: "Belum ada catatan"

                        Text(
                            text = relativeTime,
                            style = TextStyle(
                                color = darkTextProvider,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = state.lastMovementTime?.let {
                                "Terakhir ${TimeFormatter.formatTime(it)}"
                            } ?: "Tiny Taps",
                            style = TextStyle(
                                color = lightTextProvider,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Spacer(modifier = GlanceModifier.width(8.dp))

                    Box(
                        modifier = GlanceModifier
                            .height(36.dp)
                            .width(130.dp)
                            .background(coralColor)
                            .clickable(actionRunCallback<WidgetStartRecordingCallback>()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Mulai gerak",
                            style = TextStyle(
                                color = whiteProvider,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        )
                    }
                }


            }
        }
    }
}
