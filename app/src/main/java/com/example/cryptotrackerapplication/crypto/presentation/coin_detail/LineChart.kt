package com.example.cryptotrackerapplication.crypto.presentation.coin_detail


import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptotrackerapplication.crypto.domain.CoinPrice
import com.example.cryptotrackerapplication.ui.theme.CryptoTrackerApplicationTheme
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun LineChart(
    dataPoints: List<DataPoint>,
    modifier: Modifier = Modifier,
    style: ChartStyle,
    visibleDataPointsIndices: IntRange,
    unit: String,
    selectedDataPoint: DataPoint? = null,
    onSelectedDataPoint: (DataPoint) -> Unit = {},
    onXLabelWidthChanged: (Float) -> Unit = {},
    showHelperLines: Boolean = true
) {
    val textStyle = LocalTextStyle.current.copy(
        fontSize = style.labelTextSizePx
    )

    val visibleDataPoints = remember(dataPoints, visibleDataPointsIndices) {
        dataPoints.slice(visibleDataPointsIndices)
    }

    val maxYValue = remember(visibleDataPoints) {
        visibleDataPoints.maxOfOrNull { it.y } ?: 0f
    }
    val minYValue = remember(visibleDataPoints) {
        visibleDataPoints.minOfOrNull { it.y } ?: 0f
    }

    val measurer = rememberTextMeasurer()

    var xLabelWidth by remember {
        mutableFloatStateOf(0f)
    }
    LaunchedEffect(key1 = xLabelWidth) {
        onXLabelWidthChanged(xLabelWidth)
    }

    val selectedDataPointIndex = remember(selectedDataPoint) {
        dataPoints.indexOf(selectedDataPoint)
    }
    var drawPoints by remember {
        mutableStateOf(listOf<DataPoint>())
    }
    var isShowingDataPoint by remember {
        mutableStateOf(selectedDataPoint != null)
    }

    Canvas(modifier = modifier
        .fillMaxSize()
        .pointerInput(dataPoints, xLabelWidth) {
            detectHorizontalDragGestures { change, _ ->
                val newSelectedDataPointIndex = getSelectedDataPointIndex(
                    touchOffsetX = change.position.x,
                    triggerWidth = xLabelWidth,
                    drawPoints = drawPoints
                )
                isShowingDataPoint =
                    (newSelectedDataPointIndex + visibleDataPointsIndices.first) in visibleDataPointsIndices
//                            && newSelectedDataPointIndex != selectedDataPointIndex
                if (isShowingDataPoint) {
                    onSelectedDataPoint(dataPoints[newSelectedDataPointIndex])
                }
            }
        }) {
        val minLabelSpacingYPx = style.minYLabelSpacing.toPx()
        val verticalLabelPaddingPx = style.verticalLabelPadding.toPx()
        val horizontalLabelPaddingPx = style.horizontalLabelPadding.toPx()
        val xAxisLabelSpacingPx = style.xAxisLabelSpacing.toPx()

        val xLabelTextLayoutResult = visibleDataPoints.map {
            measurer.measure(
                text = it.xLabel, style = textStyle.copy(textAlign = TextAlign.Center)
            )
        }
        val maxXLabelWidth = xLabelTextLayoutResult.maxOfOrNull { it.size.width } ?: 0
        val maxXLabelHeight = xLabelTextLayoutResult.maxOfOrNull { it.size.height } ?: 0
        val maxXLabelLineCount = xLabelTextLayoutResult.maxOfOrNull { it.lineCount } ?: 0
        val xLabelLineHeight = if (maxXLabelLineCount > 0) {
            maxXLabelHeight / maxXLabelLineCount
        } else 0

        val viewPortHeightPx =
            size.height - (maxXLabelHeight + 2 * verticalLabelPaddingPx + xLabelLineHeight + xAxisLabelSpacingPx)

        // y-label calculation
        val labelViewPortHeightPx = viewPortHeightPx + xLabelLineHeight
        val labelCountExcludingLastLabel =
            ((labelViewPortHeightPx / xLabelLineHeight + minLabelSpacingYPx)).toInt()

        // (2659-2583) / 2
        val valueIncrement = (maxYValue - minYValue) / labelCountExcludingLastLabel

        val yLabels = (0..labelCountExcludingLastLabel).map {
            ValueLabel(
                value = maxYValue - (valueIncrement * it), unit = unit
            )
        }
        val yLabelTextLayoutResult = yLabels.map {
            measurer.measure(
                text = it.formatted(), style = textStyle
            )
        }
        val maxYLabelWidth = yLabelTextLayoutResult.maxOfOrNull { it.size.width } ?: 0

        val viewPortTopY = verticalLabelPaddingPx + xLabelLineHeight + 10f
        val viewPortRightX = size.width
        val viewPortBottomY = viewPortTopY + viewPortHeightPx
        val viewPortLeftX = 2f * horizontalLabelPaddingPx + maxYLabelWidth
        val viewPort = Rect(
            left = viewPortLeftX,
            top = viewPortTopY,
            right = viewPortRightX,
            bottom = viewPortBottomY
        )

//        drawRect(
//            color = Color.Green, topLeft = viewPort.topLeft, size = viewPort.size
//        )

        xLabelWidth = maxXLabelWidth + xAxisLabelSpacingPx
        xLabelTextLayoutResult.forEachIndexed { index, textLayoutResult ->
            val x = viewPortLeftX + xAxisLabelSpacingPx / 2f + xLabelWidth * index
            val y = viewPortBottomY + xAxisLabelSpacingPx
            drawText(
                textLayoutResult = textLayoutResult, topLeft = Offset(
                    x = viewPortLeftX + xAxisLabelSpacingPx / 2f + xLabelWidth * index,
                    y = viewPortBottomY + xAxisLabelSpacingPx
                ), color = if (index == selectedDataPointIndex) {
                    style.selectedLineColor
                } else {
                    style.unselectedLineColor
                }
            )
            if (showHelperLines) {
                drawLine(
                    color = if (selectedDataPointIndex == index) {
                        style.selectedLineColor
                    } else {
                        style.unselectedLineColor
                    },
                    start = Offset(
                        x = x + textLayoutResult.size.width / 2, y = viewPortBottomY
                    ),
                    end = Offset(
                        x = x + textLayoutResult.size.width / 2, y = viewPortTopY
                    ),
                    strokeWidth = if (selectedDataPointIndex == index) {
                        style.helperLinesThicknessPx * 1.9f
                    } else {
                        style.helperLinesThicknessPx
                    },
                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                )
            }
            if (selectedDataPointIndex == index) {
                val valueLAbel = ValueLabel(
                    value = visibleDataPoints[index].y, unit = unit
                )
                val valueLabelTextLayoutResult = measurer.measure(
                    text = valueLAbel.formatted(), style = textStyle.copy(
                        color = style.selectedLineColor
                    ), maxLines = 1
                )
                val textPositionX = if (selectedDataPointIndex == visibleDataPointsIndices.last) {
                    x - valueLabelTextLayoutResult.size.width
                } else {
                    x - valueLabelTextLayoutResult.size.width / 2f
                } + textLayoutResult.size.width / 2f
                val isTextVisibleRange =
                    (size.width - textPositionX).roundToInt() in 0..size.width.roundToInt()
                if (isTextVisibleRange) {
                    drawText(
                        textLayoutResult = valueLabelTextLayoutResult, topLeft = Offset(
                            x = textPositionX,
                            y = viewPortTopY - valueLabelTextLayoutResult.size.height - 10f
                        ), color = style.selectedLineColor
                    )
                }
            }
        }

        val heightRequiredForLabels = xLabelLineHeight * (labelCountExcludingLastLabel + 1)
        val remainingHeightForLabels = labelViewPortHeightPx - heightRequiredForLabels
        val distanceBetweenLabels = remainingHeightForLabels / labelCountExcludingLastLabel


        yLabelTextLayoutResult.forEachIndexed { index, textLayoutResult ->
            val x =
                horizontalLabelPaddingPx + maxYLabelWidth - textLayoutResult.size.width.toFloat()
            val y =
                viewPortTopY + index * (xLabelLineHeight + distanceBetweenLabels) - xLabelLineHeight / 2f
            drawText(
                textLayoutResult = textLayoutResult, topLeft = Offset(
                    x = horizontalLabelPaddingPx, y = index * 100f
                ), color = style.unselectedLineColor
            )
            if (showHelperLines) {
                drawLine(
                    color = style.unselectedLineColor,
                    start = Offset(
                        x = viewPortLeftX, y = y + textLayoutResult.size.height.toFloat() / 2f
                    ),
                    end = Offset(
                        x = viewPortRightX, y = y + textLayoutResult.size.height.toFloat() / 2f
                    ),
                    strokeWidth = style.helperLinesThicknessPx,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                    alpha = 0.3f
                )
            }
        }
        // visibleDataPointsIndices = 5..20
        drawPoints = visibleDataPointsIndices.map {
            val x =
                viewPortLeftX + (it - visibleDataPointsIndices.first) * xLabelWidth + xLabelWidth / 2f
            // [minYValue; maxYValue] -> [0; 1]
            val ratio = (dataPoints[it].y - minYValue) / (maxYValue - minYValue)
            val y = viewPortBottomY - (ratio * viewPortHeightPx)
            DataPoint(
                x = x, y = y, xLabel = dataPoints[it].xLabel
            )
        }

        val conPoints1 = mutableListOf<DataPoint>()
        val conPoints2 = mutableListOf<DataPoint>()
        for (i in 1 until drawPoints.size) {
            val p0 = drawPoints[i - 1]
            val p1 = drawPoints[i]

            val x = (p1.x + p0.x) / 2f
            val y1 = p0.y
            val y2 = p1.y

            conPoints1.add(DataPoint(x, y1, ""))
            conPoints2.add(DataPoint(x, y2, ""))
        }

        val linePath = Path().apply {
            if (drawPoints.isNotEmpty()) {
                moveTo(drawPoints.first().x, drawPoints.first().y)

                for (i in 1 until drawPoints.size) {
                    cubicTo(
                        x1 = conPoints1[i - 1].x,
                        y1 = conPoints1[i - 1].y,
                        x2 = conPoints2[i - 1].x,
                        y2 = conPoints2[i - 1].y,
                        x3 = drawPoints[i].x,
                        y3 = drawPoints[i].y
                    )
                }
            }
        }
        drawPath(
            path = linePath, color = style.chartLineColor, style = Stroke(
                width = 5f, cap = StrokeCap.Round
            )
        )

        drawPoints.forEachIndexed { index, point ->
            if (isShowingDataPoint) {
                val circleOffset = Offset(
                    x = point.x, y = point.y
                )
                drawCircle(
                    color = style.selectedLineColor, radius = 10f, center = circleOffset
                )

                if (selectedDataPointIndex == index) {
                    drawCircle(
                        color = Color.White, radius = 15f, center = circleOffset
                    )
                    drawCircle(
                        color = style.selectedLineColor,
                        radius = 15f,
                        center = circleOffset,
                        style = Stroke(
                            width = 3f
                        )
                    )
                }
            }
        }
    }
}

