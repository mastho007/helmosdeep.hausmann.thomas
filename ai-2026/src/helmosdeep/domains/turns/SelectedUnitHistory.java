package helmosdeep.domains.turns;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

import helmosdeep.domains.world.Army;
import helmosdeep.domains.world.Coordinate;
import helmosdeep.domains.world.Unit;
import helmosdeep.util.Contract;

/**
 * La class a pour responsabilités de :<br>
 * 
 * <ul>
 * <li>Stocker les unités sélectionnées durant le tour courant</li>
 * </ul>
 * <br>
 * It-4-q1 : <br>
 * 
 * <ol>
 * <li>Quelle règle du jeu garantit qu'une armée ne peut pas perdre son général
 * durant son propre tour ?</li>
 * 
 * <ul>
 * <li>Absence de contre-attaque durant le tour courant : <br>
 * Pendant le tour courant les unités de l'armée active peuvent se déplacer ou
 * attaquer les unités de l'autre armée, ainsi l'armée active peut perdre des
 * unités ou pas, à l'exeption du général de l'armée active qui lui ne peut pas
 * attaquer.<br>
 * </li>
 * 
 * <li>La défaite immédiate : <br>
 * Si le général est éliminé (l'armée active tue le général de l'armée énemmie),
 * lorsque l'utilisateur change de tour, la partie prend fin instantanément. Par
 * conséquent, au moment ou un tour commence, l'armée active possède un général
 * en vie et présent sur le plateau.</li>
 * </ul>
 * 
 * 
 * <li>Quel est l'invariant de classe respecté par l'historique durant tout le
 * tour ?</li>
 * 
 * 
 * <ul>
 * <li>L'invariant sur la taille et le contenu de base :<br>
 * 
 * <ul>
 * <li>historySelection.size() >= 1 : La collection historySelection n'est
 * jamais vide au cours d'un tour.</li>
 * <li>Base du général : Le premier élément du bas de la pile contient toujours
 * le général de l'armée active(et bien sur vivant).</li>
 * </ul>
 * 
 * </li>
 * 
 * <li>L'invariant sur la validité des éléments empilés :<br>
 * Tous les éléments contenus dans la pile sont non null et sont différents de
 * Unit.UNKNOWN</li>
 * 
 * 
 * </ul>
 * 
 * </ol>
 * 
 */
public class SelectedUnitHistory {

	private static final int ONE_UNIT = 1;

	// permet de connaitre l'état du tour courant
	private TurnsController turnsController;

	// contient les unités sélectionnées durant le tour courant (general, unit1,
	// unit2,...)
	private Deque<Unit> historySelection;

	/**
	 * 
	 * @param turnsController le controleur qui contient les infos du tour actuel
	 */
	public SelectedUnitHistory(TurnsController turnsController) {

		this.turnsController = Objects.requireNonNull(turnsController, "Arg. turnsController != null attendu");

		this.historySelection = new ArrayDeque<Unit>();
	}

	/**
	 * Réinitialise l'historique d'un début de tour (supprime les unités de la
	 * collection et insère le général de l'armée active)
	 * 
	 * @param general le général de l'armée active
	 */
	public void reset(Unit general) {

		// on vide la collection et on ajoute le général de l'armée
		this.historySelection.clear();
		add(general);

	}

	/**
	 * Enregistre une unité sélectionnée dans la collection
	 * 
	 * @param unit l'unité qui vient d'être sélectionnée (ne doit pas être null)
	 */
	public void add(Unit unit) {

		Objects.requireNonNull(unit, "Arg. unit != null attendu");
		Contract.require(unit != Unit.UNKNOWN, "Arg. unit != unit.UNKNOWN attendu");
		// on empile l'unité reçue
		this.historySelection.push(unit);

	}

	/**
	 * Récupère la dernière unité séléctionné valide (vivante) de l'armée active.
	 * 
	 * @return l'unité précédemment sélectionnée, s'il n'y a pas eu d'unité
	 *         sélectionnées ou d'unité encore en vie -> renvoie le général de
	 *         l'armée active
	 */
	public Unit getPreviousValidUnit() {

		Army activeArmy = turnsController.getActiveArmy();
		// tant que la pile contient plus d'un élément (contient le général + au moins
		// une unité)
		if (this.historySelection.size() > ONE_UNIT) {

			// on déplie 1 fois
			this.historySelection.pop();
		}

		// on vérifie maintenant si la pile a toujours une taille > 1 ET si l'unité au
		// sommet n'est pas dans l'armée active -> sinon on supprime l'unité au
		// sommet de la pile
		while (this.historySelection.size() > ONE_UNIT
				&& activeArmy.locateUnit(this.historySelection.peek()).equals(Coordinate.NONE)) {

			this.historySelection.pop();
		}

		// si il ne reste plus qu'un élément dans la pile -> on retourne le général de
		// l'armée active
		Unit topUnit = this.historySelection.peek();

		if (topUnit == null || activeArmy.locateUnit(topUnit).equals(Coordinate.NONE)) {

			return activeArmy.getGeneral();
		}

		return topUnit;
	}

}
