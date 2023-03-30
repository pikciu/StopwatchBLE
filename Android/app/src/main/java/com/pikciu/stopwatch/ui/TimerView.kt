package com.pikciu.stopwatch.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimerView(viewModel: TimerViewModel) {
    val time by viewModel.time.collectAsState()
    var textSize by remember { mutableStateOf(180.sp) }

    Box(
        modifier = Modifier.padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time.text,
            textAlign = TextAlign.Center,
            style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum"),
            fontSize = textSize,
            maxLines = 1,
            fontWeight = FontWeight.SemiBold,
            softWrap = false,
            onTextLayout = {
                if (it.hasVisualOverflow) {
                    textSize *= 0.9
                }
            }
        )
    }
}