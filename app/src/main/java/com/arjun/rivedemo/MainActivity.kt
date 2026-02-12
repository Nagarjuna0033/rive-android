package com.arjun.rivedemo

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RawRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
                        RiveBottomBar(
                            items = mainBottomItems,
                            selectedItemId = selectedItem,
                            onItemSelected = { item ->
                                selectedItem = item.label
                            }
                        )
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

    var showBottomPanel by remember { mutableStateOf(false) }
    var triggered by remember { mutableStateOf(false) }

    var primary by remember { mutableStateOf(true) }

    val riveWorker = rememberRiveWorker()

    val riveFileResult = rememberRiveFile(
        RiveFileSource.RawRes.from(R.raw.button_v41),
        riveWorker
    )

    when (riveFileResult) {
        is Result.Loading -> {
            CircularProgressIndicator()
            return
        }

        is Result.Error -> {
            Text("Failed to load animation")
            return
        }

        is Result.Success -> {

            Box(modifier = modifier) {
                val riveFile = riveFileResult.value
                val vmi = rememberViewModelInstance(riveFile)


                val images = listOf(
                    R.raw.ic_cash,
                    R.raw.ic_coin1,
                )
                val context = LocalContext.current



                val imageAssets = images.map { resId ->
                    produceState<Result<ByteArray>>(Result.Loading, resId) {
                        value = withContext(Dispatchers.IO) {
                            context.resources.openRawResource(resId)
                                .use { Result.Success(it.readBytes()) }
                        }
                    }.value.andThen { bytes ->
                        rememberImage(riveWorker, bytes)
                    }
                }.sequence()

                var imageIndex by remember { mutableStateOf(0) }


//                val lives by vmi.getNumberFlow("Energy_Bar/Lives")
//                    .collectAsStateWithLifecycle(0f)
//                val energy by vmi.getNumberFlow("Energy_Bar/Energy_Bar")
//                    .collectAsStateWithLifecycle(0f)
//                val coins by vmi.getNumberFlow("Coin/Item_Value")
//                    .collectAsStateWithLifecycle(0f)
//                val gems by vmi.getNumberFlow("Gem/Item_Value")
//                    .collectAsStateWithLifecycle(0f)
//                val winValue by vmi.getNumberFlow("Price_Value")
//                    .collectAsStateWithLifecycle(0f)
//                val winKind by vmi.getEnumFlow("Item_Selection/Item_Selection")
//                    .map { WinKind.from(it) }
//                    .collectAsStateWithLifecycle(WinKind.COIN)

//                val main = rememberArtboard(
//                    file = riveFile,
//                    artboardName = "Main"
//                )


                LaunchedEffect(vmi) {

                    launch {
                        riveFile.getViewModelNames().forEach {
                            println("ViewModels: $it")
                            riveFile.getViewModelProperties(it)
                                .forEach { viewModelProperty ->
                                    println("ViewModel Props: $viewModelProperty")
                                }
                        }

                        HandleSimpleRiveAsset(context)


//                        vmi.getBooleanFlow("isPressed")
//                            .collect {
//                                println("is pressed $it")
//                            }

                    }


                    riveFile.getEnums().forEach {
                        println("Enums: $it")
                    }


                    launch {
                        riveFile.getArtboardNames()
                            .forEach {
                                println("Artboard names $it")
                            }
                    }
//
//                    launch {
//                        vmi.getNumberFlow("Energy_Bar/Lives")
//                            .collect { println("Lives: $it") }
//                    }
//
//                    launch {
//                        vmi.getNumberFlow("Energy_Bar/Energy_Bar")
//                            .collect { println("Energy: $it") }
//                    }
//
//                    launch {
//                        vmi.getTriggerFlow("Button/Pressed")
//                            .collect { println("Continue pressed") }
//                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Rive(
                        file = riveFile,
                        viewModelInstance = vmi,
                        fit = Fit.Contain(),
                        modifier = Modifier
//                            .size(500.dp)
                            .weight(1f)
//                            .fillMaxWidth()
                            .semantics {
                            contentDescription = "Rive UI - Rewards Demo"
                        },
                        playing = true,
                    )

                    Button(
                        onClick = {
                            vmi.fireTrigger("Press")
                            println("Button pressed $primary")
                            if (imageAssets is Result.Success && imageAssets.value.isNotEmpty()) {
                                val assets = imageAssets.value

                                imageIndex = (imageIndex + 1) % assets.size
                                vmi.setImage("Right Icon", assets[imageIndex])
                            }
                            if (primary) {
                                // ───────── PRIMARY STATE ─────────
                                vmi.setString("Button Text", "Continue")

                                vmi.setEnum("Show Left Icon", "Show")
                                vmi.setEnum("Show Right Icon", "Show")
                                vmi.setEnum("Show Lock Icon", "Hide")
                                vmi.setColor("Bg Color", Color.Red.toArgb())
                                vmi.setNumber("Currency", 250f)
                                vmi.setEnum("Right Icon", "IC_coin1")

                            } else {
                                // ───────── DISABLED STATE ─────────
                                vmi.setString("Button Text", "Locked")

                                vmi.setEnum("Show Left Icon", "Hide")
                                vmi.setEnum("Show Right Icon", "Show")
                                vmi.setEnum("Show Lock Icon", "Show")
                                vmi.setColor("Bg Color", Color(0xFFFFBF0F).toArgb())
                                vmi.setNumber("Currency", 250f)

                            }

                            primary = !primary
                        }
                    ) {
                        Text("Toggle Button State")
                    }
//                    Row(
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//
//                        Button(
//                            onClick = {
//                                vmi.fireTrigger("Button/Pressed")
//                            },
//                        ) {
//                            Text("click to trigger")
//                        }
//
//                        Button(onClick = { showBottomPanel = !showBottomPanel}) {
//                            Text("Bottom Sheet")
//                        }
//                    }
//                    if (showBottomPanel) {
//                        RewardsBottomPanel(
//                            lives,
//                            energy,
//                            coins,
//                            gems,
//                            winValue,
////                            winKind,
//                            onDismiss = { showBottomPanel = false },
//                            onVmiSetNumber = { property, value ->
//                                vmi.setNumber(property, value)
//                            },
//                            onVmiSetColor = { property, value ->
//                                vmi.setColor(property, value)
//                            },
//                            onVmiSetEnum = { property, value ->
//                                vmi.setEnum(property, value)
//                            }
//                        )
//                    }
                }
            }
        }
    }
}

open class HandleSimpleRiveAsset(context: Context) : ContextAssetLoader(context) {
    override fun loadContents(asset: FileAsset, inBandBytes: ByteArray): Boolean {
        context.resources.openRawResource(R.raw.button_v41).use {
            return asset.decode(it.readBytes())
        }
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
                .height(80.dp)
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