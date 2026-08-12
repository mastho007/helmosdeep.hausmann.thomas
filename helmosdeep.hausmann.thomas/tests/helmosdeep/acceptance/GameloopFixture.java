package helmosdeep.acceptance;

import java.awt.Container;
import java.awt.Frame;

import javax.swing.JFrame;

import org.fest.swing.fixture.FrameFixture;

import ai.engine.core.AbstractScene;

/**
 * Récupère la boucle de jeu pour l'interroger.
 * */
class GameloopFixture extends FrameFixture {

	GameloopFixture(Frame target) {
		super(target);
	}
	
	<T extends AbstractScene> T scene(String sceneName, Class<T> clazz) {
		
		if(!(component() instanceof JFrame frame)) {
			throw new IllegalStateException("This loop has no content pane");
		}
		
		var contentPane = frame.getContentPane();
		
		return doGetScene(sceneName, clazz, contentPane);
	}

	private <T extends AbstractScene> T doGetScene(String sceneName, Class<T> clazz, Container contentPane) {
		for(int i=0; i < contentPane.getComponentCount(); ++i) {
			var c = contentPane.getComponent(i);
			
			if(sceneName.equalsIgnoreCase(c.getName())) {
				if(clazz.isInstance(c)) {
					return clazz.cast(c);
				}
			}
		}
		
		throw new IllegalArgumentException("Unable to find scene "+sceneName);
	}

}
