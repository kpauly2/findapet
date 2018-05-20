package tech.pauly.findapet.shared;

import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MainActivityTest extends BaseEspressoTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivityRobot.Hands hands = new MainActivityRobot.Hands();
    private MainActivityRobot.Eyes eyes;

    @Before
    public void setup() {
        eyes = new MainActivityRobot.Eyes(getApplicationContext());
    }

    @Test
    public void onLaunch_showDiscover() {
        eyes.seesDiscover();
    }

    @Test
    public void onClickMenu_seesNavigationPanel() {
        eyes.seesNavigationPanelClosed();

        hands.clickMenu();

        eyes.seesNavigationPanelOpen();
    }

    @Test
    public void onNavigationPanel_clickDiscover_showPetTypes() {
        hands.clickMenu()
             .clickDiscover();

        eyes.seesDiscoverMenuOpen()
            .seesSheltersButtonNotSelected()
            .seesSettingsButtonNotSelected()
            .seesFavoritesButtonNotSelected();
    }

    @Test
    public void onNavigationPanel_clickDiscoverAndDog_seesDogs() {
        hands.clickMenu()
             .clickDiscover()
             .clickDog();

        eyes.seesNavigationPanelClosed()
            .seesDogDiscover();
    }

    @Test
    public void onNavigationPanel_clickShelters_seesShelters() {
        hands.clickMenu()
             .clickShelters();

        eyes.seesNavigationPanelClosed()
            .seesShelters();
    }

    @Test
    public void onNavigationPanel_clickFavorites_seesFavorites() {
        hands.clickMenu()
             .clickFavorites();

        eyes.seesNavigationPanelClosed()
            .seesFavorites();
    }

    @Test
    public void onNavigationPanel_clickSettings_seesSettings() {
        hands.clickMenu()
             .clickSettings();

        eyes.seesNavigationPanelClosed()
            .seesSettings();
    }

    @Test
    public void onNavigationPanel_clickSheltersAndReopenNavigation_seesSheltersButtonSelected() {
        hands.clickMenu()
             .clickShelters()
             .clickMenu();

        eyes.seesNavigationPanelOpen()
            .seesSheltersButtonSelected()
            .seesDiscoverMenuClosed()
            .seesSettingsButtonNotSelected()
            .seesFavoritesButtonNotSelected();
    }

    @Test
    public void onNavigationPanel_clickFavoritesAndReopenNavigation_seesFavoritesButtonSelected() {
        hands.clickMenu()
             .clickFavorites()
             .clickMenu();

        eyes.seesNavigationPanelOpen()
            .seesFavoritesButtonSelected()
            .seesDiscoverMenuClosed()
            .seesSheltersButtonNotSelected()
            .seesSettingsButtonNotSelected();
    }

    @Test
    public void onNavigationPanel_clickSettingsAndReopenNavigation_seesSettingsButtonSelected() {
        hands.clickMenu()
             .clickSettings()
             .clickMenu();

        eyes.seesNavigationPanelOpen()
            .seesSettingsButtonSelected()
            .seesDiscoverMenuClosed()
            .seesFavoritesButtonNotSelected()
            .seesSheltersButtonNotSelected();
    }

    @Test
    public void onNavigationPanel_clickSheltersTwice_seesSheltersStillSelected() {
        hands.clickMenu()
             .clickShelters()
             .clickMenu()
             .clickShelters()
             .clickMenu();

        eyes.seesNavigationPanelOpen()
            .seesSheltersButtonSelected()
            .seesDiscoverMenuClosed()
            .seesSettingsButtonNotSelected()
            .seesFavoritesButtonNotSelected();
    }
}