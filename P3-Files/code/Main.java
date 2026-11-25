/******************************************************************************

Welcome to GDB Online.
GDB online is an online compiler and debugger tool for C, C++, Python, Java, PHP, Ruby, Perl,
C#, OCaml, VB, Swift, Pascal, Fortran, Haskell, Objective-C, Assembly, HTML, CSS, JS, SQLite, Prolog.
Code, Compile, Run and Debug online from anywhere in world.

*******************************************************************************/
import java.util.Scanner;

public class Main
{
    
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
}
