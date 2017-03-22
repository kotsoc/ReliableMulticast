
import mcgui.*;
import java.util.*;

/**
 * Simple example of how to use the Multicaster interface.
 *
 * @author Andreas Larsson &lt;larandr@chalmers.se&gt;
 */
public class ExampleCaster extends Multicaster {
     ArrayList<Integer> clock = new ArrayList<Integer>();
     ArrayList<Integer> propCount = new ArrayList<Integer>();
     ArrayList<Integer> agrSeq = new ArrayList<Integer>();
     TreeMap<Integer, String> msgHistory = new TreeMap<Integer, String>();
     int delivered  = 0;
     int myClock = 0;
     int cHosts=3;
    /**
     * No initializations needed for this simple one
     */
    public void init() {
        mcui.debug("The network has "+hosts+" hosts!");
		cHosts=hosts;
		for (int i=0;i<hosts;i++){
    		clock.add(0);
		}
    }

    /**
     * The GUI calls this module to multicast a message
     */
    public void cast(String messagetext) {
	int propSeq = 0;
	myClock++;
	clock.set(id,myClock);
        propCount.add(0);
	agrSeq.add(0);
        for(int i=0; i < hosts; i++) {
            /* Sends to everyone except itself */
            if(i != id) {
                bcom.basicsend(i,new ExampleMessage(id, messagetext,myClock,propSeq));
            }
        }
        mcui.debug("Sent out: \""+messagetext+"\"");
    }

    /**
     * Receive a basic message
     * @param message  The message received
     */

    public void basicreceive(int peer,Message message) {
		int msgClock = ((ExampleMessage)message).timestamp;
		int sender = message.getSender();
		// We propose a sequence number for the message		
		if (((ExampleMessage)message).proposed == 0 ){
			int max = 0;
			for (int i : clock){
				if (i >= max){// Calculating proposal
					max=i;				
				}
			}
			if (max<delivered){
				max=delivered;
			}
			bcom.basicsend(sender,new ExampleMessage(sender, ((ExampleMessage)message).text,((ExampleMessage)message).timestamp,max+1));
			clock.set(sender,msgClock);
		}
		else{ 
			System.out.println(sender);
			if (sender == id ){ // Receiving a proposal, Calculating and  Sending Agreeed sequence
				if (propCount.get(msgClock-1) <= hosts-1 && ((ExampleMessage)message).proposed >= agrSeq.get(msgClock-1)){
					agrSeq.set(msgClock-1,((ExampleMessage)message).proposed);
					System.out.println("agrSeq: " + agrSeq);
					propCount.set(msgClock-1,(propCount.get(msgClock-1))+1);
					System.out.println("PropCount: "+ propCount);
					if (propCount.get(msgClock-1) == cHosts -1){
						for(int i=0; i < hosts; i++) {
            					/* Sends to everyone except itself */
		        			if(i != id) {
		           				bcom.basicsend(i,new ExampleMessage(id, ((ExampleMessage)message).text,((ExampleMessage)message).timestamp,agrSeq.get(msgClock-1)));
		        			}
						}
						msgHistory.put(((ExampleMessage)message).proposed,((ExampleMessage)message).text);
						deliver(agrSeq.get(msgClock-1),sender);
					}					
				}
				else{
					System.out.println("Too many Proposals"+ propCount);
				}
			}
			else { 	// Receiving  the agreed Sequence
				msgHistory.put(((ExampleMessage)message).proposed,((ExampleMessage)message).text);
				deliver(((ExampleMessage)message).proposed,sender);	
			}
		}
		System.out.println(clock);
    }


	public void deliver(int seq,int ident){
		if (delivered == seq-1 && ident == id){
			mcui.deliver(id, msgHistory.remove(seq), "from myself!");
			delivered++;	
		}
		else if(delivered == seq-1 && ident != id){
			mcui.deliver(ident, msgHistory.remove(seq), "from"+ Integer.toString(ident));
			delivered++;
		}
		else{
			System.out.println("message out of sequence");
			for(int i=0;i<=seq+1;i++){
				if (msgHistory.containsValue(i)){
				mcui.deliver(id, msgHistory.remove(seq), "from"+ Integer.toString(ident));						
				}
			}
		}	
		
	}
    /**
     * Signals that a peer is down and has been down for a while to
     * allow for messages taking different paths from this peer to
     * arrive.
     * @param peer	The dead peer
     */
    public void basicpeerdown(int peer) {
        mcui.debug("Peer "+peer+" has been dead for a while now!");
		cHosts = cHosts -1;
    }
}
