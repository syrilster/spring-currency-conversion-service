package com.springboot.microservices.currencyconversionservice;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

@Component
public class CurrencyExchangeDefault implements CurrencyExchangeServiceProxy {
    @Override
    public CurrencyConverter retrieveExchangeValue(String from, String to) {
        Long id = new Random().nextLong();
        CurrencyConverter currencyConverter = null;
        if ("USD".equalsIgnoreCase(from)) {
            currencyConverter = new CurrencyConverter(id, from, to,
                    new BigDecimal(73.1), BigDecimal.ZERO, BigDecimal.ZERO, 8000);
        } else if ("EUR".equalsIgnoreCase(from)) {
            currencyConverter = new CurrencyConverter(id, from, to,
                    new BigDecimal(85.18), BigDecimal.ZERO, BigDecimal.ZERO, 8000);
        } else if ("AUD".equalsIgnoreCase(from)) {
            currencyConverter = new CurrencyConverter(id, from, to,
                    new BigDecimal(52.6), BigDecimal.ZERO, BigDecimal.ZERO, 8000);
        }
        return currencyConverter;
    }
}
