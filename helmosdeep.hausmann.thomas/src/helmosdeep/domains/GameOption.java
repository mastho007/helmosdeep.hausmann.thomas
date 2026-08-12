package helmosdeep.domains;

/**
 * La class a pour responsabilités de :
 * 
 * <ul>
 * <li>Connaître et stocker les choix du joueur</li>
 * </ul>
 */
public class GameOption {

	// on déclare les 2 options possibles
	private boolean optionHeavy;
	private boolean optionLight;
	private boolean estModeOptions;

	/**
	 * On créé le constructeur avec aucune option choisi initialement
	 */
	public GameOption() {

		// on démarre avec aucune des 2 options choisie
		this.optionHeavy = false;
		this.optionLight = false;
		// on commence dans le menu principal
		this.estModeOptions = false;
	}

	/**
	 * On récupère si l'option (unité lourde) à été validée.
	 * 
	 * @return true si l'option est validée, false sinon
	 */
	public boolean getOptionHeavy() {

		return this.optionHeavy;
	}

	/**
	 * On récupère si l'option (unité lourde) à été validée.
	 * 
	 * @return true si l'option est validée, false sinon
	 */
	public boolean getOptionLight() {

		return this.optionLight;
	}

	/**
	 * On récupère si l'option (unité lourde) à été validée.
	 * 
	 * @return true si l'option est validée, false sinon
	 */
	public boolean isModeOption() {

		return this.estModeOptions;
	}
	
	
	
	/**
	 * Setter qui modifie l'option lourde
	 */
	public void changeOptionHeavy() {

		this.optionHeavy = !optionHeavy;
	}

	/**
	 * Setter qui modifie l'option légère
	 */
	public void changeOptionLight() {

		this.optionLight = !optionLight;
	}

	/**
	 * Setter qui modifie l'écran du menu principal (false -> menu principale,
	 * false-> menu choix option)
	 */
	public void changeModeOptions() {

		this.estModeOptions = !estModeOptions;
	}

}
