package com.example.lab66.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeScreen(
    onTopicSelected: (String) -> Unit
) {
    var customTopic by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Вікторина",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Text(
            text = "Обери тему:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { onTopicSelected("Фотографія") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Фотографія")
        }

        Button(
            onClick = { onTopicSelected("Йога і медитація") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Йога і медитація")
        }
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        OutlinedTextField(
            value = customTopic,
            onValueChange = { customTopic = it },
            label = { Text("Власна тема") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { if (customTopic.isNotBlank()) onTopicSelected(customTopic) },
            enabled = customTopic.isNotBlank(),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Почати з власною темою")
        }
    }
}
