package com.digitalbackpack.subscription.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.digitalbackpack.subscription.data.model.Subscription
import com.digitalbackpack.subscription.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 主页ViewModel
 */
class HomeViewModel(
    private val repository: SubscriptionRepository
) : ViewModel() {
    
    private val _subscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    val subscriptions: StateFlow<List<Subscription>> = _subscriptions.asStateFlow()
    
    private val _upcomingCount = MutableStateFlow(0)
    val upcomingCount: StateFlow<Int> = _upcomingCount.asStateFlow()
    
    init {
        loadSubscriptions()
        loadUpcomingCount()
    }
    
    private fun loadSubscriptions() {
        viewModelScope.launch {
            repository.getAllSubscriptions().collect { list ->
                _subscriptions.value = list
            }
        }
    }
    
    private fun loadUpcomingCount() {
        viewModelScope.launch {
            val upcoming = repository.getUpcomingSubscriptions(daysAhead = 7)
            _upcomingCount.value = upcoming.size
        }
    }
}

class HomeViewModelFactory(
    private val repository: SubscriptionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

