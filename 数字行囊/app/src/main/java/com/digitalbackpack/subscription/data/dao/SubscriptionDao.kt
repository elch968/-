package com.digitalbackpack.subscription.data.dao

import androidx.room.*
import com.digitalbackpack.subscription.data.model.Subscription
import kotlinx.coroutines.flow.Flow

/**
 * 订阅数据访问对象
 */
@Dao
interface SubscriptionDao {
    
    /**
     * 获取所有订阅（按到期日期排序）
     */
    @Query("SELECT * FROM subscriptions ORDER BY expiryDate ASC")
    fun getAllSubscriptions(): Flow<List<Subscription>>
    
    /**
     * 根据ID获取订阅
     */
    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getSubscriptionById(id: Long): Subscription?
    
    /**
     * 获取即将到期的订阅（N天内）
     */
    @Query("SELECT * FROM subscriptions WHERE expiryDate <= :endTimestamp AND expiryDate >= :startTimestamp AND reminderEnabled = 1 ORDER BY expiryDate ASC")
    suspend fun getUpcomingSubscriptions(startTimestamp: Long, endTimestamp: Long): List<Subscription>
    
    /**
     * 根据分类获取订阅
     */
    @Query("SELECT * FROM subscriptions WHERE category = :category ORDER BY expiryDate ASC")
    fun getSubscriptionsByCategory(category: String): Flow<List<Subscription>>
    
    /**
     * 搜索订阅
     */
    @Query("SELECT * FROM subscriptions WHERE projectName LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%' ORDER BY expiryDate ASC")
    fun searchSubscriptions(query: String): Flow<List<Subscription>>
    
    /**
     * 插入订阅
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: Subscription): Long
    
    /**
     * 更新订阅
     */
    @Update
    suspend fun updateSubscription(subscription: Subscription)
    
    /**
     * 删除订阅
     */
    @Delete
    suspend fun deleteSubscription(subscription: Subscription)
    
    /**
     * 根据ID删除订阅
     */
    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun deleteSubscriptionById(id: Long)
    
    /**
     * 获取所有已过期的订阅
     */
    @Query("SELECT * FROM subscriptions WHERE expiryDate < :currentTimestamp ORDER BY expiryDate DESC")
    fun getExpiredSubscriptions(currentTimestamp: Long): Flow<List<Subscription>>
    
    /**
     * 获取订阅总数
     */
    @Query("SELECT COUNT(*) FROM subscriptions")
    suspend fun getSubscriptionCount(): Int
}

