import java.util.*;

public class Combo
{
    int combot;
    HashSet<Piece> combo;
    
    public Combo(Area area) {
    	combo = new HashSet<Piece>();
    	combot = area.chaint;
    }
    
    void gameupdate(Area area)
    {
    	/*
		combot--;
        if(combot == 0) {				
            for(Piece i: combo)
                for(Piece p: i.fallpieces(area)) {
                	if(!p.canchain) {
                		area.nchainblocks++;
                		p.canchain = true;
                	}
                }
        }*/
    }
}