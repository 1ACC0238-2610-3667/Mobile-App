package com.appsmoviles.splitly.view.households

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appsmoviles.splitly.model.beans.householdmanagement.Household
import com.appsmoviles.splitly.model.client.CredentialsSessionManager
import com.appsmoviles.splitly.viewmodel.household.HouseholdViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Households(viewModel: HouseholdViewModel, context: Context) {

    val userId = CredentialsSessionManager.getIdFromUser()
    var showDialog by remember { mutableStateOf(false) }
    var selectedHousehold by remember { mutableStateOf<Household?>(null) }

    LaunchedEffect(Unit) {
        if (userId != -1 && viewModel.isLoading) {
            Log.d("UserId", "$userId")
            viewModel.getHouseholdsByRepresentativeId(userId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "My Households",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6366F1))
                }
            } else if (viewModel.households.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No households found. Create one!",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.households) { householdAux ->
                        HouseholdItem(
                            household = householdAux!!,
                            onEdit = {
                                selectedHousehold = householdAux
                                showDialog = true
                            },
                            onDelete = {
                                viewModel.deleteHousehold(householdAux.id, userId)
                            }
                        )
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                selectedHousehold = null
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color(0xFF6366F1),
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Household")
        }

        // Error Snackbar
        if (viewModel.errorMessage != null) {
            Snackbar(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                containerColor = Color.Red,
                contentColor = Color.White
            ) {
                Text(viewModel.errorMessage!!)
            }
        }
    }

    if (showDialog) {
        HouseholdDialog(
            household = selectedHousehold,
            userId = userId,
            onDismiss = { showDialog = false },
            onConfirm = { name, description, currency, memberCount ->
                if (selectedHousehold == null) {
                    val newHousehold = Household(
                        id = "", // Server generates this
                        name = name,
                        representativeId = userId,
                        currency = currency,
                        description = description,
                        memberCount = memberCount,
                        startDate = null, // Server handles this
                        createdAt = null,
                        updatedAt = null
                    )
                    viewModel.createHousehold(newHousehold)
                } else {
                    val updated = selectedHousehold!!.copy(
                        name = name,
                        description = description,
                        currency = currency
                    )
                    viewModel.updateHouseholdById(updated.id, updated)
                }
                showDialog = false
                // Refresh after a delay or based on auxHousehold change
            }
        )
    }
    
    // Refresh list when a change occurs
    LaunchedEffect(viewModel.auxHousehold) {
        if (viewModel.auxHousehold != null) {
            viewModel.getHouseholdsByRepresentativeId(userId)
        }
    }
}



