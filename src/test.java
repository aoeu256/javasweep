
import java.util.*;

public class test {

	private static HashMap<Integer, Integer> matching;
	private static HashMap<Integer, Vector<Integer>> preds;
	private static Vector<Integer> unmatched;
	private static HashMap<Integer, Integer> unlayered;
	private static HashMap<Integer, Vector<Integer>> newlayer;
	private static HashMap<Integer, IntOrVector> pred;
	
	
	static class IntOrVector {
		public Integer num;
		public Vector<Integer> vec;
		
		public IntOrVector(Integer a) {
			num = a;
			vec = null;			
		}
		
		public IntOrVector(Vector<Integer> b) {
			vec = b;
			num = null;
		}
		
		public boolean equals(Object o) {
			if (num == null)
				return vec.equals(o);
			return num.equals(o);
		}
		
		public int hashCode() {
			if(num == null)
				return vec.hashCode();
			return num.hashCode();
		}
		
		public String toString() {
			if(num == null)
				return vec.toString();
			return num.toString();
		}		
	}
	
	public static void main(String[] args) {
		//graph = {0:[0,1], 1:[1,2], 2:[2,3], 3:[3]}
		int graphar[][] = {{0,  0,1}, {1,  1,2},  {2,  2,3}, {3,  0}};
		HashMap<Integer, Vector<Integer>> graph = new HashMap<Integer, Vector<Integer>>();
		
		
		// change the graphchar[][] structure to a real hashmap graph
		for(int[] key : graphar) {
			Vector<Integer> newvect = new Vector<Integer>();			
			for(int i=1; i<key.length; i++)
				newvect.add(key[i]);
			graph.put(key[0], newvect);
		}
		
		
		System.out.format("graph: %s\n", graph);
		System.out.format("hopcroft:%s\n", hopcroft(graph));
	}
	
	public static HashMap<Integer, Integer> hopcroft(HashMap<Integer, Vector<Integer>> graph) {	
//	'''Find maximum cardinality matching of a bipartite graph (U,V,E).
//	The input format is a dictionary mapping members of U to a list
//	of their neighbors in V.  The output is a triple (M,A,B) where M is a
//	dictionary mapping members of V to their matches in U, A is the part
//	of the maximum independent set in U, and B is the part of the MIS in V.
//	The same object may occur in both U and V, and is treated as two
//	distinct vertices if this happens.'''
	
//	# initialize greedy matching (redundant, but faster than full search)

		matching = new HashMap<Integer, Integer>();		
		
		for(Integer u : graph.keySet()) { 
			for (Integer v : graph.get(u)) {
				if(!matching.containsKey(v)) {
					matching.put(v, u);
					break;
				}
			}
		}
		
		while (true) {
//			# structure residual graph into layers
//			# pred[u] gives the neighbor in the previous layer for u in U
//			# preds[v] gives a list of neighbors in the previous layer for v in V
//			# unmatched gives a list of unmatched vertices in final layer of V,
//			# and is also used as a flag value for pred[u] when u is in the first layer
			//preds = {}
			preds = new HashMap<Integer, Vector<Integer>>();
			unmatched = new Vector<Integer>();
			
			pred = new HashMap<Integer, IntOrVector>();
			for(Integer u : graph.keySet())
				pred.put(u, new IntOrVector(unmatched));
			for(Integer v : matching.keySet())
				pred.remove(matching.get(v));
			Vector<Integer> predkeys = new Vector<Integer>();
			for(Integer i: pred.keySet()) predkeys.add(i);
			Vector<Integer> layer = predkeys;
			System.out.format("pred %s %s \n", pred, layer);
			// repeatedly extend layering structure by another pair of layers			
			while(layer.size() > 0 && unmatched.size() == 0) {
				newlayer = new HashMap<Integer, Vector<Integer>>();
				for(Integer u : layer) {
					for(Integer v : graph.get(u)) {
						if(!preds.containsKey(v)) {
							if(!newlayer.containsKey(v))
								newlayer.put(v, new Vector<Integer>());
							newlayer.get(v).add(u);
						}
					}
				}
				System.out.format("newlayer:%s\n", newlayer);
				layer = new Vector<Integer>();
				for(Integer v : newlayer.keySet()) {
					preds.put(v, newlayer.get(v));
					if(matching.containsKey(v)) {
						layer.add(matching.get(v));
						pred.put(matching.get(v), new IntOrVector(v));
						System.out.println("spec");
						System.out.format("spred %s\n", pred);
						System.out.format("matching %s\n", matching);
						System.out.format("matchv %s\n", matching.get(v));
						System.out.format("v %s\n", v);
						System.out.println("--");						
					}
					else
						unmatched.add(v);
				}
			}
//			# did we finish layering without finding any alternating paths?
			if(unmatched.size() == 0) {
				unlayered = new HashMap<Integer, Integer>();
				for(Integer u : graph.keySet()) {
					for(Integer v : graph.get(u)) {
						if (!preds.containsKey(v))
							unlayered.put(v, null);
					}
				}
				return matching;
			}
//			# recursively search backward through layers to find alternating paths
//			# recursion returns true if found path, false otherwise
			for(Integer v : unmatched) {
				recurse(v);
			}
		}
	}
		
	public static boolean recurse(Integer v){
		if (preds.containsKey(v)) {
			Vector<Integer> L = preds.get(v);
			preds.remove(v);
			for(Integer u : L) {
				if(pred.containsKey(u)) {
					IntOrVector pu = pred.get(u);
					pred.remove(u);
					if(pu.vec == unmatched || recurse(pu.num)) {
						matching.put(v, u);
						return true;
					}
				}
			}			
		}
		return false;
	}
}
