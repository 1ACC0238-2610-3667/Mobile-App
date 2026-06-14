package com.appsmoviles.splitly.viewmodel.contributions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appsmoviles.splitly.model.beans.distribution.Bills
import com.appsmoviles.splitly.model.beans.distribution.Contribution
import com.appsmoviles.splitly.model.beans.distribution.MemberContribution
import com.appsmoviles.splitly.model.beans.householdmanagement.HouseholdMember
import com.appsmoviles.splitly.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList
import kotlin.collections.forEach

class ContributionViewModel: ViewModel() {

    var isLoading by mutableStateOf(true)
    var errorMessage: String? by mutableStateOf(null)
    var contributions: MutableMap<Contribution, List<MemberContribution>> = mutableStateMapOf()


    fun createContributionAndMemberContributions(contribution: Contribution, householdId: String, totalAmount: Double){

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // 1. Create the contribution
                val responseContribution = withContext(Dispatchers.IO){
                    RetrofitClient.contributionWebService.createContribution(contribution)
                }

                if(responseContribution.isSuccessful && responseContribution.body() != null){
                    val createdContribution = responseContribution.body()!!

                    // 2. Get members to calculate split
                    val responseMembers = withContext(Dispatchers.IO) {
                        RetrofitClient.householdMemberWebService.getHouseholdMembersByHouseholdId(householdId)
                    }

                    if (responseMembers.isSuccessful && responseMembers.body() != null) {
                        val members = responseMembers.body()!!
                        val splitAmount = totalAmount / members.size

                        // 3. Create member contributions in parallel
                        coroutineScope {
                            members.map { member ->
                                async(Dispatchers.IO) {
                                    RetrofitClient.memberContributionWebService
                                        .createMemberContribution(MemberContribution(
                                            createdContribution.id!!,
                                            member.id,
                                            splitAmount
                                        ))
                                }
                            }.awaitAll()
                        }
                    } else {
                        errorMessage = "Error fetching members: ${responseMembers.code()}"
                    }
                } else {
                    errorMessage = "Error creating contribution: ${responseContribution.code()}"
                }
            } catch (e: Exception){
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun getContributions(billsPerHousehold: MutableMap<String, ArrayList<Bills>>){

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {


                withContext(Dispatchers.IO){
                    //iterate in bills perHouseholdMap
                    billsPerHousehold.forEach { (_, bills) ->
                        //iterate in bill
                        for(bill in bills){

                            val responseContribution = RetrofitClient.contributionWebService.getContributionByBillId(bill.id!!)

                            if(responseContribution.isSuccessful) {
                                val contributionEntity = responseContribution.body() as Contribution

                                withContext(Dispatchers.Main) {
                                    contributions[contributionEntity] =
                                        RetrofitClient.memberContributionWebService
                                            .getMemberContributionsByContributionId(
                                                contributionEntity.id!!
                                            )
                                            .body()!!
                                }

                            }
                        }
                    }
                }
            }catch (e: Exception){
                errorMessage = "Error: ${e.message}"
            }
            finally {
                isLoading = false
            }


        }

    }
}