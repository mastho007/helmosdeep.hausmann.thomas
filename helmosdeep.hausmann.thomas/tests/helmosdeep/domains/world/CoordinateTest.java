package helmosdeep.domains.world;

import static helmosdeep.domains.world.Coordinate.coord;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;

class CoordinateTest {

	@Test
	void should_generate_neighbors_for_even_column() {
		var neighbors = Coordinate
				.coord(1, 2)
				.getNeihgbors();
		var expected =  Set.of(
				coord(0,1),coord(2,1),
				coord(1,0),coord(1,2),
				coord(2,2), coord(2,0)
				);

		assertEquals(expected.size(), neighbors.size());
	}

	@Test
	void should_generate_neighbors_for_odd_column() {
		var neighbors = Coordinate
				.coord(1, 1)
				.getNeihgbors();
		var expected =  Set.of(
				coord(0,1),coord(2,1),
				coord(1,0),coord(1,2),
				coord(2,2), coord(2,0)
				);

		assertEquals(expected.size(), neighbors.size());
	}

	@Test
	void should_contain_exact_neighbors_when_column_is_even() {
		var neighbors = coord(1, 2).getNeihgbors();
		var expected = Set.of(
				coord(0, 2), coord(0, 3), coord(1, 3),
				coord(2, 2), coord(1, 1), coord(0, 1)
				);

		assertEquals(expected, neighbors);
	}

	@Test
	void should_contain_exact_neighbors_when_column_is_odd() {
		var neighbors = coord(1, 1).getNeihgbors();
		var expected = Set.of(
				coord(0, 1), coord(1, 2), coord(2, 2),
				coord(2, 1), coord(2, 0), coord(1, 0)
				);

		assertEquals(expected, neighbors);
	}

	@Test
	void should_return_same_instance_when_coord_called_with_same_values() {
		var first = coord(3, 4);
		var second = coord(3, 4);

		assertSame(first, second);
	}

	@Test
	void should_return_different_instances_when_coord_called_with_different_values() {
		var first = coord(3, 4);
		var second = coord(4, 3);

		assertNotSame(first, second);
	}

	@Test
	void should_be_equal_when_row_and_col_are_the_same() {
		var first = coord(5, 6);
		var second = coord(5, 6);

		assertEquals(first, second);
	}

	@Test
	void should_not_be_equal_when_row_or_col_differ() {
		var first = coord(5, 6);
		var second = coord(6, 5);

		assertNotEquals(first, second);
	}

	@Test
	void should_not_be_equal_when_compared_to_null() {
		var coordinate = coord(1, 1);

		assertNotEquals(null, coordinate);
	}

	@Test
	void should_not_be_equal_when_compared_to_different_type() {
		var coordinate = coord(1, 1);

		assertNotEquals("not a coordinate", coordinate);
	}

	@Test
	void should_have_same_hashcode_when_row_and_col_are_the_same() {
		var first = coord(7, 8);
		var second = coord(7, 8);

		assertEquals(first.hashCode(), second.hashCode());
	}

	@Test
	void should_return_row_when_getRow_is_called() {
		var coordinate = coord(3, 9);

		assertEquals(3, coordinate.getRow());
	}

	@Test
	void should_return_col_when_getCol_is_called() {
		var coordinate = coord(3, 9);

		assertEquals(9, coordinate.getCol());
	}

	@Test
	void should_return_col_as_q_when_getQ_is_called() {
		var coordinate = coord(3, 9);

		assertEquals(coordinate.getCol(), coordinate.getQ());
	}

	@Test
	void should_compute_r_when_col_is_even() {
		var coordinate = coord(1, 2);

		assertEquals(0, coordinate.getR());
	}

	@Test
	void should_compute_r_when_col_is_odd() {
		var coordinate = coord(1, 1);

		assertEquals(1, coordinate.getR());
	}

	@Test
	void should_compute_r_when_col_is_negative() {
		var coordinate = coord(2, -3);

		assertEquals(4, coordinate.getR());
	}

	@Test
	void should_format_as_col_row_when_toString_is_called() {
		var coordinate = coord(3, 5);

		assertEquals("(05, 03)", coordinate.toString());
	}

	@Test
	void should_return_zero_distance_when_comparing_coordinate_to_itself() {
		var coordinate = coord(2, 2);

		assertEquals(0.0, coordinate.distanceFrom(coordinate));
	}

	@Test
	void should_return_positive_distance_when_coordinates_differ() {
		var origin = coord(0, 0);
		var target = coord(3, 3);

		assertTrue(origin.distanceFrom(target) > 0);
	}

	@Test
	void should_return_symmetric_distance_when_comparing_two_coordinates() {
		var first = coord(0, 0);
		var second = coord(2, 3);

		assertEquals(first.distanceFrom(second), second.distanceFrom(first));
	}

	@Test
	void should_return_shifted_coordinate_when_move_is_called() {
		var coordinate = coord(2, 2);

		var moved = coordinate.move(1, -1);

		assertEquals(coord(3, 1), moved);
	}

	@Test
	void should_return_cached_instance_when_move_reaches_existing_coordinate() {
		var coordinate = coord(2, 2);

		var moved = coordinate.move(0, 0);

		assertSame(coordinate, moved);
	}

	@Test
	void should_have_extreme_values_when_accessing_NONE_constant() {
		assertEquals(Integer.MIN_VALUE, Coordinate.NONE.getRow());
		assertEquals(Integer.MIN_VALUE, Coordinate.NONE.getCol());
	}
}