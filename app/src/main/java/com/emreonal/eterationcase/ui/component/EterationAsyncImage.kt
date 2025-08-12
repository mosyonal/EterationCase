package com.emreonal.eterationcase.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.emreonal.eterationcase.R

@Composable
fun EterationAsyncImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    placeholderRes: Int = R.drawable.ic_product_placeholder,
    errorRes: Int = placeholderRes,
    contentScale: ContentScale = ContentScale.Crop
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier,
        placeholder = painterResource(id = placeholderRes),
        error = painterResource(id = errorRes)
    )
}
