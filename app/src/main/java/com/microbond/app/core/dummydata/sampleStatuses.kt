package com.microbond.app.core.dummydata

import com.microbond.app.core.data_classes_and_enums.MemberSaveStatus

val sampleStatuses = listOf(
    MemberSaveStatus.MISSED,
    MemberSaveStatus.SAVED,
    MemberSaveStatus.MISSED,
    MemberSaveStatus.SAVED,
    MemberSaveStatus.SAVED,
    MemberSaveStatus.SAVED,
    MemberSaveStatus.MISSED,
)



val groupStatus = List(6) {
    index -> when {
        index < 32 -> MemberSaveStatus.SAVED
    index < 35 -> MemberSaveStatus.MISSED
    else -> MemberSaveStatus.PENDING
    }
}

//MicrobondGroupAvatar(
//    statuses = groupStatuses,
//    avatarSize = 64.sp
//)