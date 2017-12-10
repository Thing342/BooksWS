# BooksWS
Backend for books project

## Methods:

### /login (TokenFacadeREST):
####`POST /login` (login())
Tries a username/password combo. If successful, returns a `Token` used for methods that require authentication.
* Headers:
    - `username`: The user's username
    - `password`: The user's password
* Returns:
    - `200 OK`: If the attempt was successful, with a `Token` as its payload.
    - `404 Not Found`: If the username was not found.
    - `401 Not Authorized`: If the password was incorrect.
* Example:
    - Request:
    ```
    POST /login HTTP/1.1
    username: avaliduser
    password: correctpassword
    ```
    - Response: (the token id would be XXXX, as opposed to the userid 9999)
    ```json
    {
        "created": "2017-12-10T11:38:08.643-05:00",
        "id": XXXX,
        "user": {
            "email": "user@example.com",
            "firstName": "Firstname",
            "id": 9999,
            "lastName": "Lastname",
            "passHash": "...",
            "username": "avaliduser"
        }
    }
    ```
####`DELETE /login` (logout())
Logs a user out and deletes all of their tokens.
* Headers:
    - `tokenid`: id of the token representing the current user.
* Returns:
    - `200 OK`: If successful.
    - `401 Not Authorized`: If the token was incorrect or invalid.
* Example:
    - Request:
    ```
    DELETE /login HTTP/1.1
    tokenid: XXXX
    ```
    - Response:
    ```
    Logged out user Y
    ```    
### /users (UserFacadeREST):
####`POST /users?username={}&email={}&firstname={}&lastname={}&password={}` (create())
Creates a new user with the given fields set.
* URL Parameters (all are required):
    - `username`: The new user's username (must be unique)
    - `email`: The new user's email
    - `firstname`, `lastname`: The new user's first and last name, respectively.
    - `password`: The new user's password.
* Returns:
    - `200 OK`: If adding the user was successful, with info about the new user as its payload.
    - `500 Internal Server Error`: If the username has already been taken.
* Example:
    - Request:
    ```
    POST /users?username=user&email=user@example.com&firstname=User&lastname=Name&password=mypassword HTTP/1.1
    ```
    - Response:
    ```json
    {
        "email": "user@example.com",
        "firstName": "User",
        "id": 9999,
        "lastName": "Name",
        "passHash": "...",
        "username": "user"
    }
    ```
####`PUT /users?email={}&firstname={}&lastname={}&password={}` (edit())
Edits the user represented by the token.
* URL Parameters (all are required):
    - `email`: The user's new email
    - `firstname`, `lastname`: The user's new first and last name, respectively.
    - `password`: The user's new password.
* Header Parmeters:
    - `tokenid`: Token representing the user to edit.
* Returns:
    - `200 OK`: If adding the user was successful, with info about the updated user as its payload.
    - `401 Not Authorized`: If the token is invalid or expired.
* Example:
    - Request:
    ```
    PUT/users?email=newemail@example.com&firstname=User&lastname=Name&password=mypassword HTTP/1.1
    ```
    - Response:
    ```json
    {
        "email": "newemail@example.com",
        "firstName": "User",
        "id": 9999,
        "lastName": "Name",
        "passHash": "...",
        "username": "user"
    }
    ```
####`PUT /users?email={}&firstname={}&lastname={}&password={}` (edit())
Edits the user represented by the token. To keep a field the same, pass in the old value.
* URL Parameters (all are required):
    - `email`: The user's new email
    - `firstname`, `lastname`: The user's new first and last name, respectively.
    - `password`: The user's new password.
* Header Parmeters:
    - `tokenid`: Token representing the user to edit.
* Returns:
    - `200 OK`: If editing the user was successful, with info about the updated user as its payload.
    - `401 Not Authorized`: If the token is invalid or expired.
* Example:
    - Request:
    ```
    PUT /users?email=newemail@example.com&firstname=User&lastname=Name&password=mypassword HTTP/1.1
    tokenid: XXXX
    ```
    - Response:
    ```json
    {
        "email": "newemail@example.com",
        "firstName": "User",
        "id": 9999,
        "lastName": "Name",
        "passHash": "...",
        "username": "user"
    }
    ```
