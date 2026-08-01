package helmosdeep.supervisors.game;

/**
 * Déclare les méthodes pour réagir aux demandes de la vue GameView.
 * */
public interface GameViewListener {

	/**
	 * Méthode invoquée quand la vue est sur le point d'être affichée à l'écran.
	 * */
	void onViewEntered();
	
	/**
	 * Signale une requête de déplacement décrite par une direction en X et une direction en Y.
	 * <p>
	 * Quand cette méthode est appelée, les arguments (xDir;yDir) sont différents de (0;0).
	 * </p>
	 * @param xDir le déplacement en X valant -1,0 ou 1
	 * @param yDir le déplacement en Y valant -1,0 ou 1
	 * */
	void onMove(int xDir, int yDir);

	/**
	 * Signale que l'utilisateur souhaite effectuer une action sur la case active.
	 * */
	void onAction();


	/**
	 * Signale que l'utilisateur souhaite annuler l'action précédente
	 */
	void onCancel();

	/**
	 * Signale que l'utilisateur souhaite passer son tour.
	 * */
	void onEndTurn();

}
