package helmosdeep.domains.turns;

import static org.junit.jupiter.api.Assertions.*;

import static helmosdeep.domains.world.Coordinate.coord;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import helmosdeep.domains.world.ArmiesFactory;
import helmosdeep.domains.world.Army;
import helmosdeep.domains.world.Coordinate;

class TurnTest {
	private static List<Coordinate> coords = List.of(
		coord(0,0),coord(0,1),coord(0,2),
		coord(1,0),coord(1,1),coord(1,2),
		coord(2,0),coord(2,1),coord(2,2)
	);
	private Army active;
	private Army other;
 
	@BeforeEach
	void setUp() {
		active = ArmiesFactory.createMordor();
		other = ArmiesFactory.createMankind();
	}
	
	@Test
	void should_provide_a_zero_id_when_none_is_provided() {
		var turn = new Turn(active, other);
		var otherTurn = new Turn(1, other, active);
 
		assertEquals(0, turn.getId());
		assertEquals(1, otherTurn.getId());
	}

	@Test
	void should_reject_null_arg_on_creation() {
		assertThrows(NullPointerException.class, () -> new Turn(null, other));
		assertThrows(NullPointerException.class, () -> new Turn(active, null));
	}

	@Test
	void should_return_zero_ennemies_killed_when_turn_not_yet_terminated() {
		var turn = new Turn(active, other);
 
		assertEquals(0, turn.getEnnemiesKilled());
	}

	@Test
	void should_return_zero_influence_zone_size_when_turn_not_yet_terminated() {
		var turn = new Turn(active, other);
 
		assertEquals(0, turn.getInfluenceZoneSize());
	}

	@Test
	void should_return_true_when_isActive_called_with_active_army() {
		var turn = new Turn(active, other);
 
		assertTrue(turn.isActive(active));
		assertFalse(turn.isActive(other));
	}
	
	@Test
	void should_return_true_when_is_deadlier_than_called_with_empty_optional() {
		var turn = new Turn(active, other);
 
		assertTrue(turn.isDeadlierThan(Optional.<Turn>empty()));
	}

	@Test
	void should_computes_stats_when_turn_is_terminated() {
		var turn = new Turn(active, other);
		turn.terminate(coords);
		
		var otherTurn = new Turn(1, other, active);
		active.removeAt(coord(1,0));
		otherTurn.terminate(coords);

		assertEquals(0, turn.getEnnemiesKilled());
		assertEquals(4, turn.getInfluenceZoneSize());
		
		assertEquals(1, otherTurn.getEnnemiesKilled());
		assertEquals(3, otherTurn.getInfluenceZoneSize());
	}
	
	@Test
	void should_return_true_when_is_deadlier_than_called_with_turn_having_fewer_kills() {
		var turn = new Turn(active, other);
		turn.terminate(coords);
		
		var otherTurn = new Turn(1, other, active);
		active.removeAt(coord(1,0));
		otherTurn.terminate(coords);
		
		assertTrue(otherTurn.isDeadlierThan(Optional.of(turn)));
		assertFalse(otherTurn.isDeadlierThan(Optional.of(otherTurn)));
		assertFalse(turn.isDeadlierThan(Optional.of(otherTurn)));
	}
	
	@Test
	void should_return_true_when_has_more_influence_called() {
		var turn = new Turn(active, other);
		turn.terminate(coords);
		
		var otherTurn = new Turn(1, other, active);
		active.removeAt(coord(1,0));
		otherTurn.terminate(coords);
		
		assertTrue(turn.hasMoreInfluenceThan(Optional.of(otherTurn)));
		assertFalse(otherTurn.hasMoreInfluenceThan(Optional.of(turn)));
	}
	
	@Test
	void should_return_true_when_has_more_influence_called_with_empty_optional() {
		var turn = new Turn(active, other);
 
		assertTrue(turn.hasMoreInfluenceThan(Optional.<Turn>empty()));
	}
	
}
