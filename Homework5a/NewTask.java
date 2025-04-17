package org.yinuo.rabbitmq101;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewTask {
  private final static String QUEUE_NAME = "hello";
  private final static int NUM_MESSAGES = 100;

  public static void main(String[] args) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");

    ExecutorService executor = Executors.newFixedThreadPool(10);

    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
      boolean durable = true;
      channel.queueDeclare(QUEUE_NAME, durable, false, false, null);

      for (int i = 0; i < NUM_MESSAGES; i++) {
        final int messageId = i;
        executor.submit(() -> {
          synchronized (channel) {
            try {
              String message = "Task #" + messageId;
              channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
              System.out.println(" [x] Sent '" + message + "'");
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }

        });
      }
      executor.shutdown();
      while (!executor.isTerminated()) {
        Thread.sleep(100);
      }
//      String message = String.join(" ", args);
//
//      channel.basicPublish("", "hello", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
//      System.out.println(" [x] Sent '" + message + "'");
    }
  }
}
