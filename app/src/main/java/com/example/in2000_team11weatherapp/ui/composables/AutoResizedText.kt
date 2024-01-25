package com.example.in2000_team11weatherapp.ui.composables


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.graphics.Color

/**
 * AutoResized takes in String and modifies the font size to fit the parent composable
 */
@Composable
fun AutoResizedText(
    modifier: Modifier = Modifier,
    isWeatherCardDays: Boolean = false,
    isSampleText: Boolean = false,
    text: String,
    fontWeight: FontWeight = FontWeight.Normal,
    style: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        color = Color.Black,
        fontSize = 100.sp,
        textAlign = TextAlign.Center,
    ),
    color: Color = style.color
) {
    val segments = if (text.contains(",")) {
        val segmentsList = text.split(",").map { it.trim() }
        if (isWeatherCardDays) {
            segmentsList  // Return the list without appending the comma
        } else {
            segmentsList.mapIndexed { index, segment ->
                if (index != segmentsList.size - 1) "$segment," else segment
            }
        }
    } else {
        listOf(text)
    }

    var resizedTextStyle by remember {
        mutableStateOf(style)
    }

    var shouldDraw by remember {
        mutableStateOf(false)
    }

    val defaultFontSize = 100.sp

    var fontSizeSample by remember { mutableStateOf(0.sp) }
    val fontSizeValue = if(isSampleText) fontSizeSample else resizedTextStyle.fontSize

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        segments.forEach { segment ->
            Text(
                text = segment,
                textAlign = TextAlign.Center,
                fontWeight = fontWeight,
                color = color,
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
                    .let { if (segments.size > 1) it.weight(1f) else it }
                    .drawWithContent {
                    if (shouldDraw) {
                        drawContent()
                    }
                },
                softWrap = false,
                style = resizedTextStyle.copy(fontSize = fontSizeValue),
                onTextLayout = { result ->
                    if (result.didOverflowWidth) {
                        if (resizedTextStyle.fontSize.isUnspecified || isSampleText) {
                            resizedTextStyle = resizedTextStyle.copy(fontSize = defaultFontSize)
                        }
                        resizedTextStyle = resizedTextStyle.copy(fontSize = resizedTextStyle.fontSize * 0.95)
                        shouldDraw = false  // Do not draw while resizing

                        if(isSampleText){
                            fontSizeSample = resizedTextStyle.fontSize
                        }
                    } else {
                        shouldDraw = true  // Draw when correctly sized
                    }
                }
            )
        }
    }
}