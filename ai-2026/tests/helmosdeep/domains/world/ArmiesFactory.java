package helmosdeep.domains.world;

import static helmosdeep.domains.world.Coordinate.coord;

/**
 * Fournit des armées factices pour les tests unitaires.
 *
 * <p>Cette classe utilitaire ne peut pas être instanciée : elle expose
 * uniquement des méthodes statiques de fabrication d'armées.</p>
 */
public final class ArmiesFactory {

	private ArmiesFactory() {		
	}
	
	/**
	 * Crée l'armée du Mordor, composée de Sauron (général), d'un groupe
	 * d'Orcs (unité moyenne) et de Wargs (unité légère).
	 *
	 * @return une nouvelle {@link Army} représentant le camp du Mordor,
	 *         avec ses unités enrôlées à leurs positions de départ
	 */
	public static Army createMordor() {
		var mordor = Army.of(Belligerent.MORDOR);

		mordor.enroll(coord(0,0), new Unit("Sauron", UnitType.GENERAL));
		mordor.enroll(coord(1,0), new Unit("Orcs", UnitType.AVERAGE));
		mordor.enroll(coord(0,1), new Unit("Wargs", UnitType.LIGHT));

		return mordor;
	}

	/**
	 * Crée l'armée des Hommes, composée d'Aragorn (général) et d'un groupe
	 * de Gondoriens (unité moyenne).
	 *
	 * @return une nouvelle {@link Army} représentant le camp des Hommes,
	 *         avec ses unités enrôlées à leurs positions de départ
	 */
	public static Army createMankind() {
		var mankind = Army.of(Belligerent.MANKIND);

		mankind.enroll(coord(2,2), new Unit("Aragorn", UnitType.GENERAL));
		mankind.enroll(coord(1,2), new Unit("Gondoriens", UnitType.AVERAGE));

		return mankind;
	}
}