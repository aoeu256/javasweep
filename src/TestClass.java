import java.util.HashSet;
import java.util.Random;

public class TestClass {

	public static void main(String[] args) {		
		final int MAXTRIES = 16;
		final long MAXTRIAL = 500l;
		
		int array[] = {20, 30, 8, 21, 1, 22, 23}; 
		int rdata[][] = {{1, 3, 30}, {20, 1}, {3}, {1,3}, {2,4}, {6, 8}, {22, 23}, {6, 1}}; 
		
		int b = 0;
		int c = 0;
		long a = System.currentTimeMillis();
		for(int j=0; j<MAXTRIAL; j++) {
			HashSet<Integer> beta = new HashSet<Integer>();
			HashSet<Integer> remove = new HashSet<Integer>();
			for(int i=0; i<array.length; i++) {
				if(!beta.contains(i))					
					for(int x=0; x<rdata[i].length; x++)
						beta.add(rdata[i][x]);
				else
					for(int x=0; x<rdata[i].length; x++)
						remove.add(rdata[i][x]);
			}
			beta.removeAll(remove);
			System.out.print("beta is ");
			for(Integer i : beta)
				System.out.print(i + " ");
			System.out.println();
		}
			
		long plusresult = System.currentTimeMillis() - a;
		a = System.currentTimeMillis();		
		for(int j=0; j<MAXTRIAL; j++) {
			long beta = 0l;
			long remove = 0l;
			for(int i=0; i<array.length; i++) {
				if((beta & (1<<i)) == 0)					
					for(int x=0; x<rdata[i].length; x++)
						beta |= (1<<rdata[i][x]);
				else
					for(int x=0; x<rdata[i].length; x++)
						remove |= (1<<rdata[i][x]);
			}
			beta &= ~remove;
			System.out.print("beta is ");
			for(int i=0; i<31; i++) {
				if((beta & 1<<i) > 0)
					System.out.print(i + " ");
			}
			System.out.println();
		}
		long orresult = System.currentTimeMillis() - a;
		System.out.format("Plus:%d or:%d %d %d", plusresult, orresult, c, b);
	}

}
