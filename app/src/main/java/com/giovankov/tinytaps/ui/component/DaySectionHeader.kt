package com.giovankov.tinytaps.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.giovankov.tinytaps.util.DateUtils
import com.giovankov.tinytaps.util.TimeFormatter
import java.time.LocalDate

@Composable
fun DaySectionHeader(
    date: LocalDate,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    val label = when (date) {
        today -> "Hari ini"
        yesterday -> "Kemarin"
        else -> TimeFormatter.formatDayDate(DateUtils.startOfDay(date))
    }

    Text(
        text = label,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
