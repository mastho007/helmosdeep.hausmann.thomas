package helmosdeep.supervisors.game;

import java.awt.Color;

/**
 * Définit les opérations disponibles pour interagir avec la vue du jeu.
 * 
 * Cette interface manipulent les éléments visuels du jeu, tels que les tuiles hexagonales,
 * les unités, la caméra, et d’afficher des informations dans le panneau de statut.
 */
public interface GameView {

    /**
     * Ajoute un écouteur à cette vue pour recevoir les requêtes de l'utilisateur.
     *
     * @param listener L’écouteur à ajouter
     */
    void addListener(GameViewListener listener);

    /**
     * Ajoute un hexagone à la position spécifiée avec la couleur indiquée.
     *
     * @param row   La ligne de l’hexagone
     * @param col   La colonne de l’hexagone
     * @param color La couleur de l’hexagone
     */
    void addHex(int row, int col, Color color);

    /**
     * Ajoute une unité à la position spécifiée avec un nom et des statistiques.
     *
     * @param row   La ligne de l’unité
     * @param col   La colonne de l’unité
     * @param name  Le nom de l’unité
     * @param stats Les statistiques de l’unité
     */
    void addUnit(int row, int col, String name, String stats);

    /**
     * Déplace la caméra vers la position spécifiée.
     *
     * @param newRow La ligne vers laquelle déplacer la caméra
     * @param newCol La colonne vers laquelle déplacer la caméra
     */
    void moveCameraTo(int newRow, int newCol);

    /**
     * Modifie la couleur d’un hexagone à la position spécifiée.
     *
     * @param row      La ligne de l’hexagone
     * @param col      La colonne de l’hexagone
     * @param newColor La nouvelle couleur de l’hexagone
     */
    void setHexColor(int row, int col, Color newColor);

    /**
     * Supprime une unité à la position spécifiée.
     *
     * @param row La ligne de l’unité
     * @param col La colonne de l’unité
     */
    void removeUnit(int row, int col);

    /**
     * Sélectionne une unité à la position spécifiée.
     *
     * @param currentRow La ligne de l’unité
     * @param currentCol La colonne de l’unité
     */
    void selectUnit(int currentRow, int currentCol);

    /**
     * Désélectionne une unité à la position spécifiée.
     *
     * @param currentRow La ligne de l’unité
     * @param currentCol La colonne de l’unité
     */
    void unselectUnit(int currentRow, int currentCol);

    /**
     * Déplace une unité d’une position à une autre.
     *
     * @param fromRow La ligne de départ
     * @param fromCol La colonne de départ
     * @param toRow   La ligne d’arrivée
     * @param toCol   La colonne d’arrivée
     */
    void moveUnit(int fromRow, int fromCol, int toRow, int toCol);

    /**
     * Met à jour le panneau de statut avec les lignes de texte spécifiées.
     *
     * @param lines Les lignes de texte à afficher
     */
    void setStatusPanel(String... lines);

    /**
     * Change de scène vers celle spécifiée par son nom.
     *
     * @param sceneName Le nom de la scène vers laquelle naviguer
     */
    void goTo(String sceneName);
}