####`DELETE /users` (remove())
Deletes the user represented by the token. (Note: this will also log them out)
* Header Parmeters:
    - `tokenid`: Token representing the user to delete.
* Returns:
    - `200 OK`: If deleting the user was successful.
    - `401 Not Authorized`: If the token is invalid or expired.
* Example:
    - Request:
    ```
    DELETE /users HTTP/1.1
    tokenid: XXXX
    ```
    - Response:
    ```
    Deleted user YYYY
    ```
####`GET /users/byID/{id}` (find())
Returns the user with id=`id`, if it exists
* Path Parmeters:
    - `id`: ID of the user to find.
* Returns:
    - `200 OK`: If the user exists, along with info about the user..
    - `401 Not Authorized`: If the token is invalid or expired.
* Example:
    - Request:
    ```
    GET /users/byID/9999 HTTP/1.1
    ```
    - Response:
        ```json
        {
            "email": "newemail@example.com",
            "firstName": "User",
            "id": 9999,
            "lastName": "Name",
            "passHash": "...",
            "username": "user"
        }
        ```
####`GET /users/byUsername/{name}` (find())
Returns the user with username=`name`, if it exists
* Path Parmeters:
    - `name`: ID of the user to find.
* Returns:
    - `200 OK`: If the user exists, along with info about the user.
    - `401 Not Authorized`: If the token is invalid or expired.
* Example:
    - Request:
    ```
    GET /users/byUsername/user HTTP/1.1
    ```
    - Response:
        ```json
        {
            "email": "newemail@example.com",
            "firstName": "User",
            "id": 9999,
            "lastName": "Name",
            "passHash": "...",
            "username": "user"
        }
        ```
### /books (UserBookFacadeREST):
####`POST /books?isbn={}` (create())
Adds the ISBN to the user's account.
* URL Parameters:
    - `isbn`: The 13-digit ISBN number to add.
* Header Parmeters:
    - `tokenid`: Token representing the user.
* Returns:
    - `200 OK`: If adding the book was successful.
    - `401 Not Authorized`: If the token is invalid or expired.
* Example:
    - Request:
    ```
    POST /books?isbn=1234567890123 HTTP/1.1
    tokenid: XXXX
    ```
    - Response:
    ```json
    {
        "user": 9999,
        "added": "2017-12-10T12:58:15-05:00",
        "id": 10,
        "isbn13": 1234567890123
    }
    ```
####`DELETE /books?isbn={}` (remove())
Removes the ISBN fronm the user's account.
* URL Parameters:
    - `isbn`: The 13-digit ISBN number to remove.
* Header Parmeters:
    - `tokenid`: Token representing the user.
* Returns:
    - `200 OK`: If removing the book was successful.
    - `401 Not Authorized`: If the token is invalid or expired.
* Example:
    - Request:
    ```
    DELETE /books?isbn=1234567890123 HTTP/1.1
    tokenid: XXXX
    ```
    - Response:
    ```
    Removed isbn 1234567890123 from user YYYY
    ```
####`GET /books` (userBooks())
Lists the user's current set of books.
* URL Parameters:
* Header Parmeters:
    - `tokenid`: Token representing the user.
* Returns:
    - `200 OK`: If the query was successful.
    - `401 Not Authorized`: If the token is invalid or expired.
* Example:
    - Request:
    ```
    GET /books HTTP/1.1
    tokenid: XXXX
    ```
    - Response:
    ```json
    [
        {
            "user": 101,
            "added": "2017-12-10T12:54:09-05:00",
            "id": 9563,
            "isbn13": 97803070000
        },
        {
            "user": 101,
            "added": "2017-12-10T12:58:15-05:00",
            "id": 9565,
            "isbn13": 97803071000
        },
        {
            "user": 101,
            "added": "2017-12-10T12:58:16-05:00",
            "id": 9777,
            "isbn13": 97803071100
        },
        {
            "user": 101,
            "added": "2017-12-10T13:03:50.811-05:00",
            "id": 10004,
            "isbn13": 97803071110
        }
    ]
    ```
### /apis/nyt (NYTBooksREST)
This is simply a mirror of the [NYT books API](http://developer.nytimes.com/books_api.json#/README).