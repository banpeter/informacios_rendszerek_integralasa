package org.example;

import java.util.Hashtable;
import jakarta.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class StatisticsClient {
    public static void main(String[] args) throws Exception {
        InitialContext ctx = getInitialContext();
        QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");
        QueueConnection connection = factory.createQueueConnection("quser", "Password_1");
        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = (Queue) ctx.lookup("jms/queue/colorStatistics");
        QueueReceiver receiver = session.createReceiver(queue);
        connection.start();

        System.out.println("Statisztika figyelése elkezdődött...");
        while (true) {
            Message message = receiver.receive();
            if (message instanceof MapMessage) {
                MapMessage msg = (MapMessage) message;
                String color = msg.getString("color");
                int count = msg.getInt("count");
                System.out.println(count + " '" + color + "' messages has been processed");
            }
        }
    }

    private static InitialContext getInitialContext() throws NamingException {
        Hashtable<String, Object> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        env.put(Context.PROVIDER_URL, "http-remoting://127.0.0.1:8080");
        env.put("jboss.naming.client.ejb.context", false);
        env.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        return new InitialContext(env);
    }
}