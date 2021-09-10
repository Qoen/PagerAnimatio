package com.utils.pager.animation.horizontal

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import com.utils.pager.animation.HorizontalAnimation

internal class NonAnimation(
    override var currentPage: Int, override val pagerRange: IntRange, override val size: Float
) : HorizontalAnimation() {
    override fun currentPageOffset(offset: Float, direction: Float): Float {
        return 0f
    }

    override fun targetPageOffset(offset: Float, direction: Float): Float {
        return -size * direction
    }

    override suspend fun animate(
        initialValue: Float,
        targetValue: Float,
        initialVelocity: Float,
        block: Animatable<Float, AnimationVector1D>.() -> Unit
    ) {
        block(Animatable(0f))
    }
}
