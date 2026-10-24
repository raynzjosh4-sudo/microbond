package com.microbond.app.core.dummydata

data class SampleSavingGroupPayment(
    val dailyAmount: Long,
    val mobileMoneyNumber: String,
    val mobileMoneyDisplay: String
)

val sampleSavingGroupPayment = SampleSavingGroupPayment(
    dailyAmount = 12_000L,
    mobileMoneyNumber = "0770123456",
    mobileMoneyDisplay = "MTN +256 770 123 456"
)
