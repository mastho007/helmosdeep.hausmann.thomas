package helmosdeep.domains.world;

/**
 * Permet de consommer les tuiles d'une collection
 * */
public interface TileConsumer {
	/**
	 * Méthode appelée pour chaque couple (coordinate, tile) 
	 * */
	void accept(Coordinate coord, Tile tile);
}
