package com.appsmoviles.splitly.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.appsmoviles.splitly.viewmodel.BillViewModel
import com.appsmoviles.splitly.viewmodel.household.HouseholdViewModel

@Composable
fun Contributions(householdViewModel: HouseholdViewModel, billViewModel: BillViewModel, context: Context){


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "Contributions OverView",
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {


            }
        }
    }

}