package com.emreonal.eterationcase.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emreonal.eterationcase.R
import com.emreonal.eterationcase.core.noRippleClickable
import com.emreonal.eterationcase.ui.theme.Red
import kotlinx.coroutines.delay

@Composable
fun FavoriteComponent(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit
) {
    var animateTrigger by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (animateTrigger) 1.2f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "favoriteScale"
    )
    LaunchedEffect(animateTrigger) {
        if (animateTrigger) {
            delay(150)
            animateTrigger = false
        }
    }

    Icon(
        modifier = modifier
            .size(30.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .noRippleClickable {
                if (!animateTrigger) {
                    animateTrigger = true
                    onFavoriteToggle()
                }
            },
        painter = painterResource(
            if (isFavorite) {
                R.drawable.ic_favorite
            } else {
                R.drawable.ic_unfavorite
            }
        ),
        tint = Red,
        contentDescription = "favorite"
    )
}