package helmosdeep.domains.turns;

import java.util.*;

import helmosdeep.domains.world.*;

/**
 * Représente un tour de jeu, durant lequel une armée ({@code active}) agit face
 * à une armée adverse ({@code other}) sur le champ de bataille.
 * <p>
 * Un {@code Turn} mémorise l'état des forces en présence au début du tour
 * (via {@code ennemiesAliveAtStart}), puis, une fois le tour terminé
 * (voir {@link #terminate(Iterable)}), calcule deux indicateurs :
 * <ul>
 *   <li>le nombre d'unités ennemies tuées durant le tour ;</li>
 *   <li>la taille de la zone d'influence de l'armée active, c'est-à-dire le
 *       nombre de ses unités encore en vie augmenté du nombre de cases libres
 *       sous son contrôle.</li>
 * </ul>
 * Ces indicateurs permettent de comparer deux tours entre eux pour
 * déterminer, par exemple, le tour le plus meurtrier ou le plus
 * favorable en termes d'influence.
 * <p>
 * Une instance de {@code Turn} est immuable quant à son identité (armées
 * concernées et identifiant de tour), mais ses indicateurs de résultat
 * ({@code ennemiesKilled} et {@code influenceZoneSize}) ne sont renseignés
 * qu'après l'appel à {@link #terminate(Iterable)} ; avant cet appel, ils
 * valent {@code 0}.
 */
public final class Turn {
	private final int id;
	private final Army active;
	private final Army other;
	private final int ennemiesAliveAtStart;

	private int ennemiesKilled;
	private int influenceZoneSize;

	/**
	 * Crée un nouveau tour d'identifiant {@code 0}, opposant l'armée active
	 * à l'armée adverse.
	 *
	 * @param active l'armée dont c'est le tour de jouer, ne peut pas être {@code null}.
	 * @param other l'armée adverse, ne peut pas être {@code null}.
	 * @throws NullPointerException si {@code active} ou {@code other} est {@code null}.
	 */
	Turn(Army active, Army other) {
		this(0, active, other);
	}

	/**
	 * Crée un nouveau tour identifié par {@code turnId}, opposant l'armée active
	 * à l'armée adverse.
	 * <p>
	 * Le nombre d'unités ennemies en vie au début du tour est enregistré à la
	 * création, afin de pouvoir déterminer plus tard, lors de l'appel à
	 * {@link #terminate(Iterable)}, combien d'unités ont été tuées durant ce tour.
	 *
	 * @param turnId l'identifiant de ce tour.
	 * @param active l'armée dont c'est le tour de jouer, ne peut pas être {@code null}.
	 * @param other l'armée adverse, ne peut pas être {@code null}.
	 * @throws NullPointerException si {@code active} ou {@code other} est {@code null}.
	 */
	Turn(int turnId, Army active, Army other) {
		this.id = turnId;
		this.active = Objects.requireNonNull(active);
		this.other = Objects.requireNonNull(other);

		this.ennemiesAliveAtStart = this.other.getUnitsAlive();
	}

	/**
	 * Compte, parmi les coordonnées données, le nombre de cases libres considérées
	 * comme faisant partie de la zone d'influence de l'armée active.
	 * <p>
	 * Une case est comptabilisée si elle n'est occupée par aucune unité (ni de
	 * l'armée active, ni de l'armée adverse), si elle se trouve à proximité d'au
	 * moins une unité de l'armée active, et si elle ne se trouve pas à proximité
	 * d'une unité de l'armée adverse.
	 *
	 * @param coordinates les coordonnées à examiner, ne peut pas être {@code null}.
	 * @return le nombre de cases libres appartenant à la zone d'influence de
	 *         l'armée active.
	 */
	private int countFreeTiles(Iterable<Coordinate> coordinates) {
		int total = 0;

		for(Coordinate c : coordinates) {
			if( active.getUnitAt(c).isEmpty()
				&& other.getUnitAt(c).isEmpty()
				&& active.hasUnitCloseTo(c)
				&& !other.hasUnitCloseTo(c)) {
				total += 1;
			}
		}

		return total;
	}

