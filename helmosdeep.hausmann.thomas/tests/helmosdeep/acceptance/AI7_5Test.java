package helmosdeep.acceptance;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.KeyEvent;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import org.fest.swing.exception.ComponentLookupException;
import org.fest.swing.fixture.JLabelFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SuiteDisplayName;

import helmosdeep.HelmosDeep;
import helmosdeep.supervisors.ViewsId;
import helmosdeep.util.Contract;
import helmosdeep.views.GameScene;

/**
 * Tests d'acceptation AI-7.5
 */
@SuiteDisplayName("Tests d'acceptation AI-7.5")
class AI7_5Test {

	private GameloopFixture mainWindow;
	private GameScene scene;

	@BeforeEach
	void setUp() {
		Levels.replaceBy("level-a.txt");
		var innerLoop = HelmosDeep.makeGameLoop();
		mainWindow = new GameloopFixture(innerLoop);
		innerLoop.run();
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		scene = mainWindow.<GameScene>scene(ViewsId.PLAY_GAME, GameScene.class);
	}

	@AfterEach
	void tearDown() {
		mainWindow.cleanUp();
		Levels.replaceBack();
	}

	@DisplayName("AI-7.5")
	@Test
	void should_handle_many_attacks() throws InterruptedException {
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
		
		Thread.sleep(300);
		
		verifyLabelsMatch(
				".*Mordor.+Hommes.*",
				".*Mordor.*MVT.*0.*$",
				"^.*2.*2.*$", 
				"^.*MONTAGNE\\s*\\[3\\].*$", 
				"^.*Sauron.*$",
				"^.*Trolls.*6.*Aragorn.*9.*\\s*$",
				"^.*Aragorn.*\\s*"
		);
		
		assertTrue(!scene.checkUnitAt(unitTile -> unitTile.hasName("wargs"), 2, 0),
				"Wargs should disappears from 00-02");
		assertTrue(!scene.checkUnitAt(unitTile -> unitTile.hasName("trolls"), 1, 1),
				"Trolls should disappears from 01-01");
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
	
	
	private void verifyLabelsMatch(String... patterns) {
		try {

			var statusPanel = mainWindow.panel("Situation");
			var labels = new JLabelFixture[] { 
					statusPanel.label("Situation[0]"), 
					statusPanel.label("Situation[1]"),
					statusPanel.label("Situation[2]"), 
					statusPanel.label("Situation[3]"),
					statusPanel.label("Situation[4]"),
					statusPanel.label("Situation[5]"),
					statusPanel.label("Situation[6]"),};

			for (int i = 0; i < labels.length; ++i) {
				labels[i].requireText(Pattern.compile(patterns[i], Pattern.CASE_INSENSITIVE));
			}
		} catch (ComponentLookupException ex) {
			throw new AssertionError("error while checking labels : "+ex.getMessage(), ex);
		}

	}

}
