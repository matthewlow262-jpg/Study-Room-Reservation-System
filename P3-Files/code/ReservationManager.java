
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationManager {

    public static class Reservation {
        public final int id;
        public final String studentEmail;
        public final int roomId;
        public final LocalDateTime start;
        public final LocalDateTime end;
        public boolean cancelled;

        public Reservation(int id, String studentEmail, int roomId,
                           LocalDateTime start, LocalDateTime end) {
            this.id = id;
            this.studentEmail = studentEmail;
            this.roomId = roomId;
            this.start = start;
            this.end = end;
            this.cancelled = false;
        }

        @Override
        public String toString() {
            return "Reservation #" + id
                    + " | Room " + roomId
                    + " | " + start
                    + " to " + end
                    + " | " + (cancelled ? "CANCELLED" : "ACTIVE");
        }
    }

    private final List<Reservation> reservations = new ArrayList<>();
    private int nextId = 1;

    public synchronized Reservation reserve(
            String studentEmail, int roomId,
            LocalDateTime start, LocalDateTime end) {

        if (studentEmail == null || studentEmail.isBlank()) {
            throw new IllegalArgumentException("Student email is required.");
        }

        if (roomId < 1 || roomId > 6) {
            throw new IllegalArgumentException("Room must be between 1 and 6.");
        }

        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("Invalid reservation time.");
        }

        if (!start.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reservation must be in the future.");
        }

        for (Reservation existing : reservations) {
            if (!existing.cancelled
                    && existing.roomId == roomId
                    && start.isBefore(existing.end)
                    && end.isAfter(existing.start)) {
                throw new IllegalStateException(
                        "Room is already reserved during this time.");
            }
        }

        Reservation reservation = new Reservation(
                nextId++, studentEmail.trim().toLowerCase(),
                roomId, start, end);

        reservations.add(reservation);
        return reservation;
    }

    public synchronized List<Reservation> getReservations(String studentEmail) {
        if (studentEmail == null || studentEmail.isBlank()) {
            throw new IllegalArgumentException("Student email is required.");
        }

        List<Reservation> result = new ArrayList<>();

        for (Reservation reservation : reservations) {
            if (reservation.studentEmail.equalsIgnoreCase(studentEmail.trim())) {
                result.add(reservation);
            }
        }

        return result;
    }

    public synchronized boolean cancel(int reservationId, String studentEmail) {
        if (studentEmail == null || studentEmail.isBlank()) {
            return false;
        }

        for (Reservation reservation : reservations) {
            if (reservation.id == reservationId
                    && reservation.studentEmail.equalsIgnoreCase(studentEmail.trim())
                    && !reservation.cancelled) {
                reservation.cancelled = true;
                return true;
            }
        }

        return false;
    }

    // Checks whether a room is available during a requested time range.
    // Cancelled reservations do not block availability.
    public synchronized boolean isRoomAvailable(
            int roomId, LocalDateTime start, LocalDateTime end) {

        if (roomId < 1 || roomId > 6) {
            throw new IllegalArgumentException("Room must be between 1 and 6.");
        }

        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("Invalid time range.");
        }

        if (!start.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start time must be in the future.");
        }

        for (Reservation existing : reservations) {
            if (!existing.cancelled
                    && existing.roomId == roomId
                    && start.isBefore(existing.end)
                    && end.isAfter(existing.start)) {
                return false;
            }
        }

        return true;
    }
}
