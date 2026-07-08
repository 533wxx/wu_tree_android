package com.example.myapplication.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.BranchInfo
import com.example.myapplication.data.FamilyData
import com.example.myapplication.data.Person
import com.example.myapplication.data.SearchablePerson
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FamilyViewModel(application: Application) : AndroidViewModel(application) {

    private val allData: List<FamilyData> by lazy {
        val json = application.assets.open("family_data.json")
            .bufferedReader().use { it.readText() }
        Gson().fromJson(json, object : TypeToken<List<FamilyData>>() {}.type)
    }

    // --- Branch ---
    var selectedBranchIndex by mutableIntStateOf(0)
        private set

    val currentBranch: FamilyData
        get() = allData[selectedBranchIndex]

    val branchSummaries: List<BranchInfo> by lazy {
        allData.map { fd ->
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
    private val searchIndex: List<SearchablePerson> by lazy {
        val list = mutableListOf<SearchablePerson>()
        allData.forEachIndexed { bi, fd ->
            fun addPerson(p: Person) {
                list.add(SearchablePerson(p.id, p.name, p.zi, p.hao, fd.branch, p.gen, bi))
                p.children?.forEach { addPerson(it) }
            }
            addPerson(fd.root)
            fd.siblings?.forEach { addPerson(it) }
        }
        list
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

    fun dismissSearchDropdown() { showSearchDropdown = false }

    fun openPersonModal(person: Person) { selectedPerson = person }
    fun closePersonModal() { selectedPerson = null }

    fun selectSearchResult(entry: SearchablePerson) {
        showSearchDropdown = false
        selectedBranchIndex = entry.branchIndex
        isSidebarOpen = false
        navigateToPersonId = entry.id
    }

    fun clearNavigation() { navigateToPersonId = null }

    fun toggleSidebar() { isSidebarOpen = !isSidebarOpen }
    fun closeSidebar() { isSidebarOpen = false }

    // --- Flat ID list for scroll-to ---
    val flatPersonIds: List<String> by lazy {
        val ids = mutableListOf<String>()
        allData.forEach { fd ->
            fun walk(p: Person) {
                ids.add(p.id)
                p.children?.forEach { walk(it) }
            }
            walk(fd.root)
            fd.siblings?.forEach { walk(it) }
        }
        ids
    }

    fun findBranchIndexForPerson(personId: String): Int {
        allData.forEachIndexed { i, fd ->
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
