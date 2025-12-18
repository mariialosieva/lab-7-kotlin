package com.example.lab66.data.model

import kotlinx.serialization.Serializable

@Serializable
data class QuizResponse(
    val questions: List<Question>
)

@Serializable
data class Question(
    val question: String,
    val answers: List<String>,
    val correctIndex: Int
)
