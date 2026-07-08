package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.WuAccent
import com.example.myapplication.ui.theme.WuAccentDim
import com.example.myapplication.ui.theme.WuTextMuted

@Composable
fun Breadcrumb(branchName: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("高橋吳氏", fontSize = 13.sp, color = WuTextMuted)
        Text("›", fontSize = 10.sp, color = WuAccentDim)
        Text("卷十三 世系", fontSize = 13.sp, color = WuTextMuted)
        Text("›", fontSize = 10.sp, color = WuAccentDim)
        Text(
            "${branchName}房",
            fontSize = 13.sp,
            color = WuAccent,
            fontWeight = FontWeight.SemiBold
        )
    }
}
