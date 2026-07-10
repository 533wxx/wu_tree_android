package com.example.myapplication.data

data class FamilyData(
    val branch: String,
    val root: Person,
    val siblings: List<Person>? = null
)

data class Person(
    val id: String,
    val name: String,
    val zi: String? = null,
    val hao: String? = null,
    val gen: Int? = null,
    val gender: String? = null,
    val birth: String? = null,
    val wife: String? = null,
    val death: String? = null,
    val adoptNote: String? = null,
    val note: String? = null,
    val daughters: List<String>? = null,
    val children: List<Person>? = null,
    val wifeDisplay: String? = null,
    val deathDisplay: String? = null,
    val birthOrder: String? = null
)

data class SearchablePerson(
    val id: String,
    val name: String,
    val zi: String?,
    val hao: String?,
    val branch: String,
    val gen: Int?,
    val branchIndex: Int
)

data class BranchInfo(
    val name: String,
    val count: Int,
    val maxGen: Int
)
