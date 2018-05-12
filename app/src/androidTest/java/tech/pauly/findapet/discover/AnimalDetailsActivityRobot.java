package tech.pauly.findapet.discover;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import tech.pauly.findapet.R;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.dependencyinjection.EspressoPetApplication;
import tech.pauly.findapet.shared.AnimalDetailsUseCase;
import tech.pauly.findapet.shared.TransientDataStore;
import tech.pauly.findapet.utils.RobotUtils;

public interface AnimalDetailsActivityRobot {

    class Hands {
        private IntentsTestRule<AnimalDetailsActivity> intentsTestRule;
        private EspressoPetApplication context;

        Hands(IntentsTestRule<AnimalDetailsActivity> intentsTestRule, EspressoPetApplication context) {
            this.intentsTestRule = intentsTestRule;
            this.context = context;
        }

        void launchScreen() {
            intentsTestRule.launchActivity(new Intent(context, AnimalDetailsActivity.class));
        }
    }

    class Eyes {
        private EspressoPetApplication context;

        Eyes(EspressoPetApplication context) {
            this.context = context;
        }

        Eyes seeFullCatDetails() {
            RobotUtils.seesViewWithText(R.id.animal_name, "Sahara");
            RobotUtils.seesViewWithText(R.id.animal_breed, "Domestic Short Hair / Tortoiseshell");
            RobotUtils.seesViewWithText(R.id.animal_age_type, "Senior Cat");
            RobotUtils.seesViewWithText(R.id.animal_sex, "Female");
            RobotUtils.seesViewWithText(R.id.animal_size, "Medium");
            return this;
        }

        Eyes doesNotSeeDescriptionSection() {
            RobotUtils.doesNotSeeView(R.id.divider_center);
            RobotUtils.doesNotSeeView(R.id.description_title);
            RobotUtils.doesNotSeeView(R.id.description_body);
            return this;
        }

        Eyes doesNotSeeOptionsSection() {
            RobotUtils.doesNotSeeView(R.id.divider_top);
            RobotUtils.doesNotSeeView(R.id.options_title);
            RobotUtils.doesNotSeeView(R.id.options_body);
            return this;
        }

        Eyes seesDescriptionSection() {
            RobotUtils.seesView(R.id.divider_center);
            RobotUtils.seesView(R.id.description_title);
            RobotUtils.seesViewWithContainsText(R.id.description_body, "Sahara, senior female. Sahara likes to be brushed and will lay in the same room with you or next to you. She wants to be near you but not necessarily on your lap.");
            return this;
        }

        Eyes seesOptionsSection() {
            RobotUtils.seesView(R.id.divider_top);
            RobotUtils.seesView(R.id.options_title);
            RobotUtils.seesViewWithText(R.id.options_body, context.getString(R.string.altered) + "\n" +  context.getString(R.string.house_trained));
            return this;
        }

    }

    class Dependencies {
        private TransientDataStore dataStore;

        Dependencies(TransientDataStore dataStore) {
            this.dataStore = dataStore;
        }

        public void setupCatWithMissingData() {
            Animal animal = RobotUtils.parseResource(this, "single_cat_missing", Animal.class);
            dataStore.save(new AnimalDetailsUseCase(animal));
        }

        void setupCat() {
            Animal animal = RobotUtils.parseResource(this, "single_cat", Animal.class);
            dataStore.save(new AnimalDetailsUseCase(animal));
        }
    }
}
