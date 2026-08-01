package helmosdeep.views;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import ai.engine.core.AbstractScene;
import ai.engine.ui.ListBox;
import ai.engine.ui.TitledPanel;
import helmosdeep.supervisors.ViewsId;
import helmosdeep.supervisors.mainmenu.MainMenuListener;
import helmosdeep.supervisors.mainmenu.MainMenuView;

public class MainMenuScene extends AbstractScene implements MainMenuView {
	private static final long serialVersionUID = -3160038179342152555L;
	private static final int PANEL_WIDTH = 300;
	private static final int PANEL_UNIT_HEIGHT = 200;
		
	private final TitledPanel commandPanel;
	private final ListBox itemsPanel;
	private BufferedImage background;
	private MainMenuListener listener;
	
	public MainMenuScene() {
		super();
		setName(ViewsId.MAIN_MENU);
		
		try {
			background = ImageIO.read(Paths.get("./resources/backgrounds/main-menu.png").toFile());
		} catch (IOException e) {
 			System.err.println("Unable to load background image");
		}
		
		commandPanel = new TitledPanel("Commandes", PANEL_WIDTH, PANEL_UNIT_HEIGHT);
		commandPanel.addContent("▼▲ : Déplacer le sélecteur");
		commandPanel.addContent("ESPACE : Sélectionner l'item");

		itemsPanel = new ListBox("Menu principal", PANEL_WIDTH*2, PANEL_UNIT_HEIGHT*3);
		
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int newLeft = getWidth() - commandPanel.getWidth();
				commandPanel.setLocation(newLeft - PANEL_WIDTH/10, PANEL_WIDTH/10);
				itemsPanel.setLocation((getWidth() - PANEL_WIDTH*2)/2, (getHeight() - PANEL_UNIT_HEIGHT)/2);
			}
		});
			
		this.add(commandPanel);
		this.add(itemsPanel);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(background != null) {
			g.drawImage(background, 0, 0, getWidth(), getHeight(), 0, 0, background.getWidth(), background.getHeight(), null);
		}
	}
	
	
	@Override
	public void addListener(MainMenuListener listener) {
		this.listener = listener;		
	}
	
	@Override
	public void update(long dt) {
		var f = Font.decode("Minecraft");
		this.setFont(f);
	}

	@Override
	public void handleKeys(Set<Integer> keys) {	
		if(keys.contains(KeyEvent.VK_UP)) {
			itemsPanel.moveItem(-1);
		}
		
		if(keys.contains(KeyEvent.VK_DOWN)) {
			itemsPanel.moveItem(1);
		}
		
		if(keys.contains(KeyEvent.VK_SPACE)) {
			notifyListener(itemsPanel.getSelectedIndex());
		}
	}

	@Override
	public void onEnter(String fromView) {
		if(listener != null) {
			listener.onViewEntered();
		}
		itemsPanel.selectItem(0);
	}

	@Override
	public void onLeave(String toView) {
	}


	@Override
	public void setItems(String title, String... items) {
		itemsPanel.setTitle(title);
		itemsPanel.setItems(items);
	}


	private void notifyListener(int selectedIndex) {
		if(listener != null) {
			listener.onItemSelected(selectedIndex);
		}
	}

	@Override
	public void confirmQuit() {
		var ancestor = SwingUtilities.getWindowAncestor(this);
		ancestor.setVisible(false);
		SwingUtilities.invokeLater(() -> {
			try {
				Thread.sleep(1000);
				ancestor.dispose();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
	}


}
