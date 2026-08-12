package helmosdeep.domains.world;

/**
 * Permet de consommer les unités d'une collection
 * */
public interface UnitConsumer {
	/**
	 * Méthod appelée pour couple (coordinate, unité) 
	 * */
	void accept(Coordinate coord, Unit unit);
}
