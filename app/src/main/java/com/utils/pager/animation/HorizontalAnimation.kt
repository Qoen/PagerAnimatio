package com.utils.pager.animation

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs

internal abstract class HorizontalAnimation : PagerAnimation {
    //  始终显示的页面
    abstract var currentPage: Int
    //  滑动时显示的页面
    private var targetPage = 0
    //  从第几页到第几页
    abstract val pagerRange: IntRange
    //  水平动画：则是宽度
    abstract val size: Float
    //  Compose使用的 for items
    override val items: SnapshotStateMap<Int, Float> = mutableStateMapOf(currentPage to 0f)
    //  是否在运行
    override var isRunning: Boolean by mutableStateOf(false)
    //  是否启动滑动
    private var enabled: Boolean = true
    override fun setEnabled(boolean: Boolean) {
        enabled = boolean
    }

    //  方向 -1 offset是负数， 1 offset是正数
    private var direction: Float = 0f
    //  动画
    private var animatable: Animatable<Float, AnimationVector1D> = Animatable(0f)
    //  偏移
    private var offset: Float = 0.0f

    //  滑动事件
    override fun onScroll(available: Offset): Offset {
        val delta = available.x
        if (delta == 0F || !enabled) {
            return Offset.Zero
        }
        if (!isRunning) {
            direction = (abs(delta) / delta)
            val temp = currentPage - direction.toInt()
            if (temp in pagerRange) {
                isRunning = true
                targetPage = temp
                items.keys.forEach {
                    if (it !in setOf(currentPage, targetPage)) {
                        items.remove(it)
                    }
                }
            }
        }
        if (animatable.isRunning) {
            animatable.value
            return Offset(x = delta, 0f)
        }
        if (isRunning) {
            val range = 0f.rangeMinToMax(size * direction)
            offset = (offset + delta).coerceIn(range)
            items[currentPage] = currentPageOffset(offset, direction)
            items[targetPage] = targetPageOffset(offset, direction)
            return Offset(x = delta, 0f)
        }
        return Offset.Zero
    }

    //  松手动画
    override suspend fun onFling(available: Velocity): Velocity {
        if (!isRunning) {
            //  目标页超出页面数范围时，不会启动运行
            return Velocity.Zero
        }
        val velocity = available.x
        if (animatable.isRunning) {
            return Velocity(x = velocity, y = 0f)
        }
        val initialValue = offset
        val targetValue =
            if (abs(size * THRESHOLD * direction) > abs(offset)) 0f else size * direction
        animate(initialValue, targetValue, velocity) {
            animatable = this
            offset = value
            items[currentPage] = currentPageOffset(offset, direction)
            items[targetPage] = targetPageOffset(offset, direction)
        }
        if (offset != 0f) {
            currentPage = targetPage
            offset = 0F
        }

        isRunning = false

        return Velocity(x = velocity, y = 0f)
    }

    //  当处于拖动或者动画运行时，当前页面的偏移
    abstract fun currentPageOffset(offset: Float, direction: Float): Float
    //  当处于拖动或者动画运行时，目标页面的偏移
    abstract fun targetPageOffset(offset: Float, direction: Float): Float

    //  比较两个数的大小，生成从小到大的范围，从大到小会报错
    private fun Float.rangeMinToMax(that: Float): ClosedFloatingPointRange<Float> {
        return if (this > that) {
            that.rangeTo(this)
        } else {
            this.rangeTo(that)
        }
    }

    override val orientation = Orientation.Horizontal

    //  动画方式
    protected abstract suspend fun animate(
        initialValue: Float,
        targetValue: Float,
        initialVelocity: Float,
        block: (Animatable<Float, AnimationVector1D>.() -> Unit)
    )

    companion object {
        //  偏移量超过宽度百分比则成功下一页
        private const val THRESHOLD = 0.1F
    }
}
