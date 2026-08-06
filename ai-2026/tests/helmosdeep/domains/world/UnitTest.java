package helmosdeep.domains.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UnitTest {

	private Unit unit;

	@BeforeEach
	void setUp() {
		unit = new Unit("Aragorn", UnitType.GENERAL, new StandardLightMovementStrategy());
	}

	@Test
	void should_throw_exception_when_name_is_null() {
		assertThrows(NullPointerException.class,
				() -> new Unit(null, UnitType.AVERAGE, new StandardLightMovementStrategy()));
	}

	@Test
	void should_throw_exception_when_type_is_null() {
		assertThrows(NullPointerException.class, () -> new Unit("Boromir", null, new StandardLightMovementStrategy()));
	}

	@Test
	void should_create_unit_with_no_power_and_not_moved_when_instantiated() {
		var newUnit = new Unit("Legolas", UnitType.LIGHT, new StandardLightMovementStrategy());

		assertEquals(0, newUnit.getPow());
		assertTrue(newUnit.isAllowedToMove());
	}

	@Test
	void should_return_true_when_type_matches() {
		assertTrue(unit.hasType(UnitType.GENERAL));
	}

	@Test
	void should_return_false_when_type_does_not_match() {
		assertFalse(unit.hasType(UnitType.LIGHT));
	}

	@Test
	void should_return_name_when_called() {
		assertEquals("Aragorn", unit.getName());
	}

	@Test
	void should_return_type_strength_when_called() {
		var lightUnit = new Unit("Wargs", UnitType.LIGHT, new StandardLightMovementStrategy());

		assertEquals(1, lightUnit.getStr());
	}

	@Test
	void should_return_type_movement_when_called() {
		var lightUnit = new Unit("Wargs", UnitType.LIGHT, new StandardLightMovementStrategy());

		assertEquals(4, lightUnit.getMvt());
	}

	@Test
	void should_return_true_when_unit_has_not_moved() {
		assertTrue(unit.isAllowedToMove());
	}

	@Test
	void should_return_false_when_unit_has_moved() {
		unit.setMoved();

		assertFalse(unit.isAllowedToMove());
	}

	@Test
	void should_be_allowed_to_attack_when_unit_is_general_and_power_has_not_been_set_yet() {
		var averageUnit = new Unit("Gondoriens", UnitType.AVERAGE, new StandardLightMovementStrategy());

		assertTrue(averageUnit.isAllowedToAttack());
	}

	@Test
	void should_not_be_allowed_to_attack_when_power_has_been_set_or_unit_is_general() {
		var averageUnit = new Unit("Gondoriens", UnitType.AVERAGE, new StandardLightMovementStrategy());
		averageUnit.setPower(2);

		assertFalse(averageUnit.isAllowedToAttack());
		assertFalse(unit.isAllowedToAttack());
	}

	@Test
	void should_prevent_further_movement_when_called() {
		unit.setMoved();

		assertFalse(unit.isAllowedToMove());
	}

	@Test
	void should_update_power_when_new_power_is_positive() {
		unit.setPower(5);

		assertEquals(5, unit.getPow());
	}

	@Test
	void should_throw_exception_when_new_power_is_zero() {
		assertThrows(IllegalArgumentException.class, () -> unit.setPower(0));
	}

	@Test
	void should_throw_exception_when_new_power_is_negative() {
		assertThrows(IllegalArgumentException.class, () -> unit.setPower(-1));
	}

	@Test
	void should_reset_moved_flag_when_called() {
		unit.setMoved();

		unit.resetState();

		assertTrue(unit.isAllowedToMove());
	}

	@Test
	void should_reset_power_to_zero_when_called() {
		var averageUnit = new Unit("Gondoriens", UnitType.AVERAGE, new StandardLightMovementStrategy());
		averageUnit.setPower(3);

		averageUnit.resetState();

		assertEquals(0, averageUnit.getPow());
	}
}
