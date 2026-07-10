package com.example.myapplication.data

/**
 * 格式化日期显示：将 "光绪戊寅年十一月三十日亥时" 转为
 * "光绪戊寅年（1878年）十一月三十日亥时"
 */
object DateFormatter {

    fun formatBirthDisplay(person: Person): String? {
        return person.birth
    }

    fun formatDeathDisplay(person: Person): String? {
        if (!person.deathDisplay.isNullOrBlank()) return person.deathDisplay
        return person.death
    }

    fun formatWifeDisplay(person: Person): String? {
        if (!person.wifeDisplay.isNullOrBlank()) return person.wifeDisplay
        return person.wife
    }

    private const val TAG = "DateFormatter"
}
