package helmosdeep.domains.world;

/**
 * Représente les types d'unité.
 * */
public enum UnitType {
	GENERAL(4, 3), LIGHT(1, 4), AVERAGE(2, 3), HEAVY(3, 2), UNKNOWN(0, 0);
	
	private final int mvt;
	private final int str;

	UnitType(int str, int mvt) {
		this.str = str;
		this.mvt = mvt;
	}


	int getMvt() {
		return mvt;
	}
	
	int getStr() {
		return str;
	}
}
