package banking;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Repository {
    private static final String deleteByCard = "DELETE FROM card WHERE number = ?;";

    public static void deleteAccount(Account loggedAccount) {
        try (
                PreparedStatement deleteStatement = Main.connection.prepareStatement(deleteByCard);
        ) {
            deleteStatement.setString(1, loggedAccount.getCardNumber());
            deleteStatement.executeUpdate();
            System.out.println("The account has been closed!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void transferMoney(Account currentAccount, String transferCard, int amount) {
        try (
                Statement add = Main.connection.createStatement();
                Statement substranct = Main.connection.createStatement();
        ) {
            add.executeUpdate("UPDATE card SET balance = balance + " + amount + " WHERE number = " + transferCard + ";");
            substranct.executeUpdate("UPDATE card SET balance = balance - " + amount + " WHERE number = " + currentAccount.getCardNumber() + ";");
            System.out.println("Success!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addIncomeTo(Connection connection, Account account) {
        String updateBalanceByCurdNumber = "UPDATE card SET balance = ? WHERE number = ?;";
        try (
                PreparedStatement preparedStatement = connection.prepareStatement(updateBalanceByCurdNumber)
        ) {
            preparedStatement.setString(1, account.getBalance());
            preparedStatement.setString(2, account.getCardNumber());
            preparedStatement.executeUpdate();
            System.out.println("Income was added!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void saveAccount(Account account, Connection connection) {


        try (
                Statement statement = connection.createStatement()
        ) {
            String queryInsertAccount = "INSERT INTO card(number, pin, balance) VALUES(" +
                    account.getCardNumber() + ", " + account.getPin() + ", " + account.getBalance() + ");";
            statement.executeUpdate(queryInsertAccount);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean containsKey(Connection connection, String cardNumber) {
        ResultSet numbers = null;
        try (
                Statement statement = connection.createStatement()
        ) {

            numbers = statement.executeQuery("SELECT number FROM card");
            while (numbers.next()) {
                if (cardNumber.equals(numbers.getString("number"))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static Map<String, String> getNumbersAndPin(Connection connection) {
        ResultSet resultSet = null;
        HashMap<String, String> numberAndPin = new HashMap<>();
        try (
                Statement statement = connection.createStatement()
        ) {
            resultSet = statement.executeQuery("SELECT number, pin FROM card;");
            while (resultSet.next()) {
                numberAndPin.put(resultSet.getString("number"), resultSet.getString("pin"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return numberAndPin;
    }

    public static Account getAccountByNumber(String cardNumber, Connection connection) {
        ResultSet resultSet = null;
        Account account = new Account();
        try (
                Statement statement = connection.createStatement()
        ) {
            resultSet = statement.executeQuery("SELECT number, pin, balance FROM card WHERE number = " + cardNumber + ";");
            account.setCardNumber(resultSet.getString("number"));
            account.setPin(resultSet.getString("pin"));
            account.setBalance(resultSet.getString("balance"));
        } catch (SQLException e) {
            System.out.println("Such account not exist.");
            throw new RuntimeException(e);
        }
        return account;
    }

    public static boolean checkExist(String transferCard) {
        ResultSet resultSet = null;
        try (
                Statement statement = Main.connection.createStatement();
        ) {

            resultSet = statement.executeQuery("SELECT number FROM card;");
            while (resultSet.next()) {
                if (resultSet.getString("number").equals(transferCard)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
