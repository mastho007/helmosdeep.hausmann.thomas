package helmosdeep.domains.world;

/**
 * Offrir le déplacement facilité aux unités lourdes sur tous les terrains.
 */
public class SpecialHeavyMovementStrategy extends MovementStrategy {

	@Override
	public int calculateMovementCost(Tile tile) {

		//si on est dans le cas de l'unité lourde et que l'on a sélectionné l'option
		//le cout de déplacement de l'unité 
		return 1;
	}

}
