
import java.awt.event.*;
import java.awt.*;
import java.util.*;

import javax.swing.JApplet;
import javax.swing.JComponent;

public class Area extends JComponent implements KeyListener, MouseMotionListener, MouseListener {
	private static final long serialVersionUID = 1L;
    public int[][] map;
    public HashMap<Pair, Piece> map2piece;
    public Pair pos = new Pair(0, 0);
    public int update = 1;
    public int width = 28, height = 10;
    public int blocksize = 16;
    public int piecetarget = 40;
    public int chaint = update * 40;
    public int chainreset = update * 8;
    public int fallt = update * 3;
    public long drawt = 0;
    private Vector<Piece> chainresetgroup;
    public Random rand;

    // used for finding star alignments 
    Pair[] starx;
    Pair[] stary;

    Vector<Piece> pieces;

    public int delay = 0;
    public int repeatRate = 1;
    public int pause = 10;
    public int t = 0;
    public Boolean replaymode = false;
    //public List replay;
    public int chain = 1;
    public int chaintimer = 0;
    public int score = 0;
    
    Pair selectpos; // Screwy stuff happens without this
    
    public final static int Updatemap = 0;
    public final static int Updatepieces = 1;
    public final static int Updateplace = 2;
    //public final static int Update
    public boolean[] updateflag; // should this be updated
    HashSet<Integer> buttonheld; // Holds if a few buttons were being held

    public Vector<Combo> combos;
    public Vector<Task> tasks;

    public Cursor cur;
    
    public int place1 = KeyEvent.VK_A; 
    public int place2 = KeyEvent.VK_O;
    public int rotateright = KeyEvent.VK_E;
    public int rotateleft = KeyEvent.VK_U;
    public int piecemove = KeyEvent.VK_SPACE;
    public int up = KeyEvent.VK_UP;
    public int down = KeyEvent.VK_DOWN;
    public int left = KeyEvent.VK_LEFT;
    public int right = KeyEvent.VK_RIGHT;
	//private JApplet main;
	public Piece chainstopper;
	@SuppressWarnings("unused")
	public Main main;
	private Pair mousepos;
	private Pair cursordrawpos = new Pair(0, 0);

    public boolean blockbounds(int x, int y)
    {
//        System.out.format("%d, %d\n", x, y);
//        System.out.format("%d, %d\n", width, height);
    	return (x >= 0 && x < width) && (y >= 0 && y < height);
    }
    public boolean pixelbounds(Pair pos)
    {
        return true;
    }
    
    public Area(Main main)
    {
        super();
        this.main = main;
		pieces = new Vector<Piece>(64);
		rand = new Random();
        map = new int[height][width];
    	map2piece = new HashMap<Pair, Piece>();
        tasks = new Vector<Task>(16);
        combos = new Vector<Combo>();
        cur = new Cursor(this);
        updateflag = new boolean[8];
        mousepos = new Pair(0, 0);
        buttonheld = new HashSet<Integer>();
        chainresetgroup = new Vector<Piece>();
        this.setBackground(Color.BLACK);
        this.setBounds(pos.x, pos.y, width*blocksize, height*blocksize);
        int x = 5 * 5 * 2 * 5;
    }
    
    public Pair midpixelpos(Pair block)
    {
        return new Pair(block.x*blocksize+blocksize/2+pos.x, block.y*blocksize+blocksize/2+pos.y);
    }
	

    public void paintComponent(Graphics g) {
    	g.setColor(Color.BLACK);
    	g.fillRect(pos.x, pos.y, pos.x+getWidth(), pos.y+getHeight());
    	g.setColor(Color.WHITE);
		g.drawRect(pos.x, pos.y, pos.x+width*blocksize, pos.y+height*blocksize);
		String text = "cursor:("+cur.pos.x+","+cur.pos.y+")";
		String text2 = "chain:"+chain;
		String text3 = "score:"+score;
		g.drawString(text, width*blocksize+8, 16);
		g.drawString(text2, width*blocksize+8, 32);
		g.drawString(text3, width*blocksize+8, 48);
		for(Piece p: pieces) {
			if(!p.deleted)
				p.paint(this, g, true);		
		}
		if(this.cur.p == null) {
			Pair ovpos = this.pixelpos(this.cursordrawpos);
			g.drawRect(ovpos.x, ovpos.y, blocksize, blocksize);
		}
//		cur.paint(g);
//		for(Pair p : map2piece.keySet()) {
//			Pair o = this.pixelpos(p);
//			g.setColor(Color.WHITE);
//			g.drawOval(o.x, o.y, blocksize, blocksize);
//		}
		for(Task i : tasks) {
			i.paint(this, g);
		}
	}
    
    
    
