package org.example;

import java.util.Hashtable;
import java.util.Random;


import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Context;

import javax.naming.Context;
import javax.naming.NamingException;

public class ColorSender {
    public static void main(String[] args) throws Exception {
        InitialContext ctx = getInitialContext();
        QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");

        QueueConnection connection = factory.createQueueConnection("quser", "Password_1");
        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = (Queue) ctx.lookup("jms/queue/colorQueue");
        QueueSender sender = session.createSender(queue);
        connection.start();

        String[] colors = {"RED", "GREEN", "BLUE"};
        Random random = new Random();



        while (true) {
            String color = colors[random.nextInt(3)];
            MapMessage msg = session.createMapMessage();
            msg.setStringProperty("color", color);
            msg.setString("color", color);
            sender.send(msg);
            System.out.println("Sent: " + color);
            Thread.sleep(1000);
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