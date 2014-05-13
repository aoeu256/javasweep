import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Card extends Task {
	Pair pos;
	double yspeed = -8.123f;
	static final double yaccel = 3.734f;
	double ypos; 
	BufferedImage img;
	int width;
	int height;
	
	public Card(Area area, Pair ori, String text) {
		super(600);
		pos = ori;
		ypos = pos.y;
		width = 200;
		height = 200;
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		g.drawString(text, 8, 8);
		
	}
	
	public void paint(Area area, Graphics g) {
		g.drawImage(img, pos.x, pos.y, width, height, null);
		
	}
	
	public void gameupdate() {
		yspeed += Card.yaccel;
		ypos += yspeed;
		pos.y = (int)ypos;
	}
}
