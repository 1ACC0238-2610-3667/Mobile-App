package com.appsmoviles.splitly.view.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appsmoviles.splitly.model.beans.distribution.Bills
import com.appsmoviles.splitly.view.members.MemberInfoRow
import java.util.Locale.getDefault

@Composable
fun ExpensesCard(bill: Bills){

    Card(
        modifier = Modifier
            .fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color(0xFF6366F1).copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Money, contentDescription = null, tint = Color(0xFF6366F1))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = bill.id.toString().uppercase(getDefault()) ?: "Unknown",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            MemberInfoRow(icon = Icons.Default.Email, text = bill.description ?: "No description")
            MemberInfoRow(icon = Icons.Default.Badge, text = "Amount: ${bill.amount.toString() ?: "No amount"}")
            //MemberInfoRow(icon = Icons.Default.Badge, text = "CreatedBy: ${bill.createdBy.toString() ?: "Unknown"}")

        }
    }

}