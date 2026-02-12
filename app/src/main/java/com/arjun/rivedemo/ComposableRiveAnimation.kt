package com.arjun.rivedemo

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Fit

//@Composable
//fun ComposableRiveAnimationView(
//    modifier: Modifier = Modifier,
//    @RawRes animation: Int,
//    stateMachineName: String? = null,
//    alignment: app.rive.runtime.kotlin.core.Alignment = app.rive.runtime.kotlin.core.Alignment.CENTER,
//    fit: Fit = Fit.CONTAIN,
//    onInit: (RiveAnimationView) -> Unit = {}
//) {
//    AndroidView(
//        modifier = modifier,
//        factory = { context ->
//            RiveAnimationView(context).also {
//                it.setRiveResource(
//                    resId = animation,
//                    stateMachineName = stateMachineName,
//                    alignment = alignment,
//                    fit = fit,
//
//                )
//            }
//        },
//        update = { view -> onInit(view) }
//    )
//}


@Composable
fun RiveView(
    @RawRes resId: Int,
    modifier: Modifier = Modifier,
    stateMachineName: String,
    onClickTrigger: String? = null,
    onClickListener: (() -> Unit)? = null,
    onInit: (RiveAnimationView) -> Unit = {},
    ) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            RiveAnimationView(context).apply {
                setRiveResource(
                    resId = resId,
                    stateMachineName = stateMachineName
                )

                onInit.invoke(this)

                if (onClickTrigger != null) {
                    setOnClickListener {
                        onClickListener?.invoke()
                        fireState(stateMachineName, onClickTrigger)
                    }
                }
            }
        }
    )
}

@Composable
fun RiveView(
    @RawRes resId: Int,
    modifier: Modifier = Modifier,
    stateMachineName: String,
    inputsOnInit: List<RiveInput> = emptyList(),
    inputsOnClick: List<RiveInput> = emptyList(),
    onClickListener: (() -> Unit)? = null,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            RiveAnimationView(context).apply {

                setRiveResource(
                    resId = resId,
                    stateMachineName = stateMachineName
                )

                inputsOnInit.forEach { input ->
                    applyInput(stateMachineName, input)
                }

                if (inputsOnClick.isNotEmpty() || onClickListener != null) {
                    setOnClickListener {
                        inputsOnClick.forEach { input ->
                            applyInput(stateMachineName, input)
                        }
                        onClickListener?.invoke()
                    }
                }
            }
        }
    )
}

private fun RiveAnimationView.applyInput(
    stateMachineName: String,
    input: RiveInput
) {
    when (input) {
        is RiveInput.Trigger ->
            fireState(stateMachineName, input.name)

        is RiveInput.Boolean ->
            setBooleanState(stateMachineName, input.name, input.value)

        is RiveInput.Number ->
            setNumberState(stateMachineName, input.name, input.value)
    }
}


sealed interface RiveInput {
    data class Trigger(val name: String) : RiveInput
    data class Boolean(val name: String, val value: kotlin.Boolean) : RiveInput
    data class Number(val name: String, val value: Float) : RiveInput
}