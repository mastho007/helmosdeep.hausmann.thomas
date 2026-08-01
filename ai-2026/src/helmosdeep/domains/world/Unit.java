package helmosdeep.domains.world;

import java.util.Objects;

import helmosdeep.util.Contract;

/**
 * Représente une unité militaire pouvant enrollable dans une armée et déployée
 * sur le plateau.
 *
 * <p>Une unité possède un nom, un type ({@link UnitType}) déterminant sa force
 * ({@code str}) et sa mobilité potentielle ({@code mvt}), ainsi qu'un état mutable propre à
 * chaque tour de jeu : si elle a déjà effectué un déplacement ({@code moved}), et
 * une puissance d'attaque courante ({@code pow}), fixée lors de la résolution
 * d'un combat.
 *
 * <p>Cet état ({@code moved} et {@code pow}) est remis à zéro en début de tour via
 * {@link #resetState()}.
 */
public final class Unit {

	/**
	 * Unité représentant l'absence d'unité connue ou valide, utilisée comme valeur
	 * par défaut ou de repli.
	 */
	public static final Unit UNKNOWN = new Unit("?", UnitType.UNKNOWN);

	private final UnitType type;
	private final String name;
	private boolean moved;
	private int pow;

	/**
	 * Crée une nouvelle unité avec le nom et le type donnés.
	 *
	 * <p>L'unité est créée dans son état initial : elle n'a pas encore bougé
	 * ({@code moved == false}) et sa puissance est nulle ({@code pow == 0}).
	 *
	 * @param name le nom de l'unité
	 * @param type le type de l'unité, déterminant sa force et sa mobilité
	 * @throws NullPointerException si {@code name} ou {@code type} est {@code null}
	 */
	public Unit(String name, UnitType type) {
		this.type = Objects.requireNonNull(type,  "Arg. type != null attendu");
		this.name = Objects.requireNonNull(name, "Arg. name != null attendu");
		this.moved = false;
		this.pow = 0;
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
	 * Retourne la puissance d'attaque courante de cette unité.
	 *
	 * @return la puissance d'attaque courante, ou {@code 0} si l'unité n'a pas
	 *         (ou plus) de puissance fixée
	 */
	public int getPow() {
		return this.pow;
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
	 * <p>Une unité de type {@link UnitType#GENERAL} n'est jamais autorisée à
	 * attaquer. Une unité d'un autre type n'est autorisée à attaquer que si aucune
	 * puissance d'attaque ne lui a encore été fixée pour le tour courant
	 * ({@code pow == 0}).
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
		Contract.require(newPower > 0, "Arg. newPow > 0 attendu. Recu "+newPower);
		this.pow = newPower;
		this.moved = true;
	}

	/**
	 * Réinitialise l'état de cette unité pour un nouveau tour : elle n'est plus
	 * considérée comme ayant bougé, et sa puissance d'attaque est remise à zéro.
	 */
	public void resetState() {
		this.moved = false;
		this.pow = 0;
	}

}