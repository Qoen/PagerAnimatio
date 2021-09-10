package com.utils.pager.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.utils.pager.animation.PagerBuilder
import com.utils.pager.pager.NestedPage
import com.utils.pager.pager.Pager

@Composable
fun MyPage() {
    Pager(
        Modifier,
        PaddingValues(),
        animationType = PagerBuilder.SMOOTH,
        build = {
            initialPage = 0
            pagerRange = 0..10
        }
    ) {
        Box(Modifier.background(Color.DarkGray)) {
            Text("第 $page 页")
        }
    }
}

@Composable
fun MyNestedPage() {
    NestedPage(
        parentPaddingPage = PaddingValues(),
        parentAnimationType = 1,
        parentBuild = {
            initialPage = 0
            pagerRange = 0..10
            0
        },
        childPaddingPage = PaddingValues(),
        childAnimationType = 1,
        childBuild = {
            initialPage = 0
            pagerRange = 0..5
            1
        }
    ) {
        Box(Modifier.background(Color.DarkGray)) {
            Text("第 $page 页")
        }
    }
}
