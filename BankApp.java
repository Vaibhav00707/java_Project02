import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BankApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String[] THEMES = {"Classic", "Dark", "Light", "Professional"};
    private static String currentTheme = "Classic";
    private static List<BankAccount> accounts = new ArrayList<>();
    private static final int ADMIN_PIN = 9999;
    private static final String DATA_FILE = "bank_data.dat";

    public static void main(String[] args) {
        System.out.println("\n=== ENHANCED BANKING APPLICATION ===");
        
        // Load accounts from file at startup
        loadAccountsFromFile();
        
        // Add shutdown hook to save data when application closes
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveAccountsToFile();
            System.out.println("Data saved successfully.");
        }));
        
        while (true) {
            System.out.println("\n1. Login to existing account");
            System.out.println("2. Create new account");
            System.out.println("3. Exit");
            System.out.print("Select option: ");
            
            int mainChoice = getIntInput();
            
            switch (mainChoice) {
                case 1:
                    BankAccount currentAccount = login();
                    if (currentAccount != null) {
                        bankingMenu(currentAccount);
                    }
                    break;
                    
                case 2:
                    createAccount();
                    saveAccountsToFile(); // Save after creating account
                    break;
                    
                case 3:
                    saveAccountsToFile(); // Save before exiting
                    System.out.println("Exiting application. Goodbye!");
                    return;
                    
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    // Save accounts to file
    private static void saveAccountsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(accounts);
            System.out.println("Accounts data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving accounts data: " + e.getMessage());
        }
    }
    
    // Load accounts from file
    @SuppressWarnings("unchecked")
    private static void loadAccountsFromFile() {
        if (!Files.exists(Paths.get(DATA_FILE))) {
            System.out.println("No existing data file found. Starting with empty accounts.");
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            accounts = (List<BankAccount>) ois.readObject();
            System.out.println("Accounts data loaded successfully. Total accounts: " + accounts.size());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading accounts data: " + e.getMessage());
            accounts = new ArrayList<>();
        }
    }
    
    private static void createAccount() {
        System.out.println("\n=== CREATE NEW ACCOUNT ===");
        
        System.out.print("Enter your full name: ");
        String name = scanner.nextLine();
        
        String type = "";
        while (!type.equalsIgnoreCase("Savings") && !type.equalsIgnoreCase("Checking")) {
            System.out.print("Choose account type (Savings/Checking): ");
            type = scanner.nextLine();
            if (!type.equalsIgnoreCase("Savings") && !type.equalsIgnoreCase("Checking")) {
                System.out.println("Invalid account type. Please choose either Savings or Checking.");
            }
        }
        
        int pin = 0;
        while (true) {
            System.out.print("Set a 4-digit PIN: ");
            pin = getIntInput();
            if (pin >= 1000 && pin <= 9999) {
                break;
            } else {
                System.out.println("PIN must be a 4-digit number.");
            }
        }
        
        double initialDeposit = 0;
        while (true) {
            System.out.print("Enter initial deposit amount: ");
            initialDeposit = getDoubleInput();
            if (initialDeposit >= 0) {
                break;
            } else {
                System.out.println("Initial deposit cannot be negative.");
            }
        }
        
        String accountNumber = generateAccountNumber();
        
        double interestRate = type.equalsIgnoreCase("Savings") ? 2.5 : 1.5;
        
        BankAccount newAccount = new BankAccount(name, accountNumber, type, initialDeposit, pin, interestRate);
        accounts.add(newAccount);
        
        System.out.println("\nAccount created successfully!");
        System.out.println("Your account number is: " + accountNumber);
        System.out.println("Please remember this number for future logins.");
    }
    
    private static String generateAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("ACC");
        for (int i = 0; i < 9; i++) {
            sb.append(random.nextInt(10));
        }
        
        // Check if account number already exists
        for (BankAccount account : accounts) {
            if (account.getAccountNumber().equals(sb.toString())) {
                return generateAccountNumber(); // Recursively generate a new one if duplicate
            }
        }
        
        return sb.toString();
    }
    
    private static BankAccount login() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts exist. Please create an account first.");
            return null;
        }
        
        System.out.println("\n=== LOGIN ===");
        System.out.print("Enter your account number: ");
        String accNumber = scanner.nextLine();
        
        BankAccount account = null;
        for (BankAccount acc : accounts) {
            if (acc.getAccountNumber().equals(accNumber)) {
                account = acc;
                break;
            }
        }
        
        if (account == null) {
            System.out.println("Account not found.");
            return null;
        }
        
        System.out.print("Enter your 4-digit PIN: ");
        int enteredPin = getIntInput();
        
        if (!account.verifyPin(enteredPin)) {
            if (account.isLocked()) {
                System.out.print("Account locked. Enter admin PIN to unlock: ");
                int adminPin = getIntInput();
                if (adminPin == ADMIN_PIN) {
                    account.unlockAccount(adminPin);
                    saveAccountsToFile(); // Save after unlocking account
                    System.out.print("Enter your 4-digit PIN again: ");
                    enteredPin = getIntInput();
                    if (account.verifyPin(enteredPin)) {
                        return account;
                    }
                } else {
                    System.out.println("Invalid admin PIN.");
                }
            }
            return null;
        }
        
        return account;
    }
    
    private static void bankingMenu(BankAccount currentAccount) {
        int choice;
        do {
            applyTheme(currentTheme);
            System.out.println("\n--- " + currentAccount.getAccountHolderName() + "'s Banking Menu ---");
            System.out.println("1. Deposit money");
            System.out.println("2. Withdraw money");
            System.out.println("3. Transfer funds");
            System.out.println("4. Check balance");
            System.out.println("5. View transaction history");
            System.out.println("6. Calculate interest");
            System.out.println("7. Change theme");
            System.out.println("8. Switch account");
            System.out.println("9. Account settings");
            System.out.println("10. Exit");
            System.out.print("Enter your choice: ");

            choice = getIntInput();

            switch (choice) {
                case 1:
                    System.out.print("Enter amount to deposit: ");
                    double depositAmount = getDoubleInput();
                    currentAccount.deposit(depositAmount);
                    saveAccountsToFile(); // Save after deposit
                    break;

                case 2:
                    System.out.print("Enter amount to withdraw: ");
                    double withdrawAmount = getDoubleInput();
                    currentAccount.withdraw(withdrawAmount);
                    saveAccountsToFile(); // Save after withdrawal
                    break;

                case 3:
                    System.out.print("Enter recipient account number: ");
                    scanner.nextLine(); // Consume newline
                    String recipientNumber = scanner.nextLine();
                    
                    BankAccount recipient = null;
                    for (BankAccount acc : accounts) {
                        if (acc.getAccountNumber().equals(recipientNumber)) {
                            recipient = acc;
                            break;
                        }
                    }
                    
                    if (recipient != null && !recipient.getAccountNumber().equals(currentAccount.getAccountNumber())) {
                        System.out.print("Enter amount to transfer to " + recipient.getAccountHolderName() + ": ");
                        double transferAmount = getDoubleInput();
                        currentAccount.transfer(recipient, transferAmount);
                        saveAccountsToFile(); // Save after transfer
                    } else if (recipient != null && recipient.getAccountNumber().equals(currentAccount.getAccountNumber())) {
                        System.out.println("Cannot transfer to your own account.");
                    } else {
                        System.out.println("Recipient account not found.");
                    }
                    break;

                case 4:
                    currentAccount.checkBalance();
                    break;

                case 5:
                    currentAccount.showTransactionHistory();
                    break;

                case 6:
                    System.out.print("Enter number of days for interest calculation: ");
                    int days = getIntInput();
                    currentAccount.calculateInterest(days);
                    break;

                case 7:
                    System.out.println("\nAvailable Themes:");
                    for (int i = 0; i < THEMES.length; i++) {
                        System.out.println((i + 1) + ". " + THEMES[i]);
                    }
                    System.out.print("Select theme: ");
                    int themeChoice = getIntInput();
                    if (themeChoice > 0 && themeChoice <= THEMES.length) {
                        currentTheme = THEMES[themeChoice - 1];
                        System.out.println("Theme changed to " + currentTheme);
                    } else {
                        System.out.println("Invalid theme selection.");
                    }
                    break;

                case 8:
                    System.out.println("Switching accounts...");
                    saveAccountsToFile(); // Save before switching
                    return;

                case 9:
                    accountSettings(currentAccount);
                    break;

                case 10:
                    System.out.println("Thank you for banking with us!");
                    saveAccountsToFile(); // Save before exiting
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        } while (choice != 10);
    }
    
    private static void accountSettings(BankAccount account) {
        int choice;
        do {
            System.out.println("\n=== ACCOUNT SETTINGS ===");
            System.out.println("1. Change PIN");
            System.out.println("2. View account details");
            System.out.println("3. Change interest rate (Admin only)");
            System.out.println("4. Back to main menu");
            System.out.print("Select option: ");
            
            choice = getIntInput();
            
            switch (choice) {
                case 1:
                    System.out.print("Enter current PIN: ");
                    int currentPin = getIntInput();
                    System.out.print("Enter new PIN: ");
                    int newPin = getIntInput();
                    account.changePin(currentPin, newPin);
                    saveAccountsToFile(); // Save after PIN change
                    break;
                    
                case 2:
                    System.out.println("Account Holder: " + account.getAccountHolderName());
                    System.out.println("Account Number: " + account.getAccountNumber());
                    System.out.println("Account Type: " + account.getAccountType());
                    System.out.printf("Interest Rate: %.2f%%%n", account.getInterestRate());
                    account.checkBalance();
                    break;
                    
                case 3:
                    System.out.print("Enter admin PIN: ");
                    int adminPin = getIntInput();
                    System.out.print("Enter new interest rate: ");
                    double newRate = getDoubleInput();
                    account.setInterestRate(newRate, adminPin);
                    saveAccountsToFile(); // Save after interest rate change
                    break;
                    
                case 4:
                    return;
                    
                default:
                    System.out.println("Invalid option.");
            }
        } while (choice != 4);
    }

    private static void applyTheme(String theme) {
        System.out.println("\n[Theme: " + theme + "]");
    }
    
    private static int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            scanner.next();
        }
        int input = scanner.nextInt();
        scanner.nextLine();
        return input;
    }
    
    private static double getDoubleInput() {
        while (!scanner.hasNextDouble()) {
            System.out.println("Please enter a valid number.");
            scanner.next();
        }
        double input = scanner.nextDouble();
        scanner.nextLine();
        return input;
    }
}