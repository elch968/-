package com.digitalbackpack.subscription.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 订阅项目实体类
 */
@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // 项目名称
    val projectName: String,
    
    // 项目网站URL
    val websiteUrl: String = "",
    
    // 账号（加密存储）
    val username: String,
    
    // 密码（加密存储）
    val password: String,
    
    // 到期日期
    val expiryDate: Long,
    
    // 订阅价格
    val price: Double = 0.0,
    
    // 货币单位
    val currency: String = "CNY",
    
    // 续费周期（天）
    val renewalPeriodDays: Int = 30,
    
    // 提醒天数（默认提前1天）
    val reminderDaysBefore: Int = 1,
    
    // 备注
    val notes: String = "",
    
    // 是否启用提醒
    val reminderEnabled: Boolean = true,
    
    // 创建时间
    val createdAt: Long = System.currentTimeMillis(),
    
    // 最后更新时间
    val updatedAt: Long = System.currentTimeMillis(),
    
    // 订阅类型/分类
    val category: String = "其他"
)

/**
 * 订阅分类
 */
object SubscriptionCategory {
    const val STREAMING = "流媒体"
    const val SOFTWARE = "软件服务"
    const val CLOUD_STORAGE = "云存储"
    const val VPN = "VPN"
    const val MUSIC = "音乐"
    const val EDUCATION = "教育"
    const val GAMING = "游戏"
    const val NEWS = "新闻资讯"
    const val OTHER = "其他"
    
    fun getAllCategories() = listOf(
        STREAMING, SOFTWARE, CLOUD_STORAGE, VPN, 
        MUSIC, EDUCATION, GAMING, NEWS, OTHER
    )
}

