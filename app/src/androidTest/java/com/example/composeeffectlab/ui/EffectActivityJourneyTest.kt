package com.example.composeeffectlab.ui

import android.app.LocaleManager
import android.graphics.Bitmap
import android.os.LocaleList
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.example.composeeffectlab.MainActivity
import com.example.composeeffectlab.R
import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 33)
class EffectActivityJourneyTest {
    private val compose = createAndroidComposeRule<MainActivity>()
    private var previousLocales: LocaleList? = null
    private val localeRule = object : ExternalResource() {
        override fun before() {
            val language = InstrumentationRegistry.getArguments().getString("qaLanguage") ?: return
            require(language in setOf("en", "es"))
            val manager = InstrumentationRegistry.getInstrumentation().targetContext.getSystemService(LocaleManager::class.java)
            previousLocales = manager.applicationLocales
            manager.applicationLocales = LocaleList.forLanguageTags(language)
        }
        override fun after() {
            previousLocales?.let {
                InstrumentationRegistry.getInstrumentation().targetContext.getSystemService(LocaleManager::class.java).applicationLocales = it
            }
        }
    }
    @get:Rule val rules: RuleChain = RuleChain.outerRule(localeRule).around(compose)
    private fun text(id: Int) = compose.activity.getString(id)

    @Test fun realEntryPointShowsTheExperimentAndItsOrderedTrace() {
        compose.onNodeWithText(text(R.string.guide_title)).assertIsDisplayed()
        compose.onNodeWithText("LaunchedEffect").performClick()
        val list = compose.onNode(hasScrollAction() and SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange))
        list.performScrollToNode(hasText(text(R.string.insert_effect)))
        val button = compose.onNodeWithText(text(R.string.insert_effect))
        val density = compose.activity.resources.displayMetrics.density
        assertTrue(button.fetchSemanticsNode().boundsInRoot.height / density >= 48f)
        button.performClick()
        list.performScrollToNode(hasText(text(R.string.change_key)))
        compose.onNodeWithText(text(R.string.change_key)).performClick()
        list.performScrollToNode(hasText("start:2"))
        compose.onNodeWithText("cancel:1").assertExists()
        compose.onNodeWithText("start:2").assertExists()
        list.performScrollToIndex(0)
        compose.mainClock.advanceTimeByFrame()
        compose.waitForIdle()
        compose.onNodeWithText(text(R.string.launched_title)).assertIsDisplayed()
        if (InstrumentationRegistry.getArguments().getString("captureScreenshots") == "true") {
            val language = compose.activity.resources.configuration.locales[0].language
            val image = compose.onRoot().captureToImage().asAndroidBitmap()
            File(compose.activity.filesDir, "main-launched-$language.png").outputStream().use {
                image.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            image.recycle()
        }
    }
}