	/**
	 * Retourne l'identifiant de ce tour.
	 *
	 * @return l'identifiant de ce tour.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Retourne le nombre d'ennemi tué à l'instant T.
	 * */
	public int getEnnemiesKilled() {
		return ennemiesKilled;
	}

	/**
	 * Retourne la taille de la zone d'influence de l'armée active pour ce tour,
	 * telle que calculée lors de l'appel à {@link #terminate(Iterable)}.
	 * <p>
	 * Cette valeur vaut {@code 0} tant que le tour n'a pas été terminé.
	 *
	 * @return la taille de la zone d'influence de l'armée active.
	 */
	public int getInfluenceZoneSize() {
		return this.influenceZoneSize;
	}

	/**
	 * Indique si l'armée donnée est l'armée active de ce tour.
	 *
	 * @param army l'armée à comparer à l'armée active de ce tour.
	 * @return {@code true} si {@code army} est l'armée active de ce tour,
	 *         {@code false} sinon.
	 */
	public boolean isActive(Army army) {
		return this.active.equals(army);
	}

	/**
	 * Indique si ce tour est plus meurtrier que le tour donné, c'est-à-dire si
	 * le nombre d'ennemis tués durant ce tour est strictement supérieur à celui
	 * de l'autre tour.
	 * <p>
	 * Ce tour est considéré comme plus meurtrier si {@code other} est
	 * {@link Optional#empty() vide}, c'est-à-dire s'il n'y a pas de tour auquel
	 * se comparer.
	 *
	 * @param other le tour auquel comparer ce tour, ne peut pas être {@code null}
	 *        (mais peut être {@link Optional#empty()}).
	 * @return {@code true} si ce tour est plus meurtrier que {@code other} (ou si
	 *         {@code other} est vide), {@code false} sinon.
	 */
	public boolean isDeadlierThan(Optional<Turn> other) {
		return other.isEmpty()
				|| this.getEnnemiesKilled() > other.get().getEnnemiesKilled();
	}

	/**
	 * Indique si ce tour a au moins autant d'influence que le tour donné,
	 * c'est-à-dire si la taille de sa zone d'influence est supérieure ou égale
	 * à celle de l'autre tour.
	 * <p>
	 * Ce tour est considéré comme ayant plus d'influence si {@code other} est
	 * {@link Optional#empty() vide}, c'est-à-dire s'il n'y a pas de tour auquel
	 * se comparer.
	 *
	 * @param other le tour auquel comparer ce tour, ne peut pas être {@code null}
	 *        (mais peut être {@link Optional#empty()}).
	 * @return {@code true} si la zone d'influence de ce tour est supérieure ou
	 *         égale à celle de {@code other} (ou si {@code other} est vide),
	 *         {@code false} sinon.
	 */
	public boolean hasMoreInfluenceThan(Optional<Turn> other) {
		return other.isEmpty()
				|| this.getInfluenceZoneSize() >= other.get().getInfluenceZoneSize();
	}

	/**
	 * Termine ce tour et calcule ses indicateurs de résultat : le nombre
	 * d'unités ennemies tuées durant le tour, ainsi que la taille de la zone
	 * d'influence de l'armée active.
	 * <p>
	 * Le nombre d'ennemis tués correspond à la différence entre le nombre
	 * d'unités ennemies en vie au début du tour et leur nombre à la fin du tour.
	 * La taille de la zone d'influence correspond au nombre d'unités encore en
	 * vie dans l'armée active, augmenté du nombre de cases libres, parmi
	 * {@code coordinates}, qui lui sont proches et qui ne sont pas proches de
	 * l'armée adverse (voir {@link #countFreeTiles(Iterable)}).
	 *
	 * @param coordinates l'ensemble des coordonnées du champ de bataille à
	 *        prendre en compte pour le calcul de la zone d'influence, ne peut
	 *        pas être {@code null}.
	 */
	public void terminate(Iterable<Coordinate> coordinates) {
		this.ennemiesKilled = this.ennemiesAliveAtStart - this.other.getUnitsAlive();

		this.influenceZoneSize = this.active.getUnitsAlive() + countFreeTiles(coordinates);
	}

}