import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ExercisesChapter3 {
    /**
     * Exercise 1.
     * Enhance the lazy logging technique by providing conditional logging. A
     * typical call would be logIf(Level.FINEST, () -> i == 10, () -> "a[10] = " + a[10]).
     * Don’t evaluate the condition if the logger won’t log the message.
     */
    private static Logger logger = Logger.getLogger(ExercisesChapter3.class.getName());
    
    private static void logIf(Level level, BooleanSupplier condition, Supplier<?> message) {
        if (logger.isLoggable(level) && condition.getAsBoolean()) {
            logger.log(level, message.get().toString());
        }
    }
    
    private static void exercise01() {
        int[] a = new int[20];
        int i = 10;
        logger.setLevel(Level.ALL);
        logIf(Level.FINEST, () -> i == 10, () -> "a[10] = " + a[10]); // will be logged
        logIf(Level.FINEST, () -> i != 10, () -> "a[10] = " + a[10]); // will not be logged
        logger.setLevel(Level.OFF);
        logIf(Level.FINEST, () -> i == 10, () -> "a[10] = " + a[10]); // will not be logged
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
    private static void withLock(Lock lock, Runnable action) {
        lock.lock();
        try {
            action.run();
        } finally {
            lock.unlock();
        }
    }
    
    private static void exercise02() {
        withLock(new ReentrantLock(), () -> { /* some action */ });
    }

    /**
     * Exercise 3.
     * Java 1.4 added assertions to the language, with an assert keyword. Why were
     * assertions not supplied as a library feature? Could they be implemented as
     * a library feature in Java 8?
     */
    private static void exercise03() {
        /*
         * Well, I've never used assertions in my entire life. And I hope to continue
         * that glorious tradition in Java 8. Assertions are legacy garbage. Rest in
         * peace, assertions. Rest in peace...
         */
    }

    /**
     * Exercise 4.
     * How many functional interfaces with Filter in their name can you find in the
     * Java API? Which ones add value over Predicate<T>?
     */
    private static void exercise04() {
        /*
         * I've found 11 interfaces.
         * Six of them can be replaced with some Predicate:
         * 01: java.io.FileFilter                       == Predicate<java.io.File>
         * 02: java.util.logging.Filter                 == Predicate<java.util.logging.LogRecord>
         * 03: javax.imageio.spi.ServiceRegistry.Filter == Predicate<Object>
         * 04: javax.management.NotificationFilter      == Predicate<javax.management.Notification>
         * 05: javax.xml.stream.EventFilter             == Predicate<avax.xml.stream.events.XMLEvent>
         * 06: javax.xml.stream.StreamFilter            == Predicate<javax.xml.stream.XMLStreamReader>
         * 
         * One can be replaced with IntPredicate:
         * 07: com.sun.org.apache.xalan.internal.xsltc.dom.Filter == IntPredicate
         * 
         * Another one add value over Predicate
         * 08: java.nio.file.DirectoryStream.Filter<T> // throws IOException
         * 
         * And three others are different:
         * 09: com.sun.org.apache.xpath.internal.patterns.NodeTestFilter // returns void
         * 10: org.w3c.dom.traversal.NodeFilter                          // returns short
         * 11: java.io.FilenameFilter                                    // takes two arguments
         */
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
    @FunctionalInterface
    private interface ColorTransformer {
        Color apply(int x, int y, Color colorAtXY);
    }
    
    public static Image transform(Image in, ColorTransformer f) {
        int width = (int) in.getWidth();
        int height = (int) in.getHeight();
        WritableImage out = new WritableImage(width, height);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                out.getPixelWriter().setColor(x, y, f.apply(x, y, in.getPixelReader().getColor(x, y)));
        return out;
    }
    
    private static void exercise05() throws Exception {
        final int borderSize = 10;
        final Image source = new WritableImage(50, 50);
        final int width = (int) source.getWidth();
        final int height = (int) source.getHeight();
        transform(source, (x, y, color) -> x >= borderSize &&
                                           x < width - borderSize &&
                                           y >= borderSize &&
                                           y < height - borderSize ?
                                           color : Color.GRAY);
    }

    /**
     * Exercise 6.
     * Complete the method
     *     public static <T> Image transform(Image in, BiFunction<Color, T> f, T arg)
     * from Section 3.4, “Returning Functions,” on page 53.
     */
    public static Image transform(Image in, UnaryOperator<Color> f) {
        int width = (int) in.getWidth();
        int height = (int) in.getHeight();
        WritableImage out = new WritableImage(width, height);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                out.getPixelWriter().setColor(x, y, f.apply(in.getPixelReader().getColor(x, y)));
        return out;
    }
    
    public static <T> Image transform(Image in, BiFunction<Color, T, Color> f, T arg) {
        return transform(in, color -> f.apply(color, arg));
    }
    
    private static void exercise06() {
        final Image source = new WritableImage(50, 50);
        transform(source, (c, factor) -> c.deriveColor(0, 1, factor, 1), 1.2);
    }

    /**
     * Exercise 7.
     * Write a method that generates a Comparator<String> that can be normal or
     * reversed, case-sensitive or case-insensitive, space-sensitive or space-insensitive,
     * or any combination thereof. Your method should return a lambda expression.
     */
    private static Comparator<String> comparatorGenerator(boolean isReversed, boolean isCaseSensitive, boolean isSpaceSensitive) {
        return (left, right) -> {
            if (!isSpaceSensitive) {
                left = left.replace(" ", "");
                right = right.replace(" ", "");
            }
            if (!isCaseSensitive) {
                left = left.toLowerCase();
                right = right.toLowerCase();
            }
            return isReversed ? right.compareTo(left) : left.compareTo(right);
        };
    }
    
    private static void exercise07() {
        comparatorGenerator(false, false, false).compare("abc", "A B C"); // => zero
        comparatorGenerator(true, true, false).compare("abc", "ABC");     // => negative 
        comparatorGenerator(false, true, true).compare("a bc", "AB C");   // => positive
    }

    /**
     * Exercise 8.
     * Generalize Exercise 5 by writing a static method that yields a ColorTransformer
     * that adds a frame of arbitrary thickness and color to an image.
     */
    private static ColorTransformer withBorder(int width, int height, int borderSize, Color borderColor) {
        return (x, y, color) -> x >= borderSize &&
                                x < width - borderSize &&
                                y >= borderSize &&
                                y < height - borderSize ?
                                color : borderColor; 
    }
    
    private static void exercise08() {
        final Image source = new WritableImage(50, 50);
        final int width = (int) source.getWidth();
        final int height = (int) source.getHeight();
        transform(source, withBorder(width, height, 10, Color.GRAY));
    }

    /**
     * Exercise 9.
     * Write a method lexicographicComparator(String... fieldNames) that yields a
     * comparator that compares the given fields in the given order. For example, a
     * lexicographicComparator("lastname", "firstname") takes two objects and, using
     * reflection, gets the values of the lastname field. If they are different, return the
     * difference, otherwise move on to the firstname field. If all fields match, return 0.
     */
    private static class Person {
        private String firstname;
        private String lastname;
        
        Person(String firstname, String lastname) {
            this.firstname = firstname;
            this.lastname = lastname;
        }
    }
    
    private static <T> Comparator<T> lexicographicComparator(String... fieldNames) {
        return (obj1, obj2) -> {
            @SuppressWarnings("unchecked") Class<T> clazz = (Class<T>) obj1.getClass();
            for(String fieldName : fieldNames) {
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    String value1 = (String) field.get(obj1);
                    String value2 = (String) field.get(obj2);
                    int result = value1.compareTo(value2);
                    if(result != 0) {
                        return result;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return 0;
        };
    }
    
    private static void exercise09() {
        Comparator<Person> cmprt = lexicographicComparator("lastname", "firstname");
        cmprt.compare(new Person("Ilya", "Kazlou"), new Person("Ihar", "Kazlou")); // => positive
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
        final Image image = new WritableImage(50, 50);
        UnaryOperator<Color> op = Color::brighter;
        /*
         *     Image finalImage = transform(image, op.compose(Color::grayscale)); // Compile error
         * We can't pass composition to the transform method because it is Function
         * which is not a subtype of UnaryOperator (actually it's its supertype).
         * So structural types are better than java's nominal types in case of
         * function composition.
         */
    }

    /**
     * Exercise 11.
     * Implement static methods that can compose two ColorTransformer objects, and
     * a static method that turns a UnaryOperator<Color> into a ColorTransformer that
     * ignores the x- and y-coordinates. Then use these methods to add a gray frame
     * to a brightened image. (See Exercise 5 for the gray frame.)
     */
    private static ColorTransformer compose(ColorTransformer f, ColorTransformer g) {
        return (x, y, color) -> f.apply(x, y, g.apply(x, y, color));
    }
    
    private static ColorTransformer toColorTransformer(UnaryOperator<Color> op) {
        return (x, y, color) -> op.apply(color);
    }
    
    private static void exercise11() {
        final Image source = new WritableImage(50, 50);
        final int width = (int) source.getWidth();
        final int height = (int) source.getHeight();
        ColorTransformer brighter = toColorTransformer(Color::brighter);
        ColorTransformer withBorder = withBorder(width, height, 10, Color.GRAY);
        transform(source, compose(withBorder, brighter));
    }

    /**
     * Exercise 12.
     * Enhance the LatentImage class in Section 3.6, “Laziness,” on page 56, so that it
     * supports both UnaryOperator<Color> and ColorTransformer. Hint: Adapt the former
     * to the latter.
     */
    private static class LatentImage {
        private Image in;
        private List<ColorTransformer> pendingOperations;
        
        public LatentImage(Image in) {
            this.in = in;
            pendingOperations = new ArrayList<>();
        }
        
        public static LatentImage from(Image in) {
            return new LatentImage(in);
        }
        
        public LatentImage transform(ColorTransformer f) {
            pendingOperations.add(f);
            return this;
        }
        
        public LatentImage transform(UnaryOperator<Color> f) {
            return transform(toColorTransformer(f));
        }
        
        public Image toImage() {
            int width = (int) in.getWidth();
            int height = (int) in.getHeight();
            WritableImage out = new WritableImage(width, height);
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++) {
                    Color c = in.getPixelReader().getColor(x, y);
                    for (ColorTransformer f : pendingOperations)
                        c = f.apply(x, y, c);
                    out.getPixelWriter().setColor(x, y, c);
                }
            return out;
        }
    }
    
    private static void exercise12() {
        final Image source = new WritableImage(50, 50);
        final int width = (int) source.getWidth();
        final int height = (int) source.getHeight();
        Image finalImage = LatentImage.from(source)
                                      .transform(Color::brighter) // apply UnaryOperator
                                      .transform(Color::grayscale) // apply UnaryOperator
                                      .transform(withBorder(width, height, 10, Color.GRAY)) // apply ColorTransformer
                                      .toImage();
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
    private interface ColorFilter {
        void accept(Image source, WritableImage out, int x, int y, int width, int height);
        static ColorFilter blur() {
            return (source, out, x, y, width, height) -> {
                double red = 0;
                double green = 0;
                double blue = 0;
                double alpha = 0;
                int count = 0;
                for (int dx = -1; dx < 2; dx++)
                    for (int dy = -1; dy < 2; dy++) {
                        int newX = x + dx;
                        int newY = y + dy;
                        if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                            Color newColor = source.getPixelReader().getColor(newX, newY);
                            red += newColor.getRed();
                            green += newColor.getGreen();
                            blue += newColor.getBlue();
                            alpha += newColor.getOpacity();
                            count++;
                        }
                    }
                out.getPixelWriter().setColor(x, y, new Color(red / count,
                                                              green / count,
                                                              blue / count,
                                                              alpha / count));
            };
        }
        static ColorFilter edgeDetection() {
            return (source, out, x, y, width, height) -> {
                Color newColor = source.getPixelReader().getColor(x, y); // center color
                double red = 4 * newColor.getRed();
                double green = 4 * newColor.getGreen();
                double blue = 4 * newColor.getBlue();
                double alpha = 4 * newColor.getOpacity();
                if (x > 0) {
                    newColor = source.getPixelReader().getColor(x - 1, y); // color in the west
                    red -= newColor.getRed();
                    green -= newColor.getGreen();
                    blue -= newColor.getBlue();
                    alpha -= newColor.getOpacity();
                }
                if (x < width - 1) {
                    newColor = source.getPixelReader().getColor(x + 1, y); // color in the east
                    red -= newColor.getRed();
                    green -= newColor.getGreen();
                    blue -= newColor.getBlue();
                    alpha -= newColor.getOpacity();
                }
                if (y > 0) {
                    newColor = source.getPixelReader().getColor(x, y - 1); // color in the north
                    red -= newColor.getRed();
                    green -= newColor.getGreen();
                    blue -= newColor.getBlue();
                    alpha -= newColor.getOpacity();
                }
                if (y < height - 1) {
                    newColor = source.getPixelReader().getColor(x, y + 1); // color in the south
                    red -= newColor.getRed();
                    green -= newColor.getGreen();
                    blue -= newColor.getBlue();
                    alpha -= newColor.getOpacity();
                }
                out.getPixelWriter().setColor(x, y, new Color(red < 0D ? 0D : red > 1D ? 1D : red,
                                                              green < 0D ? 0D : green > 1D ? 1D : green,
                                                              blue < 0D ? 0D : blue > 1D ? 1D : blue,
                                                              alpha < 0D ? 0D : alpha > 1D ? 1D : alpha));
            };
        }
    }
    
    private static class FilteredLatentImage {
        private LatentImage latentImage; // we use composition because LatentImage wasn't designed for inheritance
        
        public FilteredLatentImage(LatentImage latentImage) {
            this.latentImage = latentImage;
        }
        
        static FilteredLatentImage from(Image in) {
            return new FilteredLatentImage(LatentImage.from(in));
        }
        
        public FilteredLatentImage transform(ColorTransformer f) {
            latentImage.transform(f);
            return this;
        }
        
        public FilteredLatentImage transform(UnaryOperator<Color> f) {
            latentImage.transform(f);
            return this;
        }
        
        private FilteredLatentImage transform(Supplier<ColorFilter> filter) {
            Image source = toImage();
            int width = (int) source.getWidth();
            int height = (int) source.getHeight();
            WritableImage out = new WritableImage(width, height);
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++)
                    filter.get().accept(source, out, x, y, width, height);
            return FilteredLatentImage.from(out);
        }
        
        public FilteredLatentImage blur() {
            return transform(ColorFilter::blur);
        }
        
        public FilteredLatentImage edgeDetection() {
            return transform(ColorFilter::edgeDetection);
        }
        
        public Image toImage() {
            return latentImage.toImage();
        }
    }
    
    private static void exercise13() throws Exception {
        final Image source = new WritableImage(50, 50);
//        Image source = SwingFXUtils.toFXImage(ImageIO.read(new File("source.png")), null);
        final int width = (int) source.getWidth();
        final int height = (int) source.getHeight();
        Image finalImage = FilteredLatentImage.from(source)
                                              .transform(Color::brighter) // apply lazy transform
                                              .transform(Color::grayscale) // apply lazy transform
                                              .blur() // apply forced transform
                                              .transform(withBorder(width, height, 10, Color.GRAY)) // apply lazy transform
                                              .edgeDetection() // apply forced transform 
                                              .toImage();
//        ImageIO.write(SwingFXUtils.fromFXImage(finalImage, null), "png", new File("result.png"));
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
    @FunctionalInterface
    private interface ColorTransformer2 {
        Color apply(int x, int y, PixelReader reader);
        
        static ColorTransformer2 withBorder(int width, int height, int borderSize, Color borderColor) {
            return (x, y, reader) -> x >= borderSize &&
                    x < width - borderSize &&
                    y >= borderSize &&
                    y < height - borderSize ?
                    reader.get(x, y) : borderColor; 
        }
        
        static ColorTransformer2 blur(int width, int height) {
            return (x, y, reader) -> {
                double red = 0;
                double green = 0;
                double blue = 0;
                double alpha = 0;
                int count = 0;
                for (int dx = -1; dx < 2; dx++)
                    for (int dy = -1; dy < 2; dy++) {
                        int newX = x + dx;
                        int newY = y + dy;
                        if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                            Color newColor = reader.get(newX, newY);
                            red += newColor.getRed();
                            green += newColor.getGreen();
                            blue += newColor.getBlue();
                            alpha += newColor.getOpacity();
                            count++;
                        }
                    }
                return new Color(red / count, green / count, blue / count, alpha / count);
            };
        }
        
        static ColorTransformer2 edgeDetection(int width, int height) {
            return (x, y, reader) -> {
                Color newColor = reader.get(x, y);
                double red = 4 * newColor.getRed();
                double green = 4 * newColor.getGreen();
                double blue = 4 * newColor.getBlue();
                double alpha = 4 * newColor.getOpacity();
                if (x > 0) {
                    newColor = reader.get(x - 1, y);
                    red -= newColor.getRed();
                    green -= newColor.getGreen();
                    blue -= newColor.getBlue();
                    alpha -= newColor.getOpacity();
                }
                if (x < width - 1) {
                    newColor = reader.get(x + 1, y);
                    red -= newColor.getRed();
                    green -= newColor.getGreen();
                    blue -= newColor.getBlue();
                    alpha -= newColor.getOpacity();
                }
                if (y > 0) {
                    newColor = reader.get(x, y - 1);
                    red -= newColor.getRed();
                    green -= newColor.getGreen();
                    blue -= newColor.getBlue();
                    alpha -= newColor.getOpacity();
                }
                if (y < height - 1) {
                    newColor = reader.get(x, y + 1);
                    red -= newColor.getRed();
                    green -= newColor.getGreen();
                    blue -= newColor.getBlue();
                    alpha -= newColor.getOpacity();
                }
                return new Color(red < 0D ? 0D : red > 1D ? 1D : red,
                                 green < 0D ? 0D : green > 1D ? 1D : green,
                                 blue < 0D ? 0D : blue > 1D ? 1D : blue,
                                 alpha < 0D ? 0D : alpha > 1D ? 1D : alpha);
            };
        }
    }
    
    @FunctionalInterface
    private interface PixelReader {
        Color get(int x, int y);
    }
    
    private static class LazyLatentImage {
        private Image in;
        private List<ColorTransformer2> pendingOperations;
        private Color[][][] cache;
        
        public LazyLatentImage(Image in) {
            this.in = in;
            pendingOperations = new ArrayList<>();
        }
        
        public static LazyLatentImage from(Image in) {
            return new LazyLatentImage(in);
        }
        
        public LazyLatentImage transform(ColorTransformer2 f) {
            pendingOperations.add(f);
            return this;
        }
        
        public LazyLatentImage transform(UnaryOperator<Color> f) {
            return transform((x, y, reader) -> f.apply(reader.get(x, y)));
        }
        
        private PixelReader getReader(int level) {
            return (x, y) -> {
                Color result = cache[level][x][y];
                if (result == null) {
                    result = level == 0 ? in.getPixelReader().getColor(x, y) :
                                          pendingOperations.get(level - 1).apply(x, y, getReader(level - 1));
                    cache[level][x][y] = result;
                }
                return result;
            };
        }
        
        public Image toImage() {
            int width = (int) in.getWidth();
            int height = (int) in.getHeight();
            int n = pendingOperations.size();
            cache = new Color[n][width][height];
            WritableImage out = new WritableImage(width, height);
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++) {
                    Color c = getReader(0).get(x, y);
                    for (int level = 0; level < n; level++)
                        c = pendingOperations.get(level).apply(x, y, getReader(level));
                    out.getPixelWriter().setColor(x, y, c);
                }
            return out;
        }
    }
    
    private static void exercise14() throws Exception {
        final Image source = new WritableImage(50, 50);
//        Image source = SwingFXUtils.toFXImage(ImageIO.read(new File("source.png")), null);
        final int width = (int) source.getWidth();
        final int height = (int) source.getHeight();
        Image finalImage = LazyLatentImage.from(source)
                                       .transform(Color::brighter)
                                       .transform(Color::grayscale)
                                       .transform((x, y, reader) -> reader.get(width - x - 1, y)) // mirroring operation
                                       .transform(ColorTransformer2.blur(width, height))
                                       .transform(ColorTransformer2.withBorder(width, height, 10, Color.GRAY))
                                       .toImage();
//        ImageIO.write(SwingFXUtils.fromFXImage(finalImage, null), "png", new File("result.png"));
    }

    /**
     * Exercise 15.
     * Combine the lazy evaluation of Section 3.6, “Laziness,” on page 56, with the
     * parallel evaluation of Section 3.7, “Parallelizing Operations,” on page 57.
     */
    private static class ParallelLatentImage {
        private Image in;
        private List<ColorTransformer2> pendingOperations;
        private Color[][][] cache;
        
        public ParallelLatentImage(Image in) {
            this.in = in;
            pendingOperations = new ArrayList<>();
        }
        
        public static ParallelLatentImage from(Image in) {
            return new ParallelLatentImage(in);
        }
        
        public ParallelLatentImage transform(ColorTransformer2 f) {
            pendingOperations.add(f);
            return this;
        }
        
        public ParallelLatentImage transform(UnaryOperator<Color> f) {
            return transform((x, y, reader) -> f.apply(reader.get(x, y)));
        }
        
        private PixelReader getReader(int level) {
            return (x, y) -> {
                Color result = cache[level][x][y];
                if (result == null) {
                    result = level == 0 ? in.getPixelReader().getColor(x, y) :
                                          pendingOperations.get(level - 1).apply(x, y, getReader(level - 1));
                    cache[level][x][y] = result;
                }
                return result;
            };
        }
        
        private Color[][] parallelTransform(Color[][] buffer, int width, int height) {
            int cores = Runtime.getRuntime().availableProcessors();
            int n = pendingOperations.size();
            Color[][] out = new Color[width][height];
            try {
                ExecutorService pool = Executors.newCachedThreadPool();
                for (int i = 0; i < cores; i++) {
                    int fromY = i * height / cores;
                    int toY = (i + 1) * height / cores;
                    pool.submit(() -> {
                        for (int x = 0; x < width; x++)
                            for (int y = fromY; y < toY; y++) {
                                Color c = getReader(0).get(x, y);
                                for (int level = 0; level < n; level++)
                                    c = pendingOperations.get(level).apply(x, y, getReader(level));
                                out[x][y] = c;
                            }
                    });
                }
                pool.shutdown();
                pool.awaitTermination(1, TimeUnit.HOURS);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            return out;
        }
        
        public Image toImage() {
            int width = (int) in.getWidth();
            int height = (int) in.getHeight();
            cache = new Color[pendingOperations.size()][width][height];
            Color[][] buffer = new Color[width][height];
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++)
                    buffer[x][y] = in.getPixelReader().getColor(x, y);
            buffer = parallelTransform(buffer, width, height);
            WritableImage out = new WritableImage(width, height);
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++)
                    out.getPixelWriter().setColor(x, y, buffer[x][y]);
            return out;
        }
    }
    
    private static void exercise15() throws Exception {
        final Image source = new WritableImage(50, 50);
//        Image source = SwingFXUtils.toFXImage(ImageIO.read(new File("source.png")), null);
        final int width = (int) source.getWidth();
        final int height = (int) source.getHeight();
        Image finalImage = ParallelLatentImage.from(source)
                                              .transform(Color::brighter)
                                              .transform(Color::grayscale)
                                              .transform((x, y, reader) -> reader.get(width - x - 1, y))
                                              .transform(ColorTransformer2.blur(width, height))
                                              .transform(ColorTransformer2.withBorder(width, height, 10, Color.GRAY))
                                              .toImage();
//        ImageIO.write(SwingFXUtils.fromFXImage(finalImage, null), "png", new File("result2.png"));
  }

    /**
     * Exercise 16.
     * Implement the doInOrderAsync of Section 3.8, “Dealing with Exceptions,” on
     * page 58, where the second parameter is a BiConsumer<T, Throwable>. Provide
     * a plausible use case. Do you still need the third parameter?
     */
    public static <T> void doInOrderAsync(Supplier<T> first, BiConsumer<T, Throwable> second) {
        Thread t = new Thread() {
            public void run() {
                T result = null;
                Throwable error = null;
                try {
                    result = first.get();
                } catch (Throwable t) {
                    error = t;
                } finally {
                    second.accept(result, error);
                }
            }
        };
        t.start();
    }
    
    private static void exercise16() {
        Supplier<Double> doubleSupplier = () -> {
            Double result = Math.random();
            if (result >= 0.5) {
                return result;
            } else {
                throw new NumberFormatException("Error: Generated number less than 0.5");
            }
        };
        BiConsumer<Double, Throwable> doubleConsumer = (num, error) -> {
            if (error == null) {
                System.out.printf("Success: Generated number is %.2f\n", num);
            } else {
                System.out.println(error.getMessage());
            }
        };
        doInOrderAsync(doubleSupplier, doubleConsumer);
        /*
         * We still need the third parameter if we want to handle exceptions thrown by the second
         */
    }

    /**
     * Exercise 17.
     * Implement a doInParallelAsync(Runnable first, Runnable second, Consumer<Throwable>)
     * method that executes first and second in parallel, calling the handler if
     * either method throws an exception.
     */
    private static void doInParallelAsync(Runnable first, Runnable second, Consumer<Throwable> handler) {
        new Thread(() -> {
            new Thread(() -> {
                try {
                    first.run();
                } catch (Throwable t) {
                    handler.accept(t);
                }
            }).start();
            new Thread(() -> {
                try {
                    second.run();
                } catch (Throwable t) {
                    handler.accept(t);
                }
            }).start();
        }).start();
    }
    
    private static void exercise17() {
        Runnable first = () -> {
            if (Math.random() > 0.5) {
                System.out.println("First success!");
            } else {
                throw new RuntimeException("First failed!");
            }
        };
        Runnable second = () -> {
            if (Math.random() < 0.5) {
                System.out.println("Second success!");
            } else {
                throw new RuntimeException("Second failed!");
            }
        };
        Consumer<Throwable> errorHandler = error -> System.out.printf("Oops... %s\n", error.getMessage());
        doInParallelAsync(first, second, errorHandler);
    }

    /**
     * Exercise 18.
     * Implement a version of the unchecked method in Section 3.8, “Dealing with
     * Exceptions,” on page 58, that generates a Function<T, U> from a lambda that
     * throws checked exceptions. Note that you will need to find or provide a
     * functional interface whose abstract method throws arbitrary exceptions.
     */
    @FunctionalInterface
    private interface ThrowableFunction<T, R> {
        R apply(T t) throws Exception;
    } 
    
    private static <T, R> Function<T, R> unchecked(ThrowableFunction<T, R> f) {
        return arg -> {
            try {
                return f.apply(arg);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            catch (Throwable t) {
                throw t;
            }
        };
    }
    
    private static void exercise18() {
        Function<Long, Void> sleeper = unchecked((Long time) -> { Thread.sleep(time); return null; });
        Function<Path, String> pathToString = unchecked(path -> new String(Files.readAllBytes(path), StandardCharsets.UTF_8));
        sleeper.apply(10L); // sleeps 10 ms
        // pathToString.apply(Paths.get("not a path")); // will throw RE
    }

    /**
     * Exercise 19.
     * Look at the Stream<T> method <U> U reduce(U identity, BiFunction<U,? super T,U>
     * accumulator, BinaryOperator<U> combiner). Should U be declared as ? super U in the
     * first type argument to BiFunction? Why or why not?
     */
    private static void exercise19() {
        /*
         * U is argument and return type at the same time. So, we cann't use bounded wildcards
         * in such situation.
         */
    }

    /**
     * Exercise 20.
     * Supply a static method <T, U> List<U> map(List<T>, Function<T, U>).
     */
    private static <T, U> List<U> map(List<T> source, Function<T, U> mapper) {
        return source.stream().map(mapper).collect(Collectors.toList());
    }
    
    private static void exercise20() {
        List<String> stringList = Arrays.asList("1", "2", "3", "4", "5");
        List<Integer> integerList = map(stringList, Integer::valueOf);
    }

    /**
     * Exercise 21.
     * Supply a static method <T, U> Future<U> map(Future<T>, Function<T, U>). Return an
     * object of an anonymous class that implements all methods of the Future
     * interface. In the get methods, invoke the function.
     */
    private static <T, U> Future<U> map(Future<T> source, Function<T, U> mapper) {
        /* 
         * I've not found an appropriate wrapper in core API. So here is my implementation:
         */
        return new Future<U>() {

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return source.cancel(mayInterruptIfRunning);
            }

            @Override
            public boolean isCancelled() {
                return source.isCancelled();
            }

            @Override
            public boolean isDone() {
                return source.isDone();
            }

            @Override
            public U get() throws InterruptedException, ExecutionException {
                return mapper.apply(source.get());
            }

            @Override
            public U get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return mapper.apply(source.get(timeout, unit));
            }
            
        };
    }
    
    private static void exercise21() {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future<Long> longFuture = exec.submit(() -> { Thread.sleep(1000L); return System.currentTimeMillis(); } );
        try {
            Date time = map(longFuture, Date::new).get();
            System.out.printf("1 second ago time was: %s\n", time);
        } catch (Exception e) {
            System.out.printf("Error: %s\n", e.getMessage());
        }
    }

    /**
     * Exercise 22.
     * Is there a flatMap operation for CompletableFuture? If so, what is it?
     */
    private static void exercise22() {
        /*
         * It is method thenCompose 
         */
        CompletableFuture<Long> longFuture = CompletableFuture.supplyAsync(() -> System.currentTimeMillis());
        CompletableFuture<Date> timeFuture = longFuture.thenCompose(t -> CompletableFuture.supplyAsync(() -> new Date(t)));
        try {
            System.out.printf("Time is: %s\n", timeFuture.get());
        } catch (Exception e) {
            System.out.printf("Error: %s\n", e.getMessage());
        }
    }

    /**
     * Exercise 23.
     * Define a map operation for a class Pair<T> that represents a pair of objects of
     * type T.
     */
    private static class Pair<T> {
        private T first;
        private T second;
        
        public Pair(T first, T second) {
            this.first = first;
            this.second = second;
        }
        
        public <R> Pair<R> map(Function<? super T, ? extends R> mapper) {
            return new Pair<>(mapper.apply(first), mapper.apply(second));
        }
        
        @Override
        public String toString() {
            return String.format("a pair of %s and %s", first, second);
        }
    }
    
    private static void exercise23() {
        Pair<Integer> pair = new Pair<>(1, 2);
        System.out.printf("It was %s. But now it is %s\n", pair, pair.map(Double::valueOf));
    }

    /**
     * Exercise 24.
     * Can you define a flatMap method for Pair<T>? If so, what is it? If not, why not?
     */
    private static class Pair2<T> extends Pair<T> {
        public Pair2(T first, T second) {
            super(first, second);
        }
        
        public <R> Pair2<R> flatMap(BiFunction<? super T, ? super T, ? extends Pair2<R>> mapper) {
            return mapper.apply(super.first, super.second);
        }
    }
    
    private static void exercise24() {
        Pair2<Integer> pair = new Pair2<>(2, 1);
        System.out.printf("It was %s. But now it is %s\n", pair, pair.flatMap((x, y) -> new Pair2<>(Math.min(x, y), Math.max(x, y))));
    }

    public static void main(String[] args) throws Exception {
        System.out.println("*** Exercise 1 ***\nTask: " +
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
