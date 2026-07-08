package com.example.myapplication.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.WuAccent
import com.example.myapplication.ui.theme.WuBg
import com.example.myapplication.ui.theme.WuBorderLight
import com.example.myapplication.ui.theme.WuTextMuted

@Composable
fun AppTopBar(
    sidebarOpen: Boolean,
    searchQuery: String,
    onToggleSidebar: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchPositionChanged: (left: Float, bottom: Float, width: Float) -> Unit,
    showHamburger: Boolean = true,
    modifier: Modifier = Modifier
) {
    val topRotate by animateFloatAsState(if (sidebarOpen) 45f else 0f)
    val midAlpha by animateFloatAsState(if (sidebarOpen) 0f else 1f)
    val botRotate by animateFloatAsState(if (sidebarOpen) -45f else 0f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(WuBg)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Row 1: Title + Subtitle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "高橋吳氏宗譜",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = WuAccent,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                lineHeight = 24.sp
            )
            Text(
                "卷十三 · 世系 · 二〇二六年丙午岁重修",
                fontSize = 11.sp,
                color = WuTextMuted,
                fontWeight = FontWeight.Light
            )
        }

        // Row 2: Hamburger + Search
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showHamburger) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(32.dp)
                        .background(WuBorderLight.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .clickable { onToggleSidebar() }
                        .padding(5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.width(16.dp).height(1.5.dp).rotate(topRotate).background(WuTextMuted))
                        Box(Modifier.width(16.dp).height(1.5.dp).background(WuTextMuted.copy(alpha = midAlpha)))
                        Box(Modifier.width(16.dp).height(1.5.dp).rotate(botRotate).background(WuTextMuted))
                    }
                }
            }

            SearchBox(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onPositionChanged = onSearchPositionChanged,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
