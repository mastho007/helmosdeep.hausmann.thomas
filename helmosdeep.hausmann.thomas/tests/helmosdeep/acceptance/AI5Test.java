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
 * Tests d'acceptation AI-5
 */
@SuiteDisplayName("Tests d'acceptation AI-5")
class AI5Test {

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

	@DisplayName("AI-5.1")
	@Test
	void should_pick_another_friendly_unit() {
		move('U','U','L');		
		activate();
		
		verifyLabelsMatch(
				".*Mordor.+Hommes.*",
				".*Mordor.*",
				"^.*7.*2.*$", 
				"^.*PLAINE\\s*\\[1\\].*$", 
				"^.*Wargs.*$");	
	}
	
	@DisplayName("AI-5.2")
	@Test
	void should_ignore_enemy_unit_selection() {
		move('L','L','L','L','L','U','U');		
		activate();
		
		verifyLabelsMatch(
				".*Mordor.+Hommes.*",
				".*Mordor.*",
				"^.*3.*2.*$", 
				"^.*PLAINE\\s*\\[1\\].*$", 
				"^.*Sauron.*$");	
	}
	
	@DisplayName("AI-5.3")
	@Test
	void should_move_another_unit() throws InterruptedException {
		move('L','L','U','U');		
		activate();
		
		move('U','L');		
		activate();
		
		Thread.sleep(300);
		
		verifyLabelsMatch(
				"^.*Mordor.+Hommes.*$",
				"^.*Mordor.*MVT.*14.*$",
				"^.*5.*1.*$", 
				"^.*FORET\\s*\\[2\\].*$", 
				"^.*Trolls.*$");
		
		assertTrue(!scene.checkUnitAt(unitTile -> unitTile.hasName("trolls"), 2, 6),
				"Trolls should have moved from 06-02");
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName("trolls"), 1, 5),
				"Trolls should have moved to 05-01");
	}
	
	@DisplayName("AI-5.4")
	@Test
	void should_move_other_units() throws InterruptedException {
		// Sélectionne le troll
		move('L','L','U','U');
		activate();		
		// Déplace le troll
		move('U','L');		
		activate();
		
		// Sélectionne l'orc
		move('D','R','D','R');		
		activate();
		
		// Déplace l'orc
		move('U','L');		
		activate();
		
		// Sélectionne Sauron
		move('D','R','D','R');		
		activate();
				
		// Déplace Sauron
		move('L','U');		
		activate();
		
		Thread.sleep(300);
		
		verifyLabelsMatch(
				".*Mordor.+Hommes.*",
				".*Mordor.*",
				"^.*7.*3.*$", 
				"^.*FORET\\s*\\[2\\].*$", 
				"^.*Sauron.*$");	
		
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName("trolls"), 1, 5),
				"Trolls should have moved to 05-01");
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName("orcs"), 2, 6),
				"Orcs should have moved to 06-02");		
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName("sauron"), 3, 7),
				"Sauron should have moved to 07-03");
	}
	
	@DisplayName("AI-5.5")
	@Test
	void should_comply_with_move_rules() throws InterruptedException {
		// Sélectionne le troll
		moveThenActivate('L','L','U','U');
		
		// Déplace le troll => demande acceptée
		moveThenActivate('U','L');		
		
		// Tente un redéplacement => demande ignorée
		moveThenActivate('U');	
		
		// Sélectionne Sauron
		moveThenActivate('R','R','R','D','D','D','D');
		
		// Déplace Sauron sur une tuile inaccessible => demande ignorée
		moveThenActivate('L','L','L','L');
		
		// Sélectionne Wargs
		moveThenActivate('R','R');
		
		// Déplace Wargs sur une tuile occupée => demande ignorée
		moveThenActivate('L','L','L');
		
		// Déplace Wargs sur une tuile accessible et libre => demandée acceptée
		moveThenActivate('U','R');
		
		moveThenActivate('R','R','R','U');
		
		moveThenActivate('U','L');
		
		// Sélectionne wargs
		moveThenActivate('R','R','D');
		
		// Déplace Wargs => demande acceptée
		moveThenActivate('U','L');
		
		// Sélectionne Sauron
		moveThenActivate('R','D','D','D');
		
		// Tente de déplacer Sauron sur une case trop lointaine => demande refusée
		moveThenActivate('U','U');
		
		// Tente de déplacer Sauron sur une case qui consomme le pot => demande acceptée
		moveThenActivate('L','L');
		
		Thread.sleep(300);
		
		verifyLabelsMatch(
				".*Mordor.+Hommes.*",
				"^.*Mordor.*MVT.*0.*$",
				"^.*6.*2.*$", 
				"^.*PLAINE\\s*\\[1\\].*$", 
				"^.*Sauron.*$");	
	}

	private void activate() {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
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

	private void moveThenActivate(char... keys) {
		move(keys);
		activate();
	}
	
	private void verifyLabelsMatch(String... patterns) {
		try {

			var statusPanel = mainWindow.panel("Situation");
			var labels = new JLabelFixture[] { statusPanel.label("Situation[0]"), statusPanel.label("Situation[1]"),
					statusPanel.label("Situation[2]"), statusPanel.label("Situation[3]"),
					statusPanel.label("Situation[4]") };

			for (int i = 0; i < labels.length; ++i) {
				labels[i].requireText(Pattern.compile(patterns[i], Pattern.CASE_INSENSITIVE));
			}
		} catch (ComponentLookupException ex) {
			throw new AssertionError("error while checking labels : "+ex.getMessage(), ex);
		}

	}

}
