- [The Introduction Of The JAX-RS](#introduction)
- [Task List](#tasks)
  - [Implement a Heartbeat Endpoint](#1-implement-a-heartbeat-endpoint)
  - [Implement a Todo List Endpoint](#2-implement-a-todo-list-endpoint)
  - [Let's Design a Web Page](#3-lets-design-a-web-page)
  - [Make Server Data Flow](#4-make-server-data-flow)

# Introduction

Java EE (renamed as Jakarta EE) embodies the principle that "third-tier
companies make products, second-tier companies design technology, and first-tier
companies set standards." Though it has greatly benefited the Java community as
an official standard, its practical implementation has lagged, making it more of
a reference than a standard. Nevertheless, it remains a valuable resource due to
its comprehensive documentation and enterprise-focused development approach.

JAX-RS, or Java API for RESTful Web Services, is a part of Java EE that
standardizes the creation of RESTful web services, simplifying the development
of HTTP-based services. It was introduced in December 2009 with Java EE 6 under
[JSR 311](https://jcp.org/en/jsr/detail?id=311).

In this exercise, we will delve into few core specifications in Java EE through
its documentation and implement the required logic within the framework. After
learning these coding rules and design specifications, you should be able to
easily master all Java web frameworks. 

> [!NOTE]  
> The transition from javax to jakarta happened because Oracle transferred Java
> EE specifications to the Eclipse Foundation, which required a new namespace
> due to Oracle retaining rights to javax.
![JavaEE API Relocated](docs/javax-to-jakarta.png)

# Tasks

## 1. Implement a Heartbeat Endpoint

### Instruction:

For a professional web service, especially in a multi-service application,
creating a heartbeat endpoint is important. This endpoint will help indicate
that the server is alive and functional by returning the current server time.
Given that there are multiple fields, we can encapsulate them into a JSON object.

In this exercise, we will learn the Java EE REST API
[JSR 311](https://jcp.org/en/jsr/detail?id=311), official document
**[Jakarta REST Tutorial](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/websvcs/jaxrs/jaxrs.html)**
and utilize the Java EE JSON API
[JSR 353](https://jcp.org/en/jsr/detail?id=353), official document
**[Jakarta JSON Processing Tutorial](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/web/jsonp/jsonp.html)**

### Requirement:

* Endpoint: _**/api/heartbeat**_
* Method: `GET`
* Response: JSON object containing the current server time, e.g.:
  ```json
  {
    "status": "alive",
    "time": "1970-01-01T00:00:00Z"
  }
  ```

### Procedural:

1. Set up your project environment and run Gradle sync to install dependencies
correctly.

> [!Important]
> In order to unify the environment requirements, the project currently using
> the Java SE environment.

2. Define the endpoint using JAX-RS annotations and implement the logic in
`resources/HeartBeatResource.java`.

<details>
  <summary>Hint:</summary>
  <ul>
    <li>Use <code>@Path</code>, <code>@GET</code>, and <code>@Produces</code> annotations to define the endpoint and specify the response type.</li>
    <li>Utilize <code>LocalDateTime.now()</code> to get the current server time. Required <code>ISO</code> standard date time.</li>
    <li>Use <code>Json.createObjectBuilder</code> or <code>Json.createParser</code> to parse json object.</li>
  </ul>
</details>

## 2. Implement a Todo List Endpoint

### Instruction:

The todo list is one of the most popular sample cases in web development, but it
also serves as an excellent start point example to understand the essence of
RESTful API design.

By working on this task, you need learn **J**ava EE **P**ersistence **A**PI
[JSR 317](https://jcp.org/en/jsr/detail?id=317), official document
**[Jakarta Persistence Tutorial](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/persist/persistence-intro/persistence-intro.html)**,
serialize class into json by
**[Jakarta JSON Binding Tutorial](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/web/jsonb/jsonb.html)**, and
create minimal api.

### Procedural:

To make the exercise more interesting, we refer to the
[GitHub project panel](https://docs.github.com/zh/issues/planning-and-tracking-with-projects/learning-about-projects/quickstart-for-projects).

![Github Projects](docs/github-projects.png)

1. Summarize the GitHub projects page, the core content includes task, task
status and user. Then place all key components in canvas and connect by lines.

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

2. Design the entity repository for CRUD operations, need avoid N+1 problem.
Implement the todo list end point.

<details>
  <summary>Hint:</summary>
  <ul>
    <li>Use <code>@Path</code>, <code>@GET</code>, <code>@POST</code>, <code>@DELETE</code>, and <code>@Produces</code> annotations to define the endpoints and specify the response type.</li>
    <li>Use a <code>List</code> or <code>Map</code> to store todo items. Each item should have an ID, task description, and completion status.</li>
  </ul>
</details>

## 3. Let's Design a Web Page

### Instruction:

In modern web development, there is a wide variety of client-side frameworks and
libraries to build dynamic user interfaces. However, in certain scenarios,
especially when dealing with a single client component, simple logic, and
lightweight payloads, server-side rendering (SSR) proves to be an efficient and
effective solution. SSR can offer several advantages such as improved
performance, better SEO, and easier maintenance.

In this task, we will implement a web page for our todo list application using
server-side rendering. This will involve creating an endpoint that serves an
HTML page. The HTML page will allow users to view their todo list and add new
tasks.

### Details:

* Endpoint: _**/**_
* Method: `GET`
* Response: An HTML page displaying the list of todos and a form to add new tasks.
  - A table or list to display the current todo items.
  - A form with an input field for adding new tasks.
  - Include buttons for update or delete tasks.

## 4. Make Server Data Flow

### Instruction:

In reality, a server often does not serve only one person, we need to make the
data flow. 