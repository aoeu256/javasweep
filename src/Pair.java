
public class Pair implements Comparable<Pair> {
	public int x, y;
	
	public Pair(int x, int y) {
		this.x = x;
		this.y = y;		
	}
	
	public String toString() {
		return "("+x+","+y+")";
	}

	public int compareTo(Pair o) {				
		int a = x | y << 8;
		int b = o.x | o.y << 8;
		if(a < b) return -1;
		else if(a > b) return 1;
		return 0;
	}
	
	public int hashCode() {
		//System.out.format("(%d, %d) hash:%4x\n", x, y, x + (y << 8));
		return x | (y << 8);
	}
	
	public boolean equals(Object b) {
		if(b == null) return false;
		return x == ((Pair)b).x && y == ((Pair)b).y;
	}
	
}
