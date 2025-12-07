package libManSys;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Random;

public class DataGenerator {

    // Data for Books
    private static final String[] BOOK_NAME_PREFIX = {"The", "A", "My", "An", "Some"};
    private static final String[] BOOK_NAME_ADJECTIVE = {"Amazing", "Wonderful", "Great", "Incredible", "Fantastic", "Mysterious", "Lost", "Forgotten"};
    private static final String[] BOOK_NAME_NOUN = {"Adventures", "Journey", "Secret", "History", "Chronicles", "World", "Legacy", "Destiny"};
    private static final String[] BOOK_NAME_SUFFIX = {"of Dragons", "in Space", "of the Ancients", "on the Moon", "of the Future", "of the Past"};
    private static final String[] BOOK_CATEGORIES = {"Fantasy", "Science Fiction", "Mystery", "Thriller", "Romance", "History", "Biography"};

    // Data for Accounts
    private static final String[] AUTHOR_FIRST_NAME = {"John", "Jane", "Peter", "Susan", "Michael", "Emily", "David", "Sarah", "Chris", "Jessica"};
    private static final String[] AUTHOR_LAST_NAME = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez"};
    private static final String[] SEX = {"MALE", "FEMALE"};
    private static final String[] ADDRESS_CITY = {"New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia", "San Antonio", "San Diego"};
    
    public static void main(String[] args) {
        generateBooks(15);
        generateAccounts(10, "reader");
        generateAccounts(3, "librarian");
    }

    public static void generateBooks(int count) {
        DbConnect db = new DbConnect();
        db.connect();
        Random rand = new Random();

        for (int i = 0; i < count; i++) {
            String bookName = generateRandomElement(rand, BOOK_NAME_PREFIX) + " " + generateRandomElement(rand, BOOK_NAME_ADJECTIVE) + " " + generateRandomElement(rand, BOOK_NAME_NOUN) + " " + generateRandomElement(rand, BOOK_NAME_SUFFIX);
            String authorName = generateRandomElement(rand, AUTHOR_FIRST_NAME) + " " + generateRandomElement(rand, AUTHOR_LAST_NAME);
            String category = generateRandomElement(rand, BOOK_CATEGORIES);
            LocalDate issueDate = generateRandomDate(rand);

            try {
                String query = "INSERT INTO books (book_name, book_author, issue_date, book_category) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = db.con.prepareStatement(query);
                pst.setString(1, bookName);
                pst.setString(2, authorName);
                pst.setDate(3, Date.valueOf(issueDate));
                pst.setString(4, category);
                pst.executeUpdate();
                System.out.println("Inserted book: " + bookName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println(count + " books have been successfully inserted into the database.");
    }

    public static void generateAccounts(int count, String role) {
        DbConnect db = new DbConnect();
        db.connect();
        Random rand = new Random();

        for (int i = 0; i < count; i++) {
            String firstName = generateRandomElement(rand, AUTHOR_FIRST_NAME);
            String lastName = generateRandomElement(rand, AUTHOR_LAST_NAME);
            int age = 18 + rand.nextInt(53);
            String sex = generateRandomElement(rand, SEX);
            String contactNumber = "09" + String.format("%09d", rand.nextInt(1000000000));
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + rand.nextInt(100) + "@example.com";
            String address = (1 + rand.nextInt(999)) + " Main St, " + generateRandomElement(rand, ADDRESS_CITY);
            String password = "password" + rand.nextInt(100);

            try {
                String query = "INSERT INTO account (first_name, last_name, age, sex, contact_number, email, address, role, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pst = db.con.prepareStatement(query);
                pst.setString(1, firstName);
                pst.setString(2, lastName);
                pst.setInt(3, age);
                pst.setString(4, sex);
                pst.setString(5, contactNumber);
                pst.setString(6, email);
                pst.setString(7, address);
                pst.setString(8, role);
                pst.setString(9, password);
                pst.executeUpdate();
                System.out.println("Inserted " + role + ": " + firstName + " " + lastName);
            } catch (SQLException e) {
                // If email is not unique, we just skip it for simplicity
                if (e.getErrorCode() != 1062) { // 1062 is the error code for duplicate entry
                    e.printStackTrace();
                } else {
                    System.out.println("Skipped duplicate email: " + email);
                    i--; // retry generating
                }
            }
        }
        System.out.println(count + " " + role + "s have been successfully inserted into the database.");
    }
    
    private static String generateRandomElement(Random rand, String[] options) {
        return options[rand.nextInt(options.length)];
    }

    private static LocalDate generateRandomDate(Random rand) {
        long minDay = LocalDate.of(2000, 1, 1).toEpochDay();
        long maxDay = LocalDate.now().toEpochDay();
        long randomDay = minDay + rand.nextInt((int) (maxDay - minDay));
        return LocalDate.ofEpochDay(randomDay);
    }
}
