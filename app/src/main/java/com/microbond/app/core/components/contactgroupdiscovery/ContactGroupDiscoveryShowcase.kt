package com.microbond.app.core.components.contactgroupdiscovery

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.ContactMember
import com.microbond.app.core.network.DiscoverableContactGroup
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

/**
 * Showcase component demonstrating all 3 contact-group discovery patterns.
 */
@Composable
fun ContactGroupDiscoveryShowcase(
    groups: List<DiscoverableContactGroup>,
    onGroupClick: (DiscoverableContactGroup) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MicrobondDarkBackground)
            .padding(vertical = 16.dp)
    ) {
        // Pattern 1 Showcase
        Text(
            text = "Pattern 1: Dual Overlapping Cutout (Recommended)",
            color = MicrobondTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            groups.forEach { group ->
                InstagramStyleContactGroupItem(group = group, onClick = onGroupClick)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Pattern 2 Showcase
        Text(
            text = "Pattern 2: Contact Avatar + Mini Group Pill",
            color = MicrobondTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            groups.forEach { group ->
                ContactWithGroupPillItem(group = group, onClick = onGroupClick)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Pattern 3 Showcase
        Text(
            text = "Pattern 3: Interlocking Dual-Circle (50/50 Duet)",
            color = MicrobondTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            groups.forEach { group ->
                DuetStyleContactGroupItem(group = group, onClick = onGroupClick)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141A)
@Composable
private fun ContactGroupDiscoveryShowcasePreview() {
    val sampleGroups = listOf(
        DiscoverableContactGroup(
            id = "1",
            name = "Kampala Savers",
            mutualContactsCount = 3,
            contactMembers = listOf(ContactMember(profileName = "Simon H."))
        ),
        DiscoverableContactGroup(
            id = "2",
            name = "Tech Circle",
            mutualContactsCount = 1,
            contactMembers = listOf(ContactMember(profileName = "A\$H"))
        ),
        DiscoverableContactGroup(
            id = "3",
            name = "Wedding Prep",
            mutualContactsCount = 4,
            contactMembers = listOf(ContactMember(profileName = "Ben K."))
        )
    )

    ContactGroupDiscoveryShowcase(groups = sampleGroups, onGroupClick = {})
}
