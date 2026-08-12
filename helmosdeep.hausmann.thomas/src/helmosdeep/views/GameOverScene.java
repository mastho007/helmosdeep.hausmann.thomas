package helmosdeep.views;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

import javax.imageio.ImageIO;

import ai.engine.core.AbstractScene;
import ai.engine.ui.TitledPanel;
import helmosdeep.supervisors.ViewsId;
import helmosdeep.supervisors.gameover.GameOverListener;
import helmosdeep.supervisors.gameover.GameOverView;

public class GameOverScene extends AbstractScene implements GameOverView {
	private static final long serialVersionUID = -6944971013621920579L;
	private static final int PANEL_UNIT_WIDTH = 280;
	private static final int PANEL_UNIT_HEIGHT = 200;
	
	private final TitledPanel commandPanel;
	private final TitledPanel leftPanel;
	private final TitledPanel rightPanel;
	private final TitledPanel bottomPanel;
	private BufferedImage background;
	
	private GameOverListener listener;
	
	public GameOverScene() {
		super();
		setName(ViewsId.GAME_OVER);
		
		try {
			background = ImageIO.read(Paths.get("./resources/backgrounds/game-over.png").toFile());
		} catch (IOException e) {
			System.err.println("Unable to load background image");
		}
		
		commandPanel = new TitledPanel("Commandes", PANEL_UNIT_WIDTH, PANEL_UNIT_HEIGHT);
		commandPanel.addContent("ESPACE : Retour au menu");
		
		leftPanel = new TitledPanel("Panneau gauche", PANEL_UNIT_WIDTH*2, PANEL_UNIT_HEIGHT);
		rightPanel = new TitledPanel("Panneau droit", PANEL_UNIT_WIDTH*2, PANEL_UNIT_HEIGHT);
		bottomPanel = new TitledPanel("Panneau de dessous", PANEL_UNIT_WIDTH*4+PANEL_UNIT_WIDTH/10, PANEL_UNIT_HEIGHT*2);
		
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int newLeft = getWidth() - commandPanel.getWidth();
				commandPanel.setLocation(newLeft - PANEL_UNIT_WIDTH/10, PANEL_UNIT_WIDTH/10);
				leftPanel.setLocation(PANEL_UNIT_WIDTH/10, PANEL_UNIT_WIDTH/10);
				rightPanel.setLocation(2*PANEL_UNIT_WIDTH/10 + PANEL_UNIT_WIDTH*2, PANEL_UNIT_WIDTH/10);
				bottomPanel.setLocation(PANEL_UNIT_WIDTH/10, PANEL_UNIT_HEIGHT + 2*PANEL_UNIT_WIDTH/10);
			}
		});
						
		this.add(commandPanel);
		this.add(leftPanel);
		this.add(rightPanel);
		this.add(bottomPanel);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(background != null) {
			g.drawImage(background, 0, 0, getWidth(), getHeight(), 0, 0, background.getWidth(), background.getHeight(), null);
		}
	}
	
	
	@Override
	public void handleKeys(Set<Integer> keys) {
		if(keys.contains(KeyEvent.VK_SPACE)) {
			if(listener != null) {
				listener.onAction();
			}
		}		
	}

	@Override
	public void setLeftPanel(
			Color color,
			String header, 
			String... content) {
		leftPanel.setHeader(header);
		leftPanel.setColor(color);
		leftPanel.clearChildren();
		for(var c : content) {
			leftPanel.addContent(c);
		}
	}

	@Override
	public void setRightPanel(
			Color color,
			String header, 
			String... content) {
		rightPanel.setHeader(header);
		rightPanel.setColor(color);
		rightPanel.clearChildren();
		for(var c : content) {
			rightPanel.addContent(c);
		}
		
	}

	@Override
	public void setBottomPanel(
			Color color,
			String header, 
			
			String... content) {
		bottomPanel.setHeader(header);
		bottomPanel.setColor(color);
		bottomPanel.clearChildren();
		for(var c : content) {
			bottomPanel.addContent(c);
		}
	}

	@Override
	public void onEnter(String fromView) {
		if(listener != null) {
			listener.onViewEntered();
		}
	}

	public void addListener(GameOverListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void onLeave(String toView) {
		
	}
	
	@Override
	public void update(long dt) {
		
	}
}
