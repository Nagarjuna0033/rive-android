package com.arjun.rivedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.rive.Fit
import app.rive.Result
import app.rive.Rive
import app.rive.RiveFileSource
import app.rive.rememberRiveFile
import app.rive.rememberRiveWorker
import app.rive.rememberViewModelInstance
import app.rive.runtime.kotlin.core.Rive


class RiveOnlyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Rive.init(this)

        setContent {
            BottomRivePanelTesting()
        }
    }
}


@Composable
fun BottomRivePanelTesting() {

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
            .fillMaxSize()
            .clickable {
                vmi.fireTrigger("Press")
            },
        contentAlignment = Alignment.Center
    ) {

        Rive(
            file = riveFile,
            viewModelInstance = vmi,
            fit = Fit.FitWidth(),
            playing = true,
        )
    }
}
