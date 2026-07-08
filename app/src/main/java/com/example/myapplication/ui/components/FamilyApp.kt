package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.myapplication.data.Person
import com.example.myapplication.data.SearchablePerson
import com.example.myapplication.ui.theme.WuAccent
import com.example.myapplication.ui.theme.WuBg
import com.example.myapplication.ui.theme.WuBorder
import com.example.myapplication.ui.theme.WuSurface
import com.example.myapplication.ui.theme.WuText
import com.example.myapplication.ui.theme.WuTextDim
import com.example.myapplication.ui.theme.WuTextMuted
import com.example.myapplication.viewmodel.FamilyViewModel
import kotlin.math.roundToInt

@Composable
fun FamilyApp(viewModel: FamilyViewModel, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth > 900.dp
        val focusManager = LocalFocusManager.current
        val density = LocalDensity.current

        // Dropdown position state
        var dropdownLeft by remember { mutableStateOf(0f) }
        var dropdownBottom by remember { mutableStateOf(0f) }
        var dropdownWidth by remember { mutableStateOf(0f) }

        val showDropdown = viewModel.showSearchDropdown
        val searchResults = viewModel.searchResults

        // Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WuBg)
                .background(
                    Brush.radialGradient(
                        colors = listOf(WuAccent.copy(alpha = 0.04f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(0.5f, 0f),
                        radius = 0.7f
                    )
                )
        )

        // Main layout
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            AppTopBar(
                sidebarOpen = viewModel.isSidebarOpen,
                searchQuery = viewModel.searchQuery,
                onToggleSidebar = { viewModel.toggleSidebar() },
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                onSearchPositionChanged = { l, b, w ->
                    dropdownLeft = l; dropdownBottom = b; dropdownWidth = w
                },
                showHamburger = !isWide
            )

            if (isWide) {
                Row(modifier = Modifier.weight(1f)) {
                    AppSidebar(
                        branches = viewModel.branchSummaries,
                        selectedIndex = viewModel.selectedBranchIndex,
                        onSelect = { viewModel.selectBranch(it) }
                    )
                    MainContent(
                        branch = viewModel.currentBranch,
                        navigateToPersonId = viewModel.navigateToPersonId,
                        onPersonClick = { viewModel.openPersonModal(it) },
                        onNavigationDone = { viewModel.clearNavigation() }
                    )
                }
            } else {
                Box(modifier = Modifier.weight(1f)) {
                    MainContent(
                        branch = viewModel.currentBranch,
                        navigateToPersonId = viewModel.navigateToPersonId,
                        onPersonClick = { viewModel.openPersonModal(it) },
                        onNavigationDone = { viewModel.clearNavigation() }
                    )
                    if (viewModel.isSidebarOpen) {
                        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f))
                            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() })
                        { viewModel.closeSidebar() })
                        AppSidebar(
                            branches = viewModel.branchSummaries,
                            selectedIndex = viewModel.selectedBranchIndex,
                            onSelect = { viewModel.selectBranch(it); viewModel.closeSidebar() }
                        )
                    }
                }
            }
        }

        // 搜索下拉遮罩
        if (showDropdown) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { viewModel.dismissSearchDropdown() }
            )
        }

        // 搜索下拉悬浮层
        if (showDropdown && dropdownWidth > 0f) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(dropdownLeft.roundToInt(), dropdownBottom.roundToInt()),
                properties = PopupProperties(focusable = false)
            ) {
                Surface(
                    modifier = Modifier
                        .width(with(density) { dropdownWidth.toDp() })
                        .heightIn(max = 320.dp),
                    shape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp),
                    color = WuSurface,
                    shadowElevation = 12.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WuBorder)
                ) {
                    if (searchResults.isEmpty()) {
                        Text("未找到匹配结果", fontSize = 13.sp, color = WuTextDim,
                            modifier = Modifier.padding(12.dp, 16.dp))
                    } else {
                        LazyColumn {
                            items(searchResults) { entry ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            focusManager.clearFocus()
                                            viewModel.selectSearchResult(entry as SearchablePerson)
                                        }
                                        .padding(10.dp, 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row {
                                            Text(entry.name, fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold, color = WuAccent)
                                            entry.zi?.let {
                                                Text(" · 字$it", fontSize = 13.sp, color = WuTextMuted)
                                            }
                                            entry.hao?.let {
                                                Text(" · 号$it", fontSize = 13.sp, color = WuTextMuted)
                                            }
                                        }
                                        Text("${entry.branch}房 · ${entry.gen}世",
                                            fontSize = 12.sp, color = WuTextDim,
                                            modifier = Modifier.padding(top = 2.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        PersonDialog(
            person = viewModel.selectedPerson,
            onDismiss = { viewModel.closePersonModal() },
            onNavigateToPerson = { personId ->
                viewModel.closePersonModal()
                viewModel.selectBranch(viewModel.findBranchIndexForPerson(personId))
                viewModel.navigateToPersonId = personId
            }
        )
    }
}

@Composable
private fun MainContent(
    branch: com.example.myapplication.data.FamilyData,
    navigateToPersonId: String?,
    onPersonClick: (Person) -> Unit,
    onNavigationDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
            .padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 8.dp)
    ) {
        Breadcrumb(branchName = branch.branch)
        StatsBar(branch = branch)
        TreeView(branch, navigateToPersonId, onPersonClick, Modifier.weight(1f))
    }
}
