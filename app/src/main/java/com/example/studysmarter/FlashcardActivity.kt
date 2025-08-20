package com.example.studysmarter

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studysmarter.api.*
import com.example.studysmarter.ui.theme.StudySmarterTheme
import kotlinx.coroutines.launch

data class Flashcard(
    val question: String,
    val answer: String
)

class FlashcardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudySmarterTheme {
                FlashcardScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen() {
    val activity = (LocalContext.current as? ComponentActivity)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiKey = "AIzaSyDZziNQ5iDlnef32Bm1wyVQNlg8Qs3PnEw"

    var topic by remember { mutableStateOf("") }
    var count by remember { mutableStateOf("5") }
    var flashcards by remember { mutableStateOf(listOf<Flashcard>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Flashcards") },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                label = { Text("Enter a topic or description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = count,
                onValueChange = { count = it.filter { c -> c.isDigit() } },
                label = { Text("Number of flashcards") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val geminiService = GeminiClient.getClient()

                            val prompt = "Create $count flashcards on the topic: $topic. Format them as question-answer pairs, each starting with 'Q:' and 'A:'."

                            val request = GeminiRequest(
                                contents = listOf(
                                    GeminiContent(
                                        parts = listOf(GeminiPart(prompt))
                                    )
                                )
                            )

                            val response = geminiService.generateFlashcards(apiKey = apiKey, request = request)

                            if (response.isSuccessful) {
                                val text = response.body()
                                    ?.candidates?.firstOrNull()
                                    ?.content?.parts?.firstOrNull()
                                    ?.text ?: ""

                                flashcards = text
                                    .split("\n")
                                    .filter { it.startsWith("Q:") || it.startsWith("A:") }
                                    .chunked(2)
                                    .mapNotNull { pair ->
                                        if (pair.size == 2) {
                                            val question = pair[0].removePrefix("Q:").trim()
                                            val answer = pair[1].removePrefix("A:").trim()
                                            Flashcard(question, answer)
                                        } else null
                                    }
                            } else {
                                Toast.makeText(context, "API Error: ${response.code()}", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Generate")
            }

            Spacer(modifier = Modifier.height(16.dp))

            FlashcardList(flashcards)
        }
    }
}

@Composable
fun FlashcardList(flashcards: List<Flashcard>) {
    LazyColumn {
        items(flashcards) { card ->
            FlashcardCard(card)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun FlashcardCard(flashcard: Flashcard) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Q: ${flashcard.question}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "A: ${flashcard.answer}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
