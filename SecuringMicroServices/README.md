## Authentication vs Authorization
* Authentication = login + password (who you are)
* Authorization = user permissions (what are you allowed to do)

## Tokens V/S Cookies
* HTTP is a stateless protocol and once you login (using a username/pass or identity provider like FB or Google), every 
subsequent request made to the server needs to be again checked for authorization/authentication access.
* One way to do this is via “session” cookies and other way is to use “auth” tokens.
* Session Id workflow:
  * Browser passes the session_id info along with the request header.
  * Server looks up this session_id info to find a user from a in-memory store like Redis or a DB.
  * Receive the response from Redis/DB with the user details.
  * Check the roles/access for this user and fulfill the request accordingly.
  * Return the response.
* JWT Token workflow: (**Server doesn’t need to lookup DB/Cache for user details as it is embedded inside the token itself**)
  * Pass JWT Token along with the request.
  * Verify JWT and get embedded user. (Performant as no need for a user lookup)
  * Check the roles/access for this user and fulfill the request accordingly.
  * Return the response.
  
## What Does A JWT Token look like?
The token has 3 parts: 
```
<header>.<payload>.<signature>
```
* Header: A JSON(Base64 encoded) that has info about algorithm used(like HS256, RSA).
* Payload: A JSON(Base64 encoded) that has info about the user. Typically this is:
  * User Id, Username, Email etc.
  * Any ACL like: isAdmin, isManager etc.
* Signature: A String that was generated using #1 + #2 + “a secret” (that only the server knows), using the algorithm mentioned in #1.

## Steps involved in configuring Spring Security + JWT tokens
* Add the below dependency in pom to enable spring security:
  ```
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  ```
* Add dependency for JWT
  ```
   <dependency>
     <groupId>io.jsonwebtoken</groupId>
     <artifactId>jjwt</artifactId>
     <version>0.7.0</version>
   </dependency>
  ```
* After you added the above dependency check server log and observe that spring has generated a default password.
* **@EnableWebSecurity**: This is the primary spring security annotation that is used to enable web security in a project.
* **@EnableGlobalMethodSecurity**: This is used to enable method level security based on annotations
* Have a class to which extends the WebSecurityConfigurerAdapter and implement the configure() method to accept valid requests and handle csrf.
* **JwtAuthenticationEntryPoint**: This class is used to return a 401 unauthorized error message to the clients that try to access a protected resource without proper authentication. It implements Spring Security’s AuthenticationEntryPoint interface.
* Implement a filter for Authentication:
  * This is a service which accepts the username/password JSON request and generates a JWT token
* Implement a filter that -
  * reads JWT authentication token from the Authorization header of all the requests
  * validates the token
  * loads the user details associated with that token.
  * Sets the user details in Spring Security’s SecurityContext. Spring Security uses the user details to perform authorization checks. We can also access the user details stored in the SecurityContext in our controllers to perform our business logic.
 * Use postman to hit a POST request to URL http://localhost:8765/login by sending the username and password as a JSON. This will return the JWT token to be used for the actual request.


