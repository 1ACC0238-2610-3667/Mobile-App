package com.appsmoviles.splitly.view

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appsmoviles.splitly.model.beans.householdmanagement.Household
import com.appsmoviles.splitly.model.client.CredentialsSessionManager
import com.appsmoviles.splitly.viewmodel.BillViewModel
import com.appsmoviles.splitly.viewmodel.household.HouseholdViewModel
import kotlinx.serialization.Contextual

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Expenses(billViewModel: BillViewModel, householdViewModel: HouseholdViewModel, context: Context){

    val userId = CredentialsSessionManager.getIdFromUser()
    var showDialog by remember { mutableStateOf(false) }
    var selectedBill by remember { mutableStateOf<Household?>(null) }

    LaunchedEffect(Unit) {
        if (userId != -1) {
            householdViewModel.getHouseholdsByRepresentativeId(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bills", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog = true
                },
                containerColor = Color(0xFF6366F1),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Bill")
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ){ paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ){
            if(billViewModel.isLoading){
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center))
            }else if(billViewModel.householdBills.isNullOrEmpty()){
                Text(
                    text = "No Bills found. Crate omne"
                )

            }else{
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items()
                }
            }
        }

    }

}