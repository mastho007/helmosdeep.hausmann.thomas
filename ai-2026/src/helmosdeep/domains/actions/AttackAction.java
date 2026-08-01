package helmosdeep.domains.actions;

import java.util.*;

import helmosdeep.domains.turns.TurnsController;
import helmosdeep.domains.world.*;

/**
 * Attaque une unité ennemie avec l'unité actuellement sélectionnée.
 * <p>
 * L'attaquant est l'unité de l'armée active située à la position
 * sélectionnée ({@link TurnsController#getSelectedPosition()}), et le
 * défenseur est l'unité de l'armée adverse située à la position courante
 * ({@link TurnsController#getCurrentPosition()}). L'attaque n'est possible
 * que si les deux positions sont contiguës, que l'attaquant existe et est
 * en mesure d'attaquer, et que le défenseur existe.
 * <p>
 * La résolution du combat repose sur un lancer de dés ({@link FibDice})
 * qui détermine la puissance de chaque unité ; l'unité dont la puissance
 * calculée est la plus faible est retirée du plateau.
 */
public final class AttackAction implements Action {
	private final TurnsController turnsController;
	private final FibDice dice;
	
	private Optional<Unit> attacker;
	private Optional<Unit> defender;
	
	/**
	 * Crée une nouvelle action d'attaque liée au contrôleur de tours donné.
	 *
	 * @param turnsController le contrôleur de tours à partir duquel sont
	 *        déterminés l'attaquant, le défenseur et leurs positions
	 * @throws NullPointerException si {@code turnsController} est
	 *         {@code null}
	 */
	public AttackAction(TurnsController turnsController) {
		this.turnsController = Objects.requireNonNull(turnsController);
		this.dice = new FibDice();
	}
	
	/**
	 * Détermine si l'attaque peut être effectuée dans l'état actuel du jeu.
	 * <p>
	 * Recalcule au passage l'attaquant et le défenseur à partir des
	 * positions courantes du {@link TurnsController}. L'attaque est
	 * possible si :
	 * <ul>
	 *     <li>les positions sélectionnée et courante sont contiguës
	 *     (distance de 1) ;</li>
	 *     <li>une unité attaquante existe à la position sélectionnée et est
	 *     en mesure d'attaquer ;</li>
	 *     <li>une unité défenseur existe à la position courante.</li>
	 * </ul>
	 *
	 * @return {@code true} si l'attaque peut être effectuée, {@code false}
	 *         sinon
	 */
	@Override
	public boolean canBeDone() {
		this.attacker = turnsController.getActiveArmy().getUnitAt(turnsController.getSelectedPosition());
		this.defender = turnsController.getOtherArmy().getUnitAt(turnsController.getCurrentPosition());
		
		final var haveContinguousPos = turnsController.getSelectedPosition().distanceFrom(turnsController.getCurrentPosition()) == 1; 
		final var attackerCanAttack = attacker.isPresent() && attacker.get().isAllowedToAttack();
		final var defenderExists = defender.isPresent();
		
		return haveContinguousPos && attackerCanAttack && defenderExists;
	}

	/**
	 * Résout le combat entre l'attaquant et le défenseur préalablement
	 * déterminés par {@link #canBeDone()}.
	 * <p>
	 * La puissance de chaque unité est calculée via un lancer de dés, puis
	 * comparée : l'unité la plus faible est retirée du plateau. Le
	 * {@code listener} est ensuite notifié du résultat via
	 * {@link ActionListener#attackResolved}, avec le signe de l'écart de
	 * puissance et, le cas échéant, la position de l'unité retirée.
	 *
	 * @param listener l'écouteur à notifier de l'issue du combat
	 * @throws NullPointerException si {@code listener} est {@code null}
	 */
	@Override
	public void doAction(ActionListener listener) {
		Objects.requireNonNull(listener);
		
		this.turnsController.getActiveArmy().computePowerAt(turnsController.getSelectedPosition(), dice.roll());
		this.turnsController.getOtherArmy().computePowerAt(turnsController.getCurrentPosition(), dice.roll());
		var attackResult = attacker.get().getPow() - defender.get().getPow();
		var posToRemove = Optional.<Coordinate>empty();
		if(attackResult > 0) {
			posToRemove = Optional.of(turnsController.getCurrentPosition());
			this.turnsController.removeAt(turnsController.getCurrentPosition());
		} else if(attackResult < 0) {
			posToRemove =  Optional.of(turnsController.getSelectedPosition());
			this.turnsController.removeAt(turnsController.getSelectedPosition());
		}
		
		listener.attackResolved(this.attacker.get(), this.defender.get(), Math.signum(attackResult), posToRemove);
	}
}