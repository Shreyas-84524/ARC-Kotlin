package com.arc.arc.features.habits

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arc.arc.core.designsystem.components.ArcCard
import com.arc.arc.core.designsystem.components.ArcConfirmationDialog
import com.arc.arc.core.designsystem.components.ArcEmptyState
import com.arc.arc.core.designsystem.components.ArcFilterChip
import com.arc.arc.core.designsystem.components.ArcLoadingState
import com.arc.arc.core.designsystem.theme.ArcDimens
import com.arc.arc.core.designsystem.theme.ArcTheme
import com.arc.arc.features.habits.engine.HabitGoalType

@Composable
fun HabitsScreen(
    viewModel: HabitsViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openCreateEditor() },
                containerColor = ArcTheme.colors.accent,
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier.semantics { contentDescription = "Add New Habit" },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        containerColor = ArcTheme.colors.background,
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (state.isLoading && state.allHabits.isEmpty()) {
                ArcLoadingState()
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // 1. Top Summary Banner with Progress and Goal Switcher
                    HabitsSummaryCard(
                        summary = state.summary,
                        selectedGoalType = state.selectedGoalType,
                        onGoalTypeChanged = { viewModel.setGoalType(it) },
                        modifier = Modifier.padding(
                            horizontal = ArcDimens.SpacingLg,
                            vertical = ArcDimens.SpacingSm,
                        ),
                    )

                    // 2. Category Filter Chips Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = ArcDimens.SpacingLg, vertical = ArcDimens.SpacingSm),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ArcFilterChip(
                            label = "All Habits",
                            selected = state.selectedCategory == null,
                            onClick = { viewModel.selectCategory(null) },
                        )
                        HabitConstants.CATEGORIES.forEach { category ->
                            ArcFilterChip(
                                label = category,
                                selected = state.selectedCategory == category,
                                onClick = { viewModel.selectCategory(category) },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(ArcDimens.SpacingSm))

                    // 3. Habits List or Empty State
                    if (state.filteredHabits.isEmpty()) {
                        ArcEmptyState(
                            icon = Icons.Default.TrackChanges,
                            title = if (state.allHabits.isEmpty()) "No Habits Yet" else "No Habits Found",
                            description = if (state.allHabits.isEmpty()) {
                                "Build consistency by creating your first daily habit!"
                            } else {
                                "No habits match the selected '${state.selectedCategory}' category filter."
                            },
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                start = ArcDimens.SpacingLg,
                                end = ArcDimens.SpacingLg,
                                top = ArcDimens.SpacingSm,
                                bottom = 88.dp, // Space for FAB
                            ),
                            verticalArrangement = Arrangement.spacedBy(ArcDimens.SpacingMd),
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            items(
                                items = state.filteredHabits,
                                key = { it.habit.id },
                            ) { itemState ->
                                HabitCard(
                                    state = itemState,
                                    onToggleToday = { completed ->
                                        viewModel.toggleHabitToday(itemState.habit.id, completed)
                                    },
                                    onEditClick = { viewModel.openEditEditor(itemState.habit) },
                                    onDeleteClick = { viewModel.confirmDeleteHabit(itemState.habit) },
                                )
                            }
                        }
                    }
                }
            }

            // Editor Sheet
            if (state.isEditorOpen) {
                HabitEditorSheet(
                    habitToEdit = state.editingHabit,
                    onDismiss = { viewModel.closeEditor() },
                    onSave = { name, category, color, reminder ->
                        viewModel.saveHabit(name, category, color, reminder)
                    },
                )
            }

            // Delete Confirmation Dialog
            state.habitToDelete?.let { habit ->
                ArcConfirmationDialog(
                    title = "Delete Habit",
                    message = "Are you sure you want to delete \"${habit.name}\"? All check-in history for this habit will be permanently deleted.",
                    confirmLabel = "Delete",
                    cancelLabel = "Cancel",
                    onConfirm = { viewModel.deleteHabit() },
                    onDismiss = { viewModel.dismissDeleteDialog() },
                    isDanger = true,
                )
            }
        }
    }
}

@Composable
private fun HabitsSummaryCard(
    summary: com.arc.arc.features.habits.engine.HabitProgressSummary,
    selectedGoalType: HabitGoalType,
    onGoalTypeChanged: (HabitGoalType) -> Unit,
    modifier: Modifier = Modifier,
) {
    ArcCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(ArcDimens.SpacingMd)) {
            // Segmented Control: Daily / Weekly / Monthly
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ArcTheme.colors.surfaceVariant, RoundedCornerShape(10.dp))
                    .padding(3.dp),
            ) {
                HabitGoalType.entries.forEach { goalType ->
                    val isSelected = selectedGoalType == goalType
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ArcTheme.colors.surface else Color.Transparent)
                            .clickable { onGoalTypeChanged(goalType) }
                            .padding(vertical = 6.dp),
                    ) {
                        Text(
                            text = goalType.displayName,
                            style = ArcTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            ),
                            color = if (isSelected) ArcTheme.colors.accent else ArcTheme.colors.textSecondary,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(ArcDimens.SpacingMd))

            // Progress Ring and Stats
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Circular Progress
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(64.dp),
                ) {
                    CircularProgressIndicator(
                        progress = { summary.completionPercentage },
                        color = ArcTheme.colors.accent,
                        trackColor = ArcTheme.colors.surfaceVariant,
                        strokeWidth = 6.dp,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Text(
                        text = "${(summary.completionPercentage * 100).toInt()}%",
                        style = ArcTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = ArcTheme.colors.textPrimary,
                    )
                }

                Spacer(modifier = Modifier.width(ArcDimens.SpacingLg))

                // Stats Columns
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${summary.completedCount} Completed • ${summary.remainingCount} Remaining",
                        style = ArcTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = ArcTheme.colors.textPrimary,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color(0xFFF97316),
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Top Streak: ${summary.maxCurrentStreak}d (Best: ${summary.maxLongestStreak}d)",
                            style = ArcTheme.typography.bodySmall,
                            color = ArcTheme.colors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}
