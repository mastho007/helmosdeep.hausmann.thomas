package helmosdeep.domains.world;
import java.util.*;

import helmosdeep.domains.commons.TemplateResolvable;
import helmosdeep.util.*;

/**
 * Représente une armée d'un {@link Belligerent}, composée d'unités
 * positionnées sur des {@link Coordinate}, d'un pot de mouvement ({@link MvtPool})
 * partagée par les unités, et éventuellement d'un général.
 * <p>
 * Une armée ne peut posséder qu'un seul général au maximum. Elle ne peut plus
 * combattre si son général est mort ou s'il ne lui reste plus qu'une seule unité.
 */
public final class Army implements TemplateResolvable {
	private final Belligerent belligerent;
	private final HashBiMap<Coordinate, Unit> units;
	private final MvtPool mvt;
	private Optional<Unit> general;
		
	private Army(Belligerent belligerent) {
		this.belligerent = belligerent;
		this.units = new HashBiMap<>();
		this.mvt = MvtPool.create(0);
		this.general = Optional.empty();
	}

	/**
	 * Crée une nouvelle armée vide, sans unité ni général, pour le {@link Belligerent} donné.
	 *
	 * @param belligerent le camp auquel appartient cette armée, non {@code null}
	 * @return une nouvelle armée vide appartenant à {@code belligerent}
	 * @throws NullPointerException si {@code belligerent} est {@code null}
	 */
	public static Army of(Belligerent belligerent) {
		Objects.requireNonNull(belligerent, "Arg. belligerent != null attendu");
		return new Army(belligerent);
	}

	/**
	 * Retourne l'unité de cette armée présente à la position donnée, si elle existe.
	 *
	 * @param pos la position à consulter
	 * @return l'unité présente à {@code pos}, ou {@link Optional#empty()} si aucune unité
	 *         de cette armée ne s'y trouve
	 */
	public Optional<Unit> getUnitAt(Coordinate pos) {
		return Optional.ofNullable(units.getSecondOrDefault(pos, null));
	}
	
	/**
	 * Retourne le nombre de points de mouvement réellement utilisables par l'unité
	 * située à {@code unitPos}, c'est-à-dire le minimum entre le mouvement restant
	 * de l'armée et la capacité de mouvement propre à l'unité.
	 *
	 * @param unitPos la position de l'unité concernée
	 * @return le mouvement disponible pour cette unité, ou celui de {@link Unit#UNKNOWN}
	 *         si aucune unité ne se trouve à {@code unitPos}
	 */
	public int getMvtForUnitAt(Coordinate unitPos) {
		final var unit = this.units.getSecondOrDefault(unitPos, Unit.UNKNOWN);
		return Math.min(this.mvt.getActual(), unit.getMvt());
	}
	
	/**
	 * Retourne le nombre d'unités actuellement vivantes dans cette armée.
	 *
	 * @return le nombre d'unités présentes dans cette armée
	 */
	public int getUnitsAlive() {
		return this.units.size();
	}
	
	Belligerent getBelligerent() {
		return this.belligerent;
	}
	
	/**
	 * Retourne {@code true} si cette armée appartient au {@link Belligerent} donné.
	 *
	 * @param b le camp à comparer
	 * @return {@code true} si {@code b} est le camp de cette armée, {@code false} sinon
	 */
	public boolean belongsTo(Belligerent b) {
		return this.belligerent == b;
	}
	
	/**
	 * Localise la position du général de cette armée.
	 *
	 * @return la position du général, ou {@link Coordinate#NONE} si cette armée
	 *         n'a pas (ou plus) de général
	 */
	public Coordinate locateGeneral() {
		return units.getFirstOrDefault(general.orElse(Unit.UNKNOWN), Coordinate.NONE);
	}


	/**
	 * Enrôle une nouvelle unité dans cette armée à la position donnée.
	 * <p>
	 * Si l'unité enrôlée est de type {@link UnitType#GENERAL}, elle devient le général
	 * de l'armée. La capacité de mouvement de l'armée est augmentée de la moitié du
	 * mouvement de l'unité enrôlée.
	 *
	 * @param pos la position à laquelle placer l'unité, non {@code null}
	 * @param unit l'unité à enrôler, non {@code null}
	 * @throws NullPointerException si {@code pos} ou {@code unit} est {@code null}
	 * @throws IllegalArgumentException si {@code unit} est déjà présente dans cette armée,
	 *         si {@code pos} est déjà occupée, ou si {@code unit} est un général alors que
	 *         cette armée en possède déjà un
	 */
	public void enroll(Coordinate pos, Unit unit) {
		Objects.requireNonNull(pos, "Arg. pos != null attendu");
		Objects.requireNonNull(unit, "Arg. unit != null attendu");
		Contract.require(!units.containsSecond(unit), "Unité déjà présente dans cette armée");
		Contract.require(!units.containsFirst(pos), "Position sans tuile correspondante donnée");
		Contract.require(!unit.hasType(UnitType.GENERAL) || general.isEmpty(), "Cette armée possède déjà un général");
		
		if(unit.hasType(UnitType.GENERAL)) {
			general = Optional.of(unit);
		} 
		
		this.units.put(pos, unit);
		
		this.mvt.raiseCapacity(unit.getMvt() * 0.5f);
	}
	
