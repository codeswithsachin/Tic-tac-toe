class Counter {
    int value = 0;

    synchronized void increment() {
        value++;
    }
}

public class SharedCounter {
    public static void main(String[] args) throws Exception {
        Counter c = new Counter();

        Runnable job = () -> {
            for (int i = 0; i < 1000; i++)
                c.increment();
        };

        Thread t1 = new Thread(job);
        Thread t2 = new Thread(job);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Final counter = " + c.value);
    }
}