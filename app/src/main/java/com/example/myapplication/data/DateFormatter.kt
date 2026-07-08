package com.example.myapplication.data

/**
 * 格式化日期显示：将 "光绪戊寅年十一月三十日亥时" 转为
 * "光绪戊寅年（1878年）十一月三十日亥时"
 */
object DateFormatter {

    fun formatBirthDisplay(person: Person): String? {
        // 优先使用预处理好的 display
        if (!person.birthDisplay.isNullOrBlank()) return person.birthDisplay
        val birth = person.birth ?: return null
        return insertYear(birth, person.birthYear)
    }

    fun formatDeathDisplay(person: Person): String? {
        if (!person.deathDisplay.isNullOrBlank()) return person.deathDisplay
        val death = person.death ?: return null
        return insertYear(death, person.birthYear) // death may reference different year, best effort
    }

    fun formatWifeDisplay(person: Person): String? {
        if (!person.wifeDisplay.isNullOrBlank()) return person.wifeDisplay
        return person.wife
    }

    private fun insertYear(text: String, year: String?): String {
        if (year.isNullOrBlank()) return text

        // Pattern: 公元YYYY年... → already has year
        if (text.startsWith("公元") && text.contains(year)) return text

        // Find first "年" after era+ganzhi
        // e.g. "光绪戊寅年十一月..." → insert after first 年
        val firstNian = text.indexOf('年')
        if (firstNian < 0) return text

        val before = text.substring(0, firstNian + 1) // includes 年
        val after = text.substring(firstNian + 1)

        return "$before（${year}年）$after"
    }
}
