package helmosdeep.domains.world;

import java.util.*;

import helmosdeep.util.Contract;

/**
 * Regroupe l'ensemble des armées présentes sur un champ de bataille et centralise
 * les opérations qui les concernent : enrôlement d'unités, recherche d'une unité
 * par position, et parcours de toutes les unités toutes armées confondues.
 */
public class ArmyList implements Iterable<Army> {
	private final Map<Belligerent, Army> armies = new LinkedHashMap<Belligerent, Army>();

	/**
	 * Crée une nouvelle liste d'armées à partir des armées fournies.
	 *
	 * @param armies les armées à regrouper, ne peut pas être {@code null} ni contenir
	 *        de valeur {@code null}.
	 * @throws NullPointerException si {@code armies} ou l'un de ses éléments est {@code null}.
	 */
	public ArmyList(Army... armies) {
		Objects.requireNonNull(armies, "Arg. armies != null attendu");
		for(var army : armies) {
			Objects.requireNonNull(army, "Aucune armée ne peut être null");
			this.armies.put(army.getBelligerent(), army);
		}
	}

	/**
	 * Positionne une unité appartenant à un belligérant donné sur une position du champ
	 * de bataille, au sein de l'armée de cette liste correspondant à ce belligérant.
	 *
	 * @param pos la position à laquelle positionner l'unité, ne peut pas être {@code null}.
	 * @param unit l'unité à positionner, ne peut pas être {@code null}.
	 * @param belligerent le belligérant auquel appartient l'unité, ne peut pas être {@code null}.
	 * @throws NullPointerException si {@code pos}, {@code unit} ou {@code belligerent} est {@code null}.
	 * @throws IllegalArgumentException si aucune armée de cette liste n'appartient à {@code belligerent},
	 *         ou si une unité (de n'importe quelle armée) est déjà positionnée à cette position.
	 */
	public void enroll(Coordinate pos, Unit unit, Belligerent belligerent) {
		Objects.requireNonNull(pos, "Arg. pos != null attendu");
		Objects.requireNonNull(unit, "Arg. unit != null attendu");
		Objects.requireNonNull(belligerent, "Arg. belligerent != null attendu");
		Contract.require(armies.containsKey(belligerent), "Arg. belligent absent de cette liste");
		
		for(var army : armies.values()) {
			Contract.require(army.getUnitAt(pos).isEmpty(), "Une unité est déjà positionnée à cet endroit");
		}

		armies.get(belligerent).enroll(pos, unit);
	}

	/**
	 * Applique le {@link UnitConsumer} donné à chacune des unités de chaque armée de
	 * cette liste.
	 * <p>
	 * L'ordre de parcours des armées, ainsi que celui des unités au sein d'une même armée,
	 * n'est pas garanti.
	 *
	 * @param unitConsumer le consommateur à appliquer à chaque unité, ne doit pas être {@code null}.
	 * @throws NullPointerException si {@code unitConsumer} est {@code null}.
	 */
	public void applyToEachUnit(UnitConsumer unitConsumer) {
		Objects.requireNonNull(unitConsumer, "Arg. unitConsumer != null attendu");
		for(var army : armies.values()) {
			army.applyToEachUnit(unitConsumer);
		}
	}

	/**
	 * Retourne l'unité présente à la position donnée, toutes armées confondues.
	 *
	 * @param pos la position à consulter, ne peut pas être {@code null}.
	 * @return l'unité présente à {@code pos}, ou {@link Optional#empty()} si aucune unité
	 *         (de n'importe quelle armée) ne s'y trouve.
	 */
	public Optional<Unit> getUnitAt(Coordinate pos) {		
		for(var army : armies.values()) {
			var maybeUnit = army.getUnitAt(pos);
			if(maybeUnit.isPresent()) {
				return maybeUnit;
			}
		}
		return Optional.empty();
	}

	/**
	 * Retourne un itérateur sur les armées de cette liste.
	 *
	 * @return un itérateur sur les armées.
	 */
	@Override
	public Iterator<Army> iterator() {
		return Collections.unmodifiableCollection(armies.values()).iterator();
	}

	/**
	 * Retourne le nombre d'armée composant cette collection.
	 */
	public int size() {
		return armies.size();
	}

	/**
	 * Retourne l'armée de cette liste située à l'index donné.
	 *
	 * @param pos l'index de l'armée recherchée, doit être compris entre {@code 0}
	 *        (inclus) et {@link #size()} (exclu).
	 * @return l'armée située à l'index {@code pos}.
	 * @throws IllegalArgumentException si {@code pos} n'est pas compris entre
	 *         {@code 0} (inclus) et {@link #size()} (exclu).
	 */
	public Army get(int pos) {
		Contract.require(0 <= pos && pos < size(), "Arg. pos in [0; size()[ attendu. Recu "+pos);

		return List.copyOf(armies.values()).get(pos);
	}
}
