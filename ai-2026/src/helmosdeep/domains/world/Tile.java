package helmosdeep.domains.world;

/**
 * Représente les différents types de terrain pouvant composer une case du plateau.
 *
 * <p>Chaque type de terrain possède un nom destiné à l'affichage ainsi qu'un
 * coût de déplacement, utilisé pour déterminer le nombre de points de mouvement
 * nécessaires pour qu'une unité puisse traverser une case de ce type.
 */
public enum Tile {

	/** Plaine : terrain dégagé, peu coûteux à traverser. */
	LOWLAND("Plaine", 1),
	/** Forêt : terrain plus difficile à traverser qu'une plaine. */
	FOREST("Foret", 2),
	/** Montagne : terrain le plus coûteux à traverser. */
	MOUNTAIN("Montagne",3),
	/** Terrain inconnu, utilisé par défaut lorsque le type de la case n'est pas défini. */
	UNKNWOWN("?", 0);

	private final int cost;

	private final String name;

	Tile(String name, int cost) {
		this.name = name;
		this.cost = cost;
	}

	/**
	 * Retourne une représentation lisible de ce type de terrain, sous la forme
	 * {@code "Nom[cout]"} (par exemple {@code "Foret[2]"}).
	 */
	@Override
	public String toString() {
		return "%s".formatted(this.name, this.cost);
	}

	/**
	 * Retourne le coût de déplacement associé à ce type de terrain.
	 */
	public int getCost() {
		return cost;
	}
}