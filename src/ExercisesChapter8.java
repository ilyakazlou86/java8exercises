import java.io.ByteArrayInputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

public class ExercisesChapter8 {
    /**
     * Exercise 1.
     * Write a program that adds, subtracts, divides, and compares numbers
     * between 0 and 2^32 – 1, using int values and unsigned operations. Show why
     * divideUnsigned and remainderUnsigned are necessary.
     */
    private static @interface UnsignedInt {}
    
    private static final class UnsignedMath {
        private static final long MAX_SUM = 0xffffffffL;
        public static final @UnsignedInt int MIN_VALUE = 0;
        public static final @UnsignedInt int MAX_VALUE = -1;
        
        public static @UnsignedInt int add(@UnsignedInt int a, @UnsignedInt int b) {
            if (Integer.toUnsignedLong(a) > MAX_SUM - Integer.toUnsignedLong(b))
                throw new ArithmeticException("a + b > MAX_UINT");
            return a + b;
        }
        
        public static @UnsignedInt int subtract(@UnsignedInt int a, @UnsignedInt int b) {
            if (Integer.toUnsignedLong(a) < Integer.toUnsignedLong(b))
                throw new ArithmeticException("a < b");
            return a - b;
        }
    
        public static @UnsignedInt int divide(@UnsignedInt int a, @UnsignedInt int b) {
            return Integer.divideUnsigned(a, b);
        }
        
        public static int compare(@UnsignedInt int a, @UnsignedInt int b) {
            return Integer.compareUnsigned(a, b);
        }
    }
    
    private static void test(String text, IntBinaryOperator f, @UnsignedInt int a, @UnsignedInt int b) {
        try {
            int r = f.applyAsInt(a, b);
            System.out.printf(text, a, b, r);
        } catch(ArithmeticException e) {
            System.out.printf(text, a, b, e.getMessage());
        }
    }
    
    private static void exercise01() {
        System.out.println("Result:");
        @UnsignedInt int a = 0xfffffffe;
        @UnsignedInt int b = 1; // 0x00000001
        @UnsignedInt int c = -1; // 0xffffffff
        @UnsignedInt int d = 2; // 0x00000002
        @UnsignedInt int e = 0; // 0x00000000
        test("0x%x + 0x%x = 0x%x\n", UnsignedMath::add, a, b); // ok
        test("0x%x + 0x%x = error: %s\n", UnsignedMath::add, a, d); // error
        test("0x%x - 0x%x = 0x%x\n", UnsignedMath::subtract, a, b); // ok
        test("0x%x - 0x%x = error: %s\n", UnsignedMath::subtract, a, c); // erorr
        test("0x%x / 0x%x = 0x%x\n", UnsignedMath::divide, a, b); // ok
        test("0x%x / 0x%x = 0x%x\n", UnsignedMath::divide, a, c); // ok
        test("0x%x / 0x%x = error: %s\n", UnsignedMath::divide, a, e); // erorr
        test("compare(0x%x, 0x%x) = %d\n", UnsignedMath::compare, a, a); // ok
        test("compare(0x%x, 0x%x) = %d\n", UnsignedMath::compare, a, b); // ok
        test("compare(0x%x, 0x%x) = %d\n", UnsignedMath::compare, a, c); // ok
        /*
         * Difference between signed and unsigned division:
         *     0xffffffff / 0x00000002 -> 0x00000000 // signed
         *     0xffffffff / 0x00000002 -> 0x7fffffff // unsigned
         * Difference between signed and unsigned reminder:
         *     0xffffffff % 0x00000002 -> 0xffffffff // signed
         *     0xffffffff % 0x00000002 -> 0x00000001 // unsigned
         */
    }

    /**
     * Exercise 2.
     * For which integer n does Math.negateExact(n) throw an exception? (Hint: There
     * is only one.)
     */
    private static void exercise02() {
        System.out.println("Result:");
        System.out.printf("n = 0x%x\n", Integer.MIN_VALUE);
        /*
         * Math.negateExact(Integer.MIN_VALUE) will throw and ArithmeticException.
         */
    }
    
