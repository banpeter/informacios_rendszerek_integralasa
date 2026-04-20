package org.example;


import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Context;



@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination",     propertyValue = "java:/queue/colorQueue"),
        @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "color = 'RED'")
})
public class RedMDB implements MessageListener {

    private int count = 0;

    @Override
    public void onMessage(Message message) {
        try {
            System.out.println("RedMDB onMessage called");
            System.out.println("color property: " + message.getStringProperty("color"));
            if (message instanceof MapMessage) {
                count++;
                System.out.println("RED received, count: " + count);
                if (count % 10 == 0) {
                    sendStatistics("java:/queue/colorStatistics", "RED");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendStatistics(String queueName, String color) {
        try {
            InitialContext ctx = new InitialContext();
            QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("java:jboss/DefaultJMSConnectionFactory");
            QueueConnection connection = factory.createQueueConnection();
            QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = (Queue) ctx.lookup(queueName);
            QueueSender sender = session.createSender(queue);
            connection.start();

            MapMessage msg = session.createMapMessage();
            msg.setString("color", color);
            msg.setInt("count", 10);
            sender.send(msg);

            sender.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}