package helmosdeep.domains.world;

import helmosdeep.util.Contract;

/**
 * Représente une réserve de points de mouvement.
 * <p>
 * Une réserve possède deux propriétés :
 * <ul>
 *   <li>une <b>capacité brute</b> ({@code rawCapacity}), valeur flottante qui peut être
 *       augmentée progressivement par petits incréments (par exemple via des bonus
 *       cumulatifs) ;</li>
 *   <li>un nombre de <b>points de mouvement actuellement disponibles</b>
 *       ({@code actual}), valeur entière, obtenue en tronquant la capacité brute, et qui
 *       diminue au fur et à mesure des déplacements effectués.</li>
 * </ul>
 * La capacité brute ne diminue jamais : elle ne fait que croître via
 * {@link #raiseCapacity(float)}. Le nombre de points actuellement disponibles, quant à
 * lui, diminue via {@link #subtract(int)} et peut être ramené à son maximum courant
 * (la capacité brute tronquée) via {@link #refill()}.
 * */
final class MvtPool {
	private float rawCapacity;
	private int actual;

	/**
	 * Crée une réserve de points de mouvement dont la capacité initiale (brute et
	 * actuelle) vaut {@code capacity}.
	 *
	 * <p><b>Précondition :</b> {@code capacity >= 0}.
	 *
	 * @param capacity la capacité initiale de la réserve
	 * @return une nouvelle réserve entièrement remplie à hauteur de {@code capacity}
	 * @throws IllegalArgumentException si {@code capacity < 0}
	 * */
	static MvtPool create(int capacity) {
		Contract.require(capacity >= 0, "Arg. capacity >= 0 attendu");
		return new MvtPool(capacity);
	}

	/**
	 * Construit une réserve à partir d'une capacité déjà validée par {@link #create(int)}.
	 *
	 * @param capacity la capacité initiale (brute et actuelle) de la réserve
	 * */
	private MvtPool(int capacity) {
		this.rawCapacity = capacity;
		this.actual = capacity;
	}

	/**
	 * Augmente la capacité brute de la réserve de {@code increment}, puis réaligne le
	 * nombre de points actuellement disponibles sur cette nouvelle capacité (tronquée à
	 * l'entier inférieur).
	 *
	 * <p><b>Précondition :</b> {@code increment > 0}.
	 *
	 * <p><b>Attention :</b> {@link #getActual()} n'est pas simplement incrémenté de
	 * {@code increment} : il est recalculé comme la partie entière de la nouvelle
	 * {@link #rawCapacity}. Autrement dit, cet appel a pour effet de reconstituer les
	 * points de mouvement actuellement disponibles au niveau du nouveau maximum (tronqué),
	 * quels que soient les points déjà consommés depuis le dernier
	 * {@link #refill()}.
	 *
	 * @param increment le montant, strictement positif, à ajouter à la capacité brute
	 * @throws IllegalArgumentException si {@code increment <= 0}
	 * */
	void raiseCapacity(float increment) {
		Contract.require(increment > 0, "Arg. increment > 0 attendu");
		rawCapacity += increment;
		this.actual = (int)this.rawCapacity;
	}

	/**
	 * Retourne le nombre de points de mouvement actuellement disponibles dans la réserve.
	 *
	 * @return le nombre de points de mouvement actuellement disponibles (toujours
	 *         positif ou nul)
	 * */
	int getActual() {
		return actual;
	}

	/**
	 * Consomme {@code consumed} points de mouvement parmi ceux actuellement disponibles.
	 *
	 * <p><b>Précondition :</b> {@code consumed} ne doit pas excéder le nombre de points
	 * actuellement disponibles, c'est-à-dire {@code getActual() >= consumed}. Cette
	 * précondition n'exclut pas explicitement les valeurs négatives de {@code consumed} ;
	 * seule la comparaison avec {@link #getActual()} est vérifiée.
	 *
	 * @param consumed le nombre de points de mouvement à retirer de la réserve
	 * @throws IllegalArgumentException si {@code consumed > getActual()}
	 * */
	public void subtract(int consumed) {
		Contract.require(actual >= consumed,
				"mvtConsumed <= aux points de mvt actuels attendu. Reçu mvt actuel: %d, mvt à consommer : %d".formatted(actual, consumed));
		actual -= consumed;
	}

	/**
	 * Réinitialise le nombre de points de mouvement actuellement disponibles à son
	 * maximum courant, c'est-à-dire la partie entière de la capacité brute
	 * ({@link #rawCapacity}). La capacité brute elle-même n'est pas modifiée.
	 *
	 * <p>Typiquement destinée à être appelée en début de tour, pour redonner à l'unité
	 * associée l'intégralité de ses points de mouvement.
	 * */
	public void refill() {
		this.actual = (int)this.rawCapacity;

	}

}
