package helmosdeep.domains.world;

import java.util.Objects;

import helmosdeep.util.Contract;

/**
 * Représente une unité militaire pouvant enrollable dans une armée et déployée
 * sur le plateau.<br>
 *
 * Une unité possède :
 * <ul>
 * <li>Une unité possède un nom ({@code name}).</li>
 * <li>Un type ({@link UnitType}) déterminant sa force ({@code str}) et sa
 * mobilité potentielle ({@code mvt}).</li>
 * <li>Un état mutable propre à chaque tour de jeu : si elle a déjà effectué un
 * déplacement ({@code moved}).</li>
 * <li>Une puissance d'attaque courante ({@code pow}), fixée lors de la
 * résolution d'un combat..</li>
 * <li>Une stratégie propre qui détermine sont comportement dans le jeu selon
 * les options du jeu choisies..</li>
 * <li>L'unité contient le nombre de points de mouvement consommé par l'unité au
 * cour d'un tour courant ({@code mvtPointsConsumed}).</li>
 * </ul>
 *
 * Cet état ({@code moved} et {@code pow}) est remis à zéro en début de tour via
 * {@link #resetState()}. <br>
 * <br>
 * 
 * <strong>It-4-q2 postcondition de la méthode
 * ({@code getMoveCostFor(Tile tile)}):</strong>
 * 
 * <ol>
 * <li>Postcondition formelle :</li>
 * 
 * 
 * <ul>
 * <li>Si l'unité utilise la stratégie spéciale
 * ({@code SpecialHeavyMovementStrategy}) :</li>
 * 
 * Le coût de déplacement est réduit à 1, quel que soit le type de terrain
 * ciblé.
 * 
 * <li>Dans tous les autres types de stratégies :</li>
 * 
 * le cout est déterminé comme {@code cost = tile.getCost()}, le coût retourné
 * est égal au coût du type de tuile de destination.
 * 
 * </ul>
 * 
 * 
 * <li>Invariant et borne du resultat :</li>
 * 
 * En plus de la logique de stratégie, la postcondition vérifie ces propriétés :
 * <ul>
 * <li>Strictement positif ({@code cost >= 1}):</li>
 * 
 * Déplacer une unité consomme au minimum 1 point de mouvement, un cout nul ou
 * négatif est impossible.
 * 
 * <li>Intervalle de coût ({@code 1 <= cost <= tile.getCost()}):</li>
 * 
 * le coût calculé ne peut jamais dépasser le coût standard de la tuile, ni être
 * inférieur à 1.
 * 
 * <li>Consistance:</li> L'état de l'unité et l'état de la tuile ne sont pas
 * modifiés par cet appel.
 * </ul>
 * 
 * 
 * </ol>
 * 
 */
public final class Unit {

	/**
	 * Unité représentant l'absence d'unité connue ou valide, utilisée comme valeur
	 * par défaut ou de repli.
	 */
	public static final Unit UNKNOWN = new Unit("?", UnitType.UNKNOWN, new StandardLightMovementStrategy());

	private final UnitType type;
	private final String name;
	private boolean moved;
	private int pow;

	// on déclare les points consommé pour l'unité propre
	private int mvtPointsConsumed;
	// l'interface qui vas gérer les options choisie du jeu
	private final MovementStrategy strategy;

	/**
	 * Crée une nouvelle unité avec le nom et le type donnés.
	 *
	 * <p>
	 * L'unité est créée dans son état initial : elle n'a pas encore bougé
	 * ({@code moved == false}) et sa puissance est nulle ({@code pow == 0}).
	 *
	 * @param name     le nom de l'unité
	 * @param type     le type de l'unité, déterminant sa force et sa mobilité
	 * @param strategy concerne les options choisie par l'utilisateur à appliquer
	 * @throws NullPointerException si {@code name} ou {@code type} est {@code null}
	 */
	public Unit(String name, UnitType type, MovementStrategy strategy) {
		this.type = Objects.requireNonNull(type, "Arg. type != null attendu");
		this.name = Objects.requireNonNull(name, "Arg. name != null attendu");
		this.strategy = Objects.requireNonNull(strategy, "Arg. strategy != null attendu");
		this.moved = false;
		this.pow = 0;
		this.mvtPointsConsumed = 0;
	}

