package helmosdeep.supervisors.mainmenu;

/**
 * Déclare les méthodes pour gérer les interactions de l'utilisateur
 * avec la vue {@code MainMenuView}.
 * <p>
 * Cette interface permet de réagir à la sélection d'un item ou au retour au menu depuis
 * la vue de fin de partie.
 * </p>
 */
public interface MainMenuListener {

    /**
     * Méthode invoquée lorsque la vue {@code MainMenuView} est sur le point
     * d'être affichée à l'écran.
     * <p>
     * Peut être utilisée pour initialiser ou réinitialiser l'état nécessaire
     * à l'affichage du menu principal.
     * </p>
     */
    void onViewEntered();

    /**
     * Signale que l'utilisateur a sélectionné un item du menu principal.
     * <p>
     * Cette méthode est appelée avec l'indice de l'élément sélectionné,
     * permettant de déclencher l'action associée à cet élément.
     * </p>
     * @param itemIndex l'indice de l'élément sélectionné dans le menu
     */
    void onItemSelected(int itemIndex);
}