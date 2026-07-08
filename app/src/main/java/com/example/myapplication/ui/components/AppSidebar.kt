package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.BranchInfo
import com.example.myapplication.ui.theme.WuAccent
import com.example.myapplication.ui.theme.WuAccentDim
import com.example.myapplication.ui.theme.WuBg
import com.example.myapplication.ui.theme.WuBorder
import com.example.myapplication.ui.theme.WuSurface
import com.example.myapplication.ui.theme.WuSurface2
import com.example.myapplication.ui.theme.WuSurface3
import com.example.myapplication.ui.theme.WuText
import com.example.myapplication.ui.theme.WuTextDim
import com.example.myapplication.ui.theme.WuTextMuted

@Composable
fun AppSidebar(
    branches: List<BranchInfo>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(200.dp)
            .fillMaxHeight()
            .background(WuSurface)
            .border(1.dp, WuBorder)
            .padding(top = 18.dp, bottom = 18.dp)
    ) {
        Text(
            "房 支 导 航",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = WuAccent,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            letterSpacing = 2.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
        )

        LazyColumn(
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            itemsIndexed(branches) { index, info ->
                val isActive = index == selectedIndex
                val bgColor = if (isActive) WuSurface2 else Color.Transparent
                val borderColor = if (isActive) WuAccentDim else Color.Transparent
                val textColor = if (isActive) WuAccent else WuTextMuted
                val badgeBg = if (isActive) WuAccentDim else WuSurface3
                val badgeText = if (isActive) WuBg else WuTextDim

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.dp)
                        .background(bgColor, RoundedCornerShape(6.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(6.dp))
                        .clickable { onSelect(index) }
                        .padding(10.dp, 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        info.name,
                        fontSize = 14.sp,
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                        color = textColor
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        "${info.count}人",
                        fontSize = 11.sp,
                        color = badgeText,
                        modifier = Modifier
                            .background(badgeBg, RoundedCornerShape(3.dp))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SidebarOverlay(visible: Boolean, onClick: () -> Unit) {
    if (visible) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable { onClick() }
        )
    }
}
