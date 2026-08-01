package helmosdeep.domains.world;


/**
 * Décrit une nation ou un groupe d'individus en guerre.
 * */
public enum Belligerent {
	MORDOR("Mordor"), MANKIND("Les Hommes");

	private final String displayName;

	Belligerent(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public String toString() {
		return this.displayName;
	}


}
