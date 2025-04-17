package org.yinuo.rabbitmq101.fanout;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class EmitLog {

  private final static String EXCHANGE_NAME = "logs";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
      channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

      String message = argv.length < 1 ? "info: Hello World!" :
                      String.join(" ", argv);

      channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
      System.out.println(" [x] Sent '" + message + "'");
    }
  }
}
