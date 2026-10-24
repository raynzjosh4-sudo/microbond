package com.microbond.app.core.components.countrypicker

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

/**
 * Choose a country full-screen picker.
 * Emulates the messaging reference UI with country flags, native localized names,
 * international dial codes, and live search filtering.
 */
@Composable
fun ChooseCountryScreen(
    onCountrySelected: (Country) -> Unit,
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    BackHandler {
        if (isSearching) {
            isSearching = false
            searchQuery = ""
        } else {
            onNavigateBack()
        }
    }

    val displayedCountries = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            // Combines popular countries at the top with full list, eliminating duplicates
            val popular = CountryData.popularCountries
            val rest = CountryData.allCountries.filterNot { all ->
                popular.any { it.isoCode == all.isoCode }
            }
            popular + rest
        } else {
            CountryData.search(searchQuery)
        }
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (isSearching) {
                        isSearching = false
                        searchQuery = ""
                    } else {
                        onNavigateBack()
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MicrobondTextPrimary
                    )
                }

                if (isSearching) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = TextStyle(
                            color = MicrobondTextPrimary,
                            fontSize = 18.sp
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(MicrobondActiveGreen),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search country or code...",
                                    style = TextStyle(
                                        color = MicrobondInactiveGray,
                                        fontSize = 18.sp
                                    )
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .focusRequester(focusRequester)
                    )

                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }

                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear",
                                tint = MicrobondInactiveGray
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Choose a country",
                        style = TextStyle(
                            color = MicrobondTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    )

                    IconButton(onClick = { isSearching = true }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = MicrobondTextPrimary
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalDivider(color = MicrobondDividerDark, thickness = 0.5.dp)

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    items = displayedCountries,
                    key = { "${it.isoCode}_${it.dialCode}" }
                ) { country ->
                    CountryListItem(
                        country = country,
                        onClick = { onCountrySelected(country) }
                    )
                    HorizontalDivider(color = MicrobondDividerDark, thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
private fun CountryListItem(
    country: Country,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (country.flagResId != 0) {
                Image(
                    painter = painterResource(id = country.flagResId),
                    contentDescription = country.name,
                    modifier = Modifier
                        .size(width = 28.dp, height = 20.dp)
                        .clip(RoundedCornerShape(2.dp))
                )
            } else {
                Text(
                    text = country.flagEmoji,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = country.name,
                    style = TextStyle(
                        color = MicrobondTextPrimary,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                if (!country.nativeName.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = country.nativeName,
                        style = TextStyle(
                            color = MicrobondInactiveGray,
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }

        Text(
            text = "+${country.dialCode}",
            style = TextStyle(
                color = MicrobondInactiveGray,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141B)
@Composable
private fun ChooseCountryScreenPreview() {
    ChooseCountryScreen(onCountrySelected = {}, onNavigateBack = {})
}
