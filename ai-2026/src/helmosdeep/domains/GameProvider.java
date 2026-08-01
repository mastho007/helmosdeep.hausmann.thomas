package helmosdeep.domains;

/**
 * Permet d'obtenir la dernière partie créée.
 * */
public interface GameProvider {
	/**
	 * Retourne la dernière partie créée.
	 * 
	 * @throws IllegalStateException si cette méthode est apellée sans avoir créé de partie avant.
	 * */
	HelmosDeepGame getLastGame();
}
