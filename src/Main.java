
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.Timer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Main extends JApplet implements ActionListener, KeyListener {		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Vector<Area> areas = new Vector<Area>();
	//public Image offscreen;
	public Graphics bufGraphics;
	public Timer timer;
	public static BufferedImage starimg;
	public static boolean debug = true;
	public long msupdate = 100;
	private static HashMap<String, BufferedImage> images;
	
	public void initimages() {
		File imagedir = new File("image");
		String[] imagenames = imagedir.list();
		if (imagenames == null)
			System.out.print("Image directory does not exist!");
		
		int[][] graph = {{1, 2}, {3}, {0}, {1}};
		
		images = new HashMap<String, BufferedImage>(imagenames.length);
		
		for(String name : imagenames) {
			URL base = getClass().getResource("image/"+name);
			
			System.out.println("base "+ base + "name=" + name);
	
			BufferedImage img;
			try {
				img = ImageIO.read(base);
				String key = name.substring(0, name.length() - 4);
				System.out.println("splits"+key);				
				images.put(key, img);				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Main.starimg = images.get("star");
	}
	
	static BufferedImage getimage(String name) {
		//System.out.println("Getting "+ name + " images is "+images.keySet());
		return images.get(name);
	}
	
	public void addTopics(String[] topics, String category, HashMap<String, String> item2topics) {
		for(String i : topics) {
			item2topics.put(i, category);
		}
	}
	
	public Main() {
		super();
		this.setSize(640, 480);
		Shape.initshapes();
		initimages();
		Area player1 = new Area(this);
		
		areas.add(player1);		
		for(Area area : areas) {
			this.add(area);
			//area.addKeyListener(this);
			this.addKeyListener(area);
			this.addMouseListener(area);
			this.addMouseMotionListener(area);
		}
		this.addKeyListener(this);
		this.requestFocusInWindow();
	}
	
	public void start()
	{		
		this.setBackground(Color.BLACK);
		//System.out.format("Focus: %s %s\n", this.requestFocusInWindow(), this.isFocusable());
		for(Area area : areas) {
			//System.out.format("Focus: %s %s\n", area.requestFocusInWindow(), area.isFocusable());
			//area.genstack();
		}
		//paint(this.getGraphics());
		timer = new Timer();
		timer.schedule(new UpdateThread(areas), 0, msupdate);
		
	}
	
	public long seconds2ticks(long seconds) {
		return seconds * 1000 / msupdate;
	}
	
	public void actionPerformed(ActionEvent e) {
		
	}

	public static void main(String[] args) {
		new Main();		
	}
/*
	public void update(Graphics g) {
		bufGraphics.clearRect(0, 0, getWidth(), getHeight());
		paint(bufGraphics);
		g.drawImage(offscreen, 0, 0, this);

	}

	public void paint(Graphics g) {		
		for(Area area: areas)
	 		area.paint(bufGraphics);		
	}
*/	
	public void keyPressed(KeyEvent e) {
	/*
		for(Area area : areas)
			area.keyPressed(e);
			*/
	}

	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent arg0) {
	}
}
