package com.microbond.app.core.dummydata

import androidx.compose.ui.graphics.Color
import com.microbond.app.models.usermodel.KycVerificationStatus
import com.microbond.app.models.usermodel.User
import java.util.UUID

/**
 * Provides a starting / testing user utilizing the official User models.
 * This should be used across the app during development instead of hardcoded strings.
 */
object TestingUserFactory {
    
    val testUser = User(
        id = UUID.randomUUID().toString(),
        firstName = "Rayn",
        lastName = "Z",
        phoneNumber = "+256 700 000 000",
        avatarColor = Color(0xFF3E8C6C),
        aboutStatus = "Building Microbond",
        defaultCurrency = "UGX",
        kycStatus = KycVerificationStatus.VERIFIED,
        trustScore = 30,
        isVerified = true,
        isOnline = true,
        joinedDate = "Jan 2024"
    )
    
}
