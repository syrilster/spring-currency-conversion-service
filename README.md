* Classic example of a currency converter which takes inout from user and converts it to actual values.
* Makes use of the currency exchange microservice to get the exchange rate and then proceed with the calculation.

## Using Ribbon to Loadbalance between the currency exchange Micro Services.

![ribbon lb](https://user-images.githubusercontent.com/6800366/40484769-55804472-5f7a-11e8-8b68-89f462f6eb39.PNG)

## Eureka Naming Server
* To get rid of hard coded ribbon list of servers: listOfServers=http://localhost:8000, http://localhost:8001
* All Micro services will register itself with the Naming server. (Service Registration)
* Micro service will first ask the name server to check the instance details for the request to be made. i.e Service Discovery.
* Replace list of servers with app property: eureka.client.service-url.default-zone=http://localhost:8761/eureka

![naming server](https://user-images.githubusercontent.com/6800366/40485250-a5bdfe06-5f7b-11e8-82e7-ffcc4102d49c.PNG)