    /**
     * Exercise 3.
     * Euclid’s algorithm (which is over two thousand years old) computes the
     * greatest common divisor of two numbers as gcd(a, b) = a if b is zero, and
     * gcd(b, rem(a, b)) otherwise, where rem is the remainder. Clearly, the gcd
     * should not be negative, even if a or b are (since its negation would then be a
     * greater divisor). Implement the algorithm with %, floorMod, and a rem function
     * that produces the mathematical (non-negative) remainder. Which of the three
     * gives you the least hassle with negative values?
     */
    private static int gcd(int a, int b, IntBinaryOperator rem) {
        return b == 0 ? (a < 0 ? Math.negateExact(a) : a) : gcd(b, rem.applyAsInt(a, b), rem);
    }
    
    private static int mathDiv(int a, int b) {
        if (b > 0) {
            int q = (int) (a / (double) b);
            return a == b * q || a >= 0 ? q : q - 1;
        } else return -mathDiv(a, Math.negateExact(b));
    }
    
    private static int mathRem(int a, int b) {
        return a - b * mathDiv(a, b);
    }
    
    private static void exercise03() {
        int a = -12;
        int b = -3;
        gcd(a, b, (a_, b_) -> a_ % b_); // 3
        gcd(a, b, Math::floorMod); // 3
        gcd(a, b, ExercisesChapter8::mathRem); // 3
        /*
         * Looks like all algorithms work fine...
         */
    }
    
    /**
     * Exercise 4.
     * The Math.nextDown(x) method returns the next smaller floating-point number
     * than x, just in case some random process hit x exactly, and we promised a
     * number < x. Can this really happen? Consider double r = 1 - generator.
     * nextDouble(), where generator is an instance of java.util.Random. Can it ever yield
     * 1? That is, can generator.nextDouble() ever yield 0? The documentation says it
     * can yield any value between 0 inclusive and 1 exclusive. But, given that there
     * are 2^53 such floating-point numbers, will you ever get a zero? Indeed, you
     * do. The random number generator computes the next seed as next(s) = s · m
     * + a % N, where m = 25214903917, a = 11, and N = 2^48. The inverse of m modulo
     * N is v = 246154705703781, and therefore you can compute the predecessor of
     * a seed as prev(s) = (s – a) · v % N. To make a double, two random numbers are
     * generated, and the top 26 and 27 bits are taken each time. When s is 0, next(s)
     * is 11, so that’s what we want to hit: two consecutive numbers whose top bits
     * are zero. Now, working backwards, let’s start with s = prev(prev(prev(0))).
     * Since the Random constructor sets s = (initialSeed ^ m) % N, offer it s =
     * prev(prev(prev(0))) ^ m = 164311266871034, and you’ll get a zero after two
     * calls to nextDouble. But that is still too obvious. Generate a million predecessors,
     * using a stream of course, and pick the minimum seed. Hint: You will get a
     * zero after 376050 calls to nextDouble.
     */
    private static long m = 25214903917L;
    private static long a = 11L;
    private static long N = 0x1000000000000L;
    private static long v = 246154705703781L;
    
    private static long prev(long s) {
        return BigInteger.valueOf(s)
                         .subtract(BigInteger.valueOf(a))
                         .multiply(BigInteger.valueOf(v))
                         .mod(BigInteger.valueOf(N))
                         .longValue();
    }
    
    private static void exercise04() {
        System.out.println("Result:");
        long seed = Stream.iterate(prev(0L), ExercisesChapter8::prev)
                          .limit(1_000_000)
                          .mapToLong(x -> x ^ m)
                          .min()
                          .getAsLong();
        Random generator = new Random(seed);
        double zeroDouble = Double.NaN;
        int counter = 0;
        do {
            counter++;
            zeroDouble = generator.nextDouble();
        } while (zeroDouble != 0D);
        System.out.printf("Zero double generated after %d calls\n", counter);
    }
    
