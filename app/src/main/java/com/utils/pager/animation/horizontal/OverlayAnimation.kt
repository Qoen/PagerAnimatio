package com.utils.pager.animation.horizontal

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import com.utils.pager.animation.HorizontalAnimation

internal class OverlayAnimation(
    override var currentPage: Int,
    override val pagerRange: IntRange,
    override val size: Float
) : HorizontalAnimation() {
    override fun currentPageOffset(offset: Float, direction: Float): Float {
        return when (direction) {
            -1f -> offset
            1f -> 0f
            else -> throw IllegalArgumentException("direction 的参数不能为$direction")
        }
    }

    override fun targetPageOffset(offset: Float, direction: Float): Float {
        return when (direction) {
            1f -> offset
            -1f -> 0f
            else -> throw IllegalArgumentException("direction 的参数不能为$direction")
        }
    }

    override suspend fun animate(
        initialValue: Float,
        targetValue: Float,
        initialVelocity: Float,
        block: Animatable<Float, AnimationVector1D>.() -> Unit
    ) {
        Animatable(initialValue).animateTo(targetValue, initialVelocity = initialVelocity, block = block)
    }
}
