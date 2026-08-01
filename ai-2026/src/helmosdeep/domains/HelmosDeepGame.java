package helmosdeep.domains;

import java.util.*;

import helmosdeep.domains.actions.*;
import helmosdeep.domains.commons.*;
import helmosdeep.domains.turns.*;
import helmosdeep.domains.world.*;

/**
 * Implémente les règles du jeu.
 * <p>
 * Cette classe orchestre le déroulement d'une partie sur une {@link MiddleEarth}
 * donnée : elle expose l'accès aux tuiles et aux unités du terrain, gère la position courante
 * (typiquement celle de la caméra) ainsi que la progression des tours via un
 * {@link TurnsController}.
 * */
public final class HelmosDeepGame implements TemplateResolvable {
	private final MiddleEarth middleEarth;
	private final TurnsController turnsController;
	private final List<Action> actions;

	/**
	 * Construit une partie associée à la terre du milieu donnée et initialise
	 * le contrôleur de tours à partir des armées de cette terre.
	 *
	 * @param middleEarth la terre du milieu sur laquelle se joue la partie, ne peut pas être {@code null}
	 * @throws NullPointerException si {@code middleEarth} est {@code null}
	 * */
	private HelmosDeepGame(MiddleEarth middleEarth, ArmyList armies) {
		this.middleEarth = Objects.requireNonNull(middleEarth);
		this.turnsController = TurnsController.create(armies);
		this.actions = List.of(
				new MoveAction(this.middleEarth, this.turnsController), 
				new SelectUnitAction(this.turnsController),
				new AttackAction(turnsController));
	}
	/**
	 * Instancie une partie à partir d'une terre du milieu définie.
	 *
	 * @param middleEarth la terre du milieu sur laquelle se joue la partie, ne peut pas être {@code null}
	 * @param armies La liste des armées	 * 
	 * @return une nouvelle partie initialisée à partir de {@code middleEarth}
	 * @throws NullPointerException si {@code middleEarth} est {@code null}
	 * */
	public static HelmosDeepGame ofBattlefieldAndArmy(MiddleEarth middleEarth, ArmyList armies) {
		Objects.requireNonNull(middleEarth, "Arg. middleEarth != null attendu");
		Objects.requireNonNull(armies, "Arg. armies != null attendu");
		return new HelmosDeepGame(middleEarth, armies);
	}
	
	/**
	 * Applique le consommateur donné à chaque tuile de la terre du milieu de cette partie.
	 *
	 * @param tileConsumer le traitement à appliquer à chaque tuile, ne peut pas être {@code null}
	 * */
	public void applyToEachTile(TileConsumer tileConsumer) {
		middleEarth.applyToEachTile(tileConsumer);		
	}
	
	/**
	 * Applique le consommateur donné à chaque unité de la terre du milieu de cette partie.
	 *
	 * @param unitConsumer le traitement à appliquer à chaque unité, ne peut pas être {@code null}
	 * */
	public void applyToEachUnit(UnitConsumer unitConsumer) {
		turnsController.applyToEachUnit(unitConsumer);		
	}
	
	
	/**
	 * Applique le consommateur donné à chaque armée de la partie.
	 *
	 * @param unitConsumer le traitement à appliquer à chaque unité, ne peut pas être {@code null}
	 * */
	public void applyToEachArmy(ArmyAndTurnConsumer consumer) {
		turnsController.applyToEachArmy(consumer);
	}
	
