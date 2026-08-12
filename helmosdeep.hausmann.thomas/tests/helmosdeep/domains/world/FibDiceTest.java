package helmosdeep.domains.world;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class FibDiceTest {

	@Test
	void should_respect_a_fibonaci_mod_six_sequence() {
		var dice = new FibDice();
		var expected = List.of(2,2,3,4,6,3,2,4);
		
		for(int i=0; i < expected.size(); ++i) {
			assertEquals(expected.get(i), dice.roll());
		}
	}

}
