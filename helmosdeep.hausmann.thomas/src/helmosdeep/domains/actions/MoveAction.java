package helmosdeep.domains.actions;

import java.util.Objects;

import helmosdeep.domains.turns.TurnsController;
import helmosdeep.domains.world.MiddleEarth;
import helmosdeep.domains.world.MovementStrategy;
import helmosdeep.domains.world.SpecialLightMovementStrategy;

/**
 * Action déplaçant l'unité actuellement sélectionnée depuis sa position de
 * sélection vers la position courante, en Terre du Milieu.
 *
 * <p>
 * Cette implémentation respecte le contrat général décrit dans {@link Action} :
 * le coût de déplacement nécessaire pour atteindre la position visée est
 * calculé et mémorisé lors de l'appel à {@link #canBeDone()}, puis réutilisé
 * lors de l'appel à {@link #doAction(ActionListener)}. Il est donc
 * indispensable d'appeler {@link #canBeDone()} juste avant
 * {@link #doAction(ActionListener)}, sans appel intermédiaire à
 * {@link #canBeDone()} sur une autre instance d'action, et sans changement
 * d'état du jeu entre les deux appels.
 *
 * <p>
 * Cette classe suppose qu'une unité est déjà sélectionnée via le
 * {@link TurnsController} fourni au moment de l'appel à {@link #canBeDone()}
 * (invariant du système : une unité est toujours sélectionnée lorsqu'une
 * {@code MoveAction} est évaluée).
 *
 */
public final class MoveAction implements Action {
	private final MiddleEarth middleEarth;

	private final TurnsController turnsController;

	/**
	 * Nombre de points de mouvement consommés par le déplacement évalué lors du
	 * dernier appel à {@link #canBeDone()}. Cette valeur n'est significative
	 * qu'immédiatement après un appel à {@link #canBeDone()} ayant retourné
	 * {@code true}, et est utilisée par {@link #doAction(ActionListener)}.
	 */
	private int mvtConsumed;

	/**
	 * Crée une nouvelle action de déplacement.
	 *
	 * @param middleEarth     le monde du jeu sur lequel le déplacement doit être
	 *                        évalué et appliqué ; ne doit pas être {@code null}
	 * @param turnsController le contrôleur de tour donnant accès à l'unité
	 *                        sélectionnée, à la position ciblée et à l'armée active
	 *                        ; ne doit pas être {@code null}
	 * @throws NullPointerException si {@code middleEarth} ou
	 *                              {@code turnsController} est {@code null}
	 */
	public MoveAction(MiddleEarth middleEarth, TurnsController turnsController) {
		this.middleEarth = Objects.requireNonNull(middleEarth);
		this.turnsController = Objects.requireNonNull(turnsController);
	}

	/**
	 * Indique si l'unité sélectionnée peut se déplacer vers la position courante du
	 * curseur.
	 *
	 * <p>
	 * L'action est possible si, et seulement si, les quatres conditions suivantes
	 * sont réunies :
	 * <ul>
	 * <li>la position visée est libre (aucune unité n'y est présente) ;</li>
	 * <li>la position visée est accessible avec les points de mouvement disponibles
	 * pour l'unité sélectionnée (coût de déplacement strictement positif et fini)
	 * ;</li>
	 * <li>l'unité sélectionnée peut encore se déplacer ce tour-ci.</li>
	 * <li>l'unité sélectionnée est une unité légère dont l'option "unités légères"
	 * et si elle correspond au conditions dans {@link SpecialLightMovementStrategy#canMoveAfterAttack()}.</li>
	 * </ul>
	 *
	 * <p>
	 * Cette méthode calcule et mémorise le coût de déplacement correspondant, qui
	 * sera ensuite réutilisé par {@link #doAction(ActionListener)} : voir le
	 * contrat détaillé dans la documentation de {@link Action#canBeDone()}.
	 *
	 * @return {@code true} si le déplacement est réalisable, {@code false} sinon
	 */
	@Override
	public boolean canBeDone() {
		var from = turnsController.getSelectedPosition();
		var to = turnsController.getCurrentPosition();

		final var activeUnit = turnsController.getUnitAt(from).get(); // INVARIANT : une unité est toujours sélectionnée
		final var isPosFree = turnsController.getUnitAt(to).isEmpty();

		// on calcule les mouvements restants pour l'unité active, l'armée active -> le
		// minimum des deux représente le mouvement restant
		int unitMvtLeft = activeUnit.getMvt() - activeUnit.getMvtPointsConsumed();
		int armyMvtLeft = turnsController.getMvtForActiveUnit();

		int availableMvt = Math.min(unitMvtLeft, armyMvtLeft);

		mvtConsumed = middleEarth.computeMoveCostFor(availableMvt, from, to, activeUnit);

		final var isAccessible = 0 < mvtConsumed && mvtConsumed < Integer.MAX_VALUE;

		// si la case de la coordonnée courante n'est pas vide OU
		// que la case n'est pas accessible -> false
		if (!isPosFree || !isAccessible) {

			return false;
		}

		// si c'est le premier déplacement ?
		if (activeUnit.isAllowedToMove()) {

			return true;
		}

		// si c'est le 2nd déplacement on délègue le raisonnement à la stratégie
		return activeUnit.getStrategy().canMoveAfterAttack(activeUnit, middleEarth.getTileAt(to), availableMvt);

	}

	/**
	 * Déplace effectivement l'unité active de sa position de sélection vers la
	 * position courante, en consommant le nombre de points de mouvement calculé
	 * lors du dernier appel à {@link #canBeDone()}, puis notifie le déplacement au
	 * {@code actionListener}.
	 *
	 * <p>
	 * <b>Précondition :</b> {@link #canBeDone()} doit avoir été appelé juste avant
	 * et avoir retourné {@code true} ; voir le contrat détaillé dans la
	 * documentation de {@link Action#doAction(ActionListener)}.
	 *
	 * @param actionListener le récepteur notifié du déplacement effectué, via
	 *                       {@link ActionListener#moved(Object, Object)} ; ne doit
	 *                       pas être {@code null}
	 */
	@Override
	public void doAction(ActionListener actionListener) {
		var from = turnsController.getSelectedPosition();
		var to = turnsController.getCurrentPosition();

		// on ajoute le nombre de points de déplacement dépensé par l'unité
		turnsController.getUnitAt(from).get().addMvtPointsConsumed(mvtConsumed);

		turnsController.moveActiveUnit(mvtConsumed);

		actionListener.moved(from, to);
	}

}
