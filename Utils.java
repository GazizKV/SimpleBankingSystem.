package banking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class Utils {

    private static final Random random = new Random();

    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    static boolean checkAccountWith(String cardNumber, String pin, Connection connection) {
        Map<String, String> numbersAndPin = Repository.getNumbersAndPin(connection);
        for (Map.Entry<String, String> nextPair : numbersAndPin.entrySet()) {
            if (nextPair.getKey().equals(cardNumber) && nextPair.getValue().equals(pin)) {
                return true;
            }
        }
        return false;
    }

    static void createAccount(Connection connection) {
        String cardNumber = "";
        while (cardNumber.equals("") || Repository.containsKey(connection, cardNumber)) {
            cardNumber = generateCardNumber();
        }
        String pin = generatePin();
        Account account = new Account(cardNumber, "0", pin);
        Repository.saveAccount(account, connection);
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(account.getCardNumber());
        System.out.println("Your card PIN:");
        System.out.println(account.getPin());
    }

    private static String generatePin() {
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            pin.append(random.nextInt(9));
        }
        return pin.toString();
    }

    private static String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder();
        cardNumber.append(400000);
        for (int i = 0; i < 9; i++) {
            cardNumber.append(random.nextInt(9));
        }
        cardNumber = addCheckDigit(cardNumber);
        return cardNumber.toString();
    }

    private static StringBuilder addCheckDigit(StringBuilder cardNumber) {
        int[] cardDigits = new int[cardNumber.length()];
        String[] numbers = cardNumber.toString().split("");
        for (int i = 0; i < cardNumber.length(); i++) {
            cardDigits[i] = Integer.parseInt(numbers[i]);
        }
        for (int i = 0; i < cardDigits.length; i++) {
            if ((i + 1) % 2 == 1) {
                cardDigits[i] = cardDigits[i] * 2;
            }
            if (cardDigits[i] > 9) {
                cardDigits[i] = cardDigits[i] - 9;
            }
        }
        int sum = Arrays.stream(cardDigits).sum();
        int remainder = sum % 10;
        int last = 0;
        if (remainder != 0) {
            last = 10 - remainder;
        }
        return cardNumber.append(last);
    }


    public static Account getAccount(String cardNumber, Connection connection) {
        return Repository.getAccountByNumber(cardNumber, connection);
    }


    public static void addIncome(Connection connection, Account account) {
        System.out.println("Enter income:");
        int income = Main.scanner.nextInt();
        account.setBalance(String.valueOf(Integer.parseInt(account.getBalance()) + income));
        Repository.addIncomeTo(connection, account);
    }

    public static void doTransfer(Account currentAccount) {
        System.out.println("Enter card number:");
        String transferCard = null;
        try {
            transferCard = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!checkLuhnAlg(transferCard)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            return;
        }
        if (currentAccount.getCardNumber().equals(transferCard)) {
            System.out.println("You can't transfer money to the same account!");
            return;
        }
        if (!Repository.checkExist(transferCard)) {
            System.out.println("Such a card does not exist.");
            return;
        }
        System.out.println("Enter how much money you want to transfer:");
        int amount = 0;
        try {
            amount = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (Integer.parseInt(currentAccount.getBalance()) < amount) {
            System.out.println("Not enough money!");
            return;
        }
        Repository.transferMoney(currentAccount, transferCard, amount);
        try {
            Main.connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean checkLuhnAlg(String transferCard) {
        int[] cardDigits = new int[transferCard.length()];
        String[] numbers = transferCard.split("");
        for (int i = 0; i < transferCard.length(); i++) {
            cardDigits[i] = Integer.parseInt(numbers[i]);
        }
        for (int i = 0; i < cardDigits.length; i++) {
            if ((i + 1) % 2 == 1) {
                cardDigits[i] = cardDigits[i] * 2;
            }
            if (cardDigits[i] > 9) {
                cardDigits[i] = cardDigits[i] - 9;
            }
        }
        int sum = Arrays.stream(cardDigits).sum();
        int remainder = sum % 10;
        return remainder == 0;
    }

    public static void closeAccount() {
        Repository.deleteAccount(Main.loggedAccount);
        Main.loggedAccount = null;
        return;
    }
}
