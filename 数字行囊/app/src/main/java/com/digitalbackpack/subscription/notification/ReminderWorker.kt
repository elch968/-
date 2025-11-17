package com.digitalbackpack.subscription.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.digitalbackpack.subscription.SubscriptionApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 定期检查订阅并发送提醒的Worker
 * 使用WorkManager实现每日检查
 */
class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val app = applicationContext as SubscriptionApp
            val repository = app.repository
            val scheduler = ReminderScheduler(applicationContext)
            
            // 获取所有启用提醒的订阅
            val allSubscriptions = mutableListOf<com.digitalbackpack.subscription.data.model.Subscription>()
            repository.getAllSubscriptions().collect { subscriptions ->
                allSubscriptions.clear()
                allSubscriptions.addAll(subscriptions.filter { it.reminderEnabled })
            }
            
            // 为所有订阅重新安排提醒
            scheduler.rescheduleAllReminders(allSubscriptions)
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}

