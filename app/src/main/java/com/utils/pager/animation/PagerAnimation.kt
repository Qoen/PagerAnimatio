package com.utils.pager.animation

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Velocity

internal interface PagerAnimation {
    val items: SnapshotStateMap<Int, Float>
    val isRunning: Boolean
    val orientation: Orientation
    fun setEnabled(boolean: Boolean)
    fun onScroll(available: Offset): Offset
    suspend fun onFling(available: Velocity): Velocity
}
