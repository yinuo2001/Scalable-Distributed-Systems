import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.*;

public class MultiThreadedClient {

  private static final String URL = "http://localhost:8080/homework3b_war_exploded/hello";
  private static final int NUM_THREADS = 1001;

  public static void main(String[] args) throws InterruptedException {
    long startTime = System.currentTimeMillis();
    ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    CountDownLatch latch = new CountDownLatch(NUM_THREADS);

    for (int i = 0; i < NUM_THREADS; i++) {
      executor.submit(() -> {
        try {
          sendRequest();
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();
    executor.shutdown();
    long endTime = System.currentTimeMillis();
    System.out.println("Total execution time: " + (endTime - startTime) + " milliseconds");
  }

  private static void sendRequest() {
    HttpClient client = new HttpClient();
    GetMethod method = new GetMethod(URL);
    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

    try {
      int statusCode = client.executeMethod(method);
      if (statusCode != HttpStatus.SC_OK) {
        System.err.println("Request failed: " + method.getStatusLine());
      } else {
        // byte[] responseBody = method.getResponseBody();
        // System.out.println("Response: " + new String(responseBody));
      }
    } catch (HttpException e) {
      System.err.println("Protocol error: " + e.getMessage());
    } catch (IOException e) {
      System.err.println("Transport error: " + e.getMessage());
    } finally {
      method.releaseConnection(); // Release resources
    }
  }
}