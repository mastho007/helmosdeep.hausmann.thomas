package helmosdeep.domains.world;

/**
 * Cette class abstraite fournit le comportement par défaut du jeu (aucune
 * option cochée) :<br>
 * 
 * <ul>
 * <li>Permettre le calcul du cout de mouvement d'une unité sur une tuile, selon
 * les options sélectionnées</li>
 * <li></li>
 * <li></li>
 * </ul>
 * 
 */
public abstract class MovementStrategy {

	/**
	 * Détermine le cout du mouvement d'une unité lourde sur base des options de
	 * jeu.
	 * 
	 * @param Unite est l'unité qui contient un cout de mouvement propre selon son
	 *              type.
	 * @return un entier qui représente le nombre de points de mouvement restants de
	 *         l'unité.
	 */
	public int calculateMovementCost(Tile tile) {

		return tile.getCost();
	}

	/**
	 * Détermine si une unité légère peut se déplacer apès avoir attaquée sur base
	 * des options de jeu.<br>
	 * Pour ce faire il faut vérifier ces points si l'option a été cochée :
	 * <ul>
	 * <li>L'unité a déja attaquée durant ce tour ?</li>
	 * <li>Les points de mouvement de l'unité sont suffisant pour le déplacement
	 * ?</li>
	 * <li>Les points de mouvement de l'armée peuvent supporté ce déplcement ?</li>
	 * </ul>
	 * 
	 * @param unite             l'unité dont on doit déterminer si elle peut encore
	 *                          se déplacer
	 * @param tuileCible        la tuile auquel l'unité doit se déplacer
	 * @param mouvementDeLarmee le nombre de points de mouvement restant de l'armée
	 *                          de l'unité
	 * @return true si l'unité légère peut se déplacer une deuxième fois, false
	 *         sinon
	 */
	public boolean canMoveAfterAttack(Unit unite, Tile tuileCible, int mouvementDeLarmee) {

		return false;
	}

}
