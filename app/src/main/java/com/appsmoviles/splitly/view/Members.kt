package com.appsmoviles.splitly.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.appsmoviles.splitly.model.client.CredentialsSessionManager
import com.appsmoviles.splitly.viewmodel.HouseholdMemberViewModel
import com.appsmoviles.splitly.viewmodel.household.HouseholdViewModel

@Composable
fun Members(memberViewModel: HouseholdMemberViewModel, householdViewModel: HouseholdViewModel, context: Context){

    LaunchedEffect(Unit) {
        memberViewModel.getHouseholdMembersByHouseholdId(householdViewModel,
            CredentialsSessionManager.getUserId())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

    }



}