package tech.pauly.findapet.discover;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import tech.pauly.findapet.R;
import tech.pauly.findapet.dependencyinjection.EspressoPetApplication;
import tech.pauly.findapet.shared.MainTabActivity;
import tech.pauly.findapet.utils.MockWebServerRule;
import tech.pauly.findapet.utils.RobotUtils;

import static tech.pauly.findapet.utils.MockWebServerRequestPatterns.FETCH_ANIMALS;

public interface DiscoverFragmentRobot {

    class Hands {

        private IntentsTestRule<MainTabActivity> intentsTestRule;
        private EspressoPetApplication context;

        Hands(IntentsTestRule<MainTabActivity> intentsTestRule, EspressoPetApplication context) {
            this.intentsTestRule = intentsTestRule;
            this.context = context;
        }

        void launchScreen() {
            intentsTestRule.launchActivity(new Intent(context, MainTabActivity.class));
        }

        void clickCatTab() {
            RobotUtils.clickChildVieWithText(R.id.discover_tabs, R.string.tab_cat);
        }
    }
    
    class Eyes {

        private EspressoPetApplication context;

        Eyes(EspressoPetApplication context) {
            this.context = context;
        }

        void seesDogs() {
            String expectedAgeBreedText = String.format(context.getResources().getString(R.string.age_breed), "Adult", "Australian Shepherd / Catahoula Leopard Dog");
            RobotUtils.seesRecyclerViewWithItemWithTexts(R.id.animal_list_recycler_view, R.id.animal_item_card, "Earl", expectedAgeBreedText, "5 mi away");
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

        void setupDogResponse() {
            serverRule.getDispatcher().mockCall(FETCH_ANIMALS, "animal_list_response_dogs");
        }

        void setupCatResponse() {
            serverRule.getDispatcher().mockCall(FETCH_ANIMALS, "animal_list_response_cats");
        }
    }
}
