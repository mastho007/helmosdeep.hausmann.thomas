package helmosdeep.supervisors.mainmenu;

import helmosdeep.domains.GameFactory;
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

	private static final int QUIT_ITEM = 1;
	private static final int NEW_GAME_ITEM = 0;
	
	/** Vue associée à ce superviseur pour l'affichage et la navigation. */
	private final MainMenuView view;
	private final GameFactory gameFactory;

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
		this.view.setItems("Menu principal", "Nouvelle partie", "Quitter");
	}

	/**
	 * Réagit à la sélection d'un élément du menu principal par l'utilisateur.
	 * <p>
	 * Redirige vers la vue appropriée en fonction de l'index de l'élément
	 * sélectionné :
	 * <ul>
	 * <li>0 : Lance une nouvelle partie ({@link ViewsId#PLAY_GAME}).</li>
	 * <li>1 : Demande une confirmation pour quitter l'application.</li>
	 * </ul>
	 * </p>
	 *
	 * @param itemIndex l'index de l'élément sélectionné dans le menu
	 */
	@Override
	public void onItemSelected(int itemIndex) {
		Contract.require(0 <= itemIndex && itemIndex <= 1, "Arg. itemIndex in [0, 1] attendu. Reçu " + itemIndex);

		if (itemIndex == NEW_GAME_ITEM) {
			gameFactory.create();
			view.goTo(ViewsId.PLAY_GAME);
		} else if (itemIndex == QUIT_ITEM) {
			view.confirmQuit();
		}
	}
}
