package com.utils.pager.animation

import androidx.compose.ui.unit.Constraints
import com.utils.pager.animation.horizontal.OverlayAnimation
import com.utils.pager.animation.horizontal.NonAnimation
import com.utils.pager.animation.horizontal.SmoothAnimation

//  比较方便的生成动画
class PagerBuilder(private val constraints: Constraints) {
    companion object {
        const val SMOOTH = 1
        const val COVER = 2
        const val NO = 3
        private fun create(
            type: Int,
            initialPage: Int,
            pagerRange: IntRange,
            constraints: Constraints
        ): PagerAnimation {
            return when (type) {
                SMOOTH -> SmoothAnimation(
                    initialPage,
                    pagerRange,
                    constraints.maxWidth.toFloat()
                )
                COVER -> OverlayAnimation(
                    initialPage,
                    pagerRange,
                    constraints.maxWidth.toFloat()
                )
                NO -> NonAnimation(initialPage, pagerRange, constraints.maxWidth.toFloat())
                else -> throw IllegalArgumentException("type 的参数不能为$type")
            }
        }
    }

    var pagerRange = IntRange(0, 0)
    var initialPage = 0

    internal var animationType = SMOOTH
    internal fun getPagerAnimation(): PagerAnimation {
        return create(animationType, initialPage, pagerRange, constraints)
    }
}
