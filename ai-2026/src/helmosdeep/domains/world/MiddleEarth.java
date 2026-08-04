package helmosdeep.domains.world;

import java.util.*;

import helmosdeep.util.Contract;

/**
 * Modélise un champ de bataille sur la terre du millieu.
 * */
public class MiddleEarth implements Iterable<Coordinate> {
	private final Map<Coordinate, Tile> tiles = new HashMap<>();

	/**
	 * Crée et retourne une nouvelle instance de {@link MiddleEarth}.
	 *
	 * @return une nouvelle instance de {@link MiddleEarth}.
	 */
	public static MiddleEarth create() {
		return new MiddleEarth();
	}
	
	private MiddleEarth() {
	}

	/**
	 * Associe une tuile à une position donnée sur le champ de bataille.
	 *
	 * @param pos la position à laquelle associer la tuile, ne peut pas être {@code null}.
	 * @param tile la tuile à associer à la position, ne peut pas être {@code null}.
	 * @throws NullPointerException si {@code pos} ou {@code tile} est {@code null}.
	 * @throws IllegalArgumentException si la position est déjà associée à une tuile.
	 */
	public void put(Coordinate pos, Tile tile) {
		Objects.requireNonNull(pos, "Arg. pos != null attendu");
		Objects.requireNonNull(tile, "Arg. tile != null attendu");
		Contract.require(!tiles.containsKey(pos), "Position %s déjà associée à une tuile".formatted(pos));

		tiles.put(pos, tile);
	}

	/**
	 * Applique le {@link TileConsumer} donné à chacune des tuiles du champ de bataille,
	 * accompagnées de leur position respective.
	 *
	 * @param tileConsumer le consommateur à appliquer à chaque tuile, ne doit pas être {@code null}.
	 * @throws NullPointerException si {@code tileConsumer} est {@code null}.
	 */
	public void applyToEachTile(TileConsumer tileConsumer) {
		Objects.requireNonNull(tileConsumer, "Arg. tileConsumer != null attendu");
		for(var entry : tiles.entrySet()) {
			tileConsumer.accept(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Retourne la tuile associée à la position donnée.
	 *
	 * @param pos la position dont on veut connaître la tuile, ne peut pas être {@code null}.
	 * @return la tuile associée à {@code pos}, ou {@code Tile.UNKNWOWN} si aucune tuile
	 *         n'est associée à cette position.
	 * @throws NullPointerException si {@code pos} est {@code null}.
	 */
	public Tile getTileAt(Coordinate pos) {
		Objects.requireNonNull(pos);
		
		return this.tiles.getOrDefault(pos, Tile.UNKNWOWN);
	}

	/**
	 * Vérifie si l'argument existe sur cette terre du milieu, le cas échéant, retourne cet argument.
	 * <p>
	 * Si l'argument n'existe pas, la méthode retourne {@code Coordinate.NONE}.
	 * */
	public Coordinate checkTileExistsAt(Coordinate pos) {
		return this.tiles.containsKey(pos) ? pos : Coordinate.NONE;
	}

	/**
	 * Calcule récursivement le coût minimal pour se déplacer de {@code source} vers
	 * {@code destination} en disposant d'un capital de mouvement initial de {@code movement},
	 * en ne progressant qu'à travers des tuiles existantes et en se rapprochant à chaque
	 * étape de la destination.
	 *
	 * @param movement le mouvement disponible pour effectuer le déplacement ; si négatif,
	 *        le déplacement est considéré comme impossible.
	 * @param source la position de départ, doit faire partie de la carte.
	 * @param destination la position d'arrivée, doit faire partie de la carte.
	 * @param unit l'unité qui se déplace de la position source -> destination.
	 * @return le coût minimal du déplacement de {@code source} vers {@code destination},
	 *         ou {@code Integer.MAX_VALUE} si le déplacement est impossible avec le
	 *         mouvement disponible.
	 * @throws IllegalArgumentException si {@code source} ou {@code destination} ne fait
	 *         pas partie de la carte.
	 */
	public int computeMoveCostFor(int movement, Coordinate source, Coordinate destination, Unit unit) {
		Contract.require(checkTileExistsAt(source) != Coordinate.NONE,
				"%s ne fait pas partie de la carte".formatted(source));
		Contract.require(checkTileExistsAt(destination) != Coordinate.NONE,
				"%s ne fait pas partie de la carte".formatted(destination));
		
		if(movement < 0) {
			return Integer.MAX_VALUE;
		}
		
		if(source.equals(destination)) {
			return 0;
		}
		
		var distanceFromSource = source.distanceFrom(destination);
		var minCost = Integer.MAX_VALUE;

		var neighbors = filter(source.getNeihgbors());
		
		for(var n : neighbors) {
			if(n.distanceFrom(destination) <= distanceFromSource) {
				//on récupère le cout de la tuile selon les options choisie
				int tileCost = unit.getMoveCostFor(getTileAt(n));
				int movementLeft = movement - tileCost; // Garantit la fin de l'appel récursif
				int cost = computeMoveCostFor(movementLeft, n, destination, unit);
				minCost = min(minCost, cost, tileCost);
			}
		}
		
		return minCost;
	}
	
	/**
	 * Filtre les coordonnées candidates pour ne conserver que celles qui correspondent
	 * effectivement à une tuile existante sur ce champ de bataille.
	 *
	 * @param candidates les coordonnées à filtrer.
	 * @return l'ensemble des coordonnées de {@code candidates} qui possèdent une tuile
	 *         associée sur ce champ de bataille.
	 */
	private Collection<Coordinate> filter(Collection<Coordinate> candidates) {
		var result = new HashSet<Coordinate>();
		
		for(var c : candidates) {
			if(checkTileExistsAt(c) != Coordinate.NONE) {
				result.add(c);
			}
		}
		
		return result;
	}

	/**
	 * Retourne le plus petit coût entre {@code current} et {@code candidate + dCost},
	 * en écartant {@code candidate} s'il vaut {@code Integer.MAX_VALUE} (déplacement
	 * impossible).
	 *
	 * @param current le coût courant, considéré comme minimal jusqu'ici.
	 * @param candidate le coût candidat, avant ajout de {@code dCost}.
	 * @param dCost le coût additionnel à ajouter à {@code candidate} pour la comparaison.
	 * @return le plus petit des deux coûts, en tenant compte du cas où {@code candidate}
	 *         représente un déplacement impossible.
	 */
	private int min(int current, int candidate, int dCost) {
		if(candidate != Integer.MAX_VALUE && candidate + dCost < current) {
			return candidate + dCost;
		}
		return current;
	}

	/**
	 * Retourne un itérateur non modifiable sur l'ensemble des positions du champ de
	 * bataille possédant une tuile associée.
	 *
	 * @return un itérateur sur les positions de ce champ de bataille.
	 */
	@Override
	public Iterator<Coordinate> iterator() {
		return Collections.unmodifiableCollection(this.tiles.keySet()).iterator();
	}
	
}