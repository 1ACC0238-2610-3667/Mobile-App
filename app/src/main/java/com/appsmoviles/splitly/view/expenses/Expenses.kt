package com.appsmoviles.splitly.view.expenses

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.appsmoviles.splitly.model.beans.distribution.Bills
import com.appsmoviles.splitly.model.beans.distribution.Contribution
import com.appsmoviles.splitly.model.client.CredentialsSessionManager
import com.appsmoviles.splitly.view.Contributions
import com.appsmoviles.splitly.viewmodel.BillViewModel
import com.appsmoviles.splitly.viewmodel.household.HouseholdViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Expenses(billViewModel: BillViewModel, householdViewModel: HouseholdViewModel, context: Context){

    val userId = CredentialsSessionManager.getIdFromUser()
    var showDialog by remember { mutableStateOf(false) }
    val auxHouseholdBills = billViewModel.householdBills

    LaunchedEffect(Unit) {
        if (userId != -1) {
            if(householdViewModel.households.isNullOrEmpty())
                householdViewModel.getHouseholdsByRepresentativeId(userId)
            billViewModel.getAmountOfBillsByHouseholdIds(householdViewModel.households)
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
                    text = "No Bills found. Crate one"
                )

            } else if (billViewModel.errorMessage != null) {
                Snackbar(
                    modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter),
                    containerColor = Color.Red,
                    contentColor = Color.White
                ) {
                    Text(billViewModel.errorMessage!!)
                }
            } else{
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    auxHouseholdBills.forEach { (householdId, bills) ->
                        val householdName = householdViewModel.households.find { it?.id == householdId }?.name ?: "Household: $householdId"

                        item {
                                Text(
                                    text = householdName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E2938),
                                    modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
                                )
                        }

                        item {
                            HorizontalMultiBrowseCarousel(
                                state = rememberCarouselState { bills.size },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                preferredItemWidth = 260.dp,
                                itemSpacing = 12.dp,
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) { index ->
                                val auxBills = bills[index]
                                if (auxBills != null) {
                                    ExpensesCard(auxBills)
                                }
                            }
                        }





                    }


                }
            }


        }

    }


    if(showDialog){

        var houseHoldId: String by remember { mutableStateOf("") }
        var description: String by remember { mutableStateOf("") }
        var amount: Double by remember { mutableStateOf(-1.00) }
        var paymentDate: String by remember { mutableStateOf("") }

        //For the dd menu of HHolds
        var expanded by remember { mutableStateOf(false) }

        var dropdownMenuFieldSize by remember { mutableStateOf(Size.Zero) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {Text("Create New Bill")},
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp) ) {
                    OutlinedTextField(
                        value = description!!,
                        label = {
                            Text(
                                text = "Description"
                            )
                        },
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box() {

                        OutlinedTextField(
                            value = houseHoldId!!,
                            onValueChange = { houseHoldId = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    //to set ddm with the same size as the outlined text field
                                    dropdownMenuFieldSize = coordinates.size.toSize()
                                },
                            label = { Text("Household") },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.MoreVert, "Content Description",
                                    Modifier.clickable { expanded = !expanded })
                            }

                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .width(with(LocalDensity.current) { dropdownMenuFieldSize.width.toDp() })
                        ) {
                            householdViewModel.households.forEach { household ->
                                DropdownMenuItem(
                                    text = { Text(text = household!!.id) },
                                    onClick = {
                                        houseHoldId = household!!.id
                                        expanded = false
                                    }
                                )
                            }

                        }
                    }

                    OutlinedTextField(
                        value = amount.toString(),
                        onValueChange = {amount = it.toDouble()},
                        modifier = Modifier.fillMaxWidth(),
                        label = {Text("Amount to Pay")}
                    )

                    OutlinedTextField(
                        value = paymentDate.toString(),
                        onValueChange = {paymentDate = it},
                        modifier = Modifier.fillMaxWidth(),
                        label = {Text("YYYY-MM-DD")}

                    )

                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newBill = Bills(
                            id = "",
                            houseHoldId = houseHoldId,
                            description = description,
                            amount = amount,
                            createdBy = userId,
                            paymentDate = paymentDate,
                            createdAt = "",
                            updatedAt = "",
                        )
                        billViewModel.createBill(newBill)

                        /*val newContribution = Contribution(
                            id = "",
                            billId = billViewModel.newBill!!.id,
                            householdId = bill!!.houseHoldId,
                            description = bill!!.description,
                            deadlineForMembers = bill!!.paymentDate,
                            strategy = 1,
                            amount = bill!!.amount
                            )*/
                        showDialog = false

                    },
                    enabled = amount.toString().isNotBlank() && houseHoldId!!.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                ) {
                    Text("Confirm")
                }

            },
            dismissButton = {
                TextButton(onClick = {showDialog = false}) {
                    Text("Cancel")
                }

            }
        )

    }

    /*LaunchedEffect(billViewModel.householdBills) {
        if (billViewModel.householdBills != null) {
            billViewModel.getAmountOfBillsByHouseholdIds(householdViewModel.households)
        }
    }*/

}