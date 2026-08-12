package helmosdeep.domains.actions;

import java.util.Objects;

import helmosdeep.domains.turns.TurnsController;


/**
 * Action changeant l'unité sélectionnée de l'armée active selon la position courante.
 *
 * <p>Cette implémentation respecte le contrat général de {@link Action} :
 * {@link #canBeDone()} mémorise l'armée active dans un champ interne, réutilisé
 * implicitement au travers du {@link TurnsController} lors de l'appel à
 * {@link #doAction(ActionListener)}. Comme pour toute {@link Action}, appelez {@link #canBeDone()} juste avant {@link #doAction(ActionListener)}, sans
 * changement d'état du jeu entre les deux appels.
 */
public final class SelectUnitAction implements Action {
	private final TurnsController turnsController;


	/**
	 * Crée une nouvelle action de sélection d'unité.
	 *
	 * @param turnsController le contrôleur de tour donnant accès à la position courante et
	 *                        à l'armée active ; ne doit pas être {@code null}
	 * @throws NullPointerException si {@code turnsController} est {@code null}
	 */
	public SelectUnitAction(TurnsController turnsController) {
		this.turnsController = Objects.requireNonNull(turnsController);
	}

	/**
	 * Indique si une unité de l'armée active se trouve à la position courante du curseur,
	 * et peut donc être sélectionnée.
	 *
	 * @return {@code true} si une unité de l'armée active est présente à la position
	 *         courante, {@code false} sinon
	 */
	@Override
	public boolean canBeDone() {
		var activeArmy = turnsController.getActiveArmy();

		return activeArmy.getUnitAt(turnsController.getCurrentPosition()).isPresent();
	}

	/**
	 * Sélectionne l'unité présente à la position courante du curseur, puis notifie la
	 * sélection au {@code listener}.
	 *
	 * <p><b>Précondition :</b> {@link #canBeDone()} doit avoir été appelé juste avant et
	 * avoir retourné {@code true} ; voir le contrat détaillé dans la documentation de
	 * {@link Action#doAction(ActionListener)}.
	 *
	 * @param listener le récepteur notifié de la sélection effectuée, via
	 *                 {@link ActionListener#unitSelected()} ; ne doit pas être
	 *                 {@code null}
	 */
	@Override
	public void doAction(ActionListener listener) {
		turnsController.selectUnit();
		listener.unitSelected();
	}

}
