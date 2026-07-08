package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.WuAccent
import com.example.myapplication.ui.theme.WuBorder
import com.example.myapplication.ui.theme.WuSurface
import com.example.myapplication.ui.theme.WuText
import com.example.myapplication.ui.theme.WuTextDim

@Composable
fun SearchBox(
    query: String,
    onQueryChange: (String) -> Unit,
    onPositionChanged: (left: Float, bottom: Float, width: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            textStyle = TextStyle(fontSize = 13.sp, color = WuText),
            cursorBrush = SolidColor(WuAccent),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .background(WuSurface, RoundedCornerShape(6.dp))
                        .border(1.dp, WuBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    if (query.isEmpty()) {
                        Text("搜索姓名、字号…", fontSize = 13.sp, color = WuTextDim)
                    }
                    innerTextField()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coords ->
                    val pos = coords.positionInWindow()
                    onPositionChanged(pos.x, pos.y + coords.size.height, coords.size.width.toFloat())
                }
        )

        Text(
            "⌕", fontSize = 22.sp, color = WuTextDim,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 10.dp)
        )
    }
}