	/**
	 * Vérifie si cette unité est du type donné.
	 *
	 * @param type le type à comparer
	 * @return {@code true} si le type de cette unité est {@code type}
	 */
	public boolean hasType(UnitType type) {
		return this.type == type;
	}

	/**
	 * Retourne le nom de cette unité.
	 *
	 * @return le nom de l'unité
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Retourne la force de cette unité, déterminée par son type.
	 *
	 * @return la force de l'unité
	 */
	public int getStr() {
		return this.type.getStr();
	}

	/**
	 * Retourne la mobilité de cette unité, déterminée par son type.
	 *
	 * @return la mobilité de l'unité
	 */
	public int getMvt() {
		return this.type.getMvt();
	}

	/**
	 * Calcule le cout pour se déplacer sur une tuile via la bonne stratégie.
	 * 
	 * @param tile la tuile de destination
	 * @return un entier qui correspond au coût de déplacement.
	 */
	public int getMoveCostFor(Tile tile) {

		return this.strategy.calculateMovementCost(tile);
	}

	/**
	 * Récupère le nombre de points consommé
	 * 
	 * @return un entier représentant le nombre de points consommé par l'unité
	 */
	public int getMvtPointsConsumed() {

		return this.mvtPointsConsumed;
	}

	/**
	 * Ajoute à l'attribut de points consommé pour l'unité
	 * 
	 * @param points le nombre de points à ajouter à cet attribut.
	 */
	public void addMvtPointsConsumed(int points) {

		this.mvtPointsConsumed += points;
	}

	/**
	 * Retourne la puissance d'attaque courante de cette unité.
	 *
	 * @return la puissance d'attaque courante, ou {@code 0} si l'unité n'a pas (ou
	 *         plus) de puissance fixée
	 */
	public int getPow() {
		return this.pow;
	}

	/**
	 * Récupère la stratégie de l'unité en question
	 * 
	 * @return l'objet qui contient la stratégie de l'unité
	 */
	public MovementStrategy getStrategy() {

		return this.strategy;
	}

	/**
	 * Vérifie si cette unité est autorisée à se déplacer, c'est-à-dire si elle ne
	 * s'est pas déjà déplacée durant le tour courant.
	 *
	 * @return {@code true} si l'unité ne s'est pas encore déplacée
	 */
	public boolean isAllowedToMove() {
		return !moved;
	}

	/**
	 * Vérifie si cette unité est autorisée à attaquer.
	 *
	 * <p>
	 * Une unité de type {@link UnitType#GENERAL} n'est jamais autorisée à attaquer.
	 * Une unité d'un autre type n'est autorisée à attaquer que si aucune puissance
	 * d'attaque ne lui a encore été fixée pour le tour courant ({@code pow == 0}).
	 *
	 * @return {@code true} si l'unité peut attaquer
	 */
	public boolean isAllowedToAttack() {
		return UnitType.GENERAL != type && pow == 0;
	}

	/**
	 * Marque cette unité comme s'étant déplacée durant le tour courant, l'empêchant
	 * ainsi de se déplacer à nouveau jusqu'à la prochaine réinitialisation de son
	 * état via {@link #resetState()}.
	 */
	public void setMoved() {
		moved = true;
	}

	/**
	 * Fixe la puissance d'attaque courante de cette unité et verrouille cet unité.
	 *
	 * @param newPower la nouvelle puissance d'attaque, strictement positive
	 * @throws IllegalArgumentException si {@code newPower <= 0}
	 */
	public void setPower(int newPower) {
		Contract.require(newPower > 0, "Arg. newPow > 0 attendu. Recu " + newPower);
		this.pow = newPower;

		this.moved = true;

	}

	/**
	 * Réinitialise l'état de cette unité pour un nouveau tour : elle n'est plus
	 * considérée comme ayant bougé, et sa puissance d'attaque est remise à zéro et
	 * le nombre de points consommé par l'unité revient à zero.
	 */
	public void resetState() {
		this.moved = false;
		this.pow = 0;
		this.mvtPointsConsumed = 0;
	}

}