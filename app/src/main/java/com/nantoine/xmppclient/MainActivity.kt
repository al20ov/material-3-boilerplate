package com.nantoine.xmppclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nantoine.xmppclient.ui.theme.XMPPClientTheme

class MainActivity : ComponentActivity() {
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            XMPPClientTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        Scaffold(topBar = {
                            CenterAlignedTopAppBar(
                                title = { Text("XMChat") },
                                navigationIcon = {
                                    IconButton(onClick = {}) {
                                        Icon(
                                            imageVector = Icons.Filled.Menu,
                                            contentDescription = "Open side menu"
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { /*TODO*/ }) {
                                        Icon(
                                            imageVector = Icons.Filled.Person,
                                            contentDescription = "Profile settings"
                                        )
                                    }
                                }
                            )
                        }) {
                            Surface() {
                                Column() {
                                    Row() {
                                        Checkbox(checked = true, onCheckedChange = null)
                                        Text(text = "salut")
                                    }
                                    Button(onClick = { navController.navigate("detail") }) {
                                        Text(text = "Go to detail screen")
                                    }

                                }
                            }
                        }
                    }
                    composable("detail") {
                        Surface() {
                            Button(onClick = { navController.popBackStack() }) {
                                Text(text = "Go back")
                            }
                        }
                    }
                }
            }
        }
    }
}
