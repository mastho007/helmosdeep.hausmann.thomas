package helmosdeep.domains.world;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MovementStrategyTest {

	@BeforeEach
	void setUp() throws Exception {

	}

	@Test
	void calculateTheCorrectMovementCostForAstandardStrategy() {

		// on créé une unité avec une stratégie standard
		Unit unit = new Unit("Wargs", UnitType.LIGHT, new StandardLightMovementStrategy());

		assertEquals(2, unit.getStrategy().calculateMovementCost(Tile.FOREST),
				"Le cout du mouvement pour le terrain forêt doit retourné 2 pour une unité standard.");

	}

	@Test
	void ShouldAlwaysReturnFalseForAmovementAfterAttackForAstandardStrategy() {

		// on créé une unité avec une stratégie standard
		Unit unit = new Unit("Wargs", UnitType.LIGHT, new StandardLightMovementStrategy());

		assertFalse(unit.getStrategy().canMoveAfterAttack(unit, Tile.FOREST, 10),
				"L'unité qui a une stratégie standard ne doit pas pouvoir se déplacer après une attaque.");

	}

	@Test
	void foraSpecialHeavyUnitStrategyShouldReturnOneForAMovementCost() {

		// on créé une unité avec une stratégie spéciale lourde
		Unit unit = new Unit("Trolls", UnitType.HEAVY, new SpecialHeavyMovementStrategy());

		assertEquals(1, unit.getStrategy().calculateMovementCost(Tile.FOREST),
				"L'unité lourde qui a une stratégie spéciale doit retourner"
						+ " 1 pour chaque cout de mouvement tuile peut importe le type de tuile.");

	}

	@Test
	void forASpecialLightUnitStrategy() {

		// on créé une unité avec une stratégie spéciale légère
		Unit unit = new Unit("Wargs", UnitType.LIGHT, new SpecialLightMovementStrategy());

		assertFalse(unit.getStrategy().canMoveAfterAttack(unit, Tile.FOREST, 5),
				"Doit retourner false car l'unité n'a pas encore attaqué.");

		// comme si l'unité avait attaquée
		unit.setPower(5);

		assertTrue(unit.getStrategy().canMoveAfterAttack(unit, Tile.FOREST, 5),
				"L'unité doit pouvoir effectuer cette 2ième attaque.");

		unit.setPower(1);

		assertFalse(unit.getStrategy().canMoveAfterAttack(unit, Tile.MOUNTAIN, 2),
				"Doit retourner false car l'unité(avec l'armée) n'a pas assez de points de mouvement disponibles.");

		assertTrue(unit.getStrategy().canMoveAfterAttack(unit, Tile.LOWLAND, 1),
				"Doit retourner true car l'unité(avec l'armée) a juste assez de points de mouvement disponibles.");

	}

}
