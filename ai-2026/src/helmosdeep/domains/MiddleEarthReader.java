package helmosdeep.domains;

import java.util.*;

import helmosdeep.domains.world.*;
import helmosdeep.util.*;

/**
 * Instancie des objets MiddleEarth en lisant un fichier txt.
 * */
public final class MiddleEarthReader {
	private List<String> rows;
	private int rowsCount;
	private int colsCount;
	private MiddleEarth battefield;
	private ArmyList armies;
	
	/**
	 * Lit le contenu du fichier pour en extraire une nouvelle instance de la terre du milieu.
	 * */
	void loadFromFile(String filePath) {
		var rawContent = TxtFileReader.getContent(filePath);
		Contract.check(rawContent != null && !rawContent.isBlank(), "rawContent should be not blank");
		rows = Arrays.asList(rawContent.split("\n"));
		
		parseDimensions(rows.getFirst());
		Contract.check(rows.size() == 1+rowsCount+1+rowsCount, "# lignes brutes attendues %d. # reçue %d".formatted( 1+rowsCount+1+rowsCount, rows.size()));
		
		battefield = MiddleEarth.create();
		parseBattefield();
		
		armies = new ArmyList(Army.of(Belligerent.MORDOR), Army.of(Belligerent.MANKIND));
		
		parseMordor();
		parseMankind();
	}

	private void parseDimensions(String firstRow) {
		Contract.check(firstRow.strip().matches("\\d+\\s*:\\s*\\d+"), "First row should match [0-9]+:[0-9]+");
		
		rowsCount = Integer.parseInt(firstRow.substring(0,firstRow.indexOf(':')));
		colsCount = Integer.parseInt(firstRow.substring(firstRow.indexOf(':')+1).strip());
	}
	
	private void parseBattefield() {
	
		for(var row = 1; row <= rowsCount; ++row) {
			for(var col = 0; col < colsCount; ++col) {
				var coord = Coordinate.coord(row - 1, col);
				var tile = toTile(rows.get(row).charAt(col));
				getBattefield().put(coord, tile);
			}
		}
		
	}

	private Tile toTile(char tileAsChar) {
		return switch(Character.toUpperCase(tileAsChar)) {
		case 'L' -> Tile.LOWLAND;
		case 'F' -> Tile.FOREST;
		case 'M' -> Tile.MOUNTAIN;
		default -> throw new IllegalArgumentException("Unknown tile symbol ["+tileAsChar+"]");
		};
	}
	
	private void parseMordor() {
		for(var row = rowsCount; row < rows.size(); ++row) {
			for(var col = 0; col < colsCount; ++col) {
				var coord = Coordinate.coord(row - rowsCount - 2, col);
				var unit = toMordoUnit(rows.get(row).charAt(col));
				if(unit.isPresent()) {
					getArmies().enroll(coord, unit.get(), Belligerent.MORDOR);
				}
			}
		}
	}

	private Optional<Unit> toMordoUnit(char charAt) {
		return switch(Character.toUpperCase(charAt)) {
		case 'W' -> Optional.<Unit>of(new Unit("Wargs", UnitType.LIGHT));
		case 'S' -> Optional.<Unit>of(new Unit("Sauron", UnitType.GENERAL));
		case 'O' -> Optional.<Unit>of(new Unit("Orcs", UnitType.AVERAGE));
		case 'T' -> Optional.<Unit>of(new Unit("Trolls", UnitType.HEAVY));
		default  -> Optional.<Unit>empty();
		};
	}
	
	private void parseMankind() {
		for(var row = rowsCount; row < rows.size(); ++row) {
			for(var col = 0; col < colsCount; ++col) {
				var coord = Coordinate.coord(row - rowsCount - 2, col);
				var unit = toMankindUnit(rows.get(row).charAt(col));
				if(unit.isPresent()) {
					getArmies().enroll(coord, unit.get(), Belligerent.MANKIND);
				}
			}
		}
		
	}

	private Optional<Unit> toMankindUnit(char charAt) {
		return switch(Character.toUpperCase(charAt)) {
		case 'R' ->  Optional.<Unit>of(new Unit("Rohirrim", UnitType.LIGHT));
		case 'A' ->  Optional.<Unit>of(new Unit("Aragorn", UnitType.GENERAL));
		case 'G' ->  Optional.<Unit>of(new Unit("Gondoriens", UnitType.AVERAGE));
		case 'E' ->  Optional.<Unit>of(new Unit("Ents", UnitType.HEAVY));
		default  ->  Optional.<Unit>empty();
		};
	}

	public ArmyList getArmies() {
		return armies;
	}

	public MiddleEarth getBattefield() {
		return battefield;
	}

}
