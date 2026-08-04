package helmosdeep.supervisors.mainmenu;

import helmosdeep.domains.GameFactory;
import helmosdeep.domains.GameOption;
import helmosdeep.supervisors.ViewsId;
import helmosdeep.util.Contract;

/**
 * Superviseur chargé de gérer les interactions et l'affichage du menu principal
 * du jeu.
 * <p>
 * Cette classe implémente {@link MainMenuListener} pour réagir aux événements
 * du menu principal, comme la sélection d'un élément ou l'initialisation de la
 * vue.
 * </p>
 */
public class MainMenuSupervisor implements MainMenuListener {

	/* Option pour le menu principal. */
	private static final int QUIT_ITEM = 2;
	private static final int MENU_OPTION = 1;
	private static final int NEW_GAME_ITEM = 0;

	/* Option pour le menu des options. */
	private static final int RETURN_MAIN_MENU = 2;
	private static final int OPTION_LIGHT_UNIT = 1;
	private static final int OPTION_HEAVY_UNIT = 0;

	/** Vue associée à ce superviseur pour l'affichage et la navigation. */
	private final MainMenuView view;
	private final GameFactory gameFactory;

	/** On récupère les option du jeu. */
	private GameOption gameOption;

	/**
	 * Construit un nouveau superviseur pour le menu principal.
	 * <p>
	 * Ajoute ce superviseur comme écouteur de la vue.
	 * </p>
	 * 
	 * @param view la vue du menu principal à superviser
	 */
	public MainMenuSupervisor(MainMenuView view, GameFactory gameFactory) {
		this.view = Contract.require(view, view != null, "Arg. view != null attendu");
		this.gameFactory = Contract.require(gameFactory, gameFactory != null, "Arg. factory != null attendu");

		this.view.addListener(this);

		this.gameOption = new GameOption();
	}

	/**
	 * Initialise les éléments du menu principal lors de son entrée à l'écran.
	 * <p>
	 * Définit les options disponibles dans le menu (ex: "Nouvelle partie",
	 * "Quitter").
	 * </p>
	 */
	@Override
	public void onViewEntered() {

		// on détermine si on se trouve dans le menu principal ou le menu option
		if (!gameOption.isModeOption()) {

			this.view.setItems("Menu principal", "Nouvelle partie", "Règles Spécifiques", "Quitter");

		} else {
			// on passe dans le menu d'option
			// on récupère les choix fait
			String optionLourde = (this.gameOption.getOptionHeavy() == true) ? "Unités lourdes [x]"
					: "Unités lourdes []";
			String optionLegere = (this.gameOption.getOptionLight() == true) ? "Unités légères [x]"
					: "Unités légères []";

			this.view.setItems("Règles Spécifiques", optionLourde, optionLegere, "Retour au menu principal");

		}

	}

	/**
	 * Réagit à la sélection d'un élément du menu principal par l'utilisateur.
	 * <p>
	 * Redirige vers la vue appropriée en fonction de l'index de l'élément
	 * sélectionné :
	 * <ul>
	 * <li>0 : Lance une nouvelle partie ({@link ViewsId#PLAY_GAME}).</li>
	 * <li>1 : Se dirige vers le menu du choix d'option.</li>
	 * <li>2 : Demande une confirmation pour quitter l'application.</li>
	 * </ul>
	 * </p>
	 *
	 * @param itemIndex l'index de l'élément sélectionné dans le menu
	 */
	@Override
	public void onItemSelected(int itemIndex) {
		Contract.require(0 <= itemIndex && itemIndex <= 2, "Arg. itemIndex in [0, 2] attendu. Reçu " + itemIndex);

		// si l'utilisateur est dans le menu principale
		if (!this.gameOption.isModeOption()) {

			manageSelectionMainMenu(itemIndex);

		} else {
			// si l'utilisateur est dans le mode option
			manageSelectionOptionMenu(itemIndex);
			
		}

	}

	/**
	 * Gère les interactions dans le menu principal :
	 *
	 * Redirige vers la vue appropriée en fonction de l'index de l'élément
	 * sélectionné :
	 * <ul>
	 * <li>0 : Lance une nouvelle partie ({@link ViewsId#PLAY_GAME}).</li>
	 * <li>1 : Se dirige vers le menu du choix d'option.</li>
	 * <li>2 : Quitte l'application.</li>
	 * </ul>
	 * 
	 */
	private void manageSelectionMainMenu(int itemIndex) {

		if (itemIndex == NEW_GAME_ITEM) {

			// l'objet HelmoDeepGame est créé depuis la factory
			gameFactory.create(this.gameOption);
			// on récupère l'objet GameOption
			
			

			view.goTo(ViewsId.PLAY_GAME);

		} else if (itemIndex == MENU_OPTION) {
			// on passe l'attribut du GameOption (estModeOptions) à true
			this.gameOption.changeModeOptions();

			// on affiche l'écran du menu d'option
			onViewEntered();

		} else if (itemIndex == QUIT_ITEM) {
			// si on sélectionne pour quitter la partie -> fermeture de l'appli
			view.confirmQuit();
		}

	}

	/**
	 * Gère les interactions dans le menu des options :
	 * 
	 * Sur base de l'index sélectionné on gère les interactions avec le GameOption :
	 * <ul>
	 * <li>0 : On inverse l'état actuel de l'option Unité lourde.</li>
	 * <li>1 : On inverse l'état actuel de l'option Unité légère.</li>
	 * <li>2 : Retour au menu principal -> on change l'état de l'attribut
	 * enModeOption de GameOption.</li>
	 * </ul>
	 * 
	 * 
	 */
	private void manageSelectionOptionMenu(int itemindex) {

		// si l'utilisateur appuie sur "Unités lourdes"
		if (itemindex == OPTION_HEAVY_UNIT) {

			// on change l'état de l'option
			this.gameOption.changeOptionHeavy();

			// on affiche la mise a jour à l'écran
			onViewEntered();

		} else if (itemindex == OPTION_LIGHT_UNIT) {
			// si l'utilisateur appuie sur "Unités légères"

			// on change l'état de l'option
			this.gameOption.changeOptionLight();

			onViewEntered();

		} else if (itemindex == RETURN_MAIN_MENU) {
			// si l'utilisateur appuie sur "Retour au menu principal"

			// on passe le mode option à false
			this.gameOption.changeModeOptions();

			onViewEntered();

		}

	}

}
