package com.microbond.app.core.dummydata

import androidx.compose.ui.graphics.Color
import com.microbond.app.R
import com.microbond.app.core.data_classes_and_enums.ChartPreview
import com.microbond.app.core.data_classes_and_enums.GroupSavingsType
import com.microbond.app.core.data_classes_and_enums.MemberSaveStatus
import com.microbond.app.core.data_classes_and_enums.MessageStatus
import com.microbond.app.core.data_classes_and_enums.ThreadEvent
import com.microbond.app.core.data_classes_and_enums.ThreadBubbleType

private val chatStatuses = listOf(
    listOf(
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.MISSED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED
    ),
    listOf(
        MemberSaveStatus.MISSED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.MISSED,
        MemberSaveStatus.SAVED
    ),
    listOf(
        MemberSaveStatus.SAVED,
        MemberSaveStatus.PENDING,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.MISSED
    ),
    listOf(
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.PENDING,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.MISSED
    ),
    listOf(
        MemberSaveStatus.MISSED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.MISSED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.PENDING
    ),
    listOf(
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.MISSED,
        MemberSaveStatus.PENDING
    ),
    listOf(
        MemberSaveStatus.SAVED,
        MemberSaveStatus.PENDING,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED
    ),
    listOf(
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.MISSED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED
    ),
    listOf(
        MemberSaveStatus.SAVED,
        MemberSaveStatus.MISSED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED
    )
)

public val sampleChats = listOf(
    ChartPreview("Reach out to us now (You)", "M", "Microbond", "Yesterday", avatarColor = Color(0xFF527E65), messageStatus = MessageStatus.DELIVERED, memberStatuses = chatStatuses[0]),
    ChartPreview("Justin", "J", "I don't know", "11:50 AM", isFavorite = true, avatarColor = Color(0xFF8D765F), messageStatus = MessageStatus.PENDING, memberStatuses = chatStatuses[1]),
    ChartPreview("Dijah", "D", "Have a nice sleeping day", "11:04 AM", unreadCount = 1, isFavorite = true, avatarColor = Color(0xFF9D746A), messageStatus = MessageStatus.READ, memberStatuses = chatStatuses[2]),
    ChartPreview("+255 755 610171", "7", "Yes papa", "Yesterday", avatarColor = Color(0xFF617F91), messageStatus = MessageStatus.SENT, memberStatuses = chatStatuses[3]),
    ChartPreview("Denes", "D", "Ok", "Yesterday", unreadCount = 2, avatarColor = Color(0xFF96665C), messageStatus = MessageStatus.FAILED, memberStatuses = chatStatuses[4]),
    ChartPreview("Naji Mc Bedings", "N", "Voice call", "9/24/26", avatarColor = Color(0xFF765A8D), messageStatus = MessageStatus.DELIVERED, memberStatuses = chatStatuses[5]),
    ChartPreview(
        "+255 752 466503",
        "7",
        "Voice message (0:04)",
        "9/24/26",
        isFavorite = true,
        avatarColor = Color(0xFF527B91), messageStatus = MessageStatus.READ, memberStatuses = chatStatuses[6]
    ),
    ChartPreview("JAKANANA MIKANDO-BEDINGS", "J", "Photo", "9/23/26", isGroup = true, avatarColor = Color(0xFF8A795A), messageStatus = MessageStatus.DELIVERED, memberStatuses = chatStatuses[7]),
    ChartPreview("Microbond Community", "M", "Welcome to the community", "9/22/26", isGroup = true, avatarColor = Color(0xFF557D70), messageStatus = MessageStatus.READ, memberStatuses = chatStatuses[8])
)

public data class SampleGroup(
    val name: String,
    val initials: String,
    val latestMessage: String,
    val time: String,
    val unreadCount: Int = 0,
    val isOutgoingMessage: Boolean = true,
    val isFavorite: Boolean = false,
    val isGroup: Boolean = true,
    val avatarColor: Color,
    val messageStatus: MessageStatus = MessageStatus.DELIVERED,
    val groupType: GroupSavingsType = GroupSavingsType.LONG_TERM,
    val isPublic: Boolean = false,
    val memberStatuses: List<MemberSaveStatus> = emptyList()
)

