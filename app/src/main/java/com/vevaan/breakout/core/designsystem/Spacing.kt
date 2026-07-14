package com.vevaan.breakout.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class BreakoutSpacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 20.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 40.dp,
    val huge: Dp = 48.dp
)

object BreakoutDimensions {
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
    val xxxl = 40.dp
    val huge = 48.dp
    val ScreenHorizontalPadding = 20.dp
    val ScreenTopPadding = 16.dp
    val SectionSpacing = 28.dp
    val CompactContentSpacing = 12.dp
    val CardSpacing = 14.dp
    val CardPadding = 16.dp
    val HeroCardPadding = 20.dp
    val SmallCornerRadius = 12.dp
    val CardCornerRadius = 20.dp
    val HeroCornerRadius = 28.dp
    val AvatarSmall = 32.dp
    val AvatarMedium = 44.dp
    val ArtworkList = 52.dp
    val ArtworkCard = 72.dp
    val MinimumTouchTarget = 48.dp
}