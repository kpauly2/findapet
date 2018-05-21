package tech.pauly.findapet.discover;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import tech.pauly.findapet.R;
import tech.pauly.findapet.dependencyinjection.EspressoPetApplication;
import tech.pauly.findapet.shared.MainActivity;
import tech.pauly.findapet.utils.MockWebServerRule;
import tech.pauly.findapet.utils.RobotUtils;

import static tech.pauly.findapet.utils.MockWebServerRequestPatterns.FETCH_ANIMALS;

public interface DiscoverFragmentRobot {

    class Hands {

        private IntentsTestRule<MainActivity> intentsTestRule;
        private EspressoPetApplication context;

        Hands(IntentsTestRule<MainActivity> intentsTestRule, EspressoPetApplication context) {
            this.intentsTestRule = intentsTestRule;
            this.context = context;
        }

        void clickDog() {
            RobotUtils.clickChildVieWithText(R.id.animal_item_card, "Gretzky");
        }

        void launchScreen() {
            intentsTestRule.launchActivity(new Intent(context, MainActivity.class));
        }
    }
    
    class Eyes {

        private EspressoPetApplication context;

        Eyes(EspressoPetApplication context) {
            this.context = context;
        }

        void seesAnimalDetailsLaunched() {
            RobotUtils.seesLaunchedActivity(AnimalDetailsActivity.class);
        }

        void seesCats() {
            String expectedAgeBreedText = String.format(context.getResources().getString(R.string.age_breed), "Adult", "Domestic Short Hair / Tabby");
            RobotUtils.seesRecyclerViewWithItemWithTexts(R.id.animal_list_recycler_view, R.id.animal_item_card, "Saber", expectedAgeBreedText, "5 mi away");
        }
    }
    
    class Dependencies {

        MockWebServerRule serverRule;

        Dependencies(MockWebServerRule serverRule) {
            this.serverRule = serverRule;
        }

        void setupCatResponse() {
            serverRule.getDispatcher().mockCall(FETCH_ANIMALS, "animal_list_response_cats");
        }
    }
}
