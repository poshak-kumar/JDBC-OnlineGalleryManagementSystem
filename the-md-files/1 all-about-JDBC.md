Java Database Connectivity (JDBC) is a Java API that allows Java programs to interact with relational databases. It provides methods to connect to a database, execute queries, and retrieve and update data in a database. JDBC is crucial for developing Java applications that interact with databases like MySQL, Oracle, PostgreSQL, SQL Server, and others.

To run the Java JDBC program you need to setup the some tools (like: IDE, SQL Driver, etc.), to know about the required tools to run JDBC program and (Running JDBC programs in VS Code) then you refer this docs [click here](4%20setup-jdbc-tools.md)

Let’s go through JDBC step by step:

### 1. **JDBC Architecture**

To learn in detail about **How JDBC Works - Architecture of JDBC** then [click here](./2%20JDBC-architecture.md)

JDBC follows a layered architecture that separates the application from the database. It consists of two layers:
- **JDBC API:** Defines the interfaces for communication between Java applications and databases. It provides methods for querying and updating data.
- **JDBC Driver API:** Handles communication with the database server. It is responsible for establishing a connection and sending SQL queries.

### 2. **JDBC Drivers**

JDBC uses drivers to interact with a specific database. There are four types of JDBC drivers:

- **Type 1 (JDBC-ODBC Bridge Driver):** This driver translates JDBC method calls to ODBC (Open Database Connectivity) function calls. It requires the ODBC driver to be installed on the client machine.
- **Type 2 (Native-API Driver):** This driver converts JDBC calls into native calls of the database-specific API.
- **Type 3 (Network Protocol Driver):** This driver converts JDBC calls into a database-independent protocol, which is then forwarded to a server that translates them into database-specific API calls.
- **Type 4 (Thin Driver):** This is a pure Java driver that directly converts JDBC calls to the database-specific protocol.

Type 4 is the most commonly used driver because it is platform-independent.

### 3. **JDBC Components**

To learn about **Some important classes and interfaces in JDBC** in detail then [click here](./3%20JDBC-imp-classes-interfaces.md)

The JDBC API contains the following key components:

#### 3.1. **DriverManager**
- It is a class that manages a list of database drivers.
- It matches connection requests from the Java application with the appropriate database driver using a protocol specified in the `Connection` request.

```java
Connection conn = DriverManager.getConnection(url, user, password);
```

#### 3.2. **Connection**
- Represents a connection with the database.
- It is used to create `Statement` objects for executing SQL queries.

```java
Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbname", "root", "password");
```

#### 3.3. **Statement**
- Used to execute SQL queries. It is created from the `Connection` object.
- There are three types of statements:
  - **Statement:** Used for executing simple SQL queries without parameters.
  - **PreparedStatement:** Used for executing precompiled SQL queries with input parameters.
  - **CallableStatement:** Used for executing stored procedures.

```java
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM users");
```

#### 3.4. **PreparedStatement**
- Extends the `Statement` class but allows SQL queries to accept parameters. It is safer and more efficient, especially when dealing with user input, because it prevents SQL injection.

```java
String query = "SELECT * FROM users WHERE id = ?";
PreparedStatement pstmt = conn.prepareStatement(query);
pstmt.setInt(1, 101);
ResultSet rs = pstmt.executeQuery();
```

#### 3.5. **CallableStatement**
- Used to execute stored procedures in the database.

```java
CallableStatement cstmt = conn.prepareCall("{call myProcedure(?)}");
cstmt.setInt(1, 100);
cstmt.execute();
```

#### 3.6. **ResultSet**
- A `ResultSet` is an object that holds the data retrieved from a database after executing an SQL query. It acts like an iterator to go through the data.
- It is created by executing an SQL query using a `Statement` or `PreparedStatement` object.

```java
ResultSet rs = stmt.executeQuery("SELECT * FROM users");
while (rs.next()) {
    int id = rs.getInt("id");
    String name = rs.getString("name");
    System.out.println(id + " " + name);
}
```

