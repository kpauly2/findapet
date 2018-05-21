package tech.pauly.findapet.utils;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.core.internal.deps.guava.io.ByteStreams;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.InputStream;

import tech.pauly.findapet.R;
import tech.pauly.findapet.data.models.Animal;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
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

    public static void seesNavigationPanelClosed() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START)));
    }

    public static void seesNavigationPanelOpen() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isOpen()));
    }

    public static void seesButtonChecked(@IdRes int view, boolean checked) {
        Matcher<View> checkMatcher = checked ? isChecked() : isNotChecked();
        onView(withId(view))
                .check(matches(checkMatcher));
    }

    public static void seesToolbarWithTitle(String title) {
        onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
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

    public static void clickMenu() {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());
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

    private static Matcher<View> withToolbarTitle(Matcher<CharSequence> titleMatcher) {
        return new BoundedMatcher<View, Toolbar>(Toolbar.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                titleMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(Toolbar toolbar) {
                return titleMatcher.matches(toolbar.getTitle());
            }
        };
    }
}
