
import mcgui.*;
import java.util.*;

/**
 * Message implementation for ExampleCaster.
 *
 * @author Andreas Larsson &lt;larandr@chalmers.se&gt;
 */
public class ExampleMessage extends Message {

    String text;
    String timestamp;

    public ExampleMessage(int sender,String text,String timestamp) {
        super(sender);
        this.text = text;
	this.timestamp = timestamp;
    }

    /**
     * Returns the text of the message only. The toString method can
     * be implemented to show additional things useful for debugging
     * purposes.
     */
    public String getText() {
        return text;
    }

    public static final long serialVersionUID = 0;
}
