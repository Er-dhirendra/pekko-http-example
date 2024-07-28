Run Application Using

```bash
Sbt run
```
OR
```bash
UserServiceApp
```


To create `curl` commands for testing the API routes defined in the `UserRoutes` class and a `README.md` file explaining these requests and responses, follow the steps below:


### `curl` Commands

These commands will allow you to interact with the API endpoints defined in your `UserRoutes` class:

#### 1. **GET /users**

Retrieve a list of all users.

```bash
curl -X GET "http://localhost:8080/users" -H "Accept: application/json"
```

**Expected Response:**
- Status Code: `200 OK`
- Body: `[]` (Empty JSON array if no users are present)

#### 2. **POST /users**

Create a new user.

```bash
curl -X POST "http://localhost:8080/users" -H "Content-Type: application/json" -d '{"id":1, "name":"John Doe"}'
```

**Expected Response:**
- Status Code: `201 Created`
- Body: `{"description":"User 1 created."}`

#### 3. **GET /users/{id}**

Retrieve a user by ID.

```bash
curl -X GET "http://localhost:8080/users/1" -H "Accept: application/json"
```

**Expected Response:**
- Status Code: `200 OK`
- Body: `{"id":1, "name":"John Doe"}`

If the user does not exist:
- Status Code: `404 Not Found`
- Body: `{"description":"User not found"}`

#### 4. **PUT /users/{id}**

Update an existing user by ID.

```bash
curl -X PUT "http://localhost:8080/users/1" -H "Content-Type: application/json" -d '{"id":1, "name":"Jane Doe"}'
```

**Expected Response:**
- Status Code: `200 OK`
- Body: `{"description":"User 1 updated."}`

#### 5. **PATCH /users/{id}**

Partially update an existing user by ID.

```bash
curl -X PATCH "http://localhost:8080/users/1" -H "Content-Type: application/json" -d '{"name":"Jane Doe"}'
```

**Expected Response:**
- Status Code: `200 OK`
- Body: `{"description":"User 1 updated."}`

#### 6. **DELETE /users/{id}**

Delete a user by ID.

```bash
curl -X DELETE "http://localhost:8080/users/1"
```

**Expected Response:**
- Status Code: `204 No Content`
- Body: Empty

#### 7. **HEAD /users**

Check if the `/users` endpoint is available.

```bash
curl -X HEAD "http://localhost:8080/users"
```

**Expected Response:**
- Status Code: `200 OK`

#### 8. **OPTIONS /users**

Retrieve allowed HTTP methods for the `/users` endpoint.

```bash
curl -X OPTIONS "http://localhost:8080/users"
```

**Expected Response:**
- Status Code: `200 OK`
- Body: `GET, POST, PUT, PATCH, DELETE, OPTIONS`

### `README.md` File

Here's an example `README.md` file documenting the API endpoints and their corresponding `curl` commands.

```markdown
# User API Documentation

This document provides details about the User API, including `curl` commands for testing different endpoints.

## Base URL

```
http://localhost:8080
```

## Endpoints

### 1. GET /users

Retrieve a list of all users.

**Curl Command:**

```bash
curl -X GET "http://localhost:8080/users" -H "Accept: application/json"
```

**Expected Response:**

- Status Code: `200 OK`
- Body: `[]` (Empty JSON array if no users are present)

### 2. POST /users

Create a new user.

**Curl Command:**

```bash
curl -X POST "http://localhost:8080/users" -H "Content-Type: application/json" -d '{"id":1, "name":"John Doe"}'
```

**Expected Response:**

- Status Code: `201 Created`
- Body: `{"description":"User 1 created."}`

### 3. GET /users/{id}

Retrieve a user by ID.

**Curl Command:**

```bash
curl -X GET "http://localhost:8080/users/1" -H "Accept: application/json"
```

**Expected Response:**

- Status Code: `200 OK`
- Body: `{"id":1, "name":"John Doe"}`

If the user does not exist:
- Status Code: `404 Not Found`
- Body: `{"description":"User not found"}`

### 4. PUT /users/{id}

Update an existing user by ID.

**Curl Command:**

```bash
curl -X PUT "http://localhost:8080/users/1" -H "Content-Type: application/json" -d '{"id":1, "name":"Jane Doe"}'
```

**Expected Response:**

- Status Code: `200 OK`
- Body: `{"description":"User 1 updated."}`

### 5. PATCH /users/{id}

Partially update an existing user by ID.

**Curl Command:**

```bash
curl -X PATCH "http://localhost:8080/users/1" -H "Content-Type: application/json" -d '{"name":"Jane Doe"}'
```

**Expected Response:**

- Status Code: `200 OK`
- Body: `{"description":"User 1 updated."}`

### 6. DELETE /users/{id}

Delete a user by ID.

**Curl Command:**

```bash
curl -X DELETE "http://localhost:8080/users/1"
```

**Expected Response:**

- Status Code: `204 No Content`
- Body: Empty

### 7. HEAD /users

Check if the `/users` endpoint is available.

**Curl Command:**

```bash
curl -X HEAD "http://localhost:8080/users"
```

**Expected Response:**

- Status Code: `200 OK`

### 8. OPTIONS /users

Retrieve allowed HTTP methods for the `/users` endpoint.

**Curl Command:**

```bash
curl -X OPTIONS "http://localhost:8080/users"
```

**Expected Response:**

- Status Code: `200 OK`
- Body: `GET, POST, PUT, PATCH, DELETE, OPTIONS`
```

This `README.md` will provide clear instructions on how to use `curl` to interact with your API, and the commands will help you test your endpoints effectively.