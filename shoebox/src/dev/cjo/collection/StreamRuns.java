package dev.cjo.collection;

import java.io.File;
import java.security.PrivilegedAction;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Exploring Stream capabilities
 * @author sijocherian
 */
public class StreamRuns {

    public static void main(String[] args) throws Exception {

        //streamAndFilter();

        File[] filesInDir = {
                new File("1490124759175_thumbnails.mini-66.jpg"),
                new File("1490124759175_thumbnails.mini-7.jpg"),
                new File("1490124759175_thumbnails.mini-6.jpg"),
                new File("1490124759175_thumbnails.mini-70.jpg")
        };
        //filesInDir = ThumbnailFilenameComparator.sort(filesInDir);

        //streamAndSort();
        streamAndFunctionalInterface();

    }

    static void streamAndSort() {

        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(123, "Jack", "Johnson", LocalDate.of(1988, Month.APRIL, 12)));
        employees.add(new Employee(345, "Cindy", "Bower", LocalDate.of(2011, Month.DECEMBER, 15)));
        employees.add(new Employee(567, "Perry", "Node", LocalDate.of(2005, Month.JUNE, 07)));
        employees.add(new Employee(467, "Pam", "Krauss", LocalDate.of(2005, Month.JUNE, 07)));
        employees.add(new Employee(435, "Fred", "Shak", LocalDate.of(1988, Month.APRIL, 17)));
        employees.add(new Employee(678, "Ann", "Lee", LocalDate.of(2007, Month.APRIL, 12)));


        Comparator<Employee> byEmployeeNumber = (e1, e2) -> Integer.compare(
                e1.getEmployeeNumber(), e2.getEmployeeNumber());

        System.out.println("sort byEmployeeNumber");
        List<Employee>  sorted = employees.stream().sorted(byEmployeeNumber).collect(Collectors.toList());
        employees.stream().sorted(byEmployeeNumber)
                .forEach(e -> System.out.println("\t"+e));

