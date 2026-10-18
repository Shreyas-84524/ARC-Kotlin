package com.arc.arc.features.money

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arc.arc.core.di.AppContainer
import com.arc.arc.features.money.ui.MoneyScreen
import com.arc.arc.features.money.ui.MoneyViewModel

@Composable
fun MoneyRoute(
    container: AppContainer,
    modifier: Modifier = Modifier,
) {
    val viewModel: MoneyViewModel = viewModel(
        factory = MoneyViewModel.factory(
            transactionRepository = container.transactionRepository,
            authRepository = container.authRepository,
        )
    )

    val uiState by viewModel.uiState.collectAsState()

    MoneyScreen(
        uiState = uiState,
        onPreviousMonth = viewModel::previousMonth,
        onNextMonth = viewModel::nextMonth,
        onTabSelected = viewModel::setSelectedTab,
        onFilterTypeSelected = viewModel::setFilterType,
        onSearchQueryChanged = viewModel::setSearchQuery,
        onSaveTransaction = viewModel::saveTransaction,
        onDeleteTransaction = viewModel::deleteTransaction,
        onSetBudget = viewModel::setMonthlyBudget,
        onClearMessage = viewModel::clearMessage,
        modifier = modifier,
    )
}
