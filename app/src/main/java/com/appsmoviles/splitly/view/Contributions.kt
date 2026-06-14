package com.appsmoviles.splitly.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appsmoviles.splitly.model.beans.distribution.Bills
import com.appsmoviles.splitly.model.beans.distribution.Contribution
import com.appsmoviles.splitly.model.client.CredentialsSessionManager
import com.appsmoviles.splitly.view.expenses.ExpensesCard
import com.appsmoviles.splitly.viewmodel.BillViewModel
import com.appsmoviles.splitly.viewmodel.contributions.ContributionViewModel
import com.appsmoviles.splitly.viewmodel.household.HouseholdViewModel
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.mutableListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Contributions(householdViewModel: HouseholdViewModel, billViewModel: BillViewModel,
                  contributionViewModel: ContributionViewModel, context: Context){

    val userId = CredentialsSessionManager.getIdFromUser()
    var showDialog by remember { mutableStateOf(false) }
    val auxHouseholdBills = billViewModel.householdBills

    LaunchedEffect(Unit) {
        if(contributionViewModel.isLoading)
            contributionViewModel.getContributions(billViewModel.householdBills)

    }

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

            if (contributionViewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6366F1))
                }
            } else if (contributionViewModel.contributions.isNullOrEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No Bills found. Create one",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {

                    auxHouseholdBills.forEach { (householdId, bills) ->

                        item {
                            Text(
                                text = "Household: $householdId",
                                modifier = Modifier.padding(16.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        items(bills){ bill ->
                            Text(
                                text = "Bill: ${bill.id}",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                fontWeight = FontWeight.SemiBold
                            )

                            val billContributions = contributionViewModel.contributions.entries
                                .find { it.key.billId == bill.id }?.value ?: emptyList()

                            HorizontalMultiBrowseCarousel(
                                state = rememberCarouselState { billContributions.size },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                preferredItemWidth = 260.dp,
                                itemSpacing = 12.dp,
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) { index ->
                                val contribution = billContributions[index]
                                Card(
                                    modifier = Modifier.fillMaxSize(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = "Member ID", fontSize = 12.sp, color = Color.Gray)
                                        Text(text = contribution.memberId, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(text = "Amount", fontSize = 12.sp, color = Color.Gray)
                                        Text(
                                            text = "$${contribution.amount}",
                                            color = Color(0xFF6366F1),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }


        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color(0xFF6366F1),
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Contribution")
        }

        // Error Snackbar
        if (contributionViewModel.errorMessage != null) {
            Snackbar(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                containerColor = Color.Red,
                contentColor = Color.White
            ) {
                Text(contributionViewModel.errorMessage!!)
            }
        }
    }

    if(showDialog){

        var houseHoldId: String by remember { mutableStateOf("") }
        var billId: String by remember { mutableStateOf("") }
        var description: String by remember { mutableStateOf("") }
        var amount: Double by remember { mutableStateOf(-1.00) }
        var paymentDate: String by remember { mutableStateOf("") }
        var billsByHousehold: List<Bills?> by remember { mutableStateOf(arrayListOf()) }

        //For the dd menu of HHolds
        var expanded by remember { mutableStateOf(false) }
        var expanded2 by remember { mutableStateOf(false) }


        var dropdownMenuFieldSize by remember { mutableStateOf(Size.Zero) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {Text("Create New Bill")},
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp) ) {
                    OutlinedTextField(
                        value = description,
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
                            value = houseHoldId,
                            onValueChange = { newValue ->
                                houseHoldId = newValue;
                                billsByHousehold = billViewModel.householdBills[houseHoldId] ?: emptyList<Bills>()
                                            },
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



                    Box() {


                        OutlinedTextField(
                            value = billId,
                            onValueChange = { billId = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    //to set ddm with the same size as the outlined text field
                                    dropdownMenuFieldSize = coordinates.size.toSize()
                                },
                            label = { Text("Bill") },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.MoreVert, "Content Description",
                                    Modifier.clickable { expanded2 = !expanded2 })
                            }

                        )

                        DropdownMenu(
                            expanded = expanded2,
                            onDismissRequest = { expanded2 = false },
                            modifier = Modifier
                                .width(with(LocalDensity.current) { dropdownMenuFieldSize.width.toDp() })
                        ) {
                            billsByHousehold.forEach {
                                DropdownMenuItem(
                                    text = { Text(text = it!!.id!!) },
                                    onClick = {
                                        billId = it!!.id!!
                                        amount = it.amount
                                        expanded2 = false
                                    }
                                )
                            }

                        }
                    }



                    OutlinedTextField(
                        enabled = false,
                        value = amount.toString(),
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Amount to Pay")}
                    )

                    OutlinedTextField(
                        value = paymentDate,
                        onValueChange = {paymentDate = it},
                        modifier = Modifier.fillMaxWidth(),
                        label = {Text("YYYY-MM-DD")}

                    )

                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newContribution = Contribution(
                            id = "",
                            billId = billId,
                            householdId = houseHoldId,
                            description = description,
                            deadlineForMembers = billsByHousehold.find { it!!.id == billId }!!.paymentDate,
                            strategy = 0
                        )
                        contributionViewModel.createContributionAndMemberContributions(newContribution, houseHoldId, amount)
                        showDialog = false

                    },
                    enabled = amount > 0 && houseHoldId.isNotBlank(),
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

}