    /**
     * Exercise 5.
     * At the beginning of Chapter 2, we counted long words in a list as
     * words.stream().filter(w -> w.length() > 12).count(). Do the same with a lambda
     * expression, but without using streams. Which operation is faster for a
     * long list?
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
    
    private static void exercise05() throws Exception {
        System.out.println("Result:");
        String contents = new String(Files.readAllBytes(Paths.get(System.getProperty("java.home"), "COPYRIGHT")), StandardCharsets.UTF_8);
        List<String> words = Arrays.asList(contents.split("[\\P{L}]+"));
        Supplier<?> sequentialStreamTask = () -> words.stream().filter(w -> w.length() > 12).count(); // sequential task
        Supplier<?> parallelStreamTask = () -> words.parallelStream().filter(w -> w.length() > 12).count(); // parallel task
        Supplier<?> listForEachTask = () -> { // list forEach task
            AtomicInteger counter = new AtomicInteger();
            words.forEach(w -> {
                if (w.length() > 12) counter.incrementAndGet();
            });
            return counter.get();
        };
        int size = 100_000;
        Thread.sleep(1000); // let gc do the stuff
        getStatistics("Estimated sequential stream time", sequentialStreamTask, size);
        Thread.sleep(1000); // let gc do the stuff
        getStatistics("Estimated parallel stream time", parallelStreamTask, size);
        Thread.sleep(1000); // let gc do the stuff
        getStatistics("Estimated list forEach time", listForEachTask, size);
        /* 
         * listForEachTask works much faster than any stream task!
         */
    }
    
    /**
     * Exercise 6.
     * Using only methods of the Comparator class, define a comparator for Point2D
     * which is a total ordering (that is, the comparator only returns zero for equal
     * objects). Hint: First compare the x-coordinates, then the y-coordinates. Do
     * the same for Rectangle2D.
     */
    private static void exercise06() {
        System.out.println("Result:");
        Point2D[] pts = new Point2D[] {
            new Point2D(0.0, 0.0),
            new Point2D(1.0, 0.0),
            new Point2D(0.0, 0.1),
            new Point2D(1.1, 1.1)
        };
        Rectangle2D[] rects = new Rectangle2D[] {
            new Rectangle2D(0.0, 0.1, 0.2, 0.3),
            new Rectangle2D(0.0, 0.1, 0.2, 0.4),
            new Rectangle2D(0.1, 0.2, 0.3, 0.4),
            new Rectangle2D(0.1, 0.2, 0.35, 0.4),
            new Rectangle2D(0.0, 0.15, 0.35, 0.4)
        };
        Comparator<Point2D> ptsTotalOrdering = Comparator.comparingDouble(Point2D::getX)
                                                         .thenComparing(Point2D::getY);
        Comparator<Rectangle2D> recTotalOrdering = Comparator.comparingDouble(Rectangle2D::getMinX)
                                                             .thenComparingDouble(Rectangle2D::getMinY)
                                                             .thenComparing(Rectangle2D::getWidth)
                                                             .thenComparing(Rectangle2D::getHeight);
        Arrays.sort(pts, ptsTotalOrdering);
        Arrays.sort(rects, recTotalOrdering);
        System.out.println("Sorted points:");
        Stream.of(pts).forEach(System.out::println);
        System.out.println("Sorted rectangles:");
        Stream.of(rects).forEach(System.out::println);
    }
    
    /**
     * Exercise 7.
     * Express nullsFirst(naturalOrder()).reversed() without calling reversed.
     */
    private static void exercise07() {
        System.out.println("Result:");
        Comparator<String> cmprt1 = Comparator.<String>nullsFirst(Comparator.naturalOrder()).reversed();
        Comparator<String> cmprt2 = Comparator.nullsLast(Comparator.reverseOrder());
        String[] source = new String[] { null, "one", "two", null, "Three", null, "four", "five", null, null };
        System.out.printf("Source: %s\n", Arrays.asList(source));
        Arrays.sort(source, cmprt1);
        System.out.printf("With calling reversed: %s\n", Arrays.asList(source));
        Arrays.sort(source, cmprt2);
        System.out.printf("Without calling reversed: %s\n", Arrays.asList(source));
    }
    
    /**
     * Exercise 8.
     * Write a program that demonstrates the benefits of the CheckedQueue class.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static <T> void getMoreWork(Collection source, T e) {
        source.add(e); // ok but unsafe
    }
    
    private static void exercise08() {
        Collection<String> source = Collections.checkedQueue(new LinkedList<String>(), String.class);
        source.add("1"); // ok
        // source.add(2); // compile error
        getMoreWork(source, "3"); // ok
        // getMoreWork(source, 4); // ClassCastException for checked source
    }
    
    /**
     * Exercise 9.
     * Write methods that turn a Scanner into a stream of words, lines, integers, or
     * double values. Hint: Look at the source code for BufferedReader.lines.
     */
    private static Stream<String> getWords(Scanner scanner) {
        scanner.useDelimiter("[\\P{L}]+");
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                scanner, Spliterator.ORDERED | Spliterator.NONNULL), false);
    }
    
    private static Stream<String> getLines(Scanner scanner) {
        Iterator<String> iter = new Iterator<String>() {
            @Override
            public boolean hasNext() {
                return scanner.hasNextLine();
            }

            @Override
            public String next() {
                return scanner.nextLine();
            }
        };
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                iter, Spliterator.ORDERED | Spliterator.NONNULL), false);
    }
    
    private static IntStream getInts(Scanner scanner) {
        scanner.useDelimiter("[\\s.,;\n\t]+");
        Pattern pattern = Pattern.compile("\\d+");
        PrimitiveIterator.OfInt iter = new PrimitiveIterator.OfInt() {            
            @Override
            public boolean hasNext() {
                while (scanner.hasNext() && !scanner.hasNext(pattern)) scanner.next();
                return scanner.hasNext();
            }
            
            @Override
            public int nextInt() {
                return Integer.parseInt(scanner.next(pattern));
            }
        };
        return StreamSupport.intStream(Spliterators.spliteratorUnknownSize(
                iter, Spliterator.ORDERED | Spliterator.NONNULL), false);
    }
    
    private static DoubleStream getDoubles(Scanner scanner) {
        scanner.useDelimiter("[\\s,;\n\t]+");
        Pattern pattern = Pattern.compile("[-+]?[0-9]*\\.?[0-9]*([eE][-+]?[0-9]+)?");
        PrimitiveIterator.OfDouble iter = new PrimitiveIterator.OfDouble() {            
            @Override
            public boolean hasNext() {
                while (scanner.hasNext() && !scanner.hasNext(pattern)) scanner.next();
                return scanner.hasNext();
            }
            
            @Override
            public double nextDouble() {
                return Double.parseDouble(scanner.next(pattern));
            }
        };
        return StreamSupport.doubleStream(Spliterators.spliteratorUnknownSize(
                iter, Spliterator.ORDERED | Spliterator.NONNULL), false);
    }
    
    private static void exercise09() {
        System.out.println("Result:");
        String source = "One, two\n3. Four, 5\nsix 7.0 NINE 10";
        Supplier<Scanner> newScanner = () -> new Scanner(new ByteArrayInputStream(source.getBytes()), "UTF-8");
        System.out.println("Lines:");
        getLines(newScanner.get()).forEach(System.out::println);
        System.out.println("Words:");
        getWords(newScanner.get()).forEach(System.out::println);
        System.out.println("Ints:");
        getInts(newScanner.get()).forEach(System.out::println);
        System.out.println("Doubles:");
        getDoubles(newScanner.get()).forEach(System.out::println);
    }
    
    /**
     * Exercise 10.
     * Unzip the src.zip file from the JDK. Using Files.walk, find all Java files that
     * contain the keywords transient and volatile.
     */
    private static void exercise10() throws Exception {
        System.out.println("Result:");
        Path srcPath = Paths.get(System.getProperty("java.home")).getParent().resolve("src");
        PathMatcher srcMatcher = FileSystems.getDefault().getPathMatcher("glob:**.java");
        try (Stream<Path> walker = Files.walk(srcPath)) {
            walker.filter(path -> {
                if (!Files.isRegularFile(path) || !srcMatcher.matches(path)) return false;
                try (Stream<String> lines = Files.lines(path)) {
                    return lines.anyMatch(line -> line.contains("transient") && line.contains("volatile"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).forEach(System.out::println);
        }
    }
    
    /**
     * Exercise 11.
     * Write a program that gets the contents of a password-protected web page.
     * Call URLConnection connection = url.openConnection();. Form the string username:
     * password and encode it in Base64. Then call connection.setRequestProperty(
     * "Authorization", "Basic " + encoded string), followed by connection.connect() and
     * connection.getInputStream().
     */
    private static int tryConnect(String url, String auth) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
        connection.connect();
        int responseCode = connection.getResponseCode();
        connection.disconnect();
        return responseCode;
    }
    
    private static void exercise11() throws Exception {
        System.out.println("Result:");
        String url = "https://www.httpwatch.com/httpgallery/authentication/authenticatedimage/default.aspx";
        System.out.printf("Response code with correct auth: %d\n", tryConnect(url, "httpwatch:123"));
        System.out.printf("Response code with incorrect auth: %d\n", tryConnect(url, "123:123"));
    }
    
    /**
     * Exercise 12.
     * Implement the TestCase annotation and a program that loads a class with such
     * annotations and invokes the annotated methods, checking whether they yield
     * the expected values. Assume that parameters and return values are integers.
     */
    @Repeatable(TestCases.class)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface TestCase {
        int params();
        long expected();
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface TestCases {
        TestCase[] value();
    }
    
    @TestCase(params=4, expected=24L)
    @TestCase(params=0, expected=1L)
    public static long factorial(int i) {
        if (i < 0) throw new ArithmeticException("Argument must be non-negative");
        if (i == 0) return 0L; // bug
        return i < 2 ? 1L : i * factorial(i - 1);
    }
    
    private static void exercise12() throws Exception {
        System.out.println("Result:");
        Method factorialMethod = ExercisesChapter8.class.getDeclaredMethod("factorial", int.class);
        for (TestCase testCase : factorialMethod.getAnnotationsByType(TestCase.class)) {
            int params = testCase.params();
            long result = factorial(params);
            long expected = testCase.expected();
            if (result != expected)
                System.out.printf("factorial(%d) = %d, but expected %d\n", params, result, expected);
        }
    }
    
    /**
     * Exercise 13.
     * Repeat the preceding exercise, but build a source-level annotation processor
     * emitting a program that, when executed, runs the tests in its main method.
     * (See Horstmann and Cornell, Core Java, 9th Edition, Volume 2, Section 10.6 for
     * an introduction into processing source-level annotations.)
     */
    private static void exercise13() {
        // TODO
    }
    
    /**
     * Exercise 14.
     * Demonstrate the use of the Objects.requireNonNull method and show how it
     * leads to more useful error messages.
     */
    private static void exercise14() {
        String nullableString = Math.random() > 0.5D ? "" : null;
        try {
            String nonNullString = Objects.requireNonNull(nullableString, () -> "String in null at " + LocalDateTime.now());
            nonNullString.length();
        } catch (NullPointerException e) {
            // do something
        }
    }
    
    /**
     * Exercise 15.
     * Using Files.lines and Pattern.asPredicate, write a program that acts like the grep
     * utility, printing all lines that contain a match for a regular expression.
     */
    private static void grep(Path file, Charset charset, String pattern) throws Exception {
        Files.lines(file, charset)
             .filter(Pattern.compile(pattern).asPredicate())
             .forEach(System.out::println);
    }
    
    private static void grep(Path file, String pattern) throws Exception {
        grep(file, StandardCharsets.UTF_8, pattern);
    }
    
    private static void exercise15() throws Exception {
        System.out.println("Result:");
        grep(Paths.get(System.getProperty("java.home"), "THIRDPARTYLICENSEREADME.txt"),
             "^%%.*"); // all lines starting with %%
    }
    
    /**
     * Exercise 16.
     * Use a regular expression with named capturing groups to parse a line
     * containing a city, state, and zip code. Accept both 5- and 9-digit zip codes.
     */
    private static String[] parseAddress(String source) throws Exception {
        Pattern p = Pattern.compile("(?<city>[\\p{L} ]+),\\s*(?<state>[A-Z]{2})\\s*(?<zip>\\d{5}(-\\d{4})?)");
        Matcher m = p.matcher(source);
        if (!m.matches()) throw new ParseException("Incorrect address", 0);
        String[] result = new String[3];
        result[0] = m.group("city");
        result[1] = m.group("state");
        result[2] = m.group("zip");
        return result;
    }
    
    private static void exercise16() throws Exception {
        System.out.println("Result:");
        System.out.printf("Parsed addres: %s\n", Arrays.toString(parseAddress("South Park,  CO      81154")));
        System.out.printf("Parsed addres: %s\n", Arrays.toString(parseAddress("Springfield, WA 12345-6789")));
    }

    public static void main(String[] args) throws Exception {
        System.out.println("*** Exercise 1 ***\nTask: " +
            "Write a program that adds, subtracts, divides, and compares numbers\r\n" +
            "between 0 and 2^32 – 1, using int values and unsigned operations. Show why\r\n" + 
            "divideUnsigned and remainderUnsigned are necessary.");
        exercise01();
        System.out.println("\n*** Exercise 2 ***\nTask: " +
            "For which integer n does Math.negateExact(n) throw an exception? (Hint: There\r\n" + 
            "is only one.)");
        exercise02();
        System.out.println("\n*** Exercise 3 ***\nTask: " +
            "Euclid’s algorithm (which is over two thousand years old) computes the\r\n" + 
            "greatest common divisor of two numbers as gcd(a, b) = a if b is zero, and\r\n" + 
            "gcd(b, rem(a, b)) otherwise, where rem is the remainder. Clearly, the gcd\r\n" + 
            "should not be negative, even if a or b are (since its negation would then be a\r\n" + 
            "greater divisor). Implement the algorithm with %, floorMod, and a rem function\r\n" + 
            "that produces the mathematical (non-negative) remainder. Which of the three\r\n" + 
            "gives you the least hassle with negative values?");
        exercise03();
        System.out.println("\n*** Exercise 4 ***\nTask: " +
            "The Math.nextDown(x) method returns the next smaller floating-point number\r\n" + 
            "than x, just in case some random process hit x exactly, and we promised a\r\n" + 
            "number < x. Can this really happen? Consider double r = 1 - generator.\r\n" + 
            "nextDouble(), where generator is an instance of java.util.Random. Can it ever yield\r\n" + 
            "1? That is, can generator.nextDouble() ever yield 0? The documentation says it\r\n" + 
            "can yield any value between 0 inclusive and 1 exclusive. But, given that there\r\n" + 
            "are 2^53 such floating-point numbers, will you ever get a zero? Indeed, you\r\n" +
            "do. The random number generator computes the next seed as next(s) = s · m\r\n" + 
            "+ a % N, where m = 25214903917, a = 11, and N = 2^48. The inverse of m modulo\r\n" + 
            "N is v = 246154705703781, and therefore you can compute the predecessor of\r\n" + 
            "a seed as prev(s) = (s – a) · v % N. To make a double, two random numbers are\r\n" + 
            "generated, and the top 26 and 27 bits are taken each time. When s is 0, next(s)\r\n" + 
            "is 11, so that’s what we want to hit: two consecutive numbers whose top bits\r\n" + 
            "are zero. Now, working backwards, let’s start with s = prev(prev(prev(0))).\r\n" + 
            "Since the Random constructor sets s = (initialSeed ^ m) % N, offer it s =\r\n" + 
            "prev(prev(prev(0))) ^ m = 164311266871034, and you’ll get a zero after two\r\n" + 
            "calls to nextDouble. But that is still too obvious. Generate a million predecessors,\r\n" + 
            "using a stream of course, and pick the minimum seed. Hint: You will get a\r\n" + 
            "zero after 376050 calls to nextDouble.");
        exercise04();
        System.out.println("\n*** Exercise 5 ***\nTask: " +
            "At the beginning of Chapter 2, we counted long words in a list as\r\n" + 
            "words.stream().filter(w -> w.length() > 12).count(). Do the same with a lambda\r\n" + 
            "expression, but without using streams. Which operation is faster for a\r\n" + 
            "long list?");
        exercise05();
        System.out.println("\n*** Exercise 6 ***\nTask: " +
            "Using only methods of the Comparator class, define a comparator for Point2D\r\n" + 
            "which is a total ordering (that is, the comparator only returns zero for equal\r\n" + 
            "objects). Hint: First compare the x-coordinates, then the y-coordinates. Do\r\n" + 
            "the same for Rectangle2D.");
        exercise06();
        System.out.println("\n*** Exercise 7 ***\nTask: " +
            "Express nullsFirst(naturalOrder()).reversed() without calling reversed.");
        exercise07();
        System.out.println("\n*** Exercise 8 ***\nTask: " +
            "Write a program that demonstrates the benefits of the CheckedQueue class.");
        exercise08();
        System.out.println("\n*** Exercise 9 ***\nTask: " +
            "Write methods that turn a Scanner into a stream of words, lines, integers, or\r\n" + 
            "double values. Hint: Look at the source code for BufferedReader.lines.");
        exercise09();
        System.out.println("\n*** Exercise 10 ***\nTask: " +
            "Unzip the src.zip file from the JDK. Using Files.walk, find all Java files that\r\n" + 
            "contain the keywords transient and volatile.");
        exercise10();
        System.out.println("\n*** Exercise 11 ***\nTask: " +
            "Write a program that gets the contents of a password-protected web page.\r\n" + 
            "Call URLConnection connection = url.openConnection();. Form the string username:\r\n" + 
            "password and encode it in Base64. Then call connection.setRequestProperty(\r\n" + 
            "\"Authorization\", \"Basic \" + encoded string), followed by connection.connect() and\r\n" + 
            "connection.getInputStream().");
        exercise11();
        System.out.println("\n*** Exercise 12 ***\nTask: " +
            "Implement the TestCase annotation and a program that loads a class with such\r\n" + 
            "annotations and invokes the annotated methods, checking whether they yield\r\n" + 
            "the expected values. Assume that parameters and return values are integers.");
        exercise12();
        System.out.println("\n*** Exercise 13 ***\nTask: " +
            "Repeat the preceding exercise, but build a source-level annotation processor\r\n" + 
            "emitting a program that, when executed, runs the tests in its main method.\r\n" + 
            "(See Horstmann and Cornell, Core Java, 9th Edition, Volume 2, Section 10.6 for\r\n" + 
            "an introduction into processing source-level annotations.)");
        exercise13();
        System.out.println("\n*** Exercise 14 ***\nTask: " +
            "Demonstrate the use of the Objects.requireNonNull method and show how it\r\n" + 
            "leads to more useful error messages.");
        exercise14();
        System.out.println("\n*** Exercise 15 ***\nTask: " +
            "Using Files.lines and Pattern.asPredicate, write a program that acts like the grep\r\n" + 
            "utility, printing all lines that contain a match for a regular expression.");
        exercise15();
        System.out.println("\n*** Exercise 16 ***\nTask: " +
            "Use a regular expression with named capturing groups to parse a line\r\n" + 
            "containing a city, state, and zip code. Accept both 5- and 9-digit zip codes.");
        exercise16();
    }
}
