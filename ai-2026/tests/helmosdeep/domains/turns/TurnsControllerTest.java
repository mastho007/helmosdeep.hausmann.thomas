package helmosdeep.domains.turns;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import static helmosdeep.domains.world.Coordinate.coord;

import org.junit.jupiter.api.Test;

import helmosdeep.domains.world.ArmiesFactory;
import helmosdeep.domains.world.Army;
import helmosdeep.domains.world.ArmyList;
import helmosdeep.domains.world.Coordinate;
import helmosdeep.domains.world.Unit;

/**
 * Valide la classe TurnController.
 * */
class TurnsControllerTest {

	private static List<Coordinate> coords = List.of(
			coord(0,0),coord(0,1),coord(0,2),
			coord(1,0),coord(1,1),coord(1,2),
			coord(2,0),coord(2,1),coord(2,2)
		);
	
	@Test
	void should_pick_mordor_as_first_active_army() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var turnController = TurnsController.create(new ArmyList(mordor, mankind));
		
		assertEquals(mordor, turnController.getActiveArmy());
		assertEquals(mankind, turnController.getOtherArmy());
		assertEquals(coord(0,0), turnController.getCurrentPosition());
	}
	
	@Test
	void should_rejects_armies_passed_in_wrong_order() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		
		assertThrows(IllegalArgumentException.class, () -> TurnsController.create(new ArmyList(mankind, mordor)));
		assertThrows(IllegalArgumentException.class, () -> TurnsController.create(new ArmyList(mordor)));
		assertThrows(IllegalArgumentException.class, () -> TurnsController.create(new ArmyList(mankind)));
		
		assertThrows(NullPointerException.class, () -> TurnsController.create(null));
	}
	
	
	@Test
	void should_keep_selected_unit_position_when_changing_current_position() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var turnController = TurnsController.create(new ArmyList(mordor, mankind));
		
		turnController.setCurrentPosition(Coordinate.coord(2, 2));
		
		assertEquals(coord(0,0), turnController.getSelectedPosition());
	}
	
	@Test
	void should_select_a_new_unit_when_current_position_is_occupied_by_a_unit() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var turnController = TurnsController.create(new ArmyList(mordor, mankind));
		
		turnController.setCurrentPosition(Coordinate.coord(1, 0));
		turnController.selectUnit();
		
		assertEquals(coord(1,0), turnController.getCurrentPosition());
		assertEquals(coord(1,0), turnController.getSelectedPosition());
	}
	
	@Test
	void should_ignore_selection_command_when_no_ally_occupies_the_current_position() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var turnController = TurnsController.create(new ArmyList(mordor, mankind));
		
		turnController.setCurrentPosition(Coordinate.coord(0, 2));
		turnController.selectUnit();
		
		assertEquals(coord(0,2), turnController.getCurrentPosition());
		assertEquals(coord(0,0), turnController.getSelectedPosition());
		
		turnController.setCurrentPosition(Coordinate.coord(2, 2));
		turnController.selectUnit();
		
		assertEquals(coord(2,2), turnController.getCurrentPosition());
		assertEquals(coord(0,0), turnController.getSelectedPosition());
		
	}
		
	@Test
	void should_move_a_unit_when_current_position_is_free() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var turnController = TurnsController.create(new ArmyList(mordor, mankind));
		
		turnController.setCurrentPosition(Coordinate.coord(0, 2));
		turnController.moveActiveUnit(1);
		
		assertEquals(coord(0,2), turnController.getCurrentPosition());
		assertEquals(coord(0,2), turnController.getSelectedPosition());
	}
	
	@Test
	void should_ignore_unit_movement_on_occupied_position() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var turnController = TurnsController.create(new ArmyList(mordor, mankind));
		
		turnController.setCurrentPosition(Coordinate.coord(1, 0));
		turnController.moveActiveUnit(1);
		
		assertEquals(coord(1,0), turnController.getCurrentPosition());
		assertEquals(coord(0,0), turnController.getSelectedPosition());
	}
	
	@Test
	void should_update_selected_position_when_removing_active_unit() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var turnController = TurnsController.create(new ArmyList(mordor, mankind));
		
		turnController.setCurrentPosition(Coordinate.coord(1,0));
		turnController.selectUnit();		
		turnController.removeAt(turnController.getSelectedPosition());		
		
		assertEquals(coord(1,0), turnController.getCurrentPosition());
		assertEquals(coord(0,0), turnController.getSelectedPosition());		
	}
	
	@Test
	void should_keep_selected_position_when_removing_other_army_unit() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var turnController = TurnsController.create(new ArmyList(mordor, mankind));
		
		turnController.setCurrentPosition(Coordinate.coord(1,0));
		turnController.selectUnit();	
		turnController.setCurrentPosition(Coordinate.coord(2,2));
		turnController.removeAt(turnController.getCurrentPosition());		
		
		assertEquals(coord(2,2), turnController.getCurrentPosition());
		assertEquals(coord(1,0), turnController.getSelectedPosition());		
	}
	
	@Test
	void should_ignore_remove_command_when_no_unit_occupies_pos() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var turnController = TurnsController.create(new ArmyList(mordor, mankind));
	
		turnController.setCurrentPosition(Coordinate.coord(0,2));
		turnController.removeAt(turnController.getCurrentPosition());		
		
		var units = new ArrayList<Unit>(6);
		turnController.applyToEachUnit((p, u) -> units.add(u));
		
		assertEquals(5, units.size());
	}
	
	@Test
	void should_change_of_army_when_starting_a_new_turn() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var turnController = TurnsController.create(new ArmyList(mordor, mankind));
		
		turnController.startNewTurn();
		
		assertEquals(mankind, turnController.getActiveArmy());
		assertEquals(3, turnController.getMvtForActiveUnit());
		assertEquals(mordor, turnController.getOtherArmy());
		assertEquals(coord(2,2), turnController.getCurrentPosition());
	}
	
	@Test
	void should_resolve_templated_text() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var turnController = TurnsController.create(new ArmyList(mordor, mankind));
		
		String txt = turnController.resolve(":currentArmy: vs :otherArmy:");
		
		assertEquals(mordor.resolve(":name:") + " vs "+mankind.resolve(":name:"), txt);
	}
	
	@Test
	void should_apply_an_army_and_turn_consumer_to_each_army() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var turnController = TurnsController.create(new ArmyList(mordor, mankind));
		
		var armyConsumed = new ArrayList<String>();
		var turnsCount = new ArrayList<Integer>();
		
		turnController.applyToEachArmy(new ArmyAndTurnConsumer() {
			
			@Override
			public void accept(TurnSequence allTurns) {
				turnsCount.add(allTurns.count());
				
			}
			
			@Override
			public void accept(Army army, TurnSequence armyTurns, int totalTurns) {
				armyConsumed.add(army.resolve(":name:"));
				
			}
		});
		
		assertIterableEquals(List.of("Mordor", "Les Hommes"), armyConsumed);
		assertIterableEquals(List.of(1), turnsCount);
	}
	
	@Test
	void should_end_the_last_active_turn_on_end() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var turnController = TurnsController.create(new ArmyList(mordor, mankind));
		
		turnController.endLastTurn(coords);
		
		turnController.applyToEachArmy(new ArmyAndTurnConsumer() {
			
			@Override
			public void accept(TurnSequence allTurns) {
				assertEquals(1, allTurns.count());
				for(var t : allTurns) {
					assertNotEquals(0, t.getInfluenceZoneSize());
				}
			}
			
			@Override
			public void accept(Army army, TurnSequence armyTurns, int totalTurns) {				
				assertEquals(1, totalTurns);
			}
		});
	}

}
