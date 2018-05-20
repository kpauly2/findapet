package tech.pauly.findapet.shared;

import android.content.Context;

import tech.pauly.findapet.R;
import tech.pauly.findapet.utils.RobotUtils;

interface MainActivityRobot {
    
    class Hands {

        Hands clickMenu() {
            RobotUtils.clickMenu();
            return this;
        }
        
        Hands clickDiscover() {
            RobotUtils.clickView(R.id.button_discover);
            return this;
        }

        Hands clickShelters() {
            RobotUtils.clickView(R.id.button_shelters);
            return this;
        }

        Hands clickSettings() {
            RobotUtils.clickView(R.id.button_settings);
            return this;
        }

        Hands clickFavorites() {
            RobotUtils.clickView(R.id.button_favorites);
            return this;
        }

        void clickDog() {
            RobotUtils.clickView(R.id.button_dog);
        }
    }
    
    class Eyes {

        private Context context;

        Eyes(Context context) {
            this.context = context;
        }

        Eyes seesNavigationPanelClosed() {
            RobotUtils.seesNavigationPanelClosed();
            return this;
        }

        Eyes seesNavigationPanelOpen() {
            RobotUtils.seesNavigationPanelOpen();
            return this;
        }

        Eyes seesDiscoverMenuClosed() {
            RobotUtils.doesNotSeeView(R.id.button_dog);
            RobotUtils.doesNotSeeView(R.id.button_cat);
            RobotUtils.doesNotSeeView(R.id.button_smallfurry);
            RobotUtils.doesNotSeeView(R.id.button_barnyard);
            RobotUtils.doesNotSeeView(R.id.button_bird);
            RobotUtils.doesNotSeeView(R.id.button_horse);
            RobotUtils.doesNotSeeView(R.id.button_rabbit);
            RobotUtils.doesNotSeeView(R.id.button_reptile);
            return this;
        }

        Eyes seesDiscoverMenuOpen() {
            RobotUtils.seesView(R.id.button_dog);
            RobotUtils.seesView(R.id.button_cat);
            RobotUtils.seesView(R.id.button_smallfurry);
            RobotUtils.seesView(R.id.button_barnyard);
            RobotUtils.seesView(R.id.button_bird);
            RobotUtils.seesView(R.id.button_horse);
            RobotUtils.seesView(R.id.button_rabbit);
            RobotUtils.seesView(R.id.button_reptile);
            return this;
        }

        Eyes seesSheltersButtonNotSelected() {
            RobotUtils.seesButtonChecked(R.id.button_shelters, false);
            return this;
        }

        Eyes seesSettingsButtonNotSelected() {
            RobotUtils.seesButtonChecked(R.id.button_settings, false);
            return this;
        }

        Eyes seesFavoritesButtonNotSelected() {
            RobotUtils.seesButtonChecked(R.id.button_favorites, false);
            return this;
        }

        Eyes seesSettingsButtonSelected() {
            RobotUtils.seesButtonChecked(R.id.button_settings, true);
            return this;
        }

        Eyes seesSheltersButtonSelected() {
            RobotUtils.seesButtonChecked(R.id.button_shelters, true);
            return this;
        }

        Eyes seesFavoritesButtonSelected() {
            RobotUtils.seesButtonChecked(R.id.button_favorites, true);
            return this;
        }

        void seesDiscover() {
            RobotUtils.seesView(R.id.fragment_discover);
            RobotUtils.doesNotSeeView(R.id.fragment_shelters);
            RobotUtils.doesNotSeeView(R.id.fragment_favorites);
            RobotUtils.doesNotSeeView(R.id.fragment_settings);
        }

        void seesDogDiscover() {
            RobotUtils.seesView(R.id.fragment_discover);
            RobotUtils.seesToolbarWithTitle(context.getString(R.string.tab_dog));
            RobotUtils.doesNotSeeView(R.id.fragment_shelters);
            RobotUtils.doesNotSeeView(R.id.fragment_favorites);
            RobotUtils.doesNotSeeView(R.id.fragment_settings);
        }

        void seesShelters() {
            RobotUtils.seesView(R.id.fragment_shelters);
            RobotUtils.seesToolbarWithTitle(context.getString(R.string.menu_shelters));
            RobotUtils.doesNotSeeView(R.id.fragment_discover);
            RobotUtils.doesNotSeeView(R.id.fragment_favorites);
            RobotUtils.doesNotSeeView(R.id.fragment_settings);
        }

        void seesFavorites() {
            RobotUtils.seesView(R.id.fragment_favorites);
            RobotUtils.seesToolbarWithTitle(context.getString(R.string.menu_favorites));
            RobotUtils.doesNotSeeView(R.id.fragment_shelters);
            RobotUtils.doesNotSeeView(R.id.fragment_discover);
            RobotUtils.doesNotSeeView(R.id.fragment_settings);
        }

        void seesSettings() {
            RobotUtils.seesView(R.id.fragment_settings);
            RobotUtils.seesToolbarWithTitle(context.getString(R.string.menu_settings));
            RobotUtils.doesNotSeeView(R.id.fragment_shelters);
            RobotUtils.doesNotSeeView(R.id.fragment_favorites);
            RobotUtils.doesNotSeeView(R.id.fragment_discover);
        }
    }
}
