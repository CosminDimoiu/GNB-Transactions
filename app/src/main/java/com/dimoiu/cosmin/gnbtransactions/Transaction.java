package com.dimoiu.cosmin.gnbtransactions;

public class Transaction{
    private String code;
    private double amount;
    private String currency;
    private int amountInEuro;

    public Transaction(String code, double amount, String currency, int amountInEuro) {
        this.code = code;
        this.amount = amount;
        this.currency = currency;
        this.amountInEuro = amountInEuro;
    }

    public String getCode() {
        return code;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public int getAmountInEuro() {
        return amountInEuro;
    }
}
