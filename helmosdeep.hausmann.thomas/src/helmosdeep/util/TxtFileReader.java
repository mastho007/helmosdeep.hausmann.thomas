package helmosdeep.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

/**
 * Déclare une méthode utilitaire pour lire le contenu d'un fichier texte.
 * <p>
 * Cette classe propose un méthode statique pour récupérer le contenu
 * d'un fichier texte sous forme de chaîne de caractères, avec validation
 * des préconditions via des contrats.
 * </p>
 */
public final class TxtFileReader {

    /**
     * Constructeur privé pour empêcher l'instanciation de cette classe utilitaire.
     */
    private TxtFileReader() {
    }

    /**
     * Lit et retourne le contenu d'un fichier texte spécifié par son chemin.
     * <p>
     * Cette méthode valide que le chemin fourni n'est pas null et que le fichier existe.
     * </p>
     *
     * @param rawFilePath le chemin du fichier texte à lire
     * @return le contenu du fichier sous forme de chaîne de caractères
     * @throws IllegalArgumentException si le chemin est null ou si le fichier n'existe pas
     */
    public static String getContent(String rawFilePath) {
        Contract.require(
                rawFilePath != null && !rawFilePath.isBlank(),
                "Arg. rawFilePath non blanc attendu");
        
        return getContent(Paths.get(rawFilePath));
    }

    private static String getContent(Path toPath) {
        Contract.require(toPath,
                Files.exists(toPath),
                "Arg. rawFilePath doit localiser un fichier existant");
        
        try {
            return Files.readString(toPath);
        } catch(IOException ioe) {
            throw new IllegalArgumentException("Erreur pendant la lecture du fichier", ioe);
        }
    }
}