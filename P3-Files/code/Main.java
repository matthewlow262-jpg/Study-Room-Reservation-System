
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private enum Role { STUDENT, MANAGER }

    private static final String STUDENT_EMAIL = "student@utdallas.edu";
    private static final String STUDENT_PASSWORD = "Password123";

    private static final String STUDENT2_EMAIL = "student2@utdallas.edu";
    private static final String STUDENT2_PASSWORD = "Password789";

    private static final String MANAGER_EMAIL = "manager@utdallas.edu";
    private static final String MANAGER_PASSWORD = "Password456";

    private static final ReservationManager reservationManager =
            new ReservationManager();

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static class User {
        final String email;
        final Role role;

        User(String email, Role role) {
            this.email = email;
            this.role = role;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=====================================");
        System.out.println("   STUDY ROOM RESERVATION SYSTEM");
        System.out.println("=====================================");

        while (true) {
            User user = login(scanner);

            if (user == null) {
                System.out.println("No input received. Exiting.");
                break;
            }

            if (user.role == Role.STUDENT) {
                System.out.println("\nLogin successful! Welcome, student.");
                studentMenu(scanner, user.email);
            } else {
                System.out.println("\nLogin successful! Welcome, manager.");
                showManagerMenu();
                System.out.println("Manager functions are not connected yet.");
                break;
            }
        }

        scanner.close();
    }

    private static Role authenticate(String email, String password) {
        if (email == null || password == null) return null;

        email = email.trim();

        if (email.equalsIgnoreCase(STUDENT_EMAIL)
                && password.equals(STUDENT_PASSWORD)) {
            return Role.STUDENT;
        }

        if (email.equalsIgnoreCase(STUDENT2_EMAIL)
                && password.equals(STUDENT2_PASSWORD)) {
            return Role.STUDENT;
        }

        if (email.equalsIgnoreCase(MANAGER_EMAIL)
                && password.equals(MANAGER_PASSWORD)) {
            return Role.MANAGER;
        }

        return null;
    }

    private static User login(Scanner scanner) {
        while (true) {
            System.out.print("Enter your UTD email: ");
            if (!scanner.hasNextLine()) return null;
            String email = scanner.nextLine().trim();

            System.out.print("Enter your password: ");
            if (!scanner.hasNextLine()) return null;
            String password = scanner.nextLine();

            Role role = authenticate(email, password);

            if (role != null) {
                return new User(email.toLowerCase(), role);
            }

            System.out.println("Invalid username or password. Try again.");
        }
    }

    private static void showMenu(String title, String[] options) {
        int width = 37;
        String line = "=".repeat(width);

        System.out.println();
        System.out.println(line);
        System.out.println(center(title, width));
        System.out.println(line);

        for (int i = 0; i < options.length; i++) {
            System.out.println("  [" + (i + 1) + "] " + options[i]);
        }

        System.out.println("  [0] Log out");
        System.out.println(line);
    }

    private static String center(String text, int width) {
        int pad = Math.max(0, (width - text.length()) / 2);
        return " ".repeat(pad) + text;
    }

    private static void studentMenu(Scanner scanner, String email) {
        while (true) {
            showMenu("STUDENT MENU", new String[] {
                "Search available rooms",
                "Reserve a room",
                "View my reservations",
                "Cancel a reservation"
            });

            System.out.print("Choose an option: ");
            if (!scanner.hasNextLine()) return;

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("Room search is not connected yet.");
                    break;
                case "2":
                    reserveRoom(scanner, email);
                    break;
                case "3":
                    viewReservations(email);
                    break;
                case "4":
                    cancelReservation(scanner, email);
                    break;
                case "0":
                    System.out.println("Logged out.");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void reserveRoom(Scanner scanner, String email) {
        try {
            System.out.print("Enter room ID (1-6): ");
            int roomId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Start (yyyy-MM-dd HH:mm): ");
            LocalDateTime start = LocalDateTime.parse(
                    scanner.nextLine().trim(), DATE_FORMAT);

            System.out.print("End (yyyy-MM-dd HH:mm): ");
            LocalDateTime end = LocalDateTime.parse(
                    scanner.nextLine().trim(), DATE_FORMAT);

            ReservationManager.Reservation reservation =
                    reservationManager.reserve(email, roomId, start, end);

            System.out.println("Reservation confirmed!");
            System.out.println(reservation);

        } catch (NumberFormatException | DateTimeParseException e) {
            System.out.println("Invalid input format.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Reservation failed: " + e.getMessage());
        }
    }

    private static void viewReservations(String email) {
        List<ReservationManager.Reservation> reservations =
                reservationManager.getReservations(email);

        if (reservations.isEmpty()) {
            System.out.println("You have no reservations.");
            return;
        }

        System.out.println("\nYOUR RESERVATIONS");
        for (ReservationManager.Reservation reservation : reservations) {
            System.out.println(reservation);
        }
    }

    private static void cancelReservation(Scanner scanner, String email) {
        try {
            System.out.print("Enter reservation ID to cancel: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            if (reservationManager.cancel(id, email)) {
                System.out.println("Reservation cancelled successfully.");
            } else {
                System.out.println(
                        "Reservation not found or you do not own it.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid reservation ID.");
        }
    }

    private static void showManagerMenu() {
        showMenu("MANAGER MENU", new String[] {
            "Add a room",
            "Edit a room",
            "Remove a room",
            "View all reservations",
            "Usage reports"
        });
    }
}
