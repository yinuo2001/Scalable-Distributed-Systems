package org.yinuo.rabbitmq101.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLogTopic {

  private static final String EXCHANGE_NAME = "topic_logs";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {

      channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

      String routingKey = getRouting(argv);
      String message = getMessage(argv);

      channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
      System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");
    }
  }

  private static String getRouting(String[] strings) {
    if (strings.length < 1)
      return "anonymous.info";
    return strings[0];
  }

  private static String getMessage(String[] strings) {
    if (strings.length < 2)
      return "Hello World!";
    return joinStrings(strings, " ", 1);
  }

  private static String joinStrings(String[] strings, String delimiter, int startIndex) {
    int length = strings.length;
    if (length == 0) return "";
    if (length < startIndex) return "";
    StringBuilder words = new StringBuilder(strings[startIndex]);
    for (int i = startIndex + 1; i < length; i++) {
      words.append(delimiter).append(strings[i]);
    }
    return words.toString();
  }
}
