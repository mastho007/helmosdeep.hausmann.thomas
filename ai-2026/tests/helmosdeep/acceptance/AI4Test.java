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
 * Tests d'acceptation AI-4
 */
@SuiteDisplayName("Tests d'acceptation AI-4")
class AI4Test {

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

	@DisplayName("AI-4.1")
	@Test
	void should_display_remaining_moving_points_and_active_unit() {
		verifyLabelsMatch(
				"^.*Mordor.+Hommes.*$",
				"^.*Mordor.*MVT.*16.*$",
				"^.*8.*4.*$", 
				"^.*MONTAGNE\\s*\\[3\\].*$", 
				"^.*Sauron.*$");
	}

	@DisplayName("AI-4.2")
	@Test
	void should_allow_unit_to_move_to_reachable_free_tile() throws InterruptedException {
		move('L', 'L', 'L');

		activate(); // Déclenche le déplacement

		// Nécessaire d'attendre la fin du déplacement, typiquement 0.3 sec.
		Thread.sleep(300);

		verifyLabelsMatch(
				"^.*Mordor.+Hommes.*$",
				"^.*Mordor.*MVT.*13.*$",
				"^.*5.*4.*$", 
				"^.*PLAINE\\s*\\[1\\].*$", 
				"^.*Sauron.*$");	
		
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName("sauron"), 4, 5),
				"You should have moved Sauron unit to 05-04");
	}
	
	@DisplayName("AI-4.3")
	@Test
	void ignore_move_of_already_moved_unit() throws InterruptedException {
		move('L', 'L', 'L');
		activate(); // Déclenche une première demande de déplacement

		move('U');		
		activate(); // Déclenche une seconde demande de déplacement, à ignorer
		
		// Nécessaire d'attendre la fin du déplacement, typiquement 0.3 sec.
		Thread.sleep(300);

		verifyLabelsMatch(
				"^.*Mordor.+Hommes.*$",
				"^.*Mordor.*MVT.*13.*$",
				"^.*5.*3.*$", 
				"^.*FORET\\s*\\[2\\].*$", 
				"^.*Sauron.*$");	
		
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName("sauron"), 4, 5),
				"Sauron unit you have stayed at 05-04");
	}
	
	@DisplayName("AI-4.4")
	@Test
	void ignore_move_to_an_unreachable_tile() throws InterruptedException {
		move('U', 'U', 'U');

		activate(); // Déclenche une première demande de déplacement
		
		// Nécessaire d'attendre la fin du déplacement, typiquement 0.3 sec.
		Thread.sleep(300);

		verifyLabelsMatch(
				"^.*Mordor.+Hommes.*$",
				"^.*Mordor.*MVT.*16.*$",
				"^.*8.*1.*$", 
				"^.*MONTAGNE\\s*\\[3\\].*$", 
				"^.*Sauron.*$");	
		
		assertTrue(!scene.checkUnitAt(unitTile -> unitTile.hasName("sauron"), 1, 8),
				"Sauron unit you have stayed at 08-04");
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName("sauron"), 4, 8),
				"Sauron unit you have stayed at 08-04");
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
