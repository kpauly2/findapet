package tech.pauly.findapet.discover;

import android.content.Intent;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import tech.pauly.findapet.R;
import tech.pauly.findapet.shared.BaseEspressoTest;
import tech.pauly.findapet.shared.MainTabActivity;
import tech.pauly.findapet.utils.MockWebServerRule;
import tech.pauly.findapet.utils.RobotUtils;

import static tech.pauly.findapet.utils.MockWebServerRequestPatterns.FETCH_ANIMALS;

public class DiscoverFragmentTest extends BaseEspressoTest {

    @Rule
    public IntentsTestRule<MainTabActivity> intentsTestRule = new IntentsTestRule<>(MainTabActivity.class, false, false);

    @Rule
    public MockWebServerRule serverRule = new MockWebServerRule();

    @Inject
    CountingIdlingResource idlingResource;

    @Before
    public void setup() {
        getTestComponent().inject(this);
        IdlingRegistry.getInstance().register(idlingResource);
    }

    @After
    public void teardown() {
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void onLaunch_seesAnimalsFetchedFromNetwork() {
        String expectedAgeBreedText = String.format(getApplicationContext().getResources().getString(R.string.age_breed), "Adult", "Domestic Short Hair / Tabby");
        serverRule.getDispatcher().mockCall(FETCH_ANIMALS, "animal_list_response");

        intentsTestRule.launchActivity(new Intent(getApplicationContext(), MainTabActivity.class));

        RobotUtils.seesRecyclerViewWithItemWithTexts(R.id.animal_list_recycler_view, R.id.animal_item_card, "Saber", expectedAgeBreedText, "5 mi away");
    }
}
