import java.util.concurrent.CountDownLatch;

public class Multithreaded {
  private final static int N = 1000000;
  private final static int INCREASE = 10;
  private static int counter = 0;

  private static class WorkerThread extends Thread {
    private final CountDownLatch latch;

    public WorkerThread(CountDownLatch latch) {
      this.latch = latch;
    }

    private static synchronized void increaseCounter() {
      counter++;
    }

    @Override
    public void run() {
      for (int i = 0; i < INCREASE; i++) {
        increaseCounter();
      }
      latch.countDown();
    }
  }

  public static void main (String[] args) {
    long startTime = System.currentTimeMillis();
    Thread threads[] = new Thread[N];
    CountDownLatch latch = new CountDownLatch(N);

    for (int i = 0; i < N; i++) {
      threads[i] = new WorkerThread(latch);
      threads[i].start();
    }

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    System.out.println("Final counter value: " + counter);
    System.out.println("Duration: " + duration);
  }
}
