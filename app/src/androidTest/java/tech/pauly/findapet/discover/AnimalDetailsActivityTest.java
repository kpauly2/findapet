package tech.pauly.findapet.discover;

import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import tech.pauly.findapet.shared.BaseEspressoTest;
import tech.pauly.findapet.shared.TransientDataStore;

public class AnimalDetailsActivityTest extends BaseEspressoTest {

    @Rule
    public IntentsTestRule<AnimalDetailsActivity> intentsTestRule = new IntentsTestRule<>(AnimalDetailsActivity.class, false, false);

    @Inject
    TransientDataStore dataStore;

    private AnimalDetailsActivityRobot.Hands hands;
    private AnimalDetailsActivityRobot.Eyes eyes;
    private AnimalDetailsActivityRobot.Dependencies dependencies;

    @Before
    public void setup() {
        getTestComponent().inject(this);
        hands = new AnimalDetailsActivityRobot.Hands(intentsTestRule, getApplicationContext());
        eyes = new AnimalDetailsActivityRobot.Eyes(getApplicationContext());
        dependencies = new AnimalDetailsActivityRobot.Dependencies(dataStore);
    }

    @Test
    public void onLaunch_seesAnimalDetails() {
        dependencies.setupCat();

        hands.launchScreen();

        eyes.seeFullCatDetails()
            .seesOptionsSection()
            .seesDescriptionSection();
    }

    @Test
    public void onLaunch_animalMissingOptionsAndDescription_seesMissingSections() {
        dependencies.setupCatWithMissingData();

        hands.launchScreen();

        eyes.seeFullCatDetails()
            .doesNotSeeOptionsSection()
            .doesNotSeeDescriptionSection();
    }
}