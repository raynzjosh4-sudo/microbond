package com.microbond.app

import com.google.gson.Gson
import com.microbond.app.core.network.api.deposits.CategorySummary
import com.microbond.app.core.network.api.deposits.DailyDepositWidgetData
import com.microbond.app.core.network.api.deposits.DailyDepositWidgetResponse
import com.microbond.app.core.network.api.deposits.DepositMemberItem
import com.microbond.app.core.network.api.deposits.DepositMembersResponse
import com.microbond.app.core.network.api.deposits.DepositStatusFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DepositWidgetModelsTest {

    private val gson = Gson()

    @Test
    fun testDepositStatusFilterParsing() {
        assertEquals(DepositStatusFilter.FULL, DepositStatusFilter.fromKey("FULL"))
        assertEquals(DepositStatusFilter.FULL, DepositStatusFilter.fromKey("full"))
        assertEquals(DepositStatusFilter.PARTIAL, DepositStatusFilter.fromKey("PARTIAL"))
        assertEquals(DepositStatusFilter.PARTIAL, DepositStatusFilter.fromKey("HALF"))
        assertEquals(DepositStatusFilter.PARTIAL, DepositStatusFilter.fromKey("half"))
        assertEquals(DepositStatusFilter.NOT_DEPOSITED, DepositStatusFilter.fromKey("NOT_DEPOSITED"))
        assertEquals(DepositStatusFilter.ALL, DepositStatusFilter.fromKey("ALL"))
        assertEquals(DepositStatusFilter.ALL, DepositStatusFilter.fromKey("invalid_key"))
    }

    @Test
    fun testDailyDepositWidgetResponseDeserialization() {
        val json = """
        {
          "status": "success",
          "data": {
            "group_id": "74ade444-20d8-46d7-879b-86663d52e8ad",
            "date": "2026-10-21",
            "target_amount_per_member": 20000,
            "total_members": 25,
            "summary": {
              "full": {
                "count": 12,
                "total_amount": 240000.0,
                "percentage_of_members": 48.0
              },
              "partial": {
                "count": 5,
                "total_amount": 50000.0,
                "percentage_of_members": 20.0
              },
              "not_deposited": {
                "count": 8,
                "total_amount": 0.0,
                "percentage_of_members": 32.0
              }
            },
            "overall": {
              "total_expected": 500000.0,
              "total_collected": 290000.0,
              "collection_rate": 58.0
            }
          }
        }
        """.trimIndent()

        val response = gson.fromJson(json, DailyDepositWidgetResponse::class.java)
        assertNotNull(response)
        assertEquals("success", response.status)
        assertEquals(25, response.data.totalMembers)
        assertEquals(12, response.data.summary.full.count)
        assertEquals(5, response.data.summary.partial.count)
        assertEquals(8, response.data.summary.notDeposited.count)
        assertEquals(58.0, response.data.overall.collectionRate, 0.01)
    }

    @Test
    fun testDepositMembersResponseDeserialization() {
        val json = """
        {
          "status": "success",
          "data": {
            "group_id": "74ade444-20d8-46d7-879b-86663d52e8ad",
            "date": "2026-10-21",
            "filter_status": "NOT_DEPOSITED",
            "count": 1,
            "members": [
              {
                "user_id": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
                "membership_id": "mem_01",
                "full_name": "Charlie Kato",
                "profile_picture_url": "https://pub.r2.dev/avatars/kato.jpg",
                "avatar_color": "#00D664",
                "phone_number": "+256 701 *** 890",
                "role": "MEMBER",
                "is_online": true,
                "last_seen_at": 1792618000000,
                "amount_required": 20000,
                "amount_paid": 0,
                "amount_remaining": 20000,
                "percentage_paid": 0.0,
                "status": "NOT_DEPOSITED",
                "can_nudge": true
              }
            ]
          }
        }
        """.trimIndent()

        val response = gson.fromJson(json, DepositMembersResponse::class.java)
        assertNotNull(response)
        assertEquals(1, response.data.members.size)
        val member = response.data.members.first()
        assertEquals("Charlie Kato", member.fullName)
        assertTrue(member.isOnline)
        assertTrue(member.canNudge)
        assertEquals(20000L, member.amountRemaining)
    }
}
