import java.io.File;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class ExercisesChapter6 {
    
    /**
     * Exercise 1.
     * Write a program that keeps track of the longest string that is observed by a
     * number of threads. Use an AtomicReference and an appropriate accumulator.
     */
    private static void exercise01() throws Exception {
        System.out.println("Result:");
        String contents = new String(Files.readAllBytes(
            Paths.get(System.getProperty("java.home"), "COPYRIGHT")), StandardCharsets.UTF_8);
        List<String> words = Arrays.asList(contents.split("[\\P{L}]+"));
        AtomicReference<String> longestString = new AtomicReference<>("");
        BinaryOperator<String> updater = (s1, s2) -> s1.length() > s2.length() ? s1 : s2;
        words.parallelStream().forEach(s -> longestString.updateAndGet(x -> updater.apply(x, s)));
        System.out.printf("The longest string is \"%s\"\n", longestString);
    }

    /**
     * Exercise 2.
     * Does a LongAdder help with yielding a sequence of increasing IDs? Why or
     * why not?
     */
    private static void exercise02() {
        /*
         * Javadoc for method LongAdder::sum says that "concurrent updates that occur while
         * the sum is being calculated might not be incorporated". So, the answer is no.
         */
    }
    
    /**
     * Exercise 3.
     * Generate 1,000 threads, each of which increments a counter 100,000 times.
     * Compare the performance of using AtomicLong versus LongAdder.
     */
    private static long measureTimeOf(Runnable task) {
        long startTime = System.currentTimeMillis();
        task.run();
        return System.currentTimeMillis() - startTime;
    }
    
    private static void getStatistics(String text, Runnable task, int size) {
        long sum = 0L;
        long sumOfSquares = 0L;
        for (int i = 0; i < size; i++) {
            long measuredTime = measureTimeOf(task);
            sum += measuredTime;
            sumOfSquares += measuredTime * measuredTime;
        }
        double averageTime = 1D * sum / size; // average time
        double sigma = Math.sqrt(1D * sumOfSquares / size - averageTime * averageTime); // standard deviation
        System.out.printf("%s: %.3f \u00B1%.3f ms\n", text, averageTime, 3D * sigma);
    }
    
    private static void exercise03() throws Exception {
        System.out.println("Result:");
        int size = 10;
        int threadsCount = 1000;
        int counts = 100_000;
        Runnable atomicLongTask = () -> {
            AtomicLong counter = new AtomicLong();
            ExecutorService exec = Executors.newFixedThreadPool(threadsCount);
            for (int i = 0; i < threadsCount; i++)
                exec.execute(() -> {
                    for (int j = 0; j < counts; j++)
                        counter.incrementAndGet();
                });
            exec.shutdown();
            try {
                exec.awaitTermination(10L, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                exec.shutdownNow();
            }
        };
        Runnable longAdderTask = () -> {
            LongAdder counter = new LongAdder();
            ExecutorService exec = Executors.newFixedThreadPool(threadsCount);
            for (int i = 0; i < threadsCount; i++)
                exec.execute(() -> {
                    for (int j = 0; j < counts; j++)
                        counter.increment();
                });
            exec.shutdown();
            try {
                exec.awaitTermination(10L, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                exec.shutdownNow();
            }
        };
        getStatistics("Estimated AtomicLong time", atomicLongTask, size);
        Thread.sleep(1000L); // let gc do the stuff
        getStatistics("Estimated LongAdder time", longAdderTask, size);
        /*
         * Well, on my ancient machine LongAdder works 10-15% faster than AtomicLong :(
         */
    }
    
    /**
     * Exercise 4.
     * Use a LongAccumulator to compute the maximum or minimum of the
     * accumulated elements.
     */
    private static void exercise04() {
        System.out.println("Result:");
        LongAccumulator maximizer = new LongAccumulator(Math::max, Long.MIN_VALUE);
        Random rnd = new Random();
        LongStream.generate(rnd::nextLong)
                  .limit(1000L)
                  .parallel()
                  .forEach(maximizer::accumulate);
        System.out.printf("Maximum element: %d", maximizer.get());
    }
    
    /**
     * Exercise 5.
     * Write an application in which multiple threads read all words from a collection
     * of files. Use a ConcurrentHashMap<String, Set<File>> to track in which files
     * each word occurs. Use the merge method to update the map.
     */
    private static interface ConsumerEx<T> {
        void accept(T t) throws Exception;
    }
    
    private static <T> Consumer<T> unchecked(ConsumerEx<T> f) {
        return arg -> {
            try {
                f.accept(arg);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            catch (Throwable t) {
                throw t;
            }
        };
    }
    
    private static Map<String, Set<File>> getOccurrences(Collection<File> sources) throws Exception {
        ConcurrentHashMap<String, Set<File>> occurrences = new ConcurrentHashMap<>();
        sources.parallelStream().forEach(unchecked(f -> {
            String contents = new String(Files.readAllBytes(f.toPath()));
            List<String> words = Arrays.asList(contents.split("[\\P{L}]+"));
            words.parallelStream().forEach(w -> occurrences.merge(w,
                                                                  Collections.singleton(f),
                                                                  (oldValue, newValue) -> {
                                                                      Set<File> result = new HashSet<>(oldValue);
                                                                      result.addAll(newValue);
                                                                      return result;
                                                                  }));
                                                                  
        }));
        return occurrences;
    }
    
    private static void exercise05() throws Exception {
        System.out.println("Result:");
        File[] sources = new File(System.getProperty("java.home")).listFiles(f -> f.isFile() && f.getName()
                                                                                                 .toLowerCase()
                                                                                                 .endsWith(".txt"));
        Map<String, Set<File>> occurrences = getOccurrences(Arrays.asList(sources));
        String key = "JavaFX";
        if (occurrences.containsKey(key))
            System.out.printf("Word \"%s\" occurs in files: %s\n", key, occurrences.get(key));
        else
            System.out.printf("Word \"%s\" doesn't occur in any file\n", key);
    }
    
    /**
     * Exercise 6.
     * Repeat the preceding exercise, but use computeIfAbsent instead. What is the
     * advantage of this approach?
     */
    private static Map<String, Set<File>> getOccurrences2(Collection<File> sources) throws Exception {
        ConcurrentHashMap<String, Set<File>> occurrences = new ConcurrentHashMap<>();
        sources.parallelStream().forEach(unchecked(f -> {
            String contents = new String(Files.readAllBytes(f.toPath()));
            List<String> words = Arrays.asList(contents.split("[\\P{L}]+"));
            words.parallelStream().forEach(w -> occurrences.computeIfAbsent(w,
                                                                            key -> ConcurrentHashMap.newKeySet())
                                                           .add(f));    
        }));
        return occurrences;
    }
    
    private static void exercise06() throws Exception{
        System.out.println("Result:");
        File[] sources = new File(System.getProperty("java.home")).listFiles(f -> f.isFile() && f.getName()
                                                                                                 .toLowerCase()
                                                                                                 .endsWith(".txt"));
        Map<String, Set<File>> occurrences = getOccurrences2(Arrays.asList(sources));
        String key = "JavaFX";
        if (occurrences.containsKey(key))
            System.out.printf("Word \"%s\" occurs in files: %s\n", key, occurrences.get(key));
        else
            System.out.printf("Word \"%s\" doesn't occur in any file\n", key);
        /*
         * Looks like computeIfAbsent works much faster than merge. I'm not quite sure why, but I suppose
         * the problem is in expensive remappingFunction for merge. Anyway, computeIfAbsent generates new
         * value for empty keys only when necessary. This is a huge advantage.
         */
    }
    
    /**
     * Exercise 7.
     * In a ConcurrentHashMap<String, Long>, find the key with maximum value (breaking
     * ties arbitrarily). Hint: reduceEntries.
     */
    private static void exercise07() {
        System.out.println("Result:");
        ConcurrentHashMap<String, Long> source = new ConcurrentHashMap<>();
        Random rnd = new Random();
        rnd.longs(0L, 1000L).limit(100L)
                            .forEach(x -> source.put("Key" + String.valueOf(x), x));
        Map.Entry<String, ?> result = source.reduceEntries(8L, (e1, e2) -> e1.getValue() > e2.getValue() ? e1 : e2);
        System.out.printf("The key with maximum value: %s\n", result.getKey());
    }
    
    /**
     * Exercise 8.
     * How large does an array have to be for Arrays.parallelSort to be faster than
     * Arrays.sort on your computer?
     */
    private static void exercise08() throws Exception {
        System.out.println("Result:");
        int size = 1_000_000;
        int length = 2;
        Runnable sequentialSortTask = () -> {
            Arrays.sort(new Random().ints().limit(length).toArray());
        };
        Runnable parallelSortTask = () -> {
            Arrays.parallelSort(new Random().ints().limit(length).toArray());
        };
        getStatistics("Sequential sort time", sequentialSortTask, size);
        Thread.sleep(1000L); // let gc do the stuff
        getStatistics("Parallel sort time", parallelSortTask, size);
        /*
         * LOL, parallelSort is always faster on my machine :)
         */
    }
    
    /**
     * Exercise 9.
     * You can use the parallelPrefix method to parallelize the computation of
     * Fibonacci numbers. We use the fact that the nth Fibonacci number is the top
     * left coefficient of Fn, where F = ( 1 1 ; 1 0 ) . Make an array filled with 2 × 2
     * matrices. Define a Matrix class with a multiplication method, use parallelSetAll to
     * make an array of matrices, and use parallelPrefix to multiply them.
     */
    private static class FibonacciMatrix {
        private static long[][] init = { {1L, 1L}, {1L, 0L} };
        private long[][] elements;
        
        public FibonacciMatrix(long[][] elements) {
            this.elements = elements;
        }
        
        public long getFibonacciNumber() {
            return elements[0][0];
        }
        
        public static FibonacciMatrix init(int i) {
            return new FibonacciMatrix(init);
        }
        
        public static FibonacciMatrix mult(FibonacciMatrix m1, FibonacciMatrix m2) {
            long[][] result = new long[2][2];
            for (int i = 0; i < 2; i++)
                for (int j = 0; j < 2; j++)
                    for (int k= 0; k < 2; k++)
                        result[i][j] += m1.elements[i][k] * m2.elements[k][j];
            return new FibonacciMatrix(result);
        }
    }
    
    private static void exercise09() {
        System.out.println("Result:");
        int n = 10;
        FibonacciMatrix[] source = new FibonacciMatrix[n];
        Arrays.parallelSetAll(source, FibonacciMatrix::init);
        Arrays.parallelPrefix(source, FibonacciMatrix::mult);
        System.out.printf("First %d Fibonacci numbers:\n", n);
        Stream.of(source).mapToLong(FibonacciMatrix::getFibonacciNumber).forEach(System.out::println);
    }
    
    /**
     * Exercise 10.
     * Write a program that asks the user for a URL, then reads the web page at that
     * URL, and then displays all the links. Use a CompletableFuture for each stage.
     * Don’t call get. To prevent your program from terminating prematurely, call
     *     ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS);
     */
    private static CompletableFuture<String> readPage(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try (Scanner scanner = new Scanner(new URL(url).openStream(),
                                               StandardCharsets.UTF_8.toString())) {
                scanner.useDelimiter("\\A");
                return scanner.hasNext() ? scanner.next() : null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    private static CompletableFuture<Collection<String>> getLinks(String content) {
        return CompletableFuture.supplyAsync(() -> {
            String urlRegex = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            Pattern urlPattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
            Matcher urlMatcher = urlPattern.matcher(content);
            Collection<String> links = new ArrayList<>(urlMatcher.groupCount());
            while (urlMatcher.find())
                links.add(urlMatcher.group());
            return links;
        });
    }
    
    private static Void handler(Collection<String> result, Throwable error) {
        if (error != null) {
            System.out.printf("Erorr: %s\n", error.getMessage());
            return null;
        }
        if (result == null) {
            System.out.println("Selected page has no links...");
            return null;
        }
        System.out.println("Selected page has following links:");
        result.stream().forEach(System.out::println);
        return null;
    }
    
    private static void exercise10() throws Exception {
        System.out.println("Result:");
        CompletableFuture<?> linksExtractor = CompletableFuture.completedFuture("https://google.com")
                                                               .thenCompose(ExercisesChapter6::readPage)
                                                               .thenCompose(ExercisesChapter6::getLinks)
                                                               .handle(ExercisesChapter6::handler);
        ForkJoinPool.commonPool().awaitQuiescence(10L, TimeUnit.SECONDS);
        linksExtractor.join();
    }
    
    /**
     * Exercise 11.
     * Write a method
     *     public static <T> CompletableFuture<T> repeat(
     *         Supplier<T> action, Predicate<T> until)
     * that asynchronously repeats the action until it produces a value that is
     * accepted by the until function, which should also run asynchronously. Test
     * with a function that reads a java.net.PasswordAuthentication from the console,
     * and a function that simulates a validity check by sleeping for a second and
     * then checking that the password is "secret". Hint: Use recursion.
     */
    public static <T> CompletableFuture<T> repeat(Executor exec, Supplier<T> action, Predicate<T> until) {
        return CompletableFuture.supplyAsync(action, exec).thenCompose(auth -> {
            if (until.test(auth)) {
                System.out.println("You are logged in!");
                return CompletableFuture.completedFuture(auth);
            } else {
                System.err.println("Wrong password!");
                try {
                    return repeat(exec, action, until);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    
    private static PasswordAuthentication login() {
        System.out.print("Enter password: ");
        try {
            @SuppressWarnings("resource") Scanner scanner = new Scanner(System.in);
            String password = scanner.nextLine();
            return new PasswordAuthentication(null, password.toCharArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static boolean validate(PasswordAuthentication auth) {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Arrays.equals(auth.getPassword(), "secret".toCharArray());
    }
    
    private static void exercise11() throws Exception {
        System.out.println("Result:");
        ExecutorService exec = Executors.newCachedThreadPool();
        repeat(exec, ExercisesChapter6::login, ExercisesChapter6::validate);
        exec.awaitTermination(1L, TimeUnit.MINUTES);
        exec.shutdownNow();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("*** Exercise 1 ***\nTask: " +
            "Write a program that keeps track of the longest string that is observed by a\r\n" + 
            "number of threads. Use an AtomicReference and an appropriate accumulator.");
        exercise01();
        System.out.println("\n*** Exercise 2 ***\nTask: " +
            "Does a LongAdder help with yielding a sequence of increasing IDs? Why or\r\n" + 
            "why not?");
        exercise02();
        System.out.println("\n*** Exercise 3 ***\nTask: " +
            "Generate 1,000 threads, each of which increments a counter 100,000 times.\r\n" + 
            "Compare the performance of using AtomicLong versus LongAdder.");
        exercise03();
        System.out.println("\n*** Exercise 4 ***\nTask: " +
            "Use a LongAccumulator to compute the maximum or minimum of the\r\n" + 
            "accumulated elements.");
        exercise04();
        System.out.println("\n*** Exercise 5 ***\nTask: " +
            "Write an application in which multiple threads read all words from a collection\r\n" +
            "of files. Use a ConcurrentHashMap<String, Set<File>> to track in which files\r\n" + 
            "each word occurs. Use the merge method to update the map.");
        exercise05();
        System.out.println("\n*** Exercise 6 ***\nTask: " +
            "Repeat the preceding exercise, but use computeIfAbsent instead. What is the\r\n" + 
            "advantage of this approach?");
        exercise06();
        System.out.println("\n*** Exercise 7 ***\nTask: " +
            "In a ConcurrentHashMap<String, Long>, find the key with maximum value (breaking\r\n" + 
            "ties arbitrarily). Hint: reduceEntries.");
        exercise07();
        System.out.println("\n*** Exercise 8 ***\nTask: " +
            "How large does an array have to be for Arrays.parallelSort to be faster than\r\n" +
            "Arrays.sort on your computer?");
        exercise08();
        System.out.println("\n*** Exercise 9 ***\nTask: " +
            "You can use the parallelPrefix method to parallelize the computation of\r\n" +
            "Fibonacci numbers. We use the fact that the nth Fibonacci number is the top\r\n" +
            "left coefficient of Fn, where F = ( 1 1 ; 1 0 ) . Make an array filled with 2 × 2\r\n" +
            "matrices. Define a Matrix class with a multiplication method, use parallelSetAll to\r\n" +
            "make an array of matrices, and use parallelPrefix to multiply them.");
        exercise09();
        System.out.println("\n*** Exercise 10 ***\nTask: " +
            "Write a program that asks the user for a URL, then reads the web page at that\r\n" + 
            "URL, and then displays all the links. Use a CompletableFuture for each stage.\r\n" + 
            "Don’t call get. To prevent your program from terminating prematurely, call\r\n" + 
            "    ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS);");
        exercise10();
        System.out.println("\n*** Exercise 11 ***\nTask: " +
            "Write a method\r\n" + 
            "    public static <T> CompletableFuture<T> repeat(\r\n" + 
            "        Supplier<T> action, Predicate<T> until)\r\n" + 
            "that asynchronously repeats the action until it produces a value that is\r\n" + 
            "accepted by the until function, which should also run asynchronously. Test\r\n" + 
            "with a function that reads a java.net.PasswordAuthentication from the console,\r\n" + 
            "and a function that simulates a validity check by sleeping for a second and\r\n" + 
            "then checking that the password is \"secret\". Hint: Use recursion.");
        exercise11();
    }
}
