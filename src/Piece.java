import java.awt.Color;
import java.awt.Graphics;
import java.util.*;
import java.awt.image.BufferedImage;

;

public class Piece {
	public Shape shape;
	public int rot;
	int id;
	public Pair pos;
	public Pair starpos;
	public boolean canchain = false;
	public boolean incombo = false;
	public boolean deleted = false;
	// public int flags;
	public int chainreset;
	public int chaint;
	public int fallt;
	public Area area;
	public int color;
	
	public final static Color[] colors = 
		{Color.WHITE, Color.BLACK, Color.BLUE, Color.RED, Color.GREEN};
	
	public final static String[] colorname = 
	{"WHITE", "BLACK", "BLUE", "RED", "GREEN"};

	// for use in can combo
	private static final int[][] adj = { { 1, 0 }, { -1, 0 }, { 0, 1 },
			{ 0, -1 } };

	public int width() {
		return shape.shape[rot][0].length;
	}

	public int height() {
		return shape.shape[rot].length;

	}

	public void rotateright() {
		rot--;
		if (rot < 0)
			rot = 3;
		int temp = starpos.x;
		starpos.x = shape.shape[rot][0].length - starpos.y - 1;
		starpos.y = temp;
	}

	public void rotateleft() {
		rot = (rot + 1) % 4;
		int temp = starpos.x;
		starpos.x = starpos.y;
		starpos.y = shape.shape[rot].length - temp - 1;
	}

	public Piece(Area area, Pair pos, Letter letter, int rot) {
		this.color = area.rand.nextInt(Piece.colors.length - 2) + 2;
		this.pos = pos;
		this.shape = Shape.shapes[letter.ordinal()];
		this.chaint = area.chaint;
		this.chainreset = area.chainreset;
		// falling = False
		this.fallt = area.fallt;
		this.rot = rot;
		Pair temp = shape.body[rot][area.rand.nextInt(shape.body[rot].length)];
		this.starpos = new Pair(temp.x, temp.y);
	}

	Boolean canfit(Area area) {		
		for (Pair p : shape.body[rot]) {
			Pair p2 = realpos(p);
			if (area.map[p2.y][p2.x] > 0)
				return false;
		}
		return true;
	}

	public void paint(Area area, Graphics g, Boolean fill) {
		/*
		 * for(Pair p: shape.body[rot]) { Pair p1 = area.pixelpos(new
		 * Pair(p.x+pos.x, p.y+pos.y)); g.setColor(shape.color);
		 * System.out.format("p1x %d, p1y %d\n", p1.x, p1.y); g.fillRect(p1.x,
		 * p1.y, area.blocksize, area.blocksize); g.setColor(Color.WHITE);
		 * g.drawRect(p1.x, p1.y, area.blocksize, area.blocksize); }
		 * g.setColor(Color.WHITE);
		 */
		Pair star = area.pixelpos(new Pair(starpos.x + pos.x, starpos.y + pos.y));
		Pair p = area.pixelpos(pos);
		BufferedImage im;
		/*
		if (fill && !incombo)
			im = shape.img[rot];
		else
			im = shape.outlineimg[rot];
		*/
    	if(incombo && area.drawt % 4 == 0)
    		g.setColor(Color.WHITE);
    	else
    		g.setColor(Piece.colors[this.color]);
		for(Pair bp: shape.body[rot]) {
    		Pair ip = area.pixelpos(new Pair(bp.x+pos.x, bp.y+pos.y));
    		g.fillRect(ip.x, ip.y, 16, 16);
    	}
		
		im = shape.outlineimg[rot];
		g.drawImage(im, p.x, p.y, im.getWidth(), im.getHeight(), null);
		g.drawImage(Main.starimg, star.x, star.y, Main.starimg.getWidth(),
				Main.starimg.getHeight(), null);

		if (Main.debug == true) {
			g.drawString(String.valueOf(fallt), p.x, p.y);

			// for(Pair pair : shape.bottom[rot]) {
			// Pair o = area.pixelpos(realpos(pair));
			// g.setColor(Color.RED);
			// g.drawOval(o.x, o.y, area.blocksize, area.blocksize);
			// }
			g.setColor(Color.PINK);
			if (canchain) {
				g.drawOval(p.x, p.y, area.blocksize, area.blocksize);
			}
		}
	}

	public void domap(Area area) {
		for (Pair p : shape.body[rot]) {
			try {
				if (p.x == starpos.x && p.y == starpos.y) {
					int s = this.color;
					if (incombo)
						s = 1;
					area.map[p.y + pos.y][p.x + pos.x] = s;
				} else
					area.map[p.y + pos.y][p.x + pos.x] = 1;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.format("%d %d height:%d width:%d\n", p.x + pos.x,
						p.y + pos.y, area.height, area.width);
				throw e;
			}
		}
	}

	public void map(Area area) {
		domap(area);
		if (!incombo)
			dodict(area.map2piece);
	}

	public void unmap(Area area) {
		clearmap(area);
		cleardict(area.map2piece);
	}

	public void dodict(HashMap<Pair, Piece> map2piece) {
		for (Pair p : shape.body[rot])
			map2piece.put(new Pair(p.x + pos.x, p.y + pos.y), this);
	}

