package helmosdeep.domains;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import helmosdeep.domains.actions.ActionListener;
import helmosdeep.domains.turns.TurnListener;
import helmosdeep.domains.world.Coordinate;
import helmosdeep.domains.world.Unit;

/**
 * Valide la classe HelmosDeepGame 
 */
class HelmosDeepGameTest {

	private HelmosDeepGame game;
	private HelmosDeepGameFactory factory;

	@BeforeEach
	void setup() {
		factory = new HelmosDeepGameFactory();
		
		factory.create("level-a", new GameOption());
		
		this.game = factory.getLastGame();
	}
	
	@Test
	void should_allow_to_consume_tiles_and_units() {
		factory.create(new GameOption());
		
		this.game = factory.getLastGame();
		
		var tiles = new ArrayList<>();
		var units = new ArrayList<>();
		
		this.game.applyToEachTile((pos, tile) -> tiles.add(tile));
		this.game.applyToEachUnit((pos, unit) -> units.add(unit));
		
		assertEquals(45, tiles.size());
		assertEquals(22, units.size());
	}
	
	@Test
	void should_make_mordor_the_active_army_and_sauron_the_active_unit() {		
		assertEquals("Mordor (4)", game.resolve(":currentArmy: (:currentArmy.mvt:)"));
		assertEquals("Sauron", game.resolve(":currentArmy.unit:"));
		assertEquals("Plaine[1]", game.resolve(":currentTile:[:currentTileCost:]"));
		assertEquals(Coordinate.coord(0,0), game.getCurrentPosition());
	}

	@Test
	void should_update_current_position_on_move() {
		game.changePosition(1, 0);
		
		assertEquals("Foret[2]", game.resolve(":currentTile:[:currentTileCost:]"));
		assertEquals(Coordinate.coord(1,0), game.getCurrentPosition());

	}
	
	@Test
	void should_ignore_current_position_update_on_invalid_move() {
		game.changePosition(-1, -1);
		
		assertEquals("Plaine[1]", game.resolve(":currentTile:[:currentTileCost:]"));
		assertEquals(Coordinate.coord(0,0), game.getCurrentPosition());

	}
	
	@Test
	void should_notify_move_event__on_valid_selection_command() {
		var actionListener = new DummyActionListener();
		game.changePosition(1, 0);
		game.performAction(actionListener);
		
		assertTrue(actionListener.hasReceivedSelectionEvent());
		assertFalse(actionListener.hasReceivedMoveEvent());	
		assertEquals(Coordinate.coord(1,0), game.getCurrentPosition());
		assertEquals("Wargs", game.resolve(":currentArmy.unit:"));
	}
	
	@Test
	void should_ignore_move_event_on_invalid_movement() {
		var actionListener = new DummyActionListener();
		game.changePosition(2, 2);
		game.performAction(actionListener);
		
		assertFalse(actionListener.hasReceivedSelectionEvent());
		assertFalse(actionListener.hasReceivedMoveEvent());	
		assertEquals(Coordinate.coord(2,2), game.getCurrentPosition());
		assertEquals("Sauron", game.resolve(":currentArmy.unit:"));
	}
	
	@Test
	void should_notify_move_event_on_valid_move_command() {
		var actionListener = new DummyActionListener();
		game.changePosition(1, 0);
		game.performAction(actionListener);
		actionListener.reset();
		game.changePosition(0, 1);
		game.performAction(actionListener);
		
		assertFalse(actionListener.hasReceivedSelectionEvent());
		assertTrue(actionListener.hasReceivedMoveEvent());	
		
		assertEquals("Plaine[1]", game.resolve(":currentTile:[:currentTileCost:]"));
		assertEquals(Coordinate.coord(1,1), game.getCurrentPosition());
		assertEquals("Wargs", game.resolve(":currentArmy.unit:"));
	}
	
	@Test
	void should_notify_on_turn_end() {
		var actionListener = new DummyActionListener();
		var turnListener = new DummyTurnListener();
		
		game.changePosition(0, 1);
		game.performAction(actionListener);
		game.changePosition(1, 0);
		game.performAction(actionListener);
		
		game.changePosition(0, -1);
		game.performAction(actionListener);
		
		game.changePosition(1, 0);
		game.performAction(actionListener);
		
		game.endTurn(turnListener);
		
		assertTrue(turnListener.hasReceivedTurnStartedEvent());
		assertFalse(turnListener.hasReceivedGameEndedEvent());
		assertEquals(Coordinate.coord(2,2), game.getCurrentPosition());
		assertEquals("Aragorn", game.resolve(":currentArmy.unit:"));
	}

	@Test
	void should_notify_when_game_ends() {
		var actionListener = new DummyActionListener();
		var turnListener = new DummyTurnListener();
		
		game.changePosition(0, 1);
		game.performAction(actionListener);
		game.changePosition(1, 0);
		game.performAction(actionListener);
		
		game.changePosition(0, -1);
		game.performAction(actionListener);
		
		game.changePosition(1, 0);
		game.performAction(actionListener);
		
		actionListener.reset();
		game.changePosition(0, 1);
		game.performAction(actionListener);
		
		game.changePosition(-1, 0);
		game.performAction(actionListener);
		actionListener.reset();
		
		game.changePosition(1, 1);
		game.performAction(actionListener);
		
		game.endTurn(turnListener);
		assertFalse(turnListener.hasReceivedTurnStartedEvent());
		assertTrue(turnListener.hasReceivedGameEndedEvent());
	}
	
}

class DummyActionListener implements ActionListener {
	private boolean selected;
	private boolean moved;
	private boolean attackerWonOrdDrawReceived;
	private boolean defenderWonOrDrawReceived;
	
	boolean hasReceivedMoveEvent() {
		return moved;
	}
	
	boolean hasReceivedSelectionEvent() {
		return selected;
	}
	
	boolean hasReceivedAttackerWonEvent() {
		return this.attackerWonOrdDrawReceived && ! this.defenderWonOrDrawReceived;
	}
	
	boolean hasReceivedDefenderWonEvent() {
		return !this.attackerWonOrdDrawReceived && this.defenderWonOrDrawReceived;
	}
	
	boolean hasReceivedDrawAttackEvent() {
		return this.attackerWonOrdDrawReceived && this.defenderWonOrDrawReceived;
	}
	
	void reset() {
		selected = false;
		moved = false;
		attackerWonOrdDrawReceived = false;
		defenderWonOrDrawReceived = false;
	}
	
	@Override
	public void moved(Coordinate from, Coordinate to) {
		this.moved  = true;		
	}

	@Override
	public void unitSelected() {
		this.selected = true;		
	}

	@Override
	public void attackResolved(Unit attacker, Unit defender, float result, Optional<Coordinate> posToRemove) {
		attackerWonOrdDrawReceived = result >= 0;
		defenderWonOrDrawReceived = result <= 0;
		
	}
	
}

class DummyTurnListener implements TurnListener {
	private boolean turnStarted;
	private boolean gameEnded;
	
	void reset() {
		turnStarted = false;
		gameEnded = false;
	}
	
	boolean hasReceivedTurnStartedEvent() {
		return turnStarted;
	}
	
	boolean hasReceivedGameEndedEvent() {
		return gameEnded;
	}
	
	@Override
	public void newTurnStarted() {
		turnStarted  = true;		
	}

	@Override
	public void gameEnded() {
		gameEnded = true;
		
	}
	
}