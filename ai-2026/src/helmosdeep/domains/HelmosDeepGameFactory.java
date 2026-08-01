package helmosdeep.domains;

import java.util.Optional;

import helmosdeep.util.Contract;

/**
 * Implémente la logique de création de parties et fournit la dernière partie créée.
 * */
public class HelmosDeepGameFactory implements GameFactory, GameProvider {
	private final MiddleEarthReader reader = new MiddleEarthReader();
	private Optional<HelmosDeepGame> lastGame = Optional.empty();
	
	@Override
	public void create(String level) {
		reader.loadFromFile("resources/maps/%s.txt".formatted(level));
		
		lastGame = Optional.of(HelmosDeepGame.ofBattlefieldAndArmy(reader.getBattefield(), reader.getArmies()));
	}

	@Override
	public HelmosDeepGame getLastGame() {
		Contract.check(lastGame.isPresent(), "Méthode getLastGame appelée avoir d'avoir créé une partie");
		
		return lastGame.get();
	}		

}
