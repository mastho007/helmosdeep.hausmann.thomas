package helmosdeep.acceptance;

import java.awt.event.KeyEvent;
import java.util.StringJoiner;

import org.fest.swing.exception.ComponentLookupException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SuiteDisplayName;

import helmosdeep.HelmosDeep;
import helmosdeep.util.Contract;

/**
 * Tests d'acceptation AI-8
 */
@SuiteDisplayName("Tests d'acceptation AI-8")
class AI8Test {

	private GameloopFixture mainWindow;

	@BeforeEach
	void setUp() {
		Levels.replaceBy("level-a.txt");
		var innerLoop = HelmosDeep.makeGameLoop();
		mainWindow = new GameloopFixture(innerLoop);
		innerLoop.run();
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
	}

	@AfterEach
	void tearDown() {
		Levels.replaceBack();
		mainWindow.cleanUp();
	}
	

	@DisplayName("AI-8.1")
	@Test
	void should_end_game_on_no_active_fighters_left() throws InterruptedException {
		// Sélectionne le Troll en 1-0
		moveThenActivate('R');
		// Déplace le Troll en 1-1
		moveThenActivate('D');
		
		// Sélectionne le Wargs en 0-1
		moveThenActivate('L');
		// Déplace le Wargs en 0-2
		moveThenActivate('D');
		
		// Attaque le Gondoriens en 1-2
		moveThenActivate('R');
		
		// Sélectionne le Trols en 1-1
		moveThenActivate('U');
		
		// Attaque Sauron en 2-2
		moveThenActivate('D', 'R');
				
		skipTurn();
		
		Thread.sleep(300);
		
		verifyGameOverPanels();
	}
	
	@DisplayName("AI-8.2")
	@Test
	void should_end_game_on_no_other_fighters_left() throws InterruptedException {
		// Sélectionne le Wargs en 0-1
		moveThenActivate('D');
		// Déplace le Wargs en 0-2
		moveThenActivate('D');
		
		// Sélectionne le Trolls en 1-0
		moveThenActivate('U','U','R');
		// Déplace le Trolls en 1-1
		moveThenActivate('D');
		
		// Sélectionne Sauron en 0-0
		moveThenActivate('U','U','L');
		
		// Déplace Suaron en 1-0
		moveThenActivate('R');
		
		// Sélectionne le Trolls en 1-1
		moveThenActivate('D');
		
		// Attaque le Gondoriens
		moveThenActivate('D');
		
		skipTurn();
		
		Thread.sleep(300);
		
		verifyGameOverPanels();
	}
	

	@DisplayName("AI-8.3")
	@Test
	void should_end_game_on_no_other_general_left() throws InterruptedException {
		// Sélectionne le Wargs en 0-1
		moveThenActivate('D');
		// Déplace le Wargs en 0-2
		moveThenActivate('D');
		
		// Sélectionne le Trolls en 1-0
		moveThenActivate('U','U','R');
		// Déplace le Trolls en 1-1
		moveThenActivate('D');
		
		skipTurn();
		
		// Sélectionne le Gondoriens en 1-2
		moveThenActivate('L');
		
		// Déplace le Gondoriens en 0-1
		moveThenActivate('L','U');
		
		skipTurn();
				
		// Déplace Sauron en 1-0
		moveThenActivate('R');
		
		// Sélectionne le Wargs
		moveThenActivate('D','D','L');
		
		moveThenActivate('R');
		
		// Sélectionne le Trolls
		moveThenActivate('U');
		
		// Attaque Aragorn
		moveThenActivate('D', 'R');
		
		skipTurn();
		
		Thread.sleep(300);
		
		verifyGameOverPanels();
	}

	private void moveThenActivate(char... keys) {
		move(keys);
		activate();
	}
	
	private void move(char... keys) {
		int index = 0;
		var errors = new StringJoiner(",");
		for (var k : keys) {
			switch (k) {
			case 'U' -> mainWindow.pressAndReleaseKeys(KeyEvent.VK_UP);
			case 'D' -> mainWindow.pressAndReleaseKeys(KeyEvent.VK_DOWN);
			case 'L' -> mainWindow.pressAndReleaseKeys(KeyEvent.VK_LEFT);
			case 'R' -> mainWindow.pressAndReleaseKeys(KeyEvent.VK_RIGHT);
			default -> errors.add("(%d,%c)".formatted(index, k));
			}
			++index;
		}

		Contract.check(errors.length() == 0, "Errors in keys : " + errors.toString());
	}
	
	private void activate() {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
	}
	
	private void skipTurn() {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_ENTER);
	}
	
	private void verifyGameOverPanels() {
		try {
			mainWindow.panel("Panneau gauche");
			mainWindow.panel("Panneau droit");
			mainWindow.panel("Panneau de dessous");			
		} catch (ComponentLookupException ex) {
			throw new AssertionError("error while checking labels : "+ex.getMessage(), ex);
		}

	}

}
