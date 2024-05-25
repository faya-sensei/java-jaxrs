- [The Introduction Of The JAX-RS](#introduction)
- [Task List](#tasks)
    - [Heart Beat Package](#1-implement-a-heartbeat-endpoint)

# Introduction

Java EE (renamed as Jakarta EE) exemplifies the principle that "third-tier
companies make products, second-tier companies design technology, and first-tier
companies set standards." While it has significantly contributed to the Java
community as an official standard, its practical implementation has lagged,
leading to its gradual decline. Despite this, it remains a useful resource due
to its extensive documentation and enterprise-style development approach.

JAX-RS, or Java API for RESTful Web Services, is a part of Java EE that
standardizes the creation of RESTful web services, simplifying the development
of HTTP-based services. It was introduced in December 2009 with Java EE 6 under
[JSR 311](https://jcp.org/en/jsr/detail?id=311).

In this exercise, we will delve into the JAX-RS specifications through its
documentation and implement the required logic within the framework. This
exercise is designed to leverage the comprehensive documentation and enterprise
development practices, making it a common task in many production projects.

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

### Details:

* Endpoint: /heartbeat
* Method: GET
* Response: JSON object containing the current server time, e.g.:
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
  </ul>
</details>
