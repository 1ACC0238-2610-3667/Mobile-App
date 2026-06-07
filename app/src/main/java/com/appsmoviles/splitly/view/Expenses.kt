package com.appsmoviles.splitly.view

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.appsmoviles.splitly.model.beans.householdmanagement.Household
import com.appsmoviles.splitly.model.client.CredentialsSessionManager
import com.appsmoviles.splitly.viewmodel.BillViewModel
import com.appsmoviles.splitly.viewmodel.household.HouseholdViewModel
import kotlinx.serialization.Contextual

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


}