package banking;

import java.util.Objects;

public class Account {
    String cardNumber;
    String balance;
    String pin;

    public Account() {
    }

    public Account(String cardNumber, String balance, String pin) {
        this.cardNumber = cardNumber;
        this.balance = balance;
        this.pin = pin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return getCardNumber().equals(account.getCardNumber()) && getBalance()
                .equals(account.getBalance()) && getPin().equals(account.getPin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCardNumber(), getBalance(), getPin());
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
