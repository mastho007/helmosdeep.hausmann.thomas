package helmosdeep.domains.world;


/**
 * Permettre le mouvement après attaque pour les unités légères.
 */
public class SpecialLightMovementStrategy extends MovementStrategy{

	
	@Override
	public boolean canMoveAfterAttack(Unit unit, Tile targetTile, int armyMvtAvailable) {
		
		int cost = calculateMovementCost(targetTile);
		
		//on calcule les PM restants propres à l'unité
		int uniteMvtRemaining = unit.getMvt() - unit.getMvtPointsConsumed();
		
		
		return uniteMvtRemaining >= cost && armyMvtAvailable >= cost;
	}
	
	
	
	
	
}
