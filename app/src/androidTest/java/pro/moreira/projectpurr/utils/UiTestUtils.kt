package pro.moreira.projectpurr.utils

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import junit.framework.TestCase.assertEquals

@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.waitForText(
    text: String,
    timeoutMillis: Long = 5000,
) {
    waitUntilAtLeastOneExists(hasText(text), timeoutMillis = timeoutMillis)
}

fun ComposeTestRule.isTextDisplayed(
    text: String,
    expectedOccurrences: Int = 1,
) {
    if (expectedOccurrences == 1) {
        onNodeWithText(text).assertIsDisplayed()
    } else {
        assertEquals(onAllNodesWithText(text).fetchSemanticsNodes().size, expectedOccurrences)
    }
}

fun ComposeTestRule.press(text: String) {
    onNodeWithText(text).performClick()
}

fun ComposeTestRule.pressWithTag(tag: String) {
    onNodeWithTag(tag).performClick()
}

fun ComposeTestRule.clearText(inputLabel: String) {
    onNodeWithText(inputLabel).performTextClearance()
}

fun ComposeTestRule.replaceText(inputLabel: String, text: String) {
    clearText(inputLabel)
    onNodeWithText(inputLabel).performTextInput(text)
}