import java.util.*;
import java.util.concurrent.*;

public class SingleMap {
  private static final int ELEMENT_COUNT = 100000;
  private static final int THREAD_COUNT = 1000;

  public static void main(String[] args) {
    System.out.println("Single-threaded performance:");
    testSingleThread(new Hashtable<>(), "Hashtable");
    testSingleThread(new HashMap<>(), "HashMap");
    testSingleThread(new ConcurrentHashMap<>(), "ConcurrentHashMap");

    System.out.println("\nMulti-threaded performance:");
    testMultiThread(new Hashtable<>(), "Hashtable");
    testMultiThread(Collections.synchronizedMap(new HashMap<>()), "Synchronized HashMap");
    testMultiThread(new ConcurrentHashMap<>(), "ConcurrentHashMap");
  }

  private static void testSingleThread(Map<Integer, Integer> map, String mapType) {
    long startTime = System.nanoTime();
    for (int i = 0; i < ELEMENT_COUNT; i++) {
      map.put(i, i);
    }
    long endTime = System.nanoTime();

    long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
    System.out.println(mapType + " - Time taken: " + duration + " ms");
  }

  private static void testMultiThread(Map<Integer, Integer> map, String mapType) {
    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    long startTime = System.nanoTime();

    for (int i = 0; i < THREAD_COUNT; i++) {
      final int threadId = i;
      executor.execute(() -> {
        int start = threadId * (ELEMENT_COUNT / THREAD_COUNT);
        int end = start + (ELEMENT_COUNT / THREAD_COUNT);
        for (int j = start; j < end; j++) {
          map.put(j, j);
        }
      });
    }

    executor.shutdown();
    try {
      executor.awaitTermination(1, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    long endTime = System.nanoTime();
    long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
    System.out.println(mapType + " - Time taken: " + duration + " ms");
  }
}
