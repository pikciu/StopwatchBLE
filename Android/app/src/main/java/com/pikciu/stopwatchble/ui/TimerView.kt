package com.pikciu.stopwatchble.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.pikciu.stopwatchble.ui.theme.StopwatchBLETheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun TimerView(viewModel: TimerViewModel) {
    val time by viewModel.time.collectAsState()
    var textSize by remember { mutableStateOf(180.sp) }

    Box(contentAlignment = Alignment.Center) {
        Text(
            text = time.text,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Monospace,
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StopwatchBLETheme {
        TimerView(TimerViewModel())
    }
}