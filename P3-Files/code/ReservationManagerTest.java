
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ReservationManagerTest {

    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("PASS: " + name);
        } else {
            failed++;
            System.out.println("FAIL: " + name);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        LocalDateTime start = LocalDateTime.now()
                .plusDays(2)
                .withHour(14)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        LocalDateTime end = start.plusHours(1);

        ReservationManager manager = new ReservationManager();

        // T1: Valid booking
        ReservationManager.Reservation first =
                manager.reserve("student@utdallas.edu", 1, start, end);

        check("T1 valid booking",
                first != null && first.id == 1);

        // T2: Overlapping booking rejected
        boolean overlapRejected = false;
        try {
            manager.reserve("student2@utdallas.edu", 1,
                    start.plusMinutes(30), end.plusMinutes(30));
        } catch (IllegalStateException e) {
            overlapRejected = true;
        }
        check("T2 overlapping booking rejected", overlapRejected);

        // T3: Adjacent booking allowed
        ReservationManager.Reservation adjacent =
                manager.reserve("student2@utdallas.edu", 1,
                        end, end.plusHours(1));

        check("T3 adjacent booking allowed", adjacent != null);

        // T4: Same time, different room allowed
        ReservationManager.Reservation differentRoom =
                manager.reserve("student2@utdallas.edu", 2, start, end);

        check("T4 different room allowed", differentRoom != null);

        // T5: Owner can cancel
        boolean cancelled =
                manager.cancel(first.id, "student@utdallas.edu");

        check("T5 owner cancellation",
                cancelled && first.cancelled);

        // T6: Other student cannot cancel
        boolean unauthorized =
                manager.cancel(adjacent.id, "student@utdallas.edu");

        check("T6 unauthorized cancellation rejected",
                !unauthorized && !adjacent.cancelled);

        // T7: Invalid time rejected
        boolean invalidTimeRejected = false;
        try {
            manager.reserve("student@utdallas.edu", 3, end, start);
        } catch (IllegalArgumentException e) {
            invalidTimeRejected = true;
        }

        check("T7 invalid time rejected", invalidTimeRejected);

        // T8: Concurrent reservations
        ReservationManager concurrentManager = new ReservationManager();

        int threadCount = 20;
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch go = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        AtomicInteger successes = new AtomicInteger();
        AtomicInteger conflicts = new AtomicInteger();
        AtomicInteger errors = new AtomicInteger();

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int studentNumber = i;

            Thread thread = new Thread(() -> {
                ready.countDown();

                try {
                    go.await();

                    concurrentManager.reserve(
                            "student" + studentNumber + "@utdallas.edu",
                            4, start, end);

                    successes.incrementAndGet();

                } catch (IllegalStateException e) {
                    conflicts.incrementAndGet();

                } catch (Exception e) {
                    errors.incrementAndGet();

                } finally {
                    done.countDown();
                }
            });

            threads.add(thread);
            thread.start();
        }

        ready.await();
        go.countDown();
        done.await();

        check("T8 concurrent booking protection",
                successes.get() == 1
                        && conflicts.get() == 19
                        && errors.get() == 0);

        // T9: Empty room is available
        ReservationManager availabilityManager = new ReservationManager();

        check("T9 empty room available",
                availabilityManager.isRoomAvailable(1, start, end));

        // T10: Overlapping reservation blocks availability
        ReservationManager.Reservation availabilityBooking =
                availabilityManager.reserve(
                        "student@utdallas.edu", 1, start, end);

        check("T10 reserved room unavailable",
                !availabilityManager.isRoomAvailable(
                        1, start.plusMinutes(15), end));

        // T11: Cancellation restores availability
        availabilityManager.cancel(
                availabilityBooking.id, "student@utdallas.edu");

        check("T11 cancelled room available again",
                availabilityManager.isRoomAvailable(1, start, end));

        // T12: Invalid search range rejected
        boolean invalidSearchRejected = false;

        try {
            availabilityManager.isRoomAvailable(1, end, start);
        } catch (IllegalArgumentException e) {
            invalidSearchRejected = true;
        }

        check("T12 invalid search rejected", invalidSearchRejected);

        System.out.println();
        System.out.println("================================");
        System.out.println("Tests passed: " + passed);
        System.out.println("Tests failed: " + failed);
        System.out.println("================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
