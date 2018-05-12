package tech.pauly.findapet.shared;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import tech.pauly.findapet.R;
import tech.pauly.findapet.utils.RobotUtils;

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
}