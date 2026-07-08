package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.FamilyData
import com.example.myapplication.data.Person
import com.example.myapplication.ui.theme.WuAccent
import com.example.myapplication.ui.theme.WuTextMuted

@Composable
fun TreeView(
    branch: FamilyData,
    navigateToPersonId: String?,
    onPersonClick: (Person) -> Unit,
    modifier: Modifier = Modifier
) {
    val roots = listOf(branch.root) + (branch.siblings ?: emptyList())
    val scrollState = rememberScrollState()
    var containerRootY by remember { mutableStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .onGloballyPositioned { containerRootY = it.positionInRoot().y }
    ) {
        roots.forEach { root ->
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                if (roots.size > 1) {
                    Row(modifier = Modifier.padding(bottom = 10.dp)) {
                        Text(
                            " ${root.name} ",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WuAccent,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                        )
                        Text(" · ${root.gen}世", fontSize = 13.sp, color = WuTextMuted)
                        root.zi?.let { Text(" · 字$it", fontSize = 13.sp, color = WuTextMuted) }
                    }
                    Spacer(Modifier.height(1.dp))
                }
                TreeNodeCard(
                    person = root,
                    depth = 0,
                    delay = 0,
                    navigateToPersonId = navigateToPersonId,
                    onPersonClick = onPersonClick,
                    scrollState = scrollState,
                    containerRootY = containerRootY
                )
            }
        }
    }
}
