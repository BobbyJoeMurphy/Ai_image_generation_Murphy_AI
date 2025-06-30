package com.example.ai_image_generation_murphy_ai.data.repository.model

data class GPTRequest(
    val model: String = "gpt-4",
    val messages: List<Message>,
    val max_tokens: Int = 200
)

data class Message(
    val role: String,
    val content: String
)

data class GPTResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)