	/**
	 * Construit une chaîne de caractères en substituant dans {@code template} les
	 * marqueurs {@code :name:} et {@code :mvt:} respectivement par le nom du camp
	 * de cette armée et par son mouvement actuel.
	 *
	 * @param template le gabarit contenant les marqueurs à remplacer
	 * @return le gabarit avec les marqueurs remplacés par les valeurs correspondantes
	 */
	@Override
	public String resolve(String template) {
		return template
				.replace(":name:", this.belligerent.toString())
				.replace(":mvt:", ""+this.mvt.getActual());
	}
	
	/**
	 * Retourne {@code true} si cette armée est encore en état de combattre, c'est-à-dire
	 * si elle possède un général vivant et strictement plus d'une unité.
	 *
	 * @return {@code true} si l'armée peut combattre, {@code false} sinon
	 */
	public boolean canFight() {
		return general.isPresent() && getUnitsAlive() > 1;
	}
	/**
	 * Retourne {@code true} si cette armée possède au moins une unité à une distance de 1 de {@code c}.
	 * */
	public boolean hasUnitCloseTo(Coordinate c) {
		Objects.requireNonNull(c);
		
		for(var n : c.getNeihgbors()) {
			if(units.containsFirst(n)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Applique le consommateur donné à chacune des unités de cette armée, avec sa
	 * position associée.
	 *
	 * @param unitConsumer le traitement à appliquer à chaque couple (position, unité)
	 */
	public void applyToEachUnit(UnitConsumer unitConsumer) {
		for(var pos : units.getFirsts()) {
			unitConsumer.accept(pos, units.getSecond(pos));
		}
	}

	/**
	 * Déplace l'unité située à {@code fromPos} vers {@code toPos}, en consommant
	 * {@code mvtConsumed} points de mouvement sur la réserve de l'armée.
	 *
	 * @param fromPos la position actuelle de l'unité à déplacer
	 * @param toPos la position de destination
	 * @param mvtConsumed le nombre de points de mouvement consommés par ce déplacement,
	 *        doit être compris entre 1 et le mouvement disponible pour cette unité
	 * @throws IllegalArgumentException si aucune unité ne se trouve à {@code fromPos},
	 *         ou si {@code mvtConsumed} n'est pas dans l'intervalle
	 *         {@code ]0; getMvtForUnitAt(fromPos)]}
	 */
	public void move(Coordinate fromPos, Coordinate toPos, int mvtConsumed) {
		var unit = this.units.getSecondOrDefault(fromPos, Unit.UNKNOWN);
		Contract.require(unit != Unit.UNKNOWN, "fromPos ne correspond pas à une unité existante");
		Contract.require(0 < mvtConsumed && mvtConsumed <= getMvtForUnitAt(fromPos), 
				"mvtConsumed in ]0; %d] attendu. Recu %d".formatted(getMvtForUnitAt(fromPos), mvtConsumed));
		
		units.replaceFirst(fromPos, toPos);
		
		unit.setMoved();
		mvt.subtract(mvtConsumed);
	}

	/**
	 * Réinitialise cette armée pour un nouveau tour : la réserve de mouvement est
	 * entièrement rechargée et l'état de toutes les unités est remis à zéro.
	 */
	public void reset() {
		mvt.refill();
		for(var unit : units.getSeconds()) {
			unit.resetState();
		}
		
	}
	
	/**
	 * Calcule et fixe la puissance de l'unité située à {@code pos}, comme la somme
	 * de sa force propre, du nombre d'unités alliées adjacentes (à une distance de 1)
	 * et d'un terme additionnel {@code term}.
	 *
	 * @param pos la position de l'unité dont la puissance doit être calculée
	 * @param term un terme additionnel (par exemple lié au terrain ou au contexte du combat)
	 * @throws IllegalArgumentException si {@code pos} ne correspond à aucune unité de cette armée
	 */
	public void computePowerAt(Coordinate pos, int term) {
		var unit = getUnitAt(pos);
		Contract.require(unit.isPresent(), "Arg. pos ne correspond à aucune unité");
		var unitStr = unit.get().getStr();
		
		int alliesCount = 0;
		for(var n : pos.getNeihgbors()) {
			if(units.containsFirst(n)) {
				++alliesCount;
			}
		}
		
		unit.get().setPower(unitStr + alliesCount + term);
	}

	/**
	 * Retire de cette armée l'unité présente à la position donnée, si elle existe.
	 * Si l'unité retirée était le général de l'armée, celle-ci se retrouve alors
	 * sans général et ne pourra plus poursuivre la bataille.
	 *
	 * @param pos la position de l'unité à retirer
	 */
	public void removeAt(Coordinate pos) {
		var unit = units.getSecondOrDefault(pos, Unit.UNKNOWN);
		units.removeFirst(pos);
		
		if(general.isPresent() && general.get().equals(unit)) {
			general = Optional.empty();
		}
		
	}

}