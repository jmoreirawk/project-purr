package pro.moreira.projectpurr

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pro.moreira.projectpurr.utils.isTextDisplayed
import pro.moreira.projectpurr.utils.press
import pro.moreira.projectpurr.utils.pressWithTag
import pro.moreira.projectpurr.utils.replaceText
import pro.moreira.projectpurr.utils.waitForText

@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun searchForCatBreed_ClickCat_FavoriteHim_PressBack() = with(composeTestRule) {
        isTextDisplayed("Project Purr")
        waitForText("Search")
        replaceText("Search", "Cy")
        waitForText("Cyprus")
        press("Cyprus")
        pressWithTag("favorite")
        pressWithTag("back")
    }
}