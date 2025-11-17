package com.digitalbackpack.subscription.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.getSystemService
import com.digitalbackpack.subscription.data.model.Subscription
import java.util.Calendar

/**
 * 提醒调度器
 * 负责安排和取消订阅到期提醒
 */
class ReminderScheduler(private val context: Context) {
    
    private val alarmManager = context.getSystemService<AlarmManager>()
    
    /**
     * 为订阅安排提醒
     */
    fun scheduleReminder(subscription: Subscription) {
        if (!subscription.reminderEnabled) return
        
        val reminderTime = subscription.expiryDate - (subscription.reminderDaysBefore * 24 * 60 * 60 * 1000L)
        
        // 如果提醒时间已经过去，不安排提醒
        if (reminderTime <= System.currentTimeMillis()) return
        
        val intent = createReminderIntent(subscription)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            subscription.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        alarmManager?.let { manager ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 使用精确的闹钟（需要权限）
                if (manager.canScheduleExactAlarms()) {
                    manager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime,
                        pendingIntent
                    )
                } else {
                    // 如果没有精确闹钟权限，使用普通闹钟
                    manager.set(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime,
                        pendingIntent
                    )
                }
            } else {
                manager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime,
                    pendingIntent
                )
            }
        }
    }
    
    /**
     * 取消订阅的提醒
     */
    fun cancelReminder(subscriptionId: Long) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            subscriptionId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        
        pendingIntent?.let {
            alarmManager?.cancel(it)
            it.cancel()
        }
    }
    
    /**
     * 更新订阅的提醒
     */
    fun updateReminder(subscription: Subscription) {
        cancelReminder(subscription.id)
        scheduleReminder(subscription)
    }
    
    /**
     * 创建提醒Intent
     */
    private fun createReminderIntent(subscription: Subscription): Intent {
        val daysUntilExpiry = ((subscription.expiryDate - System.currentTimeMillis()) / (24 * 60 * 60 * 1000L)).toInt()
        
        return Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_SUBSCRIPTION_ID, subscription.id)
            putExtra(ReminderReceiver.EXTRA_PROJECT_NAME, subscription.projectName)
            putExtra(ReminderReceiver.EXTRA_DAYS_UNTIL_EXPIRY, daysUntilExpiry)
        }
    }
    
    /**
     * 为所有订阅重新安排提醒
     * （通常在应用启动或更新后调用）
     */
    suspend fun rescheduleAllReminders(subscriptions: List<Subscription>) {
        subscriptions.forEach { subscription ->
            scheduleReminder(subscription)
        }
    }
}

