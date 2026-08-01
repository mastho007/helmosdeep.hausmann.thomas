package helmosdeep.views;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import ai.engine.core.AbstractScene;
import ai.engine.core.Camera;
import ai.engine.core.KeyboardDriveable;
import ai.engine.core.SceneTree;
import ai.engine.core.Tween;
import ai.engine.core.Tweens;
import ai.engine.core.Updateable;
import ai.engine.core.functions.ColorFunctions;
import ai.engine.core.functions.DoubleFunctions;
import ai.engine.core.functions.IntFunctions;
import ai.engine.core.functions.PointFunctions;
import ai.engine.ui.TitledPanel;
import helmosdeep.supervisors.ViewsId;
import helmosdeep.supervisors.game.GameView;
import helmosdeep.supervisors.game.GameViewListener;
import helmosdeep.views.components.HexTile;
import helmosdeep.views.components.UnitTile;

public final class GameScene 
	extends AbstractScene implements Updateable, KeyboardDriveable, GameView {
	private static final int TILE_BOUNDS_SIZE = 200;
	private static final int PANEL_WIDTH = 300;
	private static final int PANEL_UNIT_HEIGHT = 200;
	private static final long serialVersionUID = 7650608643296807462L;
	
	private GameViewListener listener;
	
	private TitledPanel commandPanel;
	private TitledPanel statusPanel;
	
	private Camera camera;
	private SceneTree components;
	private HexTile activeHex;	
	private UnitTile selectedUnit;
	
	public GameScene() {
		super();
		this.setName(ViewsId.PLAY_GAME);
		
		commandPanel = new TitledPanel("Commandes", PANEL_WIDTH, PANEL_UNIT_HEIGHT);
		commandPanel.addContent("◀▶▼▲ : Déplacer le curseur");
		commandPanel.addContent("ESPACE : Sélectionner");
		commandPanel.addContent("ENTER : Terminer le tour");
		commandPanel.addContent("ESC : Annuler");
		commandPanel.addContent("P : Zoom avant - M : Zoom arrière");
		
		statusPanel = new TitledPanel("Situation", PANEL_WIDTH, PANEL_UNIT_HEIGHT*2);

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int newLeft = getWidth() - commandPanel.getWidth();
				commandPanel.setLocation(newLeft - PANEL_WIDTH/10, PANEL_WIDTH/10);
				statusPanel.setLocation(newLeft - PANEL_WIDTH/10, commandPanel.getY() + commandPanel.getHeight() + PANEL_WIDTH/10);				
			}
		});
		
		components = new SceneTree("HexSceneTree");
		camera = new Camera(components);
		
		this.add(commandPanel);
		this.add(statusPanel);
	}	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(g instanceof Graphics2D g2d) {
			 camera.render(g2d);
		}
	}

	@Override
	public void update(long dt) {
		 components.update(dt);				
	}

	@Override
	public void handleKeys(Set<Integer> keys) {
		// Touches de déplacement
		int xDir = keys.contains(KeyEvent.VK_LEFT) ? -1
				:  keys.contains(KeyEvent.VK_RIGHT)? 1
				:  0;
		
		int yDir = keys.contains(KeyEvent.VK_UP) ? -1
				:  keys.contains(KeyEvent.VK_DOWN)? 1
				:  0;
		if(xDir != 0 || yDir != 0) {
			listener.onMove(xDir, yDir);
		}
		
		// Touches de zoom
		int zoomRequested = keys.contains(KeyEvent.VK_P) ? -1
				: keys.contains(KeyEvent.VK_M) ? +1
				: 0;
		
		if(zoomRequested != 0) {
			Tweens.instance().enqueue(
					Tween.of(camera::setZoom, 
							camera.getZoom(), Math.clamp(camera.getZoom() + zoomRequested/2.0,0.5, 5), 
							DoubleFunctions::easeOutExpo, 
							300)
					);
		}
		
		// Touches d'action
		if(keys.contains(KeyEvent.VK_SPACE)) {
			listener.onAction();
		}
		
		if(keys.contains(KeyEvent.VK_ESCAPE)) {
			listener.onCancel();
		}
		
		if(keys.contains(KeyEvent.VK_ENTER)) {
			listener.onEndTurn();
		}
	}

	@Override
	public void onEnter(String fromView) {
		if(listener != null) {
			listener.onViewEntered();
		}
	}

	@Override
	public void onLeave(String toView) {		
	}

	@Override
	public void addListener(GameViewListener listener) {
		Objects.requireNonNull(listener, "Arg. listener != null attendu");
		this.listener = listener;		
	}

	@Override
	public void moveCameraTo(int currentRow, int currentCol) {
		var newId = "%02d-%02d".formatted(currentCol, currentRow);
		var hexTile = components.getChild(newId, HexTile.class);
		
		if(hexTile.isEmpty()) {
			return;
		}
		
		activeHex.unselect();
		activeHex = hexTile.get();
		activeHex.select();
		
		Tweens.instance().enqueue(
				Tween.of(camera::moveTo, 
						new Point(camera.getX(), camera.getY()), 
						new Point(activeHex.getX() - 300, activeHex.getY() - 300), 
						PointFunctions::easeOutExpo, 
						300)
				);
	}

	@Override
	public void selectUnit(int row, int col) {
		var unitTile = findUnitAt(row, col);
		
		if(unitTile.isEmpty()) {
			return;
		}
		
		if(selectedUnit != null) {
			selectedUnit.unselect();
		}
		
		selectedUnit = unitTile.get();
		selectedUnit.select();
		
	}
	
	@Override
	public void unselectUnit(int row, int col) {
		var unitTile = findUnitAt(row, col);
		
		if(unitTile.isEmpty()) {
			return;
		}
		
		if(selectedUnit != null) {
			selectedUnit.unselect();
			selectedUnit = null;
		}
	}

	@Override
	public void addHex(int row, int col, Color hexColor) {
		var newTile = new HexTile(row, col);
		components.attach(newTile);
		
		if(activeHex == null) {
			activeHex = newTile;
			activeHex.select();
		}
		
		newTile.setDimension(TILE_BOUNDS_SIZE, TILE_BOUNDS_SIZE);
		newTile.moveTo(mapColToWorldX(col), mapRowToWorldY(row, col));
		newTile.setColor(hexColor);
		
	}
	
	@Override
	public void addUnit(int row, int col, String name, String stats) {
		var newId = UnitTile.formatId(row, col);
		final var newTile = new UnitTile(newId, name, stats);
		components.attach(newTile);
		
		newTile.setDimension(TILE_BOUNDS_SIZE/2, TILE_BOUNDS_SIZE/2);
		newTile.moveTo(mapColToWorldX(col), mapRowToWorldY(row, col));
	}

	private int mapRowToWorldY(int row, int col) {
		return row*173+(col%2*87);
	}

	private int mapColToWorldX(int col) {
		return col*150;
	}

	@Override
	public void removeUnit(int row, int col) {
		var maybeUnit = findUnitAt(row, col);
		maybeUnit.ifPresent(unit -> {
			Tweens.instance().enqueue(Tween
					.<Integer>of(unit::setWidth, 
							unit.getWidth(), 0, 
							IntFunctions::lerp, 
							300)
					.then(() -> components.detach(unit)));
		});
	}

	private Optional<UnitTile> findUnitAt(int row, int col) {
		var worldX = mapColToWorldX(col);
		var worldY = mapRowToWorldY(row, col);
		
		var maybeUnit = components
			.getChildren(UnitTile.class)
			.stream()
			.filter(u -> Math.abs(u.getX() - worldX) <= 2 && Math.abs(u.getY() - worldY) <= 2)
			.findFirst();
		
		return maybeUnit;
	}

	@Override
	public void moveUnit(int fromRow, int fromCol, int toRow, int toCol) {
		var maybeUnit = findUnitAt(fromRow, fromCol);
		if(maybeUnit.isEmpty()) {
			return;
		}
		
		var toWorldX = mapColToWorldX(toCol);
		var toWorldY = mapRowToWorldY(toRow, toCol);
		var unit = maybeUnit.get();
		
		Tweens.instance().enqueue(Tween
				.<Point>of(unit::moveTo, 
						new Point(unit.getX(), unit.getY()), new Point(toWorldX, toWorldY), 
						PointFunctions::easeOutExpo, 
						300));
	}

	@Override
	public void setStatusPanel(String... lines) {
		statusPanel.clearChildren();
		for(var line : lines) {
			statusPanel.addContent(line);
		}
		statusPanel.updateUI();
	}

	@Override
	public void setHexColor(int row, int col, Color newColor) {
		var newId = HexTile.formatId(row, col);
		var maybeHexTile = components.getChild(newId, HexTile.class);
		maybeHexTile.ifPresent(ht -> Tweens.instance().enqueue(Tween
				.<Color>of(ht::setColor, 
						ht.getColor(), newColor, 
						ColorFunctions::easeOutExpo, 
						300)));		
	}
	
	public boolean checkTileAt(Predicate<HexTile> condition, int row, int col) {
		var newId = HexTile.formatId(row, col);
		var maybeTile = components.getChild(newId, HexTile.class);
		return maybeTile.isPresent() && condition.test(maybeTile.get());
	}
	
	public boolean checkActiveHex(Predicate<HexTile> condition) {
		return condition.test(activeHex);
	}
	
	public boolean checkUnitAt(Predicate<UnitTile> condition, int row, int col) {
		var maybeUnitTile = findUnitAt(row, col);
		return maybeUnitTile.isPresent() && condition.test(maybeUnitTile.get());
	}
	


	public boolean hasUnitAt(int row, int col) {
		var maybeUnitTile = findUnitAt(row, col);
		return maybeUnitTile.isPresent();
	}
}
