package helmosdeep.views.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import ai.engine.core.GameActor;
import ai.engine.core.ShapesRepository;
import ai.engine.core.Tween;
import ai.engine.core.Tweens;
import ai.engine.core.functions.DoubleFunctions;
import helmosdeep.views.FontUtils;

public class HexTile extends GameActor {
	private final static int MAX_CHAR_HEIGHT = 20;
	private final static double NORMAL_STROKE_WIDTH = 2.0;
	private final static double SELECTED_STROKE_WIDTH = 8.0;
	
	private final String label;
	private Tween<Double> selectTween;
	
	public HexTile(String label) {
		super(label);
		this.label = label;
		this.setProperty("strokeWidth", NORMAL_STROKE_WIDTH);
		this.setProperty("color", Color.WHITE);
	}
	
	public HexTile(int row, int col) {
		this(formatId(row, col));
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
	    
		var oldColor = graphics.getColor();
		graphics.setColor(getProperty("color", Color.class));
		graphics.fill(ShapesRepository.hexagon(getWidth()/2, getHeight()/2));
		graphics.setColor(oldColor);
		
		var fontMetrics = getProperty("fontMetrics", FontMetrics.class);
	    graphics.drawString(label, 
	    		0 - fontMetrics.stringWidth(label)/2, 
	    		-getHeight()/2 + 2*fontMetrics.getHeight());

	    
		graphics.draw(ShapesRepository.hexagon(getWidth()/2, getHeight()/2));

		graphics.setTransform(oldTransform);
	}

	@Override
	public void update(long dt) {
		
	}

	public void select() {
		if(selectTween == null || selectTween.isDone()) {
			selectTween = Tween.of(
					(width) -> setProperty("strokeWidth", width),
					NORMAL_STROKE_WIDTH, SELECTED_STROKE_WIDTH, 
					DoubleFunctions::easeOut, 
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
	
	public void setColor(Color c) {
		this.setProperty("color", c);
	}

	public Color getColor() {
		return 	this.getProperty("color", Color.class);
	}
	
	public static String formatId(int r, int c) {
		return "%02d-%02d".formatted(c, r);
	}
}
