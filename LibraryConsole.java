import java.util.List;
import java.util.Scanner;

public class LibraryConsole {
    public static void main(String[] args) {
        Archive archive = new Archive("catalogue.txt", "registry.txt");
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== City Library Digital Management System ===");
            System.out.println("1) Add Book");
            System.out.println("2) Add Member");
            System.out.println("3) Issue Book");
            System.out.println("4) Return Book");
            System.out.println("5) Search Books");
            System.out.println("6) Sort Books");
            System.out.println("7) View Categories");
            System.out.println("8) Save & Exit");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        System.out.print("Title: "); String title = sc.nextLine().trim();
                        System.out.print("Author: "); String author = sc.nextLine().trim();
                        System.out.print("Category: "); String cat = sc.nextLine().trim();
                        int bid = archive.addBook(title, author, cat);
                        System.out.println("Book added successfully with ID: " + bid);
                        break;

                    case "2":
                        System.out.print("Member Name: "); String name = sc.nextLine().trim();
                        System.out.print("Email: "); String email = sc.nextLine().trim();
                        int mid = archive.addMember(name, email);
                        System.out.println("Member added successfully with ID: " + mid);
                        break;

                    case "3":
                        System.out.print("Book ID to issue: "); int issueBid = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Member ID: "); int issueMid = Integer.parseInt(sc.nextLine().trim());
                        boolean issued = archive.issueBook(issueBid, issueMid);
                        System.out.println(issued ? "Book issued." : "Book is already issued.");
                        break;

                    case "4":
                        System.out.print("Book ID to return: "); int retBid = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Member ID: "); int retMid = Integer.parseInt(sc.nextLine().trim());
                        boolean returned = archive.returnBook(retBid, retMid);
                        System.out.println(returned ? "Book returned." : "This member did not have that book.");
                        break;

                    case "5":
                        System.out.println("Search by: 1) Title 2) Author 3) Category");
                        String sOpt = sc.nextLine().trim();
                        System.out.print("Enter search text: ");
                        String q = sc.nextLine().trim();
                        List<Tome> results;
                        if ("1".equals(sOpt)) results = archive.searchBooksByTitle(q);
                        else if ("2".equals(sOpt)) results = archive.searchBooksByAuthor(q);
                        else results = archive.searchBooksByCategory(q);
                        if (results.isEmpty()) System.out.println("No books found.");
                        else results.forEach(System.out::println);
                        break;

                    case "6":
                        System.out.println("Sort by: 1) Title 2) Author 3) Category");
                        String so = sc.nextLine().trim();
                        List<Tome> sorted;
                        if ("2".equals(so)) sorted = archive.sortBooksByAuthor();
                        else if ("3".equals(so)) sorted = archive.sortBooksByCategory();
                        else sorted = archive.sortBooksByTitle();
                        sorted.forEach(System.out::println);
                        break;

                    case "7":
                        System.out.println("Categories:");
                        archive.getCategories().forEach(System.out::println);
                        break;

                    case "8":
                        archive.saveAll();
                        System.out.println("Saved. Exiting.");
                        running = false;
                        break;

                    default:
                        System.out.println("Invalid option.");
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid number format.");
            } catch (IllegalArgumentException iae) {
                System.out.println("Input error: " + iae.getMessage());
            } catch (Exception ex) {
                System.out.println("Unexpected error: " + ex.getMessage());
            }
        }

        sc.close();
    }
}
