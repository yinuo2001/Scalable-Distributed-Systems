# Homework 2b: Servlets

**Servlet**: middleware to process HTTP requests; between client and backend

**Common Gateway Interface (CGI) & Servlet**

CGI: launch a new **process** each time
- high overhead, slow performance, not scalable

Servlet: Instead of creating a new process, Tomcat routes the request to an already running Servlet.
- execute doGet() & doPost() in separate *threads*
- lightweight, fast & scalable, persistent



http://localhost:8080/homework2b_war/skiers/12/seasons/2019/day/1/skier/123

example post request body:

{
  "time": 217,
  "liftID": 21
}
