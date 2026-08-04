package helmosdeep.domains;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Valide la classe MiddleEarthReader
 */
class MiddleEarthReaderTest {

	@Test
	void should_load_from_a_valid_txt_file() {
		var loader = new MiddleEarthReader();

		loader.loadFromFile("resources/maps/level-a.txt", new GameOption());

		assertNotNull(loader.getBattefield());
		assertNotNull(loader.getArmies());
	}

	@Test
	void should_reject_loading_when_dimensions_are_missing() {
		var loader = new MiddleEarthReader();

		assertThrows(IllegalStateException.class,
				() -> loader.loadFromFile("resources/maps/missing-dimensions.txt", new GameOption()));
	}

	@Test
	void should_reject_loading_when_rows_are_differents() {
		var loader = new MiddleEarthReader();

		assertThrows(IllegalStateException.class, () -> loader.loadFromFile("resources/maps/unexpected-rows.txt", new GameOption()));
	}

}
