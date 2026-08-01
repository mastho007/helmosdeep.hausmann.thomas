package helmosdeep.supervisors.gameover;

/**
 * Déclare les méthodes pour gérer les interactions de l'utilisateur
 * avec la vue {@code GameOverView}.
 * <p>
 * Cette interface permet de réagir aux actions de l'utilisateur
 * après la fin d'une partie, comme le retour au menu principal.
 * </p>
 */
public interface GameOverListener {

    /**
     * Méthode invoquée lorsque la vue {@code GameOverView} est sur le point
     * d'être affichée à l'écran.
     * <p>
     * Peut être utilisée pour initialiser ou réinitialiser l'état nécessaire
     * à l'affichage de la vue.
     * </p>
     */
    void onViewEntered();

    /**
     * Signale que l'utilisateur souhaite effectuer une action sur la vue
     * {@code GameOverView}.
     * <p>
     * Cette méthode est appelée quand l'utilisateur souhaite retourner au menu principal.
     * </p>
     */
    void onAction();
}