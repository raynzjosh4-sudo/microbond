package com.microbond.app.core.ui.theme

import androidx.compose.ui.graphics.Color

// Microbond Color Palette
val MicrobondDarkBackground = Color(0xFF0B141A) // Primary screen & nav bar background

val MicrobondErrorColor = Color(0xFFE53935)
val MicrobondOverlayWidgetDark= Color(0xFF121E26)
val MicrobondMassageReadColor = Color(0xFF0975E8)
val MicrobondActivePillDark = Color(0xFF103629) // Selected tab pill background
val MicrobondActiveGreen = Color(0xFF25D366)    // Active icons, text & badges
val MicrobondInactiveGray = Color(0xFF8696A0)   // Unselected icons & text
val MicrobondDividerDark = Color(0xFF1F2C34)    // Top divider border line
val MicrobondSearchBarDark = Color(0xFF1F2C34)  // Search bar background
val MicrobondTextPrimary = Color(0xFFE9EDEF) // Primary text header color
val MicrobondGroupColorBackground = Color(0x4A0E651F)

val MicrobondChipNonSelectedColor = Color(0xFF1E2329)
val MicrobondAttachmentSheetDark = Color(0xFF111B21)
val MicrobondAttachmentIconBgDark = Color(0xFF233238)
val MicrobondDragHandleDark = Color(0xFF3B4A54)
val MicrobondAvatarPlaceholderDark = Color(0xFF5E7787)
val MicrobondAvatarIconDark = Color(0xFFCFD8DC)

// Button Tokens
val MicrobondButtonDisabledBackground = Color(0xFF1F2C34) // Muted pill background when disabled
val MicrobondButtonDisabledText = Color(0xFF667781)       // Muted label text when disabled

// Attachment Icon Colors
val MicrobondAttachDepositGreen = Color(0xFF00C853)
val MicrobondAttachReportBlue = Color(0xFF007BFF)
val MicrobondAttachLocationGreen = Color(0xFF00C853)
val MicrobondAttachContactBlue = Color(0xFF00A8F3)
val MicrobondAttachDocumentPurple = Color(0xFF7B61FF)
val MicrobondAttachPollOrange = Color(0xFFFFB300)
val MicrobondAttachRulesOrange = Color(0xFFFFB300)
val MicrobondAttachStatsBlue = Color(0xFF00A8F3)

// Legacy Theme Colors
val microBondTealGreen = Color(0xFF008069)
val microBondTealGreenDark = Color(0xFF075E54)
val microBondLightGreen = Color(0xFF25D366)
val microBondBackgroundLight = Color(0xFFFFFFFF)
val microBondSurfaceLight = Color(0xFFFFFFFF)
val microBondTextPrimaryLight = Color(0xFF111B21)
val microBondTealGreenDarkTheme = Color(0xFF00A884)
val microBondBackgroundDark = Color(0xFF0B141A)
val microBondSurfaceDark = Color(0xFF202C33)
val microBondTextPrimaryDark = Color(0xFFE9EDEF)
val microBondTextSecondaryDark = Color(0xFF8696A0)
val microBondTextSecondaryLight = Color(0xFF667781)
val microBondDarkNavBackground = Color(0xFF0B141A)
val microBondActivePillDark = Color(0xFF103629)
val microBondActiveGreen = Color(0xFF25D366)
val microBondInactiveGray = Color(0xFF8696A0)
val microBondDividerDark = Color(0xFF1F2C34)

// Shared colors
val microBondCheckMarkBlue = Color(0xFF53BDEB)
val microBondChartBackgroundLight = Color(0xFFEFEAE2)
val microBondChartBackgroundDark = Color(0xFF0B141A)
val microBondOutgoingBubbleLight = Color(0xFFD9FDD3)
val microBondBondOutgoingBubbleDark = Color(0xFF005C4B)
val microBondIncomingBubbleLight = Color(0xFFFFFFFF)
val microBondIncomingBubbleDark = Color(0xFF202C33)

// Semantic Action & Destructive Tokens
val MicrobondDestructiveRed = Color(0xFFFF4F4F)
val MicrobondDestructiveDisabledBg = Color(0xFF422222)
val MicrobondDestructiveDisabledText = Color(0xFF886666)
val MicrobondBorderDark = Color(0xFF2C3840)
val MicrobondPureWhite = Color(0xFFFFFFFF)
val MicrobondTransparent = Color(0x00000000)

// Group Creation & Management Tokens
val MicrobondGroupIconBgDark = Color(0xFF183B35)     // Circular group icon background
val MicrobondFabContentDark = Color(0xFF07130F)      // Dark contrast icon on active green FAB
val MicrobondBottomSheetDark = Color(0xFF1E2B32)     // Modal bottom sheet background
val MicrobondDragHandleGray = Color(0xFF5E6C74)      // Sheet drag handle
val MicrobondCameraIconBgDark = Color(0xFF2A3942)    // Avatar photo picker background
val MicrobondGalleryTopBarDark = Color(0xFF111B21)   // Dark top bar for media picker
val MicrobondDefaultContactAvatar = Color(0xFF247A70)// Default contact avatar fallback
val MicrobondLockBadgeBg = Color(0xFF1F2C34)         // Lock status badge background
val MicrobondLockIconColor = Color(0xFF8696A0)       // Lock status icon tint
val MicrobondWarningAmber = Color(0xFFFFB300)        // Unlock requirements & alert accent

// Standard Avatar Palette
val microbondAvatarPalette = listOf(
    Color(0xFF3E8C6C), Color(0xFF527E65), Color(0xFF8D765F),
    Color(0xFF9D746A), Color(0xFF617F91), Color(0xFF96665C),
    Color(0xFF765A8D), Color(0xFF527B91), Color(0xFF8A795A)
)

// Info Widget Banner Tokens
val MicrobondInfoBannerContainer = Color(0xFF0A332B)     // Authentic WhatsApp emerald dark surface
val MicrobondInfoBannerBorder = Color(0xFF154E40)        // Subtle perimeter highlight for elevated card depth
val MicrobondInfoBannerIconBg = Color(0xFF124337)        // Badge background for banner leading icon
val MicrobondInfoBannerButtonContent = Color(0xFF0B141A) // High contrast dark text on banner action button
// Group Intro Card Tokens
val MicrobondCardSurfaceDark = Color(0xFF182229)
val MicrobondCardOutlineDark = Color(0xFF2E3D46)
val MicrobondAmberNotice = Color(0xFFFED37E)

// Deposit Status Widget Tokens
val MicrobondDepositFullGreen = Color(0xFF68E090)       // 🟢 Full deposit pill
val MicrobondDepositHalfYellow = Color(0xFFFFD166)      // 🟡 Half/partial deposit pill
val MicrobondDepositNotPaidRed = Color(0xFFFF6B6B)      // 🔴 Not deposited pill
val MicrobondDepositHeaderIconBg = Color(0xFF0F4A42)    // Group icon circle background
val MicrobondDepositRowBg = Color(0xFF1F2428)           // Status row background
val MicrobondDepositIconBg = Color(0xFF333B40)          // Member icon circle background
val MicrobondOnlineGreen = Color(0xFF00E676)            // Member online indicator dot
val MicrobondNudgeButtonBg = Color(0xFF103629)          // Nudge action button background
val MicrobondNudgeButtonText = Color(0xFF25D366)        // Nudge action label tint
val MicrobondNudgeDisabledBg = Color(0xFF1F2C34)        // Disabled nudge button background
val MicrobondNudgeDisabledText = Color(0xFF667781)      // Disabled nudge button text
val MicrobondPillTextDark = Color(0xFF111B21)           // Dark text on colored pills



