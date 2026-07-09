package com.example.myapplication.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.BranchInfo
import com.example.myapplication.data.FamilyData
import com.example.myapplication.data.FamilyRepository
import com.example.myapplication.data.Person
import com.example.myapplication.data.SearchablePerson
import kotlinx.coroutines.launch

class FamilyViewModel(application: Application) : AndroidViewModel(application) {

    // --- Data loading state ---
    var allData by mutableStateOf<List<FamilyData>?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var loadError by mutableStateOf<String?>(null)
        private set

    init {
        loadFamilyData()
    }

    private fun loadFamilyData() {
        viewModelScope.launch {
            isLoading = true
            loadError = null
            FamilyRepository.loadData(getApplication())
                .onSuccess { data ->
                    allData = data
                    // 数据加载后重置选中分支
                    if (selectedBranchIndex >= data.size) {
                        selectedBranchIndex = 0
                    }
                }
                .onFailure { error ->
                    loadError = error.message ?: "数据加载失败"
                }
            isLoading = false
        }
    }

    fun retryLoad() {
        loadFamilyData()
    }

    // --- Branch ---
    var selectedBranchIndex by mutableIntStateOf(0)
        private set

    val currentBranch: FamilyData
        get() {
            val data = allData ?: throw IllegalStateException("数据尚未加载")
            return data[selectedBranchIndex]
        }

    val branchSummaries: List<BranchInfo>
        get() {
            val data = allData ?: return emptyList()
            return data.map { fd ->
                var count = 0
                var maxGen = 0
                val seen = mutableSetOf<String>()
                fun walk(p: Person) {
                    if (seen.add(p.id)) {
                        count++
                        if ((p.gen ?: 0) > maxGen) maxGen = p.gen ?: 0
                        p.children?.forEach { walk(it) }
                    }
                }
                walk(fd.root)
                fd.siblings?.forEach { walk(it) }
                BranchInfo(name = fd.branch, count = count, maxGen = maxGen)
            }
        }

    // --- Search ---
    private val searchIndex: List<SearchablePerson>
        get() {
            val data = allData ?: return emptyList()
            val list = mutableListOf<SearchablePerson>()
            data.forEachIndexed { bi, fd ->
                fun addPerson(p: Person) {
                    list.add(SearchablePerson(p.id, p.name, p.zi, p.hao, fd.branch, p.gen, bi))
                    p.children?.forEach { addPerson(it) }
                }
                addPerson(fd.root)
                fd.siblings?.forEach { addPerson(it) }
            }
            return list
        }

    var searchQuery by mutableStateOf("")
        private set

    var showSearchDropdown by mutableStateOf(false)
        private set

    val searchResults: List<SearchablePerson>
        get() {
            val q = searchQuery.trim()
            if (q.isEmpty()) return emptyList()
            return searchIndex.filter { entry ->
                entry.name.contains(q) ||
                        (entry.zi?.contains(q) == true) ||
                        (entry.hao?.contains(q) == true)
            }.take(20)
        }

    // --- Modal ---
    var selectedPerson by mutableStateOf<Person?>(null)
    var navigateToPersonId by mutableStateOf<String?>(null)

    // --- Sidebar ---
    var isSidebarOpen by mutableStateOf(false)

    // --- Actions ---
    fun selectBranch(index: Int) {
        if (index != selectedBranchIndex) {
            selectedBranchIndex = index
            navigateToPersonId = null
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        showSearchDropdown = query.isNotBlank()
    }

    fun dismissSearchDropdown() {
        showSearchDropdown = false
    }

    fun openPersonModal(person: Person) {
        selectedPerson = person
    }

    fun closePersonModal() {
        selectedPerson = null
    }

    fun selectSearchResult(entry: SearchablePerson) {
        showSearchDropdown = false
        selectedBranchIndex = entry.branchIndex
        isSidebarOpen = false
        navigateToPersonId = entry.id
    }

    fun clearNavigation() {
        navigateToPersonId = null
    }

    fun toggleSidebar() {
        isSidebarOpen = !isSidebarOpen
    }

    fun closeSidebar() {
        isSidebarOpen = false
    }

    // --- Flat ID list for scroll-to ---
    val flatPersonIds: List<String>
        get() {
            val data = allData ?: return emptyList()
            val ids = mutableListOf<String>()
            data.forEach { fd ->
                fun walk(p: Person) {
                    ids.add(p.id)
                    p.children?.forEach { walk(it) }
                }
                walk(fd.root)
                fd.siblings?.forEach { walk(it) }
            }
            return ids
        }

    fun findBranchIndexForPerson(personId: String): Int {
        val data = allData ?: return selectedBranchIndex
        data.forEachIndexed { i, fd ->
            fun walk(p: Person): Boolean {
                if (p.id == personId) return true
                return p.children?.any { walk(it) } == true
            }
            if (walk(fd.root)) return i
            if (fd.siblings?.any { walk(it) } == true) return i
        }
        return selectedBranchIndex
    }
}
