package me.theclashfruit.rithle.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder

@Composable
fun SvgStringIcon(
    svgString: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    val context = LocalContext.current

    val model = ImageRequest.Builder(context)
        .data(svgString.encodeToByteArray())
        .decoderFactory(SvgDecoder.Factory())
        .build()

    AsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier,
        colorFilter = if (tint != Color.Unspecified) androidx.compose.ui.graphics.ColorFilter.tint(tint) else null
    )
}