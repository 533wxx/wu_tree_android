package com.example.myapplication.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.DateFormatter
import com.example.myapplication.data.Person
import com.example.myapplication.ui.theme.WuAccent
import com.example.myapplication.ui.theme.WuAccentBg
import com.example.myapplication.ui.theme.WuAccentDim
import com.example.myapplication.ui.theme.WuBlue
import com.example.myapplication.ui.theme.WuBlueBg
import com.example.myapplication.ui.theme.WuBorder
import com.example.myapplication.ui.theme.WuRed
import com.example.myapplication.ui.theme.WuRedBg
import com.example.myapplication.ui.theme.WuSurface
import com.example.myapplication.ui.theme.WuSurface2
import com.example.myapplication.ui.theme.WuSurface3
import com.example.myapplication.ui.theme.WuText
import com.example.myapplication.ui.theme.WuTextDim
import kotlinx.coroutines.delay as coroutineDelay
import com.example.myapplication.ui.theme.WuTextMuted

@Composable
fun TreeNodeCard(
    person: Person,
    depth: Int = 0,
    delay: Int = 0,
    navigateToPersonId: String?,
    onPersonClick: (Person) -> Unit,
    scrollState: ScrollState? = null,
    containerRootY: Float = 0f
) {
    var isOpen by remember { mutableStateOf(false) }
    val hasChildren = person.children?.isNotEmpty() == true

    // Auto-expand for ancestors
    LaunchedEffect(navigateToPersonId) {
        if (navigateToPersonId != null) {
            if (isAncestorOf(person, navigateToPersonId) ||
                person.children?.any { it.id == navigateToPersonId } == true) {
                isOpen = true
            }
        }
    }

    val isHighlighted = person.id == navigateToPersonId
    val cardBorder by animateColorAsState(
        if (isHighlighted) WuAccent else WuBorder,
        animationSpec = tween(300)
    )

    // 高亮卡片自动滚动到视口中央
    var cardY by remember { mutableStateOf(0f) }
    LaunchedEffect(isHighlighted) {
        if (isHighlighted && scrollState != null && cardY > 0f) {
            coroutineDelay(400) // 等待展开动画完成
            val relative = cardY - containerRootY
            val target = (relative - 300f).coerceIn(0f, scrollState.maxValue.toFloat())
            scrollState.animateScrollTo(target.toInt())
        }
    }

    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .onGloballyPositioned { coords ->
                    cardY = coords.positionInRoot().y
                }
                .clickable { onPersonClick(person) },
            shape = RoundedCornerShape(6.dp),
            colors = CardDefaults.cardColors(containerColor = WuSurface),
            border = BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(10.dp, 12.dp)) {
                // Top row: name + badges + gen
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        person.name,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = WuText
                    )
                    Spacer(Modifier.width(6.dp))
                    person.birthOrder?.let {
                        Text(
                            it,
                            fontSize = 11.sp,
                            color = WuAccent,
                            modifier = Modifier
                                .background(WuAccentBg, RoundedCornerShape(3.dp))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }
                    if (person.note == "幼歿") {
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "幼歿",
                            fontSize = 11.sp,
                            color = WuRed,
                            modifier = Modifier
                                .background(WuRedBg, RoundedCornerShape(3.dp))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    person.gen?.let {
                        Text(
                            "${it}世",
                            fontSize = 11.sp,
                            color = WuTextDim,
                            modifier = Modifier
                                .background(WuSurface3, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 1.dp)
                        )
                    }
                }

                // Zi / Hao
                val ziHao = buildString {
                    person.zi?.let { append("字$it") }
                    person.hao?.let { append(" 号$it") }
                }
                if (ziHao.isNotEmpty()) {
                    Text(
                        ziHao,
                        fontSize = 12.sp,
                        color = WuTextMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Details
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    DateFormatter.formatBirthDisplay(person)?.let {
                        Text("生于 $it", fontSize = 12.sp, color = WuText, lineHeight = 18.sp)
                    }
                    DateFormatter.formatWifeDisplay(person)?.let {
                        Text(it, fontSize = 12.sp, color = WuAccentDim, lineHeight = 18.sp)
                    }
                    person.adoptNote?.let {
                        Text(
                            it,
                            fontSize = 10.sp,
                            color = WuBlue,
                            modifier = Modifier
                                .background(WuBlueBg, RoundedCornerShape(3.dp))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }
                    DateFormatter.formatDeathDisplay(person)?.let {
                        Text(it, fontSize = 12.sp, color = WuTextDim, lineHeight = 18.sp)
                    }
                }

                // Expand toggle
                if (hasChildren) {
                    val count = person.children!!.size
                    val borderColor = if (isOpen) WuAccent else WuAccentDim
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .background(
                                if (isOpen) WuSurface3 else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            )
                            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
                            .clickable { isOpen = !isOpen }
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (isOpen) "▾" else "▸",
                            fontSize = 13.sp,
                            color = if (isOpen) WuAccent else WuAccentDim
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "$count 子嗣${if (isOpen) "收起" else "展开"}",
                            fontSize = 13.sp,
                            color = if (isOpen) WuAccent else WuAccentDim
                        )
                    }
                }
            }
        }

        // Children
        if (hasChildren) {
            AnimatedVisibility(visible = isOpen) {
                Column(
                    modifier = Modifier
                        .padding(start = 24.dp)
                        .drawBehind {
                            val lineX = 12.dp.toPx()
                            drawLine(
                                color = WuBorder,
                                start = Offset(lineX, 0f),
                                end = Offset(lineX, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                ) {
                    Text(
                        "${person.children!![0].gen}世 · ${person.children!!.size}人",
                        fontSize = 11.sp,
                        color = WuTextDim,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    person.children!!.forEachIndexed { i, child ->
                        TreeNodeCard(
                            person = child,
                            depth = depth + 1,
                            delay = i + depth * 3,
                            navigateToPersonId = navigateToPersonId,
                            onPersonClick = onPersonClick,
                            scrollState = scrollState,
                            containerRootY = containerRootY
                        )
                    }
                }
            }
        }
    }
}

private fun isAncestorOf(person: Person, targetId: String): Boolean {
    if (person.children == null) return false
    return person.children.any { child ->
        child.id == targetId || isAncestorOf(child, targetId)
    }
}
