package helmosdeep.supervisors.game;

import java.awt.Color;
import java.util.Objects;
import java.util.Optional;

import helmosdeep.domains.*;
import helmosdeep.domains.actions.*;
import helmosdeep.domains.turns.TurnListener;
import helmosdeep.domains.world.*;

import helmosdeep.supervisors.ViewsId;

/**
 * Répond aux demandes de l'utilisateur en rafraichissant d'abord le modèle, puis la vue.
 * */
public final class GameSupervisor implements GameViewListener, TileConsumer, UnitConsumer, ActionListener, TurnListener {
	private final GameView view;
	private final GameProvider provider;
	
	private HelmosDeepGame currentGame;
	
	/**
	 * Construit un superviseur pour la vue {@code view} et ...
	 * 
	 * @param view la vue, obligatoirement défini
	 * */
	public GameSupervisor(GameView view, GameProvider provider) {
		this.view = Objects.requireNonNull(view, "Arg. view != null attendu");
		this.provider = Objects.requireNonNull(provider, "Arg. provuder != null attendu");
		this.view.addListener(this);
	}
	
	@Override
	public void onViewEntered() {
		this.currentGame = this.provider.getLastGame();
		
		// Dessine les tuiles et les unités
		this.currentGame.applyToEachTile(this);
		this.currentGame.applyToEachUnit(this);
		
		this.updateStatusPanel();
		
		this.updateCamera();
	}

	private void updateStatusPanel() {
		updateStatusPanel("", "");
	}

	
	private void updateCamera() {
		var currentPosition = this.currentGame.getCurrentPosition();
		this.view.moveCameraTo(currentPosition.getRow(), currentPosition.getCol());
	}

	/**
	 * Ajoute une tuile à l'écran.
	 * <p>
	 * Cette méthode ne doit pas être appelée plusieurs fois pour la même tuile.
	 * */
	@Override
	public void accept(Coordinate pos, Tile tile) {
		var color = switch(tile) {
		case LOWLAND -> Color.YELLOW;
		case FOREST -> Color.GREEN;
		case MOUNTAIN -> Color.GRAY;
		case UNKNWOWN -> Color.RED;
		};
		
		view.addHex(pos.getRow(), pos.getCol(), color);		
	}
	
	/**
	 * Ajoute une unité à l'écran.
	 * <p>
	 * Cette méthode ne doit pas être appelée plusieurs fois pour la même unité.
	 * */
	@Override
	public void accept(Coordinate pos, Unit unit) {
		view.addUnit(
				pos.getRow(), 
				pos.getCol(), 
				unit.getName(), 
				"%d - %d".formatted(unit.getStr(), unit.getMvt()));
		
	}

	@Override
	public void onMove(int xDir, int yDir) {
		// /!\ inverser les argmuments (x -> col, y -> row)
		this.currentGame.changePosition(yDir, xDir);
		this.updateStatusPanel();
		this.updateCamera();
	}

	@Override
	public void onAction() {
		this.currentGame.performAction(this);
	}

	@Override
	public void onCancel() {
		this.view.goTo(ViewsId.MAIN_MENU);		
		
		
		
		
	}

	@Override
	public void onEndTurn() {
		this.currentGame.endTurn(this);
	}

	
	@Override
	public void moved(Coordinate from, Coordinate to) {
		this.view.moveUnit(from.getRow(), from.getCol(), to.getRow(), to.getCol());
		updateStatusPanel();
		updateCamera();
	}

	@Override
	public void unitSelected() {
		updateStatusPanel();
		updateCamera();		
	}

	@Override
	public void newTurnStarted() {
		updateStatusPanel();
		updateCamera();			
	}

	@Override
	public void gameEnded() {
		this.view.goTo(ViewsId.GAME_OVER);		
	}

	
	@Override
	public void attackResolved(Unit attacker, Unit defender, float result, Optional<Coordinate> posToRemove) {
		var attackDescription = "%s (PUI: %d) attaque %s (PUI: %d)".formatted(attacker.getName(), attacker.getPow(), defender.getName(), defender.getPow());
		var attackResult = 
				result > 0 ? "%s gagne".formatted(attacker.getName()) : 
				result < 0 ? "%s gagne".formatted(defender.getName()) :
				"Égalité";
				
		updateStatusPanel(attackDescription, attackResult);		
		
		if(posToRemove.isPresent()) {
			this.view.removeUnit(posToRemove.get().getRow(), posToRemove.get().getCol());
		}
		updateCamera();
	}

	private void updateStatusPanel(String attackDescription, String attackResult) {
		Objects.requireNonNull(this.currentGame, "Champ currentGame != null attendu");
		
		this.view.setStatusPanel(
				this.currentGame.resolve("Le :firstArmy: vs les :secondArmy:"),
				this.currentGame.resolve("Au tour du :currentArmy: (MVT: :currentArmy.mvt:)"),
				this.currentGame.resolve("Position courante :currentPos:"),				
				this.currentGame.resolve("Type de terrain :currentTile:[:currentTileCost:]"),
				this.currentGame.resolve("Unité active :currentArmy.unit:"),
				attackDescription,
				attackResult);
	}


}
