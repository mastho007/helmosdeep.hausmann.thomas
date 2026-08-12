package helmosdeep.acceptance;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.KeyEvent;
import java.util.StringJoiner;
import java.util.regex.Pattern;

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
 * Tests d'acceptation de l'US AI-2
 * */
@SuiteDisplayName("Tests d'acceptation AI-2")
class AI2Test {
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
	
	@DisplayName("TA 2.1")
	@Test
	void should_select_active_hex_when_sauron_is_and_display_hex_info_on_game_started() {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		var scene = mainWindow.<GameScene>scene(ViewsId.PLAY_GAME, GameScene.class);
		
		assertTrue(scene.checkActiveHex( ht -> ht.getId().endsWith("08-04")), 
				"You should have moved the camera to row 4 and col 8");
		assertTrue(scene.hasUnitAt(4, 8), 
				"Sauron should be at row 4 and col 8");
		assertTrue(scene.checkUnitAt( ut -> ut.hasName("Sauron"), 4, 8), 
				"Sauron should appear at row 4 and col 8");
		
		var firstLabel = situationAt(0);
		var secondLabel = situationAt(1);
		var thirdLabel = situationAt(2);
		var fourthLabel = situationAt(3);
		
		firstLabel.requireText(Pattern.compile(".*Mordor.+Hommes.*", Pattern.CASE_INSENSITIVE));
		secondLabel.requireText(Pattern.compile(".*Mordor.*", Pattern.CASE_INSENSITIVE));
		thirdLabel.requireText(Pattern.compile("^.*8.*4.*$"));
		fourthLabel.requireText(Pattern.compile("^.*MONTAGNE\s*\\[3\\].*$", Pattern.CASE_INSENSITIVE));		
	}
	
	@DisplayName("TA 2.2")
	@Test
	void should_update_hex_info_on_game_left_move() {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		var scene = mainWindow.<GameScene>scene(ViewsId.PLAY_GAME, GameScene.class);
		
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_LEFT);
		
		assertTrue(scene.checkActiveHex( ht -> ht.getId().endsWith("07-04")), 
				"You should have moved the camera to row 4 and col 7");
		
		var firstLabel = situationAt(0);
		var secondLabel = situationAt(1);
		var thirdLabel = situationAt(2);
		var fourthLabel = situationAt(3);
		
		firstLabel.requireText(Pattern.compile(".*Mordor.+Hommes.*", Pattern.CASE_INSENSITIVE));
		secondLabel.requireText(Pattern.compile(".*Mordor.*", Pattern.CASE_INSENSITIVE));
		thirdLabel.requireText(Pattern.compile("^.*7.*4.*$"));
		fourthLabel.requireText(Pattern.compile("^.*PLAINE\\s*\\[1\\].*$", Pattern.CASE_INSENSITIVE));		
	}
	
	@DisplayName("TA 2.3")
	@Test
	void should_update_hex_info_on_game_up_move() {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		var scene = mainWindow.<GameScene>scene(ViewsId.PLAY_GAME, GameScene.class);
		
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_UP);
		
		assertTrue(scene.checkActiveHex( ht -> ht.getId().endsWith("08-03")), 
				"You should have moved the camera to row 3 and col 8");

		var thirdLabel = situationAt(2);
		var fourthLabel = situationAt(3);

		thirdLabel.requireText(Pattern.compile("^.*8.*3.*$"));
		fourthLabel.requireText(Pattern.compile("^.*PLAINE\\s*\\[1\\].*$", Pattern.CASE_INSENSITIVE));		
	}
	
	@DisplayName("TA 2.4")
	@Test
	void should_update_hex_info_on_many_moves() {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		var scene = mainWindow.<GameScene>scene(ViewsId.PLAY_GAME, GameScene.class);
		move('U','U','L','L','D','R');
		
		assertTrue(scene.checkActiveHex( ht -> ht.getId().endsWith("07-03")), 
				"You should have moved the camera to row 3 and col 7");

		var thirdLabel = situationAt(2);
		var fourthLabel = situationAt(3);

		thirdLabel.requireText(Pattern.compile("^.*7.*3.*$"));
		fourthLabel.requireText(Pattern.compile("^.*FOR.T\\s*\\[2\\].*$", Pattern.CASE_INSENSITIVE));		
		
		assertTrue(scene.hasUnitAt(3, 7), 
				"Orcs should be at row 3 and col 7");
		assertTrue(scene.checkUnitAt( ut -> ut.hasName("Orcs"), 3, 7), 
				"Orcs should appear at row 3 and col 7");
	}
	
	private JLabelFixture situationAt(int index) {
		var statusPanel = mainWindow.panel("Situation");
		return statusPanel.label("Situation["+index+"]"); 
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
}