    public void keyPressed(KeyEvent e) {
		
    	int code = e.getKeyCode();
    	/*
    	if     (code == up)    cur.moveup();
    	else if(code == right) cur.moveright();
    	else if(code == down)  cur.movedown();
    	else if(code == left)  cur.moveleft();
    	*/
    	if(code == KeyEvent.VK_1) this.genstack();
    	else if(code == place1 || code == place2) {
    		updateflag[Updateplace] = true;
    		selectpos = new Pair(cur.pos.x, cur.pos.y);
    	}
    	else if(code == rotateright) {
        	if(cur.p != null && !buttonheld.contains(rotateright))
    			cur.p.rotateright();
    		buttonheld.add(rotateright);
    	}
    	
        else if(code == rotateleft) {
        	if(cur.p != null && !buttonheld.contains(rotateleft))
    			cur.p.rotateleft();
    		buttonheld.add(rotateleft);
        }
        else if(code == KeyEvent.VK_M)
        	printmap();
        else if(code == KeyEvent.VK_2)
        	System.out.println("map2piece is"+ map2piece);
        else if(code == KeyEvent.VK_C) {
        	System.out.println("The combos are");
        	for(Combo comb : combos) {
        		System.out.format("%3d %s", comb.combot, comb.combo);
        	}
        }
        else if(code == KeyEvent.VK_P) {
        	System.out.println("Pieces are");
        	for(Piece p : pieces) {
        		System.out.println(p);
        	}
        }
        else if(code == KeyEvent.VK_T) {
        	for(Task t : tasks) {
        		System.out.println("task:"+t);
        		
        	}
        }
    	this.repaint();
	}
    
    private void press(int code) {
		if(cur.p != null) {
			if(code == rotateright)
				cur.p.rotateright();
			if(code == rotateleft)
				cur.p.rotateleft();
		}
		//if(buttonheld.contains(code)) {
		//	buttonheld.remove(code);
		//}
			
		buttonheld.add(code);
    }
    
