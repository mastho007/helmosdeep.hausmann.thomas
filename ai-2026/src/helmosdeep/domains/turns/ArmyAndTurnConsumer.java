package helmosdeep.domains.turns;

import helmosdeep.domains.world.Army;

public interface ArmyAndTurnConsumer {

	void accept(Army army, TurnSequence armyTurns, int totalTurns);

	void accept(TurnSequence allTurns);

}
