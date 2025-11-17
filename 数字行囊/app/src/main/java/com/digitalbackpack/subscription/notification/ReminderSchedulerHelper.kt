package com.digitalbackpack.subscription.notification

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * 提醒调度助手
 * 使用WorkManager安排定期检查任务
 */
object ReminderSchedulerHelper {
    
    private const val WORK_NAME = "subscription_reminder_work"
    
    /**
     * 开始定期检查任务
     * 每天检查一次所有订阅
     */
    fun startPeriodicReminderCheck(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
        
        val reminderWork = PeriodicWorkRequestBuilder<ReminderWorker>(
            1, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.HOURS) // 首次延迟1小时后执行
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            reminderWork
        )
    }
    
    /**
     * 立即执行一次提醒检查
     */
    fun runImmediateCheck(context: Context) {
        val immediateWork = OneTimeWorkRequestBuilder<ReminderWorker>()
            .build()
        
        WorkManager.getInstance(context).enqueue(immediateWork)
    }
    
    /**
     * 取消所有提醒检查
     */
    fun cancelAllReminders(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}

