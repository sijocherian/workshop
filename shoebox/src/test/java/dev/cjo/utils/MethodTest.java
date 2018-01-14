package dev.cjo.utils;

import org.junit.Test;

import java.time.Duration;
import java.util.UUID;
import static org.junit.Assert.assertTrue;

public class MethodTest {



    @Test
    public void testMethodPoller()
    {
        MethodPoller<String> poller = new MethodPoller<>();
        String uuidThatStartsWithOneTwoThree = poller.method(() -> UUID.randomUUID().toString())
                .until(input -> input.startsWith("123"))
                .execute();


        assertTrue(uuidThatStartsWithOneTwoThree.startsWith("123"));
        System.out.println(uuidThatStartsWithOneTwoThree);
    }

    @Test
    public void testMethodRetry()
    {
        MethodRetry<String> exec = new MethodRetry<>();
        Object uuidStr = exec.method(() -> UUID.randomUUID().toString())
                .withMaxRetries(3).withBackoff(3000, 60000)
                .execute();


        //assertTrue(uuidThatStartsWithOneTwoThree.startsWith("123"));
        System.out.println("Positive test: "+ uuidStr);

        uuidStr = exec.method(() ->
        {
            if(System.currentTimeMillis()%3 == 0)
                throw new RuntimeException("failed execution");
            else
                return UUID.randomUUID().toString();
        })
                .withMaxRetries(3).withBackoff(3000, 60000)
                .execute();


        //assertTrue(uuidThatStartsWithOneTwoThree.startsWith("123"));
        System.out.println("Alternate failure test: "+ uuidStr);
    }
}

