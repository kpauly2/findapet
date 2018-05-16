package tech.pauly.findapet.shared;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import tech.pauly.findapet.R;
import tech.pauly.findapet.utils.RobotUtils;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void onLaunch_showDiscover() {
        RobotUtils.seesView(R.id.fragment_discover);
    }

    @Test
    public void onClickMenu_seesNavigationPanel() {

    }

    @Test
    public void onNavigationPanel_clickShelter_seesShelters() {

    }

    @Test
    public void onNavigationPanel_clickFavorites_seesFavorites() {

    }

    @Test
    public void onNavigationPanel_clickDiscover_showPetTypes() {

    }

    @Test
    public void onNavigationPanel_clickDiscoverAndDog_seesDogs() {

    }
}