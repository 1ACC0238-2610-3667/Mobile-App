package com.appsmoviles.splitly.view.households

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.appsmoviles.splitly.model.beans.householdmanagement.Household

@Composable
fun HouseholdDialog(
    household: Household?,
     userId: Int,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(household?.name ?: "") }
    var description by remember { mutableStateOf(household?.description ?: "") }
    var currency by remember { mutableStateOf(household?.currency ?: "USD") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (household == null) "Create Household" else "Edit Household") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = currency,
                    onValueChange = { currency = it },
                    label = { Text("Currency (e.g. USD, PEN)") },
                    modifier = Modifier.fillMaxWidth()
                )





            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, description, currency) },
                enabled = name.isNotBlank() && description.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
