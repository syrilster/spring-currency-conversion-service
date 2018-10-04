## Authentication vs Authorization
* Authentication = login + password (who you are)
* Authorization = user permissions (what are you allowed to do)

## Tokens V/S Cookies
* HTTP is a stateless protocol and once you login (using a username/pass or identity provider like FB or Google), every 
subsequent request made to the server needs to be again checked for authorization/authentication access.
* One way to do this is via “session” cookies and other way is to use “auth” tokens.


