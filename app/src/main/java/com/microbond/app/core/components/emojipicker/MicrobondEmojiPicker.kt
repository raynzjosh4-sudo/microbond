package com.microbond.app.core.components.emojipicker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.SentimentSatisfiedAlt
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import kotlinx.coroutines.launch

enum class EmojiCategory(val title: String, val icon: ImageVector) {
    SMILEYS("Smileys & People", Icons.Outlined.SentimentSatisfiedAlt),
    ANIMALS("Animals & Nature", Icons.Outlined.Pets),
    FOOD("Food & Drink", Icons.Outlined.Coffee),
    ACTIVITIES("Activities", Icons.Outlined.SportsSoccer),
    TRAVEL("Travel & Places", Icons.Outlined.DirectionsCar),
    OBJECTS("Objects", Icons.Outlined.Lightbulb),
    SYMBOLS("Symbols", Icons.Outlined.Tag),
    FLAGS("Flags", Icons.Outlined.Flag)
}

/**
 * WhatsApp-style Emoji Picker Component matching the user's design screenshot.
 * Features:
 * - Top control row with Search, centered Emoji pill selector, and Backspace button
 * - Category section header
 * - 8-column emoji grid
 * - Bottom category switcher bar
 */
@Composable
fun MicrobondEmojiPicker(
    modifier: Modifier = Modifier,
    onEmojiSelected: (String) -> Unit,
    onBackspace: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val currentCategory = EmojiCategory.entries[selectedCategoryIndex]
    val gridState = rememberLazyGridState()

    val currentEmojis = remember(selectedCategoryIndex, searchQuery, isSearchActive) {
        if (isSearchActive && searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            ALL_EMOJIS_FLAT.filter { it.contains(q) || it in ALL_EMOJIS_FLAT }
        } else {
            EMOJI_DATA[currentCategory] ?: emptyList()
        }
    }

    LaunchedEffect(selectedCategoryIndex) {
        gridState.scrollToItem(0)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0C1317))
    ) {
        // ── Drag Handle / Top Divider ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF374248))
            )
        }

        // ── Top Control Bar (Search, Center Emoji Capsule Pill, Backspace) ──────
        if (isSearchActive) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MicrobondInactiveGray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search emoji...", color = MicrobondInactiveGray, fontSize = 14.sp) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = MicrobondTextPrimary,
                        unfocusedTextColor = MicrobondTextPrimary,
                        cursorColor = MicrobondActiveGreen,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = TextStyle(fontSize = 14.sp),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    searchQuery = ""
                    isSearchActive = false
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Search",
                        tint = MicrobondInactiveGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Search icon (Left)
                IconButton(
                    onClick = { isSearchActive = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Emoji",
                        tint = MicrobondInactiveGray,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Center Emoji Pill Capsule
                Box(
                    modifier = Modifier
                        .width(110.dp)
                        .height(30.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color(0xFF202C33)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SentimentSatisfiedAlt,
                        contentDescription = "Emoji mode",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Backspace button (Right)
                IconButton(
                    onClick = onBackspace,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Backspace",
                        tint = MicrobondInactiveGray,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // ── Category Header ────────────────────────────────────────────────────
        Text(
            text = if (isSearchActive && searchQuery.isNotBlank()) "Search results" else currentCategory.title,
            color = MicrobondInactiveGray,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 16.dp, top = 6.dp, bottom = 4.dp)
        )

        // ── 8-Column Emoji Grid ────────────────────────────────────────────────
        LazyVerticalGrid(
            columns = GridCells.Fixed(8),
            state = gridState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 6.dp)
        ) {
            items(currentEmojis, key = { it }) { emoji ->
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onEmojiSelected(emoji)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        fontSize = 24.sp
                    )
                }
            }
        }

        HorizontalDivider(color = Color(0xFF1F2C34), thickness = 0.8.dp)

        // ── Bottom Category Tab Row ───────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(Color(0xFF0B141B))
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            EmojiCategory.entries.forEachIndexed { index, category ->
                val isSelected = !isSearchActive && selectedCategoryIndex == index
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            isSearchActive = false
                            selectedCategoryIndex = index
                            coroutineScope.launch { gridState.scrollToItem(0) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = category.title,
                        tint = if (isSelected) Color.White else Color(0xFF6B7B85),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ── Emoji dataset categorised by category matching WhatsApp ──────────────────
private val EMOJI_DATA = mapOf(
    EmojiCategory.SMILEYS to listOf(
        "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣",
        "🥲", "🥹", "☺️", "😊", "😇", "🙂", "🙃", "😉",
        "😌", "😍", "🥰", "😘", "😗", "😙", "😚", "😋",
        "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎",
        "🥸", "🤩", "🥳", "😏", "😒", "😞", "😔", "😟",
        "😕", "🙁", "☹️", "😣", "😖", "😫", "😩", "🥺",
        "😢", "😭", "😮‍💨", "😤", "😠", "😡", "🤬", "🤯",
        "😳", "🥵", "🥶", "😱", "😨", "😰", "😥", "😓",
        "🫣", "🤗", "🫡", "🤔", "🫢", "🤫", "🤥", "😶",
        "😶‍🌫️", "😐", "😑", "😬", "🫨", "🫠", "🙄", "😯",
        "😦", "😧", "😮", "😲", "🥱", "😴", "🤤", "😪",
        "😵", "😵‍💫", "🫥", "🤐", "🥴", "🤢", "🤮", "🤧",
        "😷", "🤒", "🤕", "🤑", "🤠", "😈", "👿", "👹",
        "👺", "🤡", "💩", "👻", "💀", "☠️", "👽", "👾",
        "🤖", "🎃", "😺", "😸", "😹", "😻", "😼", "😽",
        "🙀", "😿", "😾", "👋", "🤚", "🖐️", "✋", "🖖",
        "🫱", "🫲", "🫳", "🫴", "🫵", "👌", "🤌", "🤏",
        "✌️", "🤞", "🫰", "🤟", "🤘", "🤙", "👈", "👉",
        "👆", "🖕", "👇", "☝️", "👍", "👎", "✊", "👊",
        "🤛", "🤜", "👏", "🙌", "🫶", "👐", "🤲", "🤝",
        "🙏", "✍️", "💅", "🤳", "💪", "🦾", "🦿", "🦵",
        "🦶", "👂", "🦻", "👃", "🫀", "🫁", "🧠", "🦷",
        "🦴", "👀", "👁️", "👅", "👄", "🫦", "💋", "❤️"
    ),
    EmojiCategory.ANIMALS to listOf(
        "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼",
        "🐨", "🐯", "🦁", "🐮", "🐷", "🐽", "🐸", "🐵",
        "🐔", "🐧", "🐦", "🐤", "🐣", "🐥", "🦆", "🦅",
        "🦉", "🦇", "🐺", "🐗", "🐴", "🦄", "🐝", "🪱",
        "🐛", "🦋", "🐌", "🐞", "🐜", "🪰", "🪲", "🪳",
        "🦟", "🦗", "🕷️", "🦂", "🐢", "🐍", "🦎", "🦖",
        "🦕", "🐙", "🦑", "🦐", "🦞", "🦀", "🐡", "🐠",
        "🐟", "🐬", "🐳", "🐋", "🦈", "🦭", "🐊", "🐅",
        "🐆", "🦓", "🦍", "🦧", "🦣", "🐘", "🦛", "🦏",
        "🐪", "🐫", "🦒", "🦘", "🦬", "🐃", "🐂", "🐄",
        "🐎", "🐖", "🐏", "🐑", "🦙", "🐐", "🦌", "🐕",
        "🐩", "🦮", "🐕‍🦺", "🐈", "🐈‍⬛", "🪶", "🐓", "🦃",
        "🦤", "🦚", "🦜", "🦢", "🦩", "🕊️", "🐇", "🦝",
        "🦨", "🦡", "🦫", "🦦", "🦥", "🐁", "🐀", "🐿️",
        "🦔", "🐾", "🐉", "🐲", "🌵", "🎄", "🌲", "🌳",
        "🌴", "🪵", "🌱", "🌿", "☘️", "🍀", "🎍", "🪴",
        "🍃", "🍂", "🍁", "🍄", "🐚", "🪨", "🌾", "💐",
        "🌷", "🌹", "🥀", "🌺", "🌸", "🌼", "🌻", "🌞"
    ),
    EmojiCategory.FOOD to listOf(
        "🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇",
        "🍓", "🫐", "🍈", "🍒", "🍑", "🥭", "🍍", "🥥",
        "🥝", "🍅", "🍆", "🥑", "🥦", "🥬", "🥒", "🌶️",
        "🫑", "🌽", "🥕", "🫒", "🧄", "🧅", "🥔", "🍠",
        "🥐", "🥯", "🍞", "🥖", "🥨", "🧀", "🥚", "🍳",
        "🧈", "🥞", "🧇", "🥓", "🥩", "🍗", "🍖", "🦴",
        "🌭", "🍔", "🍟", "🍕", "🫓", "🥪", "🥙", "🧆",
        "🌮", "🌯", "🫔", "🥗", "🥘", "🫕", "🥫", "🍝",
        "🍜", "🍲", "🍛", "🍣", "🍱", "🥟", "🦪", "🍤",
        "🍙", "🍚", "🍘", "🍥", "🥠", "🥮", "🍢", "🍡",
        "🍧", "🍨", "🍦", "🥧", "🧁", "🍰", "🎂", "🍮",
        "🍭", "🍬", "🍫", "🍿", "🍩", "🍪", "🌰", "🥜",
        "🍯", "🥛", "🍼", "☕", "🫖", "🍵", "🍶", "🍾",
        "🍷", "🍸", "🍹", "🍺", "🍻", "🥂", "🥃", "🥤",
        "🧋", "🧃", "🧉", "🧊", "🥢", "🍽️", "🍴", "🥄"
    ),
    EmojiCategory.ACTIVITIES to listOf(
        "⚽", "🏀", "🏈", "⚾", "🥎", "🎾", "🏐", "🏉",
        "🥏", "🎱", "🪀", "🏓", "🏸", "🏒", "🏑", "🥍",
        "🏏", "🪃", "🥅", "⛳", "🪁", "🏹", "🎣", "🤿",
        "🥊", "🥋", "🎽", "🛹", "🛼", "🛷", "⛸️", "🥌",
        "🎿", "⛷️", "🏂", "🪂", "🏋️", "🤼", "🤸", "⛹️",
        "🤺", "🤾", "🏌️", "🏇", "🧘", "🏄", "🏊", "🤽",
        "🚣", "🧗", "🚵", "🚴", "🏆", "🥇", "🥈", "🥉",
        "🏅", "🎖️", "🏵️", "🎗️", "🎫", "🎟️", "🎪", "🤹",
        "🎭", "🩰", "🎨", "🎬", "🎤", "🎧", "🎼", "🎹",
        "🥁", "🎷", "🎺", "🎸", "🪕", "🎻", "🎲", "♟️",
        "🎯", "🎳", "🎮", "🎰", "🧩"
    ),
    EmojiCategory.TRAVEL to listOf(
        "🚗", "🚕", "🚙", "🚌", "🚎", "🏎️", "🚓", "🚑",
        "🚒", "🚐", "🛻", "🚚", "🚛", "🚜", "🛴", "🚲",
        "🛵", "🏍️", "🛺", "🚨", "🚔", "🚍", "🚘", "🚖",
        "🚡", "🚠", "🚟", "🚃", "🚋", "🚞", "🚝", "🚄",
        "🚅", "🚈", "🚂", "🚆", "🚇", "🚊", "🚉", "✈️",
        "🛫", "🛬", "🛩️", "💺", "🛰️", "🚀", "🛸", "🚁",
        "🛶", "⛵", "🚤", "🛥️", "🛳️", "⛴️", "🚢", "⚓",
        "🛟", "⛽", "🚧", "🚦", "🚥", "🚏", "🗺️", "🗿",
        "🗽", "🗼", "🏰", "🏯", "🏟️", "🎡", "🎢", "🎠",
        "⛲", "⛱️", "🏖️", "🏝️", "🏜️", "🌋", "⛰️", "🏔️",
        "🏕️", "⛺", "🛖", "🏠", "🏡", "🏘️", "🏚️", "🏗️",
        "🏢", "🏬", "🏣", "🏤", "🏥", "🏦", "🏨", "🏪",
        "🏫", "🏛️", "⛪", "🕌", "🛕", "🕍", "⛩️", "🕋"
    ),
    EmojiCategory.OBJECTS to listOf(
        "⌚", "📱", "📲", "💻", "⌨️", "🖥️", "🖨️", "🖱️",
        "💽", "💾", "💿", "📀", "📷", "📸", "📹", "🎥",
        "📽️", "🎞️", "📞", "☎️", "📟", "📠", "📺", "📻",
        "🎙️", "🎚️", "🎛️", "⏱️", "⏲️", "⏰", "🕰️", "⌛",
        "⏳", "📡", "🔋", "🔌", "💡", "🔦", "🕯️", "🧯",
        "🗑️", "🛢️", "💸", "💵", "💴", "💶", "💷", "🪙",
        "💰", "💳", "💎", "⚖️", "🪜", "🧰", "🔧", "🔨",
        "⚒️", "🛠️", "⛏️", "🪚", "🔩", "⚙️", "🧱", "⛓️",
        "🧲", "🔫", "💣", "🧨", "🪓", "🔪", "🗡️", "⚔️",
        "🛡️", "🔮", "📿", "🧿", "💈", "⚗️", "🔭", "🔬",
        "🩹", "🩺", "💊", "💉", "🧬", "🧪", "🌡️", "🧹",
        "🪠", "🧺", "🧻", "🚽", "🚰", "🚿", "🛁", "🧼",
        "🔑", "🗝️", "🚪", "🪑", "🛋️", "🛏️", "🛍️", "🛒",
        "🎁", "🎈", "🎏", "🎀", "🎊", "🎉", "✉️", "📦"
    ),
    EmojiCategory.SYMBOLS to listOf(
        "💘", "💝", "💖", "💗", "💓", "💞", "💕", "💟",
        "💔", "❤️", "🧡", "💛", "💚", "💙", "💜", "🤎",
        "🖤", "🤍", "💯", "💢", "💥", "💫", "💦", "💨",
        "🕳️", "💬", "👁️‍🗨️", "🗨️", "🗯️", "💭", "💤", "♨️",
        "🛑", "🕛", "🕧", "🕐", "🕜", "🕑", "🕝", "🕒",
        "🔇", "🔈", "🔉", "🔊", "📢", "📣", "🔔", "🔕",
        "🎵", "🎶", "🏧", "♿", "🚹", "🚺", "🚻", "🚼",
        "🚾", "⚠️", "🚸", "⛔", "🚫", "🚳", "🚭", "🚯",
        "⬆️", "↗️", "➡️", "↘️", "⬇️", "↙️", "⬅️", "↖️",
        "↕️", "↔️", "↩️", "↪️", "⤴️", "⤵️", "🔃", "🔄",
        "🔙", "🔚", "🔛", "🔜", "🔝", "✝️", "☪️", "🕉️",
        "✡️", "☸️", "☯️", "♈", "♉", "♊", "♋", "♌",
        "♍", "♎", "♏", "♐", "♑", "♒", "♓", "⛎",
        "▶️", "⏸️", "⏹️", "⏺️", "⏏️", "🔅", "🔆", "📶",
        "✔️", "❌", "❓", "❗", "©️", "®️", "™️", "💲"
    ),
    EmojiCategory.FLAGS to listOf(
        "🏁", "🚩", "🎌", "🏴", "🏳️", "🏳️‍🌈", "🏳️‍⚧️", "🏴‍☠️",
        "🇺🇬", "🇰🇪", "🇹🇿", "🇷🇼", "🇧🇮", "🇸🇸", "🇳🇬", "🇬🇭",
        "🇿🇦", "🇪🇬", "🇪🇹", "🇨🇩", "🇿🇲", "🇿🇼", "🇲🇼", "🇲🇿",
        "🇺🇸", "🇬🇧", "🇨🇦", "🇩🇪", "🇫🇷", "🇮🇹", "🇪🇸", "🇵🇹",
        "🇳🇱", "🇧🇪", "🇨🇭", "🇸🇪", "🇳🇴", "🇩🇰", "🇫🇮", "🇵🇱",
        "🇨🇳", "🇯🇵", "🇰🇷", "🇮🇳", "🇦🇺", "🇳🇿", "🇧🇷", "🇲🇽",
        "🇦🇷", "🇨🇱", "🇨🇴", "🇵🇪", "🇸🇦", "🇦🇪", "🇹🇷", "🇮🇱"
    )
)

private val ALL_EMOJIS_FLAT: List<String> by lazy {
    EMOJI_DATA.values.flatten().distinct()
}
