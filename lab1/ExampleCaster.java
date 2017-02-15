
import mcgui.*;
import java.util.*;

/**
 * Simple example of how to use the Multicaster interface.
 *
 * @author Andreas Larsson &lt;larandr@chalmers.se&gt;
 */
public class ExampleCaster extends Multicaster {
     Vector<Integer> clock = new Vector<Integer>();
     int sum = 0;
    /**
     * No initializations needed for this simple one
     */
    public void init() {
        mcui.debug("The network has "+hosts+" hosts!");
	
	for (int i=0;i<hosts;i++){
    		clock.add(0);	
	}
    }
        
    /**
     * The GUI calls this module to multicast a message
     */
    public void cast(String messagetext) {
		clock.set(id,clock.get(id)+1); 
        for(int i=0; i < hosts; i++) {
            /* Sends to everyone except itself */
            if(i != id) {
                bcom.basicsend(i,new ExampleMessage(id, messagetext, clock));
            }
        }
        mcui.debug("Sent out: \""+messagetext+"\"");
        mcui.deliver(id, messagetext, "from myself!");
		System.out.println("Clock" + clock);
    }
    
    /**
     * Receive a basic message
     * @param message  The message received
     */
    public void basicreceive(int peer,Message message) {
		int clock_sum = 0,index = -1 ;
		
		for (int i=0; i < hosts; i++){
			clock_sum += ((ExampleMessage)message).timestamp.get(i);
			if (((ExampleMessage)message).timestamp.get(i) != clock.get(i)){
				index = i;
			}
		}
		System.out.println("sum =" +clock_sum);
			System.out.println("Timestamp"+((ExampleMessage)message).timestamp);
		if (clock_sum <=sum +1 ){
			sum =clock_sum;
			if (index != -1){
				clock.set(index,clock.get(index)+1);
			}
		}
		///////////////////////////////////////////////////
		String time_s =((ExampleMessage)message).timestamp.toString();
        mcui.deliver(peer, ((ExampleMessage)message).text,time_s);
		System.out.println("Clock" + clock);
		System.out.println();
    }

    /**
     * Signals that a peer is down and has been down for a while to
     * allow for messages taking different paths from this peer to
     * arrive.
     * @param peer	The dead peer
     */
    public void basicpeerdown(int peer) {
        mcui.debug("Peer "+peer+" has been dead for a while now!");
    }
}
