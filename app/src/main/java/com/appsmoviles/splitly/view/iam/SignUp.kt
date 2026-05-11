package com.appsmoviles.splitly.view.iam

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appsmoviles.splitly.R

@Composable
fun SignUp(nav: NavHostController) {
    var txtName by remember { mutableStateOf("") }
    var txtEmail by remember { mutableStateOf("") }
    var txtPas by remember { mutableStateOf("") }
    var txtConfirmPas by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    //For Role Switcher
    var checked by remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.splitlylogo),
                contentDescription = "Splitly Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Join Splitly and start sharing",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Member",
                    color = if (!checked) MaterialTheme.colorScheme.primary else Color.Gray,
                    fontWeight = if (!checked) FontWeight.Bold else FontWeight.Normal
                )

                Spacer(modifier = Modifier.width(12.dp))

                Switch(
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                        role = if (checked) "Representative" else "Member"
                    }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Representative",
                    color = if (checked) MaterialTheme.colorScheme.primary else Color.Gray,
                    fontWeight = if (checked) FontWeight.Bold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = txtName,
                onValueChange = { txtName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Full Name") },
                placeholder = { Text("John Doe") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = txtEmail,
                onValueChange = { txtEmail = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email Address") },
                placeholder = { Text("example@mail.com") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = txtPas,
                onValueChange = { txtPas = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon = {
                    val icon = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(imageVector = icon, contentDescription = null)
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = txtConfirmPas,
                onValueChange = { txtConfirmPas = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Confirm Password") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon = {
                    val icon = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                        Icon(imageVector = icon, contentDescription = null)
                    }
                },
                visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    // Sign up logic
                    nav.navigate("Dashboard")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Sign Up", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Already have an account? ", color = Color.Gray)
                Text(
                    text = "Log In",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        nav.popBackStack()
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
