package helmosdeep.domains.world;

import static org.junit.jupiter.api.Assertions.*;

import static helmosdeep.domains.world.Coordinate.coord;

import java.util.*;

import org.junit.jupiter.api.Test;
/**
 * Valide la classe Army
 * */
class ArmyTest {
	@Test
	void should_instantiate_unable_to_fight_army() {
		var emptyArmy = Army.of(Belligerent.MORDOR);
		assertFalse(emptyArmy.canFight());
	}
	
	@Test
	void should_be_named_after_the_belligerent() {
		var emptyArmy = Army.of(Belligerent.MANKIND);
		
		assertTrue(emptyArmy.belongsTo(Belligerent.MANKIND));
		assertFalse(emptyArmy.belongsTo(Belligerent.MORDOR));
		assertEquals(Belligerent.MANKIND.toString(), emptyArmy.resolve(":name:"));
	}
	
	@Test
	void should_allow_to_fight_when_there_are_a_general_and_another_unit() {
		var army = Army.of(Belligerent.MORDOR);
		
		army.enroll(coord(0,0), new Unit("S", UnitType.GENERAL));
		army.enroll(coord(1,0), new Unit("G", UnitType.LIGHT));
		
		assertTrue(army.canFight());
	}
	
	@Test
	void should_disallow_to_fight_when_you_have_only_a_general() {
		var army = Army.of(Belligerent.MORDOR);
		
		army.enroll(coord(0,0), new Unit("S", UnitType.GENERAL));
		
		assertFalse(army.canFight());
	}
	@Test
	void should_disallow_to_fight_when_you_have_only_a_unit() {
		var army = Army.of(Belligerent.MORDOR);
		
		army.enroll(coord(0,0), new Unit("G", UnitType.AVERAGE));
		
		assertFalse(army.canFight());
	}
	
	@Test
	void should_empeach_enrolling_on_duplication() {
		var army = Army.of(Belligerent.MORDOR);
		var general = new Unit("S", UnitType.GENERAL);
		var lieutenant  = new Unit("L", UnitType.LIGHT);
		army.enroll(coord(0,0), general);
		army.enroll(coord(1,1), lieutenant);
		
		assertThrows(IllegalArgumentException.class, () -> army.enroll(coord(0, 0), new Unit("G", UnitType.HEAVY)));
		assertThrows(IllegalArgumentException.class, () -> army.enroll(coord(1, 0), general));
		assertThrows(IllegalArgumentException.class, () -> army.enroll(coord(1, 0),  new Unit("S²", UnitType.GENERAL)));
		assertThrows(IllegalArgumentException.class, () -> army.enroll(coord(1, 0),  lieutenant));
	}
	
	@Test
	void should_return_an_unit_from_a_coordinate() {
		var army = Army.of(Belligerent.MORDOR);
		var general = new Unit("S", UnitType.GENERAL);
		var lieutenant  = new Unit("L", UnitType.LIGHT);
		army.enroll(coord(0,0), general);
		army.enroll(coord(1,1), lieutenant);
		
		assertTrue(army.getUnitAt(coord(0, 0)).isPresent());
		assertTrue(army.getUnitAt(coord(1, 1)).isPresent());
		assertFalse(army.getUnitAt(coord(1, 0)).isPresent());
		assertFalse(army.getUnitAt(coord(0, 1)).isPresent());
	}
	
	@Test
	void should_provide_a_mvt_capability() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		
		assertEquals("5", mordor.resolve(":mvt:"));
		assertEquals("3", mankind.resolve(":mvt:"));
		
