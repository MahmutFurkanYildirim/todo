package com.furkanyildirim.learningcompose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.furkanyildirim.learningcompose.ui.screens.AuthChoiceScreen
import com.furkanyildirim.learningcompose.ui.screens.AddTodoScreen
import com.furkanyildirim.learningcompose.ui.screens.FocusModeScreen
import com.furkanyildirim.learningcompose.ui.screens.OnboardingScreen
import com.furkanyildirim.learningcompose.ui.screens.SettingsScreen
import com.furkanyildirim.learningcompose.ui.screens.SplashScreen
import com.furkanyildirim.learningcompose.ui.screens.StatsScreen
import com.furkanyildirim.learningcompose.ui.screens.TodoDetailScreen
import com.furkanyildirim.learningcompose.ui.screens.TodoScreen
import com.furkanyildirim.learningcompose.viewmodel.TodoViewModel

@Composable
fun TodoNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    authChoiceCompleted: Boolean,
    onCompleteAuthChoice: () -> Unit,
    onboardingCompleted: Boolean,
    onCompleteOnboarding: () -> Unit,
    onResetOnboarding: () -> Unit,
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit,
    signedInEmail: String?,
    onEmailSignIn: (String, String) -> Unit,
    onEmailSignUp: (String, String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onSignOut: () -> Unit
) {
    val viewModel: TodoViewModel = hiltViewModel()
    val todos by viewModel.todos.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    val focusWorkMinutes by viewModel.focusWorkMinutes.collectAsStateWithLifecycle()
    val focusBreakMinutes by viewModel.focusBreakMinutes.collectAsStateWithLifecycle()
    val focusAutoStartBreak by viewModel.focusAutoStartBreak.collectAsStateWithLifecycle()
    val focusAutoStartWork by viewModel.focusAutoStartWork.collectAsStateWithLifecycle()
    val focusSessionHistory by viewModel.focusSessionHistory.collectAsStateWithLifecycle()
    val syncTelemetry by viewModel.syncTelemetry.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {
        composable("splash") {
            SplashScreen(
                onFinished = {
                    val destination = when {
                        !authChoiceCompleted -> "auth_choice"
                        onboardingCompleted -> "todo_list"
                        else -> "onboarding"
                    }
                    navController.navigate(destination) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("auth_choice") {
            LaunchedEffect(signedInEmail, authChoiceCompleted) {
                if (!signedInEmail.isNullOrBlank() && !authChoiceCompleted) {
                    onCompleteAuthChoice()
                    navController.navigate(if (onboardingCompleted) "todo_list" else "onboarding") {
                        popUpTo("auth_choice") { inclusive = true }
                    }
                }
            }

            AuthChoiceScreen(
                onEmailSignIn = onEmailSignIn,
                onEmailSignUp = onEmailSignUp,
                onGoogleSignIn = onGoogleSignIn,
                onContinueAsGuest = {
                    onCompleteAuthChoice()
                    navController.navigate(if (onboardingCompleted) "todo_list" else "onboarding") {
                        popUpTo("auth_choice") { inclusive = true }
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    onCompleteOnboarding()
                    navController.navigate("todo_list") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("todo_list") {
            TodoScreen(
                viewModel = viewModel,
                onTodoClick = { todo ->
                    navController.navigate("todo_detail/${todo.id}")
                },
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onOpenSettings = { navController.navigate("settings") },
                onOpenStats = { navController.navigate("stats") },
                onOpenFocusMode = { navController.navigate("focus_mode") },
                onOpenAddTodo = { navController.navigate("add_todo") },
                recentSearches = recentSearches
            )
        }

        composable("add_todo") {
            AddTodoScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("settings") {
            SettingsScreen(
                isDarkTheme = isDarkTheme,
                recentSearchCount = recentSearches.size,
                onThemeChange = onThemeChange,
                onClearRecentSearches = viewModel::clearRecentSearches,
                onResetOnboarding = {
                    onResetOnboarding()
                    navController.navigate("onboarding") {
                        popUpTo("todo_list") { inclusive = true }
                    }
                },
                selectedLanguage = selectedLanguage,
                onLanguageChange = onLanguageChange,
                signedInEmail = signedInEmail,
                onGoogleSignIn = onGoogleSignIn,
                onSignOut = {
                    onSignOut()
                    navController.navigate("auth_choice") {
                        popUpTo("todo_list") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("focus_mode") {
            FocusModeScreen(
                workDurationMinutes = focusWorkMinutes,
                breakDurationMinutes = focusBreakMinutes,
                autoStartBreak = focusAutoStartBreak,
                autoStartWork = focusAutoStartWork,
                onWorkDurationChange = viewModel::setFocusWorkMinutes,
                onBreakDurationChange = viewModel::setFocusBreakMinutes,
                onAutoStartBreakChange = viewModel::setFocusAutoStartBreak,
                onAutoStartWorkChange = viewModel::setFocusAutoStartWork,
                onFocusSessionCompleted = viewModel::recordFocusSession,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("stats") {
            StatsScreen(
                todos = todos,
                focusSessionHistory = focusSessionHistory,
                syncTelemetry = syncTelemetry,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("todo_detail/{todoId}") { backStackEntry ->
            val todoId = backStackEntry.arguments?.getString("todoId")?.toIntOrNull()
            val todo = todos.find { it.id == todoId }

            todo?.let {
                TodoDetailScreen(
                    todo = it,
                    onBackClick = { navController.popBackStack() },
                    onUpdateTitle = { todoItem, newTitle ->
                        viewModel.updateTodoTitle(todoItem, newTitle)
                    },
                    onUpdateCategory = { todoItem, category ->
                        viewModel.updateTodoCategory(todoItem, category)
                    },
                    onUpdatePriority = { todoItem, priority ->
                        viewModel.updateTodoPriority(todoItem, priority)
                    },
                    onUpdateRepeatInterval = { todoItem, repeatIntervalDays ->
                        viewModel.updateTodoRepeatInterval(todoItem, repeatIntervalDays)
                    },
                    onUpdateRepeatRule = { todoItem, repeatRule ->
                        viewModel.updateTodoRepeatRule(todoItem, repeatRule)
                    },
                    onTogglePinned = { todoItem ->
                        viewModel.togglePinned(todoItem)
                    }
                )
            }
        }
    }
}
