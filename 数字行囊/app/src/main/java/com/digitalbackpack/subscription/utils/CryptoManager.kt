package com.digitalbackpack.subscription.utils

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 加密管理器
 * 使用AES-GCM加密算法保护敏感信息
 */
class CryptoManager(context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    private val secretKey: SecretKey
        get() {
            val keyString = encryptedPrefs.getString(KEY_ENCRYPTION_KEY, null)
            return if (keyString != null) {
                val keyBytes = Base64.decode(keyString, Base64.DEFAULT)
                SecretKeySpec(keyBytes, "AES")
            } else {
                generateAndSaveKey()
            }
        }
    
    companion object {
        private const val PREFS_NAME = "crypto_prefs"
        private const val KEY_ENCRYPTION_KEY = "encryption_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH = 128
        private const val IV_LENGTH = 12
    }
    
    /**
     * 生成并保存加密密钥
     */
    private fun generateAndSaveKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256, SecureRandom())
        val key = keyGenerator.generateKey()
        
        val keyString = Base64.encodeToString(key.encoded, Base64.DEFAULT)
        encryptedPrefs.edit().putString(KEY_ENCRYPTION_KEY, keyString).apply()
        
        return key
    }
    
    /**
     * 加密字符串
     * @param plainText 明文
     * @return Base64编码的加密文本（包含IV）
     */
    fun encrypt(plainText: String): String {
        if (plainText.isEmpty()) return plainText
        
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            
            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            
            // 将IV和加密数据组合
            val combined = ByteArray(iv.size + encryptedBytes.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)
            
            return Base64.encodeToString(combined, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            return plainText
        }
    }
    
    /**
     * 解密字符串
     * @param encryptedText Base64编码的加密文本（包含IV）
     * @return 明文
     */
    fun decrypt(encryptedText: String): String {
        if (encryptedText.isEmpty()) return encryptedText
        
        try {
            val combined = Base64.decode(encryptedText, Base64.DEFAULT)
            
            // 分离IV和加密数据
            val iv = ByteArray(IV_LENGTH)
            val encryptedBytes = ByteArray(combined.size - IV_LENGTH)
            System.arraycopy(combined, 0, iv, 0, iv.size)
            System.arraycopy(combined, iv.size, encryptedBytes, 0, encryptedBytes.size)
            
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            return encryptedText
        }
    }
    
    /**
     * 清除所有密钥（危险操作！）
     */
    fun clearKeys() {
        encryptedPrefs.edit().clear().apply()
    }
}

