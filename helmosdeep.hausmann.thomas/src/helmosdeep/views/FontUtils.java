package helmosdeep.views;

import java.awt.FontMetrics;
import java.awt.Graphics;

public final class FontUtils {
	
	public static FontMetrics fitFont(Graphics graphics, String longString, int height, int xSpace) {
		var font = graphics
				.getFont()
				.deriveFont(height*1f);
		graphics.setFont(font);
		var metrics = graphics.getFontMetrics();
		var actualSpace = metrics.stringWidth(longString);
		
		while(actualSpace > xSpace) {
			--height;
			font = font.deriveFont(height*1f);
			graphics.setFont(font);
			metrics = graphics.getFontMetrics();
			actualSpace = metrics.stringWidth(longString);
			if(height < 8) {
				break;
			}
		}
		

		return metrics;
	}
}
