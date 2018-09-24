## What does this Microservice provide?
* This is a classic example of a currency converter service which will take a currency "from" and "to" as the input and convert it to actual values. Ex: Convert USD to INR.
* This makes use of the currency exchange microservice to get the exchange rate(conversion multiple) and then proceed with the calculation.

## Final Endpoints
* Currency conversion Service: http://localhost:8765/currency-conversion-service/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}
* Currency Exchange Service: http://localhost:8765/currency-exchange-service/currency-exchange/from/{from}/to/{to}

## Using Ribbon to Loadbalance between the currency exchange Micro Services.
* Using Feign Client we were able to set up a  proxy between services. (@FeignClient(name = "currency-exchange-service", url = "localhost:8000")).
* Above annotation has the server URL hardcoded. This is an issue if we want to have more instances of the same microservices as the URL changes.
* This can be mitigated using Ribbon LB. @RibbonClient(name = "currency-exchange-service")
* Ribbon brings in a centralized way to define the list of servers in application properties. Ex: currency-exchange-service.ribbon.listOfServers=http://localhost:8000, http://localhost:8001
* This is still a static list of servers and not dynamically managed.
* To use a different LB apart from the default Round Robin check here (https://github.com/Netflix/ribbon/wiki/Working-with-load-balancers#common-rules).

![ribbon lb](https://user-images.githubusercontent.com/6800366/40484769-55804472-5f7a-11e8-8b68-89f462f6eb39.PNG)

## Eureka Naming Server
* To get rid of hard coded ribbon list of servers: listOfServers=http://localhost:8000, http://localhost:8001
* All Micro services will register itself with the Naming server. (Service Registration)
* Micro service will first ask the name server to check the instance details for the request to be made. i.e Service Discovery.
* Replace list of servers with app property: eureka.client.service-url.default-zone=http://localhost:8761/eureka

![naming server](https://user-images.githubusercontent.com/6800366/40485250-a5bdfe06-5f7b-11e8-82e7-ffcc4102d49c.PNG)


## API Gateways (Zuul)
Micro services should interact with each other via API gateway for below reasons:

* Common place for Authentication, Authorization and security
* Rate limits
* Fault Tolerance - Configure some default response if the actual service is not available
* Service Aggregation - Aggregate several different services as one service call for an external caller

**Requesting a microservice using zuul API gateway endpoint**

Format: http://localhost:8765/{application-name}/{uri}

Example: http://localhost:8765/currency-exchange-service/currency-exchange/from/USD/to/INR

![zuul filter](https://user-images.githubusercontent.com/6800366/43877693-aadbe44e-9bde-11e8-8873-ad0f64f9be61.png)


## Distributed Tracing 
* Spring Cloud Sleuth implements a distributed tracing solution for Spring Cloud.
* Distributed tracing is required to track the request as it is going through multiple systems.
* Slueth allows to have a unique identifier for each request via currency-calculation service ---> CurrencyExchangeService --> Limits service.
* Slueth provided unique id will be used in Zipkin tracing.
* A call route to currency conversion service can be shown below:
    * First User calls the Currency conversion API
    * Request goes to Zuul log filter to do logging.
    * Then Currency conversion tries to call the exchange microservice via a proxy of Zuull api gateway.
    * Request goes to Zuul(API gateway) log filter to do logging.
    * Then Zuul api gateway calls the currency exchange service. 
* Distributed tracing allows us to check where the exact failure is. This is because sleuth assigns one unique id for all requests in the chain.
* Centralized log tracing using ELK Stack. "ELK" is the acronym for three open source projects: Elasticsearch, Logstash, and Kibana. Elasticsearch is a search and analytics engine. Logstash is a server‑side data processing pipeline that ingests data from multiple sources simultaneously, transforms it, and then sends it to a "stash" like Elasticsearch. Kibana lets users visualize data with charts and graphs in Elasticsearch.

![zipkin distributed tracing](https://user-images.githubusercontent.com/6800366/40572726-5b37e85e-60d1-11e8-853d-7640058493f7.PNG)




