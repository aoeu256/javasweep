import java.util.*;

public class UpdateThread extends TimerTask {
	Vector<Area> areas;
	
	public UpdateThread(Vector<Area> areas2) {
		this.areas = areas2;
	}
	
	public void run() {
		for(Area area : areas)
			area.gameupdate();		
	}
}
