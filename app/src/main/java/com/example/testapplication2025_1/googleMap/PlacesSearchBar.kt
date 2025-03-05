package com.example.testapplication2025_1.googleMap

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.kotlin.awaitFetchPlace
import com.google.android.libraries.places.api.net.kotlin.awaitFindAutocompletePredictions
import kotlinx.coroutines.launch

data class PlaceItem(
    val id: String,
    val name: String
)

@Composable
fun PlacesSearchBar(
    modifier: Modifier = Modifier,
    onPlaceSelected: (LatLng) -> Unit,
) {
    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }
    val token = remember { AutocompleteSessionToken.newInstance() }

    var text by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf(listOf<PlaceItem>()) }

    val scope = rememberCoroutineScope()

    fun selectPlace(placeId: String) = scope.launch {
        val fields = listOf(Place.Field.LOCATION)
        val location = placesClient.awaitFetchPlace(placeId, fields).place.location ?: return@launch
        onPlaceSelected(location)
        text = ""
    }

    LaunchedEffect(text) {
        if (text.isBlank()) {
            predictions = listOf()
            return@LaunchedEffect
        }
        val result = placesClient.awaitFindAutocompletePredictions {
            sessionToken = token
            query = text
        }
        predictions = result.autocompletePredictions.map {
            PlaceItem(id = it.placeId, name = it.getFullText(null).toString())
        }
    }

    LazyColumn(modifier = modifier.animateContentSize()) {
        item {
            TextField(
                value = text,
                onValueChange = { text = it },
                maxLines = 1
            )
        }
        if (predictions.isNotEmpty()) {
            items(predictions) { item ->
                Text(
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable(onClick = { selectPlace(item.id) }),
                    text = item.name
                )
            }
        }
    }
}