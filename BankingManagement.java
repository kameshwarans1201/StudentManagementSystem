import java.sql.*;
import java.util.Scanner;

public class BankingManagement {

    static final String URL = "jdbc:mysql://localhost:3306/bankdb";
    static final String USER = "root";
    String password = "YOUR_MYSQL_PASSWORD";

    static Scanner sc = new Scanner(System.in);

    // Create Account
    static void createAccount() {
        System.out.print("Enter Account Holder Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Initial Deposit: ");
        double balance = sc.nextDouble();
        sc.nextLine();

        if (name.trim().isEmpty() || balance < 0) {
            System.out.println("Invalid details!");
            return;
        }

        String sql = "INSERT INTO accounts (name, balance) VALUES (?, ?)";

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setDouble(2, balance);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    System.out.println("Account Created Successfully!");
                    System.out.println("Account Number: " + rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Deposit Money
    static void deposit() {
        System.out.print("Enter Account Number: ");
        int accNo = sc.nextInt();

        System.out.print("Enter Deposit Amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        if (amount <= 0) {
            System.out.println("Amount must be greater than zero!");
            return;
        }

        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_no = ?";

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setInt(2, accNo);

            if (ps.executeUpdate() > 0) {
                System.out.println("Deposit Successful!");
            } else {
                System.out.println("Account Not Found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Withdraw Money
    static void withdraw() {
        System.out.print("Enter Account Number: ");
        int accNo = sc.nextInt();

        System.out.print("Enter Withdrawal Amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        if (amount <= 0) {
            System.out.println("Amount must be greater than zero!");
            return;
        }

        String sql = "UPDATE accounts SET balance = balance - ? " +
                     "WHERE account_no = ? AND balance >= ?";

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setInt(2, accNo);
            ps.setDouble(3, amount);

            if (ps.executeUpdate() > 0) {
                System.out.println("Withdrawal Successful!");
            } else {
                System.out.println("Account Not Found or Insufficient Balance!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Check Balance
    static void checkBalance() {
        System.out.print("Enter Account Number: ");
        int accNo = sc.nextInt();
        sc.nextLine();

        String sql = "SELECT name, balance FROM accounts WHERE account_no = ?";

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, accNo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Account Holder: " + rs.getString("name"));
                    System.out.printf("Balance: %.2f%n", rs.getDouble("balance"));
                } else {
                    System.out.println("Account Not Found!");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Display All Accounts
    static void displayAccounts() {
        String sql = "SELECT * FROM accounts";

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n--- All Bank Accounts ---");

            while (rs.next()) {
                System.out.println("-------------------------");
                System.out.println("Account Number: " + rs.getInt("account_no"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.printf("Balance: %.2f%n", rs.getDouble("balance"));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Main Method
    public static void main(String[] args) {

        while (true) {
            System.out.println("\n===== BANKING MANAGEMENT SYSTEM =====");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Check Balance");
            System.out.println("5. Display All Accounts");
            System.out.println("6. Exit");

            System.out.print("Enter Your Choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    createAccount();
                    break;

                case 2:
                    deposit();
                    break;

                case 3:
                    withdraw();
                    break;

                case 4:
                    checkBalance();
                    break;

                case 5:
                    displayAccounts();
                    break;

                case 6:
                    System.out.println("Thank You!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }
}