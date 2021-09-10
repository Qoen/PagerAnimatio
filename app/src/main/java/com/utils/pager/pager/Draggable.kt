package com.utils.pager.pager

import android.util.Log
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Velocity
import com.utils.pager.animation.PagerAnimation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class NestedScrollConnection(var onScroll: (Offset) -> Offset, var onFling: suspend (Velocity) -> Velocity) : NestedScrollConnection {
    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        return available - onScroll(available)
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        return available - onFling(available)
    }
}

private fun draggableState(dispatcher: NestedScrollDispatcher, orientation: Orientation, onScroll: (Offset) -> Offset) : DraggableState {
     return DraggableState { delta ->
         var available = if (orientation == Orientation.Horizontal) {
             Offset(x = delta, y = 0f)
         } else {
             Offset(x = 0f, y = delta)
         }
         val parentsConsumed = dispatcher.dispatchPreScroll(
             available = available,
             source = NestedScrollSource.Drag
         )
         available -= parentsConsumed

         val weConsumed = onScroll(available)

         available -= weConsumed

         val totalConsumed = parentsConsumed + weConsumed

         dispatcher.dispatchPostScroll(
             consumed = totalConsumed,
             available = available,
             source = NestedScrollSource.Drag
         )
     }
}

internal fun Modifier.pageDraggable(
    animation: PagerAnimation,
    enabled: Boolean,
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "pageDraggable"
        properties["animation"] = animation
        properties["enabled"] = enabled
    }
) {
    key(enabled) {
        animation.setEnabled(enabled)
    }
    val dispatcher by remember {
        mutableStateOf(NestedScrollDispatcher())
    }
    val nestedScrollConnection by remember(animation) {
        mutableStateOf(
            NestedScrollConnection(onScroll = animation::onScroll, onFling = animation::onFling)
        )
    }
    val draggableState = remember(animation) {
        draggableState(dispatcher, animation.orientation, animation::onScroll)
    }

    Modifier
        .draggable(
            state = draggableState,
            orientation = animation.orientation,
            onDragStopped = { velocity ->
                launch() {
                    var available: Velocity = if (animation.orientation == Orientation.Horizontal) {
                        Velocity(velocity, 0F)
                    } else {
                        Velocity(0F, velocity)
                    }
                    val parentsConsumed = dispatcher.dispatchPreFling(available)
                    available -= parentsConsumed
                    val weConsumed = animation.onFling(available)
                    available -= weConsumed
                    dispatcher.dispatchPostFling(
                        weConsumed,
                        available,
                    )
                }
            }
        )
        .nestedScroll(nestedScrollConnection, dispatcher)
}
