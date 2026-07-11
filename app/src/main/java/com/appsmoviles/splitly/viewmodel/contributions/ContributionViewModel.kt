package com.appsmoviles.splitly.viewmodel.contributions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.beans.distribution.Bills
import com.appsmoviles.splitly.model.beans.distribution.Contribution
import com.appsmoviles.splitly.model.beans.distribution.CreateBillResource
import com.appsmoviles.splitly.model.beans.distribution.CreateContributionResource
import com.appsmoviles.splitly.model.beans.distribution.CreateMemberContributionResource
import com.appsmoviles.splitly.model.beans.distribution.MemberContribution
import com.appsmoviles.splitly.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContributionViewModel: ViewModel() {

    var isLoading by mutableStateOf(false)
    var errorMessage: String? by mutableStateOf(null)
    var contributions: MutableMap<Contribution, List<MemberContribution>> = mutableStateMapOf()

    fun createFullExpense(
        householdId: String,
        description: String,
        totalAmount: Double,
        creatorId: Int,
        paymentDate: String,
        deadline: String,
        isProportional: Boolean = false,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                isLoading = true
                errorMessage = null
            }

            try {
                val membersRes = RetrofitClient.householdMemberWebService.getHouseholdMembersByHouseholdId(householdId)
                val members = membersRes.body() ?: emptyList()

                if (members.isEmpty()) {
                    throw Exception("No hay miembros en este hogar. Invita a alguien antes de crear gastos.")
                }

                if (isProportional) {
                    val invalidMembers = members.filter { it.income == null || it.income!! <= 0.0 }
                    if (invalidMembers.isNotEmpty()) {
                        throw Exception("Todos los miembros (incluyéndote) deben tener un ingreso registrado válido.")
                    }
                }
                
                val totalIncome = if (isProportional) members.sumOf { it.income!! } else 0.0

                val newBill = CreateBillResource(
                    houseHoldId = householdId,
                    description = description,
                    amount = totalAmount,
                    createdBy = creatorId,
                    paymentDate = paymentDate
                )
                val billRes = RetrofitClient.billWebService.createBill(newBill)
                if (!billRes.isSuccessful || billRes.body() == null) throw Exception("Error al crear Recibo")
                val createdBill = billRes.body()!!

                val newContribution = CreateContributionResource(
                    billId = createdBill.id!!,
                    householdId = householdId,
                    description = description,
                    deadlineForMembers = deadline,
                    strategy = 1
                )
                val contribRes = RetrofitClient.contributionWebService.createContribution(newContribution)
                if (!contribRes.isSuccessful || contribRes.body() == null) throw Exception("Error al crear Regla de División")
                val createdContrib = contribRes.body()!!

                coroutineScope {
                    val deferreds = members.map { member ->
                        async(Dispatchers.IO) {
                            val memberShare = if (isProportional) {
                                totalAmount * (member.income!! / totalIncome)
                            } else {
                                totalAmount / members.size
                            }
                            
                            val mc = CreateMemberContributionResource(
                                contributionId = createdContrib.id!!,
                                memberId = member.id!!,
                                amount = memberShare
                            )
                            val mcRes = RetrofitClient.memberContributionWebService.createMemberContribution(mc)
                            if (!mcRes.isSuccessful) throw Exception("Fallo al asignar cuota al miembro ${member.id}")
                        }
                    }
                    deferreds.awaitAll()
                }

                withContext(Dispatchers.Main) {
                    isLoading = false
                    onSuccess()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = e.message
                    isLoading = false
                }
            }
        }
    }

    fun getContributions(billsPerHousehold: MutableMap<String, ArrayList<Bills>>){
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                withContext(Dispatchers.IO){
                    billsPerHousehold.forEach { (_, bills) ->
                        for(bill in bills){
                            val responseContribution = RetrofitClient.contributionWebService.getContributionByBillId(bill.id!!)
                            if(responseContribution.isSuccessful && responseContribution.body() != null) {
                                val contributionEntity = responseContribution.body() as Contribution
                                val mcRes = RetrofitClient.memberContributionWebService.getMemberContributionsByContributionId(contributionEntity.id!!)
                                if (mcRes.isSuccessful && mcRes.body() != null) {
                                    withContext(Dispatchers.Main) {
                                        contributions[contributionEntity] = mcRes.body()!!
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception){
                errorMessage = "Error al cargar historial: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}