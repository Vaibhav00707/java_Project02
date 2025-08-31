import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class BankAccount implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String accountHolderName;
    private final String accountNumber;
    private final String accountType;
    private double balance;
    private int pin;
    private final Map<LocalDateTime, String> transactionHistory;
    private double interestRate;
    private boolean isLocked;
    private int failedAttempts;
    private static final int ADMIN_PIN = 9999;

    public BankAccount(String accountHolderName, String accountNumber, String accountType,
            double initialBalance, int pin, double interestRate) {
        this.accountHolderName = accountHolderName;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = initialBalance;
        this.pin = pin;
        this.interestRate = interestRate;
        this.transactionHistory = new HashMap<>();
        this.isLocked = false;
        this.failedAttempts = 0;
        recordTransaction("Account opened with initial balance: " + initialBalance);
    }

    private void recordTransaction(String description) {
        LocalDateTime now = LocalDateTime.now();
        transactionHistory.put(now, description);
    }

    public void deposit(double amount) {
        if (isLocked) {
            System.out.println("Account is locked. Please contact support.");
            return;
        }

        if (amount > 0) {
            balance += amount;
            System.out.printf("Successfully deposited: $%.2f%n", amount);
            recordTransaction("Deposit: +$" + amount);
        } else {
            System.out.println("Deposit amount must be greater than zero.");
        }
    }

    public void withdraw(double amount) {
        if (isLocked) {
            System.out.println("Account is locked. Please contact support.");
            return;
        }

        if (amount > 0) {
            if (amount <= balance) {
                balance -= amount;
                System.out.printf("Successfully withdrew: $%.2f%n", amount);
                recordTransaction("Withdrawal: -$" + amount);
            } else {
                System.out.println("Insufficient balance for this withdrawal.");
            }
        } else {
            System.out.println("Withdraw amount must be greater than zero.");
        }
    }

    public void transfer(BankAccount recipient, double amount) {
        if (isLocked) {
            System.out.println("Account is locked. Please contact support.");
            return;
        }

        if (amount > 0) {
            if (amount <= balance) {
                this.balance -= amount;
                recipient.balance += amount;
                System.out.printf("Transferred $%.2f to %s%n", amount, recipient.getAccountHolderName());
                recordTransaction("Transfer to " + recipient.getAccountNumber() + ": -$" + amount);
                recipient.recordTransaction("Transfer from " + this.accountNumber + ": +$" + amount);
            } else {
                System.out.println("Insufficient balance for this transfer.");
            }
        } else {
            System.out.println("Transfer amount must be greater than zero.");
        }
    }

    public void checkBalance() {
        System.out.printf("Current balance: $%.2f%n", balance);
    }

    public void showTransactionHistory() {
        System.out.println("\n--- Transaction History ---");
        if (transactionHistory.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        
        transactionHistory.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String formattedDate = entry.getKey().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    System.out.println(formattedDate + " - " + entry.getValue());
                });
    }

    public void calculateInterest(int days) {
        if (days > 0) {
            double interest = balance * (interestRate / 100) * (days / 365.0);
            System.out.printf("Estimated interest for %d days: $%.2f%n", days, interest);
        } else {
            System.out.println("Days must be greater than zero.");
        }
    }

    public boolean verifyPin(int enteredPin) {
        if (isLocked) {
            System.out.println("Account is locked. Please contact support.");
            return false;
        }
        
        if (enteredPin == pin) {
            failedAttempts = 0;
            return true;
        } else {
            failedAttempts++;
            if (failedAttempts >= 3) {
                isLocked = true;
                System.out.println("Too many failed attempts. Account locked.");
            } else {
                System.out.println("Incorrect PIN. " + (3 - failedAttempts) + " attempts remaining.");
            }
            return false;
        }
    }

    public void unlockAccount(int adminPin) {
        if (adminPin == ADMIN_PIN) {
            isLocked = false;
            failedAttempts = 0;
            System.out.println("Account unlocked successfully.");
        } else {
            System.out.println("Invalid admin PIN.");
        }
    }
    
    public void changePin(int oldPin, int newPin) {
        if (isLocked) {
            System.out.println("Account is locked. Please contact support.");
            return;
        }
        
        if (oldPin == pin) {
            pin = newPin;
            System.out.println("PIN changed successfully.");
            recordTransaction("PIN changed");
        } else {
            System.out.println("Incorrect current PIN.");
        }
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public boolean isLocked() {
        return isLocked;
    }
    
    public double getInterestRate() {
        return interestRate;
    }
    
    public void setInterestRate(double interestRate, int adminPin) {
        if (adminPin == ADMIN_PIN) {
            this.interestRate = interestRate;
            System.out.println("Interest rate changed to " + interestRate + "%");
            recordTransaction("Interest rate changed to " + interestRate + "%");
        } else {
            System.out.println("Invalid admin PIN.");
        }
    }
}