package helmosdeep.domains.turns;

import java.util.*;
import helmosdeep.domains.world.*;

/**
 * Représente une séquence de tours de jeu.
 * <p>
 * Cette classe gère une liste ordonnée de {@link Turn}, permettant de démarrer
 * de nouveaux tours, de terminer le tour en cours, et de filtrer les tours
 * selon l'armée active. Elle implémente {@link Iterable} afin de pouvoir
 * parcourir les tours qu'elle contient.
 */
public final class TurnSequence implements Iterable<Turn> {
	private final List<Turn> turns;

	/**
	 * Crée une nouvelle séquence de tours à partir de deux armées et démarre
	 * immédiatement le premier tour.
	 *
	 * @param active l'armée active pour le premier tour, non nulle
	 * @param other l'armée adverse pour le premier tour, non nulle
	 * @return une nouvelle séquence de tours contenant un premier tour démarré
	 * @throws NullPointerException si {@code active} ou {@code other} est nul
	 */
	public static TurnSequence fromArmies(Army active, Army other) {
		Objects.requireNonNull(active, "Arg. active != null attendu");
		Objects.requireNonNull(other, "Arg. other != null attendu");
		
		var sequence = new TurnSequence();
		sequence.startNewTurn(active, other);
		return sequence;
	}

	private TurnSequence() {
		turns = new ArrayList<>();
	}

	/**
	 * Construit une séquence de tours à partir d'une liste existante de tours.
	 * <p>
	 * La liste fournie est rendue non modifiable. En conséquence, cette séquence est immuable.
	 *
	 * @param turns la liste des tours à utiliser
	 */
	private TurnSequence(List<Turn> turns) {
		this.turns = Collections.unmodifiableList(turns);
	}

	/**
	 * Termine le dernier tour de la séquence, s'il en existe un.
	 * <p>
	 * Si la séquence est vide, cette méthode ne fait rien.
	 *
	 * @param coordSource la source de coordonnées utilisée pour terminer le tour, non nulle
	 * @throws NullPointerException si {@code coordSource} est nul
	 */
	public void endLastTurn(Iterable<Coordinate> coordSource) {
		Objects.requireNonNull(coordSource, "Arg.coordSource != null attendu");
		
		if(!this.turns.isEmpty()) {
			this.turns.getLast().terminate(coordSource);
		}
	}

	/**
	 * Démarre un nouveau tour et l'ajoute à la séquence.
	 * <p>
	 * L'identifiant du nouveau tour correspond au nombre de tours déjà
	 * présents dans la séquence, incrémenté de un.
	 *
	 * @param active l'armée active pour ce nouveau tour, non nulle
	 * @param other l'armée adverse pour ce nouveau tour, non nulle
	 * @throws NullPointerException si {@code active} ou {@code other} est nul
	 */
	public void startNewTurn(Army active, Army other) {
		Objects.requireNonNull(active);
		Objects.requireNonNull(other);
		
		int id = this.turns.size() + 1;
		this.turns.add(new Turn(id, active, other));
	}

	/**
	 * Filtre les tours de cette séquence pour ne conserver que ceux où
	 * l'armée donnée est active.
	 *
	 * @param army l'armée servant de critère de filtrage
	 * @return une nouvelle séquence de tours contenant uniquement les tours
	 *         où {@code army} est active
	 */
	public TurnSequence filterByArmy(Army army) {
		List<Turn> result = new ArrayList<Turn>();
		for(var turn : turns) {
			if(turn.isActive(army)) {
				result.add(turn);
			}
		}
		
		return new TurnSequence(result);
	}

	/**
	 * Retourne un itérateur sur les tours de cette séquence.
	 * <p>
	 * L'itérateur renvoyé ne permet pas de modifier la collection sous-jacente.
	 *
	 * @return un itérateur non modifiable sur les tours de la séquence
	 */
	@Override
	public Iterator<Turn> iterator() {
		return Collections.unmodifiableCollection(turns).iterator();
	}

	/**
	 * Retourne le nombre de tours contenus dans cette séquence.
	 *
	 * @return le nombre de tours de la séquence
	 */
	public int count() {
		return turns.size();
	}
	
}