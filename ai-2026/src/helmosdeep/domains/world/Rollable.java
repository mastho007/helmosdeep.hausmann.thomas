package helmosdeep.domains.world;

/**
 * Décrit un mécanisme de lancer de dé.
 *
 * <p>Les implémentations peuvent reposer sur un tirage réellement aléatoire
 * ou sur toute autre stratégie (par exemple déterministe, comme {@link FibDice}),
 * du moment que le contrat de {@link #roll()} est respecté.
 */
public interface Rollable {

	/**
	 * Simule un lancer de dé à six faces.
	 *
	 * @return une valeur entière comprise entre 1 et 6 (inclus)
	 */
	int roll();
}