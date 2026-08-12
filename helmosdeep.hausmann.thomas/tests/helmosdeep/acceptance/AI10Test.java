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
 * Tests d'acceptation AI-9
 */
@SuiteDisplayName("Tests d'acceptation AI-10")
class AI10Test {

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
	
	@DisplayName("TA 10.1")
	@Test
	void should_have_three_items() {
		var firstLabel = mainWindow.label(ViewsId.MAIN_MENU+"[0]");
		var secondLabel = mainWindow.label(ViewsId.MAIN_MENU+"[1]");
		var thirdLabel = mainWindow.label(ViewsId.MAIN_MENU+"[2]");
		
		firstLabel.requireText(Pattern.compile("Nouvelle partie", Pattern.CASE_INSENSITIVE));
		secondLabel.requireText(Pattern.compile("R.gles Sp.cifiques", Pattern.CASE_INSENSITIVE));
		thirdLabel.requireText(Pattern.compile("Quitter", Pattern.CASE_INSENSITIVE));
	}
	
	@DisplayName("TA 10.2")
	@Test
	void should_show_the_option_menu() {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_DOWN);
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		var firstLabel = mainWindow.label(ViewsId.MAIN_MENU+"[0]");
		var secondLabel = mainWindow.label(ViewsId.MAIN_MENU+"[1]");
		var thirdLabel = mainWindow.label(ViewsId.MAIN_MENU+"[2]");
		
		firstLabel.requireText(Pattern.compile("\\[\\s?\\]\\s?Unit.s\\s?lourdes", Pattern.CASE_INSENSITIVE));
		secondLabel.requireText(Pattern.compile("\\[\\s?\\]\\s?Unit.s\\s?l.g.res", Pattern.CASE_INSENSITIVE));
		thirdLabel.requireText(Pattern.compile("Retour au menu principal", Pattern.CASE_INSENSITIVE));
	}
	
	@DisplayName("TA 10.3")
	@Test
	void should_select_first_item() {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_DOWN);
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		var firstLabel = mainWindow.label(ViewsId.MAIN_MENU+"[0]");
		
		firstLabel.requireText(Pattern.compile("\\[x\\]\\s?Unit.s\\s?lourdes", Pattern.CASE_INSENSITIVE));
		
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		firstLabel = mainWindow.label(ViewsId.MAIN_MENU+"[0]");
		firstLabel.requireText(Pattern.compile("\\[\\s?\\]\\s?Unit.s\\s?lourdes", Pattern.CASE_INSENSITIVE));
	}
	
	
	@DisplayName("TA 10.4")
	@Test
	void should_select_two_items() {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_DOWN);
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_DOWN);
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		var firstLabel = mainWindow.label(ViewsId.MAIN_MENU+"[0]");
		var secondLabel = mainWindow.label(ViewsId.MAIN_MENU+"[1]");
		
		firstLabel.requireText(Pattern.compile("\\[x\\]\\s?Unit.s\\s?lourdes", Pattern.CASE_INSENSITIVE));
		secondLabel.requireText(Pattern.compile("\\[x\\]\\s?Unit.s\\s?l.g.res", Pattern.CASE_INSENSITIVE));
	}
	
	@DisplayName("TA 10.5")
	@Test
	void should_launch_a_game() {
		//Entrer dans les règles spécifiques
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_DOWN);
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		// Activer l'item unite légère
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_DOWN);
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		// Retour au menu
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_UP);
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		// Lancer une nouvelle partie
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		var statusPanel = mainWindow.panel("Situation");
		
		statusPanel.requireVisible();
		var firstLabel = statusPanel.label("Situation[0]");
		var secondLabel = statusPanel.label("Situation[1]");
		
		firstLabel.requireText(Pattern.compile(".*Mordor.+Hommes.*", Pattern.CASE_INSENSITIVE));
		secondLabel.requireText(Pattern.compile("Au tour.+Mordor.*", Pattern.CASE_INSENSITIVE));
	}
}
