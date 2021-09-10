package com.utils.pager.pager

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.utils.pager.animation.PagerBuilder

data class ConstraintsScope(
    val parentConstraints: Constraints,
    val childConstraints: Constraints,
)

data class ChildPageScope<T>(
    val page: Int,
    val constraints: Constraints,
    var data: T,
    var isRunning: Boolean
)

@Composable
fun <T> Pager(
    modifier: Modifier,
    paddingPage: PaddingValues = PaddingValues(0.dp),
    animationType: Int,
    build: PagerBuilder.(Constraints) -> T,
    enabled: Boolean = true,
    content: @Composable ChildPageScope<T>.(Boolean) -> Unit,
) {
    Constraints(
        itemPadding = paddingPage,
    ) {
        val pair by remember(this) {
            val builder = PagerBuilder(parentConstraints)
            val data = build(builder, childConstraints)
            mutableStateOf(Pair(data, builder))
        }
        val data by remember(key1 = pair) {
            mutableStateOf(pair.first)
        }
        val pagerAnimation by remember(pair, animationType) {
            val builder = pair.second
            builder.animationType = animationType
            mutableStateOf(builder.getPagerAnimation())
        }
        val measurePolicy by remember(pagerAnimation.orientation) {
            val value = if (pagerAnimation.orientation == Orientation.Horizontal)
                HorizontalMeasurePolicy()
            else
                VerticalMeasurePolicy()
            mutableStateOf(value)
        }
        ParentLayout(pagerAnimation.items, modifier.pageDraggable(pagerAnimation,enabled), measurePolicy) {
//            val childPageScope by remember(childConstraints,pagerAnimation.isRunning) {
//                mutableStateOf()
//            }
            val zIndex by remember {
                mutableStateOf(it.key * -0.1f)
            }
            ChildLayout(
                it.value,
                zIndex,
                pagerAnimation.orientation,
                paddingPage
            ) {
                content(ChildPageScope(page = it.key, constraints = childConstraints, data = data, pagerAnimation.isRunning),pagerAnimation.isRunning)
            }
        }
    }
}

@Composable
private fun ParentLayout(
    items: SnapshotStateMap<Int, Float>,
    modifier: Modifier,
    measurePolicy: MeasurePolicy,
    content: @Composable (MutableMap.MutableEntry<Int, Float>) -> Unit,
) {
    Layout(
        content = {
            for (item in items) {
                key(item.key) {
                    content(item)
                }
            }
        },
        modifier = modifier,
        measurePolicy = measurePolicy,
    )
}

@Composable
private fun ChildLayout(
    offset: Float,
    zIndex: Float,
    orientation: Orientation,
//    contentBackground: androidx.compose.ui.graphics.Color,
    contentPadding: PaddingValues,
    content: @Composable () -> Unit,
) {
    val dpOffset = DpOffset.Zero.let {
        with(LocalDensity.current) {
            if (orientation == Orientation.Horizontal)
                it.copy(x = offset.toDp())
            else
                it.copy(y = offset.toDp())
        }
    }
    Box(
        Modifier
            .offset(dpOffset.x, dpOffset.y)
            .zIndex(zIndex)
//            .background(contentBackground)
            .padding(contentPadding)
    ) {
        content()
    }
}


@Composable
private fun Constraints(
    itemPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable ConstraintsScope.() -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val childConstraints by with(LocalDensity.current) {
            remember(constraints, itemPadding) {
                val startPadding = itemPadding.calculateStartPadding(LayoutDirection.Ltr)
                val endPadding = itemPadding.calculateEndPadding(LayoutDirection.Ltr)
                val maxWidth = maxWidth - startPadding - endPadding
                val maxHeight =
                    maxHeight - itemPadding.calculateTopPadding() - itemPadding.calculateBottomPadding()
                val childConstraints = Constraints(
                    minWidth = 0,
                    maxWidth = maxWidth.toPx().toInt(),
                    minHeight = 0,
                    maxHeight = maxHeight.toPx().toInt(),
                )
                mutableStateOf(childConstraints)
            }
        }
        val constraintsScope by remember(constraints,childConstraints) {
            mutableStateOf(ConstraintsScope(constraints,childConstraints))
        }
        content(constraintsScope)
    }
}

internal class HorizontalMeasurePolicy : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {
        val placeables = measurables.map { it.measure(constraints) }
        return layout(constraints.maxWidth, constraints.maxHeight) {
            for (placeable in placeables) {
                placeable.place(0, 0)
            }
        }
    }
}

internal class VerticalMeasurePolicy : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {
        val placeables = measurables.map { it.measure(constraints) }
        return layout(constraints.maxWidth, constraints.maxHeight) {
            for (placeable in placeables) {
                var top = 0
                placeable.place(0, top)
                top += placeable.height
            }
        }
    }
}

