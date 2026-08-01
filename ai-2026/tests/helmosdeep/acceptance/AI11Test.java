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
 * Tests d'acceptation AI-11
 */
@SuiteDisplayName("Tests d'acceptation AI-11")
class AI11Test {
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
	
	@DisplayName("AI-11.1")
	@Test
	void should_comply_with_old_rule()  throws InterruptedException  {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		// Sélectionne le Troll en 1-0
		moveThenActivate('R');
		
		// Déplace le Troll en 1-1
		moveThenActivate('D');
		
		Thread.sleep(300);
		
		verifyLabelsMatch(
				BATTLE_TITLE_PATTERN,
				".*Mordor.*MVT.*4.*$",
				"^.*1.*1.*$", 
				"^.*MONTAGNE\\s*\\[3\\].*$", 
				"^.*Trolls.*$",
				"^.*$",
				"^.*"
		);
	}
	
	@DisplayName("AI-11.2")
	@Test
	void should_apply_new_rule_with_a_one_tile_move()  throws InterruptedException  {
		activateHeavyUnitRule();
		
		// Sélectionne le Troll en 1-0
		moveThenActivate('R');
		
		// Déplace le Troll en 1-1
		moveThenActivate('D');
		
		Thread.sleep(300);
		
		verifyLabelsMatch(
				BATTLE_TITLE_PATTERN,
				".*Mordor.*MVT.*3.*$",
				"^.*1.*1.*$", 
				"^.*MONTAGNE\\s*\\[3\\].*$", 
				"^.*Trolls.*$",
				"^.*$",
				"^.*"
		);
	}
	
	@DisplayName("AI-11.3")
	@Test
	void should_apply_new_rule_with_a_many_tiles_move()  throws InterruptedException  {
		activateHeavyUnitRule();
		
		// Sélectionne le Troll en 1-0
		moveThenActivate('R');
		
		// Déplace le Troll en 1-2
		moveThenActivate('D', 'D');
		
		Thread.sleep(300);
		
		verifyLabelsMatch(
				BATTLE_TITLE_PATTERN,
				".*Mordor.*MVT.*2.*$",
				"^.*1.*2.*$", 
				"^.*FORET\\s*\\[2\\].*$", 
				"^.*Trolls.*$",
				"^.*$",
				"^.*"
		);
	}
	
	@DisplayName("AI-11.4")
	@Test
	void should_apply_new_rule_with_every_heavy_unit()  throws InterruptedException  {
		activateHeavyUnitRule();
		
		skipTurn();
		
		// Sélectionne l'Ents en 2-0
		moveThenActivate('U', 'U', 'U');
		
		// Déplace le Troll en 3-1
		moveThenActivate('D', 'R');
		
		Thread.sleep(300);
		
		verifyLabelsMatch(
				BATTLE_TITLE_PATTERN,
				".*Hommes.*MVT.*2.*$",
				"^.*3.*1.*$", 
				"^.*MONTAGNE\\s*\\[3\\].*$", 
				"^.*Ents.*$",
				"^.*$",
				"^.*"
		);
	}
	
	@DisplayName("AI-11.5")
	@Test
	void should_not_consume_too_many_mvt_cost()  throws InterruptedException  {
		activateHeavyUnitRule();
		var scene = mainWindow.<GameScene>scene(ViewsId.PLAY_GAME, GameScene.class);
		
		skipTurn();
		
		// Sélectionne l'Ents en 2-0
		moveThenActivate('U', 'U', 'U');
		
		// Déplace le Troll en 3-2
		moveThenActivate('D', 'D','R');
		
		Thread.sleep(300);
		
		verifyLabelsMatch(
				BATTLE_TITLE_PATTERN,
				".*Hommes.*MVT.*4.*$",
				"^.*3.*2.*$", 
				"^.*FORET\\s*\\[2\\].*$", 
				"^.*Ents.*$",
				"^.*$",
				"^.*"
		);
		
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName("Ents"), 0, 2),
				"Ents should stay at 2-0");
		assertTrue(!scene.checkUnitAt(unitTile -> unitTile.hasName("Ents"), 2, 3),
				"There should be no Ents in 3-2");
	}

	private void activateHeavyUnitRule() {
		moveThenActivate('D');
		activate();
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
