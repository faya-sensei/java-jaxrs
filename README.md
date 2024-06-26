- [The Introduction Of The JAX-RS](#introduction)
- [Task Check List](#tasks)
  - [Implement a Heartbeat Endpoint](#1-implement-a-heartbeat-endpoint)
  - [Implement Authorization and Authentication Endpoint](#2-implement-authorization-endpoint-and-authentication-middleware)
  - [Implement a Todo List Endpoint](#3-implement-a-todo-list-endpoint)
  - [Let's Design a Web Page](#3-lets-design-a-web-page)
  - [Make Server Data Flow](#4-make-server-data-flow)

# Introduction

Java EE, now known as Jakarta EE, embodies the principle that "third-tier
companies make products, second-tier companies design technology, and first-tier
companies set standards." Though it has greatly benefited the Java community as
an official standard, its practical implementation has lagged, making it more of
a reference point than a strict standard. Nevertheless, it remains a valuable
resource due to its extensive documentation and enterprise-oriented development
approach.

JAX-RS, or Java API for RESTful Web Services, is a part of Java EE that
standardizes the creation of RESTful web services, thereby simplifying the
development of HTTP-based services. It was introduced in December 2009 with Java
EE 6 under [JSR 311](https://jcp.org/en/jsr/detail?id=311).

In this exercise, we will explore key specifications in Java EE through its
documentation and implement the required logic within the framework. By
mastering these coding rules and design specifications, you will be
well-equipped to proficiently use all Java web frameworks.

> [!NOTE]
> The transition from javax to jakarta happened because Oracle transferred Java
> EE specifications to the Eclipse Foundation, which required a new namespace
> due to Oracle retaining rights to javax.
![JavaEE API Relocated](docs/javax-to-jakarta.png)

# Tasks

## 1. Implement a Heartbeat Endpoint

### Instruction:

For a professional web service, especially in a multi-service application,
creating a heartbeat endpoint is important. This endpoint will indicate that the
server is alive and functional by returning the current server time. To
effectively manage multiple fields, we can encapsulate them into a JSON object.

In this exercise, we will utilize the Java EE REST API as defined in
[JSR 311](https://jcp.org/en/jsr/detail?id=311) and refer to the official
documentation in the
**[Jakarta REST Tutorial](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/websvcs/jaxrs/jaxrs.html)**.
Additionally, we will use the Java EE JSON API as defined in
[JSR 353](https://jcp.org/en/jsr/detail?id=353) and refer to the official
documentation in the
**[Jakarta JSON Processing Tutorial](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/web/jsonp/jsonp.html)**

### Procedural:

1. **Set up your project environment**: Ensure your project is properly
configured and run Gradle sync to install dependencies correctly.

> [!Important]
> The project testing uses the Java SE environment, Pay close attention to the
> differences between environments.

2. **Define the endpoint**: Use JAX-RS annotations to define the endpoint and
implement the logic in `resources/endpoint/HeartBeatResource.java`.

### Requirement:

* **Endpoint**: _**/api/heartbeat**_
* **Method**: `GET`
* **Response**: JSON object containing the current server time, e.g.:
  ```json
  {
    "status": "alive",
    "time": "1970-01-01T00:00:00Z"
  }
  ```

<details>
  <summary>Hint:</summary>
  <ul>
    <li>Use <code>@Path</code>, <code>@GET</code>, and <code>@Produces</code> annotations to define the endpoint and specify the response type.</li>
    <li>Utilize <code>LocalDateTime.now()</code> to get the current server time. Required <code>ISO</code> standard date time.</li>
    <li>Use <code>Json.createObjectBuilder</code> or <code>Json.createParser</code> to parse json object.</li>
  </ul>
</details>

## 2. Implement Authorization Endpoint and Authentication Middleware

### Instruction:

Authorization and authentication are critical components in web services. JWT
(JSON Web Token) is one of the simplest and most secure ways to manage user
authentication. In this task, you will implement endpoints for user registration
and login.

### Procedural:

1. **Implement the DTO**: Finalize the `UserDTO` class that serves as a **Data
Transfer Object** for transferring user data. Ensure this class includes getter
and setter methods for all fields to maintain proper encapsulation and access.
Use JSON binding annotations to handle sensitive information, such as passwords,
to ensure correct serialization and deserialization during client-server
communication.

2. **Implement the entity**: Finalize the `UserEntity` class and annotate it
with JPA annotations to map it to the corresponding database table. Ensure
getter, setter and constructor is implemented.

3. **Implement the repository**: Define a repository class that implements
IRepository to manage the persistence of `UserEntity`. This repository will
handle basic CRUD operations, allowing the application to create, read, update,
and delete user data in the database. 

4. **Implement the service**: Finalize the `AuthService` class and implement the
`IAuthService` and `IService` interfaces in a service class to handle the
business logic, including tasks like password hashing and token creation. The
service layer will act as a bridge between the resource and repository layers,
ensuring that business rules are applied and data is processed correctly before
being stored or returned.
  - Sample password hash by native java library:
    ```java
    private Optional<String> hashPassword(final String password) {
        try {
            byte[] hashedBytes = MessageDigest.getInstance("SHA-256")
                    .digest(password.getBytes(StandardCharsets.UTF_8));

            return Optional.of(IntStream.range(0, hashedBytes.length)
                    .mapToObj(i -> String.format("%02x", hashedBytes[i]))
                    .collect(Collectors.joining()));
        } catch (NoSuchAlgorithmException e) {
            return Optional.empty();
        }
    }
    ```
    While this example uses SHA-256 for hashing, it is recommended to use more
    secure algorithms like Bcrypt or Argon2 for production environments.
  - JWT Token Creation: Please refer to
    [Java-JWT](https://github.com/auth0/java-jwt?tab=readme-ov-file#create-a-jwt)
    for a detailed guide on creating and verifying JWT tokens.

5. **Implement the resource**: Finalize the `AuthResource` class to define
RESTful endpoints for user registration, login, and token verification. Use
JAX-RS annotations to define these endpoints and specify their behaviors. Inject
the service interface into this resource class to delegate business logic
processing to the service layer. This setup ensures that the resource class
focuses on handling HTTP requests and responses, while the service layer handles
the underlying business logic.

### Requirement:

#### Authorization

* **Endpoint**: _**/api/auth/register**_
* **Method**: `POST`
* * **Request**: JSON object containing user registration details, e.g.:
```json
{
  "name": "sicusa",
  "password": "sensei"
}
```
* **Response**: JSON object of the user info and token, e.g.:
```json
{
  "id": 1,
  "name": "sicusa",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwibmFtZSI6InNpY3VzYSJ9.3g3Cuk9_45qOfgWNJfxc0VUAcBFKTPmT6H-Zz6SqL4w"
}
```

* **Endpoint**: _**/api/auth/login**_
* **Method**: `POST`
* * **Request**: JSON object containing user login details, e.g.:
```json
{
  "name": "sieluna",
  "password": "sensei"
}
```
* **Response**: JSON object of the user info and token, e.g.:

```json
{
  "id": 2,
  "name": "sieluna",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyIiwibmFtZSI6InNpZWx1bmEifQ.AcGg2X8oysEOv0Oc8YxmASPAzNLtLPQENhxldfVXcwg"
}
```

#### Authentication

* **Endpoint**: _**/api/auth**_
* **Method**: `GET`
* **Response**: JSON object of a new refreshed token (and user info), e.g.:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyIiwibmFtZSI6InNpZWx1bmEifQ.AcGg2X8oysEOv0Oc8YxmASPAzNLtLPQENhxldfVXcwg"
}
```

#### JWT Auth Middleware

Implement `ContainerRequestFilter` and implement the `SecurityContext` and set 
into `ContainerRequestContext`.

<details>
  <summary>Hint:</summary>
  <ul>
    <li>Use <code>@Path</code>, <code>@GET</code>, <code>@POST</code>, <code>@DELETE</code> annotations to define the endpoints and specify the response type.</li>
    <li>Build up the entities relationship by <code>@Entity</code>, <code>@Id</code>, <code>@ManyToOne</code>, <code>@OneToMany</code> and <code>@ManyToMany</code>.</li>
  </ul>
</details>

## 3. Implement a Todo List Endpoint

### Instruction:

The todo list is a popular example in web development and serves as an excellent
starting point to understand RESTful API design.

In this task, you will learn the **J**ava EE **P**ersistence **A**PI as defined in
[JSR 317](https://jcp.org/en/jsr/detail?id=317) and refer to the official
documentation in the
**[Jakarta Persistence Tutorial](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/persist/persistence-intro/persistence-intro.html)**,
and serialize classes into JSON refer to the official
documentation in the
**[Jakarta JSON Binding Tutorial](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/web/jsonb/jsonb.html)**.
Implement a minimal API following these guidelines.

### Procedural:

To make the exercise more valuable, we will refer to the
[GitHub project panel](https://docs.github.com/zh/issues/planning-and-tracking-with-projects/learning-about-projects/quickstart-for-projects).

![GitHub Projects](docs/github-projects.png)
*Figure 1: GitHub Projects layout.*

1. **Summarize the GitHub projects page**: the core content includes tasks, task
status and users. Let's place all key components on a canvas and connect them
with lines.

    ```mermaid
      erDiagram
        users {
          int id PK
          string name
        }
        boards {
          int id PK
        }
        user_board {
          int user_id FK
          int board_id FK
        }
        statuses {
          int id PK
          string name
          int board_id FK
        }
        tasks {
          int id PK
          string title
          string description
          datetime startDate
          datetime endDate
          int status_id FK
          int board_id FK
          int assigner_id FK
        }
      
        users ||--o{ tasks: "assigner_id:id"
        boards ||--o{ statuses : "board_id:id"
        boards ||--o{ tasks : "board_id:id"
        statuses ||--o{ tasks : "status_id:id"
        users ||--o{ user_board : "user_id:id"
        boards ||--o{ user_board: "board_id:id"
    ```

2. **Design the entity repository for CRUD operations**: Ensure that the N+1
problem is avoided. Implement the todo list endpoint.

### Requirement:

#### Get all Projects

* **Endpoint**: _**/api/project**_
* **Method**: `GET`
* **Response**: JSON array of projects from the database, e.g.

```json
[
  {
    "id": 1,
    "name": "Project 1",
    "ownerIds": [1]
  },
  {
    "id": 2,
    "name": "Project 2",
    "ownerIds": [1]
  }
]
```

#### Get one Project by ID

* **Endpoint**: _**/api/project/:id**_
* **Method**: `GET`
* **Response**: JSON object of the project with the specified ID, e.g.:
```json
{
  "id": 1,
  "name": "Project 1",
  "ownerIds": [1],
  "tasks": [
    {
      "id": 1,
      "title": "Task",
      "description": "Task description",
      "startDate": "1970-01-01T00:00:00Z",
      "endDate": "1970-01-10T23:59:59Z",
      "status": "Done",
      "projectId": 1,
      "assignerId": 1
    }
  ]
}
```

#### Create a new Project

* **Endpoint**: _**/api/project**_
* **Method**: `POST`
* **Request**: JSON object with new project details, e.g.:
```json
{
  "name": "Project 3"
}
```
* **Response**: JSON object of the newly created project, e.g.:
```json
{
  "id": 3,
  "name": "Project 3",
  "ownerIds": [1]
}
```

#### Get one Task by ID

* **Endpoint**: _**/api/project/tasks/:id**_
* **Method**: `GET`
* **Response**: JSON object of the task with the specified ID, e.g.:
```json
{
  "id": 1,
  "title": "Learn Java",
  "description": "Java is a high-level, class-based, object-oriented programming language.",
  "startDate": "1970-01-01T00:00:00Z",
  "endDate": "1970-01-10T23:59:59Z",
  "status": "Todo",
  "boardId": 1,
  "assignerId": 1
}
```

#### Save one task

* **Endpoint**: _**/api/project/tasks**_
* **Method**: `POST`
* **Request**: JSON object with new task details, e.g.:
```json
{
  "title": "Learn Sleep",
  "description": "zzz...",
  "endDate": "1970-01-10T23:59:59Z",
  "status": "Todo",
  "boardId": 1,
  "assignerId": 1
}
```
* **Response**: JSON object of the newly created task, e.g.:
```json
{
  "id": 3,
  "title": "Learn Sleep",
  "description": "zzz...",
  "startDate": "1970-01-01T00:00:00Z",
  "endDate": "1970-01-10T23:59:59Z",
  "status": "Todo",
  "boardId": 1,
  "assignerId": 1
}
```

#### Update one Task by ID

* **Endpoint**: _**/api/project/tasks/:id**_
* **Method**: `PUT`
* **Request**: JSON object with the fields to update, e.g.:
```json
{
  "status": "Done"
}
```
* **Response**: JSON object of the updated task, e.g.:
```json
{
  "id": 3,
  "title": "Learn Sleep",
  "description": "zzz...",
  "startDate": "1970-01-01T00:00:00Z",
  "endDate": "1970-01-10T23:59:59Z",
  "status": "Done",
  "boardId": 1,
  "assignerId": 1
}
```

#### Remove one task by id

* **Endpoint**: _**/api/project/tasks/:id**_
* **Method**: `DELETE`
* **Response**: Status code indicating successful deletion.

## 3. Let's Design a Web Page

### Instruction:

In this task, we aim to create a web page that allows users to manage their todo
list. To achieve this, we will utilize web components. Given that tasks and
their statuses are highly independent and require interactivity, web components
will enable efficient encapsulation of the logic and behavior for these
interactive elements. This will allow users to drag and drop tasks between
different statuses seamlessly.

### Details:

* **Endpoint**: _**/**_
* **Method**: `GET`

> [!IMPORTANT]  
> This task is optional as it does not have a prerequisite for web development
> skills. You can either create your own front-end implementation or use the
> provided ready-made implementation.

## 4. Make Server Data Flow

### Instruction:

Server-Sent Events (SSE) and WebSockets are essential technologies in modern web
applications. While REST APIs work well for many tasks, SSE and WebSockets are
ideal for applications that need real-time updates, such as flight tracking, web
camera and chat rooms. In this exercise, we will use SSE since it does not
require two-way communication.

### Procedural:

1. **Singleton the SseBroadcaster**: Mark resource or service as Singleton.

2. **Register sink and broadcast message**: Each client is a **SseEventSink**,
The **SseBroadcaster** required to distribute event to all client. refer to:
[Jakarta SSE Tutorial](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/websvcs/jaxrs-client/jaxrs-client003.html#_using_server_sent_events)

### Details:

* **Endpoint**: _**/api/project/tasks**_
* **Method**: `GET`
* **Response**: JSON object with the fields to update by SSE data flow.
