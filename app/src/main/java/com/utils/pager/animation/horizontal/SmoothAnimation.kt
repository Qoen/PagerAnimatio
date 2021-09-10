package com.utils.pager.animation.horizontal

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import com.utils.pager.animation.HorizontalAnimation

internal class SmoothAnimation(
    override var currentPage: Int,
    override val pagerRange: IntRange,
    override val size: Float
) : HorizontalAnimation() {

    override fun currentPageOffset(offset: Float, direction: Float): Float {
        return offset
    }

    override fun targetPageOffset(offset: Float, direction: Float): Float {
//        return when (direction) {
//            -1f -> offset + size * direction
//            1f -> offset + size * direction
//            else -> throw IllegalArgumentException("direction 的参数不能为$direction")
//        }
        return offset - (size * direction)
    }

    override suspend fun animate(
        initialValue: Float,
        targetValue: Float,
        initialVelocity: Float,
        block: Animatable<Float, AnimationVector1D>.() -> Unit
    ) {
        Animatable(initialValue,1f).animateTo(
            targetValue,
            initialVelocity = initialVelocity,
            block = block
        )
    }
}
