package com.nantoine.xmppclient

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import org.intellij.lang.annotations.JdkConstants
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

@SuppressLint("CoroutineCreationDuringComposition")
@ExperimentalFoundationApi
@Composable
fun HomeContent() {
  // Here, we need to remember the value of numbers, otherwise, it will recalculate the numbers
  // the whole time we're scrolling
  val numbers by remember {
    mutableStateOf(generateNumbers())
  }
  // These two lines yield way lower performance
//  val numbers = remember { generateNumbers() }
//  val numbers = generateNumbers()
  val listState = rememberLazyListState()
  val scope = rememberCoroutineScope()

  Box() {
    LazyColumn(state = listState) {
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
    val showButton = listState.firstVisibleItemIndex > 0

    AnimatedVisibility(
      visible = showButton,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.align(Alignment.BottomCenter)
    ) {
      FloatingActionButton(onClick = {
        scope.launch {
          listState.scrollToItem(0)
        }
      }) {
        Icon(imageVector = Icons.Filled.KeyboardArrowUp, contentDescription = null)
      }

    }
  }

}

fun generateNumbers(): List<Int> {
  var numbers: List<Int> = emptyList()

  for (i in 0..50) {
    numbers += i
  }
  return numbers
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