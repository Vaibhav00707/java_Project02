Enhanced Banking Application (Java)

This project is a console-based banking application built with Java.
It simulates core banking functionalities such as account creation, login with PIN security, deposits, withdrawals, fund transfers, interest calculation, transaction history tracking, and more.
All data is persisted locally using Java serialization, so accounts remain saved even after restarting the application.

Overview

The application provides a menu-driven interface for users to interact with their bank accounts.
It includes security features like account lock after multiple failed login attempts, an admin PIN for unlocking accounts, and the ability to set interest rates for accounts.

Getting Started

Clone the Repository
git clone https://github.com/yourusername/bank-app-java.git

cd bank-app-java

Compile the Program
javac BankAccount.java BankApp.java

Run the Program
java BankApp

Usage

When you run the application, you’ll be presented with a menu:

Login to existing account

Create new account

Exit

Create new account → Guided setup with name, type, PIN, and initial deposit

Login → Secure login with account number and PIN

Exit → Safely saves all data before exiting

After logging in, you can access the banking menu with options like deposits, withdrawals, transfers, balance checks, and more.

Features

Secure Login with PIN

3 failed attempts lock the account

Unlock via Admin PIN (9999)

Account Operations

Deposit and withdraw funds

Transfer money between accounts

View current balance

Transaction History

Stores every activity (deposit, withdraw, transfer, PIN changes, etc.) with timestamps

Interest Calculation

Calculates estimated interest for a given number of days based on account type and rate

Account Settings

Change your PIN

View detailed account information

Admin can update the interest rate

Customizable Themes

Options: Classic, Dark, Light, Professional

Persistent Data Storage

Accounts are saved to bank_data.dat automatically on exit and reloaded on startup

Admin Controls

Admin PIN: 9999

Unlock locked accounts

Change account interest rates

Project Structure

BankAccount.java → Class representing a bank account

BankApp.java → Main application with menus and logic

bank_data.dat → Serialized data file (auto-generated)

README.md → Project documentation

Future Enhancements

Add password/PIN encryption for higher security

Build a GUI (Swing/JavaFX) version for better usability

Enable online storage or database integration
