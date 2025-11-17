package com.digitalbackpack.subscription.data.repository

import com.digitalbackpack.subscription.data.dao.SubscriptionDao
import com.digitalbackpack.subscription.data.model.Subscription
import com.digitalbackpack.subscription.utils.CryptoManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 订阅仓库
 * 负责数据访问和加密/解密逻辑
 */
class SubscriptionRepository(
    private val subscriptionDao: SubscriptionDao,
    private val cryptoManager: CryptoManager
) {
    
    /**
     * 获取所有订阅（自动解密）
     */
    fun getAllSubscriptions(): Flow<List<Subscription>> {
        return subscriptionDao.getAllSubscriptions().map { subscriptions ->
            subscriptions.map { decryptSubscription(it) }
        }
    }
    
    /**
     * 根据ID获取订阅（自动解密）
     */
    suspend fun getSubscriptionById(id: Long): Subscription? {
        return subscriptionDao.getSubscriptionById(id)?.let { decryptSubscription(it) }
    }
    
    /**
     * 获取即将到期的订阅
     */
    suspend fun getUpcomingSubscriptions(daysAhead: Int = 7): List<Subscription> {
        val currentTime = System.currentTimeMillis()
        val endTime = currentTime + (daysAhead * 24 * 60 * 60 * 1000L)
        return subscriptionDao.getUpcomingSubscriptions(currentTime, endTime)
            .map { decryptSubscription(it) }
    }
    
    /**
     * 根据分类获取订阅
     */
    fun getSubscriptionsByCategory(category: String): Flow<List<Subscription>> {
        return subscriptionDao.getSubscriptionsByCategory(category).map { subscriptions ->
            subscriptions.map { decryptSubscription(it) }
        }
    }
    
    /**
     * 搜索订阅
     */
    fun searchSubscriptions(query: String): Flow<List<Subscription>> {
        return subscriptionDao.searchSubscriptions(query).map { subscriptions ->
            subscriptions.map { decryptSubscription(it) }
        }
    }
    
    /**
     * 插入订阅（自动加密）
     */
    suspend fun insertSubscription(subscription: Subscription): Long {
        val encryptedSubscription = encryptSubscription(subscription)
        return subscriptionDao.insertSubscription(encryptedSubscription)
    }
    
    /**
     * 更新订阅（自动加密）
     */
    suspend fun updateSubscription(subscription: Subscription) {
        val encryptedSubscription = encryptSubscription(
            subscription.copy(updatedAt = System.currentTimeMillis())
        )
        subscriptionDao.updateSubscription(encryptedSubscription)
    }
    
    /**
     * 删除订阅
     */
    suspend fun deleteSubscription(subscription: Subscription) {
        subscriptionDao.deleteSubscription(subscription)
    }
    
    /**
     * 根据ID删除订阅
     */
    suspend fun deleteSubscriptionById(id: Long) {
        subscriptionDao.deleteSubscriptionById(id)
    }
    
    /**
     * 获取已过期的订阅
     */
    fun getExpiredSubscriptions(): Flow<List<Subscription>> {
        val currentTime = System.currentTimeMillis()
        return subscriptionDao.getExpiredSubscriptions(currentTime).map { subscriptions ->
            subscriptions.map { decryptSubscription(it) }
        }
    }
    
    /**
     * 获取订阅总数
     */
    suspend fun getSubscriptionCount(): Int {
        return subscriptionDao.getSubscriptionCount()
    }
    
    /**
     * 加密订阅中的敏感信息
     */
    private fun encryptSubscription(subscription: Subscription): Subscription {
        return subscription.copy(
            username = cryptoManager.encrypt(subscription.username),
            password = cryptoManager.encrypt(subscription.password)
        )
    }
    
    /**
     * 解密订阅中的敏感信息
     */
    private fun decryptSubscription(subscription: Subscription): Subscription {
        return try {
            subscription.copy(
                username = cryptoManager.decrypt(subscription.username),
                password = cryptoManager.decrypt(subscription.password)
            )
        } catch (e: Exception) {
            // 如果解密失败，返回原始数据
            subscription
        }
    }
}