private fun getSelectedDataPointIndex(
    touchOffsetX: Float, triggerWidth: Float, drawPoints: List<DataPoint>
): Int {
    val triggerRangeLeft = touchOffsetX - triggerWidth / 2f
    val triggerRangeRight = touchOffsetX + triggerWidth / 2f
    return drawPoints.indexOfFirst {
        it.x in triggerRangeLeft..triggerRangeRight
    }
}

@SuppressLint("NewApi")
@Preview
@Composable
private fun LineChartPreview() {
    CryptoTrackerApplicationTheme {
        val coinHistoryRandomized = remember {
            (1..20).map {
                CoinPrice(
                    priceUsd = Random.nextFloat() * 1000.0,
                    dateTime = ZonedDateTime.now().plusHours(it.toLong())

                )
            }
        }
        val style = ChartStyle(
            chartLineColor = Color.Black,
            unselectedLineColor = Color(0xFF7C7C7C),
            selectedLineColor = Color.Red,
            helperLinesThicknessPx = 1f,
            axisLineThicknessPx = 5f,
            labelTextSizePx = 14.sp,
            minYLabelSpacing = 25.dp,
            verticalLabelPadding = 10.dp,
            horizontalLabelPadding = 10.dp,
            xAxisLabelSpacing = 10.dp
        )
        val dataPoints = remember(coinHistoryRandomized) {
            coinHistoryRandomized.map {
                DataPoint(
                    x = it.dateTime.hour.toFloat(),
                    y = it.priceUsd.toFloat(),
                    xLabel = DateTimeFormatter.ofPattern("ha\nM/d").format(it.dateTime)
                )
            }
        }
        LineChart(dataPoints = dataPoints,
            style = style,
            visibleDataPointsIndices = 0..19,
            unit = "$",
            modifier = Modifier
                .width(700.dp)
                .height(300.dp)
                .background(Color.White),
            selectedDataPoint = dataPoints.first(),
            onSelectedDataPoint = {}

        )
    }
}