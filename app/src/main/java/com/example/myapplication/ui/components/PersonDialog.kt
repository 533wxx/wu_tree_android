package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.data.DateFormatter
import com.example.myapplication.data.Person
import com.example.myapplication.ui.theme.WuAccent
import com.example.myapplication.ui.theme.WuAccentDim
import com.example.myapplication.ui.theme.WuBorder
import com.example.myapplication.ui.theme.WuSurface
import com.example.myapplication.ui.theme.WuSurface2
import com.example.myapplication.ui.theme.WuText
import com.example.myapplication.ui.theme.WuTextDim
import com.example.myapplication.ui.theme.WuTextMuted

@Composable
fun PersonDialog(
    person: Person?,
    onDismiss: () -> Unit,
    onNavigateToPerson: (String) -> Unit
) {
    if (person == null) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(6.dp),
            color = WuSurface
        ) {
            Column {
                // Sticky header
                Row(
                    modifier = Modifier
                        .background(WuSurface)
                        .padding(20.dp, 24.dp, 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        person.name,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = WuAccent
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = onDismiss) {
                        Text("×", fontSize = 40.sp, color = WuTextDim)
                    }
                }
                HorizontalDivider(color = WuBorder)

                // Scrollable body
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp, 24.dp)
                ) {
                    // Section: 基本信息
                    SectionTitle("基 本 信 息")
                    person.gen?.let { DetailRow("世代", "${it}世") }
                    person.birthOrder?.let { DetailRow("排行", it) }
                    person.zi?.let { DetailRow("字", it) }
                    person.hao?.let { DetailRow("号", it) }
                    DateFormatter.formatBirthDisplay(person)?.let { DetailRow("生于", it) }
                    DateFormatter.formatWifeDisplay(person)?.let { DetailRow("配偶", it) }
                    DateFormatter.formatDeathDisplay(person)?.let { DetailRow("歿葬", it) }
                    person.note?.let { DetailRow("备注", it) }
                    person.adoptNote?.let { DetailRow("过继", it) }
                    person.daughters?.let { DetailRow("女儿", it.joinToString("、")) }

                    // Section: 子嗣
                    if (!person.children.isNullOrEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        SectionTitle("子 嗣（${person.children.size}人）")
                        person.children.forEach { child ->
                            ChildItem(child) {
                                onDismiss()
                                onNavigateToPerson(child.id)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        fontSize = 13.sp,
        color = WuAccentDim,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    HorizontalDivider(color = WuBorder, modifier = Modifier.padding(bottom = 10.dp))
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 5.dp)) {
        Text(
            label,
            fontSize = 14.sp,
            color = WuTextDim,
            modifier = Modifier.width(50.dp)
        )
        Text(
            value,
            fontSize = 14.sp,
            color = WuText,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ChildItem(child: Person, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp, 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    child.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WuText
                )
                child.zi?.let {
                    Text(
                        " · 字$it",
                        fontSize = 13.sp,
                        color = WuTextMuted
                    )
                }
                child.note?.let {
                    Text(
                        " · $it",
                        fontSize = 13.sp,
                        color = WuTextMuted
                    )
                }
            }
        }
        child.gen?.let {
            Text("${it}世", fontSize = 12.sp, color = WuTextDim)
        }
    }
}
