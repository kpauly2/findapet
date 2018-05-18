package tech.pauly.findapet.discover;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import tech.pauly.findapet.shared.BaseEspressoTest;
import tech.pauly.findapet.shared.MainActivity;
import tech.pauly.findapet.utils.MockWebServerRule;

public class DiscoverFragmentTest extends BaseEspressoTest {

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class, false, false);

    @Rule
    public MockWebServerRule serverRule = new MockWebServerRule();

    @Inject
    CountingIdlingResource idlingResource;

    private DiscoverFragmentRobot.Hands hands;
    private DiscoverFragmentRobot.Eyes eyes;
    private DiscoverFragmentRobot.Dependencies dependencies;

    @Before
    public void setup() {
        getTestComponent().inject(this);
        hands = new DiscoverFragmentRobot.Hands(intentsTestRule, getApplicationContext());
        eyes = new DiscoverFragmentRobot.Eyes(getApplicationContext());
        dependencies = new DiscoverFragmentRobot.Dependencies(serverRule);
        IdlingRegistry.getInstance().register(idlingResource);
    }

    @After
    public void teardown() {
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void onLaunch_seesAnimalsFetchedFromNetwork() {
        dependencies.setupCatResponse();

        hands.launchScreen();

        eyes.seesCats();
    }

    @Test
    public void clickAnimal_launchAnimalDetails() {
        dependencies.setupCatResponse();
        hands.launchScreen();

        hands.clickDog();

        eyes.seesAnimalDetailsLaunched();
    }
}
