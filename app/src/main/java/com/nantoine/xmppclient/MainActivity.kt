package com.nantoine.xmppclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nantoine.xmppclient.ui.theme.XMPPClientTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.impl.JidCreate

class MainActivity : ComponentActivity() {
  private val xmppFormModel by viewModels<XMPPFormModel>()
  private val xmppClient: XMPPConnection = XMPPConnection()

  @ExperimentalFoundationApi
  @ExperimentalMaterial3Api
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val scope = CoroutineScope(Dispatchers.IO)

    setContent {

      XMPPClientTheme {
        val navController = rememberNavController()


        NavHost(navController = navController, startDestination = "home") {
          composable("home") {
            Home(xmppFormModel, xmppClient, scope)
          }
        }
      }
    }
  }
}

class XMPPFormModel : ViewModel() {
  var username by mutableStateOf("")
  var password by mutableStateOf("")
  var domain by mutableStateOf("")
  var hostname by mutableStateOf("")
  var port by mutableStateOf("")
}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun Home(
  xmppFormModel: XMPPFormModel = viewModel(),
  xmppClient: XMPPConnection,
  scope: CoroutineScope
) {
  val drawerState = rememberDrawerState(DrawerValue.Closed)


  Scaffold(
    topBar = { TopBar(drawerState, scope) },
    content = { _ ->
      NavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
          Surface() {
            Column(
              Modifier.padding(16.dp)
            ) {
              MyTextField(
                value = xmppFormModel.username,
                onValueChange = { xmppFormModel.username = it },
                label = "Username",
              )
              Spacer(modifier = Modifier.height(8.dp))
              MyTextField(
                value = xmppFormModel.password,
                onValueChange = { xmppFormModel.password = it },
                label = "Password",
              )
              Spacer(modifier = Modifier.height(8.dp))
              MyTextField(
                value = xmppFormModel.domain,
                onValueChange = { xmppFormModel.domain = it },
                label = "Domain",
              )
              Spacer(modifier = Modifier.height(8.dp))
              MyTextField(
                value = xmppFormModel.hostname,
                onValueChange = { xmppFormModel.hostname = it },
                label = "Hostname",
              )
              Spacer(modifier = Modifier.height(8.dp))
              MyTextField(
                value = xmppFormModel.port,
                onValueChange = { xmppFormModel.port = it },
                label = "Port",
              )
              Spacer(modifier = Modifier.height(16.dp))
              Button(
                onClick = {
                  scope.launch {
                    xmppClient.setConnectionSettings(
                      XMPPConnectionSettings(
                        xmppFormModel.username,
                        xmppFormModel.password,
                        xmppFormModel.hostname
                      )
                    )
                    xmppClient.buildConnection()
                    xmppClient.connect()
                    xmppClient.login()
                  }
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                shape = RoundedCornerShape(100),
              ) {
                Text(
                  text = "Connect",
                  fontWeight = FontWeight.Bold,
                  fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
              }
              Button(onClick = {
                xmppClient.sendMessage()
              }) {
                Text(text = "Send a message")
              }

            }
          }
        },
        content = {
          HomeContent()
        }
      )
    },
  )
}

@Composable
fun MyTextField(
  value: String,
  onValueChange: (it: String) -> Unit,
  label: String,
  modifier: Modifier = Modifier
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label) },
    modifier = Modifier
      .fillMaxWidth()
      .composed { modifier },
    singleLine = true,
    colors = TextFieldDefaults.outlinedTextFieldColors(
      focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
      unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
      cursorColor = MaterialTheme.colorScheme.secondary
    ),
    textStyle = TextStyle(
      color = MaterialTheme.colorScheme.secondary,
      fontSize = MaterialTheme.typography.bodyLarge.fontSize
    )
  )
}

@Composable
fun HomeContent() {
  Text(text = "home")
}

data class XMPPConnectionSettings(
  val username: String,
  val password: String,
  val hostname: String,
  val domain: String = hostname,
  val port: String = "5222"
)

class XMPPConnection {

  private lateinit var connectionSettings: XMPPConnectionSettings

  private lateinit var config: XMPPTCPConnectionConfiguration
  private lateinit var conn: XMPPTCPConnection

  fun setConnectionSettings(connSettings: XMPPConnectionSettings) {
    connectionSettings = connSettings
  }

  fun buildConnection(): Unit {
    config = XMPPTCPConnectionConfiguration.builder()
      .setUsernameAndPassword(connectionSettings.username, connectionSettings.password)
      .setXmppDomain(connectionSettings.domain)
      .setHost(connectionSettings.hostname)
      .setPort(connectionSettings.port.toInt())
      .build()
    conn = XMPPTCPConnection(config)
  }

  fun connect(): String {
    try {
      conn.connect()
    } catch (e: Exception) {
      return e.toString()
    }
    return ""
  }

  fun login(): String {
    try {
      conn.login()
    } catch (e: Exception) {
      return e.toString()
    }
    return ""
  }

  fun sendMessage(): Unit {
    val chatManager = ChatManager.getInstanceFor(conn)
    val jid = JidCreate.entityBareFrom("testuser@im.jabber.com");
    val chat = chatManager.chatWith(jid);
    chat.send("Howdy!");
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
//
//@ExperimentalFoundationApi
//@Composable
//fun HomeContent() {
//  // Here, we need to remember the value of numbers, otherwise, it will recalculate the numbers
//  // the whole time we're scrolling
//  val numbers by remember {
//    mutableStateOf(generateNumbers())
//  }
//  // These two lines yield way lower performance
////  val numbers = remember { generateNumbers() }
////  val numbers = generateNumbers()
//  val listState = rememberLazyListState()
//  val scope = rememberCoroutineScope()
//
//  Box() {
//    LazyColumn(state = listState) {
//      val grouped = numbers.groupBy { it.toString()[0] }
//
//      grouped.forEach { (initial, groupedNumbers) ->
//        stickyHeader {
//          Column(
//            modifier = Modifier
//              .border(
//                BorderStroke(
//                  1.dp,
//                  color = MaterialTheme.colorScheme.secondary
//                )
//              )
//              .padding(8.dp)
//              .fillParentMaxWidth()
//          ) {
//            Text("Starts with $initial")
//          }
//        }
//
//        items(groupedNumbers) { number ->
//          Text(number.toString(), Modifier.fillMaxWidth())
//        }
//      }
//    }
//    val showButton = listState.firstVisibleItemIndex > 0
//
//    AnimatedVisibility(
//      visible = showButton,
//      enter = fadeIn(),
//      exit = fadeOut(),
//      modifier = Modifier.align(Alignment.BottomCenter)
//    ) {
//      FloatingActionButton(onClick = {
//        scope.launch {
//          listState.scrollToItem(0)
//        }
//      }, modifier = Modifier.padding(8.dp)) {
//        Icon(imageVector = Icons.Filled.KeyboardArrowUp, contentDescription = null)
//      }
//
//    }
//  }
//
//}
//
//fun generateNumbers(): List<Int> {
//  var numbers: List<Int> = emptyList()
//
//  for (i in 0..50) {
//    numbers += i
//  }
//  return numbers
//}
//
