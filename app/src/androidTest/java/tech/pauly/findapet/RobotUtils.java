package tech.pauly.findapet;

import android.support.annotation.IdRes;
import android.support.test.espresso.NoMatchingViewException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
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

    //endregion
}
