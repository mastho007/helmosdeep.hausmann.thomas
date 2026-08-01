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
 * Tests d'acceptation AI-7
 */
@SuiteDisplayName("Tests d'acceptation AI-7")
class AI7Test {

	private static final String WARGS_NAME = "wargs";
	private static final String SAURON_NAME = "sauron";
	private static final String ENTS_NAME = "ents";
	private static final String BATTLE_TITLE_PATTERN = ".*Mordor.+Hommes.*";
	private static final String WHATEVER_PATTERN = "^.*$";
	private GameloopFixture mainWindow;
	private GameScene scene;

	@BeforeEach
	void setUp() {
		var innerLoop = HelmosDeep.makeGameLoop();
		mainWindow = new GameloopFixture(innerLoop);
		innerLoop.run();
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		scene = mainWindow.<GameScene>scene(ViewsId.PLAY_GAME, GameScene.class);
	}

	@AfterEach
	void tearDown() {
		mainWindow.cleanUp();
	}

	@DisplayName("AI-7.1")
	@Test
	void should_attack_an_ennemy_and_lose() throws InterruptedException {
		// Sélectionne le Wargs en 06-04
		moveThenActivate('L','L');
		
		// Déplace le Wargs en 04-03
		moveThenActivate('L','L','U');
		
		// Attaque le Ents en 03-02
		moveThenActivate('L','U');
		
		Thread.sleep(300);
		
		verifyLabelsMatch(
				BATTLE_TITLE_PATTERN,
				".*Mordor.*MVT.*12.*$",
				"^.*3.*2.*$", 
				"^.*PLAINE\\s*\\[1\\].*$", 
				"^.*Sauron.*$",
				"^.*Wargs.*attaque.*Ents.*$",
				"^.*Ents.*"
		);
		
		assertTrue(!scene.checkUnitAt(unitTile -> unitTile.hasName(WARGS_NAME), 3, 4),
				"Wargs should disappears from 04-03");
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName(ENTS_NAME), 2, 3),
				"Ents should stay");
	}
	
	@DisplayName("AI-7.2")
	@Test
	void should_attack_an_ennemy_and_win() throws InterruptedException {
		// Sélectionne le Wargs en 06-04
		moveThenActivate('L','L');
		
		// Déplace le Wargs en 04-03
		moveThenActivate('L','L','U');
		
		skipTurn();
		
		// Sélectionne l'Ents en 03-02
		moveThenActivate('R','R','R','D', 'D');
		
		// Attaque le Wargs
		moveThenActivate('R','D');
		
		Thread.sleep(300);
		
		verifyLabelsMatch(
				BATTLE_TITLE_PATTERN,
				".*Hommes.*MVT.*19.*$",
				"^.*4.*3.*$", 
				"^.*FORET\\s*\\[2\\].*$", 
				"^.*Ents.*$",
				"^.*Ents.*attaque.*Wargs.*$",
				"^.*Ents.*"
		);
		
		assertTrue(!scene.checkUnitAt(unitTile -> unitTile.hasName(WARGS_NAME), 3, 4),
				"Wargs should disappears from 04-03");
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName(ENTS_NAME), 2, 3),
				"Ents should stay");
	}
	
	@DisplayName("AI-7.3")
	@Test
	void ignore_attacks_request_with_a_general() throws InterruptedException {
		// Déplace Sauron en 05-04
		moveThenActivate('L','L','L');
		
		skipTurn();
		
		// Sélectionne l'Ents en 03-02
		moveThenActivate('D','D','D','D', 'R', 'R', 'R');
		
		// Déplace l'Ents en 04-04
		moveThenActivate('R');
		
		skipTurn();
		// Attaque l'Ents en 04-04
		moveThenActivate('L');
		
		Thread.sleep(300);
		
		verifyLabelsMatch(
				BATTLE_TITLE_PATTERN,
				".*Mordor.*MVT.*16.*$",
				"^.*4.*4.*$", 
				"^.*FORET\\s*\\[2\\].*$", 
				"^.*Sauron.*$",
				WHATEVER_PATTERN,
				WHATEVER_PATTERN
		);
		
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName(SAURON_NAME), 4, 5),
				"Sauron should stay at 05-04");
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName(ENTS_NAME), 4, 4),
				"Ents should stay at 04-04");
	}
	
	@DisplayName("AI-7.4")
	@Test
	void ignore_the_second_attack_request_on_a_unit() throws InterruptedException {
		// Déplace Sauron en 05-04
		moveThenActivate('L','L','L');
		skipTurn();
		
		// Sélectionne l'Ents en 03-02
		moveThenActivate('D','D','D','D', 'R', 'R', 'R');
		
		// Déplace l'Ents en 04-04
		moveThenActivate('R');
		
		skipTurn();
		// Attaque l'Ents en 04-04
		moveThenActivate('L');
		
		Thread.sleep(300);
		
		verifyLabelsMatch(
				BATTLE_TITLE_PATTERN,
				".*Mordor.*MVT.*16.*$",
				"^.*4.*4.*$", 
				"^.*FORET\\s*\\[2\\].*$", 
				"^.*Sauron.*$",
				WHATEVER_PATTERN,
				WHATEVER_PATTERN
		);
		
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName(SAURON_NAME), 4, 5),
				"Sauron should stay at 05-04");
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName(ENTS_NAME), 4, 4),
				"Ents should stay at 04-04");
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
