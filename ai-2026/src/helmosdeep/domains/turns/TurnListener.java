package helmosdeep.domains.turns;

/**
 * Écouteur notifié de l'issue du passage au tour suivant.
 * <p>
 * Après le traitement d'une fin de tour, une des deux méthodes de
 * cette interface est appelée : soit {@link #newTurnStarted()} si la partie peut
 * continuer, soit {@link #gameEnded()} si elle est terminée parce qu'une des
 * deux armées n'est plus en mesure de combattre.
 */
public interface TurnListener {

	/**
	 * Appelée lorsqu'un nouveau tour démarre, c'est-à-dire lorsque les deux
	 * armées sont encore capables de combattre.
	 */
	void newTurnStarted();

	/**
	 * Appelée lorsque la partie se termine, c'est-à-dire lorsqu'au moins une
	 * des deux armées n'est plus capable de combattre.
	 */
	void gameEnded();
}