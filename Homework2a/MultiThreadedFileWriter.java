import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MultiThreadedFileWriter {
  private final static int numThreads = 500;
  private final static int numStrings = 5000;
  private final static String fileName = "output.txt";

  private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();


  // Thread generates strings.
  private static class WorkerThread extends Thread {
    private final int threadId;

    public WorkerThread(int threadId) {
      this.threadId = threadId;
    }


    // write all the strings from one thread after they are generated
    public void generateAllThenWrite() {
      List<String> buffer = new ArrayList<>();
      for (int i = 0; i < numStrings; i++) {
        String str = System.currentTimeMillis() + ", " + threadId + ", " + i + "\n";
        buffer.add(str);
      }
      // Write all collected strings to the file at once
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
        for (String line : buffer) {
          writer.write(line);
        }
        writer.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // write every string to the file immediately after it is generated
    public void writeImmediately() {
      for (int i = 0; i < numStrings; i++) {
        String str = System.currentTimeMillis() + ", " + threadId + ", " + i + "\n";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
          writer.write(str);
          writer.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static void main(String[] args) {

    // Time of write every string to the file immediately
    // after it is generated in the loop in each thread
    long startTime = System.currentTimeMillis();
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    for (int i = 0; i < numThreads; i++) {
      executor.execute(new WorkerThread(i)::writeImmediately);
    }
    executor.shutdown();
    try {
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    System.out.println("Duration for write immediately after generation: " + duration);

    // Time of write all the strings from one thread
    // after they are generated and just before a thread terminates
    startTime = System.currentTimeMillis();
    executor = Executors.newFixedThreadPool(numThreads);
    for (int i = 0; i < numThreads; i++) {
      executor.execute(new WorkerThread(i)::generateAllThenWrite);
    }
    executor.shutdown();
    try {
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    endTime = System.currentTimeMillis();
    duration = endTime - startTime;
    System.out.println("Duration for write all from one thread at once: " + duration);

    // One thread writes all the strings, the rest generate them
    startTime = System.currentTimeMillis();
    Thread writerThread = new Thread(MultiThreadedFileWriter::writeToFile);
    writerThread.start();
    executor = Executors.newFixedThreadPool(numThreads - 1);
    for (int i = 0; i < numThreads - 1; i++) {
      int generatorId = i;
      executor.execute(() -> generateStrings(generatorId));
    }
    executor.shutdown();
    try {
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    try {
      writerThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    endTime = System.currentTimeMillis();
    duration = endTime - startTime;
    System.out.println("Duration for one thread writes all: " + duration);
  }

  private static void generateStrings(int generatorId) {
    for (int i = 0; i < numStrings; i++) {
      String str = System.currentTimeMillis() + ", " + generatorId + ", " + i + "\n";
      try {
        queue.put(str); // Blocks if queue is full
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  private static void writeToFile() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
      while (!queue.isEmpty()) {
        String line = queue.poll(100, TimeUnit.MILLISECONDS); // Waits if empty
        if (line != null) {
          writer.write(line);
        }
      }
      writer.flush();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
