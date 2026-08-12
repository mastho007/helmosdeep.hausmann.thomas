package helmosdeep.domains.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MvtPoolTest {

	@Test
	void should_create_pool_fully_filled_when_capacity_is_valid() {
		var pool = MvtPool.create(3);

		assertEquals(3, pool.getActual());
	}

	@Test
	void should_create_pool_with_zero_actual_when_capacity_is_zero() {
		var pool = MvtPool.create(0);

		assertEquals(0, pool.getActual());
	}

	@Test
	void should_throw_exception_when_capacity_is_negative() {
		assertThrows(IllegalArgumentException.class, () -> MvtPool.create(-1));
	}

	@Test
	void should_increase_actual_when_capacity_is_raised() {
		var pool = MvtPool.create(3);

		pool.raiseCapacity(2.5f);

		assertEquals(5, pool.getActual());
	}

	@Test
	void should_truncate_actual_when_raised_capacity_is_not_an_integer() {
		var pool = MvtPool.create(3);

		pool.raiseCapacity(0.9f);

		assertEquals(3, pool.getActual());
	}

	@Test
	void should_reset_actual_to_new_capacity_when_raised_even_if_points_were_consumed() {
		var pool = MvtPool.create(3);
		pool.subtract(2);

		pool.raiseCapacity(0.7f);

		assertEquals(3, pool.getActual());
	}

	@Test
	void should_throw_exception_when_increment_is_zero() {
		var pool = MvtPool.create(3);

		assertThrows(IllegalArgumentException.class, () -> pool.raiseCapacity(0));
	}

	@Test
	void should_throw_exception_when_increment_is_negative() {
		var pool = MvtPool.create(3);

		assertThrows(IllegalArgumentException.class, () -> pool.raiseCapacity(-1f));
	}

	@Test
	void should_decrease_actual_when_points_are_subtracted() {
		var pool = MvtPool.create(5);

		pool.subtract(2);

		assertEquals(3, pool.getActual());
	}

	@Test
	void should_allow_subtracting_all_available_points_when_consumed_equals_actual() {
		var pool = MvtPool.create(4);

		pool.subtract(4);

		assertEquals(0, pool.getActual());
	}

	@Test
	void should_throw_exception_when_consumed_exceeds_actual() {
		var pool = MvtPool.create(2);

		assertThrows(IllegalArgumentException.class, () -> pool.subtract(3));
	}

	@Test
	void should_restore_actual_to_truncated_capacity_when_refilled() {
		var pool = MvtPool.create(3);
		pool.subtract(2);

		pool.refill();

		assertEquals(3, pool.getActual());
	}

	@Test
	void should_not_change_raw_capacity_when_refilled() {
		var pool = MvtPool.create(3);
		pool.raiseCapacity(0.5f);
		pool.subtract(3);

		pool.refill();

		assertEquals(3, pool.getActual());
	}
}