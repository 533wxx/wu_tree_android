package com.example.myapplication.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * 族谱数据仓库。
 * 加载策略：有效缓存 → 网络获取并更新缓存 → 过期缓存兜底 → 内置 asset 兜底。
 */
object FamilyRepository {

    private const val REMOTE_URL =
        "https://raw.githubusercontent.com/533wxx/wu_tree_db/main/contents/collections/familyData.json"

    private val gson = Gson()
    private val dataType = object : TypeToken<List<FamilyData>>() {}.type

    /**
     * 加载族谱数据。
     * 在 [Dispatchers.IO] 上执行，适配 Compose ViewModel 的 viewModelScope。
     */
    /**
     * 始终从本地缓存文件加载数据。
     * 若缓存文件不存在，从 asset 初始化种子数据。
     */
    suspend fun loadDataFast(context: Context): Result<List<FamilyData>> =
        withContext(Dispatchers.IO) {
            try {
                // 始终读取缓存文件（即使已过期）
                val json = CacheManager.loadExpiredCache(context)
                    ?: run {
                        // 缓存文件不存在 → 从 asset 初始化种子
                        val seedJson = context.assets.open("family_data.json")
                            .bufferedReader().use(BufferedReader::readText)
                        CacheManager.saveToCache(context, seedJson)
                        seedJson
                    }
                val data = gson.fromJson<List<FamilyData>>(json, dataType)
                Result.success(data)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * 判断缓存是否已过期，用于决定是否需要后台刷新。
     */
    fun isCacheExpired(context: Context): Boolean = !CacheManager.isCacheValid(context)

    /**
     * 后台静默刷新：从网络拉取最新数据并写入缓存。
     * 失败不抛异常，静默忽略。
     */
    suspend fun refreshCacheInBackground(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                val json = fetchRemoteJson()
                val data = gson.fromJson<List<FamilyData>>(json, dataType)
                CacheManager.saveToCache(context, json)
            } catch (_: Exception) {
                // 静默忽略
            }
        }
    }

    private fun fetchRemoteJson(): String {
        val url = URL(REMOTE_URL)
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 15_000
        connection.readTimeout = 30_000
        connection.requestMethod = "GET"

        return try {
            val code = connection.responseCode
            if (code != HttpURLConnection.HTTP_OK) {
                throw Exception("HTTP $code: ${connection.responseMessage}")
            }
            BufferedReader(InputStreamReader(connection.inputStream, "UTF-8"))
                .use(BufferedReader::readText)
        } finally {
            connection.disconnect()
        }
    }
}
