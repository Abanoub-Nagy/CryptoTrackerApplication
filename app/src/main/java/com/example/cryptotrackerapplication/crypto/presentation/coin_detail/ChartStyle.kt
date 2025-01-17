package com.example.cryptotrackerapplication.crypto.presentation.coin_detail

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

data class ChartStyle(
    val chartLineColor: Color,
    val unselectedLineColor: Color,
    val selectedLineColor: Color,
    val helperLinesThicknessPx: Float,
    val axisLineThicknessPx: Float,
    val labelTextSizePx: TextUnit,
    val minYLabelSpacing: Dp,
    val verticalLabelPadding: Dp,
    val horizontalLabelPadding: Dp,
    val xAxisLabelSpacing: Dp,
)
