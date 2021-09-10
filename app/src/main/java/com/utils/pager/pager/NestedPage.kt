package com.utils.pager.pager

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Constraints
import com.utils.pager.animation.PagerBuilder

@Composable
fun <T, V> NestedPage(
    parentPaddingPage: PaddingValues,
    parentAnimationType: Int,
    parentBuild: PagerBuilder.(Constraints) -> T,
    childPaddingPage: PaddingValues,
    childAnimationType: Int,
    childBuild: PagerBuilder.(Constraints) -> V,
    childContent: @Composable ChildPageScope<V>.() -> Unit,
) {
    Pager(
        modifier = Modifier.background(Color.Red),
        paddingPage = parentPaddingPage,
        animationType = parentAnimationType,
        build = parentBuild,
        enabled = true
    ) { endd->
        key(isRunning) {
            Log.e("TAG", "运行改变 $isRunning $endd", )
        }
        LaunchedEffect(key1 = isRunning) {
            Log.e("TAG2", "运行改变 $isRunning $endd", )
        }
        Pager(
            Modifier.background(Color.White),
            paddingPage = childPaddingPage,
            animationType = childAnimationType,
            build = childBuild,
            enabled = !isRunning
        ) {
            childContent()
        }
    }
}
