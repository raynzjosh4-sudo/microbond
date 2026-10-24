package com.microbond.app.core.components.threadbubbles.bubbletype.callandcontactbubble

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.avatar.MicroBondAvatar
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.MicroBondInk
import com.microbond.app.core.components.threadbubbles.MicroBondMuted
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.components.threadbubbles.widgets.statusicon.MicroBondStatusIcon
import com.microbond.app.core.data_classes_and_enums.ContactBubbleData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MicroBondContactBubble(
    contacts: List<ContactBubbleData>,
    meta: MicroBondMeta,
    modifier: Modifier = Modifier
) {
    var expanded by remember(contacts) { mutableStateOf(false) }

    MicroBondBubbleSurface(
        meta = meta,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 1.dp),
        showTimestamp = false
    ) {
        when {
            contacts.size > 1 && !expanded -> {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ContactAvatar(contacts.first(), size = 40.dp)
                        Text(
                            text = "${contacts.first().name} and ${contacts.size - 1} other ${if (contacts.size == 2) "contact" else "contacts"}",
                            color = Color(0xFF25D366),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        ContactTimeStatus(meta)
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MicroBondMuted.copy(alpha = 0.22f))
                    )
                    Text(
                        text = "View all",
                        color = Color(0xFF25D366),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                            .padding(vertical = 5.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            else -> {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    contacts.forEachIndexed { index, contact ->
                        ContactBubbleItem(
                            contact = contact,
                            meta = meta.takeIf { index == contacts.lastIndex }
                        )
                        if (index < contacts.lastIndex) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(MicroBondMuted.copy(alpha = 0.22f))
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (contacts.size > 1) {
                            Text(
                                text = "Show less",
                                color = Color(0xFF25D366),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { expanded = false }
                                    .padding(vertical = 5.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactTimeStatus(meta: MicroBondMeta) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(meta.time, color = MicroBondMuted, fontSize = 11.sp)
        if (meta.outgoing) MicroBondStatusIcon(meta.status)
    }
}

@Composable
private fun ContactBubbleItem(contact: ContactBubbleData, meta: MicroBondMeta?) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ContactAvatar(contact, size = 44.dp)
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = contact.name,
                        color = Color(0xFF25D366),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = contact.phone,
                        color = MicroBondMuted,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                meta?.let { ContactTimeStatus(it) }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MicroBondMuted.copy(alpha = 0.18f))
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("Invite", color = Color(0xFF25D366), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(
                modifier = Modifier
                    .height(18.dp)
                    .size(width = 1.dp, height = 18.dp)
                    .background(MicroBondMuted.copy(alpha = 0.18f))
            )
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("Save", color = Color(0xFF25D366), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun ContactAvatar(contact: ContactBubbleData, size: androidx.compose.ui.unit.Dp) {
    val context = LocalContext.current
    val bitmap by produceState<Bitmap?>(initialValue = null, contact.photoUri) {
        value = withContext(Dispatchers.IO) {
            contact.photoUri?.let { photoUri ->
                runCatching {
                    context.contentResolver.openInputStream(Uri.parse(photoUri))?.use(BitmapFactory::decodeStream)
                }.getOrNull()
            }
        }
    }

    MicroBondAvatar(
        painter = bitmap?.asImageBitmap()?.let(::BitmapPainter),
        name = contact.name,
        modifier = Modifier.size(size),
        backgroundColor = Color(0xFF25D366),
        fallbackContent = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
            tint = Color.White,
                modifier = Modifier.size(size * 0.62f)
            )
        }
    )
}
