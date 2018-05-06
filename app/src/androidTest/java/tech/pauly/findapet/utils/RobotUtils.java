package tech.pauly.findapet.utils;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.core.internal.deps.guava.io.ByteStreams;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.view.View;

import org.hamcrest.Matcher;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.InputStream;

import tech.pauly.findapet.data.models.Animal;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;

public class RobotUtils {

    //region Eyes

    public static void seesView(@IdRes int id) {
        onView(withId(id))
                .check(matches(isDisplayed()));
    }

    public static void seesViewWithText(@IdRes int id, @StringRes int text) {
        onView(withId(id))
                .check(matches(isDisplayed()))
                .check(matches(withText(text)));
    }

    public static void seesViewWithContainsText(@IdRes int id, String text) {
        onView(withId(id))
                .check(matches(isDisplayed()))
                .check(matches(withText(containsString(text))));
    }

    public static void seesViewWithText(@IdRes int id, String text) {
        onView(withId(id))
                .check(matches(isDisplayed()))
                .check(matches(withText(text)));
    }

    public static void doesNotSeeView(@IdRes int id) {
        try {
            onView(withId(id)).check(matches(not(isDisplayed())));
        } catch (NoMatchingViewException e) {}
    }

    public static void seesLaunchedActivity(Class activityClass) {
        intended(hasComponent(activityClass.getName()));
    }

    public static void seesActivityIsFinishing(IntentsTestRule testRule) {
        testRule.getActivity().isFinishing();
    }

    //endregion

    //region Hands

    public static void clickView(@IdRes int id) {
        onView(withId(id)).perform(click());
    }

    public static void seesRecyclerViewWithItemWithTexts(@IdRes int recyclerView, @IdRes int childView, String text1, String text2, String text3) {
        Matcher<View> viewMatcher = allOf(withId(childView), hasDescendant(withText(text1)), hasDescendant(withText(text2)), hasDescendant(withText(text3)));
        Matcher<View> recyclerViewMatcher = allOf(withId(recyclerView), isDisplayed());
        onView(recyclerViewMatcher).perform(RecyclerViewActions.scrollTo(viewMatcher));
        onView(viewMatcher).check(matches(isDisplayed()));
    }

    public static void clickChildVieWithText(@IdRes int parentView, @StringRes int childText) {
        Matcher<View> childViewMatcher = allOf(isDescendantOfA(withId(parentView)), withText(childText), isDisplayed());
        onView(childViewMatcher).perform(click());
    }

    public static void clickChildVieWithText(@IdRes int parentView, String childText) {
        Matcher<View> childViewMatcher = allOf(isDescendantOfA(withId(parentView)), withText(childText), isDisplayed());
        onView(childViewMatcher).perform(click());
    }

    //endregion

    public static <T> T parseResource(Object sourceObject, String responseFilename, Class<T> clazz) {
        Serializer ser = new Persister();
        try {
            return clazz.cast(ser.read(Animal.class, RobotUtils.loadResource(sourceObject, responseFilename)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String loadResource(Object sourceObject, String responseFilename) {
        String body = null;
        try {
            InputStream responseStream = sourceObject.getClass().getResourceAsStream("/" + responseFilename + ".xml");
            if (responseStream == null) {
                throw new IOException("Resource not found: " + responseFilename);
            }
            body = new String(ByteStreams.toByteArray(responseStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }
}
