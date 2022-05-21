package banking;

import org.sqlite.SQLiteDataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    public static Scanner scanner = new Scanner(System.in);

    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public static Account loggedAccount;
    private static String url = "jdbc:sqlite:";
    public static Connection connection = null;
    public static String dbFileName;

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-fileName")) {
                dbFileName = args[i + 1];
                break;
            }
        }
        createConnectionToDataBase();
        while (true) {
            System.out.println("\n1. Create an account\n" +
                    "2. Log into account\n" +
                    "0. Exit");
            switch (scanner.nextLine()) {
                case "1":
                    Utils.createAccount(connection);
                    break;
                case "2":
                    logIntoAccount();
                    break;
                case "0":
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Bye!");
                    System.exit(0);
            }
        }
    }

    private static void createConnectionToDataBase() {
        try {
            Class.forName("org.sqlite.JDBC");
            SQLiteDataSource dataSource = new SQLiteDataSource();
            dataSource.setUrl(url + Main.dbFileName);
            connection = dataSource.getConnection();
            if (connection.isValid(5)) {
                String createTable = "CREATE TABLE IF NOT EXISTS " +
                        "card(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "number TEXT, pin TEXT, balance INTEGER DEFAULT 0);";
                Statement statement = connection.createStatement();
                statement.executeUpdate(createTable);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void logIntoAccount() {
        System.out.println("Enter your card number:");
        String cardNumber = null;
        try {
            cardNumber = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Enter your PIN:");
        String pin = null;
        try {
            pin = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (
                Utils.checkAccountWith(cardNumber, pin, connection)
        ) {
            System.out.println("You have successfully logged in!\n");
            loggedAccount = Utils.getAccount(cardNumber, connection);
            accountRoom();
        } else {
            System.out.println("Wrong card number or PIN!");
        }
    }

    private static void accountRoom() {
        while (true) {
            System.out.println("1. Balance\n" +
                    "2. Add income\n" +
                    "3. Do transfer\n" +
                    "4. Close account\n" +
                    "5. Log out\n" +
                    "0. Exit");
            switch (scanner.nextLine()) {
                case "1":
                    System.out.println("Balance: " + loggedAccount.getBalance());;
                    break;
                case "2":
                    Utils.addIncome(connection, loggedAccount);
                    break;
                case "3":
                    Utils.doTransfer(loggedAccount);
                    break;
                case "4":
                    Utils.closeAccount();
                    return;
                case "5":
                    loggedAccount = null;
                    System.out.println("You have successfully logged out!");
                    return;
                case "0":
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    loggedAccount = null;
                    System.out.println("Bye!");
                    System.exit(0);
            }
        }
    }

}