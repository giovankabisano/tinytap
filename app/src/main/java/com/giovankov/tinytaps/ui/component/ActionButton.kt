package com.giovankov.tinytaps.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    ) {
        icon?.let {
            Icon(imageVector = it, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun SecondaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    ) {
        icon?.let {
            Icon(imageVector = it, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}
