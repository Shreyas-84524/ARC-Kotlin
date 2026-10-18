package com.arc.arc.features.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.QueryBuilder
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arc.arc.core.designsystem.components.ArcCard
import com.arc.arc.core.designsystem.components.ArcEmptyState
import com.arc.arc.core.designsystem.components.ArcLoadingState
import com.arc.arc.core.designsystem.components.ArcPriorityBadge
import com.arc.arc.core.designsystem.theme.ArcDimens
import com.arc.arc.core.designsystem.theme.ArcTheme
import com.arc.arc.core.model.TaskModel
import com.arc.arc.core.model.TaskPriority
import java.text.SimpleDateFormat

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToTasks: () -> Unit,
    onNavigateToHabits: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val configuration = LocalConfiguration.current
    val locale = androidx.core.os.ConfigurationCompat.getLocales(configuration)[0] ?: java.util.Locale.US
    val activityDateFormat = remember(locale) { SimpleDateFormat("MMM d, h:mm a", locale) }
    val deadlineDateFormat = remember(locale) { SimpleDateFormat("MMM d", locale) }

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ArcTheme.colors.background,
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->
        if (state.isLoading) {
            ArcLoadingState()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(
                    start = ArcDimens.SpacingLg,
                    end = ArcDimens.SpacingLg,
                    top = ArcDimens.SpacingMd,
                    bottom = ArcDimens.SpacingXl,
                ),
                verticalArrangement = Arrangement.spacedBy(ArcDimens.SpacingLg),
            ) {
                // 1. Today's Progress Card
                item(key = "progress_card") {
                    ProgressOverviewCard(
                        state = state,
                    )
                }

                // 2. Productivity Score Banner
                item(key = "score_banner") {
                    ProductivityScoreBanner(
                        score = state.productivityScore,
                    )
                }

                // 3. Quick Metrics Row
                item(key = "metrics_row") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        MetricCard(
                            title = "Daily Streak",
                            value = "${state.maxStreakDays}d",
                            subtitle = "Keep it burning!",
                            icon = Icons.Default.LocalFireDepartment,
                            accentColor = Color(0xFFF97316),
                            modifier = Modifier.weight(1f),
                        )
                        MetricCard(
                            title = "Tasks Done",
                            value = "${state.completedTasksCount}/${state.totalTasksCount}",
                            subtitle = "All time",
                            icon = Icons.Default.CheckCircle,
                            accentColor = ArcTheme.colors.accent,
                            modifier = Modifier.weight(1f),
                        )
                        MetricCard(
                            title = "Habits Today",
                            value = "${state.completedTodayHabitsCount}/${state.totalHabitsCount}",
                            subtitle = "Check-ins",
                            icon = Icons.Default.TrackChanges,
                            accentColor = Color(0xFF10B981),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                // 4. Today's Habits Carousel
                item(key = "habits_section") {
                    SectionHeader(
                        title = "Today's Habits",
                        actionLabel = "View All",
                        onActionClick = onNavigateToHabits,
                    )

                    Spacer(modifier = Modifier.height(ArcDimens.SpacingSm))

                    if (state.todayHabits.isEmpty()) {
                        ArcCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "No habits tracked yet. Start building a streak!",
                                style = ArcTheme.typography.bodyMedium,
                                color = ArcTheme.colors.textSecondary,
                                modifier = Modifier.padding(ArcDimens.SpacingMd),
                            )
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            items(
                                items = state.todayHabits,
                                key = { it.habit.id },
                            ) { habitItem ->
                                TodayHabitCard(
                                    item = habitItem,
                                    onToggle = { completed ->
                                        viewModel.toggleHabitToday(habitItem.habit.id, completed)
                                    },
                                )
                            }
                        }
                    }
                }

                // 5. Today's Tasks Checklist
                item(key = "tasks_section") {
                    SectionHeader(
                        title = "Today's Tasks",
                        actionLabel = "View All",
                        onActionClick = onNavigateToTasks,
                    )

                    Spacer(modifier = Modifier.height(ArcDimens.SpacingSm))

                    if (state.todayTasks.isEmpty()) {
                        ArcCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(ArcDimens.SpacingMd),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = ArcTheme.colors.accent,
                                    modifier = Modifier.size(24.dp),
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "All caught up for today! No pending tasks.",
                                    style = ArcTheme.typography.bodyMedium,
                                    color = ArcTheme.colors.textSecondary,
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            state.todayTasks.take(4).forEach { task ->
                                TodayTaskRow(
                                    task = task,
                                    onToggle = { viewModel.toggleTask(task.id) },
                                )
                            }
                        }
                    }
                }

                // 6. Upcoming Deadlines
                if (state.upcomingDeadlines.isNotEmpty()) {
                    item(key = "upcoming_section") {
                        SectionHeader(
                            title = "Upcoming Deadlines",
                            actionLabel = null,
                            onActionClick = null,
                        )

                        Spacer(modifier = Modifier.height(ArcDimens.SpacingSm))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            state.upcomingDeadlines.forEach { item ->
                                UpcomingTaskCard(
                                    item = item,
                                    dateFormat = deadlineDateFormat,
                                )
                            }
                        }
                    }
                }

                // 7. Recent Activity Feed
                item(key = "activity_section") {
                    SectionHeader(
                        title = "Recent Activity",
                        actionLabel = null,
                        onActionClick = null,
                    )

                    Spacer(modifier = Modifier.height(ArcDimens.SpacingSm))

                    if (state.recentActivities.isEmpty()) {
                        ArcCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "No recent activity recorded yet.",
                                style = ArcTheme.typography.bodyMedium,
                                color = ArcTheme.colors.textSecondary,
                                modifier = Modifier.padding(ArcDimens.SpacingMd),
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            state.recentActivities.forEach { activity ->
                                ActivityRow(
                                    activity = activity,
                                    dateFormat = activityDateFormat,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String?,
    onActionClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = title,
            style = ArcTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = ArcTheme.colors.textPrimary,
        )

        if (actionLabel != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = actionLabel,
                        style = ArcTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = ArcTheme.colors.accent,
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = ArcTheme.colors.accent,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressOverviewCard(
    state: DashboardUiState,
    modifier: Modifier = Modifier,
) {
    ArcCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Today's Progress",
                    style = ArcTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ArcTheme.colors.textPrimary,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Completed: ${state.completedTodayTasksCount} / ${state.totalTodayTasksCount} Tasks",
                    style = ArcTheme.typography.bodyMedium,
                    color = ArcTheme.colors.textSecondary,
                )
                Text(
                    text = "Completed: ${state.completedTodayHabitsCount} / ${state.totalHabitsCount} Habits",
                    style = ArcTheme.typography.bodyMedium,
                    color = ArcTheme.colors.textSecondary,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(68.dp),
            ) {
                CircularProgressIndicator(
                    progress = { state.progressPercentage },
                    color = ArcTheme.colors.accent,
                    trackColor = ArcTheme.colors.surfaceVariant,
                    strokeWidth = 6.dp,
                    modifier = Modifier.fillMaxSize(),
                )
                Text(
                    text = "${(state.progressPercentage * 100).toInt()}%",
                    style = ArcTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = ArcTheme.colors.textPrimary,
                )
            }
        }
    }
}

@Composable
private fun ProductivityScoreBanner(
    score: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = ArcTheme.colors.accent.copy(alpha = 0.12f),
        shape = RoundedCornerShape(ArcDimens.RadiusCard),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = ArcTheme.colors.accent,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Productivity Score",
                        style = ArcTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = ArcTheme.colors.textPrimary,
                    )
                    Text(
                        text = "Based on tasks & habit completion",
                        style = ArcTheme.typography.labelSmall,
                        color = ArcTheme.colors.textSecondary,
                    )
                }
            }

            Surface(
                color = ArcTheme.colors.accent,
                shape = RoundedCornerShape(20.dp),
            ) {
                Text(
                    text = "$score / 100",
                    style = ArcTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    ArcCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = title,
                    style = ArcTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = ArcTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp),
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = ArcTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = ArcTheme.colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = ArcTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = ArcTheme.colors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun TodayHabitCard(
    item: DashboardHabitItem,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val habitColor = Color(item.habit.colorValue.toInt())
    val isDone = item.isCompletedToday

    val buttonBg by animateColorAsState(
        targetValue = if (isDone) habitColor else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "buttonBg",
    )

    ArcCard(
        modifier = modifier
            .width(180.dp)
            .clickable { onToggle(!isDone) },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(buttonBg)
                    .border(
                        width = 2.dp,
                        color = if (isDone) habitColor else ArcTheme.colors.borderSubtle,
                        shape = CircleShape,
                    ),
            ) {
                if (isDone) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.habit.name,
                    style = ArcTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = ArcTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.habit.category,
                    style = ArcTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = ArcTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun TodayTaskRow(
    task: TaskModel,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val priorityColor = when (task.priorityEnum) {
        TaskPriority.HIGH -> ArcTheme.colors.priorityHigh
        TaskPriority.MUST -> ArcTheme.colors.priorityMust
        TaskPriority.LOW -> ArcTheme.colors.priorityLow
    }

    ArcCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle() },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (task.completed) priorityColor else Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = priorityColor,
                        shape = CircleShape,
                    ),
            ) {
                if (task.completed) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = ArcTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (task.completed) TextDecoration.LineThrough else null,
                    ),
                    color = if (task.completed) ArcTheme.colors.textDisabled else ArcTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${task.category} • ${task.displayPriority}",
                    style = ArcTheme.typography.labelSmall,
                    color = ArcTheme.colors.textSecondary,
                )
            }

            ArcPriorityBadge(priority = task.priorityEnum)
        }
    }
}

