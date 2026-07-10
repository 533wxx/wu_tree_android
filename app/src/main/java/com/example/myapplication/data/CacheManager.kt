package com.example.myapplication.data

import android.content.Context
import java.io.File

/**
 * 本地 JSON 缓存管理器。
 * 缓存文件存储在 [Context.cacheDir]，元数据（时间戳）使用 SharedPreferences。
 * 缓存有效期 7 天。
 */
object CacheManager {

    private const val CACHE_FILE_NAME = "family_cache.json"
    private const val PREFS_NAME = "family_cache_prefs"
    private const val KEY_TIMESTAMP = "cache_timestamp"

    /** 缓存有效期：1 小时（毫秒） */
    val CACHE_TTL_MS: Long = 60 * 60 * 1000L

    /**
     * 检查缓存是否在有效期内。
     * @return true 表示缓存文件存在且未过期
     */
    fun isCacheValid(context: Context): Boolean {
        val cacheFile = getCacheFile(context)
        if (!cacheFile.exists()) return false
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val timestamp = prefs.getLong(KEY_TIMESTAMP, -1)
        if (timestamp == -1L) return false
        return (System.currentTimeMillis() - timestamp) < CACHE_TTL_MS
    }

    /**
     * 从缓存读取 JSON 字符串。
     * 仅当缓存有效时返回内容，否则返回 null。
     */
    fun loadFromCache(context: Context): String? {
        return if (isCacheValid(context)) {
            getCacheFile(context).readText()
        } else null
    }

    /**
     * 从缓存读取 JSON 字符串（忽略过期检查）。
     * 用于网络失败后的降级策略。
     */
    fun loadExpiredCache(context: Context): String? {
        val file = getCacheFile(context)
        return if (file.exists()) file.readText() else null
    }

    /**
     * 将 JSON 字符串写入缓存并记录时间戳。
     */
    fun saveToCache(context: Context, json: String) {
        getCacheFile(context).writeText(json)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }

    private fun getCacheFile(context: Context): File {
        return File(context.cacheDir, CACHE_FILE_NAME)
    }
}
