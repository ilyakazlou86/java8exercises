public class ExercisesChapter3 {
    /**
     * Exercise 1.
     * Enhance the lazy logging technique by providing conditional logging. A
     * typical call would be logIf(Level.FINEST, () -> i == 10, () -> "a[10] = " + a[10]).
     * Don’t evaluate the condition if the logger won’t log the message.
     */
    private static void exercise01() {

    }

    /**
     * Exercise 2.
     * When you use a ReentrantLock, you are required to lock and unlock with the
     * idiom
     *     myLock.lock();
     *     try {
     *         some action
     *     } finally {
     *     myLock.unlock();
     *     }
     * Provide a method withLock so that one can call
     *     withLock(myLock, () -> { some action })
     */
    private static void exercise02() {

    }

    /**
     * Exercise 3.
     * Java 1.4 added assertions to the language, with an assert keyword. Why were
     * assertions not supplied as a library feature? Could they be implemented as
     * a library feature in Java 8?
     */
    private static void exercise03() {

    }

    /**
     * Exercise 4.
     * How many functional interfaces with Filter in their name can you find in the
     * Java API? Which ones add value over Predicate<T>?
     */
    private static void exercise04() {

    }

    /**
     * Exercise 5.
     * Here is a concrete example of a ColorTransformer. We want to put a frame around
     * an image, like this:
     *     [*]
     * First, implement a variant of the transform method of Section 3.3, “Choosing
     * a Functional Interface,” on page 50, with a ColorTransformer instead of an
     * UnaryOperator<Color>. Then call it with an appropriate lambda expression to put
     * a 10 pixel gray frame replacing the pixels on the border of an image.
     */
    private static void exercise05() {

    }

    /**
     * Exercise 6.
     * Complete the method
     *     public static <T> Image transform(Image in, BiFunction<Color, T> f, T arg)
     * from Section 3.4, “Returning Functions,” on page 53.
     */
    private static void exercise06() {

    }

    /**
     * Exercise 7.
     * Write a method that generates a Comparator<String> that can be normal or
     * reversed, case-sensitive or case-insensitive, space-sensitive or space-insensitive,
     * or any combination thereof. Your method should return a lambda expression.
     */
    private static void exercise07() {

    }

    /**
     * Exercise 8.
     * Generalize Exercise 5 by writing a static method that yields a ColorTransformer
     * that adds a frame of arbitrary thickness and color to an image.
     */
    private static void exercise08() {

    }

    /**
     * Exercise 9.
     * Write a method lexicographicComparator(String... fieldNames) that yields a
     * comparator that compares the given fields in the given order. For example, a
     * lexicographicComparator("lastname", "firstname") takes two objects and, using
     * reflection, gets the values of the lastname field. If they are different, return the
     * difference, otherwise move on to the firstname field. If all fields match, return 0.
     */
    private static void exercise09() {

    }

    /**
     * Exercise 10.
     * Why can’t one call
     *     UnaryOperator op = Color::brighter;
     *     Image finalImage = transform(image, op.compose(Color::grayscale));
     * Look carefully at the return type of the compose method of UnaryOperator<T>.
     * Why is it not appropriate for the transform method? What does that say about
     * the utility of structural and nominal types when it comes to function
     * composition?
     */
    private static void exercise10() {

    }

    /**
     * Exercise 11.
     * Implement static methods that can compose two ColorTransformer objects, and
     * a static method that turns a UnaryOperator<Color> into a ColorTransformer that
     * ignores the x- and y-coordinates. Then use these methods to add a gray frame
     * to a brightened image. (See Exercise 5 for the gray frame.)
     */
    private static void exercise11() {

    }

    /**
     * Exercise 12.
     * Enhance the LatentImage class in Section 3.6, “Laziness,” on page 56, so that it
     * supports both UnaryOperator<Color> and ColorTransformer. Hint: Adapt the former
     * to the latter.
     */
    private static void exercise12() {

    }

    /**
     * Exercise 13.
     * Convolution filters such as blur or edge detection compute a pixel from
     * neighboring pixels. To blur an image, replace each color value by the average
     * of itself and its eight neighbors. For edge detection, replace each color value
     * c with 4c – n – e – s – w, where the other colors are those of the pixel to the
     * north, east, south, and west. Note that these cannot be implemented lazily,
     * using the approach of Section 3.6, “Laziness,” on page 56, since they require
     * the image from the previous stage (or at least the neighboring pixels) to have
     * been computed. Enhance the lazy image processing to deal with these operations.
     * Force computation of the previous stage when one of these operators
     * is evaluated.
     */
    private static void exercise13() {

    }

    /**
     * Exercise 14.
     * To deal with lazy evaluation on a per-pixel basis, change the transformers so
     * that they are passed a PixelReader object from which they can read other pixels
     * in the image. For example, (x, y, reader) -> reader.get(width - x, y) is a mirroring
     * operation. The convolution filters from the preceding exercises can be
     * easily implemented in terms of such a reader. The straightforward operations
     * would simply have the form (x, y, reader) -> reader.get(x, y).grayscale(), and
     * you can provide an adapter from UnaryOperation<Color>. A PixelReader is at a
     * particular level in the pipeline of operations. Keep a cache of recently read
     * pixels at each level in the pipeline. If a reader is asked for a pixel, it looks in
     * the cache (or in the original image at level 0); if that fails, it constructs a
     * reader that asks the previous transform.
     */
    private static void exercise14() {

    }

    /**
     * Exercise 15.
     * Combine the lazy evaluation of Section 3.6, “Laziness,” on page 56, with the
     * parallel evaluation of Section 3.7, “Parallelizing Operations,” on page 57.
     */
    private static void exercise15() {

    }

    /**
     * Exercise 16.
     * Implement the doInOrderAsync of Section 3.8, “Dealing with Exceptions,” on
     * page 58, where the second parameter is a BiConsumer<T, Throwable>. Provide
     * a plausible use case. Do you still need the third parameter?
     */
    private static void exercise16() {

    }

    /**
     * Exercise 17.
     * Implement a doInParallelAsync(Runnable first, Runnable second, Consumer<Throwable>)
     * method that executes first and second in parallel, calling the handler if
     * either method throws an exception.
     */
    private static void exercise17() {

    }

    /**
     * Exercise 18.
     * Implement a version of the unchecked method in Section 3.8, “Dealing with
     * Exceptions,” on page 58, that generates a Function<T, U> from a lambda that
     * throws checked exceptions. Note that you will need to find or provide a
     * functional interface whose abstract method throws arbitrary exceptions.
     */
    private static void exercise18() {

    }

    /**
     * Exercise 19.
     * Look at the Stream<T> method <U> U reduce(U identity, BiFunction<U,? super T,U>
     * accumulator, BinaryOperator<U> combiner). Should U be declared as ? super U in the
     * first type argument to BiFunction? Why or why not?
     */
    private static void exercise19() {

    }

    /**
     * Exercise 20.
     * Supply a static method <T, U> List<U> map(List<T>, Function<T, U>).
     */
    private static void exercise20() {

    }

    /**
     * Exercise 21.
     * Supply a static method <T, U> Future<U> map(Future<T>, Function<T, U>). Return an
     * object of an anonymous class that implements all methods of the Future
     * interface. In the get methods, invoke the function.
     */
    private static void exercise21() {

    }

    /**
     * Exercise 22.
     * Is there a flatMap operation for CompletableFuture? If so, what is it?
     */
    private static void exercise22() {

    }

    /**
     * Exercise 23.
     * Define a map operation for a class Pair<T> that represents a pair of objects of
     * type T.
     */
    private static void exercise23() {

    }

    /**
     * Exercise 24.
     * Can you define a flatMap method for Pair<T>? If so, what is it? If not, why not?
     */
    private static void exercise24() {

    }

    public static void main(String[] args) throws Exception {
        System.out.println("\n*** Exercise 1 ***\nTask: " +
            "Enhance the lazy logging technique by providing conditional logging. A\r\n" + 
            "typical call would be logIf(Level.FINEST, () -> i == 10, () -> \"a[10] = \" + a[10]).\r\n" + 
            "Don’t evaluate the condition if the logger won’t log the message.");
        exercise01();
        System.out.println("\n*** Exercise 2 ***\nTask: " +
            "When you use a ReentrantLock, you are required to lock and unlock with the\r\n" + 
            "idiom\r\n" + 
            "    myLock.lock();\r\n" + 
            "    try {\r\n" + 
            "        some action\r\n" + 
            "    } finally {\r\n" + 
            "        myLock.unlock();\r\n" + 
            "    }\r\n" + 
            "Provide a method withLock so that one can call\r\n" + 
            "    withLock(myLock, () -> { some action })");
        exercise02();
        System.out.println("\n*** Exercise 3 ***\nTask: " +
            "Java 1.4 added assertions to the language, with an assert keyword. Why were\r\n" + 
            "assertions not supplied as a library feature? Could they be implemented as\r\n" + 
            "a library feature in Java 8?");
        exercise03();
        System.out.println("\n*** Exercise 4 ***\nTask: " +
            "How many functional interfaces with Filter in their name can you find in the\r\n" + 
            "Java API? Which ones add value over Predicate<T>?");
        exercise04();
        System.out.println("\n*** Exercise 5 ***\nTask: " +
            "Here is a concrete example of a ColorTransformer. We want to put a frame around\r\n" + 
            "an image, like this:\r\n" +
            "    [*]\r\n" +
            "First, implement a variant of the transform method of Section 3.3, “Choosing\r\n" + 
            "a Functional Interface,” on page 50, with a ColorTransformer instead of an\r\n" + 
            "UnaryOperator<Color>. Then call it with an appropriate lambda expression to put\r\n" + 
            "a 10 pixel gray frame replacing the pixels on the border of an image.");
        exercise05();
        System.out.println("\n*** Exercise 6 ***\nTask: " +
            "Complete the method\r\n" + 
            "    public static <T> Image transform(Image in, BiFunction<Color, T> f, T arg)\r\n" + 
            "from Section 3.4, “Returning Functions,” on page 53.");
        exercise06();
        System.out.println("\n*** Exercise 7 ***\nTask: " +
            "Write a method that generates a Comparator<String> that can be normal or\r\n" +
            "reversed, case-sensitive or case-insensitive, space-sensitive or space-insensitive,\r\n" + 
            "or any combination thereof. Your method should return a lambda expression.");
        exercise07();
        System.out.println("\n*** Exercise 8 ***\nTask: " +
            "Generalize Exercise 5 by writing a static method that yields a ColorTransformer\r\n" + 
            "that adds a frame of arbitrary thickness and color to an image.");
        exercise08();
        System.out.println("\n*** Exercise 9 ***\nTask: " +
            "Write a method lexicographicComparator(String... fieldNames) that yields a comparator that\r\n" +
            "compares the given fields in the given order. For example, a\r\n" + 
            "lexicographicComparator(\"lastname\", \"firstname\") takes two objects and, using\r\n" + 
            "reflection, gets the values of the lastname field. If they are different, return the\r\n" + 
            "difference, otherwise move on to the firstname field. If all fields match, return 0.");
        exercise09();
        System.out.println("\n*** Exercise 10 ***\nTask: " +
            "Why can’t one call\r\n" + 
            "    UnaryOperator op = Color::brighter;\r\n" + 
            "    Image finalImage = transform(image, op.compose(Color::grayscale));\r\n" + 
            "Look carefully at the return type of the compose method of UnaryOperator<T>.\r\n" + 
            "Why is it not appropriate for the transform method? What does that say about\r\n" +
            "the utility of structural and nominal types when it comes to function\r\n" + 
            "composition?");
        exercise10();
        System.out.println("\n*** Exercise 11 ***\nTask: " +
            "Implement static methods that can compose two ColorTransformer objects, and\r\n" + 
            "a static method that turns a UnaryOperator<Color> into a ColorTransformer that\r\n" +
            "ignores the x- and y-coordinates. Then use these methods to add a gray frame\r\n" + 
            "to a brightened image. (See Exercise 5 for the gray frame.)");
        exercise11();
        System.out.println("\n*** Exercise 12 ***\nTask: " +
            "Enhance the LatentImage class in Section 3.6, “Laziness,” on page 56, so that it\r\n" + 
            "supports both UnaryOperator<Color> and ColorTransformer. Hint: Adapt the former\r\n" + 
            "to the latter.");
        exercise12();
        System.out.println("\n*** Exercise 13 ***\nTask: " +
            "Convolution filters such as blur or edge detection compute a pixel from\r\n" + 
            "neighboring pixels. To blur an image, replace each color value by the average\r\n" + 
            "of itself and its eight neighbors. For edge detection, replace each color value\r\n" + 
            "c with 4c – n – e – s – w, where the other colors are those of the pixel to the\r\n" + 
            "north, east, south, and west. Note that these cannot be implemented lazily,\r\n" + 
            "using the approach of Section 3.6, “Laziness,” on page 56, since they require\r\n" + 
            "the image from the previous stage (or at least the neighboring pixels) to have\r\n" + 
            "been computed. Enhance the lazy image processing to deal with these operations.\r\n" +
            "Force computation of the previous stage when one of these operators\r\n" + 
            "is evaluated.");
        exercise13();
        System.out.println("\n*** Exercise 14 ***\nTask: " +
            "To deal with lazy evaluation on a per-pixel basis, change the transformers so\r\n" + 
            "that they are passed a PixelReader object from which they can read other pixels\r\n" + 
            "in the image. For example, (x, y, reader) -> reader.get(width - x, y) is a mirroring\r\n" +
            "operation. The convolution filters from the preceding exercises can be\r\n" + 
            "easily implemented in terms of such a reader. The straightforward operations\r\n" + 
            "would simply have the form (x, y, reader) -> reader.get(x, y).grayscale(), and\r\n" + 
            "you can provide an adapter from UnaryOperation<Color>. A PixelReader is at a\r\n" + 
            "particular level in the pipeline of operations. Keep a cache of recently read\r\n" + 
            "pixels at each level in the pipeline. If a reader is asked for a pixel, it looks in\r\n" + 
            "the cache (or in the original image at level 0); if that fails, it constructs a\r\n" + 
            "reader that asks the previous transform.");
        exercise14();
        System.out.println("\n*** Exercise 15 ***\nTask: " +
            "Combine the lazy evaluation of Section 3.6, “Laziness,” on page 56, with the\r\n" + 
            "parallel evaluation of Section 3.7, “Parallelizing Operations,” on page 57.");
        exercise15();
        System.out.println("\n*** Exercise 16 ***\nTask: " +
            "Implement the doInOrderAsync of Section 3.8, “Dealing with Exceptions,” on\r\n" + 
            "page 58, where the second parameter is a BiConsumer<T, Throwable>. Provide\r\n" + 
            "a plausible use case. Do you still need the third parameter?");
        exercise16();
        System.out.println("\n*** Exercise 17 ***\nTask: " +
            "Implement a doInParallelAsync(Runnable first, Runnable second, Consumer<Throwable>)\r\n" + 
            "method that executes first and second in parallel, calling the handler if\r\n" + 
            "either method throws an exception.");
        exercise17();
        System.out.println("\n*** Exercise 18 ***\nTask: " +
            "Implement a version of the unchecked method in Section 3.8, “Dealing with\r\n" + 
            "Exceptions,” on page 58, that generates a Function<T, U> from a lambda that\r\n" + 
            "throws checked exceptions. Note that you will need to find or provide a\r\n" + 
            "functional interface whose abstract method throws arbitrary exceptions.");
        exercise18();
        System.out.println("\n*** Exercise 19 ***\nTask: " +
            "Look at the Stream<T> method <U> U reduce(U identity, BiFunction<U,? super T,U>\r\n" + 
            "accumulator, BinaryOperator<U> combiner). Should U be declared as ? super U in the\r\n" + 
            "first type argument to BiFunction? Why or why not?");
        exercise19();
        System.out.println("\n*** Exercise 20 ***\nTask: " +
            "Supply a static method <T, U> List<U> map(List<T>, Function<T, U>).");
        exercise20();
        System.out.println("\n*** Exercise 21 ***\nTask: " +
            "Supply a static method <T, U> Future<U> map(Future<T>, Function<T, U>). Return an\r\n" + 
            "object of an anonymous class that implements all methods of the Future\r\n" + 
            "interface. In the get methods, invoke the function.");
        exercise21();
        System.out.println("\n*** Exercise 22 ***\nTask: " +
            "Is there a flatMap operation for CompletableFuture? If so, what is it?");
        exercise22();
        System.out.println("\n*** Exercise 23 ***\nTask: " +
            "Define a map operation for a class Pair<T> that represents a pair of objects of\r\n" + 
            "type T.");
        exercise23();
        System.out.println("\n*** Exercise 24 ***\nTask: " +
            "Can you define a flatMap method for Pair<T>? If so, what is it? If not, why not?");
        exercise24();
    }
}
