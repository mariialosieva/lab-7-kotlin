package com.example.lab66.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.example.lab66.data.model.Question

@Composable
fun GameScreen(
    uiState: QuizUiState.Success,
    onAnswerSelected: (Int) -> Unit,
    onNextQuestion: () -> Unit
) {
    val currentQuestion = uiState.questions[uiState.currentQuestionIndex]
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Прогрес
        LinearProgressIndicator(
            progress = { (uiState.currentQuestionIndex + 1) / uiState.questions.size.toFloat() },
            modifier = Modifier.fillMaxWidth(),
            trackColor = Color(0xFF495235)
        )
        Text(
            text = "Питання ${uiState.currentQuestionIndex + 1} з ${uiState.questions.size}",
            modifier = Modifier.padding(vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Питання
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFDA983C)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Text(
                text = currentQuestion.question,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Варіанти відповідей
        currentQuestion.answers.forEachIndexed { index, answerText ->
            AnswerButton(
                text = answerText,
                state = getAnswerState(index, uiState),
                onClick = { onAnswerSelected(index) },
                enabled = uiState.selectedAnswerIndex == null
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Кнопка Далі
        if (uiState.selectedAnswerIndex != null) {
            Button(
                onClick = onNextQuestion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.currentQuestionIndex < uiState.questions.size - 1) "Наступне питання" else "Завершити")
            }
        }
    }
}

enum class AnswerState {
    DEFAULT, CORRECT, WRONG, IGNORED
}

fun getAnswerState(index: Int, uiState: QuizUiState.Success): AnswerState {
    if (uiState.selectedAnswerIndex == null) return AnswerState.DEFAULT

    val currentQuestion = uiState.questions[uiState.currentQuestionIndex]
    
    return when {
         index == currentQuestion.correctIndex -> AnswerState.CORRECT // Завжди показуємо правильну
         index == uiState.selectedAnswerIndex && index != currentQuestion.correctIndex -> AnswerState.WRONG // Якщо вибрали цю і вона неправильна
         else -> AnswerState.IGNORED // Інші варіанти (бліді/неактивні)
    }
}

@Composable
fun AnswerButton(
    text: String,
    state: AnswerState,
    onClick: () -> Unit,
    enabled: Boolean
) {
    val backgroundColor = when (state) {
        AnswerState.CORRECT -> Color(0xFF4CAF50) // Green
        AnswerState.WRONG -> Color(0xFFE57373) // Red
        AnswerState.IGNORED -> Color.LightGray.copy(alpha = 0.5f)
        AnswerState.DEFAULT -> Color(0xFF8F9562)
    }
    
//    val contentColor = when(state) {
//         AnswerState.IGNORED -> Color.Gray
//         else -> Color.Black
//    }
    val contentColor = if (state == AnswerState.CORRECT || state == AnswerState.WRONG) Color.Black else Color.White

    // Using OutlinedButton or Button with custom colors
    Button(
        onClick = onClick,
        enabled = enabled || state != AnswerState.DEFAULT, // Keep clickable visual style but logical handling in parent
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = backgroundColor,
            disabledContentColor = contentColor
        ),
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RectangleShape // Or default rounded
    ) {
        Text(text)
    }
}