		assertEquals(3, mankind.getMvtForUnitAt(coord(2,2)));
		assertEquals(3, mordor.getMvtForUnitAt(coord(0,0)));
	}
	
	@Test
	void should_clamp_a_unit_mvt_to_the_army_mvt() {
		var mordor = Army.of(Belligerent.MORDOR);
		mordor.enroll(coord(0,0), new Unit("Sauron", UnitType.GENERAL));
		
		assertEquals("1", mordor.resolve(":mvt:"));
		assertEquals(1, mordor.getMvtForUnitAt(coord(0,0)));
	}
	
	@Test
	void should_locate_the_general() {
		var mankind = ArmiesFactory.createMankind();
		var emptyArmy = Army.of(Belligerent.MORDOR);
		
		assertEquals(coord(2,2), mankind.locateGeneral());
		assertEquals(Coordinate.NONE, emptyArmy.locateGeneral());
	}
	
	@Test
	void should_detect_allies_close_to_a_coordinate() {
		var mankind = ArmiesFactory.createMankind();
		
		// Les positions suivantes sont à une distance de 1 du gondoriens
		assertTrue(mankind.hasUnitCloseTo(coord(0,2)));
		assertTrue(mankind.hasUnitCloseTo(coord(3,2)));
		
		assertFalse(mankind.hasUnitCloseTo(coord(0,0)));
		assertFalse(mankind.hasUnitCloseTo(coord(2,0)));
	}

	@Test
	void should_move_unit_and_consume_mvt() {
		var mordor = ArmiesFactory.createMordor();
		var newPos = coord(5,5);

		mordor.move(coord(0,0), newPos, 2);

		assertTrue(mordor.getUnitAt(coord(0,0)).isEmpty());
		assertTrue(mordor.getUnitAt(newPos).isPresent());
		assertEquals("3", mordor.resolve(":mvt:"));
	}

	@Test
	void should_mark_unit_as_unable_to_move_again_when_moved() {
		var mordor = ArmiesFactory.createMordor();
		var newPos = coord(5,5);

		mordor.move(coord(0,0), newPos, 1);

		assertFalse(mordor.getUnitAt(newPos).get().isAllowedToMove());
	}

	@Test
	void should_throw_exception_when_moving_from_a_position_without_unit() {
		var mordor = ArmiesFactory.createMordor();

		assertThrows(IllegalArgumentException.class, () -> mordor.move(coord(9,9), coord(8,8), 1));
	}

	@Test
	void should_throw_exception_when_mvt_consumed_is_out_of_bounds() {
		var mordor = ArmiesFactory.createMordor();

		assertThrows(IllegalArgumentException.class, () -> mordor.move(coord(0,0), coord(5,5), 0));
		assertThrows(IllegalArgumentException.class, () -> mordor.move(coord(0,0), coord(5,5), 4));
	}

	@Test
	void should_refill_mvt_pool_on_reset() {
		var mordor = ArmiesFactory.createMordor();
		mordor.move(coord(0,0), coord(5,5), 2);

		mordor.reset();

		assertEquals("5", mordor.resolve(":mvt:"));
	}

	@Test
	void should_allow_unit_to_move_again_after_reset() {
		var mordor = ArmiesFactory.createMordor();
		var newPos = coord(5,5);
		mordor.move(coord(0,0), newPos, 1);

		mordor.reset();

		assertTrue(mordor.getUnitAt(newPos).get().isAllowedToMove());
	}

	@Test
	void should_reset_unit_power_to_zero_on_reset() {
		var mordor = ArmiesFactory.createMordor();
		mordor.computePowerAt(coord(1,0), 1);

		mordor.reset();

		assertEquals(0, mordor.getUnitAt(coord(1,0)).get().getPow());
	}

	@Test
	void should_compute_power_including_str_allies_and_term() {
		var mordor = ArmiesFactory.createMordor();

		mordor.computePowerAt(coord(0,0), 1);

		assertEquals(4 + 2 + 1, mordor.getUnitAt(coord(0,0)).get().getPow());
	}

	@Test
	void should_compute_power_with_no_allies_nearby() {
		var army = Army.of(Belligerent.MORDOR);
		army.enroll(coord(0,0), new Unit("Solo", UnitType.LIGHT));

		army.computePowerAt(coord(0,0), 2);

		assertEquals(1 + 0 + 2, army.getUnitAt(coord(0,0)).get().getPow());
	}

	@Test
	void should_throw_exception_when_computing_power_on_position_without_unit() {
		var army = Army.of(Belligerent.MORDOR);

		assertThrows(IllegalArgumentException.class, () -> army.computePowerAt(coord(0,0), 1));
	}

	@Test
	void should_remove_unit_at_given_position() {
		var mordor = ArmiesFactory.createMordor();

		mordor.removeAt(coord(1,0));

		assertTrue(mordor.getUnitAt(coord(1,0)).isEmpty());
		assertEquals(2, mordor.getUnitsAlive());
	}

	@Test
	void should_clear_general_when_general_is_removed() {
		var mordor = ArmiesFactory.createMordor();

		mordor.removeAt(coord(0,0));

		assertEquals(Coordinate.NONE, mordor.locateGeneral());
		assertFalse(mordor.canFight());
	}

	@Test
	void should_not_affect_general_when_removing_another_unit() {
		var mordor = ArmiesFactory.createMordor();

		mordor.removeAt(coord(1,0));

		assertEquals(coord(0,0), mordor.locateGeneral());
	}

	@Test
	void should_do_nothing_when_removing_at_position_without_unit() {
		var mordor = ArmiesFactory.createMordor();

		mordor.removeAt(coord(9,9));

		assertEquals(3, mordor.getUnitsAlive());
	}
	
	@Test
	void should_accept_an_army_consumer() {
		var mordor = ArmiesFactory.createMordor();
		var unitsNames  = new ArrayList<String>();
		
		mordor.applyToEachUnit((Coordinate coord, Unit unit) -> unitsNames.add(unit.getName()));
		
		unitsNames.sort((s1, s2) -> s1.compareTo(s2));
		
		assertIterableEquals(List.of("Orcs", "Sauron","Wargs"), unitsNames);
	}
}