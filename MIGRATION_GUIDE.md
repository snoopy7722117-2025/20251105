# Struts 1.0 to Spring Boot Migration Guide

## Overview
This project demonstrates the migration path from Apache Struts 1.0 to Spring Boot 3.2.0, highlighting the key differences and improvements in modern Java web application development.

## Key Migration Concepts

### 1. Configuration: XML → Annotations

**Struts 1.0:**
```xml
<!-- web.xml -->
<servlet>
    <servlet-name>action</servlet-name>
    <servlet-class>org.apache.struts.action.ActionServlet</servlet-class>
    <init-param>
        <param-name>config</param-name>
        <param-value>/WEB-INF/struts-config.xml</param-value>
    </init-param>
</servlet>

<!-- struts-config.xml -->
<action-mappings>
    <action path="/user" 
            type="com.example.UserAction" 
            name="userForm">
        <forward name="success" path="/user.jsp"/>
    </action>
</action-mappings>
```

**Spring Boot:**
```java
@SpringBootApplication
public class SpringBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootApplication.class, args);
    }
}

@Controller
@RequestMapping("/users")
public class UserController {
    @GetMapping
    public String listUsers(Model model) {
        return "users/list";
    }
}
```

### 2. Action Classes → Controllers

**Struts 1.0:**
```java
public class UserAction extends Action {
    public ActionForward execute(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        UserForm userForm = (UserForm) form;
        // Business logic
        return mapping.findForward("success");
    }
}
```

**Spring Boot:**
```java
@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    
    @PostMapping
    public String createUser(@Valid @ModelAttribute User user,
                           BindingResult result) {
        if (result.hasErrors()) {
            return "users/form";
        }
        userService.saveUser(user);
        return "redirect:/users";
    }
}
```

### 3. ActionForm → Model/Entity Classes

**Struts 1.0:**
```java
public class UserForm extends ActionForm {
    private String username;
    private String email;
    
    public ActionErrors validate(ActionMapping mapping,
                                HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (username == null || username.length() < 3) {
            errors.add("username", 
                new ActionError("error.username.required"));
        }
        return errors;
    }
}
```

**Spring Boot:**
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;
    
    @Email(message = "Email should be valid")
    private String email;
    
    // Getters and setters
}
```

### 4. Data Access: Manual DAO → Spring Data JPA

**Struts 1.0:**
```java
public class UserDAO {
    public List<User> getAllUsers() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM users");
            rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                users.add(user);
            }
        } finally {
            // Close resources
        }
        return users;
    }
}
```

**Spring Boot:**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    // No implementation needed - Spring Data JPA provides it
}
```

### 5. View Layer: JSP → Thymeleaf

**Struts 1.0 JSP:**
```jsp
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:form action="/user">
    <html:text property="username"/>
    <html:errors property="username"/>
    <html:submit/>
</html:form>
```

**Spring Boot Thymeleaf:**
```html
<form th:action="@{/users}" th:object="${user}" method="post">
    <input type="text" th:field="*{username}" />
    <span th:if="${#fields.hasErrors('username')}" 
          th:errors="*{username}">Error</span>
    <button type="submit">Submit</button>
</form>
```

### 6. Validation

**Struts 1.0:**
- Validation in ActionForm.validate() method
- Or using Validator framework with validation.xml

**Spring Boot:**
- JSR-303/Bean Validation annotations
- @Valid annotation in controller methods
- Automatic validation by Spring MVC

### 7. Dependency Injection

**Struts 1.0:**
- Manual instantiation and lookup
- ServiceLocator pattern
- Manual resource management

**Spring Boot:**
- @Autowired, @Component, @Service, @Repository
- Constructor injection (recommended)
- Automatic lifecycle management

## Benefits of Spring Boot Migration

1. **Less Configuration**: Convention over configuration, auto-configuration
2. **Modern Standards**: Uses current Java standards (JPA, Bean Validation, etc.)
3. **Better Testing**: Built-in support for unit and integration testing
4. **Embedded Server**: No need for external servlet container
5. **Production Ready**: Built-in metrics, health checks, and monitoring
6. **Active Community**: Large, active community and regular updates
7. **Microservices Ready**: Easy to build microservices architecture

## Project Structure Comparison

### Struts 1.0 Structure:
```
webapp/
├── WEB-INF/
│   ├── web.xml
│   ├── struts-config.xml
│   ├── validation.xml
│   ├── classes/
│   │   └── com/example/
│   │       ├── action/
│   │       ├── form/
│   │       └── dao/
│   └── lib/
└── jsp/
```

### Spring Boot Structure:
```
src/
├── main/
│   ├── java/
│   │   └── com/example/springbootapp/
│   │       ├── controller/
│   │       ├── model/
│   │       ├── repository/
│   │       ├── service/
│   │       └── SpringBootApplication.java
│   └── resources/
│       ├── application.properties
│       ├── templates/
│       └── static/
└── test/
```

## Running the Application

### Build and Run:
```bash
# Using Maven
mvn clean install
mvn spring-boot:run

# Or using the generated JAR
java -jar target/struts-to-springboot-1.0.0.jar
```

### Access the Application:
- Main application: http://localhost:8080/
- User management: http://localhost:8080/users
- H2 Console: http://localhost:8080/h2-console

## Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## Migration Strategy

When migrating a real Struts 1.0 application:

1. **Analyze Current Application**
   - Document all Actions, ActionForms, and JSPs
   - Identify business logic vs presentation logic
   - Map URL patterns and forwards

2. **Set Up Spring Boot Project**
   - Create new Spring Boot project with required dependencies
   - Set up project structure

3. **Migrate in Phases**
   - Phase 1: Data layer (DAO → Repository)
   - Phase 2: Business logic (Extract from Actions → Services)
   - Phase 3: Controllers (Actions → Controllers)
   - Phase 4: Views (JSP → Thymeleaf/JSP)

4. **Test Thoroughly**
   - Write unit tests for each component
   - Integration tests for end-to-end flows
   - Performance testing

5. **Deploy and Monitor**
   - Use Spring Boot Actuator for monitoring
   - Configure logging appropriately
   - Set up health checks

## Conclusion

The migration from Struts 1.0 to Spring Boot represents a significant modernization of a Java web application. While the initial effort may seem substantial, the benefits in terms of maintainability, testability, and developer productivity make it worthwhile.