public val sampleSavingsGroups = listOf(
    SampleGroup(
        name = "Sunrise Vault",
        initials = "S",
        latestMessage = "Monthly contribution approved",
        time = "9:41 AM",
        unreadCount = 2,
        avatarColor = Color(0xFF3E8C6C),
        messageStatus = MessageStatus.READ,
        groupType = GroupSavingsType.LONG_TERM,
        isPublic = false,
        memberStatuses = listOf(
            MemberSaveStatus.SAVED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.MISSED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.PENDING
        )
    ),
    SampleGroup(
        name = "Mtaa Payout Circle",
        initials = "M",
        latestMessage = "Your payout turn is this Friday",
        time = "Yesterday",
        unreadCount = 1,
        avatarColor = Color(0xFFB57A2A),
        messageStatus = MessageStatus.DELIVERED,
        groupType = GroupSavingsType.SHORT_TERM,
        isPublic = true,
        memberStatuses = listOf(
            MemberSaveStatus.SAVED,
            MemberSaveStatus.MISSED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.MISSED
        )
    ),
    SampleGroup(
        name = "Growth Vault",
        initials = "G",
        latestMessage = "Goal target reached at 86%",
        time = "Mon",
        unreadCount = 0,
        avatarColor = Color(0xFF2F7D6C),
        messageStatus = MessageStatus.SENT,
        groupType = GroupSavingsType.LONG_TERM,
        isPublic = true,
        memberStatuses = listOf(
            MemberSaveStatus.SAVED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.PENDING,
            MemberSaveStatus.SAVED
        )
    ),
    SampleGroup(
        name = "Harbor ROSCA",
        initials = "H",
        latestMessage = "Next round begins next week",
        time = "Sun",
        unreadCount = 3,
        avatarColor = Color(0xFFD18A32),
        messageStatus = MessageStatus.DELIVERED,
        groupType = GroupSavingsType.SHORT_TERM,
        isPublic = false,
        memberStatuses = listOf(
            MemberSaveStatus.MISSED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.MISSED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.PENDING
        )
    )
)

val dummyThreadHistory = listOf(
    ThreadEvent(1, "Sija kulimba", "9:44 PM", isCurrentUser = false, senderName = "~ Am Eli", senderPhone = "+256 704 438291", senderInitials = "A", senderColor = Color(0xFF75D7C7), senderAvatarRes = null),
    ThreadEvent(2, "St Mary's 😂", "9:46 PM", isCurrentUser = false, type = ThreadBubbleType.REPLY, senderName = "~ josephjonez256", senderPhone = "+256 708 870800", senderInitials = "J", senderColor = Color(0xFFE77DA2), senderAvatarRes = R.drawable.img_965037026417633943, quotedSender = "~ Am Eli", quotedText = "Sija kulimba"),
    ThreadEvent(3, "Check out this place", "9:46 PM", isCurrentUser = false, type = ThreadBubbleType.IMAGE, imageRes = R.drawable.img_fidelis, senderName = "~ Am Eli", senderPhone = "+256 704 438291", senderInitials = "A", senderColor = Color(0xFF75D7C7), senderAvatarRes = null),
    ThreadEvent(4, "0:08", "9:47 PM", isCurrentUser = false, type = ThreadBubbleType.VOICE, senderName = "~ Am Eli", senderPhone = "+256 704 438291", senderInitials = "A", senderColor = Color(0xFF75D7C7), senderAvatarRes = null),
    ThreadEvent(5, "0:19", "9:50 PM", isCurrentUser = false, type = ThreadBubbleType.VOICE, senderName = "~ josephjonez256", senderPhone = "+256 708 870800", senderInitials = "J", senderColor = Color(0xFFE77DA2), senderAvatarRes = R.drawable.img_965037026417633943),
    ThreadEvent(6, "Yoo", "6:53 PM", isCurrentUser = false, senderName = "~ Am Eli", senderPhone = "+256 704 438291", senderInitials = "A", senderColor = Color(0xFF75D7C7), senderAvatarRes = null),
    ThreadEvent(7, "Yes pap", "8:03 PM", isCurrentUser = true, status = MessageStatus.DELIVERED),
    ThreadEvent(8, "Plan kitty broda", "8:20 PM", isCurrentUser = false, type = ThreadBubbleType.REPLY, senderName = "~ josephjonez256", senderPhone = "+256 708 870800", senderInitials = "J", senderColor = Color(0xFFE77DA2), senderAvatarRes = R.drawable.img_965037026417633943, quotedSender = "You", quotedText = "Yes pap"),
    ThreadEvent(9, "Wagana sente", "8:27 PM", isCurrentUser = true, status = MessageStatus.READ),
    ThreadEvent(10, "0:18", "8:29 PM", isCurrentUser = true, type = ThreadBubbleType.VOICE, status = MessageStatus.READ, senderAvatarRes = null),
    ThreadEvent(11, "Ok", "10:02 AM", isCurrentUser = false, senderName = "~ Am Eli", senderPhone = "+256 704 438291", senderInitials = "A", senderColor = Color(0xFF75D7C7), senderAvatarRes = null),
    ThreadEvent(12, "See you at the meeting", "10:04 AM", isCurrentUser = false, senderName = "~ josephjonez256", senderPhone = "+256 708 870800", senderInitials = "J", senderColor = Color(0xFFE77DA2), senderAvatarRes = R.drawable.img_965037026417633943),
    ThreadEvent(13, "Looking good", "10:10 AM", isCurrentUser = true, type = ThreadBubbleType.IMAGE, imageRes = R.drawable.img_1086634216382568373, status = MessageStatus.READ),
    ThreadEvent(14, "", "10:11 AM", isCurrentUser = false, type = ThreadBubbleType.IMAGE, imageRes = R.drawable.img_download, senderName = "~ josephjonez256", senderPhone = "+256 708 870800", senderInitials = "J", senderColor = Color(0xFFE77DA2), senderAvatarRes = R.drawable.img_965037026417633943)
)