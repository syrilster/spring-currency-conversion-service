package com.springboot.microservices.currencyconversionservice;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "currency-exchange-service", url = "localhost:8000")
@FeignClient(name = "netflix-zuul-api-gateway-server")
/* If only using Ribbon then have the Feign Client without the URL
@FeignClient(name = "currency-exchange-service")*/
//@RibbonClient(name = "currency-exchange-service")
public interface CurrencyExchangeServiceProxy {
    //@GetMapping("/currency-exchange/from/{from}/to/{to}")
    //To execute the request via Zuul. i.e  "http://localhost:8765/{application-name}/{uri}"
    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    CurrencyConverter retrieveExchangeValue(@PathVariable("from") String from, @PathVariable("to") String to);
}
