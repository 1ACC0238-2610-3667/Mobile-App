package com.appsmoviles.splitly.view.members

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.appsmoviles.splitly.model.beans.householdmanagement.Invitation
import com.appsmoviles.splitly.model.client.CredentialsSessionManager
import com.appsmoviles.splitly.viewmodel.HouseholdMemberViewModel
import com.appsmoviles.splitly.viewmodel.household.HouseholdViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Members(memberViewModel: HouseholdMemberViewModel, householdViewModel: HouseholdViewModel, context: Context) {

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        memberViewModel.getHouseholdMembersByHouseholdId(
            householdViewModel,
            CredentialsSessionManager.getIdFromUser()
        )
    }

    var query by remember { mutableStateOf("") }
    val auxHouseholdMembers = memberViewModel.householdMembers

    val filteredItems = remember(query, auxHouseholdMembers) {
        if (query.isBlank()) auxHouseholdMembers
        else auxHouseholdMembers.mapValues { (_, values) ->
            values.filter { it?.name?.contains(query, ignoreCase = true) == true }
        }.filterValues { it.isNotEmpty() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Household Members", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
                Icon(Icons.Default.Add, contentDescription = "Add Member")
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            //Search Bar btw
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search members...", color = Color(0xFF94A3B8)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF6366F1)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {}),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6366F1),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF1F5F9),
                        unfocusedContainerColor = Color(0xFFF1F5F9)
                    )
                )
            }

            if (memberViewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6366F1))
                }
            } else if (filteredItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No members found", color = Color.Gray)
                }
            }else if(memberViewModel.errorMessage != null){
                Snackbar(
                    modifier = Modifier.padding(16.dp).align(Alignment.End),
                    containerColor = Color.Red,
                    contentColor = Color.White
                ){
                    Text(memberViewModel.errorMessage!!)
                }
            }else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    filteredItems.forEach { (householdId, members) ->
                        val householdName = householdViewModel.households.find { it?.id == householdId }?.name ?: "Household: $householdId"
                        
                        item {
                            Text(
                                text = householdName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
                            )
                        }

                        item {
                            HorizontalMultiBrowseCarousel(
                                state = rememberCarouselState { members.size },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                preferredItemWidth = 260.dp,
                                itemSpacing = 12.dp,
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) { index ->
                                val member = members[index]
                                if (member != null) {
                                    MemberCard(user = member)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if(showDialog){

        var email by remember { mutableStateOf("") }
        var householdId by remember { mutableStateOf("") }
        //For the dd menu of HHolds
        var expanded by remember { mutableStateOf(false) }

        var dropdownMenuFieldSize by remember { mutableStateOf(Size.Zero) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {Text("Invite New Member")},
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp) ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {email = it},
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = householdId,
                        onValueChange = {householdId = it},
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned{ coordinates ->
                                //to set ddm with the same size as the outlined text field
                                dropdownMenuFieldSize = coordinates.size.toSize()
                            },
                        label = {Text("Household")},
                        trailingIcon = {
                            Icon(Icons.Default.MoreVert, "Cotent Description",
                                Modifier.clickable{ expanded = !expanded})
                        }

                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {expanded = false},
                        modifier = Modifier
                            .width(with(LocalDensity.current){dropdownMenuFieldSize.width.toDp()})
                    ) {
                        householdViewModel.households.forEach {  household ->
                            DropdownMenuItem(
                                text = {Text(text = household!!.id)},
                                onClick = {
                                    householdId =household!!.id
                                    expanded = false
                                }
                            )
                        }

                    }

                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newInvitation = Invitation(
                            id = 0,
                            email = email,
                            householdId = householdId,
                            description = ""
                        )
                        memberViewModel.createInvitation(newInvitation)
                    },
                    enabled = email.isNotBlank() && householdId.isNotBlank(),
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


