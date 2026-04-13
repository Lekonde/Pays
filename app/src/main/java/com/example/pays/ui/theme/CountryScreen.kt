package com.example.pays.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.pays.Viewmodel.CountryCategory
import com.example.pays.Viewmodel.CountryViewModel
import com.example.pays.model.Country

@Composable
fun CountryAppNavigation(viewModel: CountryViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf("home") }

    if (currentScreen == "home") {
        HomeScreen(onCategorySelected = { category ->
            viewModel.fetchCountries(category)
            currentScreen = "list"
        })
    } else {
        CountryListScreen(
            viewModel = viewModel,
            onBack = { currentScreen = "home" }
        )
    }
}

@Composable
fun HomeScreen(onCategorySelected: (CountryCategory) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenue sur l'App Pays",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = { onCategorySelected(CountryCategory.ALL) },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Tous les pays du monde")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onCategorySelected(CountryCategory.AFRICA) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Pays d'Afrique")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryListScreen(viewModel: CountryViewModel, onBack: () -> Unit) {
    val countries by viewModel.filteredCountries.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Liste des Pays") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchBar(query = searchQuery, onQueryChanged = { viewModel.onSearchQueryChanged(it) })

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (error != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Erreur: $error", color = Color.Red)
                        Button(onClick = { /* Le ViewModel gère déjà le retry via fetch */ }) {
                            Text("Réessayer")
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(countries) { country ->
                            CountryItem(country)
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChanged: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        placeholder = { Text("Rechercher un pays ou une capitale...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun CountryItem(country: Country) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(country.flags.png)
                .crossfade(true)
                .build(),
            contentDescription = "Flag",
            modifier = Modifier.size(100.dp, 60.dp),
            contentScale = ContentScale.Fit,
            error = painterResource(android.R.drawable.ic_menu_report_image),
            placeholder = painterResource(android.R.drawable.ic_menu_gallery)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = country.name.common, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Capitale: ${country.capital?.joinToString() ?: "N/A"}", fontSize = 14.sp)
            Text(text = "Population: ${country.population}", fontSize = 14.sp)
        }
    }
}
