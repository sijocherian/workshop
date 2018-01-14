package dev.cjo.utils;


import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Callable;


public class MethodRetry<T> {

    //Duration pollDurationSec;
    //int pollIntervalMillis;
    Checker checker = new Checker();
    private Callable<T> callMethod = null;

    public MethodRetry() {
    }



    public MethodRetry<T> method(Callable<T> c) {
        callMethod = c;
        return this;
    }


    /**
     * set wait between retries, exponentially backing off to maxwaitMillis and multiplying
     * successive delays by a factor of 2.
     * @param waitMillis
     * @param maxwaitMillis
     * @return
     */
    public MethodRetry withBackoff(long waitMillis, long maxwaitMillis) {
        assertTrue(waitMillis>0, "waitMillis  must be greater than 0");
        assertTrue(maxwaitMillis >= waitMillis, "maxwaitMillis must be greater than waitMillis");
        checker.setMaxWaitMillis(maxwaitMillis);
        checker.setWaitMillis(waitMillis);
        return this;
    }

    public MethodRetry withMaxRetries(int maxRetries) {
        assertTrue(maxRetries >= -1, "maxRetries must be greater than or equal to -1");
        checker.setMaxRetries(maxRetries);
        return this;
    }
    public T execute()
    {

        T result = null;
        Throwable failure=null;
        if(callMethod==null) return result;


        while (true) {
            try {
                failure=null;
                result = callMethod.call();

            } catch (Throwable t) {
                if (InterruptedException.class.isInstance(t.getCause()))
                    throw new ExecuteException(t.getMessage(), t);
                failure = t;
            }

            if(checker.check(failure))     {
                return result;
            }
            else {
                try {
                    Thread.sleep(checker.getWaitMillis());
                    //
                } catch (InterruptedException e) {
                    throw new ExecuteException("Waiting to retry an operation is interrupted");
                }
            }

        }


    }

    public static void assertTrue(boolean expression, String errorMessageFormat, Object... args) {
        if (!expression)
            throw new IllegalArgumentException(String.format(errorMessageFormat, args));
    }

    public static class ExecuteException extends RuntimeException {
        private static final String errorName = "EXECUTION_ERROR";
        public ExecuteException(String msg, Throwable e) {
            super(msg, e);
        }

        public ExecuteException(String msg) {
            super(msg);
        }


    }


    class Checker {
        volatile boolean completed;
        volatile boolean retriesExceeded;
        //volatile boolean success;
        volatile int executions=0;
        volatile long waitMillis;
        long MaxWaitMillis;
        int MaxRetries=-1;

        /**
         *
         * @param failure
         * @return true if method call success, throws RuntimeException if retries exceeded
         */
        public boolean check(Throwable failure) {
            executions++;

            retriesExceeded = MaxRetries> -1 && executions> MaxRetries;
            waitMillis = Math.min(MaxWaitMillis, waitMillis * 2);

            if(retriesExceeded && failure != null)
                throw new ExecuteException("Retries exceeded for the execution. Aborting.");

            completed = (failure == null)?
                        true : false;
            return completed;
        }

        public long getWaitMillis() {
            return waitMillis;
        }

        public void setWaitMillis(long waitNanos) {
            this.waitMillis = waitNanos;
        }

        public long getMaxWaitMillis() {
            return MaxWaitMillis;
        }

        public void setMaxWaitMillis(long maxWaitMillis) {
            MaxWaitMillis = maxWaitMillis;
        }

        public int getMaxRetries() {
            return MaxRetries;
        }

        public void setMaxRetries(int maxRetries) {
            MaxRetries = maxRetries;
        }
    }


}

