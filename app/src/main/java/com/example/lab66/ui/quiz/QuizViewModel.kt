package com.example.lab66.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab66.data.model.Question
import com.example.lab66.data.repository.QuizAiDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Стан UI
sealed class QuizUiState {
    object Idle : QuizUiState() // Початковий стан (вибір теми)
    object Loading : QuizUiState() // Завантаження питань
    data class Success(
        val questions: List<Question>,
        val currentQuestionIndex: Int = 0,
        val userScore: Int = 0,
        val isGameOver: Boolean = false,
        val selectedAnswerIndex: Int? = null,
        val isAnswerCorrect: Boolean? = null
    ) : QuizUiState()
    data class Error(val message: String) : QuizUiState()
}

class QuizViewModel : ViewModel() {
    private val repository = QuizAiDataSource()
    
    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Idle)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    // Завантаження питань за темою
    fun loadQuestions(topic: String) {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            try {
                val response = repository.generateQuestions(topic)
                _uiState.value = QuizUiState.Success(
                    questions = response.questions
                )
            } catch (e: Exception) {
                _uiState.value = QuizUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // Вибір відповіді користувачем
    fun submitAnswer(answerIndex: Int) {
        val currentState = _uiState.value as? QuizUiState.Success ?: return
        if (currentState.selectedAnswerIndex != null) return // Already answered

        val currentQuestion = currentState.questions[currentState.currentQuestionIndex]
        val isCorrect = answerIndex == currentQuestion.correctIndex
        val newScore = if (isCorrect) currentState.userScore + 1 else currentState.userScore

        _uiState.value = currentState.copy(
            selectedAnswerIndex = answerIndex,
            isAnswerCorrect = isCorrect,
            userScore = newScore
        )
    }

    // Перехід до наступного питання
    fun nextQuestion() {
        val currentState = _uiState.value as? QuizUiState.Success ?: return
        
        val nextIndex = currentState.currentQuestionIndex + 1
        if (nextIndex < currentState.questions.size) {
            _uiState.value = currentState.copy(
                currentQuestionIndex = nextIndex,
                selectedAnswerIndex = null,
                isAnswerCorrect = null
            )
        } else {
            _uiState.value = currentState.copy(
                isGameOver = true
            )
        }
    }

    // Скидання гри
    fun resetGame() {
        _uiState.value = QuizUiState.Idle
    }
}
