package com.neversoft.launcher.shell

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
@LooperMode(LooperMode.Mode.PAUSED)
class StartMenuBackHandlerTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun backClosesStartThenDelegatesNextBackToExistingNavigation() {
        val startOpen = mutableStateOf(true)
        var normalNavigationBacks = 0
        compose.setContent {
            BackHandler { normalNavigationBacks++ }
            StartMenuBackHandler(visible = startOpen.value) { startOpen.value = false }
        }

        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.runOnIdle {
            assertFalse(startOpen.value)
            assertFalse(compose.activity.isFinishing)
            assertEquals(0, normalNavigationBacks)
        }
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.runOnIdle { assertEquals(1, normalNavigationBacks) }

        // Reopening Start re-enables interception instead of leaving a stale callback.
        compose.runOnIdle { startOpen.value = true }
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.runOnIdle {
            assertFalse(startOpen.value)
            assertEquals(1, normalNavigationBacks)
        }
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.runOnIdle { assertEquals(2, normalNavigationBacks) }
    }

    @Test fun closedStartDoesNotInterceptNormalBack() {
        var dismissals = 0
        var normalNavigationBacks = 0
        compose.setContent {
            BackHandler { normalNavigationBacks++ }
            StartMenuBackHandler(visible = false) { dismissals++ }
        }
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.runOnIdle {
            assertEquals(0, dismissals)
            assertEquals(1, normalNavigationBacks)
        }
    }
}
