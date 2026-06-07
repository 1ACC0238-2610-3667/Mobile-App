package com.appsmoviles.splitly.shared.components

import android.app.appsearch.SearchResult
import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MovableContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.appsmoviles.splitly.model.beans.householdmanagement.HouseholdMember
import com.appsmoviles.splitly.model.beans.iam.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBar(items: Map<String, List<User>>?){

    var query by remember { mutableStateOf("") }

    var filteredItems = remember(query){
        if(query.isBlank()) items
        else items!!.filterValues { values ->
            values.any { value ->
                value.name!!.contains(query, ignoreCase = true)
            }
        }
    }


    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = {query = it},
            label = {Text("Search")},
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {})
        )

        Spacer(modifier = Modifier.height(16.dp))


        LazyColumn() {
            filteredItems?.forEach { (key, value) ->

                item {
                    Text(
                        text = key
                    )
                }

                item {

                    HorizontalMultiBrowseCarousel(
                        state = rememberCarouselState { value.count() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(top = 16.dp, bottom = 16.dp),
                        preferredItemWidth = 186.dp,
                        itemSpacing = 8.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) { i ->
                        val auxValue = value[i]
                        ElevatedCard(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 6.dp
                            ),
                            modifier = Modifier
                                .size(width = 240.dp, height = 100.dp)
                        ) {
                            Text(
                                text = "Name: ${auxValue.name!!}",
                                modifier = Modifier
                                    .padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Email: ${auxValue.email!!}",
                                modifier = Modifier
                                    .padding(16.dp),
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Role: ${auxValue.role!!}",
                                modifier = Modifier
                                    .padding(16.dp),
                                textAlign = TextAlign.Center
                            )


                        }
                    }
                }
            }
        }
    }

}