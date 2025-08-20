package com.example.studysmarter

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    onStudyTasksClick: () -> Unit,
    onFlashcardsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onStudyTasksClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Study Tasks")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onFlashcardsClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Flashcards")
        }
    }
}
