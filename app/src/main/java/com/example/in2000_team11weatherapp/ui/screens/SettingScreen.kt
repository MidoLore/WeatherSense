package com.example.in2000_team11weatherapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.in2000_team11weatherapp.ui.composables.AutoResizedText
import com.example.in2000_team11weatherapp.ui.composables.BottomNavBar
import com.example.in2000_team11weatherapp.viewmodel.TextToSpeechViewModel


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavHostController,textToSpeechViewModel: TextToSpeechViewModel) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val titleHeight= screenHeight / 100 * 15
    val titleToSettingBarHeight= screenHeight / 100 * 2
    val buttonWidth = screenWidth / 100 * 32
    val buttonHeight = screenHeight / 100 * 15

    MaterialTheme {
        Scaffold (
            bottomBar =  {BottomNavBar(navController = navController, buttonWidth = buttonWidth, buttonHeight = buttonHeight, textToSpeechViewModel,true)},
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(90, 44, 241, 255))
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .height(titleHeight)
                            .padding(horizontal = titleHeight)
                    ) {
                        AutoResizedText(
                            text = "Settings",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(
                        modifier = Modifier.height(titleToSettingBarHeight)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        ExpandableCard(textToSpeechViewModel)
                    }
                    Spacer(modifier = Modifier.fillMaxHeight())
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableCard(textToSpeechViewModel: TextToSpeechViewModel) {
    var expanded1 by remember { mutableStateOf(false) }
    var expanded2 by remember { mutableStateOf(false) }
    var expanded3 by remember { mutableStateOf(false) }
    var expanded4 by remember { mutableStateOf(false) }
    var isCheckedOne by remember { mutableStateOf(false) }
    var isCheckedTwo by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(0.dp))
            .background(Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .clickable {
                    textToSpeechViewModel.onTextFieldValueChange("About App")
                    textToSpeechViewModel.textToSpeech()
                    expanded1 = !expanded1 }
        ) {
            Column (modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()) {
                Text(text = "About the app",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 48.sp,
                    color = Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
                if (expanded1) {
                    Text(
                        text = "Weather forecast for people with visual impairments",
                        fontSize = 30.sp,
                        color = Black,
                        modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(1.dp))
        Card(
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    textToSpeechViewModel.onTextFieldValueChange("Contrast Menu")
                    textToSpeechViewModel.textToSpeech()
                    expanded2 = !expanded2 }
                .background(Color.White)
        ) {
            Column (modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()){
                Text(text = "Contrast",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 48.sp,
                    color = Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
                if (expanded2) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Red/blue",
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 30.sp,
                            color = Black
                        )
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = isCheckedOne,
                            onCheckedChange = { Checked ->
                                isCheckedOne = Checked
                                println(if (isCheckedOne) "on" else "off")
                            },
                        modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "High Contrast",
                            fontSize = 30.sp,
                            color = Black,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                        Switch(checked = isCheckedTwo,
                            onCheckedChange = { Checked ->
                                isCheckedTwo = Checked
                                println(if (isCheckedTwo) "on" else "off")
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(1.dp))
        Card(
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .clickable {
                    textToSpeechViewModel.onTextFieldValueChange("Information about Icon")
                    textToSpeechViewModel.textToSpeech()
                    expanded3 = !expanded3 }
        ) {
            Column (modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()){
                Text(text = "Icons",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 48.sp,
                    color = Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
                if (expanded3) {
                    Text(text = "Weather forecast icons provided by met.no",
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 30.sp,
                        color = Black)
                }
            }
        }
        Spacer(modifier = Modifier.height(1.dp))
        Card(
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .clickable {
                    textToSpeechViewModel.onTextFieldValueChange("API used")
                    textToSpeechViewModel.textToSpeech()
                    expanded4 = !expanded4 }
        ) {
            Column (modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()) {
                Text(text = "API used",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 48.sp,
                    color = Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
                if (expanded4) {
                    Text(
                        text = "LocationForecast and Google API",
                        fontSize = 30.sp,
                        color = Black,
                        modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}