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
* Payload: A JSON(Base64 encoded) that has info about the user. (Like username, password, email etc)
* Signature: A String that was generated using #1 + #2 + “a secret” (that only the server knows), using the algorithm mentioned in #1.


