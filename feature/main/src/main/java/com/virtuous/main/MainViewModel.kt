package com.virtuous.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtuous.analytics.AnalyticsHelper
import com.virtuous.analytics.error.ErrorHelper
import com.virtuous.domain.repository.NotificationRepository
import com.virtuous.domain.repository.UserRepository
import com.virtuous.home.graph.home.HomeViewModel.HomeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val errorHelper: ErrorHelper,
) : ViewModel() {

    fun readNotification(notificationId: String) = viewModelScope.launch {
        notificationRepository.readNotification(notificationId).onSuccess {
            analyticsHelper.trackActionEvent(
                screenName = "main_activity",
                actionName = "read_notification",
            )
        }.onFailure {
            errorHelper.logError(it)
        }
    }
}