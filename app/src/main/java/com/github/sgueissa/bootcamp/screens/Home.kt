package com.github.sgueissa.bootcamp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.github.sgueissa.bootcamp.Routes


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun Home(navController: NavHostController) {
    var mainName by remember {
        mutableStateOf("")
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column (
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = "Hello World!",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = mainName,
                onValueChange = { mainName = it },
                label = { Text("Your Name") },
                singleLine = true
            )
            Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
            Button(onClick = {
                navController.navigate(Routes.Greeting.route + "/$mainName")
            }) {
                Text("Go")
            }
        }
    }
}