import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Vector;

public class Shape {	
	class Line {
		int x1, y1, x2, y2;
		public Line(int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
		Boolean equals(Line o) {
			return x1 == o.x1 && y1 == o.y1 && x2 == o.x2 && y2 == o.y2;  
		}
		public int hashCode() {
			return x1 | y1 << 2 | x2 << 4 | y2 << 6;
		}
	}
	public int[][][] shape;
    public Pair[][] top;
    public Pair[][] bottom;
    public Pair[][] body;
    //public int[][][] outline;
    public int[][] baseshape;
    public BufferedImage[] img;
    public BufferedImage[] outlineimg;
    public Letter letter;
    public Color color;

    public final static int[][] zshape = 
    {{1,1,0},
     {0,1,1}};
    public final static int[][] ishape = 
    {{1},
     {1},
     {1},
     {1}};
    public final static int[][] oshape =
    {{1,1},
     {1,1}};
    public final static int[][] sshape = 
    {{0,1,1},
     {1,1,0}};
    public final static int[][] lshape =
    {{1,0},
     {1,0},
     {1,1}};
    public final static int[][] jshape =
    {{0,1},
     {0,1},
     {1,1}};
    public final static int[][] tshape =
    {{0,1,0},
     {1,1,1}};
    
    public static Color[] letter2color;    
    public static Shape[] shapes;
    
    public static void initshapes()
    {
    	
    	shapes = new Shape[Letter.values().length];
    	shapes[Letter.J.ordinal()] = new Shape(Letter.J, jshape, Color.RED);
    	shapes[Letter.O.ordinal()] = new Shape(Letter.O, oshape, Color.YELLOW);
    	shapes[Letter.L.ordinal()] = new Shape(Letter.L, lshape, Color.GREEN);
    	shapes[Letter.S.ordinal()] = new Shape(Letter.S, sshape, Color.CYAN);
    	shapes[Letter.I.ordinal()] = new Shape(Letter.I, ishape, Color.BLUE);
    	shapes[Letter.Z.ordinal()] = new Shape(Letter.Z, zshape, Color.MAGENTA);
    	shapes[Letter.T.ordinal()] = new Shape(Letter.T, tshape, Color.ORANGE);
    }
    
    void rotateright()
    {
        invert();
    	flip();
    }

    public void printShape(int[][] shape)
    {
        for (int y = 0; y < shape.length; y++) {                
            for (int x = 0; x < shape[0].length; x++) {
                System.out.print(shape[y][x]);
            }
            System.out.println();
        }
    }

    void invert()
    {
        int[][] newshape = new int[baseshape[0].length][baseshape.length];
        for (int x = 0; x < baseshape[0].length; x++)
            for (int y = 0; y < baseshape.length; y++)
                newshape[x][y] = baseshape[y][x];
        baseshape = newshape;
    }        

    void flip() // flip vertically
    {
        int ylen = baseshape.length;
        int temp;
        for(int x=0; x<baseshape[0].length; x++)
            for(int y=0; y<ylen/2; y++)
            {
                temp = baseshape[y][x];
                baseshape[y][x] = baseshape[ylen - y-1][x];
                baseshape[ylen - y-1][x] = temp;
            }
    }

    public Shape(Letter letter, int[][] template, Color color)
    {
    	final int ROTS = 4;
        this.baseshape = template;
        this.letter = letter;
        this.color = color;
        shape = new int[ROTS][][];
        body = new Pair[ROTS][4];
        bottom = new Pair[ROTS][];
        top = new Pair[ROTS][];
        //outline = new int[ROTS][][];
        img = new BufferedImage[ROTS];
        outlineimg = new BufferedImage[ROTS];
        for (int rot = 0; rot < ROTS; rot++)
        {            
        	shape[rot] = new int[baseshape.length][baseshape[0].length];
            int b = 0;
        	for (int x = 0; x < baseshape[0].length; x++)
                for (int y = 0; y < baseshape.length; y++) {
                	shape[rot][y][x] = baseshape[y][x];
                	if(shape[rot][y][x] == 1) {
                		body[rot][b] = new Pair(x, y);
                		b++;
                	}
                }
    		Vector<Pair> vbottom = new Vector<Pair>();
    		for(int x=0; x<baseshape[0].length; x++) {
    			for(int y=baseshape.length-1; y>-1; y--) {
    				if(baseshape[y][x] == 1) {
    					vbottom.add(new Pair(x, y+1));
    					break;
    				}
    			}
    		}
    		
    		Vector<Pair> vtop = new Vector<Pair>();
    		for(int x=0; x<baseshape[0].length; x++) {
    			for(int y=0; y<baseshape.length; y++) {
    				if(baseshape[y][x] == 1) {
    					vtop.add(new Pair(x, y-1));
    					break;
    				}
    			}
    		}
    		    		
    		bottom[rot] = new Pair[vbottom.size()];
    		vbottom.toArray(bottom[rot]);
    		top[rot] = new Pair[vtop.size()];
    		vtop.toArray(top[rot]);
    		
    		int[][] vline = new int[5][5];
    		int[][] hline = new int[5][5];
    		
    		for(Pair p: body[rot]) {
    		    int x = p.x;
    		    int y = p.y;
    			int[][] adjpoints = {{x, y, x+1, y}, {x, y, x, y+1}, {x+1, y, x+1, y+1}, {x, y+1, x+1, y+1}};    			
    			////System.out.format("%s {{%d, %d, %d, %d}, {%d, %d, %d, %d}, {%d, %d, %d, %d}, {%d, %d, %d, %d}}\n", letter, x, y, x+1, y, x, y, x, y+1, x+1, y, x+1, y+1, x, y+1, x+1, y+1);
    			for(int[] line : adjpoints) {
    				if(line[0] == line[2]) {
						if(vline[line[0]][line[1]] == 1)
							vline[line[0]][line[1]] = 0;
						else
							vline[line[0]][line[1]] = 1;
    				} else {
						if(hline[line[0]][line[1]] == 1)
							hline[line[0]][line[1]] = 0;
						else
							hline[line[0]][line[1]] = 1;
    				}
    			}
    		}
    		
    		img[rot] = new BufferedImage(baseshape[0].length*16, baseshape.length*16, BufferedImage.TYPE_INT_ARGB);
        	outlineimg[rot] = new BufferedImage(baseshape[0].length*16, baseshape.length*16, BufferedImage.TYPE_INT_ARGB);
    		Graphics2D g = img[rot].createGraphics();
    		Graphics2D outg = outlineimg[rot].createGraphics();
        	g.setColor(color);
        	
        	for(Pair p: body[rot])
        		g.fillRect(p.x*16, p.y*16, 16, 16);
        	
        	g.setColor(Color.WHITE);
        	for(int y=0; y<5; y++) {
        		for(int x=0; x<5; x++) {
        			if(hline[x][y] == 1) {
        				int dy = Math.min(y*16, img[rot].getHeight()-1);
        				g   .drawLine(x*16, dy, (x+1)*16-1, dy);
        				outg.drawLine(x*16, dy, (x+1)*16-1, dy);
        			}
        			if(vline[x][y] == 1) {
        				int dx = Math.min(x*16, img[rot].getWidth()-1);
        				g   .drawLine(dx, y*16, dx, (y+1)*16-1);
        				outg.drawLine(dx, y*16, dx, (y+1)*16-1);
        			}
        		}
        	}
            rotateright();
        }
    }
}