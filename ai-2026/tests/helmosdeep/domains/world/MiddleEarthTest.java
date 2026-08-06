/**
 * 
 */
package helmosdeep.domains.world;

import static helmosdeep.domains.world.Coordinate.coord;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Valide la classe MiddleEarth
 */
class MiddleEarthTest {

	@Test
	void should_return_an_empty_middle_earth_after_creation() {
		var middleEarth = MiddleEarth.create();
		
		assertEquals(Tile.UNKNWOWN, middleEarth.getTileAt(coord(0, 0)));
	}

	@Test
	void should_allow_to_consume_tiles() {
		var md = MiddleEarth.create();
		md.put(coord(0, 0), Tile.LOWLAND);
		md.put(coord(0, 1), Tile.LOWLAND);
		md.put(coord(0, 2), Tile.LOWLAND);
		md.put(coord(1, 0), Tile.FOREST);
		md.put(coord(1, 1), Tile.FOREST);
		md.put(coord(1, 2), Tile.FOREST);
		md.put(coord(2, 0), Tile.MOUNTAIN);
		md.put(coord(2, 1), Tile.MOUNTAIN);
		md.put(coord(2, 2), Tile.MOUNTAIN);
		
		md.applyToEachTile((coord, tile) -> {
			switch(coord.getRow()) {
			case 0 -> assertEquals(Tile.LOWLAND, tile);
			case 1 -> assertEquals(Tile.FOREST, tile);
			case 2 -> assertEquals(Tile.MOUNTAIN, tile);
			}
		});
	}
	
	@Test
	void should_provide_coordinate_iterators() {
		var md = MiddleEarth.create();
		md.put(coord(0, 0), Tile.LOWLAND);
		md.put(coord(0, 1), Tile.LOWLAND);
		md.put(coord(0, 2), Tile.LOWLAND);
		md.put(coord(1, 0), Tile.FOREST);
		md.put(coord(1, 1), Tile.FOREST);
		md.put(coord(1, 2), Tile.FOREST);
		md.put(coord(2, 0), Tile.MOUNTAIN);
		md.put(coord(2, 1), Tile.MOUNTAIN);
		md.put(coord(2, 2), Tile.MOUNTAIN);
		
		int count = 0;
		for(Coordinate coord : md) {
			assertTrue(0 <= coord.getRow() && coord.getRow() <= 2);
			assertTrue(0 <= coord.getCol() && coord.getCol() <= 2);
			++count;
		}
		
		assertEquals(9, count);
	}
	
	
	@Test
	void should_reject_invalid_args_when_putting_tiles() {
		var md = MiddleEarth.create();
		md.put(coord(1, 1), Tile.MOUNTAIN);
		
		assertThrows(NullPointerException.class, () -> md.put(null, Tile.MOUNTAIN));
		assertThrows(NullPointerException.class, () -> md.put(coord(0,0), null));
		assertThrows(IllegalArgumentException.class, () -> md.put(coord(1, 1), Tile.FOREST));
	}
	
	@Test
	void should_compute_minimal_cost_between_two_coords() {
		var md = MiddleEarth.create();
		Unit uniteUn = new Unit("Orcs", UnitType.AVERAGE, new StandardLightMovementStrategy());
		Unit uniteDeux =  new Unit("Trolls", UnitType.HEAVY, new StandardHeavyMovementStrategy());
		md.put(coord(0, 0), Tile.LOWLAND);
		md.put(coord(0, 1), Tile.LOWLAND);
		md.put(coord(0, 2), Tile.LOWLAND);
		md.put(coord(1, 0), Tile.FOREST);
		md.put(coord(1, 1), Tile.FOREST);
		md.put(coord(1, 2), Tile.FOREST);
		md.put(coord(2, 0), Tile.MOUNTAIN);
		md.put(coord(2, 1), Tile.MOUNTAIN);
		md.put(coord(2, 2), Tile.MOUNTAIN);
		
		assertEquals(0, md.computeMoveCostFor(1, coord(0,0), coord(0,0), uniteUn));
		assertEquals(1, md.computeMoveCostFor(1, coord(0,0), coord(0,1), uniteUn));
		assertEquals(6, md.computeMoveCostFor(8, coord(0,0), coord(2,2), uniteUn));
		assertEquals(5, md.computeMoveCostFor(8, coord(0,1), coord(2,2), uniteDeux));
		assertEquals(Integer.MAX_VALUE, md.computeMoveCostFor(0, coord(0,0), coord(0,1), uniteUn));
	}
	
	@Test
	void should_reject_absent_coordinate() {
		
		Unit uniteUn = new Unit("Orcs", UnitType.AVERAGE, new StandardLightMovementStrategy());
		var md = MiddleEarth.create();
		
		md.put(coord(0, 0), Tile.LOWLAND);
		md.put(coord(0, 1), Tile.LOWLAND);
		md.put(coord(0, 2), Tile.LOWLAND);
		md.put(coord(1, 0), Tile.FOREST);
		md.put(coord(1, 1), Tile.FOREST);
		md.put(coord(1, 2), Tile.FOREST);
		md.put(coord(2, 0), Tile.MOUNTAIN);
		md.put(coord(2, 1), Tile.MOUNTAIN);
		md.put(coord(2, 2), Tile.MOUNTAIN);
		
		assertThrows(IllegalArgumentException.class, () -> md.computeMoveCostFor(5, coord(-1, 0), coord(2,2), uniteUn));
		assertThrows(IllegalArgumentException.class, () -> md.computeMoveCostFor(5, coord(1, 1), coord(3,2), uniteUn));
	}
}
