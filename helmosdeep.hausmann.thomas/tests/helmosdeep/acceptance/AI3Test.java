package helmosdeep.acceptance;

import static org.junit.Assert.assertNotNull;

import java.awt.event.KeyEvent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SuiteDisplayName;

import helmosdeep.HelmosDeep;
import helmosdeep.supervisors.ViewsId;
import helmosdeep.views.MainMenuScene;

/**
 *Implémente les TA de l'US 3
 * */
@SuiteDisplayName("Tests d'acceptation AI-3")
class AI3Test {
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
	
	@Test
	void should_go_back_to_the_main_menu_when_escape_is_pressed() {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_ESCAPE);
		
		var scene = mainWindow.<MainMenuScene>scene(ViewsId.MAIN_MENU, MainMenuScene.class);
		
		assertNotNull(scene);
		mainWindow.panel(ViewsId.MAIN_MENU).requireVisible();
	}
}
