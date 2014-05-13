
import java.util.*;
import java.awt.*;

public class Task {	
	int maxt;
	int t;
	public Task(int timer) {
		t = timer;
	}
	
	void paint(Area area, Graphics g) {
	
	}
	
	void gameupdate() {
		
	}
	
	void update() {
		if(t > 0)
			t--;
		gameupdate();
	}
	
}
