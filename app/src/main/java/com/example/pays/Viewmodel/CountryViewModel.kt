package com.example.pays.Viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pays.model.Country
import com.example.pays.model.CountryApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

enum class CountryCategory {
    ALL, AFRICA
}

class CountryViewModel : ViewModel() {
    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val api = CountryApp.create()

    // Filtrage automatique des pays selon la recherche
    val filteredCountries = combine(_countries, _searchQuery) { countries, query ->
        if (query.isBlank()) {
            countries
        } else {
            countries.filter {
                it.name.common.contains(query, ignoreCase = true) ||
                (it.capital?.any { cap -> cap.contains(query, ignoreCase = true) } ?: false)
            }
        }
    }.asStateFlow(_countries.value)

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun fetchCountries(category: CountryCategory) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _searchQuery.value = "" // Reset recherche lors du changement de catégorie
            try {
                val result = when (category) {
                    CountryCategory.ALL -> api.getAllCountries()
                    CountryCategory.AFRICA -> api.getAfricanCountries()
                }
                Log.d("CountryViewModel", "Fetched ${result.size} countries for $category")
                _countries.value = result
            } catch (e: Exception) {
                Log.e("CountryViewModel", "Error fetching countries", e)
                _error.value = e.localizedMessage ?: "Erreur réseau"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

// Extension pour transformer un Flow en StateFlow sans passer par stateIn dans certains cas
private fun <T> kotlinx.coroutines.flow.Flow<T>.asStateFlow(initialValue: T): kotlinx.coroutines.flow.StateFlow<T> {
    val flow = this
    val state = MutableStateFlow(initialValue)
    kotlinx.coroutines.GlobalScope.launch {
        flow.collect { state.value = it }
    }
    return state
}
