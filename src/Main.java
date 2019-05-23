import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.function.Predicate;

import static java.util.Collections.synchronizedList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.iterate;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        List<Integer> listOfInt = synchronizedList(createListOfInt(10000));
        CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(new RemoveNumbersThread(listOfInt, (i) -> i < 5000, countDownLatch)).start();
        new Thread(new RemoveNumbersThread(listOfInt, (i) -> i > 500, countDownLatch)).start();

        countDownLatch.await();  //main thread is waiting on CountDownLatch to finish
        System.out.println("List is empty now");
    }

    private static List<Integer> createListOfInt(int max) {
        return iterate(1, n -> n + 1)
                .limit(max)
                .collect(toList());
    }
}

class RemoveNumbersThread implements Runnable{

    List<Integer> listOfInt;
    private final Predicate predicate;
    private final CountDownLatch countDownLatch;


    public RemoveNumbersThread(List<Integer> listOfInt, Predicate<Integer> predicate, CountDownLatch countDownLatch) {
        this.listOfInt = listOfInt;
        this.predicate = predicate;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        listOfInt.removeIf(predicate);
        countDownLatch.countDown();
    }
}
