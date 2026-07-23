package com.nexaai.presentation.navigation

sealed class Screen(val route: String) {
    data object ConversationsList : Screen("conversations")
    data object Chat : Screen("chat/{conversationId}") {
        fun createRoute(conversationId: Long) = "chat/$conversationId"
    }
    data object ModelManager : Screen("models")
    data object Settings : Screen("settings")
    data object Memory : Screen("memory")
}
