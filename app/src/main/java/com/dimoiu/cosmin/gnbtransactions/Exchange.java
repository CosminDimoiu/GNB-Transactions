package com.dimoiu.cosmin.gnbtransactions;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Math.round;

/* This class stores exchange rates and has everything necessary to make conversions for all currencies.*/

public class Exchange {
    private static List<ExchangeRate> exchangeRates;

    public static void setExchangeRates(List<ExchangeRate> exchangeRates) {
        Exchange.exchangeRates = exchangeRates;
    }

    public static List<ExchangeRate> getExchangeRates() {
        return exchangeRates;
    }


    /* This method makes all the conversions we need from any kind of currency in Euro. */
    public static int convertToEuro(String currency, double amount){

        /* First we check if the current amount is already in Euro. If it is, we just round it.*/
        if(currency.equals("EUR")){
            return (int) round(amount);
        }

        /* If it is not in Euro, then I start searching for a direct conversion from the current currency into Euro. If we find it, the the conversion is made and the result is rounded.*/
        Predicate<ExchangeRate> toEuroPredicate= p->p.getToCurrency().equals("EUR");
        Predicate<ExchangeRate> fromMyCurrencyPredicate= p->p.getFromCurrency().equals(currency);
        Predicate<ExchangeRate> fromMyCurrencyToEuroPredicate= fromMyCurrencyPredicate.and(toEuroPredicate);
        List<ExchangeRate> customSearchForDirectConversion= exchangeRates.stream().filter(fromMyCurrencyToEuroPredicate).collect(Collectors.toList());
        if(customSearchForDirectConversion.size()!=0){
            return (int)round(amount*customSearchForDirectConversion.get(0).getRate());
        }


        /* If there is not a direct conversion provided, we are starting to look for an indirect one. We are trying to find a secondary conversion that can be transformed into Euro.
        The conversion is: from the current currency in a substitute currency and from this substitute currency into Euro. */
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


        /* In case we can not find even an indirect conversion, then we must find a more complicate one. This means that are required not only one substitute, but two of them.
        With this we cover every possibility of conversion, if we have 4 types of currency.
        This conversion is: from current currency to a substitute currency, then from this substitute currency into another substitute currency, after that we convert the new substitute directly into Euro.*/
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
