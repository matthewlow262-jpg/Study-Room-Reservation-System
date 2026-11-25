/******************************************************************************

we are going to show all rooms
we are going to have 6


*******************************************************************************/
import java.util.Scanner;

public class Main {
    
    static class Room {
        final String id;
        int capacity;

        Room(String id, int capacity) {
            this.id = id;
            this.capacity = capacity;
        }
    }
    
    static class Reservation {
        int roomId;
        String date;
        int startTime;
        int endTime;
        String owner;

        Reservation(int roomId, String date, int start, int end, String owner) {
            this.roomId = roomId;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.owner = owner;
        }
    }
    
    
    
    private static final String username1 = "student@utdallas.edu";
    private static final String password1 = "Password123";
    
    private static final String username2 = "manager@utdallas.edu";
    private static final String password2 = "Password456";
    
    private static final int MAX_ATTEMPTS = 3;
    
	public static void main(String[] args) {
	    
	    Scanner scanner = new Scanner(System.in);
	    
	    
        System.out.println("=====================================");
        System.out.println("   STUDY ROOM RESERVATION SYSTEM");
        System.out.println("=====================================");
        
        String loggedInEmail = login(scanner);
        
        if (loggedInEmail == null) {
            System.out.println("Too many failed attempts. exiting.");
        } else {
            System.out.println("\nLogin successful! Welcome, " + loggedInEmail);
            showMenu(scanner, loggedInEmail);
        }
        scanner.close();
        
        
	}


private static String login(Scanner scanner) {
    
    int remaining = MAX_ATTEMPTS;
    
    for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
        	    
        	    System.out.print("Enter your UTD email: ");
                String email = scanner.nextLine();
        
                System.out.print("Enter your password: ");
                String password = scanner.nextLine();	
                
                if ((email.equals(username1) && password.equals(password1)) || (email.equals(username2) && password.equals(password2))) {
                    return email;
                }
                
                remaining--;
                
                if (remaining > 0) {
                    System.out.println("Invalid username or password. " + remaining + " attempts left.");
                }
        
        }
        return null;
        
}

private static void showMenu(Scanner scanner, String email) {
    boolean loggedIn = true;
    while (loggedIn) {
        System.out.println("\n---MENU---");
        System.out.println("1. View your reservations");
        System.out.println("2. Make a reservation");
        System.out.println("3. Cancel a reservation");
        System.out.println("0. Logout");
        System.out.print("Choose an option: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                System.out.println("show rooms");
                break;
            case "2":
                System.out.println("make a reservation");
                break;
            case "3":
                System.out.println("cancel a reservation");
                break;
            case "0":
                System.out.println("Logged out.");
                loggedIn = false;
                break;
            default:
                    System.out.println("Invalid option.");
            }
    }
}
}
