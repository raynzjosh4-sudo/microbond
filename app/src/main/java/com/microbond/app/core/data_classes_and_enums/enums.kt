package com.microbond.app.core.data_classes_and_enums

enum class MessagesFabFlow {
    CREATE_GROUP,
    INVITE_A_FREIND
}

enum class GroupSavingsType {
    LONG_TERM,
    SHORT_TERM
}

enum class SavingsGroupType(val uiLabel: String) {
    MERRY_GO_ROUND("Rotating Savings"),
    GOAL_SAVINGS("Goal Savings"),
    BILL_SPLIT("Bill Split"),
    WELFARE_FUND("Welfare & Emergency"),
    EVENT_COMMITTEE("Event Committee"),
    INVESTMENT_CLUB("Investment Club"),
    FUNDRAISER("Public Fundraiser"),
    SHARED_WALLET("Shared Wallet"),
    ASSOCIATION_DUES("Membership Dues")
}

enum class MemberSaveStatus {
    SAVED, //Green ring segment
    MISSED, //Red ring segment
    PENDING // Dark gray segment
}

// status ticks enums
enum class MessageStatus {
    PENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED
}