	/**
	 * Retourne la position courante, qui devrait correspondre à la position de la caméra dans le jeu.
	 *
	 * @return la position courante gérée par le contrôleur de tours
	 * */
	public Coordinate getCurrentPosition() {
		return turnsController.getCurrentPosition();
	}
    /**
     * Formate un modèle de texte en remplaçant les placeholders par les valeurs dynamiques de la partie.
     * Les placeholders suivants sont supportés :
     * <ul>
     *   <li>{@code :army[current]:} : nom de l'armée active</li>
     *   <li>{@code :army[other]:} : nom de l'autre armée</li>
     *   <li>{@code :army[0]:} : nom de l'armée du Mordor</li>
     *   <li>{@code :army[1]:} : nom de l'armée des Hommes</li>
     *   <li>{@code :currentPos:} : coordonnée de la position courante (format {@code Coordinate.toString()})</li>
     *   <li>{@code :terrainType:} : type de terrain à la position courante (format {@code TileType.toString()})</li>
     * </ul>
     *
     * @param template le modèle de texte à formater, ne peut pas être {@code null}
     * @return le texte formaté avec les valeurs actuelles de la partie
     * @throws NullPointerException si {@code template} est {@code null}
     *
     * @example
     * Exemple d'utilisation :
     * String result = game.format("Position actuelle : :currentPos: sur :currentTile:");
     * Peut retourner : "Position actuelle : (5,3) sur MOUNTAIN"
     */
	@Override
    public String resolve(String template) {
        var updated = template
                .replace(":currentTile:", middleEarth.getTileAt(turnsController.getCurrentPosition()).toString())
        		.replace(":currentTileCost:", ""+middleEarth.getTileAt(turnsController.getCurrentPosition()).getCost());
        return turnsController.resolve(updated);
        
    }
	
    /**
     * Essaie de changer la position courante, tout en restant dans les limites de la terre du milieu.
     * Si le déplacement calculé sort des limites (résultat {@link Coordinate#NONE}), la position
     * courante n'est pas modifiée.
     *
     * @param rowDir le déplacement à appliquer sur les lignes (peut être négatif, nul ou positif)
     * @param colDir le déplacement à appliquer sur les colonnes (peut être négatif, nul ou positif)
     * */
	public void changePosition(int rowDir, int colDir) {
		var newPosition = middleEarth.checkTileExistsAt(getCurrentPosition().move(rowDir, colDir));
		if(Coordinate.NONE.equals(newPosition)) {
			return;
		}
		
		turnsController.setCurrentPosition(newPosition);
	}

	/**
	 * Déclenche, parmi les actions possibles de cette partie, la première qui est
	 * réalisable dans l'état courant du jeu, et notifie son exécution au
	 * {@code listener} donné.
	 * <p>
	 * Cette méthode illustre le protocole d'utilisation d'une {@link Action} décrit dans
	 * sa documentation : les actions candidates (ici, dans l'ordre, une
	 * {@link MoveAction} puis une {@link SelectUnitAction}) sont testées une à une via
	 * {@link Action#canBeDone()} ; dès qu'une action retourne {@code true}, elle est
	 * exécutée <b>immédiatement</b> via {@link Action#doAction(ActionListener)}, sans
	 * qu'aucun appel à {@link Action#canBeDone()} sur une autre action ne s'intercale, et
	 * le parcours s'arrête là (au plus une action est exécutée par appel).
	 * <p>
	 * Si aucune des actions candidates n'est réalisable dans l'état courant, cette
	 * méthode ne fait rien : le {@code listener} n'est notifié d'aucun évènement.
	 *
	 * @param listener le récepteur notifié de l'action effectuée, le cas échéant (par
	 *                 exemple via {@link ActionListener#moved(Object, Object)} ou
	 *                 {@link ActionListener#unitSelected()} selon l'action exécutée) ;
	 *                 ne peut pas être {@code null}
	 * */
	public void performAction(ActionListener listener) {		
		for(var action : actions) {
			if(action.canBeDone()) {
				action.doAction(listener);
				break;
			}
		}
	}
	
	/**
	 * */
	public void endTurn(TurnListener listener) {
		turnsController.endLastTurn(middleEarth);
		turnsController.startNewTurn();
		
		if(turnsController.getActiveArmy().canFight() && turnsController.getOtherArmy().canFight()) {
			listener.newTurnStarted();
		} else {
			turnsController.endLastTurn(middleEarth);
			listener.gameEnded();
		}
		
	}
}