package com.dimoiu.cosmin.gnbtransactions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Math.round;

public class Exchange {
    private static List<ExchangeRate> exchangeRates;

    public static void setExchangeRates(List<ExchangeRate> exchangeRates) {
        Exchange.exchangeRates = exchangeRates;
    }

    public static List<ExchangeRate> getExchangeRates() {
        return exchangeRates;
    }

    public static int convertToEuro(String currency, double amount){
        if(currency.equals("EUR")){
            return (int) round(amount);
        }

        Predicate<ExchangeRate> toEuroPredicate= p->p.getToCurrency().equals("EUR");
        Predicate<ExchangeRate> fromMyCurrencyPredicate= p->p.getFromCurrency().equals(currency);
        Predicate<ExchangeRate> fromMyCurrencyToEuroPredicate= fromMyCurrencyPredicate.and(toEuroPredicate);

        List<ExchangeRate> customSearchForDirectConversion= exchangeRates.stream().filter(fromMyCurrencyToEuroPredicate).collect(Collectors.toList());
        if(customSearchForDirectConversion.size()!=0){
            return (int)round(amount*customSearchForDirectConversion.get(0).getRate());
        }

        List<ExchangeRate> customSearchForEuroConversion= exchangeRates.stream().filter(toEuroPredicate).collect(Collectors.toList());
        for(ExchangeRate exchangeRate:customSearchForEuroConversion) {

            Predicate<ExchangeRate> toSubstituteCurrencyPredicate= p->p.getToCurrency().equals(exchangeRate.getFromCurrency());
            Predicate<ExchangeRate> fromMyCurrencyToSubstituteCurrencyPredicate=fromMyCurrencyPredicate.and(toSubstituteCurrencyPredicate);


            List<ExchangeRate> customSearchForIndirectConversion= exchangeRates.stream().filter(fromMyCurrencyToSubstituteCurrencyPredicate).collect(Collectors.toList());

            if(customSearchForIndirectConversion.size()!=0){
                int intermediateConversion=(int)round(amount*customSearchForIndirectConversion.get(0).getRate());
                return (int) round(intermediateConversion*exchangeRate.getRate());
            }

        }

        List<ExchangeRate> fromMyCurrencyConversion= exchangeRates.stream().filter(fromMyCurrencyPredicate).collect(Collectors.toList());
        for(ExchangeRate exchangeRate: fromMyCurrencyConversion){
            Predicate<ExchangeRate> fromUnknownCurrencyPredicate = p->p.getFromCurrency().equals(exchangeRate.getToCurrency());
            List<ExchangeRate> searchFromUnknownCurrency= exchangeRates.stream().filter(fromUnknownCurrencyPredicate).collect(Collectors.toList());

            for(ExchangeRate otherExchangeRate:searchFromUnknownCurrency){
                Predicate<ExchangeRate> toUnknownCurrencyPredicate = p->p.getFromCurrency().equals(otherExchangeRate.getToCurrency());
                Predicate<ExchangeRate> fromUnknownCurrencyToEuroPredicate = toUnknownCurrencyPredicate.and(toEuroPredicate);
                List<ExchangeRate> searchFromUnknownToEuro = exchangeRates.stream().filter(fromUnknownCurrencyToEuroPredicate).collect(Collectors.toList());

                if(searchFromUnknownToEuro.size()!=0){
                    int fromUnknownToEuroConversion = (int) round(amount*searchFromUnknownToEuro.get(0).getRate());
                    int fromUnknownToUnknownConversion = (int) round(fromUnknownToEuroConversion*otherExchangeRate.getRate());
                    return (int) round(fromUnknownToUnknownConversion*exchangeRate.getRate());
                }
            }
        }

        return 0;
    }

}
