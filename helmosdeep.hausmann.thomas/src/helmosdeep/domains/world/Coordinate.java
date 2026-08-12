package helmosdeep.domains.world;

import java.util.*;

/**
 * Représente une coordonnée
 * <p>
 * De base, les coordonnées utilisent le système (col, row). Elles sont également compatibles
 * avec un système de coordonnées axiales (q,r).
 * </p>
 * <p>
 * Les objets de cette classe sont immuables et mis en cache pour éviter les doublons.
 * Deux coordonnées avec les mêmes valeurs de ligne et colonne sont garanties d'être le même objet en mémoire.
 * </p>
 */
public final class Coordinate {
	private static final Map<Integer, Coordinate> CACHE = new HashMap<>();
	public static final Coordinate NONE = new Coordinate(Integer.MIN_VALUE, Integer.MIN_VALUE);
	
    private final int row;
    private final int col;

    /**
     * Fabrique une coordonnée immuable à partir des valeurs de ligne et colonne.
     * Si une coordonnée avec les mêmes valeurs existe déjà en cache, celle-ci est retournée.
     *
     * @param row la ligne de la coordonnée (peut être négative)
     * @param col la colonne de la coordonnée (peut être négative)
     * @return une instance de {@code Coordinate} correspondant à (row, col)
     */
    public static Coordinate coord(int row, int col) {
        final int key = toInt(row, col);

        if (!CACHE.containsKey(key)) {
            CACHE.put(key, new Coordinate(row, col));
        }

        return CACHE.get(key);
    }

    /**
     * Constructeur privé pour garantir l'immuabilité et l'utilisation de la fabrique {@link #coord(int, int)}.
     *
     * @param row la ligne de la coordonnée
     * @param col la colonne de la coordonnée
     */
    private Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Convertit une paire (row, col) en une clé entière unique pour le cache.
     *
     * @param row la ligne
     * @param col la colonne
     * @return une clé entière unique
     */
    private static int toInt(int row, int col) {
        return row * 100 + col;
    }
    
    @Override
    public String toString() {
    	return "(%02d, %02d)".formatted(getCol(), getRow());
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Coordinate that && equalTo(that);
    }

    @Override
    public int hashCode() {
        return toInt(row, col);
    }

    /**
     * Compare cette coordonnée à une autre pour l'égalité structurelle.
     *
     * @param that la coordonnée à comparer
     * @return {@code true} si les deux coordonnées ont les mêmes valeurs de ligne et colonne
     * @throws NullPointerException si {@code that} est {@code null}
     */
    private boolean equalTo(Coordinate that) {
        return toInt(row, col) == toInt(that.row, that.col);
    }

    /**
     * Retourne la ligne de cette coordonnée.
     *
     * @return la valeur de la ligne
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Retourne la colonne de cette coordonnée.
     *
     * @return la valeur de la colonne
     */
    public int getCol() {
        return this.col;
    }
    
    /**
     * Retourne le composant Q de cette coordonée.
     * <p>
     * Ce composant correspond au composant col.
     * </p>
     * */
    public int getQ() {
    	return col;
    }
    
    /**
     * Retourne le composant R de cette coordonée.
     * <p>
     * La valeur du composant dépend de la valeur de parité de la colonne.
     * {@code r = row - (col - col&1)/2}.
     * </p>
     * */
    public int getR() {
    	int parity = Math.abs(getCol()) % 2;
    	return getRow() -  (getCol() - parity) / 2;
    }
    
    /**
     * Retourne la distance entre cette coordonnée et l'autre
     * en appliquant une version modifiée de la distance de Manhattan, 
     * appliquée à une grille hexagonale.
     * 
     * Pour plus d'infos : {@link https://www.redblobgames.com/grids/hexagons/#distances}
     * */
    public double distanceFrom(Coordinate other) {
    	return (Math.abs(this.getQ() - other.getQ()) + 
    	Math.abs(this.getQ() + this.getR() - other.getQ() - other.getR()) +
    	Math.abs(this.getR() - other.getR())) / 2.0;
    }

    /**
     * Retourne une nouvelle coordonnée décalée de (rowDir, colDir).
     *
     * @param rowDir le décalage à appliquer à la ligne (peut être négatif)
     * @param colDir le décalage à appliquer à la colonne (peut être négatif)
     * @return une nouvelle instance de {@code Coordinate} correspondant à (row + rowDir, col + colDir)
     */
    public Coordinate move(int rowDir, int colDir) {
        return Coordinate.coord(row + rowDir, col + colDir);
    }

    /**
     * Retourne les voisins de cette coordonnées.
     * */
	public Collection<Coordinate> getNeihgbors() {
		return this.getCol() % 2 == 0 
				? Set.of(
						this.move(-1, 0), this.move(-1, 1), this.move(0, 1), 
						this.move(1, 0), this.move(0, -1), this.move(-1, -1)) 
				: Set.of(
						this.move(-1, 0), this.move(0, 1), this.move(1, 1), 
						this.move(1, 0), this.move(1, -1), this.move(0, -1));
	}
	
	
}
