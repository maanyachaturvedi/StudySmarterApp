package com.example.studysmarter

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.studysmarter.ui.theme.StudySmarterTheme
import java.time.LocalDate
import java.util.Calendar

// Data class
data class Task(
    val description: String,
    var isDone: Boolean = false,
    val dueDate: LocalDate? = null
)

enum class FilterDueDate { ALL, OVERDUE, DUE_TODAY, NO_DUE_DATE }

class TaskActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudySmarterTheme {
                TaskScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var newTaskText by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf<LocalDate?>(null) }
    val taskList = remember { mutableStateListOf<Task>() }

    var filterDueDate by remember { mutableStateOf(FilterDueDate.ALL) }
    var filterDueDateDropdownExpanded by remember { mutableStateOf(false) }

    var sortByDueDateAsc by remember { mutableStateOf(true) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            dueDate = LocalDate.of(year, month + 1, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)) {

            OutlinedTextField(
                value = newTaskText,
                onValueChange = { newTaskText = it },
                label = { Text("Enter new task") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Due date: ${dueDate?.toString() ?: "None"}", modifier = Modifier.weight(1f))
                Button(onClick = { datePickerDialog.show() }) {
                    Text("Select Due Date")
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    if (newTaskText.isNotBlank()) {
                        taskList.add(Task(newTaskText.trim(), false, dueDate))
                        newTaskText = ""
                        dueDate = null
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add Task")
            }

            Spacer(Modifier.height(16.dp))

            // Filter UI
            Text("Filter by Due Date:")
            Box {
                Button(onClick = { filterDueDateDropdownExpanded = true }) {
                    Text(filterDueDate.name)
                }
                DropdownMenu(
                    expanded = filterDueDateDropdownExpanded,
                    onDismissRequest = { filterDueDateDropdownExpanded = false }
                ) {
                    FilterDueDate.values().forEach { fd ->
                        DropdownMenuItem(
                            text = { Text(fd.name) },
                            onClick = {
                                filterDueDate = fd
                                filterDueDateDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(onClick = { sortByDueDateAsc = !sortByDueDateAsc }) {
                Text(if (sortByDueDateAsc) "Sort: Due ↑" else "Sort: Due ↓")
            }

            Spacer(Modifier.height(16.dp))

            val filteredTasks = taskList
                .filter { task ->
                    when (filterDueDate) {
                        FilterDueDate.ALL -> true
                        FilterDueDate.OVERDUE -> task.dueDate?.isBefore(LocalDate.now()) == true && !task.isDone
                        FilterDueDate.DUE_TODAY -> task.dueDate == LocalDate.now()
                        FilterDueDate.NO_DUE_DATE -> task.dueDate == null
                    }
                }
                .sortedWith(compareBy {
                    if (sortByDueDateAsc) it.dueDate ?: LocalDate.MAX
                    else it.dueDate?.let { d -> LocalDate.ofEpochDay(-d.toEpochDay()) } ?: LocalDate.MIN
                })

            LazyColumn {
                items(filteredTasks) { task ->
                    TaskItem(
                        task = task,
                        onCheckChange = { task.isDone = it },
                        onDelete = { taskList.remove(task) }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onCheckChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isDone,
            onCheckedChange = onCheckChange
        )

        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
            Text(text = task.description)
            task.dueDate?.let {
                Text(text = "Due: $it", style = MaterialTheme.typography.bodySmall)
            }
        }

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Task")
        }
    }
}
