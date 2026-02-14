package com.arjun.rivedemo

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.rive.Fit
import app.rive.ImageAsset
import app.rive.Result.Loading.sequence
import app.rive.Rive
import app.rive.RiveFileSource
import app.rive.RivePointerInputMode
import app.rive.ViewModelInstance
import app.rive.ViewModelInstanceSource
import app.rive.ViewModelSource
import app.rive.core.ImageHandle
import app.rive.rememberArtboard
import app.rive.rememberImage
import app.rive.rememberRiveFile
import app.rive.rememberRiveWorker
import app.rive.rememberStateMachine
import app.rive.rememberViewModelInstance
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Rive
import app.rive.runtime.kotlin.core.SMIBoolean
import app.rive.runtime.kotlin.core.SMINumber
import app.rive.runtime.kotlin.core.SMITrigger
import com.arjun.rivedemo.ui.theme.RivedemoTheme
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import app.rive.Result.Loading.andThen
import app.rive.Result
import app.rive.Result.Loading.andThen
import app.rive.Result.Loading.sequence
import app.rive.Result.Loading.zip
import app.rive.runtime.kotlin.core.ContextAssetLoader
import app.rive.runtime.kotlin.core.FileAsset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.MutableState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Rive.init(this)
        setContent {
            RivedemoTheme {
                var selectedItem by remember { mutableStateOf("home") }

                Scaffold(
                    bottomBar = {

                        BottomRivePanel()
                    }
                ) { padding ->
                    Greeting("Rive", modifier = Modifier.padding(padding))
                }
            }
        }
    }
}

val mainBottomItems = listOf(
    MainBottomItem.Home,
    MainBottomItem.Scan,
    MainBottomItem.Profile
)

@Composable
fun Greeting(name : String, modifier: Modifier = Modifier) {

    var primary by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(Color.White)
    ) {

        Button(
            modifier = Modifier
                .align(Alignment.Center)
                .navigationBarsPadding()
                .padding(24.dp)
                .fillMaxWidth(0.9f),
            shape = RoundedCornerShape(16.dp),
            onClick = {
                println("Button pressed $primary")
//                        if (primary) {
//                            // ───────── PRIMARY STATE ─────────
//                                vmi.setString("Button Text", "Continue")
//
//                                vmi.setEnum("Show Lock Icon", "Hide")
//                                vmi.setEnum("Right Cash", "Show")
//                                vmi.setEnum("Right Coin", "Hide")
//                                vmi.setNumber("Currency", 250f)
//
//                        } else {
//                            // ───────── DISABLED STATE ─────────
//                                vmi.setString("Button Text", "Locked")
//
//                                vmi.setEnum("Show Lock Icon", "Show")
//                                vmi.setEnum("Right Cash", "Hide")
//                                vmi.setEnum("Right Coin", "Show")
//                                vmi.setNumber("Currency", 250f)
//
//                        }

                primary = !primary
            },
        ) {
            Text("Toggle Button State")
        }
    }
}


@Composable
fun BottomRivePanel() {

    val riveWorker = rememberRiveWorker()

    val riveFileResult = rememberRiveFile(
        RiveFileSource.RawRes.from(R.raw.button_v46),
        riveWorker
    )

    if (riveFileResult !is Result.Success) return

    val riveFile = riveFileResult.value
    val vmi = rememberViewModelInstance(riveFile)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
            .size(125.dp),
        contentAlignment = Alignment.Center
    ) {

        Rive(
            modifier = Modifier
                .align(Alignment.Center),
            file = riveFile,
            viewModelInstance = vmi,
            fit = Fit.FitWidth(),
        )
    }
}




@Composable
fun RiveBottomBarItem(
    item: BottomNavigationItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RiveView(
            resId = item.rive.resId,
            stateMachineName = item.rive.stateMachineName,
            inputsOnClick = if (selected)
                item.rive.inputsOnReselect
            else
                item.rive.inputsOnSelect,
            modifier = Modifier.size(48.dp),
            onClickListener = { onClick() }
        )
        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

//ViewModel: Button
//--------------------------------
//Button Text        : STRING
//Show Left Icon     : ENUM
//Left Icon          : ASSET_IMAGE
//Show Right Icon    : ENUM
//Right Icon         : ASSET_IMAGE
//Show Lock Icon     : ENUM
//Lock Icon          : ASSET_IMAGE
//Currency           : NUMBER
//Bg Color           : COLOR
//Shadow Color       : COLOR


@Composable
fun RiveBottomBar(
    items: List<BottomNavigationItem>,
    selectedItemId: String,
    onItemSelected: (BottomNavigationItem) -> Unit
) {
    Surface(tonalElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
//                .height(80.dp)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                RiveBottomBarItem(
                    item = item,
                    selected = item.id == selectedItemId,
                    onClick = { onItemSelected(item) }
                )
            }
        }
    }
}


interface BottomNavigationItem {

    val id: String

    val label: String

    val contentDescription: String

    val graphRoute: String

    val startDestinationRoute: String

    val testTag: String

    val rive: RiveConfig
}

data class RiveConfig(
    @RawRes val resId: Int,
    val stateMachineName: String,
    val inputsOnSelect: List<RiveInput> = emptyList(),
    val inputsOnReselect: List<RiveInput> = emptyList()
)

sealed interface MainBottomItem : BottomNavigationItem {

    data object Home : MainBottomItem {
        override val id = "home"
        override val label = "Home"
        override val contentDescription = "Home tab"
        override val graphRoute = "home_graph"
        override val startDestinationRoute = "home"
        override val testTag = "bottom_home"

        override val rive = RiveConfig(
            resId = R.raw.sword_nav,
            stateMachineName = "State Machine 1",
            inputsOnSelect = listOf(RiveInput.Trigger("Fire")),
            inputsOnReselect = listOf(RiveInput.Trigger("Fire"))
        )
    }

    data object Scan : MainBottomItem {
        override val id = "scan"
        override val label = "Scan"
        override val contentDescription = "Scan tab"
        override val graphRoute = "scan_graph"
        override val startDestinationRoute = "scan"
        override val testTag = "bottom_scan"

        override val rive = RiveConfig(
            resId = R.raw.sword_nav,
            stateMachineName = "State Machine 1",
            inputsOnSelect = listOf(RiveInput.Trigger("Fire"))
        )
    }

    data object Profile : MainBottomItem {
        override val id = "profile"
        override val label = "Profile"
        override val contentDescription = "Profile tab"
        override val graphRoute = "profile_graph"
        override val startDestinationRoute = "profile"
        override val testTag = "bottom_profile"

        override val rive = RiveConfig(
            resId = R.raw.sword_nav,
            stateMachineName = "State Machine 1",
            inputsOnSelect = listOf(RiveInput.Trigger("Fire"))
        )
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RivedemoTheme {
        Greeting("Android")
    }
}