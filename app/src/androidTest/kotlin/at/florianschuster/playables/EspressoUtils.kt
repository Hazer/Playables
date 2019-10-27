package at.florianschuster.playables

import android.content.res.Resources
import android.view.View
import junit.framework.AssertionFailedError
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast

object EspressoUtils {

    fun matchFirstDisplayedWithId(id: Int): Matcher<View> = object : TypeSafeMatcher<View>() {
        private var alreadyMatched = false
        private var resources: Resources? = null

        override fun describeTo(description: Description) {
            var idDescription = id.toString()
            if (resources != null) {
                idDescription = try {
                    resources!!.getResourceName(id)
                } catch (e: Resources.NotFoundException) {
                    // No big deal, will just use the int value.
                    String.format("%s (resource name not found)", id)
                }
            }
            description.appendText("with id: $idDescription")
        }

        public override fun matchesSafely(view: View): Boolean {
            return if (alreadyMatched) {
                false
            } else {
                resources = view.resources
                alreadyMatched = isDisplayed().matches(view) && id == view.id
                alreadyMatched
            }
        }
    }

    fun clickChildViewWithId(id: Int): ViewAction = object : ViewAction {
        override fun getConstraints(): Matcher<View> = isDisplayingAtLeast(90)

        override fun getDescription(): String = "Click on a child view with id $id."

        override fun perform(uiController: UiController, view: View) {
            val v = view.findViewById<View>(id)
            v?.performClick()
        }
    }

    fun recyclerViewItemCount(count: Int): ViewAssertion = ViewAssertion { view, _ ->
        if (view is RecyclerView) {
            val adapter = view.adapter
            if (adapter != null && adapter.itemCount != count) {
                throw AssertionFailedError(
                    "RecyclerView with id=" + view.id + " has " +
                            adapter.itemCount + " items, expected " + count + " items"
                )
            }
        } else {
            throw AssertionFailedError("View is not a RecyclerView")
        }
    }

}
