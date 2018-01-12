package dev.cjo.collect.utils;

import org.junit.Test;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.Assert.assertTrue;

public class MethodPoller<T> {

    Duration pollDurationSec;
    int pollIntervalMillis;

    private Supplier<T> callMethod = null;
    private Predicate<T> pollResultPredicate = null;

    public MethodPoller() {
    }

    public MethodPoller poll(Duration pollDurationSec, int pollIntervalMillis) {
        this.pollDurationSec = pollDurationSec;
        this.pollIntervalMillis = pollIntervalMillis;
        return this;
    }


    public MethodPoller<T> method(Supplier<T> supplier) {
        callMethod = supplier;
        return this;
    }

    public MethodPoller<T> until(Predicate<T> predicate) {
        pollResultPredicate = predicate;
        return this;
    }

    public T execute()
    {
        // TODO: Validate that poll, method and until have been called.

        T result = null;
        boolean pollSucceeded = false;
        // TODO: Add check on poll duration
        // TODO: Use poll interval
        while (!pollSucceeded) {
            result = callMethod.get();
            System.out.println("Result:"+result);
            pollSucceeded = pollResultPredicate.test(result);
        }

        return result;
    }

    @Test
    public void test()
    {
        MethodPoller<String> poller = new MethodPoller<>();
        String uuidThatStartsWithOneTwoThree = poller.method(() -> UUID.randomUUID().toString())
                .until(input -> input.startsWith("123"))
                .execute();


        assertTrue(uuidThatStartsWithOneTwoThree.startsWith("123"));
        System.out.println(uuidThatStartsWithOneTwoThree);
    }
}

