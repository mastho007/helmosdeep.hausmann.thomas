package helmosdeep.domains.world;

/**
 * Permettre le mouvement après attaque pour les unités légères si ces
 * conditions sont remplie :
 * 
 * <ul>
 * <li>l'unité a déja attaquée</li>
 * <li>le nombre de points de mouvement restant de l'unité est suffisant par
 * rapport au cout de déplacement</li>
 * </ul>
 */
public class SpecialLightMovementStrategy extends MovementStrategy {

	@Override
	public boolean canMoveAfterAttack(Unit unit, Tile targetTile, int availableMvt) {

		// est ce que l'unité a déja attaqué durant le même tour ?
		boolean hasAttacked = unit.getPow() > 0;

		// le cout de la tuile ne doit pas dépasser la capital mouvement disponible
		int cost = calculateMovementCost(targetTile);

		boolean hasEnoughMvt = availableMvt >= cost;

		return hasAttacked && hasEnoughMvt;
	}

}
