package utility;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class Listener implements MessageListener {
    public void onMessage (Message m) {
        try {
            TextMessage msg = (TextMessage)m;
            String content = msg.getText();
            System.out.println("Message is arrived: " + content);
        }catch(JMSException e) {
            System.out.println(e.getMessage());
        }
    }
}
