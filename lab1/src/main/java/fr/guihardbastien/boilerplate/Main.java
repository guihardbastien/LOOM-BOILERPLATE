package fr.guihardbastien.boilerplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private final ReentrantLock lock = new ReentrantLock();
    public int counter = 0;

    public void syncAdd() {
        lock.lock();
        try {
            counter++;
        } finally {
            lock.unlock();
        }
    }

    public void fibersHelloWorld() throws InterruptedException {
        var a = new ArrayList<Thread>();
        for (int i = 0; i < 10_000; i++) {
            var t = Thread.ofVirtual().start(() -> {
                try {
                    Thread.sleep(Duration.ofSeconds(1));
                    this.syncAdd();
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + "-- Interrupted");
                }
            });
            a.add(t);
        }

        for (var t : a) {
            t.join();
        }

        System.out.println(this.counter);
    }


    public static void continuationHelloWorld() {
        var scope = new ContinuationScope("scope");
        var continuation1 = new Continuation(scope, () -> {
            System.out.println("start 1");
            Continuation.yield(scope);
            System.out.println("middle 1");
            Continuation.yield(scope);
            System.out.println("end 1");
        });
        var continuation2 = new Continuation(scope, () -> {
            System.out.println("start 2");
            Continuation.yield(scope);
            System.out.println("middle 2");
            Continuation.yield(scope);
            System.out.println("end 2");
        });
        var list = List.of(continuation1, continuation2);

        while (!continuation1.isDone()) {
            list.forEach(Continuation::run);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        //continuationHelloWorld();

        var m = new Main();
        m.fibersHelloWorld();
    }
}
