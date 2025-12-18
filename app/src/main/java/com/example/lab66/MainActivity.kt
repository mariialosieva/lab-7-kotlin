package com.example.lab66

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab66.ui.quiz.GameScreen
import com.example.lab66.ui.quiz.QuizUiState
import com.example.lab66.ui.quiz.QuizViewModel
import com.example.lab66.ui.quiz.ResultScreen
import com.example.lab66.ui.quiz.WelcomeScreen
import com.example.lab66.ui.theme.Lab66Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab66Theme(dynamicColor = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QuizApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun QuizApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val viewModel: QuizViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(navController = navController, startDestination = "welcome") {
            composable("welcome") {
                WelcomeScreen(
                    onTopicSelected = { topic ->
                        viewModel.loadQuestions(topic)
                        navController.navigate("game")
                    }
                )
            }
            
            composable("game") {
                when (val state = uiState) {
                    is QuizUiState.Idle -> {
                        // Should probably not happen here if navigated correctly, or loading not started
                        Box(contentAlignment = Alignment.Center) { Text("Starting...") }
                        if (state is QuizUiState.Success) navController.navigate("game") // Retry logic check
                    }
                    is QuizUiState.Loading -> {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is QuizUiState.Success -> {
                        if (state.isGameOver) {
                            // Automatically navigate to results if game over
                            // We use LaunchedEffect or just render ResultScreen directly or navigate
                            // Better to navigate to a result route to keep backstack clean or straightforward
                            // However, strictly inside "game" composable updates is easier. 
                            // Let's use a separate logic:
                            // If game over, we can redirect or show result.
                            // Let's redirect to "result" route
                           androidx.compose.runtime.LaunchedEffect(Unit) {
                               navController.navigate("result") {
                                   popUpTo("welcome") { saveState = false } // Keep welcome in backstack? Or clear game?
                                   // Let's pop everything up to welcome so back goes to welcome
                               }
                           }
                        } else {
                            GameScreen(
                                uiState = state,
                                onAnswerSelected = { viewModel.submitAnswer(it) },
                                onNextQuestion = { viewModel.nextQuestion() }
                            )
                        }
                    }
                    is QuizUiState.Error -> {
                        Box(contentAlignment = Alignment.Center) {
                            Text("Error: ${state.message}")
                            // Button to retry?
                        }
                    }
                }
            }

            composable("result") {
                // We need to retrieve score from somewhere.
                // We can pass arguments or read from shared ViewModel.
                // Since ViewModel is scoped to NavHost (owner depends on where we initiated it).
                // Here `viewModel()` inside `QuizApp` uses LocalViewModelStoreOwner which is usually the Activity or NavHost if scoped.
                // Default `viewModel()` in Activity composable is Activity scoped. So update persists.
                
                val state = uiState
                if (state is QuizUiState.Success) {
                    ResultScreen(
                        score = state.userScore,
                        totalQuestions = state.questions.size,
                        onRestart = {
                            viewModel.resetGame()
                            navController.navigate("welcome") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        }
                    )
                } else {
                   // Fallback if state lost or invalid
                    val score = if (state is QuizUiState.Success) state.userScore else 0
                    ResultScreen(score = score, totalQuestions = 10, onRestart = {
                        viewModel.resetGame()
                         navController.navigate("welcome") 
                    })
                }
            }
        }
    }
}