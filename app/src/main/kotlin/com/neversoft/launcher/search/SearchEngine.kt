package com.neversoft.launcher.search

interface SearchProvider { suspend fun query(q: String): List<SearchResult> }

data class SearchResult(val id: String, val label: String, val subtitle: String = "",
    val type: ResultType = ResultType.APP, val packageName: String? = null, val filePath: String? = null)

enum class ResultType { APP, FILE, MEDIA, CONTACT }

class LauncherSearchEngine {
    suspend fun search(query: String): List<SearchResult> = emptyList()
}
