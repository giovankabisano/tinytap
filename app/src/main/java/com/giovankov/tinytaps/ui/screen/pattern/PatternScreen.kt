package com.giovankov.tinytaps.ui.screen.pattern

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.giovankov.tinytaps.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternScreen(
    uiState: PatternUiState,
    onRangeChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(title = { Text(stringResource(R.string.pattern_title)) })

        // Range selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = uiState.daysRange == 7,
                onClick = { onRangeChange(7) },
                label = { Text(stringResource(R.string.pattern_7_days)) }
            )
            FilterChip(
                selected = uiState.daysRange == 30,
                onClick = { onRangeChange(30) },
                label = { Text(stringResource(R.string.pattern_30_days)) }
            )
        }

        if (!uiState.hasData) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.pattern_no_data),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))

            // Hourly Heatmap
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.pattern_heatmap_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HourlyHeatmapChart(
                        hourCounts = uiState.heatmapData.hourCounts,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Daily Trend
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.pattern_trend_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    DailyTrendChart(
                        entries = uiState.trendData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HourlyHeatmapChart(
    hourCounts: Map<Int, Int>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val textMeasurer = rememberTextMeasurer()
    val maxCount = hourCounts.values.maxOrNull() ?: 1

    Canvas(modifier = modifier) {
        val cellWidth = size.width / 24f
        val labelHeight = 20f
        val chartHeight = size.height - labelHeight

        for (hour in 0..23) {
            val count = hourCounts[hour] ?: 0
            val intensity = if (maxCount > 0) count.toFloat() / maxCount else 0f
            val color = primaryColor.copy(alpha = 0.1f + (intensity * 0.9f))
            val barColor = if (count > 0) color else trackColor

            // Bar
            drawRect(
                color = barColor,
                topLeft = Offset(hour * cellWidth + 1f, chartHeight * (1f - intensity)),
                size = Size(cellWidth - 2f, chartHeight * intensity.coerceAtLeast(0.05f))
            )

            // Hour label (every 4 hours)
            if (hour % 4 == 0) {
                drawText(
                    textMeasurer = textMeasurer,
                    text = "${hour}",
                    topLeft = Offset(hour * cellWidth, chartHeight + 2f),
                    style = androidx.compose.ui.text.TextStyle(
                        color = textColor,
                        fontSize = androidx.compose.ui.unit.TextUnit(9f, androidx.compose.ui.unit.TextUnitType.Sp)
                    )
                )
            }
        }
    }
}

@Composable
private fun DailyTrendChart(
    entries: List<com.giovankov.tinytaps.domain.usecase.analytics.DailyTrendEntry>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    if (entries.isEmpty()) return

    val maxCount = entries.maxOf { it.episodeCount }.coerceAtLeast(1)

    Canvas(modifier = modifier) {
        val barWidth = size.width / entries.size
        val chartHeight = size.height

        entries.forEachIndexed { index, entry ->
            val heightRatio = entry.episodeCount.toFloat() / maxCount
            val barHeight = chartHeight * heightRatio

            drawRect(
                color = if (entry.episodeCount > 0) primaryColor else trackColor,
                topLeft = Offset(
                    x = index * barWidth + 2f,
                    y = chartHeight - barHeight
                ),
                size = Size(barWidth - 4f, barHeight.coerceAtLeast(2f))
            )
        }
    }
}
