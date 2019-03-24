import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ExercisesChapter5 {
    private static final int CURRECT_YEAR = LocalDate.now().getYear();
    private static final LocalDate TODAY = LocalDate.now();
    private static final Instant NOW = Instant.now();
    private static final DateTimeFormatter D_FORMATTER = DateTimeFormatter.ofPattern("E yyyy-MM-dd");
    private static final DateTimeFormatter T_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    /**
     * Exercise 1.
     * Compute Programmer’s Day without using plusDays.
     */
    private static void exercise01() {
        System.out.println("Result:");
        // LocalDate programmersDay = LocalDate.of(CURRECT_YEAR, 1, 1).plusDays(255); // with plusDays
        LocalDate programmersDay = LocalDate.ofYearDay(CURRECT_YEAR, 256); // without plusDays
        System.out.printf("Next Programmer’s Day is: %s\n", programmersDay.format(D_FORMATTER));
    }

    /**
     * Exercise 2.
     * What happens when you add one year to LocalDate.of(2000, 2, 29)? Four years?
     * Four times one year?
     */
    private static void exercise02() {
        System.out.println("Result:");
        LocalDate source = LocalDate.of(2000, 2, 29); // 2000-2-29
        LocalDate sourcePlusOneYear = source.plusYears(1); // 2001-2-28 because 2001 was not a leap year
        LocalDate sourcePlusFourYears = source.plusYears(4); // 2004-2-29 because 2004 was a leap year
        LocalDate sourcePlusOneYearFourTimes = source.plusYears(1).plusYears(1).plusYears(1).plusYears(1); // 2004-2-28 because 2001 was not a leap year
        System.out.printf("%s + one year = %s\n", source.format(D_FORMATTER), sourcePlusOneYear.format(D_FORMATTER)); // plus one year
        System.out.printf("%s + four years = %s\n", source.format(D_FORMATTER), sourcePlusFourYears.format(D_FORMATTER)); // plus one year
        System.out.printf("%s + one year four times = %s\n", source.format(D_FORMATTER), sourcePlusOneYearFourTimes.format(D_FORMATTER)); // plus one year four times
    }
    
    /**
     * Exercise 3.
     * Implement a method next that takes a Predicate<LocalDate> and returns an
     * adjuster yielding the next date fulfilling the predicate. For example,
     *     today.with(next(w -> getDayOfWeek().getValue() < 6))
     * computes the next workday.
     */
    private static TemporalAdjuster next(Predicate<LocalDate> rule) {
        return TemporalAdjusters.ofDateAdjuster(w -> {
            LocalDate result = w;
            do {
                result = result.plusDays(1);
            } while (!rule.test(result));
            return result;
        });
    }
    
    private static void exercise03() {
        System.out.println("Result:");
        System.out.printf("Next workday is: %s\n", TODAY.with(next(w -> w.getDayOfWeek().getValue() < 6)).format(D_FORMATTER));
        System.out.printf("Next Sunday is: %s\n", TODAY.with(next(w -> w.getDayOfWeek() == DayOfWeek.SUNDAY)).format(D_FORMATTER));
    }
    
    /**
     * Exercise 4.
     * Write an equivalent of the Unix cal program that displays a calendar for a
     * month. For example, java Cal 3 2013 should display
     *                  1  2  3
     *      4  5  6  7  8  9 10
     *     11 12 13 14 15 16 17
     *     18 19 20 21 22 23 24
     *     25 26 27 28 29 30 31
     * indicating that March 1 is a Friday. (Show the weekend at the end of
     * the week.)
     */
    private static class Cal {
        private static void usage() {
            System.err.println("Usage: Cal month year\nExample: Cal 3 2013");
            System.exit(1);
        }
        
        public static void main(String[] args) {
            try {
                int month = Integer.parseInt(args[0]);
                int year = Integer.parseInt(args[1]);
                LocalDate firstDay = LocalDate.of(year, month, 1); // first day of month
                int firstDayOfWeek = firstDay.getDayOfWeek().getValue(); // first day of month as day of week
                Stream.generate(() -> "   ") // generating gap for first day of month
                      .limit(firstDayOfWeek - 1)
                      .forEach(System.out::print);
                int lengthOfMonth = firstDay.lengthOfMonth();
                IntStream.iterate(1, x -> x + 1) // generating all days of month
                         .limit(lengthOfMonth)
                         .mapToObj(day -> { // converting to string
                             String result = String.valueOf(day);
                             if (day < 10) result = " " + result;
                             boolean isLast = day == lengthOfMonth;
                             boolean isSunday = (firstDayOfWeek + day - 1) % 7 == 0;
                             return result + (isLast ? "\r\n" : (isSunday ? "\r\n" : " "));
                         })
                         .forEach(System.out::print); // printing
            } catch(Exception e) {
                usage();
            }
        }
    }
    
    private static void exercise04() {
        System.out.println("Result:");
        Cal.main(new String[] {"3", "2013"});
    }
    
    /**
     * Exercise 5.
     * Write a program that prints how many days you have been alive.
     */
    private static void exercise05() {
        System.out.println("Result:");
        LocalDate birthday = LocalDate.of(1986, Month.JULY, 7);
        long daysOfMyLife = ChronoUnit.DAYS.between(birthday, TODAY);
        System.out.printf("I'm alive for %d days\n", daysOfMyLife);
        /*
         * OMG. 2019-05-14 will be my 12000th day on the Earth! I really need to alive to this date :) 
         */
    }
    
    /**
     * Exercise 6.
     * List all Friday the 13th in the twentieth century.
     */
    private static void exercise06() {
        System.out.println("Result:");
        LocalDate jasonTime = LocalDate.of(1900, Month.JANUARY, 1)
                                       .with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        LocalDate gameOver = LocalDate.of(2000, Month.JANUARY, 1);
        for (int counter = 0; jasonTime.isBefore(gameOver); jasonTime = jasonTime.plusWeeks(1L)) {
            if (jasonTime.getDayOfMonth() == 13)
                System.out.printf("%d: %s\n", ++counter, jasonTime.format(D_FORMATTER));
        }
    }
    
    /**
     * Exercise 7.
     * Implement a TimeInterval class that represents an interval of time, suitable for
     * calendar events (such as a meeting on a given date from 10:00 to 11:00).
     * Provide a method to check whether two intervals overlap.
     */
    private static class TimeInterval {
        private ZonedDateTime startTime;
        private ZonedDateTime endTime;
        
        public TimeInterval(ZonedDateTime startTime, ZonedDateTime endTime) {
            if (startTime.isAfter(endTime)) throw new IllegalArgumentException("startTime > endTime");
            this.startTime = startTime;
            this.endTime = endTime;
        }
        
        public boolean isOverlap(TimeInterval another) {
            TimeInterval first, second;
            if (startTime.isBefore(another.startTime)) {
                first = this;
                second = another;
            } else {
                first = another;
                second = this;
            }
            return first.endTime.isAfter(second.startTime);
        }
        
        @Override
        public String toString() {
            boolean isSameDate = startTime.toLocalDate().isEqual(endTime.toLocalDate());
            return String.format("{%s-%s}", startTime.format(DT_FORMATTER), endTime.format(isSameDate ? T_FORMATTER : DT_FORMATTER));
        }
    }
    
    private static void exercise07() {
        System.out.println("Result:");
        TimeInterval first = new TimeInterval(ZonedDateTime.of(TODAY, LocalTime.of(10, 0), ZoneId.of("GMT+3")),
                                              ZonedDateTime.of(TODAY, LocalTime.of(11, 0), ZoneId.of("GMT+3")));
        TimeInterval second = new TimeInterval(ZonedDateTime.of(TODAY, LocalTime.of(10, 30), ZoneId.of("GMT+3")),
                                               ZonedDateTime.of(TODAY, LocalTime.of(12, 0), ZoneId.of("GMT+3")));
        TimeInterval third = new TimeInterval(ZonedDateTime.of(TODAY, LocalTime.of(11, 30), ZoneId.of("GMT+3")),
                                               ZonedDateTime.of(TODAY, LocalTime.of(12, 0), ZoneId.of("GMT+3")));
        System.out.printf("%s and %s is %soverlapped\n", first, second, first.isOverlap(second) ? "" : "not ");
        System.out.printf("%s and %s is %soverlapped\n", first, third, first.isOverlap(third) ? "" : "not ");
    }
    
    /**
     * Exercise 8.
     * Obtain the offsets of today’s date in all supported time zones for the current
     * time instant, turning ZoneId.getAvailableIds into a stream and using stream
     * operations.
     */
    private static void exercise08() {
        System.out.println("Result:");
        ZoneId.getAvailableZoneIds().stream()
                                    .peek(zId -> System.out.printf("Offset for %s = ", zId))
                                    .map(zId -> NOW.atZone(ZoneId.of(zId)).getOffset())
                                    .forEach(System.out::println);
    }
    
    /**
     * Exercise 9.
     * Again using stream operations, find all time zones whose offsets aren’t full
     * hours.
     */
    private static class Container {
        private final String id;
        private final ZoneOffset offset;
        
        public Container(String id, ZoneOffset offset) {
            this.id = id;
            this.offset = offset;
        }

        public String getId() {
            return id;
        }

        public ZoneOffset getOffset() {
            return offset;
        }
    }
    
    private static void exercise09() {
        System.out.println("Result:");
        ZoneId.getAvailableZoneIds().stream()
                                    .map(zId -> new Container(zId, NOW.atZone(ZoneId.of(zId)).getOffset()))
                                    .filter(c -> c.getOffset().getTotalSeconds() % 3600 != 0L) // I haven't found better solution :(
                                    .forEach(c -> System.out.printf("Offset for %s = %s\n", c.getId(), c.getOffset()));
    }
    
    /**
     * Exercise 10.
     * Your flight from Los Angeles to Frankfurt leaves at 3:05 pm local time and
     * takes 10 hours and 50 minutes. When does it arrive? Write a program that
     * can handle calculations like this.
     */
    private static class FlightCalculator {
        protected LocalDate fromDate;
        protected LocalTime fromTime;
        protected String fromZone;
        protected LocalDate toDate;
        protected LocalTime toTime;
        protected String toZone;
        protected Duration flightDuration;
        
        public FlightCalculator fromDate(LocalDate fromDate) {
            this.fromDate = fromDate;
            return this;
        }
        
        public FlightCalculator fromTime(LocalTime fromTime) {
            this.fromTime = fromTime;
            return this;
        }
        
        public FlightCalculator fromTime(int hour, int minute) {
            return fromTime(LocalTime.of(hour, minute));
        }
        
        public FlightCalculator fromZone(String fromZone) {
            this.fromZone = fromZone;
            return this;
        }
        
        public FlightCalculator toDate(LocalDate toDate) {
            this.toDate = toDate;
            return this;
        }
        
        public FlightCalculator toTime(LocalTime toTime) {
            this.toTime = toTime;
            return this;
        }
        
        public FlightCalculator toTime(int hour, int minute) {
            return toTime(LocalTime.of(hour, minute));
        }
        
        public FlightCalculator toZone(String toZone) {
            this.toZone = toZone;
            return this;
        }
        
        public FlightCalculator duration(Duration flightDuration) {
            this.flightDuration = flightDuration;
            return this;
        }
        
        public FlightCalculator duration(long hours, long minutes) {
            return duration(Duration.ofHours(hours).plus(Duration.ofMinutes(minutes)));
        }
        
        public LocalDateTime calculateArrival() {
            return ZonedDateTime.of(LocalDateTime.of(fromDate, fromTime), ZoneId.of(fromZone))
                                .withZoneSameInstant(ZoneId.of(toZone))
                                .plus(flightDuration)
                                .toLocalDateTime();
        }
        
        public Duration calculateFlightDuration() {
            return Duration.between(ZonedDateTime.of(LocalDateTime.of(fromDate, fromTime), ZoneId.of(fromZone)).toInstant(),
                                    ZonedDateTime.of(LocalDateTime.of(toDate, toTime), ZoneId.of(toZone)).toInstant());
        }
    }
    
    private static void exercise10() {
        System.out.println("Result:");
        LocalDateTime arrival = new FlightCalculator().fromDate(TODAY)
                                                      .fromTime(15, 5)
                                                      .fromZone("America/Los_Angeles")
                                                      .toZone("CET")
                                                      .duration(10, 50)
                                                      .calculateArrival();
        System.out.printf("Arrival local time: %s\n", arrival.format(DT_FORMATTER));
    }
    
    /**
     * Exercise 11.
     * Your return flight leaves Frankfurt at 14:05 and arrives in Los Angeles at
     * 16:40. How long is the flight? Write a program that can handle calculations
     * like this.
     */
    private static String durationToString(Duration duration) {
        return LocalTime.MIDNIGHT.plus(duration).format(T_FORMATTER);
    }
    
    private static void exercise11() {
        System.out.println("Result:");
        Duration duration = new FlightCalculator().fromDate(TODAY)
                                                  .fromTime(14, 5)
                                                  .fromZone("CET")
                                                  .toDate(TODAY)
                                                  .toTime(16, 40)
                                                  .toZone("America/Los_Angeles")
                                                  .calculateFlightDuration();
        System.out.printf("Flight time: %s\n", durationToString(duration));
    }
    
    /**
     * Exercise 12.
     * Write a program that solves the problem described at the beginning of
     * Section 5.5, “Zoned Time,” on page 109. Read a set of appointments in different
     * time zones and alert the user which ones are due within the next hour in
     * local time.
     */
    private static class Appointment {
        public final LocalTime time;
        public final String zone;
        public final String message;
        
        public Appointment(int hour, int minute, String zone, String message) {
            this.time = LocalTime.of(hour, minute);
            this.zone = zone;
            this.message = message;
        }
        
        public boolean isDueWithinNextHour(int atHour, int atMinute, String atZone) {
            ZonedDateTime appointmentTime = ZonedDateTime.of(TODAY, time, ZoneId.of(zone));
            ZonedDateTime appointmentLocalTime = ZonedDateTime.of(TODAY, LocalTime.of(atHour, atMinute), ZoneId.of(atZone));
            long diff = Duration.between(appointmentLocalTime, appointmentTime).toMinutes();
            return  0L < diff && diff <= 60L;
        }
        
        @Override
        public String toString() {
            return String.format("%s %s (%s)", time.format(T_FORMATTER), zone, message);
        }
    }
    
    private static void alert(Set<Appointment> appointments, int hour, int minute, String zone) {
        appointments.stream().filter(a -> a.isDueWithinNextHour(hour, minute, zone))
                             .forEach(System.out::println);
    }
    
    private static void exercise12() {
        System.out.println("Result:");
        alert(Stream.of(new Appointment(7, 0, "GMT+3", "Breakfast"),
                        new Appointment(14, 0, "GMT+3", "Lunch"),
                        new Appointment(19, 0, "GMT+3", "Dinner"),
                        new Appointment(12, 35, "CET", "Working meeting"),
                        new Appointment(17, 20, "America/Los_Angeles", "Favourite TV show"))
                    .collect(Collectors.toSet()), 13, 45, "GMT+3");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("*** Exercise 1 ***\nTask: " +
            "Compute Programmer’s Day without using plusDays.");
        exercise01();
        System.out.println("\n*** Exercise 2 ***\nTask: " +
            "What happens when you add one year to LocalDate.of(2000, 2, 29)? Four years?\r\n" + 
            "Four times one year?");
        exercise02();
        System.out.println("\n*** Exercise 3 ***\nTask: " +
            "Implement a method next that takes a Predicate<LocalDate> and returns an\r\n" + 
            "adjuster yielding the next date fulfilling the predicate. For example,\r\n" + 
            "    today.with(next(w -> getDayOfWeek().getValue() < 6))\r\n" + 
            "computes the next workday.");
        exercise03();
        System.out.println("\n*** Exercise 4 ***\nTask: " +
            "Write an equivalent of the Unix cal program that displays a calendar for a\r\n" + 
            "month. For example, java Cal 3 2013 should display\r\n" +
            "                 1  2  3\r\n" + 
            "     4  5  6  7  8  9 10\r\n" + 
            "    11 12 13 14 15 16 17\r\n" + 
            "    18 19 20 21 22 23 24\r\n" + 
            "    25 26 27 28 29 30 31\r\n" +
            "indicating that March 1 is a Friday. (Show the weekend at the end of\r\n" + 
            "the week.)");
        exercise04();
        System.out.println("\n*** Exercise 5 ***\nTask: " +
            "Write a program that prints how many days you have been alive.");
        exercise05();
        System.out.println("\n*** Exercise 6 ***\nTask: " +
            "List all Friday the 13th in the twentieth century.");
        exercise06();
        System.out.println("\n*** Exercise 7 ***\nTask: " +
            "Implement a TimeInterval class that represents an interval of time, suitable for\r\n" + 
            "calendar events (such as a meeting on a given date from 10:00 to 11:00).\r\n" + 
            "Provide a method to check whether two intervals overlap.");
        exercise07();
        System.out.println("\n*** Exercise 8 ***\nTask: " +
            "Obtain the offsets of today’s date in all supported time zones for the current\r\n" + 
            "time instant, turning ZoneId.getAvailableIds into a stream and using stream\r\n" + 
            "operations.");
        exercise08();
        System.out.println("\n*** Exercise 9 ***\nTask: " +
            "Again using stream operations, find all time zones whose offsets aren’t full\r\n" + 
            "hours.");
        exercise09();
        System.out.println("\n*** Exercise 10 ***\nTask: " +
            "Your flight from Los Angeles to Frankfurt leaves at 3:05 pm local time and\r\n" + 
            "takes 10 hours and 50 minutes. When does it arrive? Write a program that\r\n" + 
            "can handle calculations like this.");
        exercise10();
        System.out.println("\n*** Exercise 11 ***\nTask: " +
            "Your return flight leaves Frankfurt at 14:05 and arrives in Los Angeles at\r\n" + 
            "16:40. How long is the flight? Write a program that can handle calculations\r\n" + 
            "like this.");
        exercise11();
        System.out.println("\n*** Exercise 12 ***\nTask: " +
            "Write a program that solves the problem described at the beginning of\r\n" + 
            "Section 5.5, “Zoned Time,” on page 109. Read a set of appointments in different\r\n" +
            "time zones and alert the user which ones are due within the next hour in\r\n" + 
            "local time.");
        exercise12();
    }
}
