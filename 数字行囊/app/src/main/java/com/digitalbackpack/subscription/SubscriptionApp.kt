package com.digitalbackpack.subscription

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.getSystemService
import com.digitalbackpack.subscription.data.database.AppDatabase
import com.digitalbackpack.subscription.data.repository.SubscriptionRepository
import com.digitalbackpack.subscription.notification.ReminderSchedulerHelper
import com.digitalbackpack.subscription.utils.CryptoManager

/**
 * 应用程序类
 */
class SubscriptionApp : Application() {
    
    lateinit var database: AppDatabase
        private set
    
    lateinit var repository: SubscriptionRepository
        private set
    
    lateinit var cryptoManager: CryptoManager
        private set
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化数据库
        database = AppDatabase.getDatabase(this)
        
        // 初始化加密管理器
        cryptoManager = CryptoManager(this)
        
        // 初始化仓库
        repository = SubscriptionRepository(
            database.subscriptionDao(),
            cryptoManager
        )
        
        // 创建通知渠道
        createNotificationChannel()
        
        // 启动定期提醒检查
        ReminderSchedulerHelper.startPeriodicReminderCheck(this)
    }
    
    /**
     * 创建通知渠道（Android 8.0+）
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }
            
            val notificationManager = getSystemService<NotificationManager>()
            notificationManager?.createNotificationChannel(channel)
        }
    }
    
    companion object {
        const val CHANNEL_ID = "subscription_reminders"
        const val CHANNEL_NAME = "订阅提醒"
        const val CHANNEL_DESCRIPTION = "订阅到期提醒通知"
    }
}

