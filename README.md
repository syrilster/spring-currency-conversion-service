## What does this Microservice provide?
* This is a classic example of a currency converter service which will take a currency "from" and "to" as the input and convert it to actual values. Ex: Convert USD to INR.
* This makes use of the currency exchange microservice to get the exchange rate(conversion multiple) and then proceed with the calculation.

## Final Endpoints
* Currency conversion Service: http://localhost:8765/currency-conversion-service/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}
* Currency Exchange Service: http://localhost:8765/currency-exchange-service/currency-exchange/from/{from}/to/{to}
* After adding **Zuul routes** to pick the service names as per the URL hit: This is to skip the application name that had to be specified in the URL:
  ```
  zuul:
  routes:
    currency-exchange-service:
      path: /currency-exchange/**
      serviceId: currency-exchange-service
      strip-prefix: false
    currency-conversion-service:
        path: /currency-converter/**
        serviceId: currency-conversion-service
        strip-prefix: false
  ```
* **Simplified URL's**:
    * Currency conversion Service: http://localhost:8765/currency-converter/from/{from}/to/{to}/quantity/{quantity}
    * Currency Exchange Service: http://localhost:8765/currency-exchange/from/{from}/to/{to}

## Feign Client
* Feign is an HTTP client created by Netflix to make HTTP communications easier. It is integrated to Spring Boot with the spring-cloud-starter-feign starter.
* To create a client to consume an HTTP service, an interface annotated with @FeignClient must be created. 
* Endpoints can be declared in this interface using an API that is very close to the Spring MVC API. The @EnableFeignClients annotation must also be added to a Spring Configuration class.

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
* An API Gateway, aka Edge Service, provides a unified interface for a set of microservices so that clients no need to know about all the details of microservices internals.
* Micro services should interact with each other via API gateway for below reasons:

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

## Setting up distributed tracing using Zipkin
* Enable sleuth to have unique id's for each request called log correlation ID's. This unique Id enables us to trace a single request till the end as the same id is used by sleuth. 
  ```
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
  </dependency>
  ```
  * Adding a sampling policy to manage volume. Add the below code in the main class. This sampler logs ass the request, however a custom logic can be provided here.
    ```
    @Bean
    public Sampler defaultSampler() {
      return Sampler.ALWAYS_SAMPLE;
    }
    ```
* If spring-cloud-sleuth-zipkin is on the classpath, the app generates and collects Zipkin-compatible traces. By default, it sends them over HTTP to a Zipkin server on localhost (port 9411). You can configure the location of the service by setting spring.zipkin.baseUrl. 
   * If you depend on spring-rabbit, your app sends traces to a RabbitMQ broker instead of HTTP.
* Download the zipkin jar from open zipkin page
* Install and start rabbit MQ.
  ```
  brew update
  brew install rabbitmq
  Start RabbitMQ service using: /usr/local/sbin/rabbitmq-server
  ```
* Start zipkin with a config of the Rabbit MQ details to allow Zipkin to listen messages on the MQ.
  ```
  RABBIT_URI=amqp://localhost java -jar zipkin-server-2.11.5-exec.jar
  ```
* Add the below pom.xml dependencies for zipkin and rabbitmq bus to all the required micro services.
  ```
  <dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-sleuth-zipkin</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-bus-amqp</artifactId>
		</dependency>
  ```
* Start all the microservices and make a request to find the trace in Zipkin dashboard.


## Enabling Cross-Origin Resource Sharing (CORS)
* In many cases, the host that serves the JS (e.g. localhost:3000) is different from the host that serves the data (e.g. localhost:8765). The request will get an error: **No 'Access-Control-Allow-Origin' header is present on the requested resource.** In such a case, CORS enables the cross-domain communication.
* @CrossOrigin on a @RequestMapping-Annotated Handler Method
  ```
    @CrossOrigin
    @RequestMapping("/{id}")
    public Account getAccountDetails(@PathVariable Long id) {
        // ...
    }
  ```
* @CrossOrigin on the Controller i.e. at the class level
* Global CORS configuration to handle all the methods request calls: 
  ```
    @Configuration
    @EnableWebMvc
    public class ApplicationCORS implements WebMvcConfigurer {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
          registry.addMapping("/**");
      }
    }
  ```

## Hystrix Circuit Breaker
* Provides latency tolerance and fault tolerance logic.
* Overview about the circuit breaker pattern here: https://github.com/syrilster/DesignPatterns/tree/master/DesignPrinciples/CircuitBreakerPattern
* Enable using:
  ```
  feign:
   hystrix:
    enabled: true
  ```
* Two approaches:
  * Using Hystrix and creating a method level fallback.
    ```
    Enable Hystrix at the application main class using annotation @EnableHystrix
    
    Add the below annotation and define a fallback method like below
    @HystrixCommand(fallbackMethod = "retrieveExchangeValueDefault")
     public CurrencyConverter retrieveExchangeValueFeign()
     
     ```
  * Using circuit breaker applied at the Feign annotation level.
    ```
     Enable Hystrix at the application main class using annotation @EnableCircuitBreaker
     
     @FeignClient(name = "netflix-zuul-api-gateway-server", fallback = CurrencyExchangeDefault.class)
     public interface CurrencyExchangeServiceProxy {
	  @GetMapping("/currency-exchange/from/{from}/to/{to}")
	  CurrencyConverter retrieveExchangeValue(@PathVariable("from") String from, @PathVariable("to") String to);
     }
	
     Then implement the fallback logic in the class like below:
     @Component
     public class CurrencyExchangeDefault implements CurrencyExchangeServiceProxy {
	 @Override
	 public CurrencyConverter retrieveExchangeValue(String from, String to) {
	  /*Some implementation*/
	 }
     }
    ```


