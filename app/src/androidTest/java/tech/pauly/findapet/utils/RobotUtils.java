package tech.pauly.findapet.utils;

import android.support.annotation.IdRes;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.view.View;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

public class RobotUtils {

    //region Eyes

    public static void seesView(@IdRes int id) {
        onView(withId(id))
                .check(matches(isDisplayed()));
    }

    public static void doesNotSeeView(@IdRes int id) {
        try {
            onView(withId(id)).check(matches(not(isDisplayed())));
        } catch (NoMatchingViewException e) {}
    }

    //endregion

    //region Hands

    public static void clickView(@IdRes int id) {
        onView(withId(id))
                .perform(click());
    }

    public static void seesRecyclerViewWithItemWithTexts(@IdRes int recyclerView, @IdRes int childView, String text1, String text2, String text3) {
        Matcher<View> viewMatcher = allOf(withId(childView), hasDescendant(withText(text1)), hasDescendant(withText(text2)), hasDescendant(withText(text3)));
        onView(withId(recyclerView)).perform(RecyclerViewActions.scrollTo(viewMatcher));
        onView(viewMatcher).check(matches(isDisplayed()));
    }

    //endregion
}
