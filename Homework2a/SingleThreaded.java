import java.util.ArrayList;
import java.util.Vector;

public class SingleThreaded {
  public static void main(String[] args) {
    int elementCount = 100000000;

    ArrayList<Integer> arrayList = new ArrayList<>();
    long listStartTime = System.currentTimeMillis();

    for (int i = 0; i < elementCount; i++) {
      arrayList.add(i);
    }

    long listEndTime = System.currentTimeMillis();
    long listDuration = listEndTime - listStartTime;
    System.out.println("ArrayList duration: " + listDuration);

    Vector<Integer> vector = new Vector<>();
    long vectorStartTime = System.currentTimeMillis();

    for (int i = 0; i < elementCount; i++) {
      vector.add(i);
    }

    long vectorEndTime = System.currentTimeMillis();
    long vectorDuration = vectorEndTime - vectorStartTime;
    System.out.println("Vector duration: " + vectorDuration);
  }
}