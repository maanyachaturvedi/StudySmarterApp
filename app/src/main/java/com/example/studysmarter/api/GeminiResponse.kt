package com.example.studysmarter.api

data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

data class GeminiCandidate(
    val content: GeminiContentResponse
)

data class GeminiContentResponse(
    val parts: List<GeminiPartResponse>
)

data class GeminiPartResponse(
    val text: String
)
