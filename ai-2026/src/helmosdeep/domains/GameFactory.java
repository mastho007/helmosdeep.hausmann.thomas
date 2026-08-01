package helmosdeep.domains;

/**
 * Permet de créer de nouvelles parties.
 * */
public interface GameFactory {
	/**
	 * Instancie une nouvelle partie
	 * */
	void create(String level);
	
	/**
	 * Instancie une nouvelle partie avec le premier niveau.
	 * <p>
	 * Dans l'implémentation par défaut, ce niveau est identifié par "level-1".
	 * */
	default void create() {
		create("level-1");
	}

}
