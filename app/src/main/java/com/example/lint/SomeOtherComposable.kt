package com.example.lint

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
fun SomeOtherComposable() {
  LaunchedEffect(Unit) {
    runAThing()
  }
}

private suspend fun runAThing() {
  delay(1000)
}