package helmosdeep.domains.turns;

import java.util.Objects;
import java.util.Optional;

import helmosdeep.domains.commons.TemplateResolvable;
import helmosdeep.domains.world.*;
import helmosdeep.util.*;

/**
 * Gère les changements de tour
 * */
public final class TurnsController implements TemplateResolvable {
	private final ArmyList armies;
	private int currentArmyIndex;
	
	private Coordinate selectedPos;
	private Coordinate currentPos;	
	
	private final TurnSequence turns;
	
	public static TurnsController create(ArmyList armies) {
		Objects.requireNonNull(armies);
		Contract.require(armies.size() == 2, "Arg. armies doit avoir 2 éléments");
		Contract.require(armies.get(0).belongsTo(Belligerent.MORDOR), "La première armée doit appartenir au Mordor");
		
		return new TurnsController(armies);
	}
	
	private TurnsController(ArmyList armies) {
		this.armies = armies;
		this.currentArmyIndex = 0;
		this.selectedPos = this.getActiveArmy().locateGeneral();
		this.currentPos = selectedPos;
		this.turns = TurnSequence.fromArmies(getActiveArmy(), getOtherArmy());
	}

	public Army getActiveArmy() {
		return this.armies.get(currentArmyIndex);
	}

	public Army getOtherArmy() {
		var otherArmyIndex = (currentArmyIndex + 1) % 2;
		return this.armies.get(otherArmyIndex);
	}

	public Coordinate getCurrentPosition() {
		return currentPos;
	}
	
	public Coordinate getSelectedPosition() {
		return selectedPos;
	}


	@Override
	public String resolve(String template) {
		final var name_key = ":name:";
		
		return template
				.replace(":currentPos:", currentPos.toString())
				.replace(":currentArmy:", getActiveArmy().resolve(name_key))
		        .replace(":otherArmy:", getOtherArmy().resolve(name_key))
		        .replace(":firstArmy:", armies.get(0).resolve(name_key))
		        .replace(":secondArmy:", armies.get(1).resolve(name_key))
		        .replace(":currentArmy.mvt:", getActiveArmy().resolve(":mvt:"))
		        .replace(":otherArmy.mvt:", getActiveArmy().resolve(":mvt:"))        
		        .replace(":currentArmy.pui:", getActiveArmy().resolve(":pui:"))
		        .replace(":currentArmy.unit:", getActiveArmy().getUnitAt(selectedPos).get().getName())
		        .replace(":otherArmy.unit", getOtherArmy().getUnitAt(currentPos).orElse(Unit.UNKNOWN).getName());
	}

	/**
	 * Définit la position courante à {@code newPosition}.
	 * */
	public void setCurrentPosition(Coordinate newPosition) {
		Objects.requireNonNull(newPosition);
		currentPos = newPosition;		
	}

	public void startNewTurn() {
		this.currentArmyIndex = (currentArmyIndex + 1) % 2;
		this.getActiveArmy().reset();
		this.selectedPos = this.getActiveArmy().locateGeneral();
		this.currentPos = selectedPos;

		this.turns.startNewTurn(getActiveArmy(), getOtherArmy());
	}
	
	public void endLastTurn(Iterable<Coordinate> coordSource) {
		this.turns.endLastTurn(coordSource);
	}


	public void moveActiveUnit(int mvtConsumed) {
		if(getUnitAt(currentPos).isPresent()) {
			return;
		}
		
		getActiveArmy().move(selectedPos, currentPos, mvtConsumed);
		selectedPos = currentPos;
	}

	public void selectUnit() {
		if(getActiveArmy().getUnitAt(currentPos).isPresent()) {
			this.selectedPos = currentPos;		
		}
	}

	public void removeAt(Coordinate pos) {
		if(pos.equals(selectedPos)) {
			getActiveArmy().removeAt(pos);
			selectedPos = getActiveArmy().locateGeneral();
		}
		if(getOtherArmy().getUnitAt(pos).isPresent()) {
			getOtherArmy().removeAt(pos);
		}
		
	}

	public void applyToEachArmy(ArmyAndTurnConsumer consumer) {
		for(var army : armies) {
			consumer.accept(army, turns.filterByArmy(army), turns.count());
		}
		consumer.accept(turns);
	}

	public int getMvtForActiveUnit() {
		return getActiveArmy().getMvtForUnitAt(this.selectedPos);
	}
	
	/**
	 * Applique le {@link UnitConsumer} donné à chacune des unités présentes sur le champ de bataille,
	 * toutes armées confondues.
	 * <p>
	 * L'ordre de parcours des armées, ainsi que celui des unités au sein d'une même armée,
	 * n'est pas garanti.
	 *
	 * @param unitConsumer le consommateur à appliquer à chaque unité, ne doit pas être {@code null}.
	 * @throws NullPointerException si {@code unitConsumer} est {@code null}.
	 */
	public void applyToEachUnit(UnitConsumer unitConsumer) {
		Objects.requireNonNull(unitConsumer, "Arg. unitConsumer != null attendu");
		for(var army : armies) {
			army.applyToEachUnit(unitConsumer);
		}
		
	}

	public Optional<Unit> getUnitAt(Coordinate from) {
		return this.armies.getUnitAt(from);
	}
	
}
