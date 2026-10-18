package com.arc.arc.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arc.arc.core.auth.AuthState
import com.arc.arc.core.di.AppContainer
import com.arc.arc.core.designsystem.components.ArcLoadingState
import com.arc.arc.core.designsystem.theme.ArcTheme
import com.arc.arc.features.auth.LoginScreen
import com.arc.arc.features.auth.LoginViewModel

@Composable
fun ArcNavHost(
    container: AppContainer,
    tasksContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val authState by container.authRepository.authState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ArcTheme.colors.background),
    ) {
        when (val state = authState) {
            is AuthState.Initializing -> {
                ArcLoadingState()
            }

            is AuthState.Unauthenticated, is AuthState.Error -> {
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModel.factory(container.authRepository)
                )
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        // Handled reactively by authState state flow
                    },
                )
            }

            is AuthState.Authenticated -> {
                MainShellScreen(
                    authRepository = container.authRepository,
                    preferencesDataStore = container.preferencesDataStore,
                    tasksContent = tasksContent,
                    habitsContent = {
                        com.arc.arc.features.habits.HabitsRoute(container = container)
                    },
                )
            }
        }
    }
}
