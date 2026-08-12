package helmosdeep.domains.commons;

/**
 * Représente un objet capable de résoudre les jetons (placeholders) présents
 * dans un gabarit textuel en les remplaçant par ses propres données.
 * <p>
 * Un jeton est une sous-chaîne délimitée par des deux-points, par exemple
 * {@code :name:} ou {@code :mvt:}. L'implémentation est responsable de
 * définir les jetons reconnus et les valeurs correspondantes.
 * Les jetons non reconnus par l'implémentation sont laissés inchangés dans
 * le résultat, ce qui permet de chaîner plusieurs appels à {@link #resolve(String)}
 * sur des objets différents afin de résoudre progressivement un même gabarit.
 *
 * <p><b>Exemple :</b>
 * <pre>{@code
 * String template = "Position actuelle : :currentPos: - Armée : :currentArmy:";
 * String result = battle.resolve(template);
 * }</pre>
 */
public interface TemplateResolvable {

	/**
	 * Résout les jetons connus de cette instance dans le gabarit fourni et
	 * retourne le texte obtenu après substitution.
	 * <p>
	 * Les jetons non reconnus par cette implémentation sont conservés tels
	 * quels dans la chaîne retournée (aucune exception n'est levée).
	 *
	 * @param template le texte contenant, le cas échéant, un ou plusieurs
	 *                 jetons à remplacer (ex. {@code ":name:"}, {@code ":mvt:"})	 *                 
	 * @return le template avec les jetons connus remplacés par leur valeur
	 * @throws IllegalArgumentException si le texte est blanc.
	 */
	String resolve(String template);
}