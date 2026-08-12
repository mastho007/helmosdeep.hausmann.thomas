package helmosdeep.domains.world;

/**
 * Implémentation déterministe d'un dé à six faces, basée sur une suite de type
 * Fibonacci plutôt que sur un générateur aléatoire.
 *
 * <p>Cette implémentation ne fait appel à aucune source d'aléa : pour une instance
 * donnée, la suite de valeurs retournées par {@link #roll()} est entièrement
 * déterministe et reproductible. Elle est donc particulièrement adaptée aux tests
 * unitaires ou aux scénarios nécessitant des résultats prévisibles, en lieu et
 * place d'un dé véritablement aléatoire.
 *
 * <p>Le fonctionnement interne repose sur une suite proche de Fibonacci
 * ({@code last} et {@code beforeLast}), initialisée à (1, 1) :
 * <ul>
 *   <li>les deux premiers appels à {@link #roll()} retournent chacun {@code last + 1}
 *       (soit {@code 2}), sans faire progresser la suite ;</li>
 *   <li>à partir du troisième appel, la suite progresse à chaque tirage
 *       ({@code last} devient {@code last + beforeLast}), et la valeur retournée est
 *       {@code 1 + (last % 6)}, ramenant ainsi le résultat dans l'intervalle
 *       {@code [1, 6]}, comme un dé à six faces classique.</li>
 * </ul>
 */
public final class FibDice implements Rollable {

	private static final int TWO = 2;

	/** Dernier terme calculé de la suite. */
	private int last = 1;

	/** Terme précédant {@code last} dans la suite. */
	private int beforeLast = 1;

	/** Nombre de fois où {@link #roll()} a été appelée sur cette instance. */
	private int rollCount = 0;

	/**
	 * Simule un lancer de dé et retourne une valeur comprise entre 1 et 6.
	 *
	 * <p>Le résultat n'est pas aléatoire : il dépend uniquement du nombre d'appels
	 * précédents à cette méthode sur la même instance, selon la progression
	 * décrite dans la documentation de la classe.
	 *
	 * @return une valeur entière comprise entre 1 et 6 (inclus)
	 */
	@Override
	public int roll() {
		++rollCount;
		
		if(rollCount <= TWO) {
			return last + 1;
		} else {
			int oldLast = last;
			last = last + beforeLast;
			beforeLast = oldLast;

			return 1 + (last % 6);
		}
	}
}