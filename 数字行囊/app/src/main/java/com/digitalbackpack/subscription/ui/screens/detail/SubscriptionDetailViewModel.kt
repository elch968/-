package com.digitalbackpack.subscription.ui.screens.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.digitalbackpack.subscription.data.model.Subscription
import com.digitalbackpack.subscription.data.repository.SubscriptionRepository
import com.digitalbackpack.subscription.notification.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 订阅详情ViewModel
 */
class SubscriptionDetailViewModel(
    application: Application,
    private val repository: SubscriptionRepository,
    private val subscriptionId: Long
) : AndroidViewModel(application) {
    
    private val reminderScheduler = ReminderScheduler(application)
    private val _subscription = MutableStateFlow<Subscription?>(null)
    val subscription: StateFlow<Subscription?> = _subscription.asStateFlow()
    
    fun loadSubscription() {
        viewModelScope.launch {
            _subscription.value = repository.getSubscriptionById(subscriptionId)
        }
    }
    
    fun deleteSubscription() {
        viewModelScope.launch {
            // 取消提醒
            reminderScheduler.cancelReminder(subscriptionId)
            // 删除订阅
            repository.deleteSubscriptionById(subscriptionId)
        }
    }
}

class SubscriptionDetailViewModelFactory(
    private val application: Application,
    private val repository: SubscriptionRepository,
    private val subscriptionId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubscriptionDetailViewModel::class.java)) {
            return SubscriptionDetailViewModel(application, repository, subscriptionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

