import java.util.Scanner;

public class Main {

    private enum Role { STUDENT, MANAGER }

    private static final String STUDENT_EMAIL = "student@utdallas.edu";
    private static final String STUDENT_PASSWORD = "Password123";

    private static final String STUDENT2_EMAIL = "student2@utdallas.edu";
    private static final String STUDENT2_PASSWORD = "Password789";

    private static final String MANAGER_EMAIL = "manager@utdallas.edu";
    private static final String MANAGER_PASSWORD = "Password456";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=====================================");
        System.out.println("   STUDY ROOM RESERVATION SYSTEM");
        System.out.println("=====================================");

        Role role = login(scanner);

        if (role == null) {
            System.out.println("No input received. Exiting.");
        } else if (role == Role.STUDENT) {
            System.out.println("\nLogin successful! Welcome, student.");
            showStudentMenu();
        } else {
            System.out.println("\nLogin successful! Welcome, manager.");
            showManagerMenu();
        }
        scanner.close();
    }

    private static Role authenticate(String email, String password) {
        if (email == null || password == null) {
            return null;
        }
        email = email.trim();
        if (email.equalsIgnoreCase(STUDENT_EMAIL) && password.equals(STUDENT_PASSWORD)) {
            return Role.STUDENT;
        }
        if (email.equalsIgnoreCase(STUDENT2_EMAIL) && password.equals(STUDENT2_PASSWORD)) {
            return Role.STUDENT;
        }
        if (email.equalsIgnoreCase(MANAGER_EMAIL) && password.equals(MANAGER_PASSWORD)) {
            return Role.MANAGER;
        }
        return null;
    }

    private static Role login(Scanner scanner) {
        while (true) {
            System.out.print("Enter your UTD email: ");
            if (!scanner.hasNextLine()) return null;
            String email = scanner.nextLine();

            System.out.print("Enter your password: ");
            if (!scanner.hasNextLine()) return null;
            String password = scanner.nextLine();

            Role role = authenticate(email, password);
            if (role != null) {
                return role;
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

    private static void showStudentMenu() {
        showMenu("STUDENT MENU", new String[] {
            "Search available rooms",
            "Reserve a room",
            "View my reservations",
            "Cancel a reservation"
        });
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
