package com.example.composeeffectlab.ui

import android.graphics.Bitmap
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.composeeffectlab.R
import com.example.composeeffectlab.model.EffectEvent
import com.example.composeeffectlab.ui.theme.ComposeEffectLabTheme
import java.io.File
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EffectScreenTest {
    @get:Rule val rule = createComposeRule()
    private var observed = emptyList<EffectEvent>()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private fun text(id: Int) = context.getString(id)
    private fun start() { rule.setContent { ComposeEffectLabTheme { ComposeEffectLabApp { observed = it } } } }
    private fun list() = rule.onNode(hasScrollAction() and SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange))
    private fun click(id: Int) {
        list().performScrollToNode(hasText(text(id)))
        rule.onNodeWithText(text(id)).performClick()
    }
    private fun tab(id: Int) = rule.onNode(hasText(text(id)) and hasClickAction()).performScrollTo().performClick()
    private fun capture(name: String) {
        if (InstrumentationRegistry.getArguments().getString("captureScreenshots") != "true") return
        val image = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        File(context.filesDir, "$name.png").outputStream().use { image.compress(Bitmap.CompressFormat.PNG, 100, it) }
        image.recycle()
    }

    @Test fun scrollingTheScreenDoesNotCancelAnExplicitlyActiveEffect() {
        start(); tab(R.string.tab_launched); click(R.string.insert_effect)
        list().performScrollToNode(hasText("start:1"))
        rule.waitForIdle()
        rule.runOnIdle {
            assertEquals(1, observed.count { it.message == "start:1" })
            assertFalse("Scrolling must not withdraw the effect selected by the active control", observed.any { it.message == "cancel:1" })
        }
        capture("launched-scroll")
    }

    @Test fun changingTheKeyReleasesPreviousWorkAndRemovingItReleasesTheCurrentWork() {
        start(); tab(R.string.tab_launched); click(R.string.insert_effect); click(R.string.change_key)
        rule.waitForIdle()
        rule.runOnIdle { assertEquals(listOf("start:1", "cancel:1", "start:2"), observed.map { it.message }) }
        capture("launched-key")
        click(R.string.remove_effect)
        rule.runOnIdle { assertEquals("cancel:2", observed.last().message) }
    }

    @Test fun latestValueChangesWithoutRestartAndDeliveryCanRunAgain() {
        start(); tab(R.string.tab_updated)
        // Locate both controls with the frame clock running. Lazy scrolling needs frames.
        list().performScrollToNode(hasText(text(R.string.update_message)))
        rule.mainClock.autoAdvance = false
        try {
            rule.onNodeWithText(text(R.string.start_delivery)).performClick()
            rule.mainClock.advanceTimeByFrame()
            rule.onNodeWithText(text(R.string.update_message)).performClick()
            rule.mainClock.advanceTimeByFrame()
            rule.runOnIdle { assertEquals(listOf("start"), observed.map { it.message }) }
            rule.mainClock.advanceTimeBy(3_100); rule.waitForIdle()
            rule.runOnIdle { assertEquals(listOf("start", "delivered:${text(R.string.message_b)}"), observed.map { it.message }) }
            rule.onNodeWithText(text(R.string.start_delivery)).assertIsEnabled()
            capture("updated-delivered")
            rule.onNodeWithText(text(R.string.start_delivery)).performClick()
            rule.mainClock.advanceTimeByFrame()
            rule.mainClock.advanceTimeBy(3_100); rule.waitForIdle()
            rule.runOnIdle {
                assertEquals(2, observed.count { it.message == "start" })
                assertEquals(2, observed.count { it.message.startsWith("delivered:") })
            }
        } finally {
            rule.mainClock.autoAdvance = true
        }
    }

    @Test fun listenerIsReleasedOnReplacementAndWhenTheScreenLeaves() {
        start(); tab(R.string.tab_disposable); click(R.string.register_listener)
        list().performScrollToNode(hasText("Lifecycle"))
        rule.onNodeWithText("Lifecycle").performClick()
        rule.waitForIdle()
        rule.runOnIdle { assertEquals(listOf("register:GPS", "dispose:GPS", "register:Lifecycle"), observed.map { it.message }) }
        capture("listener-lifecycle")
        tab(R.string.tab_guide)
        rule.runOnIdle { assertEquals("dispose:Lifecycle", observed.last().message) }
    }
}
