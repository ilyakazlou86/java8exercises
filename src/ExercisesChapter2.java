import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ExercisesChapter2 {
    /**
     * Exercise 1.
     * Write a parallel version of the for loop in Section 2.1, “From Iteration to
     * Stream Operations,” on page 22. Obtain the number of processors. Make that
     * many separate threads, each working on a segment of the list, and total up
     * the results as they come in. (You don’t want the threads to update a single
     * counter. Why?)
     * @throws IOException 
     */
    private static <T> void exercise1() throws Exception {
        String contents = new String(Files.readAllBytes(
            Paths.get(System.getProperty("java.home"), "COPYRIGHT")), StandardCharsets.UTF_8); // Read file into string
        List<String> words = Arrays.asList(contents.split("[\\P{L}]+")); // Split into words; nonletters are delimiters
        
        /* sequential words counting */
        int count = 0;
        for (String w : words) {
            if (w.length() > 12) count++;
        }
        
        /* parallel words counting */
        int cores = Runtime.getRuntime().availableProcessors(); // obtaining the number of processors
        int size = words.size();
        int wordsPerCore = size / cores + 1;
        ExecutorService exec = Executors.newFixedThreadPool(cores);
        @SuppressWarnings("unchecked") Future<Integer>[] results = new Future[cores];
        for (int i = 0; i < cores; i++) {
            int from = i * wordsPerCore;
            int to = Math.min(from + wordsPerCore, size);
            results[i] = exec.submit(() -> {
                int count2 = 0; // we don't want the threads to update a single counter to avoid the race condition on it
                for (int j = from; j < to; j++) {
                    if (words.get(j).length() > 12) count2++;
                }
                return count2;
            });
        }
        exec.shutdown();
        int parallelCount = 0;
        for (Future<Integer> f : results) {
            parallelCount += f.get();
        }
        System.out.printf("Result:\nSequential count: %d\nParallel count: %d\n", count, parallelCount);
    }
    
    /**
     * Exercise 2.
     * Verify that asking for the first five long words does not call the filter method
     * once the fifth long word has been found. Simply log each method call.
     * @throws Exception 
     */
    private static void exercise2() throws Exception {
        String contents = new String(Files.readAllBytes(
            Paths.get(System.getProperty("java.home"), "COPYRIGHT")), StandardCharsets.UTF_8);
        List<String> words = Arrays.asList(contents.split("[\\P{L}]+"));
        
        AtomicInteger callsCount = new AtomicInteger(0);
        List<String> firstFiveLongWords = words.stream()
                                               .filter(w -> {
                                                   callsCount.incrementAndGet();
                                                   return w.length() > 12;
                                               })
                                               .limit(5)
                                               .collect(Collectors.toList());
        System.out.printf("Result:\nTotal words: %d\nFiltered words: %d\nFilter calls: %d\n", words.size(), firstFiveLongWords.size(), callsCount.get());
    }
    
    /**
     * Exercise 3.
     * Measure the difference when counting long words with a parallelStream instead
     * of a stream. Call System.currentTimeMillis before and after the call, and print the
     * difference. Switch to a larger document (such as War and Peace) if you have
     * a fast computer.
     */
    private static long measureTimeOf(Supplier<?> task) {
        long startTime = System.currentTimeMillis();
        task.get();
        return System.currentTimeMillis() - startTime;
    }
    
    private static void getStatistics(String text, Supplier<?> task, int size) {
        long sum = 0;
        long sumOfSquares = 0;
        for (int i = 0; i < size; i++) {
            long measuredTime = measureTimeOf(task);
            sum += measuredTime;
            sumOfSquares += measuredTime * measuredTime;
        }
        double averageTime = 1. * sum / size; // average time
        double sigma = Math.sqrt(1. * sumOfSquares / size - averageTime * averageTime); // standard deviation
        System.out.printf("%s: %.3f \u00B1%.3f ms\n", text, averageTime, 3 * sigma);
    }
    
    private static void exercise3(Path source) throws Exception {
        String contents = new String(Files.readAllBytes(source), StandardCharsets.UTF_8);
        List<String> words = Arrays.asList(contents.split("[\\P{L}]+"));
        Supplier<?> sequentialTask = () -> words.stream().filter(w -> w.length() > 12).count(); // sequential task
        Supplier<?> parallelTask = () -> words.parallelStream().filter(w -> w.length() > 12).count(); // parallel task
        int size = 10000; // number of measurements of each task
        System.out.println("Result:");
        getStatistics("Estimated sequential time", sequentialTask, size);
        Thread.sleep(1000); // let gc do the stuff
        getStatistics("Estimated parallel time", parallelTask, size);
        /* 
         * I've not gain any crucial performance on my fucking dual core machine:(
         * Even for large files, Karl!!!
         */
    }
    
    /**
     * Exercise 4.
     * Suppose you have an array int[] values = { 1, 4, 9, 16 }. What is
     * Stream.of(values)? How do you get a stream of int instead?
     */
    private static void exercise4() {
        int[] values = { 1, 4, 9, 16 };
        Stream.of(values); // gives stream of ONE element of type int[]
        IntStream.of(values); // gives stream of four int elements
    }
    
    /**
     * Exercise 5.
     * Using Stream.iterate, make an infinite stream of random numbers—not by
     * calling Math.random but by directly implementing a linear congruential generator.
     * In such a generator, you start with x(0) = seed and then produce x(n + 1) =
     * (a x(n) + c) % m, for appropriate values of a, c, and m. You should implement a
     * method with parameters a, c, m, and seed that yields a Stream<Long>. Try out a =
     * 25214903917, c = 11, and m = 2^48.
     */
    private static Stream<Long> generate(long a, long c, long m, long seed) {
        return Stream.iterate(seed, x -> (a * x + c) % m);
    }
    
    private static void exercise5() {
        Stream<?> numbers = generate(25214903917L, 11L, 0x1000000000000L, 0L);
        System.out.printf("Result: %s, ...\n", numbers.limit(5)
                                                      .map(Object::toString)
                                                      .collect(Collectors.joining(", ")));
    }
    
    /**
     * Exercise 6.
     * The characterStream method in Section 2.3, “The filter, map, and flatMap Methods,”
     * on page 25, was a bit clumsy, first filling an array list and then turning it
     * into a stream. Write a stream-based one-liner instead. One approach is to
     * make a stream of integers from 0 to s.length() - 1 and map that with the
     * s::charAt method reference.
     */
    private static Stream<Character> characterStream(String s) { // clumsy method
        List<Character> result = new ArrayList<>();
        for (char c : s.toCharArray()) result.add(c);
        return result.stream();
    }
    
    private static Stream<Character> characterStream2(String s) { // cool method
        return IntStream.range(0, s.length()).mapToObj(s::charAt);
        /*
         * We can also make it shorter:
         *     return s.chars().mapToObj(c -> (char) c);
         */
    }
    
    private static void exercise6(String source) {
        Stream<?> chars = characterStream2(source);
        System.out.printf("Result: %s\n", chars.map(Object::toString)
                                               .collect(Collectors.joining(", ")));
    }

    /**
     * Exercise 7.
     * Your manager asks you to write a method public static <T> boolean
     * isFinite(Stream<T> stream). Why isn’t that such a good idea? Go ahead and
     * write it anyway.
     */
    public static <T> boolean isFinite(Stream<T> stream) {
        return stream.spliterator().getExactSizeIfKnown() != -1L;
    }
    
    private static void exercise7() {
        Stream<?> finiteStream = Stream.of("1", "2", "3");
        Stream<?> infiniteStream = Stream.generate(() -> "1");
        System.out.printf("Result:\nisFinite(finiteStream) => %s\n", isFinite(finiteStream)); // gives true
        System.out.printf("isFinite(infiniteStream) => %s\n", isFinite(infiniteStream)); // gives false
        System.out.printf("isFinite(emptyStream) => %s\n", isFinite(Stream.empty())); // gives true
        /*
         * Well, spliterator works fine but it's a terminal operation.
         * So we'll get IllegalStateException if we try to work with the stream in the future
         */
    }
    
    /**
     * Exercise 8.
     * Write a method public static <T> Stream<T> zip(Stream<T> first, Stream<T> second)
     * that alternates elements from the streams first and second, stopping when
     * one of them runs out of elements.
     */
    public static <T> Stream<T> zip(Stream<T> first, Stream<T> second) {
        /*
         * I've not found a better solution than that:(
         */
        Iterator<T> firstIter = first.iterator();
        Iterator<T> secondIter = second.iterator();
        Stream<T> result = Stream.empty();
        while(firstIter.hasNext() && secondIter.hasNext()) {
            result = Stream.concat(result, Stream.of(firstIter.next(), secondIter.next()));
        }
        return result;
    }
    
    private static void exercise8() {
        Integer[] firstArray = { 1, 3, 5, 7, 9 };
        Integer[] secondArray = { 2, 4, 6, 8 };
        Stream<Integer> firstStream = Stream.of(firstArray);
        Stream<Integer> secondStream = Stream.of(secondArray);
        Stream<?> zippedStream = zip(firstStream, secondStream);
        System.out.printf("Result:\nFirst stream: %s\n", Arrays.toString(firstArray));
        System.out.printf("Second stream: %s\n", Arrays.toString(secondArray));
        System.out.printf("Zipped stream: %s\n", zippedStream.collect(Collectors.toList()));
    }
    
    /**
     * Exercise 9.
     * Join all elements in a Stream<ArrayList<T>> to one ArrayList<T>. Show how to do
     * this with the three forms of reduce.
     */
    private static <T> ArrayList<T> streamJoin1(Stream<ArrayList<T>> source) {
        return source.reduce((left, right) -> {
                                 ArrayList<T> result = new ArrayList<>(left);
                                 result.addAll(right);
                                 return result;
                             }).orElse(new ArrayList<T>(0));
    }
    
    private static <T> ArrayList<T> streamJoin2(Stream<ArrayList<T>> source) {
        return source.reduce(new ArrayList<T>(),
                             (left, right) -> {
                                 ArrayList<T> result = new ArrayList<>(left);
                                 result.addAll(right);
                                 return result;
                             });
    }
    
    private static <T> ArrayList<T> streamJoin3(Stream<ArrayList<T>> source) {
        return source.reduce(new ArrayList<T>(),
                             (left, right) -> {
                                 ArrayList<T> result = new ArrayList<>(left);
                                 result.addAll(right);
                                 return result;
                             },
                             (left, right) -> {
                                 ArrayList<T> result = new ArrayList<>(left);
                                 result.addAll(right);
                                 return result;
                             });
    }
    
    private static void exercise9() {
        @SuppressWarnings("unchecked")
        ArrayList<Integer>[] lists = new ArrayList[] {
            new ArrayList<Integer>(Arrays.asList(1, 2, 3)),
            new ArrayList<Integer>(Arrays.asList(4, 5)),
            new ArrayList<Integer>(Arrays.asList(6, 7, 8, 9))
        };
        System.out.printf("Result:\nSource stream: %s\n", Arrays.toString(lists));
        System.out.printf("streamJoin1(source): %s\n", streamJoin1(Stream.of(lists)));
        System.out.printf("streamJoin2(source): %s\n", streamJoin2(Stream.of(lists)));
        System.out.printf("streamJoin3(source): %s\n", streamJoin3(Stream.of(lists).parallel()));
    }
    
    /**
     * Exercise 10.
     * Write a call to reduce that can be used to compute the average of a Stream<Double>.
     * Why can’t you simply compute the sum and divide by count()?
     */
    private static class IntermediateResult {
        final double total;
        final int count;
        
        IntermediateResult(double total, int count) {
            this.total = total;
            this.count = count;
        }
        
        IntermediateResult() {
            this(0D, 0);
        }
        
        double average() {
            return count == 0 ? 0D : total / count;
        }
        
        IntermediateResult accumulate(double num) {
            return new IntermediateResult(total + num, count + 1);
        }
        
        IntermediateResult combine(IntermediateResult result) {
            return new IntermediateResult(total + result.total, count + result.count);
        }
    }
    
    private static void exercise10() {
        /*
         * I have no idea how to do it with core API.
         * So here is my solution
         */
        Stream<Double> source = Stream.generate(Math::random).limit(10);
        double average = source.reduce(new IntermediateResult(),
                                       IntermediateResult::accumulate,
                                       IntermediateResult::combine)
                               .average();
        System.out.printf("Result: %f\n", average);
        /*
         * We can't compute the sum and divide by count because both operations are terminal
         */
    }
    
    /**
     * Exercise 11.
     * It should be possible to concurrently collect stream results in a single ArrayList,
     * instead of merging multiple array lists, provided it has been constructed with
     * the stream’s size, since concurrent set operations at disjoint positions
     * are threadsafe. How can you achieve that?
     */
    private static void exercise11() {
        // I don't understand this task:(
    }
    
    /**
     * Exercise 12.
     *  Count all short words in a parallel Stream<String>, as described in Section 2.13,
     *  “Parallel Streams,” on page 40, by updating an array of AtomicInteger. Use
     *  the atomic getAndIncrement method to safely increment each counter.
     */
    private static void exercise12() throws Exception {
        String contents = new String(Files.readAllBytes(
            Paths.get(System.getProperty("java.home"), "COPYRIGHT")), StandardCharsets.UTF_8);
        Stream<String> words = Stream.of(contents.split("[\\P{L}]+"));
        
        AtomicInteger[] shortWords = Stream.generate(() -> new AtomicInteger())
                                           .limit(12)
                                           .toArray(AtomicInteger[]::new);
        words.parallel().forEach(s -> {
            if (s.length() < 12)
                shortWords[s.length()].incrementAndGet();
            });
        System.out.printf("Result: %s\n", Arrays.toString(shortWords));
    }
    
    /**
     * Exercise 13.
     * Repeat the preceding exercise, but filter out the short strings and use the
     * collect method with Collectors.groupingBy and Collectors.counting.
     * @throws Exception 
     */
    private static void exercise13() throws Exception {
        String contents = new String(Files.readAllBytes(
            Paths.get(System.getProperty("java.home"), "COPYRIGHT")), StandardCharsets.UTF_8);
        Stream<String> words = Stream.of(contents.split("[\\P{L}]+"));
        
        Map<Integer, Long> shortWords = words.parallel()
                                             .filter(s -> s.length() < 12)
                                             .collect(Collectors.groupingBy(String::length, Collectors.counting()));
        System.out.printf("Result: %s\n", shortWords.values());
    }

    public static void main(String[] args) throws Exception {
        System.out.println("*** Exercise 1 ***\nTask: \r\n" +
                "Write a parallel version of the for loop in Section 2.1, “From Iteration to\r\n" + 
                "Stream Operations,” on page 22. Obtain the number of processors. Make that\r\n" + 
                "many separate threads, each working on a segment of the list, and total up\r\n" + 
                "the results as they come in. (You don’t want the threads to update a single\r\n" + 
                "counter. Why?)");
        exercise1();
        System.out.println("\n*** Exercise 2 ***\nTask: Verify that asking for the first five long words does not call the filter method\r\n" + 
                "once the fifth long word has been found. Simply log each method call.");
        exercise2();
        System.out.println("\n*** Exercise 3 ***\nTask: Measure the difference when counting long words with a parallelStream instead\r\n" + 
                "of a stream. Call System.currentTimeMillis before and after the call, and print the\r\n" + 
                "difference. Switch to a larger document (such as War and Peace) if you have\r\n" + 
                "a fast computer.");
        exercise3(Paths.get(System.getProperty("java.home"), "THIRDPARTYLICENSEREADME.txt"));
        System.out.println("\n*** Exercise 4 ***\nTask: \r\n" +
                "Suppose you have an array int[] values = { 1, 4, 9, 16 }. What is\r\n" + 
                "Stream.of(values)? How do you get a stream of int instead?");
        exercise4();
        System.out.println("\n*** Exercise 5 ***\nTask: \r\n" +
                "Using Stream.iterate, make an infinite stream of random numbers—not by\r\n" + 
                "calling Math.random but by directly implementing a linear congruential generator.\r\n" + 
                "In such a generator, you start with x(0) = seed and then produce x(n + 1) =\r\n" + 
                "(a x(n) + c) % m, for appropriate values of a, c, and m. You should implement a\r\n" + 
                "method with parameters a, c, m, and seed that yields a Stream<Long>. Try out a =\r\n" + 
                "25214903917, c = 11, and m = 2^48.");
        exercise5();
        System.out.println("\n*** Exercise 6 ***\nTask: \r\n" +
                "The characterStream method in Section 2.3, “The filter, map, and flatMap Methods,”\r\n" + 
                "on page 25, was a bit clumsy, first filling an array list and then turning it\r\n" + 
                "into a stream. Write a stream-based one-liner instead. One approach is to\r\n" + 
                "make a stream of integers from 0 to s.length() - 1 and map that with the\r\n" + 
                "s::charAt method reference.");
        exercise6("Hello world!");
        System.out.println("\n*** Exercise 7 ***\nTask: \r\n" +
                "Your manager asks you to write a method public static <T> boolean\r\n" + 
                "isFinite(Stream<T> stream). Why isn’t that such a good idea? Go ahead and\r\n" + 
                "write it anyway.");
        exercise7();
        System.out.println("\n*** Exercise 8 ***\nTask: \r\n" +
                "Write a method public static <T> Stream<T> zip(Stream<T> first, Stream<T> second)\r\n" + 
                "that alternates elements from the streams first and second, stopping when\r\n" + 
                "one of them runs out of elements.");
        exercise8();
        System.out.println("\n*** Exercise 9 ***\nTask: \r\n" +
                "Join all elements in a Stream<ArrayList<T>> to one ArrayList<T>. Show how to do\r\n" + 
                "this with the three forms of reduce.");
        exercise9();
        System.out.println("\n*** Exercise 10 ***\nTask: \r\n" +
                "Write a call to reduce that can be used to compute the average of a Stream<Double>.\r\n" + 
                "Why can’t you simply compute the sum and divide by count()?");
        exercise10();
        System.out.println("\n*** Exercise 11 ***\nTask: \r\n" +
                "It should be possible to concurrently collect stream results in a single ArrayList,\r\n" + 
                "instead of merging multiple array lists, provided it has been constructed with\r\n" +
                "the stream’s size, since concurrent set operations at disjoint positions\r\n" + 
                "are threadsafe. How can you achieve that?");
        exercise11();
        System.out.println("\n*** Exercise 12 ***\nTask: \r\n" +
                "Count all short words in a parallel Stream<String>, as described in Section 2.13,\r\n" + 
                "“Parallel Streams,” on page 40, by updating an array of AtomicInteger. Use\r\n" + 
                "the atomic getAndIncrement method to safely increment each counter.");
        exercise12();
        System.out.println("\n*** Exercise 13 ***\nTask: \r\n" +
                "Repeat the preceding exercise, but filter out the short strings and use the\r\n" + 
                "collect method with Collectors.groupingBy and Collectors.counting.");
        exercise13();
    }
}
