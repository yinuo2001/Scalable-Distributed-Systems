package org.yinuo.rabbitmq101;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Worker {
  private final static String QUEUE_NAME = "hello";

  private static void doWork(String task) throws InterruptedException {
    for (char ch: task.toCharArray()) {
      if (ch == '.') Thread.sleep(1000);
    }
  }

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    boolean durable = true;
    channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    channel.basicQos(1);

    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), "UTF-8");

      System.out.println(" [x] Received '" + message + "'");
      try {
        doWork(message);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } finally {
        System.out.println(" [x] Done");
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
      }
    };
    boolean autoAck = false;
    channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });
  }
}
