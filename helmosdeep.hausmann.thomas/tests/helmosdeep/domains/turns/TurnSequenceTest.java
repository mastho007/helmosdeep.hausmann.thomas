package helmosdeep.domains.turns;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.Test;

import helmosdeep.domains.world.*;

class TurnSequenceTest {

	// ---- fromArmies ----

	@Test
	void should_contain_one_turn_when_created_from_armies() {
		var sequence = TurnSequence.fromArmies(ArmiesFactory.createMordor(), ArmiesFactory.createMankind());

		assertEquals(1, sequence.count());
	}

	@Test
	void should_set_first_turn_id_to_one_when_created_from_armies() {
		var sequence = TurnSequence.fromArmies(ArmiesFactory.createMordor(), ArmiesFactory.createMankind());

		var firstTurn = sequence.iterator().next();
		assertEquals(1, firstTurn.getId());
	}

	@Test
	void should_throw_NPE_when_active_army_is_null_on_fromArmies() {
		var other = ArmiesFactory.createMankind();
		var active = ArmiesFactory.createMordor();
		
		assertThrows(NullPointerException.class, () -> TurnSequence.fromArmies(null, other));		
		assertThrows(NullPointerException.class, () -> TurnSequence.fromArmies(active, null));
	}

	// ---- startNewTurn ----

	@Test
	void should_increase_count_when_new_turn_started() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var sequence = TurnSequence.fromArmies(mordor, mankind);

		sequence.startNewTurn(mankind, mordor);

		assertEquals(2, sequence.count());
	}

	@Test
	void should_assign_incrementing_id_when_new_turn_started() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var sequence = TurnSequence.fromArmies(mordor, mankind);

		sequence.startNewTurn(mankind, mordor);

		var iterator = sequence.iterator();
		iterator.next(); // premier tour, id 1
		var secondTurn = iterator.next();
		assertEquals(2, secondTurn.getId());
	}

	@Test
	void should_throw_NPE_when_active_army_is_null_on_startNewTurn() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var sequence = TurnSequence.fromArmies(mordor, mankind);

		assertThrows(NullPointerException.class, () -> sequence.startNewTurn(null, mordor));
	}

	@Test
	void should_throw_NPE_when_other_army_is_null_on_startNewTurn() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var sequence = TurnSequence.fromArmies(mordor, mankind);

		assertThrows(NullPointerException.class, () -> sequence.startNewTurn(mankind, null));
	}

	// ---- endLastTurn ----

	@Test
	void should_return_zero_ennemies_killed_when_turn_not_yet_terminated() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var sequence = TurnSequence.fromArmies(mordor, mankind);

		var turn = sequence.iterator().next();

		assertEquals(0, turn.getEnnemiesKilled());
	}

	@Test
	void should_return_zero_influence_zone_size_when_turn_not_yet_terminated() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var sequence = TurnSequence.fromArmies(mordor, mankind);

		var turn = sequence.iterator().next();

		assertEquals(0, turn.getInfluenceZoneSize());
	}

	@Test
	void should_set_ennemies_killed_to_zero_when_last_turn_terminated_without_casualties() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var sequence = TurnSequence.fromArmies(mordor, mankind);

		sequence.endLastTurn(List.<Coordinate>of());

		var turn = sequence.iterator().next();
		assertEquals(0, turn.getEnnemiesKilled());
	}

	@Test
	void should_set_influence_zone_size_to_active_army_units_alive_when_last_turn_terminated_with_no_free_tiles() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var sequence = TurnSequence.fromArmies(mordor, mankind);

		sequence.endLastTurn(List.<Coordinate>of());

		var turn = sequence.iterator().next();
		assertEquals(mordor.getUnitsAlive(), turn.getInfluenceZoneSize());
	}

	@Test
	void should_throw_NPE_when_coordSource_is_null_on_endLastTurn() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var sequence = TurnSequence.fromArmies(mordor, mankind);

		assertThrows(NullPointerException.class, () -> sequence.endLastTurn(null));
	}

	@Test
	void should_not_throw_when_endLastTurn_called_on_empty_sequence() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var sequence = TurnSequence.fromArmies(mordor, mankind).filterByArmy(mordor.getClass().cast(mankind)); // volontairement vide, voir ci-dessous

		assertDoesNotThrow(() -> sequence.endLastTurn(List.<Coordinate>of()));
	}

	@Test
	void should_leave_count_unchanged_when_endLastTurn_called_on_empty_sequence() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var emptySequence = buildEmptySequence(mordor, mankind);

		emptySequence.endLastTurn(List.<Coordinate>of());

		assertEquals(0, emptySequence.count());
	}

	// ---- filterByArmy ----

	@Test
	void should_keep_only_turns_where_given_army_is_active_when_filtered_by_army() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var sequence = TurnSequence.fromArmies(mordor, mankind); // tour 1 : actif = mordor
		sequence.startNewTurn(mankind, mordor);                  // tour 2 : actif = mankind
		sequence.startNewTurn(mordor, mankind);                  // tour 3 : actif = mordor

		var filtered = sequence.filterByArmy(mordor);

		assertEquals(2, filtered.count());
	}

	@Test
	void should_return_turns_all_active_for_given_army_when_filtered_by_army() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var sequence = TurnSequence.fromArmies(mordor, mankind);
		sequence.startNewTurn(mankind, mordor);
		sequence.startNewTurn(mordor, mankind);

		var filtered = sequence.filterByArmy(mordor);

		for (var turn : filtered) {
			assertTrue(turn.isActive(mordor));
		}
	}

	@Test
	void should_return_empty_sequence_when_no_turn_is_active_for_given_army() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var thirdArmy = ArmiesFactory.createMankind(); // instance différente, jamais utilisée comme active
		var sequence = TurnSequence.fromArmies(mordor, mankind);

		var filtered = sequence.filterByArmy(thirdArmy);

		assertEquals(0, filtered.count());
	}

	// ---- count ----

	@Test
	void should_return_number_of_turns_when_count_called() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var sequence = TurnSequence.fromArmies(mordor, mankind);
		sequence.startNewTurn(mankind, mordor);

		assertEquals(2, sequence.count());
	}

	// ---- iterator ----

	@Test
	void should_iterate_over_all_turns_when_iterator_called() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var sequence = TurnSequence.fromArmies(mordor, mankind);
		sequence.startNewTurn(mankind, mordor);

		int nbIterated = 0;
		for (var ignored : sequence) {
			nbIterated++;
		}

		assertEquals(sequence.count(), nbIterated);
	}

	@Test
	void should_throw_UnsupportedOperationException_when_removing_from_iterator() {
		var mordor = ArmiesFactory.createMordor();
		var mankind = ArmiesFactory.createMankind();
		var sequence = TurnSequence.fromArmies(mordor, mankind);

		var iterator = sequence.iterator();
		iterator.next();

		assertThrows(UnsupportedOperationException.class, iterator::remove);
	}

	// ---- Utilitaire de test ----

	private TurnSequence buildEmptySequence(Army active, Army other) {
		var sequence = TurnSequence.fromArmies(active, other);
		return sequence.filterByArmy(ArmiesFactory.createMankind()); // instance neuve, active dans aucun tour
	}
}