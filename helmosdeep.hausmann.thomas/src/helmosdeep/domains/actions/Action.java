package helmosdeep.domains.actions;

/**
 * Représente une action qu'un joueur peut réaliser durant son tour de jeu
 * (par exemple : sélectionner une unité, déplacer une unité, etc.).
 *
 * <p>Une implémentation encapsule :
 * <ol>
 *   <li>la logique déterminant si l'action est actuellement réalisable
 *       ({@link #canBeDone()}) ;</li>
 *   <li>l'exécution de l'action elle-même ({@link #doAction(ActionListener)}).</li>
 * </ol>
 *
 * <h2>Utilisation</h2>
 * <p>Une implémentation d'{@code Action} doit toujours être utilisée selon le protocole
 * suivant :
 * {@code if(action.canBeDone()) {
 * 	doAction(listener);
 * }}
 *
 * <p>Les implémentations ne sont pas thread-safe et sont destinées à être
 * utilisées de façon séquentielle dans le fil d'exécution qui gère le tour de jeu.
 */
public interface Action {

	/**
	 * Indique si cette action peut être réalisée dans l'état courant du jeu.
	 *
	 * <p>Cette méthode peut, selon les implémentations, calculer et mémoriser en interne
	 * des données nécessaires à l'exécution de l'action par
	 * {@link #doAction(ActionListener)}. Il est donc impératif d'appeler cette méthode
	 * immédiatement avant tout appel à {@link #doAction(ActionListener)}, et de ne pas
	 * réutiliser un résultat obtenu lors d'un appel précédent si l'état du jeu a pu
	 * changer entre-temps.
	 *
	 * @return {@code true} si l'action peut être exécutée via
	 *         {@link #doAction(ActionListener)}, {@code false} sinon
	 */
	boolean canBeDone();

	/**
	 * Exécute cette action et notifie le {@code listener} fourni du résultat de
	 * l'exécution.
	 *
	 * <p><b>Précondition :</b> cette méthode ne doit être appelée que si le dernier appel
	 * à {@link #canBeDone()} sur cette même instance a retourné {@code true}, et qu'aucun
	 * changement d'état susceptible d'invalider ce résultat n'est intervenu depuis. Le
	 * comportement est non spécifié si cette précondition n'est pas respectée.
	 *
	 * @param listener le récepteur des notifications correspondant au type d'action
	 *                 réalisée (par exemple, notifié qu'une unité a été déplacée ou
	 *                 sélectionnée) ; ne doit pas être {@code null}
	 */
	void doAction(ActionListener listener);
}