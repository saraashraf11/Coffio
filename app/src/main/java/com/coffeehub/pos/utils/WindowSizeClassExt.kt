package com.coffeehub.pos.utils

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

fun WindowSizeClass.isCompact(): Boolean = widthSizeClass == WindowWidthSizeClass.Compact
fun WindowSizeClass.isMedium(): Boolean = widthSizeClass == WindowWidthSizeClass.Medium
fun WindowSizeClass.isExpanded(): Boolean = widthSizeClass == WindowWidthSizeClass.Expanded
fun WindowSizeClass.isTablet(): Boolean = isExpanded()
fun WindowSizeClass.shouldUseNavigationRail(): Boolean = isMedium() || isExpanded()
fun WindowSizeClass.shouldUseDrawer(): Boolean = isExpanded()
fun WindowSizeClass.getGridColumns(): Int = when (widthSizeClass) {
    WindowWidthSizeClass.Compact -> 2
    WindowWidthSizeClass.Medium -> 3
    else -> 4
}
