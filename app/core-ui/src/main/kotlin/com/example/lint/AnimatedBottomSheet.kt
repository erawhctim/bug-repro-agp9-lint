package com.example.lint

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
fun AnimatedBottomSheet(
  modifier: Modifier = Modifier,
  initVisible: Boolean = false,
  entranceDelay: Long = 500,
  animDuration: Int = 500,
  content: @Composable (() -> Unit) -> Unit = {},
) {
  var isVisible by remember { mutableStateOf(initVisible) }

  // Trigger animation enter
  LaunchedEffect(Unit) {
    delay(entranceDelay)
    isVisible = true
  }
}
