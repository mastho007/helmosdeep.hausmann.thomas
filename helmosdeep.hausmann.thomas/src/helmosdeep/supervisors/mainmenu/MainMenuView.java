package helmosdeep.supervisors.mainmenu;

/**
 * Déclare les méthodes pour afficher et gérer l'interface du menu principal.
 * <p>
 * Cette interface permet d'ajouter des écouteurs d'événements, de configurer
 * les éléments du menu, de naviguer vers d'autres vues et de confirmer
 * la fermeture de l'application.
 * </p>
 */
public interface MainMenuView {

    /**
     * Ajoute un écouteur pour réagir aux interactions de l'utilisateur
     * avec le menu principal.
     *
     * @param listener l'écouteur à ajouter pour gérer les événements du menu
     */
    void addListener(MainMenuListener listener);

    /**
     * Définit les éléments à afficher dans le menu principal.
     *
     * @param title le titre du panneau
     * @param items les éléments du menu à afficher
     */
	void setItems(String title, String... items);
	

    /**
     * Navigue vers une autre vue identifiée par son nom.
     *
     * @param viewName le nom de la vue vers laquelle naviguer
     */
    void goTo(String viewName);

    /**
     * Confirme la demande de quitter l'application et libère les ressources.
     */
    void confirmQuit();

}
