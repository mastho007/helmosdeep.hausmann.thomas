package helmosdeep.supervisors.gameover;

import java.awt.Color;

/**
 * Déclare les méthodes pour afficher et gérer l'interface de fin de partie.
 * <p>
 * Cette interface permet de configurer les différents panneaux de la vue
 * de fin de partie, ainsi que de naviguer vers d'autres vues.
 * </p>
 */
public interface GameOverView {

    /**
     * Définit le contenu et la couleur du panneau gauche de la vue de fin de partie.
     *
     * @param color   la couleur de fond du panneau
     * @param header  le titre ou en-tête du panneau
     * @param content le contenu à afficher dans le panneau (chaînes de caractères)
     */
    void setLeftPanel(Color color, String header, String... content);

    /**
     * Définit le contenu et la couleur du panneau droit de la vue de fin de partie.
     *
     * @param color   la couleur de fond du panneau
     * @param header  le titre ou en-tête du panneau
     * @param content le contenu à afficher dans le panneau (chaînes de caractères)
     */
    void setRightPanel(Color color, String header, String... content);

    /**
     * Définit le contenu et la couleur du panneau inférieur de la vue de fin de partie.
     *
     * @param color   la couleur de fond du panneau
     * @param header  le titre ou en-tête du panneau
     * @param content le contenu à afficher dans le panneau (chaînes de caractères)
     */
    void setBottomPanel(Color color, String header, String... content);

    /**
     * Navigue vers une autre vue identifiée par son ID.
     *
     * @param viewId l'identifiant de la vue vers laquelle naviguer
     */
    void goTo(String viewId);
}