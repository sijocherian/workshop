package cspark.stest;

import spark.api.java.JavaRDD;
import spark.api.java.JavaSparkContext;
import spark.api.java.function.Function;
import spark.storage.StorageLevel;

import java.io.Serializable;
import java.util.*;

public class DemoStringMapDist {

    private static final String CODE_JAR = "target/project.ioexp.jar";
    static String YOUR_SPARK_HOME = "/spark/spark-0.7.0";
    private static int slices = 8, dataStringSize = 4000, dataCount = 130000;

    public static void main(String[] args) {
        System.out.println("Args: DemoStringMapDist <master> <file> <Iterations> <optionInt> <dataStringSize> <dataCount>");
        JavaSparkContext sc = new JavaSparkContext(args[0], "DemoStringMapDist", YOUR_SPARK_HOME, CODE_JAR);

        int matchItr = 10;
        Map<Integer, Long> execTimeMap = new HashMap<Integer, Long>();
        JavaRDD<String> simpleRDD = createSimpleRDD(sc);
        runWarmupProbe(simpleRDD);

        int ic = 1;
        for (; ic <= matchItr; ic++) {

            long submitTime = System.currentTimeMillis();
            JavaRDD<String> mapResults = simpleRDD.map(new FnStrPointMap());
            List<String> resultOutput = mapResults.collect();     //creates local collection , need heap to hold all

            long jobDuration = (System.currentTimeMillis() - submitTime);
            execTimeMap.put(ic, jobDuration);
            System.out.println(ic + " job time " + jobDuration + " msec; ");
        }

        double totT = 0;
        for (double t : execTimeMap.values()) {
            totT += t;
        }
        System.out.println(matchItr + " itr Avgtime " + (int) (totT / matchItr) + " ms");

    }

    public static void runWarmupProbe(JavaRDD<String> simpleRDD) {

        JavaRDD<String> wuRDD = simpleRDD.map(new FnStrPointMap());
        List<String> output = wuRDD.collect();
    }

    public static JavaRDD<String> createSimpleRDD(JavaSparkContext sc) {

        List<String> sList = new ArrayList<String>(dataCount);
        String mydata = InputDataString.substring(0, dataStringSize);
        System.out.println("Sample data size: " + mydata.length());

        for (int i = 1; i <= dataCount; i++) {
            String myd = new String(mydata);
            sList.add(myd);
        }

        JavaRDD<String> oneRDD = null;
        if (slices == 1)
            oneRDD = sc.parallelize(sList);
        else
            oneRDD = sc.parallelize(sList, slices);

        oneRDD.cache();

        return oneRDD;
    }


    static class FnStrPointMap extends Function<String, String> {
        int count = 0;

        public String call(String data) {
            count++;
            return "k" + count;
        }
    }

    public static String InputDataString = "4k long str";

}
