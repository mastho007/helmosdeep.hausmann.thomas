package helmosdeep.acceptance;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
 * Tests d'acceptation AI-12
 */
@SuiteDisplayName("Tests d'acceptation AI-12")
class AI12Test {
	private static final String BATTLE_TITLE_PATTERN = ".*Mordor.+Hommes.*";

	private GameloopFixture mainWindow;

	@BeforeEach
	void setUp() {
		Levels.replaceBy("level-b.txt");
		var innerLoop = HelmosDeep.makeGameLoop();
		mainWindow = new GameloopFixture(innerLoop);
		innerLoop.run();
	}

	@AfterEach
	void tearDown() {
		Levels.replaceBack();
		mainWindow.cleanUp();
	}

	@DisplayName("AI-12.1")
	@Test
	void should_comply_with_old_rule() throws InterruptedException {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		// Sélectionne le Wargs en 0-1
		moveThenActivate('D');

		// Déplace le Wargs en 0-2
		moveThenActivate('D');

		skipTurn();

		// Selectionne le Rohirrim
		moveThenActivate('L');

		// Déplace le Rohirrim en 1-2
		moveThenActivate('U');

		// Attaque le Wargs
		moveThenActivate('L');

		moveThenActivate('R', 'R');

		Thread.sleep(300);

		verifyLabelsMatch(
				BATTLE_TITLE_PATTERN, 
				".*Hommes.*MVT.*2.*$",
				"^.*2.*2.*$", 
				"^.*FORET\\s*\\[2\\].*$",
				"^.*Rohirrim.*$", "^.*$", "^.*");
		
		var scene = mainWindow.<GameScene>scene(ViewsId.PLAY_GAME, GameScene.class);
		
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName("Rohirrim"), 2, 1),
				"Rohirrim should stay at 1-2");
		assertTrue(!scene.checkUnitAt(unitTile -> unitTile.hasName("Rohirrim"), 2, 2),
				"There should be no Rohirrim in 2-2");
	}

	@DisplayName("AI-12.2")
	@Test
	void should_apply_new_rule_with_a_one_tile_move() throws InterruptedException {
		activateLightUnitRule();
		// Sélectionne le Wargs en 0-1
		moveThenActivate('D');

		// Déplace le Wargs en 0-2
		moveThenActivate('D');

		skipTurn();

		// Selectionne le Rohirrim
		moveThenActivate('L');

		// Déplace le Rohirrim en 1-2
		moveThenActivate('U');

		// Attaque le Wargs
		moveThenActivate('L');

		moveThenActivate('R', 'R');

		Thread.sleep(300);

		verifyLabelsMatch(
				BATTLE_TITLE_PATTERN, 
				".*Hommes.*MVT.*0.*$", 
				"^.*2.*2.*$", 
				"^.*FORET\\s*\\[2\\].*$",
				"^.*Rohirrim.*$", "^.*$", "^.*");
		
		var scene = mainWindow.<GameScene>scene(ViewsId.PLAY_GAME, GameScene.class);
		
		assertTrue(!scene.checkUnitAt(unitTile -> unitTile.hasName("Rohirrim"), 2, 1),
				"Rohirrim should have moved from 1-2");
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName("Rohirrim"), 2, 2),
				"There should be a Rohirrim in 2-2");
	}

	@DisplayName("AI-12.3")
	@Test
	void should_ignore_move_on_a_too_expensive_tile() throws InterruptedException {
		activateLightUnitRule();
		// Sélectionne le Wargs en 0-1
		moveThenActivate('D');

		// Déplace le Wargs en 1-2
		moveThenActivate('D','R');

		// Attaque le Rohirrim en 1-3
		moveThenActivate('D');
		
		skipTurn();

		// Selectionne le Rohirrim
		moveThenActivate('L');

		// Déplace le Rohirrim en 1-1 => Refus
		moveThenActivate('U','U');

		Thread.sleep(300);

		verifyLabelsMatch(
				BATTLE_TITLE_PATTERN, 
				".*Hommes.*MVT.*4.*$", 
				"^.*1.*1.*$", 
				"^.*MONTAGNE\\s*\\[3\\].*$",
				"^.*Rohirrim.*$", "^.*$", "^.*");
		
		var scene = mainWindow.<GameScene>scene(ViewsId.PLAY_GAME, GameScene.class);
		
		assertTrue(!scene.checkUnitAt(unitTile -> unitTile.hasName("Rohirrim"), 1, 1),
				"Rohirrim should not be in 1-1");
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName("Rohirrim"), 3, 1),
				"There should be a Rohirrim in 1-3");
	}
	
	@DisplayName("AI-12.4")
	@Test
	void should_ignore_move_on_a_too_far_tile() throws InterruptedException {
		activateLightUnitRule();
		// Sélectionne le Wargs en 0-1
		moveThenActivate('D');

		// Déplace le Wargs en 0-2
		moveThenActivate('D');

		skipTurn();
		
		// Selectionne le Rohirrim
		moveThenActivate('L');

		// Déplace le Rohirrim en 1-2 => ok
		moveThenActivate('U');
		
		// Attaque le Wargs en 0-2 => Victoire
		moveThenActivate('L');
		
		// Déplace le Rohirrim en 0-1 => Refus
		moveThenActivate('U');

		Thread.sleep(300);

		verifyLabelsMatch(
				BATTLE_TITLE_PATTERN, 
				".*Hommes.*MVT.*2.*$", 
				"^.*0.*1.*$", 
				"^.*PLAINE\\s*\\[1\\].*$",
				"^.*Rohirrim.*$", "^.*$", "^.*");
		
		var scene = mainWindow.<GameScene>scene(ViewsId.PLAY_GAME, GameScene.class);
		
		assertTrue(!scene.checkUnitAt(unitTile -> unitTile.hasName("Rohirrim"), 1, 0),
				"Rohirrim should not be in 0-1");
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName("Rohirrim"), 2, 1),
				"There should be a Rohirrim in 1-2");
	}

	@DisplayName("AI-12.5")
	@Test
	void should_end_the_game_with_new_rule() throws InterruptedException {
		activateLightUnitRule();
		// Sélectionne le Wargs en 0-1
		moveThenActivate('D');

		// Déplace le Wargs en 0-2
		moveThenActivate('D');

		skipTurn();
		
		// Selectionne le Rohirrim
		moveThenActivate('L');

		// Déplace le Rohirrim en 1-2 => ok
		moveThenActivate('U');
		
		// Attaque le Wargs en 0-2 => Victoire
		moveThenActivate('L');
		
		// Sélectionne le Ents en 2-0
		moveThenActivate('U','U','R','R');

		// Attaque le Troll en 1-0 => Défaite
		moveThenActivate('L');
		
		skipTurn();
		
		skipTurn();
		
		// Sélectionne le Rohirrim en 1-2
		moveThenActivate('L','U');
		
		// Déplace le Rohirrim en 0-1
		moveThenActivate('L','U');
		
		// Attaque Sauron => défaite de l'armée
		moveThenActivate('U');
		
		skipTurn();
		
		Thread.sleep(300);
		
		verifyGameOverPanels();
	}
	

	private void activateLightUnitRule() {
		moveThenActivate('D');
		moveThenActivate('D');
		moveThenActivate('U');
		activate();
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
			var labels = new JLabelFixture[] { statusPanel.label("Situation[0]"), statusPanel.label("Situation[1]"),
					statusPanel.label("Situation[2]"), statusPanel.label("Situation[3]"),
					statusPanel.label("Situation[4]"), statusPanel.label("Situation[5]"),
					statusPanel.label("Situation[6]"), };

			for (int i = 0; i < labels.length; ++i) {
				labels[i].requireText(Pattern.compile(patterns[i], Pattern.CASE_INSENSITIVE));
			}
		} catch (ComponentLookupException ex) {
			throw new AssertionError("error while checking labels : " + ex.getMessage(), ex);
		}

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
