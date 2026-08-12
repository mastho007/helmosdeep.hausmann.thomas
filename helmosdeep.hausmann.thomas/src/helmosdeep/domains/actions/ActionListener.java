package helmosdeep.domains.actions;

import java.util.Optional;

import helmosdeep.domains.world.*;

/**
 * Écoute des événements liés aux actions d'une partie.
 *
 */
public interface ActionListener {

	/**
	 * Appelée lorsqu'un déplacement d'une unité a été effectuée.
	 *
	 * @param from la coordonnée de départ du déplacement
	 * @param to   la coordonnée d'arrivée du déplacement
	 */
	void moved(Coordinate from, Coordinate to);

	/**
	 * Appelée lorsqu'une unité est sélectionnée.
	 */
	void unitSelected();

	/**
	 * Appelée lorsqu'un combat entre deux unités a été résolu.
	 *
	 * @param attacker    l'unité qui a initié l'attaque
	 * @param defender    l'unité qui a subi l'attaque
	 * @param result      le résultat numérique du combat (> 0 signifie que l'attaquant a gagné, < 0 indique que le défenseur a gagné et 0 indique une égalité)
	 * @param posToRemove la coordonnée de l'unité à retirer ou {@code Optional.empty()}
	 *                    si aucune unité n'a été détruite
	 */
	void attackResolved(Unit attacker, Unit defender, float result, Optional<Coordinate> posToRemove);
}