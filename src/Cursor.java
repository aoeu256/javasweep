import java.awt.*;

public class Cursor {
	public Piece p;
	public Pair pos;
	public Boolean grabMode = true;
	public Area area;
	int size;
	
	public Cursor(Area area) {
		this.area = area;
		size = area.blocksize * 8;
		p = null;
		pos = new Pair(area.width / 2, area.height  / 2);
	}
	
	void paint(Graphics g) {
		if(p == null) {
			int x = area.midpixelx(pos.x);
			int y = area.midpixely(pos.y);
			g.setColor(Color.RED);
			g.drawLine(x-size, y, x+size, y);
			g.drawLine(x, y-size, x, y+size);
		}
		else {			
			//Pair pix = area.pixelpos(p.pos);
			p.paint(area, g, false);
		}
	}
	
	//void moveup()    {if (pos.y > 0) pos.y -= 1; }
	
	void moveup()    {
		if(pos.y > 0) pos.y--;		
	}	
	void movedown()  {
		if (pos.y < area.height-1) pos.y += 1;
	}
	void moveleft()  {
		if (pos.x > 0) pos.x -= 1;
	}
	void moveright() {
		if (pos.x < area.width-1) pos.x += 1;
	}
	
	void setPiece(Piece p) {
		this.p = p;
		pos = p.pos;
	}
}
