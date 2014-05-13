import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


public class PieceExplosion extends Task {
	
	class Shard {
		public double x, y;
		public double dx, dy;
		
		BufferedImage im;
		
		Shard(int x, int y, double dx, double dy, BufferedImage im) {
			this.x = x;
			this.y = y;
			this.dx = dx;
			this.dy = dy;
			this.im = im;
		}

		public void paint(Graphics2D g) {
			//BufferedImageOp = new BufferedImageOp();
			
			g.drawImage(im, (int)x, (int)y, im.getWidth(), im.getHeight(), null);
		}
	}

	private int color;
	private Shard[] shards;
	private int t = 0;
	private int deletet;
	private Area area;
	private double maxyspeed;
	final public double gravity = 0.3;
	
	static BufferedImage[] shardIm = null;
	
	public int randInt(int max) {
		return (int)(this.area.rand.nextDouble() * max - 0.0001);
	}
	
	public PieceExplosion(Area area, int x, int y, int color) {
		super(200);
		final int n = 5;
		this.color = color;
		this.area = area;
		this.shards = new Shard[n];
		this.t = 0;
		double spd = 1.5;
		double spd_2 = spd / 2.0;
		maxyspeed = 0.0;
		
		if(shardIm == null) {
			int nshards = 4;
			shardIm = new BufferedImage[nshards*2];
			for(int i=0; i<nshards; i++) {
				shardIm[i]         = Main.getimage("bigshard-"+i);
				shardIm[i+nshards] = Main.getimage("smallshard-"+i);
			}
		}
		for(int i=0; i<n; i++) {
			
			double yspeed = spd_2 - (spd * area.rand.nextDouble());
			if(yspeed < maxyspeed)
				maxyspeed = yspeed;
			shards[i] = new Shard(x, y, spd_2 - (spd * area.rand.nextDouble()), yspeed, shardIm[randInt(shardIm.length)]);			
		}
		double c = area.getHeight()+area.getY()-y;
		double a = this.gravity / 2;
		this.deletet = (int)((-maxyspeed + Math.sqrt(maxyspeed*maxyspeed - 4*a*c))/(2*a)) + 1;
		this.deletet = (int)(this.area.main.seconds2ticks((long)this.deletet));
	}
	
	
	void gameupdate() {
		for (Shard s : shards) {
			//ika.Video.TintBlit(i.image, int(i.x) - i.image.width/2, int(i.y) - i.image.height/2, self.color)
			s.x += s.dx;
			s.y += s.dy;
			s.dy += this.gravity;
		}
		this.t++;
	}
	
	boolean canDelete() {		
		return this.t == this.deletet;
	}
	
	void paint(Area area, Graphics g) {
		for(Shard s: shards) {
			s.paint( (Graphics2D)g );
		}
	}
}
