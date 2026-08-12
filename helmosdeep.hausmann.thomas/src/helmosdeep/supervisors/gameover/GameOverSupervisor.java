package helmosdeep.supervisors.gameover;

import java.awt.Color;
import java.util.Objects;
import java.util.Optional;

import helmosdeep.domains.GameProvider;
import helmosdeep.domains.turns.ArmyAndTurnConsumer;
import helmosdeep.domains.turns.Turn;
import helmosdeep.domains.turns.TurnSequence;
import helmosdeep.domains.world.Army;
import helmosdeep.domains.world.Belligerent;
import helmosdeep.domains.world.Coordinate;
import helmosdeep.supervisors.ViewsId;

/**
 * Superviseur chargé de gérer les interactions et l'affichage
 * de la vue de fin de partie.
 * <p>
 * Cette classe implémente {@link GameOverListener} pour réagir aux événements
 * de la vue et configurer son affichage initial.
 * </p>
 */
public final class GameOverSupervisor implements GameOverListener, ArmyAndTurnConsumer {

    /** Vue associée à ce superviseur pour l'affichage et la navigation. */
    private final GameOverView view;
	private final GameProvider gameProvider;
	private Army winner;

	
    /**
     * Construit un nouveau superviseur pour la vue de fin de partie.
     *
     * @param view la vue de fin de partie à superviser
     */
    public GameOverSupervisor(GameOverView view, GameProvider provider) {
        this.view = Objects.requireNonNull(view);
        this.gameProvider = Objects.requireNonNull(provider);
    }

    /**
     * Initialise l'affichage des panneaux de la vue de fin de partie
     * lors de son entrée à l'écran.
     * <p>
     * Définit le contenu et les couleurs des panneaux gauche, droit et inférieur.
     * </p>
     */
    @Override
    public void onViewEntered() {
    	var lastGame = gameProvider.getLastGame();
    	
        lastGame.applyToEachArmy(this);
    }

    /**
     * Réagit à une action de l'utilisateur sur la vue de fin de partie.
     * <p>
     * Redirige vers le menu principal lorsque l'utilisateur effectue une action.
     * </p>
     */
    @Override
    public void onAction() {
        view.goTo(ViewsId.MAIN_MENU);
    }

	@Override
	public void accept(Army army, TurnSequence armiesTurn, int totalTurns) {
		var name = army.resolve(":name:");
		
		var unitsRatio = "%d unites en vie".formatted(army.getUnitsAlive());
		String generalMsg = buildGeneralMessage(army);
		
		var deadliestTurn = Optional.<Turn>empty();
		var mostInfluencialTurn = Optional.<Turn>empty();
		
		for(var turn : armiesTurn ) {
			if(turn.isDeadlierThan(deadliestTurn)) {
				deadliestTurn = Optional.of(turn);
			}
			if(turn.hasMoreInfluenceThan(mostInfluencialTurn)) {
				mostInfluencialTurn = Optional.of(turn);
			}
		}
		
		var bestTurnMsg = buildBestTurnMessage(totalTurns, deadliestTurn);
		var influenceZoneCount = buildMostInfluencialZoneMessage(mostInfluencialTurn);
		
		if(army.belongsTo(Belligerent.MORDOR)) {
			updateLeftPanel(name, unitsRatio, generalMsg, bestTurnMsg, influenceZoneCount);
		} else {
			updateRightPanel(name, unitsRatio, generalMsg, bestTurnMsg, influenceZoneCount);
		}
		
		if(army.canFight()) {
			winner = army;
		}				
	}

	private String buildMostInfluencialZoneMessage(Optional<Turn> mostInfluencialTurn) {
		return mostInfluencialTurn.isPresent() ? 
				"Zone d'influence "+mostInfluencialTurn.get().getInfluenceZoneSize() : "Zone d'influence 0";
	}

	private String buildBestTurnMessage(int totalTurns, Optional<Turn> deadliestTurn) {
		return deadliestTurn.isPresent() && deadliestTurn.get().getId() < totalTurns  ?
				"Meilleur tour : "+deadliestTurn.get().getId() :
				"N'a pas joue";
	}

	private String buildGeneralMessage(Army army) {
		return army.locateGeneral() != Coordinate.NONE ? "General vivant" : "General mort";
	}

	private void updateLeftPanel(String name, String...messages) {
		this.view.setLeftPanel(
				new Color(46, 88, 148, 200),
				name,
				messages
		);		
	}
	
	private void updateRightPanel(String name, String...messages) {
		this.view.setRightPanel(
				new Color(194, 103, 165, 200),
				name,
				messages
			);	
	}

	@Override
	public void accept(TurnSequence allTurns) {
		var deadliestTurn = Optional.<Turn>empty();
		for(var turn : allTurns ) {
			if(turn.isDeadlierThan(deadliestTurn)) {
				deadliestTurn = Optional.of(turn);
			}
		}
		
		this.view.setBottomPanel(
				new Color(194, 103, 165, 200),
				"Resultats", 
				winner.resolve("Vainqueur :name:"),
				"Nombre de tours : "+(allTurns.count() - 1), // Le dernier tour n'a pas été joué
				"Meilleur tour : "+deadliestTurn.get().getId());
	}
}
