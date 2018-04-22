package com.dimoiu.cosmin.gnbtransactions;

/* This class is meant for storing exchange rates from the provided web service. */

public class ExchangeRate {
    private String fromCurrency;
    private String toCurrency;
    private double rate;

    public ExchangeRate(String fromCurrency, String toCurrency, double rate) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public double getRate() {
        return rate;
    }
}
