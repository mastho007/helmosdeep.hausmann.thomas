package helmosdeep.domains.world;

import static helmosdeep.domains.world.Coordinate.coord;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Valide la classe ArmyList
 */
class ArmyListTest {

	private Army mordor;
	private Army mankind;
	private ArmyList armyList;

	@BeforeEach
	void setUp() {
		mordor = ArmiesFactory.createMordor();
		mankind = ArmiesFactory.createMankind();
		armyList = new ArmyList(mordor, mankind);
	}

	@Test
	void should_throw_exception_when_created_with_null_args() {
		assertThrows(NullPointerException.class, () -> new ArmyList((Army[]) null));
		assertThrows(NullPointerException.class, () -> new ArmyList(mordor, null));
	}

	@Test
	void should_create_empty_list_when_no_armies_given() {
		var emptyList = new ArmyList();

		assertEquals(0, emptyList.size());
	}

	@Test
	void should_return_number_of_armies_when_size_called() {
		assertEquals(2, armyList.size());
	}

	@Test
	void should_return_army_at_index_when_get_called() {
		assertSame(mordor, armyList.get(0));
		assertSame(mankind, armyList.get(1));
	}

	@Test
	void should_throw_illegal_argument_exception_when_get_called_with_invalid_index() {
		assertThrows(IllegalArgumentException.class, () -> armyList.get(2));
	}

	@Test
	void should_iterate_over_all_armies_when_iterated() {
		var seen = new ArrayList<Army>();

		for (var army : armyList) {
			seen.add(army);
		}

		assertEquals(List.of(mordor, mankind), seen);
	}

	@Test
	void should_return_unit_when_getUnitAt_called_with_position_occupied_in_first_army() {
		var unit = armyList.getUnitAt(coord(0, 0));

		assertTrue(unit.isPresent());
		assertEquals("Sauron", unit.get().getName());
	}

	@Test
	void should_return_unit_when_getUnitAt_called_with_position_occupied_in_second_army() {
		var unit = armyList.getUnitAt(coord(2, 2));

		assertTrue(unit.isPresent());
		assertEquals("Aragorn", unit.get().getName());
	}

	@Test
	void should_return_empty_when_getUnitAt_called_with_unoccupied_position() {
		var unit = armyList.getUnitAt(coord(9, 9));

		assertFalse(unit.isPresent());
	}

	@Test
	void should_throw_NullPointerException_when_put_called_with_null_position() {
		assertThrows(NullPointerException.class,
				() -> armyList.enroll(null, new Unit("Legolas", UnitType.LIGHT), Belligerent.MANKIND));
	}

	@Test
	void should_throw_NullPointerException_when_put_called_with_null_unit() {
		assertThrows(NullPointerException.class, () -> armyList.enroll(coord(3, 3), null, Belligerent.MANKIND));
	}

	@Test
	void should_throw_NullPointerException_when_put_called_with_null_belligerent() {
		assertThrows(NullPointerException.class,
				() -> armyList.enroll(coord(3, 3), new Unit("Legolas", UnitType.LIGHT), null));
	}

	@Test
	void should_position_unit_in_matching_army_when_put_called_on_free_position() {
		var legolas = new Unit("Legolas", UnitType.LIGHT);

		armyList.enroll(coord(3, 3), legolas, Belligerent.MANKIND);

		var unitAtPos = armyList.getUnitAt(coord(3, 3));
		assertTrue(unitAtPos.isPresent());
		assertSame(legolas, unitAtPos.get());
	}

	@Test
	void should_not_position_unit_in_wrong_army_when_put_called() {
		var legolas = new Unit("Legolas", UnitType.LIGHT);

		armyList.enroll(coord(3, 3), legolas, Belligerent.MANKIND);

		assertTrue(mordor.getUnitAt(coord(3, 3)).isEmpty());
		assertTrue(mankind.getUnitAt(coord(3, 3)).isPresent());
	}

	@Test
	void should_throw_IllegalArgumentException_when_put_called_on_position_already_occupied_in_same_army() {
		assertThrows(IllegalArgumentException.class,
				() -> armyList.enroll(coord(0, 0), new Unit("Legolas", UnitType.LIGHT), Belligerent.MORDOR));
	}

	@Test
	void should_throw_IllegalArgumentException_when_put_called_on_position_already_occupied_in_other_army() {
		assertThrows(IllegalArgumentException.class,
				() -> armyList.enroll(coord(2, 2), new Unit("Legolas", UnitType.LIGHT), Belligerent.MORDOR));
	}

	@Test
	void should_throw_IllegalArgumentException_when_put_called_with_belligerent_absent_from_list() {
		var soloList = new ArmyList(mordor);

		assertThrows(IllegalArgumentException.class,
				() -> soloList.enroll(coord(3, 3), new Unit("Legolas", UnitType.LIGHT), Belligerent.MANKIND));
	}

	@Test
	void should_throw_NullPointerException_when_applyToEachUnit_called_with_null_consumer() {
		assertThrows(NullPointerException.class, () -> armyList.applyToEachUnit(null));
	}

	@Test
	void should_apply_consumer_to_every_unit_of_every_army_when_applyToEachUnit_called() {
		var visitedNames = new ArrayList<String>();

		armyList.applyToEachUnit((pos, unit) -> visitedNames.add(unit.getName()));

		assertEquals(5, visitedNames.size());
		assertTrue(visitedNames.containsAll(List.of("Sauron", "Orcs", "Wargs", "Aragorn", "Gondoriens")));
	}

}