public class ExercisesChapter8 {
    /**
     * Exercise 1.
     * Write a program that adds, subtracts, divides, and compares numbers
     * between 0 and 2^32 – 1, using int values and unsigned operations. Show why
     * divideUnsigned and remainderUnsigned are necessary.
     */
    private static void exercise01() {
        System.out.println("Result:");
        
    }

    /**
     * Exercise 2.
     * For which integer n does Math.negateExact(n) throw an exception? (Hint: There
     * is only one.)
     */
    private static void exercise02() {
        System.out.println("Result:");
        
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
    private static void exercise03() {
        System.out.println("Result:");
        
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
    private static void exercise04() {
        System.out.println("Result:");
        
    }
    
    /**
     * Exercise 5.
     * At the beginning of Chapter 2, we counted long words in a list as
     * words.stream().filter(w -> w.length() > 12).count(). Do the same with a lambda
     * expression, but without using streams. Which operation is faster for a
     * long list?
     */
    private static void exercise05() {
        System.out.println("Result:");
        
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
        
    }
    
    /**
     * Exercise 7.
     * Express nullsFirst(naturalOrder()).reversed() without calling reversed.
     */
    private static void exercise07() {
        System.out.println("Result:");
        
    }
    
    /**
     * Exercise 8.
     * Write a program that demonstrates the benefits of the CheckedQueue class.
     */
    private static void exercise08() {
        System.out.println("Result:");
        
    }
    
    /**
     * Exercise 9.
     * Write methods that turn a Scanner into a stream of words, lines, integers, or
     * double values. Hint: Look at the source code for BufferedReader.lines.
     */
    private static void exercise09() {
        System.out.println("Result:");
        
    }
    
    /**
     * Exercise 10.
     * Unzip the src.zip file from the JDK. Using Files.walk, find all Java files that
     * contain the keywords transient and volatile.
     */
    private static void exercise10() {
        System.out.println("Result:");
        
    }
    
    /**
     * Exercise 11.
     * Write a program that gets the contents of a password-protected web page.
     * Call URLConnection connection = url.openConnection();. Form the string username:
     * password and encode it in Base64. Then call connection.setRequestProperty(
     * "Authorization", "Basic " + encoded string), followed by connection.connect() and
     * connection.getInputStream().
     */
    private static void exercise11() {
        System.out.println("Result:");
        
    }
    
    /**
     * Exercise 12.
     * Implement the TestCase annotation and a program that loads a class with such
     * annotations and invokes the annotated methods, checking whether they yield
     * the expected values. Assume that parameters and return values are integers.
     */
    private static void exercise12() {
        System.out.println("Result:");
        
    }
    
    /**
     * Exercise 13.
     * Repeat the preceding exercise, but build a source-level annotation processor
     * emitting a program that, when executed, runs the tests in its main method.
     * (See Horstmann and Cornell, Core Java, 9th Edition, Volume 2, Section 10.6 for
     * an introduction into processing source-level annotations.)
     */
    private static void exercise13() {
        System.out.println("Result:");
        
    }
    
    /**
     * Exercise 14.
     * Demonstrate the use of the Objects.requireNonNull method and show how it
     * leads to more useful error messages.
     */
    private static void exercise14() {
        System.out.println("Result:");
        
    }
    
    /**
     * Exercise 15.
     * Using Files.lines and Pattern.asPredicate, write a program that acts like the grep
     * utility, printing all lines that contain a match for a regular expression.
     */
    private static void exercise15() {
        System.out.println("Result:");
        
    }
    
    /**
     * Exercise 16.
     * Use a regular expression with named capturing groups to parse a line
     * containing a city, state, and zip code. Accept both 5- and 9-digit zip codes.
     */
    private static void exercise16() {
        System.out.println("Result:");
        
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
