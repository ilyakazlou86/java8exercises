import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ExercisesChapter1 {
    private static String answer;

    /**
     * Exercise 1.
     * Is the comparator code in the Arrays.sort method called in the same thread as
     * the call to sort or a different thread?
     */
    private static <T> void exercise1(T[] arrayToSort) {
        /* Yes it is. Let's proof it! */
        Thread executorThread = Thread.currentThread();
        Comparator<? super T> cmprt = (first, second) -> {
            if (answer == null) {
                answer = Thread.currentThread() == executorThread ? "Yes, threads are the same." : "No, threads are different";
                System.out.printf("Answer: %s\n", answer);
            }
            return 0; // stub just for instance!
        };
        Arrays.sort(arrayToSort, cmprt);
    }
    
    /**
     * Exercise 2.
     * Using the listFiles(FileFilter) and isDirectory methods of the java.io.File class,
     * write a method that returns all subdirectories of a given directory. Use a
     * lambda expression instead of a FileFilter object. Repeat with a method
     * expression.
     */
    private static void exercise2(File root) {
        System.out.printf("Result:\nRoot dir: %s\n", root);
        /* Using lambda expression first */
        File[] subDirs = root.listFiles(file -> file.isDirectory());
        /* Using method expression instead */
        subDirs = root.listFiles(File::isDirectory);
        System.out.printf("Sub dirs: %s\n", Arrays.asList(subDirs));
    }
    
    /**
     * Exercise 3.
     * Using the list(FilenameFilter) method of the java.io.File class, write a method
     * that returns all files in a given directory with a given extension. Use a lambda
     * expression, not a FilenameFilter. Which variables from the enclosing scope does
     * it capture?
     */
    private static void exercise3(File root, String extension) {
        System.out.printf("Result:\nRoot dir: %s\n", root);
        final Object obj1 = new Object(); // final variable
        Object obj2 = new Object(); // effectively final variable;
        Object obj3 = new Object(); // non final variable;
        String[] filteredFiles = root.list((dir, name) -> {
            /*
             * This lambda expression captures class fields and all final or effectively final local variables.
             * So we can do something like:
             * answer1 = null;
             * root.toString();
             * extension.length();
             * obj1.hashCode();
             * obj2.getClass(); 
             * 
             * But the following code is incorrect:
             * obj3.hashCode(); // must be final or effectively final! 
             */
            return name.toLowerCase().endsWith(extension);   
        });
        obj3 = null;
        System.out.printf("Files with '%s' extension: %s\n", extension, Arrays.asList(filteredFiles));
    }
    
    /**
     * Exercise 4.
     * Given an array of File objects, sort it so that the directories come before the
     * files, and within each group, elements are sorted by path name. Use a lambda
     * expression, not a Comparator.
     */
    private static void exercise4(File[] srcArray) {
        System.out.printf("Result:\nSource files: %s\n", Arrays.asList(srcArray));
        Arrays.sort(srcArray, (first, second) -> {
            if(first.equals(second)) {
                return 0;
            }
            boolean firstIsDirectory = first.isDirectory();
            boolean secondIsDirectory = second.isDirectory();
            if(firstIsDirectory ^ secondIsDirectory) {
                return firstIsDirectory ? -1 : 1;
            }
            return first.getAbsolutePath().compareTo(second.getAbsolutePath());
        });
        System.out.printf("Sorted files: %s\n", Arrays.asList(srcArray));
    }
    
    /**
     * Exercise 5.
     * Take a file from one of your projects that contains a number of ActionListener,
     * Runnable, or the like. Replace them with lambda expressions. How many lines
     * did it save? Was the code easier to read? Were you able to use method
     * references?
     */
    private static void exercise5() {
        System.out.println(
                "Result:\n" +
                "    JButton btn = new JButton(\"Ok\");\r\n" + 
                "    /* Legacy code */\r\n" + 
                "    btn.addActionListener(new ActionListener() {\r\n" + 
                "        @Override\r\n" + 
                "        public void actionPerformed(ActionEvent e) {\r\n" + 
                "            /* do something here */\r\n" + 
                "        }\r\n" + 
                "    });\r\n" + 
                "    /* Modern code */\r\n" + 
                "    btn.addActionListener(e -> {\r\n" + 
                "        /* do something here */\r\n" + 
                "    }); // saves 3 lines\r\n" + 
                "    \r\n" + 
                "    /* Legacy code */\r\n" + 
                "    new Thread(new Runnable() {\r\n" + 
                "        @Override\r\n" + 
                "        public void run() {\r\n" + 
                "            /* do something here */\r\n" + 
                "        }\r\n" + 
                "    }).start();\r\n" + 
                "    /* Modern code */\r\n" + 
                "    new Thread(() -> {\r\n" + 
                "        /* do something here */\r\n" + 
                "    }).start(); // saves 3 lines\r\n" + 
                "    \r\n" + 
                "    List<?> list = Arrays.asList(\"Hello\", \"world\");\r\n" + 
                "    /* Legacy code */\r\n" + 
                "    for(Object str : list) {\r\n" + 
                "        System.out.println(str);\r\n" + 
                "    }\r\n" + 
                "    /* Modern code */\r\n" + 
                "    list.stream().forEach(System.out::println); // saves 2 lines");
    }
    
    /**
     * Exercise 6.
     * Didn’t you always hate it that you had to deal with checked exceptions in a
     * Runnable? Write a method uncheck that catches all checked exceptions and turns
     * them into unchecked exceptions. For example,
     *     new Thread(uncheck(
     *         () -> { System.out.println("Zzz"); Thread.sleep(1000); })).start();
     *             // Look, no catch (InterruptedException)!
     * Hint: Define an interface RunnableEx whose run method may throw any exceptions.
     * Then implement public static Runnable uncheck(RunnableEx runner). Use a
     * lambda expression inside the uncheck function.
     * Why can’t you just use Callable<Void> instead of RunnableEx?
     */
    private interface RunnableEx {
        void run() throws Exception;
    }
    
    private static Runnable uncheck(RunnableEx runner) {
        return () -> {
            try {
                runner.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
    
    private static Runnable uncheck2(Callable<Void> runner) {
        return () -> {
            try {
                runner.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
    
    private static void exercise6() throws InterruptedException {
        System.out.println("Result:");
        Thread t = new Thread(uncheck(
            () -> { System.out.println("Zzz"); Thread.sleep(1000); }
        ));
        t.start(); // Look, no catch (InterruptedException)!
        t.join();
        /* We can use Callable<Void> but we have to return null */
        t = new Thread(uncheck2(
            () -> { System.out.println("Zzz"); Thread.sleep(1000); return null; }
        ));
        t.start();
        t.join();
    }

    /**
     * Exercise 7.
     * Write a static method andThen that takes as parameters two Runnable instances
     * and returns a Runnable that runs the first, then the second. In the main method,
     * pass two lambda expressions into a call to andThen, and run the returned
     * instance.
     */
    private static Runnable andThen(Runnable first, Runnable second) {
        return () -> { first.run(); second.run(); };
    }
    
    private static void exercise7() throws InterruptedException {
        System.out.println("Result:");
        Thread t = new Thread(andThen(
            () -> System.out.print("Hello "),
            () -> System.out.println("world!")
        ));
        t.start();
        t.join();
    }
    
    /**
     * Exercise 8.
     * What happens when a lambda expression captures values in an enhanced
     * for loop such as this one?
     *      String[] names = { "Peter", "Paul", "Mary" };
     *      List<Runnable> runners = new ArrayList<>();
     *      for (String name : names)
     *          runners.add(() -> System.out.println(name));
     * Is it legal? Does each lambda expression capture a different value, or do they
     * all get the last value? What happens if you use a traditional loop for (int i = 0;
     * i < names.length; i++)?
     * @throws InterruptedException 
     */
    private static void exercise8() throws InterruptedException {
        System.out.println("Result:");
        String[] names = { "Peter", "Paul", "Mary" };
        List<Runnable> runners = new ArrayList<>();
        for (String name : names)
            runners.add(() -> System.out.println(name));
        /* Let's check it out what happens... */
        ExecutorService exec = Executors.newFixedThreadPool(2);
        runners.forEach(exec::execute);
        exec.shutdown();
        exec.awaitTermination(10, TimeUnit.SECONDS);
        /* 
         * Looks like everything is OK and each lambda expression captures a different value
         * But we can't use traditional loop like that:
         *     for (int i = 0; i < names.length; i++) {
         *         runners.add(() -> System.out.println(names[i])); // i is not final or effectively final
         *     }
         * 
         * So correct code looks like that:
         *     for (int i = 0; i < names.length; i++) {
         *         String name = names[i];
         *         runners.add(() -> System.out.println(name));
         *     }
         *     
         * Or like that dirty hack:
         *     for (int[] i = new int[] {0}; i[0] < names.length; i[0]++) {
         *         runners.add(() -> System.out.println(names[i[0]]));
         *     }    
         */
    }
    
    /**
     * Exercise 9.
     * Form a subclass Collection2 from Collection and add a default method void
     * forEachIf(Consumer<T> action, Predicate<T> filter) that applies action to each
     * element for which filter returns true. How could you use it?
     */
    private interface Collection2<T> extends Collection<T> {
        default void forEachIf(Consumer<T> action, Predicate<T> filter) {
            stream().filter(filter).forEach(action);
        }
    }
    
    private static class List2<T> extends ArrayList<T> implements Collection2<T> {
        public List2(Collection<T> list) {
            super(list);
        }
    }
    
    private static void exercise9() {
        System.out.println("Result:");
        Collection2<Integer> list = new List2<>(Arrays.asList(-10, 0, Integer.MAX_VALUE, 5, -3, 2, 9, -100, 0, 10));
        System.out.printf("List of various integers: %s\n", list);
        System.out.println("Digits:");
        list.forEachIf(System.out::println, num -> num >=0 && num < 10);
    }
    
    /**
     * Exercise 10.
     * Go through the methods of the Collections class. If you were king for a day,
     * into which interface would you place each method? Would it be a default
     * method or a static method?
     */
    private static void exercise10() {
        /* 
         * This question is really hard for those who is not an excellent library designer (like me).
         * But I can propose to put all methods which names contain word List, Set, Map etc.
         * to corresponding interface. Logical methods like min, max, fill i'll put into Collection.
         * For instance:
         *     Collections::checkedList     goes to List::checkedList       as a static method
         *     Collections::emptyMap        goes to Map::emptyMap           as a static method
         *     Collections::synchronizedSet goes to Set::synchronizedSet    as a static method
         *     Collections::disjoint        goes to Collection::disjoint    as a default method
         *     Collections::fill            goes to Collection::fill        as a default method
         *     Collections::min/max         goes to Collection::min/max     as default methods
         */
    }
    
    /**
     * Exercise 11.
     * Suppose you have a class that implements two interfaces I and J, each of
     * which has a method void f(). Exactly what happens if f is an abstract, default,
     * or static method of I and an abstract, default, or static method of J? Repeat
     * where a class extends a superclass S and implements an interface I, each
     * of which has a method void f().
     */
    private interface AbstractMethodI {
        void f();
    }
    
    private interface DefaultMethodI {
        default void f() { System.out.println("Calling DefaultMethodI::f"); }
    }
    
    private interface StaticMethodI {
        static void f() { System.out.println("Calling StaticMethodI::f"); }
    }
    
    private interface AbstractMethodJ {
        void f();
    }
    
    private interface DefaultMethodJ {
        default void f() { System.out.println("Calling DefaultMethodJ::f"); }
    }
    
    private interface StaticMethodJ {
        static void f() { System.out.println("Calling StaticMethodJ::f"); }
    }
    
    private static class C1 implements AbstractMethodI, AbstractMethodJ {
        public void f() { System.out.println("Calling C1::f"); } // we have to implement abstract method for both interfaces
    }
    
    private static class C2 implements AbstractMethodI, DefaultMethodJ {
        public void f() { // we have to implement abstract method for interface I
            System.out.println("Calling C2::f");
            DefaultMethodJ.super.f(); // we can also call default method of interface J
        }
    }
    
    private static class C3 implements AbstractMethodI, StaticMethodJ {
        public void f() { // we have to implement abstract method for interface I
            System.out.println("Calling C3::f");
            StaticMethodJ.f(); // we can also call static method of interface J
        }
    }
    
    private static class C4 implements DefaultMethodI, DefaultMethodJ {
        public void f() { // we have to resolve collision by implementing method explicitly
            System.out.println("Calling C4::f");
            DefaultMethodI.super.f(); // we can also call default method of interface I
            DefaultMethodJ.super.f(); // we can also call default method of interface J
        }
    }
    
    private static class C5 implements DefaultMethodI, StaticMethodJ {
        // we can leave default implementation by interface I
        // or reimplement method
        // or call static method of interface J
    }
    
    private static class C6 implements StaticMethodI, StaticMethodJ {
        // there is no abstract methods to implement so we can only call static methods of both interfaces
    }
    
    private static class S {
        public void f() { System.out.println("Calling S::f"); }
    }
    
    private static class C7 extends S implements AbstractMethodI {
        // we can use implementation by class S
        // or reimplement method
    }
    
    private static class C8 extends S implements DefaultMethodI {
        // implementation by class S wins
        // we can also reimplement method
        // or call default method of interface I
    }
    
    private static class C9 extends S implements StaticMethodI {
        // there is no abstract methods to implement so we can use implementation by class S
        // or reimplement method
        // or call static method of interface I
    }
    
    private static void exercise11() {
        System.out.print("Result:\nnew C1().f() -> ");
        new C1().f();
        System.out.print("new C2().f() -> ");
        new C2().f();
        System.out.print("new C3().f() -> ");
        new C3().f();
        System.out.print("new C4().f() -> ");
        new C4().f();
        System.out.print("new C5().f() -> ");
        new C5().f();
        System.out.print("new C6().f() -> not implemented\n");
        System.out.print("new C7().f() -> ");
        new C7().f();
        System.out.print("new C8().f() -> ");
        new C8().f();
        System.out.print("new C9().f() -> ");
        new C9().f();
    }
    
    /**
     * Exercise 12.
     * In the past, you were told that it’s bad form to add methods to an interface
     * because it would break existing code. Now you are told that it’s okay to add
     * new methods, provided you also supply a default implementation. How safe
     * is that? Describe a scenario where the new stream method of the Collection
     * interface causes legacy code to fail compilation. What about binary
     * compatibility? Will legacy code from a JAR file still run?
     */
    private static void exercise12() {
        // I have no idea how to do that :(
    }

    public static void main(String[] args) throws Exception {
        System.out.println("*** Exercise 1 ***\nQuestion: Is the comparator code in the Arrays.sort method called in the same thread as\r\n" + 
                "the call to sort or a different thread?");
        exercise1(new Integer[] { 1, 2, 3 });
        System.out.println("\n*** Exercise 2 ***\nTask: Using the listFiles(FileFilter) and isDirectory methods of the java.io.File class,\r\n" + 
                "write a method that returns all subdirectories of a given directory. Use a\r\n" + 
                "lambda expression instead of a FileFilter object. Repeat with a method\r\n" + 
                "expression.");
        exercise2(new File(System.getProperty("java.home")));
        System.out.println("\n*** Exercise 3 ***\nTask: Using the list(FilenameFilter) method of the java.io.File class, write a method\r\n" + 
                "that returns all files in a given directory with a given extension. Use a lambda\r\n" + 
                "expression, not a FilenameFilter. Which variables from the enclosing scope does\r\n" + 
                "it capture?");
        exercise3(new File(System.getProperty("java.home")), ".txt");
        System.out.println("\n*** Exercise 4 ***\nTask: Given an array of File objects, sort it so that the directories come before the\r\n" + 
                "files, and within each group, elements are sorted by path name. Use a lambda\r\n" + 
                "expression, not a Comparator.");
        exercise4(new File(System.getProperty("java.home")).listFiles());
        System.out.println("\n*** Exercise 5 ***\nTask: Take a file from one of your projects that contains a number of ActionListener,\r\n" + 
                "Runnable, or the like. Replace them with lambda expressions. How many lines\r\n" + 
                "did it save? Was the code easier to read? Were you able to use method\r\n" + 
                "references?");
        exercise5();
        System.out.println("\n*** Exercise 6 ***\nTask: Didn’t you always hate it that you had to deal with checked exceptions in a\r\n" + 
                "Runnable? Write a method uncheck that catches all checked exceptions and turns\r\n" + 
                "them into unchecked exceptions. For example,\r\n" + 
                "    new Thread(uncheck(\r\n" + 
                "        () -> { System.out.println(\"Zzz\"); Thread.sleep(1000); })).start();\r\n" + 
                "            // Look, no catch (InterruptedException)!\r\n" + 
                "Hint: Define an interface RunnableEx whose run method may throw any exceptions.\r\n" +
                "Then implement public static Runnable uncheck(RunnableEx runner). Use a\r\n" + 
                "lambda expression inside the uncheck function.\r\n" + 
                "Why can’t you just use Callable<Void> instead of RunnableEx?");
        exercise6();
        System.out.println("\n*** Exercise 7 ***\nTask: Write a static method andThen that takes as parameters two Runnable instances\r\n" + 
                "and returns a Runnable that runs the first, then the second. In the main method,\r\n" + 
                "pass two lambda expressions into a call to andThen, and run the returned\r\n" + 
                "instance.");
        exercise7();
        System.out.println("\n*** Exercise 8 ***\nTask: What happens when a lambda expression captures values in an enhanced\r\n" + 
                "for loop such as this one?\r\n" + 
                "    String[] names = { \"Peter\", \"Paul\", \"Mary\" };\r\n" + 
                "    List<Runnable> runners = new ArrayList<>();\r\n" + 
                "    for (String name : names)\r\n" + 
                "        runners.add(() -> System.out.println(name));\r\n" +
                "Is it legal? Does each lambda expression capture a different value, or do they\r\n" + 
                "all get the last value? What happens if you use a traditional loop for (int i = 0;\r\n" + 
                "i < names.length; i++)?");
        exercise8();
        System.out.println("\n*** Exercise 9 ***\nTask: Form a subclass Collection2 from Collection and add a default method void\r\n" + 
                "forEachIf(Consumer<T> action, Predicate<T> filter) that applies action to each\r\n" + 
                "element for which filter returns true. How could you use it?");
        exercise9();
        System.out.println("\n*** Exercise 10 ***\nTask: Go through the methods of the Collections class. If you were king for a day,\r\n" + 
                "into which interface would you place each method? Would it be a default\r\n" + 
                "method or a static method?");
        exercise10();
        System.out.println("\n*** Exercise 11 ***\nTask: Suppose you have a class that implements two interfaces I and J, each of\r\n" + 
                "which has a method void f(). Exactly what happens if f is an abstract, default,\r\n" + 
                "or static method of I and an abstract, default, or static method of J? Repeat\r\n" + 
                "where a class extends a superclass S and implements an interface I, each\r\n" + 
                "of which has a method void f().");
        exercise11();
        System.out.println("\n*** Exercise 12 ***\nTask:  In the past, you were told that it’s bad form to add methods to an interface\r\n" + 
                "because it would break existing code. Now you are told that it’s okay to add\r\n" + 
                "new methods, provided you also supply a default implementation. How safe\r\n" + 
                "is that? Describe a scenario where the new stream method of the Collection\r\n" + 
                "interface causes legacy code to fail compilation. What about binary\r\n" + 
                "compatibility? Will legacy code from a JAR file still run?");
        exercise12();
    }
}
