import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

@Composable
fun safePainterResource(@DrawableRes resId: Int?): Painter? {
    if (resId == null || resId == 0) return null
    return painterResource(resId)
}
