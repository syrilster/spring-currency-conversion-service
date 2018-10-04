package com.springboot.microservices.currencyconversionservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@CrossOrigin
@RestController
public class CurrencyConversionController {

    Logger logger = LoggerFactory.getLogger(CurrencyConversionController.class);

    @Autowired
    private CurrencyExchangeServiceProxy proxy;

    @GetMapping("/currency-converter-rest-template/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConverter retrieveExchangeValue(@PathVariable String from, @PathVariable String to,
                                                   @PathVariable BigDecimal quantity) {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

        ResponseEntity<CurrencyConverter> responseEntity = new RestTemplate()
                .getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                        CurrencyConverter.class, uriVariables);
        CurrencyConverter response = responseEntity.getBody();
        return new CurrencyConverter(response.getId(), from, to, response.getConversionMultiple(),
                quantity, quantity.multiply(response.getConversionMultiple()), response.getPort());
    }

    @GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
    //@HystrixCommand(fallbackMethod = "retrieveExchangeValueDefault")
    public CurrencyConverter retrieveExchangeValueFeign(@PathVariable String from, @PathVariable String to,
                                                        @PathVariable BigDecimal quantity) {
        try {
            CurrencyConverter response = proxy.retrieveExchangeValue(from, to);
            logger.info("Inside Currency Conversion method: " + response);
            return new CurrencyConverter(response.getId(), from, to, response.getConversionMultiple(),
                    quantity, quantity.multiply(response.getConversionMultiple()), response.getPort());
        } catch (Exception exception) {
            throw new RuntimeException();
        }
    }

    public CurrencyConverter retrieveExchangeValueDefault(@PathVariable String from, @PathVariable String to,
                                                          @PathVariable BigDecimal quantity) {
        logger.info("Inside Currency Conversion fallback method");
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
