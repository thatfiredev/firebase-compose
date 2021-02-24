package io.github.rosariopfernandes.firebasecompose.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import io.github.rosariopfernandes.firebasecompose.ui.theme.purple500

@Composable
fun OnlyText(
        title: String,
        message: String
) {
    Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
    ) {
        Text(text = title, style = MaterialTheme.typography.h4, color = purple500)
        Text(text = message, textAlign = TextAlign.Center)
    }
}