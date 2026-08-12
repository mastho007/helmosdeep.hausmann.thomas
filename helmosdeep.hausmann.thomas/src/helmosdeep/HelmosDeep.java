package helmosdeep;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ai.engine.SwingGameLoop;
import helmosdeep.domains.HelmosDeepGameFactory;
import helmosdeep.supervisors.game.GameSupervisor;
import helmosdeep.supervisors.gameover.GameOverSupervisor;
import helmosdeep.supervisors.mainmenu.MainMenuSupervisor;
import helmosdeep.views.GameOverScene;
import helmosdeep.views.GameScene;
import helmosdeep.views.MainMenuScene;

/**
 * Classe principale du jeu HelmosDeep.
 * 
 * Cette classe est le point d'entrée de l'application et gère l'initialisation
 * des polices personnalisées, la création de la boucle de jeu et le lancement
 * de l'interface graphique.
 */
public class HelmosDeep {

    /**
     * Méthode principale, point d'entrée de l'application.
     * Initialise les polices, puis lance la boucle de jeu dans le thread de l'UI.
     *
     * @param args Arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        registerFonts();

        SwingUtilities.invokeLater(() -> {
            makeGameLoop().run();
        });
    }

    /**
     * Crée et configure la boucle de jeu principale.
     * <p>
     * Initialise les différentes scènes du jeu (menu principal, jeu, fin de jeu)
     * et leurs superviseurs associés.
     *</p>
     *
     * @return Une instance de SwingGameLoop configurée avec les scènes du jeu
     */
    public static SwingGameLoop makeGameLoop() {
    	var gameFactory = new HelmosDeepGameFactory();    	
        var menuScene = new MainMenuScene();
        menuScene.addListener(new MainMenuSupervisor(menuScene, gameFactory));

        var gameScene = new GameScene();
        gameScene.addListener(new GameSupervisor(gameScene, gameFactory));

        var gameOverScene = new GameOverScene();
        gameOverScene.addListener(new GameOverSupervisor(gameOverScene, gameFactory));

        return new SwingGameLoop("Ai - 2026 - Helwar", menuScene, gameScene, gameOverScene);
    }

    private static void registerFonts() {
        try {
            var font = Font.createFont(Font.TRUETYPE_FONT, new File("./resources/fonts/Ithaca-LVB75.ttf"));
            font = font.deriveFont(16f);

            GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .registerFont(font);

            for(var key : UIManager.getDefaults().keySet()) {
                if(key instanceof String strKey && strKey.endsWith(".font")) {
                    UIManager.put(strKey, font);
                }
            }
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
