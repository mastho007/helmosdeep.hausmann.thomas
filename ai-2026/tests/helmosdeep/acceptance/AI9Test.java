package helmosdeep.acceptance;

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

/**
 * Tests d'acceptation AI-9
 */
@SuiteDisplayName("Tests d'acceptation AI-9")
class AI9Test {

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
	
	@DisplayName("AI-9.1")
	@Test
	void should_end_game_on_no_active_units_left() throws InterruptedException {
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
		
		verifyLeftPanel(
				"^.*Mordor.*$",
				"^.*1.*$",
				"^.*General.*vivant.*$",
				"^.*tour.*1.*$",
				"^.*influence.*3.*$");		
		verifyRightPanel(
				"^.*Hommes.*$",
				"^.*2.*$",
				"^.*General.*vivant.*$",
				"^.*pas joue.*$",
				"^.*influence.*5.*$");
		verifyBottomPanel(
				"^.*Resultat.*$",
				"^.*Hommes.*$",
				"^.*1.*$",
				"^.*1.*$");
	}
	
	@DisplayName("AI-9.2")
	@Test
	void should_end_game_on_no_other_units_left() throws InterruptedException {
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
		
		// Déplace Sauron en 1-0
		moveThenActivate('R');
		
		// Sélectionne le Trolls en 1-1
		moveThenActivate('D');
		
		// Attaque le Gondoriens
		moveThenActivate('D');
		
		skipTurn();
		
		Thread.sleep(300);
		
		verifyLeftPanel(
				"^.*Mordor.*$",
				"^.*3.*$",
				"^.*General.*vivant.*$",
				"^.*tour.*1.*$",
				"^.*influence.*5.*$");		
		verifyRightPanel(
				"^.*Hommes.*$",
				"^.*1.*$",
				"^.*General.*vivant.*$",
				"^.*pas joue.*$",
				"^.*influence.*1.*$");
		verifyBottomPanel(
				"^.*Resultat.*$",
				"^.*Mordor.*$",
				"^.*1.*$",
				"^.*1.*$");
	}
	
	@DisplayName("AI-9.3")
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
		
		verifyLeftPanel(
				"^.*Mordor.*$",
				"^.*3.*$",
				"^.*General.*vivant.*$",
				"^.*tour.*3.*$",
				"^.*influence.*6.*$");		
		verifyRightPanel(
				"^.*Hommes.*$",
				"^.*1.*$",
				"^.*General.*mort.*$",
				"^.*Meilleur tour.*2.*$",
				"^.*influence.*2.*$");
		verifyBottomPanel(
				"^.*Resultat.*$",
				"^.*Mordor.*$",
				"^.*3.*$",
				"^.*3.*$");
	}

	@DisplayName("AI-9.4")
	@Test
	void should_go_back_to_main_scene() throws InterruptedException {
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
		
		activate();
		
		var firstLabel = mainWindow.label(ViewsId.MAIN_MENU+"[0]");
		var secondLabel = mainWindow.label(ViewsId.MAIN_MENU+"[1]");
		
		firstLabel.requireText(Pattern.compile("Nouvelle partie", Pattern.CASE_INSENSITIVE));
		secondLabel.requireText(Pattern.compile("Quitter", Pattern.CASE_INSENSITIVE));
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

	private void verifyLeftPanel(String...patterns) {
		try {
			var leftPanel = mainWindow.panel("Panneau gauche");
			var labels = new JLabelFixture[] { 
					// Je passe par un panel, car le nom identifie le panneau,
					// pas le label (à corriger dans la lib)
					leftPanel.panel("Panneau gauche[title]").label(),
					leftPanel.label("Panneau gauche[0]"), 
					leftPanel.label("Panneau gauche[1]"),
					leftPanel.label("Panneau gauche[2]"), 
					leftPanel.label("Panneau gauche[3]")
					};
			
			for (int i = 0; i < labels.length; ++i) {
				labels[i].requireText(Pattern.compile(patterns[i], Pattern.CASE_INSENSITIVE));
			}
		}  catch (ComponentLookupException ex) {
			throw new AssertionError("error while checking labels : "+ex.getMessage(), ex);
		}
		
	}
	
	private void verifyRightPanel(String...patterns) {
		try {
			var leftPanel = mainWindow.panel("Panneau droit");
			var labels = new JLabelFixture[] { 
					leftPanel.panel("Panneau droit[title]").label(),
					leftPanel.label("Panneau droit[0]"), 
					leftPanel.label("Panneau droit[1]"),
					leftPanel.label("Panneau droit[2]"), 
					leftPanel.label("Panneau droit[3]")
					};
			
			for (int i = 0; i < labels.length; ++i) {
				labels[i].requireText(Pattern.compile(patterns[i], Pattern.CASE_INSENSITIVE));
			}
		}  catch (ComponentLookupException ex) {
			throw new AssertionError("error while checking labels : "+ex.getMessage(), ex);
		}
		
	}

	private void verifyBottomPanel(String...patterns) {
		try {
			var leftPanel = mainWindow.panel("Panneau de dessous");
			var labels = new JLabelFixture[] { 
					leftPanel.panel("Panneau de dessous[title]").label(), 
					leftPanel.label("Panneau de dessous[0]"),
					leftPanel.label("Panneau de dessous[1]"), 
					leftPanel.label("Panneau de dessous[2]")};
			
			for (int i = 0; i < labels.length; ++i) {
				labels[i].requireText(Pattern.compile(patterns[i], Pattern.CASE_INSENSITIVE));
			}
		}  catch (ComponentLookupException ex) {
			throw new AssertionError("error while checking labels : "+ex.getMessage(), ex);
		}
		
	}
	
}
