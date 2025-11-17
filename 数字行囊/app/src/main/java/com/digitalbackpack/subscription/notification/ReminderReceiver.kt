package com.digitalbackpack.subscription.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.digitalbackpack.subscription.SubscriptionApp
import com.digitalbackpack.subscription.ui.MainActivity

/**
 * 提醒通知接收器
 */
class ReminderReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val subscriptionId = intent.getLongExtra(EXTRA_SUBSCRIPTION_ID, -1L)
        val projectName = intent.getStringExtra(EXTRA_PROJECT_NAME) ?: "订阅项目"
        val daysUntilExpiry = intent.getIntExtra(EXTRA_DAYS_UNTIL_EXPIRY, 0)
        
        if (subscriptionId != -1L) {
            showNotification(context, subscriptionId, projectName, daysUntilExpiry)
        }
    }
    
    private fun showNotification(
        context: Context,
        subscriptionId: Long,
        projectName: String,
        daysUntilExpiry: Int
    ) {
        val notificationManager = context.getSystemService<NotificationManager>() ?: return
        
        // 创建点击通知后打开应用的Intent
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            subscriptionId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val title = when {
            daysUntilExpiry == 0 -> "⚠️ 今天到期"
            daysUntilExpiry == 1 -> "⏰ 明天到期"
            else -> "⏰ ${daysUntilExpiry}天后到期"
        }
        
        val content = when {
            daysUntilExpiry == 0 -> "$projectName 今天就要到期了，请及时续费！"
            daysUntilExpiry == 1 -> "$projectName 将在明天到期，别忘了续费哦！"
            else -> "$projectName 将在${daysUntilExpiry}天后到期"
        }
        
        val notification = NotificationCompat.Builder(context, SubscriptionApp.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        notificationManager.notify(subscriptionId.toInt(), notification)
    }
    
    companion object {
        const val EXTRA_SUBSCRIPTION_ID = "extra_subscription_id"
        const val EXTRA_PROJECT_NAME = "extra_project_name"
        const val EXTRA_DAYS_UNTIL_EXPIRY = "extra_days_until_expiry"
    }
}

