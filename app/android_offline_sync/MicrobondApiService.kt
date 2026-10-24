package com.microbond.app.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

data class RemoteObligation(
    val id: String,
    val status: String,
    val amountPaid: Long,
    val updatedAt: Long
)

interface MicrobondApiService {

    @POST("api/v1/group/association-dues/pay")
    suspend fun payObligation(
        @Path("obligationId") obligationId: String,
        @Body payloadJson: String
    ): Response<RemoteObligation>
}