	public synchronized void grabpiece() {
		if(cur.p == null) {
			Piece p = map2piece.get(selectpos);				
			System.out.print("got "+p+"\n");
			if(p != null && !p.incombo) {
				cur.setPiece(p);  
				p.unmap(this);	
				// tell adj top piece that it can start to fall
				for(Piece fp : p.adjpieces(p.shape.top[p.rot], this)) {
					if(fp.canfall(this))
						fp.fallt = this.fallt - 1;
				}
				System.out.println("grab piece");
				if(buttonheld.contains(rotateright))
					cur.p.rotateright();
				if(buttonheld.contains(rotateleft))
					cur.p.rotateleft();
				updateflag[Updatepieces] = true;
				p.fallt = this.fallt; // Reset timers
				p.canchain = false;
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		buttonheld.remove(e.getKeyCode());		
	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public int midpixelx(int blockx) {
		return blockx*blocksize+blocksize/2+pos.x; 
	}
	
	public int midpixely(int blocky) {
		return blocky*blocksize+blocksize/2+pos.y;
	}
	
	public Pair pixelpos(Pair block) {
		return new Pair(block.x*blocksize+pos.y, block.y*blocksize+pos.y);
	}
	
	public void clearmap()
	{
		for (int y=0; y<height; y++)
			for(int x=0; x<width; x++)
				map[y][x] = 0;
	}
	
	public void printmap() {
		System.out.println("----------------------------");
		for (int y=0; y<height; y++) {
			for(int x=0; x<width; x++)
				System.out.print(map[y][x]);
			System.out.println("");
		}
		System.out.println("----------------------------");
	}

	Letter randomletter()
	{
		final Letter[] letters = Letter.values();
		return letters[rand.nextInt(letters.length)];
	}
	
	public synchronized void placepiece() {
		if(cur.p.canfit(this)) {				
			cur.pos = new Pair(selectpos.x, selectpos.y);
			cur.p.map(this);
			if(cur.p.canfall(this))
				cur.p.fallt = fallt - 1; // start the piece falling
			cur.p = null;
		}
		else {
//			// Bubble the piece up
			boolean deletepiece = false;
			while(!cur.p.canfit(this)) {
				cur.p.pos.y -= 1;
				if(cur.p.pos.y < 0) {
					deletepiece = true;
					break;
				}
			}
//			if(deletepiece) {
//				cur.p.deleted = true;
//			}
			if(!deletepiece) {
				cur.p.map(this);
				cur.p = null;
				updateflag[Updatepieces] = true;
			}
		}
	}
	
	public void genstack()
	{
		cur.p = null;
		clearmap();
		pieces.clear();
		map2piece.clear();
		
		//pieceTarget = {'J':2, 'O':2, 'L':2, 'Z':2, 'I':2, 'S':2, 'T':2}
		//sumtarget = 25#sum(pieceTarget.values());
		final int sumtarget = 30;
		final int tries = 100;
		//fp = fps.FPSManager(
		for(int i=0; i<tries; i++)
		{
			
			if(pieces.size() == sumtarget)
				break;
			Letter letter = randomletter();

			Piece p = new Piece(this, new Pair(0,0), letter, rand.nextInt(4));
			p.pos.x = rand.nextInt(width - p.width() + 1);
			//p.x = ika.Random(0, width - len(p.shape[0]) + 1);`
			p.bottomfall(this);
			
			if(p.pos.y > 0 && !(p.cancombo(this))) {
				p.domap(this);
				
				pieces.add(p);
				//printmap();
			}
		}
		if(pieces.size() < sumtarget)
			genstack();
			
		for(Piece p: pieces)
			p.dodict(map2piece);
		
		System.out.println("map2piece is " + map2piece);
		System.gc();		
	}
	public void test1() {
		clearmap();
		pieces.clear();
		map2piece.clear();
		
		//pieceTarget = {'J':2, 'O':2, 'L':2, 'Z':2, 'I':2, 'S':2, 'T':2}
		//sumtarget = 25#sum(pieceTarget.values());
		final int sumtarget = 30;
		final int tries = 100;
		//fp = fps.FPSManager(
		
		
		Piece a = new Piece(this, new Pair(0,0), Letter.O, 0);
		a.bottomfall(this);
		
		Piece b = new Piece(this, new Pair(0, 0), Letter.O, 0);
		
		
		
	}	
	void image(String imagename) {
		
	}
	
	void gameupdate() {
		drawt++;		
		for(Task i : tasks) {
			i.update();
		}
		
		// update input
		if (updateflag[Updateplace]) {
			if(cur.p == null)
				grabpiece();
			else
				placepiece();
			updateflag[Updateplace] = false;
		}
		
		updatecombos();
		updatemap();		
		updatepieces(); // let the pieces fall
		
		this.repaint();
	}
	private void updatepieces() {
		if(!updateflag[Updatepieces])
			return;
		
		for(Piece p : pieces) {
			if(!p.deleted && p != cur.p)
				p.gameupdate(this);
		}
		boolean resetchain = true;
		for(Piece p : this.chainresetgroup) {
			if(p.canchain) {
				resetchain = false;
				break;
			}	
		}
		if(resetchain) {				
			if(chain != 1)
				System.out.println("Reset chain!");
			chain = 1;
		}			
	}
	private void updatemap() {
		updateflag[Updatemap] = true;
		if(updateflag[Updatemap]) {
			clearmap();
			map2piece.clear();
			for(Piece p : pieces) {
				if(p != cur.p && !p.deleted) {
					p.map(this);
				}
			}
			updateflag[Updatemap] = false;
		}
	}
	private void updatecombos() {
		Combo combo = new Combo(this);
		
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				if(map[y][x] > 1) {
					int color = map[y][x];
					if(y+1 < height && map[y+1][x] == color)
						tryaddpiecegroup(y, x, y+1, x, combo);
					if(x+1 < width && map[y][x+1] == color)
						tryaddpiecegroup(y, x, y, x+1, combo);
				}
			}
		}
		if(combo.combo.size() > 1) {
			updateflag[Updatemap] = true;
			
			// If any piece inside the combo can chain raise the chain number
			boolean canchain = false;
			int cardx = 0;
			
			int cardy = 0;
			
			for(Piece i : combo.combo) {
				if(i.canchain)
					canchain = true;
				cardx += i.pos.x;
				cardy += i.pos.y;
				System.out.println("x:" + i.pos.x + "y:" + i.pos.y);
			}
			if(canchain) {
				chain++;
			}
			combos.add(combo);
			System.out.println("COMBO!"+combo.combo.size());
			String text = "";
			if(chain > 1) {
				text = chain + "x CHAIN";
				score += (chain * chain);
			}
			if(combo.combo.size() > 2) {
				text = text + " " + combo.combo.size() + " COMBO";
				if(combo.combo.size() == 4)
					score += 200;
				else
					score += 10 * (combo.combo.size() * combo.combo.size()); 
				
			}			
			cardx /= combo.combo.size();
			cardy /= combo.combo.size();
			Pair cardpos = new Pair(cardx, cardy);
			System.out.println("After:" + cardx + "," + cardy);
			System.out.println("Pixel:" + pixelpos(cardpos));
			
			
			if (!text.equals(""))
				tasks.add(new Card(this, this.pixelpos(cardpos), text));
			
			playSound("clear");
		}
		// update chains
		Vector<Combo> newcombos = new Vector<Combo>(combos.size());
		Vector<Combo> oldcombo;
		chainresetgroup.clear();
		for (Combo comb : combos) {
			comb.combot -= 1;
			if (comb.combot == 0) {
				for (Piece i : comb.combo) {
					i.deleted = true;
					for(Pair b : i.body()) {
						Pair pos = 	midpixelpos(i.realpos(b));
						tasks.add(new PieceExplosion(this, pos.x, pos.y, i.color));					
					}
					for(Piece p : i.fallpieces(this)) {
						System.out.println(p + " can now chain!");
						p.canchain = true;
						p.fallt = fallt - 1; // let the piece start falling
						chainresetgroup.add(p);
					}
				}
			}
			else {
				newcombos.add(comb);
			}
		}
		oldcombo = combos;
		combos = newcombos;
		oldcombo = null;
	}
	
	private Task PieceExplosion(Area area, int x, int y, int color) {
		// TODO Auto-generated method stub
		return null;
	}
	private void playSound(String string) {
		System.out.println("Playning sound " + string);		
	}
	
	public void addScore(int score) {
		this.score += score;
	}
	
	private void tryaddpiecegroup(int y, int x, int y2, int x2, Combo combo) {
		Piece a = map2piece.get(new Pair(x, y));
		Piece b = map2piece.get(new Pair(x2, y2));
		if(a == null || b == null) {
			System.out.println("ERROR!");
			printmap();
			System.out.println(map2piece);
			System.out.format("%d,%d %d,%d \n", x, y, x2, y2);
		}
		boolean chainteq = a.chaint == b.chaint && a.chaint == this.chaint && b.chaint == this.chaint;
		boolean fallteq = a.fallt == b.fallt && a.fallt == fallt && b.fallt == fallt;
		if(chainteq && fallteq) {
			combo.combo.add(a);
			combo.combo.add(b);
			a.incombo = true;
			b.incombo = true;
		}
	}
	
	public int clamp(int a, int lo, int hi) {
		return Math.min(hi, Math.max(a, lo));
	}
	
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	public void mouseMoved(MouseEvent e) {
		if(cur.p != null) {
			//cur.p.pos = blockpos(e.getX(), e.getY());
			//cur.p.pos.x = clamp(cur.p.pos.x, 0, width - cur.p.width());
			//cur.p.pos.y = clamp(cur.p.pos.y, 0, height - cur.p.height());
			cur.p.pos = this.mouse2block(e.getX(), e.getY(), cur.p.width(), cur.p.height());
		}
		else {
			this.cursordrawpos = mouse2block(e.getX(), e.getY(), 1, 1);
			//System.out.println(this.cursordrawpos+" "+e.getX());
		}
	}
	
	private Pair mouse2block(int mx, int my, int width, int height) {
		Pair p = blockpos(mx, my);
		p.x = clamp(p.x, 0, this.width - width);
		p.y = clamp(p.y, 0, this.height - height);
		return p;
	}

	public void mouseClicked(MouseEvent e) {
		//onPlace(e);
	}
	
	/**
	 * Converts pixel coordinates to block coordinates.
	 * @return A pair representing the block coordinates.
	 */
	public Pair blockpos(int x, int y) {
		Pair a = new Pair((x - this.pos.x) / blocksize, (y - this.pos.y) / blocksize);
		a.x = Math.min(Math.max(a.x, 0), width);
		a.y = Math.min(Math.max(a.y, 0), height);
		return a;
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {

		/*
		mousepos.x = e.getX();
		mousepos.y = e.getY();
		//if(cur.p == null) {
		System.out.println("Mouse pos is "+mousepos);
		selectpos = blockpos(mousepos.x, mousepos.y);			
		updateflag[Updateplace] = true;
		//}
		 * 
		 */
		onPlace(e);
	}
	public void mouseReleased(MouseEvent e) {		
		onPlace(e);
	}
	
	public void onPlace(MouseEvent e) {
		mousepos.x = e.getX();
		mousepos.y = e.getY();
		selectpos = blockpos(mousepos.x, mousepos.y);
		updateflag[Updateplace] = true;		
	}
}
