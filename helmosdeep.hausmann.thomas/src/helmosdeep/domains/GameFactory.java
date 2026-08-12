package helmosdeep.domains;

/**
 * Permet de créer de nouvelles parties.
 */
public interface GameFactory {
	/**
	 * Instancie une nouvelle partie
	 */
	void create(String level, GameOption gameOption);

	/**
	 * Instancie une nouvelle partie avec le premier niveau.
	 * <p>
	 * - Dans l'implémentation par défaut, ce niveau est identifié par
	 * "level-1".<br>
	 * - En paramètre il y a un objet GameOption qui permet de récupérer les
	 * différentes options choisies pour la bonne création des unités.
	 * 
	 */
	default void create(GameOption gameOption) {
		create("level-b", gameOption);
	}

}
