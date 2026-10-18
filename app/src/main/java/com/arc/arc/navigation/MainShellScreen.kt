package com.arc.arc.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arc.arc.core.auth.IAuthRepository
import com.arc.arc.core.auth.UserSession
import com.arc.arc.core.datastore.AppPreferencesDataStore
import com.arc.arc.core.designsystem.components.ArcBottomNavigationBar
import com.arc.arc.core.designsystem.components.ArcConfirmationDialog
import com.arc.arc.core.designsystem.components.ArcNavTab
import com.arc.arc.core.designsystem.theme.ArcDimens
import com.arc.arc.core.designsystem.theme.ArcTheme
import com.arc.arc.features.dashboard.DashboardPlaceholderScreen
import com.arc.arc.features.habits.HabitsPlaceholderScreen
import com.arc.arc.features.money.MoneyPlaceholderScreen
import kotlinx.coroutines.launch

@Composable
fun MainShellScreen(
    authRepository: IAuthRepository,
    preferencesDataStore: AppPreferencesDataStore,
    tasksContent: @Composable () -> Unit,
    habitsContent: @Composable () -> Unit = {},
    dashboardContent: (@Composable ((ArcNavTab) -> Unit) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableStateOf(ArcNavTab.TASKS) }
    var showSignOutDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val userSession = authRepository.currentUserSession
    val preferences by preferencesDataStore.preferencesFlow.collectAsState(
        initial = com.arc.arc.core.datastore.AppPreferences()
    )

    if (showSignOutDialog) {
        ArcConfirmationDialog(
            title = "Sign Out",
            message = "Are you sure you want to sign out from ${userSession?.displayName ?: "ARC"}?",
            confirmLabel = "Sign Out",
            isDanger = true,
            onConfirm = {
                showSignOutDialog = false
                scope.launch { authRepository.signOut() }
            },
            onDismiss = { showSignOutDialog = false },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ArcTheme.colors.background,
        topBar = {
            MainTopBar(
                userSession = userSession,
                isDarkMode = preferences.themeMode == "dark",
                onToggleTheme = {
                    val nextMode = if (preferences.themeMode == "dark") "light" else "dark"
                    scope.launch { preferencesDataStore.setThemeMode(nextMode) }
                },
                onSignOutClick = { showSignOutDialog = true },
            )
        },
        bottomBar = {
            ArcBottomNavigationBar(
                currentRoute = selectedTab.route,
                onTabSelected = { selectedTab = it },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (selectedTab) {
                ArcNavTab.DASHBOARD -> {
                    if (dashboardContent != null) {
                        dashboardContent { targetTab -> selectedTab = targetTab }
                    } else {
                        DashboardPlaceholderScreen(
                            onNavigateToTasks = { selectedTab = ArcNavTab.TASKS },
                        )
                    }
                }
                ArcNavTab.TASKS -> tasksContent()
                ArcNavTab.HABITS -> habitsContent()
                ArcNavTab.MONEY -> MoneyPlaceholderScreen(
                    onNavigateToTasks = { selectedTab = ArcNavTab.TASKS },
                )
            }
        }
    }
}

@Composable
private fun MainTopBar(
    userSession: UserSession?,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = ArcTheme.colors.surface,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ArcDimens.ScreenHorizontalPadding, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // App Branding & User Indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ARC",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArcTheme.colors.accent,
                    letterSpacing = (-0.5).sp,
                )
                Spacer(modifier = Modifier.width(12.dp))

                if (userSession != null) {
                    Row(
                        modifier = Modifier
                            .background(
                                color = ArcTheme.colors.card,
                                shape = RoundedCornerShape(ArcDimens.RadiusPill),
                            )
                            .border(
                                width = 1.dp,
                                color = ArcTheme.colors.borderSubtle,
                                shape = RoundedCornerShape(ArcDimens.RadiusPill),
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(ArcTheme.colors.accent),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = userSession.username.take(1).uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827),
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (userSession.isAdmin) "${userSession.username} ★" else userSession.username,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ArcTheme.colors.textPrimary,
                        )
                    }
                }
            }

            // Action Icons (Theme toggle & Sign Out)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Theme",
                        tint = ArcTheme.colors.textSecondary,
                        modifier = Modifier.size(20.dp),
                    )
                }

                IconButton(onClick = onSignOutClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Sign Out",
                        tint = ArcTheme.colors.danger,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}
