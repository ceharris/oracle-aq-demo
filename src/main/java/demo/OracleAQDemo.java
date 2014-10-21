package demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import oracle.jdbc.pool.OracleDataSource;
import oracle.jms.AQjmsDestination;
import oracle.jms.AQjmsFactory;
import oracle.jms.AQjmsSession;

public class OracleAQDemo {

  public static void main(String[] args) throws Exception {
    OracleDataSource dataSource = new OracleDataSource();
    dataSource.setURL("jdbc:oracle:thin:@leafminer.cns.vt.edu:1525:nemidev");
    dataSource.setUser("jmsuser");
    dataSource.setPassword("3^cbt13g.fSGW");
    ConnectionFactory connectionFactory = 
        AQjmsFactory.getConnectionFactory(dataSource);
    Connection connection = connectionFactory.createConnection();
    AQjmsSession session = (AQjmsSession) connection.createSession(false, 
        Session.AUTO_ACKNOWLEDGE);

    AQjmsDestination destination = (AQjmsDestination) session.getQueue("jmsuser", "jms_text_que");
//    MessageConsumer consumer = session.createConsumer(destination);
//    consumer.setMessageListener(new AQTextMessageListener());
//    connection.start();

    MessageProducer producer = session.createProducer(destination);
    
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    System.out.print("Message: ");
    String line = reader.readLine();
    while (line != null) {
      if (!line.trim().isEmpty()) {
        System.out.println("Sending: " + line);
        producer.send(session.createTextMessage(line));
      }
      System.out.print("Message: ");
      line = reader.readLine();
    }
    Thread.currentThread().join();
  }

  public static class AQTextMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
      try {
        System.out.println(((TextMessage) message).getText());
      }
      catch (JMSException ex) {
        throw new RuntimeException(ex);
      }
    }
  }
  
}
