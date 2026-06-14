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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList
import kotlin.collections.forEach

class ContributionViewModel: ViewModel() {

    var isLoading by mutableStateOf(true)
    var errorMessage: String? by mutableStateOf(null)
    var contributions: MutableMap<Contribution, List<MemberContribution>> = mutableStateMapOf()


    fun createContributionAndMemberContributions(contribution: Contribution, members: List<HouseholdMember>, totalAmount: Double){

        viewModelScope.launch(Dispatchers.Main) {
            isLoading = true
            errorMessage = null

            try {

                //first need to create the contribution
                val responseContribution = withContext(Dispatchers.IO){
                    RetrofitClient.contributionWebService.createContribution(contribution)
                }

                //now the scawy part - the membersContributions
                if(responseContribution.isSuccessful && responseContribution.body()!=null){
                    members.forEach {
                        RetrofitClient.memberContributionWebService
                            .createMemberContribution(MemberContribution(
                                responseContribution.body()!!.id!!,
                                it.id,
                                (totalAmount/members.size),
                            ))
                    }
                }
                else{
                    errorMessage = "Error: ${responseContribution.message()} and ${responseContribution.code()}"
                }
            }catch (e: Exception){
                errorMessage = "Error: ${e.message}"
            }finally {
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