	public void clearmap(Area area) {
		for (Pair p : shape.body[rot])
			area.map[p.y + pos.y][p.x + pos.x] = 0;
	}

	public String toString() {
		String rotstring = "";
		if (rot == 0)
			rotstring = "^";
		if (rot == 1)
			rotstring = ">";
		if (rot == 2)
			rotstring = "v";
		if (rot == 4)
			rotstring = "<";		
		return "" + shape.letter + "" + pos + "*" + starpos + rotstring + Piece.colorname[this.color];
	}

	public void cleardict(HashMap<Pair, Piece> map2piece) {
		for (Pair p : shape.body[rot])
			map2piece.remove(new Pair(p.x + pos.x, p.y + pos.y));
	}

	public void gameupdate(Area area) {
		if (fallt < area.fallt) {
			if (fallt > 0)
				fallt -= 1;
			else {
				if (canfall(area)) {
					//System.out.format("calling fallpieces for piece %s\n", this);
					//System.out.println(area.map2piece);
					for (Piece p : this.fallpieces(area)) {
						//System.out.format("Piece %s: ready to fall!\n",
						//		p.shape.letter);
						
						p.pos.y += 1;
					}
					pos.y += 1;
					area.updateflag[Area.Updatemap] = true;
				} else {
					// if(!cancombo(area)) {
					// // sound['land'].Play();
					// }
					// else {
					// for(Piece p: this.fallpieces(area))
					// p.canchain = false;
					// canchain = false;
					// }
					fallt = area.fallt;
				}
			}
		}
		if (fallt == area.fallt) {
			if (canchain) {
				// log('chain reset for %d is %d' % (id, chainreset));				
				if (chainreset > 0) {
					chainreset -= 1;
					System.out.println(this+" "+chainreset);
				}
				else if (chainreset == 0) {
					chainreset = area.chainreset;
					//System.out.println("chaining deactivated");
					for (Piece p : this.fallpieces(area))
						p.canchain = false;
					canchain = false;
				}
			}
		}
	}

	public boolean cancombo(Area area) {
		int x = pos.x + starpos.x;
		int y = pos.y + starpos.y;
		for (int[] a : Piece.adj) {
			int x2 = x + a[0];
			int y2 = y + a[1];
			if (x2 < 0 || y2 < 0 || y2 >= area.height || x2 >= area.width)
			{}
			else if (area.map[y2][x2] == this.color)
				return true;
		}
		return false;
	}

	public boolean canfall(Area area) {
		if (chaint < area.chaint)
			return false;
		for (Pair p : shape.bottom[rot]) {
			if (pos.x + p.x == area.map[0].length) {
				System.out.println("area hegiht:" + (pos.x + p.x) + " "
						+ area.map[0].length);
				System.out.format("width:%d", width());
				shape.printShape(shape.shape[rot]);
			}
			if (p.y + pos.y < 0) {
			} else if (p.y + pos.y < area.height
					&& area.map[pos.y + p.y][pos.x + p.x] == 0) {
			} else
				return false;
		}
		return true;
	}

	void bottomfall(Area area) {
		pos.y = -3;
		while (canfall(area))
			pos.y++;
	}

	HashSet<Piece> adjpieces(Pair[] coords, Area area) {
		HashSet<Piece> s = new HashSet<Piece>();
		// System.out.println(area.map2piece);
		for (Pair p : coords) {
			Pair p1 = realpos(p);
			Piece piece = area.map2piece.get(p1);
			// System.out.format("coords:%s real:%s self:%s\n", p, p1, pos);
			if (piece != null) {
				s.add(piece);
				// ssSystem.out.format("got piece %s\n", piece.letter);
			}
		}

		return s;
	}

	public Pair realpos(Pair relative) {
		return new Pair(relative.x + pos.x, relative.y + pos.y);
	}

	public HashSet<Piece> alltoppieces(Area area) {
		HashSet<Piece> s = new HashSet<Piece>();
//		System.out.format("%sadjpieces:%s %s\n", this, adjpieces(
//				shape.top[rot], area), shape.top[rot]);
		for (Piece p : adjpieces(shape.top[rot], area)) {
			if (this != p) {
				s.add(p);
				s.addAll(p.alltoppieces(area));
			}
		}
		return s;
	}

	HashSet<Piece> fallpieces(Area area) {
		HashSet<Piece> alltopset = alltoppieces(area);
		HashSet<Piece> b = new HashSet<Piece>();
		HashSet<Piece> remv = new HashSet<Piece>();
//		System.out.format("%salltopset:%s\n", this, alltopset);
		alltopset.add(this);
		for (Piece i : alltopset) {
			if (!(i == this)) {
//				System.out.format("%s bottom has %s\n", i, i.adjpieces(
//						shape.bottom[i.rot], area));
//				System.out.format("%s alltopset has %s\n", this, alltopset);
				if (alltopset.containsAll(i.adjpieces(i.shape.bottom[i.rot],
						area))) {
//					System.out.println(i + " added.");
					b.add(i);
				} else {
//					System.out.println("Removed " + i);
					remv.addAll(i.alltoppieces(area));
				}
			}
		}
		b.removeAll(remv);
		return b;
	}
	
	public Pair[] body() {
		return this.shape.body[rot];
	}
}
