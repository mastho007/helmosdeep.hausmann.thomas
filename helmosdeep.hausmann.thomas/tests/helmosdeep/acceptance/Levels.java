package helmosdeep.acceptance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Déclare des méthodes utilitaires pour remplacer les contenus de fichier.
 * 
 */
class Levels {
	private final static Path MAPS_DIR = Paths.get("resources", "maps");
	
	static void replaceBy(String fileName) {
		var sourcePath = MAPS_DIR.resolve(fileName);
		var destPath = MAPS_DIR.resolve("level-1.txt");
		
		try {
			Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
		} catch(IOException ex) {
			throw new RuntimeException("Unable to replace file "+ex.getMessage(), ex);
		}
	}
	
	static void replaceBack() {
		var sourcePath = MAPS_DIR.resolve("level-1-backup.txt");
		var destPath = MAPS_DIR.resolve("level-1.txt");
		
		try {
			Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
		} catch(IOException ex) {
			throw new RuntimeException("Unable to replace file "+ex.getMessage(), ex);
		}
	}
}
