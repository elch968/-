package com.digitalbackpack.subscription.ui.screens.add_edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.digitalbackpack.subscription.data.model.Subscription
import com.digitalbackpack.subscription.data.model.SubscriptionCategory
import com.digitalbackpack.subscription.data.repository.SubscriptionRepository
import com.digitalbackpack.subscription.notification.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 添加/编辑订阅ViewModel
 */
class AddEditViewModel(
    application: Application,
    private val repository: SubscriptionRepository,
    private val subscriptionId: Long
) : AndroidViewModel(application) {
    
    private val reminderScheduler = ReminderScheduler(application)
    
    private val _uiState = MutableStateFlow(SubscriptionUiState())
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()
    
    fun loadSubscription(id: Long) {
        viewModelScope.launch {
            repository.getSubscriptionById(id)?.let { subscription ->
                _uiState.value = SubscriptionUiState(
                    id = subscription.id,
                    projectName = subscription.projectName,
                    websiteUrl = subscription.websiteUrl,
                    username = subscription.username,
                    password = subscription.password,
                    expiryDate = subscription.expiryDate,
                    price = subscription.price,
                    currency = subscription.currency,
                    renewalPeriodDays = subscription.renewalPeriodDays,
                    reminderDaysBefore = subscription.reminderDaysBefore,
                    notes = subscription.notes,
                    reminderEnabled = subscription.reminderEnabled,
                    category = subscription.category
                )
            }
        }
    }
    
    fun updateProjectName(name: String) {
        _uiState.value = _uiState.value.copy(projectName = name)
    }
    
    fun updateCategory(category: String) {
        _uiState.value = _uiState.value.copy(category = category)
    }
    
    fun updateWebsiteUrl(url: String) {
        _uiState.value = _uiState.value.copy(websiteUrl = url)
    }
    
    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }
    
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }
    
    fun updateExpiryDate(date: Long) {
        _uiState.value = _uiState.value.copy(expiryDate = date)
    }
    
    fun updatePrice(price: Double) {
        _uiState.value = _uiState.value.copy(price = price)
    }
    
    fun updateCurrency(currency: String) {
        _uiState.value = _uiState.value.copy(currency = currency)
    }
    
    fun updateRenewalPeriod(days: Int) {
        _uiState.value = _uiState.value.copy(renewalPeriodDays = days)
    }
    
    fun updateReminderDays(days: Int) {
        _uiState.value = _uiState.value.copy(reminderDaysBefore = days)
    }
    
    fun updateReminderEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(reminderEnabled = enabled)
    }
    
    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }
    
    fun saveSubscription() {
        viewModelScope.launch {
            val state = _uiState.value
            val subscription = Subscription(
                id = state.id,
                projectName = state.projectName,
                websiteUrl = state.websiteUrl,
                username = state.username,
                password = state.password,
                expiryDate = state.expiryDate,
                price = state.price,
                currency = state.currency,
                renewalPeriodDays = state.renewalPeriodDays,
                reminderDaysBefore = state.reminderDaysBefore,
                notes = state.notes,
                reminderEnabled = state.reminderEnabled,
                category = state.category
            )
            
            if (subscriptionId == -1L) {
                val newId = repository.insertSubscription(subscription)
                // 为新订阅安排提醒
                reminderScheduler.scheduleReminder(subscription.copy(id = newId))
            } else {
                repository.updateSubscription(subscription)
                // 更新提醒
                reminderScheduler.updateReminder(subscription)
            }
        }
    }
}

/**
 * 订阅UI状态
 */
data class SubscriptionUiState(
    val id: Long = 0,
    val projectName: String = "",
    val websiteUrl: String = "",
    val username: String = "",
    val password: String = "",
    val expiryDate: Long = System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000L), // 默认30天后
    val price: Double = 0.0,
    val currency: String = "CNY",
    val renewalPeriodDays: Int = 30,
    val reminderDaysBefore: Int = 1,
    val notes: String = "",
    val reminderEnabled: Boolean = true,
    val category: String = SubscriptionCategory.OTHER
)

class AddEditViewModelFactory(
    private val application: Application,
    private val repository: SubscriptionRepository,
    private val subscriptionId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditViewModel::class.java)) {
            return AddEditViewModel(application, repository, subscriptionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

