package helmosdeep.views.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import ai.engine.core.GameActor;
import ai.engine.core.SpriteSheet;
import ai.engine.core.Tween;
import ai.engine.core.Tweens;
import ai.engine.core.functions.DoubleFunctions;
import helmosdeep.views.FontUtils;

public class UnitTile extends GameActor {
	private final static double NORMAL_STROKE_WIDTH = 1.0;
	private final static double SELECTED_STROKE_WIDTH = 4.0;
	private final static int MAX_CHAR_HEIGHT = 18;
	private final static Map<String, SpriteSheet> SPRITES_DICTIONARY = new HashMap<>();
	private Tween<Double> selectTween;
	
	private final String hexId;
	private final String name;
	private final String stats; 
	private long currentTime;
	
	public UnitTile(String unitId, String name, String stats) {		
		super(unitId);
		
		String lowerCasedName = name.toLowerCase();
		if(!SPRITES_DICTIONARY.containsKey(lowerCasedName)) {
			SPRITES_DICTIONARY.put(
				lowerCasedName, 
				new SpriteSheet("resources/sprites/%s.png".formatted(lowerCasedName), 1, 4));
		}
		
		this.hexId = unitId;
		this.name = name;
		this.stats = stats;
		this.currentTime = 0;
		
		setProperty("strokeWidth", NORMAL_STROKE_WIDTH);
		setProperty("unitBgColor", new Color(240, 240,240,220));
		setProperty("textBgColor", new Color(200, 200,200,220));
	}

	@Override
	public void render(Graphics2D graphics) {
		if(!hasProperty("fontMetrics")) {
			setProperty("fontMetrics", FontUtils.fitFont(graphics, "x".repeat(10), MAX_CHAR_HEIGHT, getWidth()));
		}
		
		var oldTransform = graphics.getTransform();
		graphics.rotate(this.getAngleRad());
		
		float strokeWidth = getProperty("strokeWidth", Double.class).floatValue();
		graphics.setStroke(new BasicStroke(strokeWidth));
		graphics.translate(getX(), getY());
	    
		var spriteSheet = SPRITES_DICTIONARY.get(name.toLowerCase());
		var oldColor = graphics.getColor();
		graphics.setColor(getProperty("unitBgColor", Color.class));
		graphics.fillRect(- getWidth()/2, -getHeight()/2, getWidth(), getHeight());
	    graphics.drawImage(getCurrentSprite(spriteSheet), 
	    		-spriteSheet.getTileWidth()/2, getHeight()/6 - spriteSheet.getTileHeight(),
	    		spriteSheet.getTileWidth(), spriteSheet.getTileHeight(), 
	    		null);
		graphics.setColor(oldColor);
		// Rendu des textes
		var fontMetrics = getProperty("fontMetrics", FontMetrics.class); 
	    
		oldColor = graphics.getColor();
		graphics.setColor(getProperty("textBgColor", Color.class));
		graphics.fillRect(- getWidth()/2, getHeight()/6, getWidth(), 35*fontMetrics.getHeight()/10);
		graphics.setColor(oldColor);

	    graphics.drawString(hexId, 
	    		-fontMetrics.stringWidth(hexId) / 2, 
	    		getHeight() / 6 + fontMetrics.getHeight());
	    graphics.drawString(name, 
	    		-fontMetrics.stringWidth(name) / 2, 
	    		getHeight() / 6 + 2*fontMetrics.getHeight());
	    graphics.drawString(stats, 
	    		-fontMetrics.stringWidth(stats) / 2, 
	    		getHeight() / 6 + 3*fontMetrics.getHeight());
	    
	    graphics.setTransform(oldTransform);
	}
	
	private Image getCurrentSprite(SpriteSheet ss) {
		return ss.getSprite(0, (int)currentTime/90);
	}
	
	@Override
	public void update(long dt) {
		currentTime = (currentTime + dt)%360;
	}
	
	
	public void setWidth(int newWidth) {
		this.setDimension(newWidth, getHeight());
	}

	public void select() {
		if(selectTween == null || selectTween.isDone()) {
			selectTween = Tween.of(
					(width) -> setProperty("strokeWidth", width),
					NORMAL_STROKE_WIDTH, SELECTED_STROKE_WIDTH, 
					DoubleFunctions::easeOutExpo, 
					300);
		}
		
		Tweens.instance().enqueue(selectTween);		
	}
	
	public void unselect() {
		if(selectTween != null && !selectTween.isDone()) {
			Tweens.instance().cancel(selectTween);
			selectTween = null;
		}
		setProperty("strokeWidth", NORMAL_STROKE_WIDTH);		
	}
	
	public void moveTo(Point newDest) {
		this.moveTo(newDest.x, newDest.y);
	}
	
	public boolean hasName(String expected) {
		return expected.equalsIgnoreCase(name);
	}

	public static String formatId(int row, int col) {
		return "U%02d-%02d".formatted(col, row);
	}

}
