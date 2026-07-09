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
    suspend fun loadData(context: Context): Result<List<FamilyData>> =
        withContext(Dispatchers.IO) {
            try {
                // 1. 检查有效缓存（< 7 天）
                CacheManager.loadFromCache(context)?.let { json ->
                    val data = gson.fromJson<List<FamilyData>>(json, dataType)
                    return@withContext Result.success(data)
                }

                // 2. 缓存过期或不存在 → 网络获取
                val json = fetchRemoteJson()
                val data = gson.fromJson<List<FamilyData>>(json, dataType)
                CacheManager.saveToCache(context, json)
                Result.success(data)
            } catch (e: Exception) {
                // 3. 网络失败 → 依次降级
                e.printStackTrace()
                try {
                    // 3a. 尝试过期缓存
                    CacheManager.loadExpiredCache(context)?.let { json ->
                        val data = gson.fromJson<List<FamilyData>>(json, dataType)
                        return@withContext Result.success(data)
                    }

                    // 3b. 尝试内置 asset
                    val assetJson = context.assets.open("family_data.json")
                        .bufferedReader().use(BufferedReader::readText)
                    val data = gson.fromJson<List<FamilyData>>(assetJson, dataType)
                    Result.success(data)
                } catch (fallbackError: Exception) {
                    Result.failure(fallbackError)
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
