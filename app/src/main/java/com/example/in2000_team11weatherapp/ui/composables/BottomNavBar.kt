package com.example.in2000_team11weatherapp.ui.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import com.example.in2000_team11weatherapp.MainActivity
import com.example.in2000_team11weatherapp.R
import com.example.in2000_team11weatherapp.viewmodel.TextToSpeechViewModel


@Composable
fun BottomNavBar(
    navController: NavHostController,
    buttonWidth: Dp,
    buttonHeight: Dp,
    textToSpeechViewModel: TextToSpeechViewModel,
    refreshingMain:Boolean,
) {
    val buttonHeightTester = buttonHeight/2
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val sizeScale by animateFloatAsState(if (isPressed) 0.95f else 1f)

    val interactionSource2 = remember { MutableInteractionSource() }
    val isPressed2 by interactionSource2.collectIsPressedAsState()
    val sizeScale2 by animateFloatAsState(if (isPressed2) 0.95f else 1f)

    val interactionSource3 = remember { MutableInteractionSource() }
    val isPressed3 by interactionSource3.collectIsPressedAsState()
    val sizeScale3 by animateFloatAsState(if (isPressed3) 0.95f else 1f)

    Box(modifier = Modifier.wrapContentSize(Alignment.BottomStart)) {// tried to set position to bottom of screen, doesn't work
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight)
                .background(Color(90, 44, 241, 255)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    textToSpeechViewModel.onTextFieldValueChange("Changing screen Search Screen") // Update the text in the ViewModel
                    textToSpeechViewModel.textToSpeech()
                    navController.navigate(MainActivity.Destination.SearchScreen.route)
                },
                colors = ButtonDefaults.buttonColors(Color.White),
                shape = RoundedCornerShape(10),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(buttonWidth)
                    .height(buttonHeight)
                    .graphicsLayer(
                        scaleX = sizeScale3,
                        scaleY = sizeScale3
                    ),
                interactionSource = interactionSource3


            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_search_24),
                    contentDescription = "placeholder weather icon Image",
                    modifier = Modifier.fillMaxSize()
                )
            }
            if (!refreshingMain){
                Button(
                    onClick = {
                        textToSpeechViewModel.onTextFieldValueChange("Changing screen to, Weather Screen") // Update the text in the ViewModel
                        textToSpeechViewModel.textToSpeech()
                        navController.navigate(MainActivity.Destination.MainScreen.route)
                    },
                    colors = ButtonDefaults.buttonColors(Color.White),
                    shape = RoundedCornerShape(10),
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(buttonWidth)
                        .height(buttonHeightTester)
                        .graphicsLayer(
                            scaleX = sizeScale2,
                            scaleY = sizeScale2
                        ),
                    interactionSource = interactionSource2
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_home_24),
                        contentDescription = "placeholder weather icon Image",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }else{
                Button(
                    onClick = {
                        textToSpeechViewModel.onTextFieldValueChange("Refreshing WeatherScreen") // Update the text in the ViewModel
                        textToSpeechViewModel.textToSpeech()
                        navController.navigate(MainActivity.Destination.LocationScreen.route)
                    },
                    colors = ButtonDefaults.buttonColors(Color.White),
                    shape = RoundedCornerShape(10),
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(buttonWidth)
                        .height(buttonHeightTester)
                        .graphicsLayer(
                            scaleX = sizeScale2,
                            scaleY = sizeScale2
                        ),
                    interactionSource = interactionSource2
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_home_24),
                        contentDescription = "placeholder weather icon Image",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Button(
                onClick = {
                    textToSpeechViewModel.onTextFieldValueChange("Changing screen to, Setting Screen") // Update the text in the ViewModel
                    textToSpeechViewModel.textToSpeech()
                    navController.navigate(MainActivity.Destination.SettingScreen.route)
                },
                colors = ButtonDefaults.buttonColors(Color.White),
                shape = RoundedCornerShape(10),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(buttonWidth)
                    .height(buttonHeight)
                    .graphicsLayer(
                        scaleX = sizeScale,
                        scaleY = sizeScale
                    ),
                interactionSource = interactionSource
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_settings_24),
                    contentDescription = "placeholder weather icon Image",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}