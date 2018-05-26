## What does this Microservice provide?
* This is a classic example of a currency converter service which will take a currency "from" and "to" as the input and convert it to actual values. Ex: Convert USD to INR.
* This makes use of the currency exchange microservice to get the exchange rate(conversion multiple) and then proceed with the calculation.

## Using Ribbon to Loadbalance between the currency exchange Micro Services.
* Using Feign Client we were able to set up a  proxy between services. (@FeignClient(name = "currency-exchange-service", url = "localhost:8000")).
* Above annotation has the server URL hardcoded. This is an issue if we want to have more instances of the same microservices as the URL changes.
* This can be mitigated using Ribbon LB. @RibbonClient(name = "currency-exchange-service")
* Ribbon brings in a centralized way to define the list of servers in application properties. Ex: currency-exchange-service.ribbon.listOfServers=http://localhost:8000, http://localhost:8001
* This is still a static list of servers and not dynamically managed.

![ribbon lb](https://user-images.githubusercontent.com/6800366/40484769-55804472-5f7a-11e8-8b68-89f462f6eb39.PNG)

## Eureka Naming Server
* To get rid of hard coded ribbon list of servers: listOfServers=http://localhost:8000, http://localhost:8001
* All Micro services will register itself with the Naming server. (Service Registration)
* Micro service will first ask the name server to check the instance details for the request to be made. i.e Service Discovery.
* Replace list of servers with app property: eureka.client.service-url.default-zone=http://localhost:8761/eureka

![naming server](https://user-images.githubusercontent.com/6800366/40485250-a5bdfe06-5f7b-11e8-82e7-ffcc4102d49c.PNG)

## Distributed Tracing 
* Spring Cloud Sleuth implements a distributed tracing solution for Spring Cloud.
* Distributed tracing is required to track the request as it is going through multiple systems.
* A call route to currency conversion service can be shown below:
    * First User calls the Currency conversion API
    * Request goes to Zuul log filter to do logging.
    * Then Currency conversion tries to call the exchange microservice via a proxy of Zuull api gateway.
    * Request goes to Zuul(API gateway) log filter to do logging.
    * Then Zuul api gateway calls the currency exchange service. 
* Distributed tracing allows us to check where the exact failure is. This is because sleuth assigns one unique id for all requests in the chain.


