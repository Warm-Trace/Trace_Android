package com.virtuous.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtuous.common.event.EventHelper
import com.virtuous.domain.repository.UserRepository
import com.virtuous.navigation.AuthGraph
import com.virtuous.navigation.HomeGraph
import com.virtuous.navigation.NavigationEvent
import com.virtuous.navigation.NavigationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val eventHelper: EventHelper,
    val navigationHelper: NavigationHelper,
    private val userRepository: UserRepository
) : ViewModel() {
    fun checkSession() = viewModelScope.launch {
        userRepository.checkTokenHealth().onSuccess { isExpired ->
            if (!isExpired) navigationHelper.navigate(
                NavigationEvent.To(
                    HomeGraph.HomeRoute,
                    popUpTo = true
                )
            )
            else navigationHelper.navigate(NavigationEvent.To(AuthGraph.LoginRoute, popUpTo = true))
        }.onFailure {
            navigationHelper.navigate(NavigationEvent.To(AuthGraph.LoginRoute, popUpTo = true))
        }
    }
}