#### 3.7. **SQLException**
- The `SQLException` class handles database-related errors.
- It's important to catch and handle this exception properly when working with JDBC.

```java
try {
    // JDBC code
} catch (SQLException e) {
    e.printStackTrace();
}
```

### 4. **JDBC Steps to Connect and Execute Queries**

Here’s a step-by-step guide on how to connect to a database and execute SQL queries using JDBC:

#### 4.1. Load the Driver
In earlier versions of JDBC, you had to explicitly load the JDBC driver class using `Class.forName()`:

```java
Class.forName("com.mysql.cj.jdbc.Driver");
```

Since JDBC 4.0, the drivers are automatically loaded when using `DriverManager`.

#### 4.2. Establish a Connection
Use the `DriverManager.getConnection()` method to establish a connection to the database:

```java
Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase", "root", "password");
```

#### 4.3. Create a Statement or PreparedStatement
To execute a SQL query, you need to create a `Statement` or `PreparedStatement` object:

```java
Statement stmt = conn.createStatement();
// or
PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
```

#### 4.4. Execute the Query
Once the statement is prepared, you can execute the SQL query:

```java
ResultSet rs = stmt.executeQuery("SELECT * FROM users");
// or
pstmt.setInt(1, 101);
ResultSet rs = pstmt.executeQuery();
```

#### 4.5. Process the Result
Use the `ResultSet` object to process the data returned by the query:

```java
while (rs.next()) {
    int id = rs.getInt("id");
    String name = rs.getString("name");
    System.out.println(id + " " + name);
}
```

#### 4.6. Close the Resources
Finally, always close the `ResultSet`, `Statement`, and `Connection` to release the database resources:

```java
rs.close();
stmt.close();
conn.close();
```

### 5. **Transaction Management in JDBC**

- By default, each SQL query is executed in auto-commit mode, which means it’s committed automatically after execution.
- You can control transactions manually by disabling auto-commit mode and explicitly committing or rolling back transactions.

```java
conn.setAutoCommit(false);  // Disable auto-commit

// Execute queries

conn.commit();  // Commit the transaction
conn.rollback();  // Rollback if something goes wrong
```

### 6. **Batch Processing in JDBC**

Batch processing allows you to execute multiple queries together, reducing the number of database round-trips, which improves performance.

```java
PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (name, age) VALUES (?, ?)");
pstmt.setString(1, "John");
pstmt.setInt(2, 30);
pstmt.addBatch();  // Add first query to batch

pstmt.setString(1, "Jane");
pstmt.setInt(2, 25);
pstmt.addBatch();  // Add second query to batch

pstmt.executeBatch();  // Execute all queries in the batch
```

### 7. **Connection Pooling**

Connection pooling improves performance by reusing existing database connections instead of creating new ones for every request. Popular libraries for connection pooling include:
- **HikariCP**
- **Apache DBCP**
- **C3P0**

Connection pooling is often used in production environments for large-scale applications.

### 8. **JDBC Best Practices**

- Always close resources (`Connection`, `Statement`, `ResultSet`) in a `finally` block or use a try-with-resources statement to ensure they are closed properly.
- Use `PreparedStatement` over `Statement` to prevent SQL injection and to improve performance.
- Use connection pooling for better performance in enterprise applications.

### Example Code: JDBC with MySQL

```java
import java.sql.*;

public class JdbcExample {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mydatabase";
        String user = "root";
        String password = "password";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            while (rs.next()) {
                System.out.println(rs.getInt("id") + " " + rs.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

### Conclusion

JDBC is a powerful API for connecting Java applications to relational databases. By understanding its components—`DriverManager`, `Connection`, `Statement`, `PreparedStatement`, `CallableStatement`, `ResultSet`—you can interact with databases efficiently and securely. Using connection pooling, batch processing, and proper resource management helps optimize performance in real-world applications.