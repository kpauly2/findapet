package tech.pauly.findapet;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

public class MainTabActivityTest {

    @Rule
    public ActivityTestRule<MainTabActivity> activityRule = new ActivityTestRule<>(MainTabActivity.class);

    @Test
    public void onLaunch_showDiscover() {
        RobotUtils.seesView(R.id.fragment_discover);
    }

    @Test
    public void onClickDiscover_seeOnlyDiscover() {
        RobotUtils.clickView(R.id.navigation_discover);

        RobotUtils.seesView(R.id.fragment_discover);
        RobotUtils.doesNotSeeView(R.id.fragment_shelters);
        RobotUtils.doesNotSeeView(R.id.fragment_favorites);
        RobotUtils.doesNotSeeView(R.id.fragment_settings);
    }

    @Test
    public void onClickShelters_seeOnlyShelters() {
        RobotUtils.clickView(R.id.navigation_shelters);

        RobotUtils.seesView(R.id.fragment_shelters);
        RobotUtils.doesNotSeeView(R.id.fragment_discover);
        RobotUtils.doesNotSeeView(R.id.fragment_favorites);
        RobotUtils.doesNotSeeView(R.id.fragment_settings);
    }

    @Test
    public void onClickFavorites_seeOnlyFavorites() {
        RobotUtils.clickView(R.id.navigation_favorites);

        RobotUtils.seesView(R.id.fragment_favorites);
        RobotUtils.doesNotSeeView(R.id.fragment_discover);
        RobotUtils.doesNotSeeView(R.id.fragment_shelters);
        RobotUtils.doesNotSeeView(R.id.fragment_settings);
    }

    @Test
    public void onClickSettings_seeOnlySettings() {
        RobotUtils.clickView(R.id.navigation_settings);

        RobotUtils.seesView(R.id.fragment_settings);
        RobotUtils.doesNotSeeView(R.id.fragment_discover);
        RobotUtils.doesNotSeeView(R.id.fragment_shelters);
        RobotUtils.doesNotSeeView(R.id.fragment_favorites);
    }
}