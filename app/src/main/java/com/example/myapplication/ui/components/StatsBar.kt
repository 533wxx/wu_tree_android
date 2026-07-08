package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.FamilyData
import com.example.myapplication.data.Person
import com.example.myapplication.ui.theme.WuAccent
import com.example.myapplication.ui.theme.WuBorder
import com.example.myapplication.ui.theme.WuSurface
import com.example.myapplication.ui.theme.WuTextDim

@Composable
fun StatsBar(branch: FamilyData, modifier: Modifier = Modifier) {
    val stats = remember(branch) {
        val seen = mutableSetOf<String>()
        var total = 0
        var maxGen = 0
        fun count(p: Person) {
            if (seen.add(p.id)) {
                total++
                if ((p.gen ?: 0) > maxGen) maxGen = p.gen ?: 0
                p.children?.forEach { count(it) }
            }
        }
        count(branch.root)
        branch.siblings?.forEach { count(it) }
        listOf(
            "房支" to branch.branch,
            "总人数" to "${total}人",
            "世代跨度" to "${maxGen}世"
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        stats.forEach { (label, value) ->
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = WuSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, WuBorder)
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        label,
                        fontSize = 10.sp,
                        color = WuTextDim,
                        letterSpacing = 1.sp
                    )
                    Text(
                        value,
                        fontSize = 15.sp,
                        color = WuAccent,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
