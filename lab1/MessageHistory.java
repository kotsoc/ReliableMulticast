
import mcgui.*;
import java.util.*;

/**
 * History implementation for messages
 *
 */
public class MessageHistory {
    
    Vector<Integer> clock = new Vector<Integer>();
	int clockSum;    
	String message;
        
    public MessageHistory(Vector<Integer> clock, int clockSum, String message) {
        this.clock = clock;
		this.clockSum = clockSum;
		this.message = message;
    }
    
	// Getters
	public Vector<Integer> getClock() {
        return this.clock;
    }

	public int getclockSum() {
        return this.clockSum;
    }

	public String getMessage() {
        return this.message;
    }
	
	// Setters
    public void setClock(Vector<Integer> clock) {
        this.clock = clock;
    }

    public void setclockSum(int clockSum) {
        this.clockSum = clockSum;
    }

    public void setClock(String message) {
        this.message = message;
    }

}
