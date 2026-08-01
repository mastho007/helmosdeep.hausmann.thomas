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
 * Tests d'acceptation AI-6
 */
@SuiteDisplayName("Tests d'acceptation AI-6")
class AI6Test {

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

	@DisplayName("AI-6.1")
	@Test
	void shoud_end_mordor_turn() {		
		skipTurn();
		
		verifyLabelsMatch(
				".*Mordor.+Hommes.*",
				".*Hommes.*MVT.*19.*$",
				"^.*0.*0.*$", 
				"^.*PLAINE\\s*\\[1\\].*$", 
				"^.*Aragorn.*$");	
	}
	
	@DisplayName("AI-6.2")
	@Test
	void shoud_end_mankind_turn() {		
		skipTurn();
		
		skipTurn();
		
		verifyLabelsMatch(
				"^.*Mordor.+Hommes.*$",
				"^.*Mordor.*MVT.*16.*$",
				"^.*8.*4.*$", 
				"^.*MONTAGNE\\s*\\[3\\].*$", 
				"^.*Sauron.*$");
	}
	

	@DisplayName("AI-6.3")
	@Test
	void keeps_armies_states_between_each_turn() throws InterruptedException {
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
		
		skipTurn();
		
		//Sélectione le Rohirims
		move('R','R');		
		activate();
		
		// Déplace le Rohirims
		move('R','R');		
		activate();
		
		skipTurn();
		
		Thread.sleep(300);
		
		verifyLabelsMatch(
				"^.*Mordor.+Hommes.*$",
				"^.*Mordor.*MVT.*16.*$",
				"^.*8.*4.*$", 
				"^.*MONTAGNE\\s*\\[3\\].*$", 
				"^.*Sauron.*$");
		
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName("rohirrim"), 0, 4),
				"rohirrim should have moved to 04-00");
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName("trolls"), 1, 5),
				"Trolls should have moved to 05-01");		
		assertTrue(scene.checkUnitAt(unitTile -> unitTile.hasName("orcs"), 2, 6),
				"Orcs should have moved to 06-02");
		
	}

	
	private void skipTurn() {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_ENTER);
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
