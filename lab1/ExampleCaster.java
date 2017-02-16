
import mcgui.*;
import java.util.*;

/**
 * Simple example of how to use the Multicaster interface.
 *
 * @author Andreas Larsson &lt;larandr@chalmers.se&gt;
 */
public class ExampleCaster extends Multicaster {
     Vector<Integer> clock = new Vector<Integer>();
	 LinkedList<int[]> oura = new LinkedList<int[]>();
	 Dictionary<String, String> msgHistory = new Hashtable<String, String>();
     int myClock = 0;
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
	myClock++;
        for(int i=0; i < hosts; i++) {
            /* Sends to everyone except itself */
            if(i != id) {
                bcom.basicsend(i,new ExampleMessage(id, messagetext,myClock));
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

		int msgClock = ((ExampleMessage)message).timestamp;
		int sender = message.getSender();
		int tupple[] = {sender,msgClock};
		
		String key = Integer.toString(sender)+Integer.toString(msgClock);
		System.out.println("Message Clock: " + msgClock);
		msgHistory.put(key,((ExampleMessage)message).text);
		if ( msgClock <= clock.get(sender)+1 ){
			String time_s =Integer.toString(((ExampleMessage)message).timestamp);
			mcui.deliver(peer, ((ExampleMessage)message).text,time_s);
			clock.set(sender,msgClock);
			oura.addFirst(tupple);
		}
		else{
			System.out.println("STIN OURA");
			// STIN OURAAA
		}
		System.out.println("Clock: " + clock);
		System.out.println(oura);
    }


	public void deliver(int id,String messagetext,int clock_sum){
		//if(queue[1] == queue[2] 
		//mcui.deliver(id, messagetext, "from myself!");
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