@Composable
private fun UpcomingTaskCard(
    item: DashboardTaskItem,
    dateFormat: SimpleDateFormat,
    modifier: Modifier = Modifier,
) {
    val dateText = item.task.dueDate?.let { dateFormat.format(it) } ?: ""

    ArcCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = ArcTheme.colors.accent,
                modifier = Modifier.size(22.dp),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.task.title,
                    style = ArcTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = ArcTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Due in ${item.daysUntilDue} day${if (item.daysUntilDue > 1) "s" else ""} ($dateText)",
                    style = ArcTheme.typography.labelSmall,
                    color = ArcTheme.colors.textSecondary,
                )
            }

            ArcPriorityBadge(priority = item.task.priorityEnum)
        }
    }
}

@Composable
private fun ActivityRow(
    activity: DashboardActivityItem,
    dateFormat: SimpleDateFormat,
    modifier: Modifier = Modifier,
) {
    val (icon, iconColor) = when (activity.type) {
        DashboardActivityType.TASK -> Pair(Icons.Default.CheckBox, Color(0xFF1890FF))
        DashboardActivityType.HABIT -> Pair(Icons.Default.LocalFireDepartment, Color(0xFFF97316))
    }

    ArcCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Surface(
                color = iconColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(34.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    style = ArcTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = ArcTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${activity.subtitle} • ${dateFormat.format(activity.timestamp)}",
                    style = ArcTheme.typography.labelSmall,
                    color = ArcTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
