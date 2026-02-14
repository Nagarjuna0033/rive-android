package com.example.benchmark

import android.content.Intent
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MemoryUsageMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RiveBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @OptIn(ExperimentalMetricApi::class)
    @Test
    fun measureRive() = benchmarkRule.measureRepeated(
        packageName = "com.arjun.rivedemo",
        metrics = listOf(
            FrameTimingMetric(),
            MemoryUsageMetric(mode = MemoryUsageMetric.Mode.Max)
        ),
        iterations = 5,
        setupBlock = {
            pressHome()
            startActivityAndWait(
                Intent().apply {
                    setClassName(
                        "com.arjun.rivedemo",
                        "com.arjun.rivedemo.RiveOnlyActivity"
                    )
                }
            )
        }
    ) {
        repeat(10) {
            device.click(
                device.displayWidth / 2,
                device.displayHeight / 2
            )
            Thread.sleep(500)
        }
    }

}