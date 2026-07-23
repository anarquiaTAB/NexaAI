package com.nexaai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nexaai.presentation.chat.ChatScreen
import com.nexaai.presentation.conversations.ConversationsListScreen
import com.nexaai.presentation.memory.MemoryScreen
import com.nexaai.presentation.modelmanager.ModelManagerScreen
import com.nexaai.presentation.settings.SettingsScreen

@Composable
fun NexaAiNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.ConversationsList.route) {

        composable(Screen.ConversationsList.route) {
            ConversationsListScreen(
                onOpenConversation = { id -> navController.navigate(Screen.Chat.createRoute(id)) },
                onOpenModelManager = { navController.navigate(Screen.ModelManager.route) },
                onOpenSettings = { navController.navigate(Screen.Settings.route) },
                onOpenMemory = { navController.navigate(Screen.Memory.route) }
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("conversationId") { type = NavType.LongType })
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getLong("conversationId") ?: 0L
            ChatScreen(
                conversationId = conversationId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ModelManager.route) {
            ModelManagerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Memory.route) {
            MemoryScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
