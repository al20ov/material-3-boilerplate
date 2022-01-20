package com.nantoine.xmppclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nantoine.xmppclient.ui.theme.XMPPClientTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : ComponentActivity() {
  @ExperimentalFoundationApi
  @ExperimentalMaterial3Api
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      XMPPClientTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "home") {
          composable("home") {
            Home()
          }
        }
      }
    }
  }
}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun Home() {
  val drawerState = rememberDrawerState(DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  Scaffold(
    topBar = { TopBar(drawerState, scope) },
    content = { _ ->
      NavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
          Button(
            modifier = Modifier
              .align(Alignment.CenterHorizontally)
              .padding(top = 16.dp),
            onClick = { scope.launch { drawerState.close() } },
            content = { Text("Close Drawer") }
          )
        },
        content = {
          HomeContent()
        }
      )
    },
  )
}

@ExperimentalFoundationApi
@Composable
fun HomeContent() {
  var numbers: List<Int> = emptyList()

  for (i in 0..50) {
    numbers += i
  }

  Column(
    modifier = Modifier
      .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    LazyColumn() {
      val grouped = numbers.groupBy { it.toString()[0] }
      grouped.forEach { (initial, groupedNumbers) ->
        stickyHeader {
          Column(
            modifier = Modifier
              .border(
                BorderStroke(
                  1.dp,
                  color = MaterialTheme.colorScheme.secondary
                )
              )
              .padding(8.dp)
              .fillParentMaxWidth()
          ) {
            Text("Starts with $initial")
          }
        }

        items(groupedNumbers) { number ->
          Text(number.toString(), Modifier.fillMaxWidth())
        }
      }
    }
  }
}

@ExperimentalMaterial3Api
@Composable
fun TopBar(drawerState: DrawerState, scope: CoroutineScope) {

  CenterAlignedTopAppBar(
    title = { Text("XMChat") },
    navigationIcon = {
      IconButton(onClick = {
        scope.launch {
          if (drawerState.isOpen) drawerState.close() else drawerState.open()
        }
      }) {
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

}