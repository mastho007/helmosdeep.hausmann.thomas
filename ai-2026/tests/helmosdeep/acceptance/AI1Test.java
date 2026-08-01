package helmosdeep.acceptance;

import java.awt.event.KeyEvent;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SuiteDisplayName;

import helmosdeep.HelmosDeep;
import helmosdeep.supervisors.ViewsId;

/**
 * Tests d'acceptation de l'US AI-1
 * */
@SuiteDisplayName("Tests d'acceptation AI-1")
class AI1Test {
	private GameloopFixture mainWindow;

	@BeforeEach
	void setUp() {
		var innerLoop = HelmosDeep.makeGameLoop();
		mainWindow = new GameloopFixture(innerLoop);
		innerLoop.run();
	}

	@AfterEach
	void tearDown() {
		mainWindow.cleanUp();

	}
	
	@DisplayName("TA 1.1")
	@Test
	void should_have_two_items() {
		var firstLabel = mainWindow.label(ViewsId.MAIN_MENU+"[0]");
		var secondLabel = mainWindow.label(ViewsId.MAIN_MENU+"[1]");
		
		firstLabel.requireText(Pattern.compile("Nouvelle partie", Pattern.CASE_INSENSITIVE));
		secondLabel.requireText(Pattern.compile("Quitter", Pattern.CASE_INSENSITIVE));
	}
	
	@DisplayName("TA 1.2")
	@Test
	void should_exit_on_quit_selected() {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_DOWN);
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		mainWindow.requireNotVisible();
	}
	
	@DisplayName("TA 1.3")
	@Test
	void should_go_to_play_game_when_new_game_selected() {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_DOWN);
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_DOWN);
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		var statusPanel = mainWindow.panel("Situation");
		
		statusPanel.requireVisible();
		var firstLabel = statusPanel.label("Situation[0]");
		var secondLabel = statusPanel.label("Situation[1]");
		
		firstLabel.requireText(Pattern.compile(".*Mordor.+Hommes.*", Pattern.CASE_INSENSITIVE));
		secondLabel.requireText(Pattern.compile("Au tour.+Mordor.*", Pattern.CASE_INSENSITIVE));
	}
}
