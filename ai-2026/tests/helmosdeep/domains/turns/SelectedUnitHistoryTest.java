package helmosdeep.domains.turns;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import helmosdeep.domains.world.ArmiesFactory;
import helmosdeep.domains.world.Army;
import helmosdeep.domains.world.ArmyList;
import helmosdeep.domains.world.Coordinate;
import helmosdeep.domains.world.StandardHeavyMovementStrategy;
import helmosdeep.domains.world.StandardLightMovementStrategy;
import helmosdeep.domains.world.Unit;
import helmosdeep.domains.world.UnitType;

class SelectedUnitHistoryTest {

	private SelectedUnitHistory selectedUnitHistory;
	private Army mordor;
	private Army mankind;
	
	@BeforeEach
	void setUp() throws Exception {

		this.mordor = ArmiesFactory.createMordor();
		this.mankind = ArmiesFactory.createMankind();
		ArmyList armyList = new ArmyList(mordor, mankind);

		TurnsController turnsController = TurnsController.create(armyList);

		this.selectedUnitHistory = new SelectedUnitHistory(turnsController);
	}

	@Test
	void should_throw_when_turnsController_is_null() {

		assertThrows(NullPointerException.class, () -> new SelectedUnitHistory(null));
	}

	@Test
	void should_push_unit_when_unit_is_valid() {

		Unit unit = new Unit("Wargs", UnitType.LIGHT, new StandardLightMovementStrategy());

		this.selectedUnitHistory.add(unit);

	}

	@Test
	void should_throw_exception_when_adding_unknown_unit() {

		assertThrows(IllegalArgumentException.class, () -> this.selectedUnitHistory.add(Unit.UNKNOWN));

	}

	@Test
	void should_clear_history_and_add_general_when_reset() {

		Unit unitOne = new Unit("Wargs", UnitType.LIGHT, new StandardLightMovementStrategy());

		Unit unitTwo = new Unit("Trolls", UnitType.HEAVY, new StandardHeavyMovementStrategy());

		this.selectedUnitHistory.add(unitOne);
		this.selectedUnitHistory.add(unitTwo);
		this.selectedUnitHistory.reset(new Unit("Sauron", UnitType.GENERAL, new StandardHeavyMovementStrategy()));

		assertTrue(this.selectedUnitHistory.getPreviousValidUnit().getName().equals("Sauron"));

	}

	@Test
	void should_return_previous_unit_when_multiple_valid_units_in_history() {


		this.selectedUnitHistory.add(this.mordor.getUnitAt(Coordinate.coord(1, 0)).get());
		this.selectedUnitHistory.add(this.mordor.getUnitAt(Coordinate.coord(0, 1)).get());

		assertTrue(this.selectedUnitHistory.getPreviousValidUnit().getName().equals("Orcs"));

	}
	
	
	@Test
	void should_return_general_when_only_one_unit_was_added_after_general() {

		this.selectedUnitHistory.reset(new Unit("Sauron", UnitType.GENERAL, new StandardHeavyMovementStrategy()));

		this.selectedUnitHistory.add(this.mordor.getUnitAt(Coordinate.coord(1, 0)).get());

		assertEquals("Sauron", this.selectedUnitHistory.getPreviousValidUnit().getName());

	}
	
	
	@Test
	void should_skip_dead_units_and_return_last_alive_unit() {

		this.selectedUnitHistory.reset(new Unit("Sauron", UnitType.GENERAL, new StandardHeavyMovementStrategy()));

		this.selectedUnitHistory.add(this.mordor.getUnitAt(Coordinate.coord(1, 0)).get());
		
		this.mordor.removeAt((Coordinate.coord(1, 0)));
		
		this.selectedUnitHistory.add(this.mordor.getUnitAt(Coordinate.coord(0, 1)).get());
		
		assertEquals("Sauron", this.selectedUnitHistory.getPreviousValidUnit().getName());

	}
	
	
	
	
	
	

}
