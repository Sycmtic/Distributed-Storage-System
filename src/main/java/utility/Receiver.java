package utility;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.integration.transports.netty.NettyConnectorFactory;
import org.hornetq.integration.transports.netty.TransportConstants;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

public class Receiver implements Runnable {
    String queueName;

    public Receiver(String queueName) {
        this.queueName = queueName;
    }

    @Override
    public void run() {
        try {
            Map<String, Object> connectionParams = new HashMap<String, Object>();
            connectionParams.put(TransportConstants.PORT_PROP_NAME, 5445);

            TransportConfiguration transportConfiguration = new TransportConfiguration(
                    NettyConnectorFactory.class.getName(), connectionParams);

            //Create and start connection
            QueueConnectionFactory queueFactory = (QueueConnectionFactory) HornetQJMSClient.createConnectionFactory(transportConfiguration);
            QueueConnection queueConn = queueFactory.createQueueConnection();

            //create Queue session
            QueueSession queueSess = queueConn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            //Get the Queue object
            Queue queue = HornetQJMSClient.createQueue(queueName);
            //Create QueueReceiver
            QueueReceiver receiver = queueSess.createReceiver(queue);

            queueConn.start();

            //Register the listener object with receiver
            Listener listener = new Listener();
            receiver.setMessageListener(listener);
            Logger.infoLog("Listening to " + queueName);
            while(true) {
                Thread.sleep(30 * 1000);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
