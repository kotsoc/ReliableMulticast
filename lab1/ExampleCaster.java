
import mcgui.*;
import java.util.*;

/**
 * Simple example of how to use the Multicaster interface.
 *
 * @author Andreas Larsson &lt;larandr@chalmers.se&gt;
 */
public class ExampleCaster extends Multicaster {
     Vector<Integer> clock = new Vector<Integer>();
     //LinkedList<int[]> queue = new LinkedList<int[]>();
     TreeMap<Integer, String> msgHistory = new TreeMap<Integer, String>();
     int myClock,delivered,propCount  = 0;
	 int agrSeq = 0;
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
	int propSeq = 0;
	myClock++;
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
		int clockSum = 0;
		// We propose a sequence number for the message		
		if (((ExampleMessage)message).proposed == 0 ){
			for (int i : clock){
				clockSum += i; // Calculating proposal
			}
			clock.set(sender,msgClock);
			bcom.basicsend(sender,new ExampleMessage(sender, ((ExampleMessage)message).text,((ExampleMessage)message).timestamp,clockSum+1));
		}
		else{ 
			System.out.println(sender);
			if (sender == id ){ // Receiving a proposal, Calculating and  Sending Agreeed sequence
				if (propCount <= hosts-1 && ((ExampleMessage)message).proposed >= agrSeq){
					agrSeq = ((ExampleMessage)message).proposed;
					System.out.println("agrSeq: " + agrSeq);
					propCount++;
					System.out.println("Proposal too low"+ propCount);
					if (propCount == hosts -1){
						for(int i=0; i < hosts; i++) {
            			/* Sends to everyone except itself */
		        			if(i != id) {
		           				bcom.basicsend(i,new ExampleMessage(id, ((ExampleMessage)message).text,((ExampleMessage)message).timestamp,agrSeq));
		        			}
						}
						msgHistory.put(((ExampleMessage)message).proposed,((ExampleMessage)message).text);
						clock.set(id,clock.get(id)+1);
						deliver(agrSeq,sender);
						propCount = 0;
					}					
				}
				else{
					System.out.println("Proposal too low"+ agrSeq);
				}
			}
			else { 										// Receiving  the agreed Sequence
				msgHistory.put(((ExampleMessage)message).proposed,((ExampleMessage)message).text);
				deliver(((ExampleMessage)message).proposed,sender);	
			}
		}
		System.out.println(clock);
    }


	public void deliver(int seq,int ident){
		delivered++;
		if (delivered == seq && ident == id){
			mcui.deliver(id, msgHistory.remove(seq), "from myself!");	
		}
		else if(delivered == seq && ident != id){
			mcui.deliver(id, msgHistory.remove(seq), "from"+ Integer.toString(ident));
		}
		else{
			System.out.println("message out of sequence");
			for(int i=0;i<msgHistory.size();i++){
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
    }
}