        System.out.println("sort getEmployeeLastName");
        Comparator<Employee> byLastName = Comparator.comparing(
                Employee::getEmployeeLastName,
                 String.CASE_INSENSITIVE_ORDER);
        employees.stream().sorted(byLastName)
                .forEach(e -> System.out.println("\t"+e));

    }






    static void streamAndFunctionalInterface() throws Exception {

        /*
        Without lambda expressions:

obj.aMethod(new AFunctionalInterface() {
    @Override
    public boolean anotherMethod(int i)
    {
        return i == 982
    }
});

With lambda expressions:
obj.aMethod(i -> i == 982);
        */
        ArrayList<String> list = new ArrayList<>();
        list.add("cat");
        list.add("dog");
        list.add("cheetah");
        list.add("deer");

        //Predicate
        // Remove elements that start with c.
        list.removeIf(element -> element.startsWith("c"));
        System.out.println(list.toString());

        //UnaryOperator
        list.replaceAll(element -> "Prefix " + element);

        //A BiConsumer is a functional object that receives two parameters. Here we use a BiConsumer in the forEach method on HashMap.
        HashMap<String, String> hash = new HashMap<>();
        hash.put("cat", "orange");
        hash.put("dog", "black");
        hash.put("snake", "green");
        // Use lambda expression that matches BiConsumer to display HashMap.
        hash.forEach((string1, string2) -> System.out.println(string1 + "..."
                + string2 + ", " + string1.length()));




        /* Predicate
         Streams Filter */
        List languages = Arrays.asList("Java", "Scala", "C++", "Haskell", "Lisp");
        System.out.println("\nStream1");
        //languages.forEach(n -> System.out.println(n));
        //or short form in fn is not modifing element
        languages.forEach(System.out::println);

        System.out.println("Languages which starts with J :");
        filter(languages, (str) -> str.startsWith("J"));


        System.out.println("Print all languages :");
        filter(languages, (str) -> true);

        System.out.println("Print language whose length greater than 4:");
        filter(languages, (str) -> str.length() > 4);

        //if a method accept interface declared in java.util.function package e.g. Predicate, Function, Consumer or Supplier,
        // you can pass lambda expression to them.
        Predicate<String> startsWithJ = (n) -> n.startsWith("J");
        languages.stream().filter((Predicate<String>) (str) -> str.endsWith("a"))
                .forEach((n) -> System.out.print("\nStream 1.1 " + n));

        System.out.println("\nStream2");
        // applying 12% VAT on each purchase
        // Without lambda expressions:
        /*List costBeforeTax = Arrays.asList(100, 200, 300, 400, 500);
        for (Integer cost : costBeforeTax) {
            double price = cost + .12*cost;
            System.out.println(price);
        }*/
        // With Lambda expression:
        List<Integer> costBeforeTax = Arrays.asList(100, 200, 300, 400, 500);
        costBeforeTax.stream().map((cost) -> cost + .12 * cost).forEach(System.out::println);

        //Map and Reduce operations are core of functional programming, reduce is also known as fold operation
        //reduce to one Total value
        double bill = costBeforeTax.stream().map((cost) -> cost + .12 * cost)
                .reduce((sum, cost1) -> sum + cost1).get();
        System.out.println("Total : " + bill);

        //Accumulator
        BinaryOperator<Double> accumulator = (ia, ib) -> ia + ib;
        bill = costBeforeTax.stream().map((cost) -> cost + .12 * cost).reduce(accumulator).get();
        System.out.println("Total : " + bill);

        //reduce as plain old function
        Double totalbill = 0.0;
        for (Double s :
                costBeforeTax.stream().map((cost) -> cost + .12 * cost)
                        .collect(Collectors.toList())) {
            totalbill = BiOperate(totalbill, s, (ia, ib) -> ia + ib);
        }
        System.out.println("totalbill : " + totalbill);





        //collect
        System.out.println("\nStream3");
        // Convert String to Uppercase and join them using coma
        List<String> G7 = Arrays
                .asList("USA", "Japan", "France", "Germany", "Italy", "U.K.", "Canada");
        String G7Countries = G7.stream().map(x -> x.toUpperCase())
                .collect(Collectors.joining(", "));
        System.out.println(G7Countries);

        /// Collectors static methods
        //collect another List
        System.out.println("\nStream3.1");
        // Create a List with String more than 2 characters
        List<String> filtered = G7.stream().filter(x -> x.length() > 4)
                .collect(Collectors.toList());
        System.out.printf("Original List : %s, filtered list : %s %n", G7, filtered);

        //collectingAndThen
        System.out.println("\nStream3.2");
        List<String> adapterFiltered = G7.stream().filter(x -> x.length() > 4).collect(
                Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        System.out.printf("Adaptedfiltered list : %s %n", adapterFiltered);
        //Collections$UnmodifiableCollection        adapterFiltered.add("mytest");

        //List<String> filtered = G7.stream().filter(x -> x.length()> 4).collect(Collectors.toList());
        //System.out.printf("Original List : %s, filtered list : %s %n", G7, filtered);

        System.out.println("\nStream3 done");




        //adv 1
        System.out.println("\nAdvStream 1");


        //Diff target types
        Callable<String> callMe = () -> "Hello";
        System.out.println(callMe.call());


        PrivilegedAction<String> action = () -> "PrivilegedAction Hello";
        System.out.println( action.run() );


        Predicate<Integer> isOdd = n -> n % 2 != 0;
        BinaryOperator<Integer> sum = (x, y) -> x + y;
        Callable<Integer> callMe1 = () -> 42;
        //Block<String> printer -> (String s) -> { System.out.println(s); };
        Runnable runner = () -> {
            System.out.println("Hello World!");
        };
        //Thread thread1 = new Thread(runner);
        //thread1.start();
        new Thread(() -> {
            System.out.println("Hello World!");
        }).start();

        System.out.println("\nAdvStream 2");



        class Person {
            String city, lastName, firstName;

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getLastName() {
                return lastName;
            }

            public void setLastName(String lastName) {
                this.lastName = lastName;
            }

            public String getFirstName() {
                return firstName;
            }

            public void setFirstName(String firstName) {
                this.firstName = firstName;
            }

            public Person(String lastName, String firstName, String city) {
                this.city = city;
                this.lastName = lastName;
                this.firstName = firstName;
            }
        }

        List<Person> pList = Arrays.asList(
                new Person("engel", "todd", "lynn"),
                new Person("engel", "leslie", "lynn"),
                new Person("cherian", "s", "lexington"),
                new Person("cherian", "j", "lynn")
        );

        //groupBy
        Map<String, Set<String>> namesByCity = pList.stream().collect(
                Collectors.groupingBy(
                        Person::getCity,
                        //Collector implementing the downstream reduction
                        Collectors.mapping(Person::getLastName, Collectors.toSet())));

        System.out.printf("namesByCity list : %s %n", namesByCity);

    }

    public static void filter(List<String> names, Predicate<String> condition) {
        for (String name : names) {
            if (condition.test(name)) {
                System.out.println(name + " ");
            }
        }
    }

    public static Double BiOperate(Double a, Double b, BinaryOperator<Double> opr) {
        return opr.apply(a, b);
    }


    /*while
            ateemp++
            exec

              if failed:
                 if exeeded boot out


               //.info("Retrying operation in " + attemptInterval / 1000L + " seconds");
              sleep*/
/*    public static void withRetry(Consumer action) { //Function<? super T, ? super R > action
        long attemptInterval = 10 * 1000L;
        int attemptCount=0, maxAttempts=3;

        do {
            try {

                attemptCount++;
                action.accept();
            } catch (Exception e) {
                // N.B. we can cast to E since it's the only possibility
                evaluateRetry((E) e, attemptCount, transaction);
            }
            try {
                Thread.sleep(attemptInterval);
                //Thread.sleep(sleepTime);
                //sleepTime = Math.min(MAX_SLEEP, sleepTime * 2);
            } catch (InterruptedException e) {
                throw new ExecuteException("Waiting to retry an operation is interrupted");
            }
            //.info("Retrying operation in " + attemptInterval / 1000L + " seconds");

            new RuntimeException("Maximum attempts exceeded");


        } while ( attemptCount <= maxAttempts);
    }*/

    public static class Employee {

        private Integer employeeNumber;
        private String employeeFirstName;
        private String employeeLastName;
        private LocalDate hireDate;

        //..

        public Employee(
                Integer employeeNumber, String employeeFirstName, String employeeLastName,
                LocalDate hireDate) {
            this.employeeNumber = employeeNumber;
            this.employeeFirstName = employeeFirstName;
            this.employeeLastName = employeeLastName;
            this.hireDate = hireDate;
        }

        public Integer getEmployeeNumber() {
            return employeeNumber;
        }

        public void setEmployeeNumber(Integer employeeNumber) {
            this.employeeNumber = employeeNumber;
        }

        public String getEmployeeFirstName() {
            return employeeFirstName;
        }

        public void setEmployeeFirstName(String employeeFirstName) {
            this.employeeFirstName = employeeFirstName;
        }

        public String getEmployeeLastName() {
            return employeeLastName;
        }

        public void setEmployeeLastName(String employeeLastName) {
            this.employeeLastName = employeeLastName;
        }

        public LocalDate getHireDate() {
            return hireDate;
        }

        public void setHireDate(LocalDate hireDate) {
            this.hireDate = hireDate;
        }

        @Override
        public String toString() {
            return "Employee{" +
                    "employeeNumber=" + employeeNumber +
                    ", employeeFirstName='" + employeeFirstName + '\'' +
                    ", employeeLastName='" + employeeLastName + '\'' +
                    ", hireDate=" + hireDate +
                    '}';
        }
    }

    public static class ThumbnailFilenameComparator {

        /*public int compare(File f1, File f2) {
            String s1 = f1.getName();
            String s2 = f2.getName();

        }*/
        public static File[] sort(File[] fileArray) {

            Arrays.sort(fileArray, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    try {
                        int i1, i2;
                        Matcher nameMatcher1 = FilenamePattern.matcher(f1.getName());
                        if (nameMatcher1.find()) {
                            String time1 = nameMatcher1.group(1);
                            i1 = Integer.parseInt(time1);
                        } else {
                            throw new AssertionError(
                                    "Thumbnail filename not matched expected pattern");
                        }

                        Matcher nameMatcher2 = FilenamePattern.matcher(f2.getName());
                        if (nameMatcher2.find()) {
                            String time2 = nameMatcher2.group(1);
                            i2 = Integer.parseInt(time2);
                        } else {
                            throw new AssertionError(
                                    "Thumbnail filename not matched expected pattern");
                        }

                        return i1 - i2;
                    } catch (NumberFormatException e) {
                        throw new AssertionError(e);
                    }
                }
            });

            return fileArray;
        }

        public static Pattern FilenamePattern = Pattern.compile("^\\S+\\-([0-9]+)\\.[^\\.]+$");
    }
}
