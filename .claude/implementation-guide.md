# Vortex Java SDK Integration Guide

This guide provides step-by-step instructions for integrating Vortex into a Java application using the Java SDK.

## SDK Information

- **Group ID**: `com.vortexsoftware`
- **Artifact ID**: `vortex-java-sdk`
- **Version**: `1.1.0+`
- **Requires**: Java 17+
- **Build Tool**: Maven or Gradle
- **Type**: Backend SDK with Spring Boot auto-configuration

## Expected Input Context

This guide expects to receive the following context from the orchestrator:

### Integration Contract
```yaml
Integration Contract:
  API Endpoints:
    Prefix: /api/vortex
    JWT: POST {prefix}/jwt
    Get Invitations: GET {prefix}/invitations
    Get Invitation: GET {prefix}/invitations/:id
    Accept Invitations: POST {prefix}/invitations/accept
  Scope:
    Entity: "workspace"
    Type: "workspace"
    ID Field: "workspace.id"
  File Paths:
    Backend:
      Vortex Config: src/main/java/com/yourapp/config/VortexConfiguration.java
      Main App: src/main/java/com/yourapp/Application.java
      Properties: src/main/resources/application.yml
  Authentication:
    Pattern: "JWT Bearer token" (or Spring Security, etc.)
    User Extraction: SecurityContextHolder.getContext().getAuthentication()
  Database:
    ORM: "Spring Data JPA" | "MyBatis" | "JDBC Template" | "Hibernate"
    User Model: User entity
    Membership Model: WorkspaceMember entity (or equivalent)
```

### Discovery Data
- Backend technology stack (Java version, Spring Boot version)
- Web framework (Spring Boot, Spring MVC, Jakarta EE, Micronaut, Quarkus, etc.)
- Database ORM/library
- Authentication framework in use
- Existing controller/endpoint structure
- Properties file location (application.yml, application.properties)

## Implementation Overview

The Java SDK provides two integration modes:

1. **Spring Boot Auto-Configuration** (Recommended): Automatic setup with minimal configuration
2. **Manual Integration**: Full control using VortexClient directly

Both modes provide:
- JWT generation
- Invitation management API calls
- Complete endpoint implementations

## Critical Java SDK Specifics

### Key Patterns
- **Auto-Configuration**: Spring Boot automatically configures VortexClient and controllers
- **VortexConfig Interface**: Implement for authentication and authorization
- **Exception Handling**: All methods throw `VortexException`
- **Resource Management**: VortexClient implements AutoCloseable (use try-with-resources)
- **JWT Generation**: `generateJwt(Map<String, Object>)` with User object and optional extras
- **Type Safety**: Full Java types with generics

### Spring Boot Pattern (Recommended)
```java
// application.yml
vortex:
  api:
    key: ${VORTEX_API_KEY}

// Configuration
@Bean
public VortexConfig vortexConfig() {
    return new VortexConfig() {
        @Override
        public VortexUser authenticateUser() {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) return null;

            String userId = auth.getName();
            String userEmail = getUserEmail(auth);
            Boolean isAdmin = hasAdminRole(auth);

            return new VortexUser(userId, userEmail, isAdmin);
        }

        @Override
        public boolean authorizeOperation(String operation, VortexUser user) {
            return user != null;
        }

        private String getUserEmail(Authentication auth) {
            // Extract email from UserDetails
            Object principal = auth.getPrincipal();
            if (principal instanceof UserDetails) {
                // If using Spring Security UserDetails with email
                // Cast to your custom UserDetails implementation
                // return ((MyUserDetails) principal).getEmail();
            }
            // Fallback: use username as email (adjust for your setup)
            return auth.getName() + "@example.com";
        }

        private Boolean hasAdminRole(Authentication auth) {
            return auth.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        }
    };
}
```

## Step-by-Step Implementation

### Step 1: Install SDK

#### Maven

Add to your `pom.xml`:

```xml
<dependency>
    <groupId>com.vortexsoftware</groupId>
    <artifactId>vortex-java-sdk</artifactId>
    <version>1.1.0</version>
</dependency>
```

#### Gradle

Add to your `build.gradle`:

```gradle
dependencies {
    implementation 'com.vortexsoftware:vortex-java-sdk:1.1.0'
}
```

### Step 2: Set Up Environment Variables

Add to your environment or `.env` file:

```bash
VORTEX_API_KEY=VRTX.your-api-key-here.secret
```

**IMPORTANT**: Never commit your API key to version control.

### Step 3: Configure Application Properties

#### Option A: Spring Boot Auto-Configuration (Recommended)

Add to `src/main/resources/application.yml`:

```yaml
vortex:
  api:
    key: ${VORTEX_API_KEY}
    base-url: https://api.vortexsoftware.com  # optional
```

Or `application.properties`:

```properties
vortex.api.key=${VORTEX_API_KEY}
vortex.api.base-url=https://api.vortexsoftware.com
```

#### Option B: Manual Configuration

Create `src/main/java/com/yourapp/config/VortexConfiguration.java`:

```java
package com.yourapp.config;

import com.vortexsoftware.sdk.VortexClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VortexConfiguration {

    @Bean
    public VortexClient vortexClient(@Value("${vortex.api.key}") String apiKey) {
        return new VortexClient(apiKey);
    }
}
```

### Step 4: Implement Authentication Configuration

Create `VortexConfig` implementation to connect to your authentication system:

```java
package com.yourapp.config;

import com.vortexsoftware.sdk.spring.VortexConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class VortexSecurityConfiguration {

    @Bean
    public VortexConfig vortexConfig() {
        return new VortexConfig() {

            @Override
            public VortexUser authenticateUser() {
                // Extract user from Spring Security context
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication == null || !authentication.isAuthenticated()) {
                    return null;
                }

                // Get your user details
                String userId = authentication.getName(); // Or get from principal
                String userEmail = getUserEmail(authentication); // Your method to extract email
                Boolean isAdmin = hasAdminRole(authentication); // Your method to check admin

                return new VortexUser(userId, userEmail, isAdmin);
            }

            @Override
            public boolean authorizeOperation(String operation, VortexUser user) {
                // Implement your authorization logic
                if (user == null) {
                    return false;
                }

                // Example: Only admins can revoke invitations
                if ("REVOKE_INVITATION".equals(operation)) {
                    return user.getUserIsAutojoinAdmin() != null && user.getUserIsAutojoinAdmin();
                }

                // Allow all other operations for authenticated users
                return true;
            }
        };
    }

    private String getUserEmail(Authentication authentication) {
        // Extract email from your UserDetails or custom principal
        // Example: return ((MyUserDetails) authentication.getPrincipal()).getEmail();
        return "user@example.com"; // Implement your logic
    }

    private Boolean hasAdminRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
    }
}
```

### Step 5: Implement Accept Invitations Endpoint (CRITICAL)

The accept invitations endpoint requires custom business logic to add users to your database. You MUST override the default controller:

```java
package com.yourapp.controller;

import com.vortexsoftware.sdk.VortexClient;
import com.vortexsoftware.sdk.VortexException;
import com.vortexsoftware.sdk.spring.VortexConfig;
import com.vortexsoftware.sdk.types.*;
import com.yourapp.entity.WorkspaceMember;
import com.yourapp.repository.WorkspaceMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vortex")
public class CustomVortexController {

    private static final Logger logger = LoggerFactory.getLogger(CustomVortexController.class);

    private final VortexClient vortexClient;
    private final VortexConfig vortexConfig;
    private final WorkspaceMemberRepository workspaceMemberRepository; // Your repository

    public CustomVortexController(
            VortexClient vortexClient,
            VortexConfig vortexConfig,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.vortexClient = vortexClient;
        this.vortexConfig = vortexConfig;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    /**
     * Override accept invitations endpoint with custom database logic
     * POST /api/vortex/invitations/accept
     */
    @PostMapping("/invitations/accept")
    public ResponseEntity<?> acceptInvitations(@RequestBody AcceptInvitationRequest request) {
        try {
            // 1. Check authentication
            VortexConfig.VortexUser user = vortexConfig.authenticateUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication required", "code", "UNAUTHORIZED"));
            }

            if (!vortexConfig.authorizeOperation("ACCEPT_INVITATIONS", user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to accept invitations", "code", "FORBIDDEN"));
            }

            // 2. Accept invitations via Vortex API
            List<InvitationResult> results = vortexClient.acceptInvitations(
                    request.getInvitationIds(),
                    request.getTarget()
            );

            // 3. Add user to your database for each group
            // THIS IS CRITICAL - ADJUST BASED ON YOUR ORM/DATABASE LIBRARY

            for (InvitationResult result : results) {
                for (InvitationGroup group : result.getGroups()) {
                    // Example with Spring Data JPA:
                    WorkspaceMember member = new WorkspaceMember();
                    member.setUserId(user.getUserId());
                    member.setWorkspaceId(group.getGroupId()); // Customer's group ID
                    member.setRole("member");
                    member.setJoinedAt(LocalDateTime.now());

                    workspaceMemberRepository.save(member);
                }
            }

            // Example with MyBatis:
            // for (InvitationResult result : results) {
            //     for (InvitationGroup group : result.getGroups()) {
            //         WorkspaceMember member = new WorkspaceMember();
            //         member.setUserId(user.getUserId());
            //         member.setWorkspaceId(group.getGroupId());
            //         member.setRole("member");
            //         member.setJoinedAt(LocalDateTime.now());
            //
            //         workspaceMemberMapper.insert(member);
            //     }
            // }

            // Example with JdbcTemplate:
            // for (InvitationResult result : results) {
            //     for (InvitationGroup group : result.getGroups()) {
            //         jdbcTemplate.update(
            //             "INSERT INTO workspace_members (user_id, workspace_id, role, joined_at) VALUES (?, ?, ?, ?)",
            //             user.getUserId(), group.getGroupId(), "member", LocalDateTime.now()
            //         );
            //     }
            // }

            // 4. Return success
            return ResponseEntity.ok(results);

        } catch (VortexException e) {
            logger.error("Failed to accept invitations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to accept invitations", "code", "INTERNAL_ERROR"));
        }
    }
}
```

### Step 6: Using Auto-Configured Endpoints

If using Spring Boot auto-configuration without custom accept logic, the SDK automatically provides these endpoints:

```
POST   /api/vortex/jwt                                      - Generate JWT
GET    /api/vortex/invitations                             - Get invitations by target
GET    /api/vortex/invitations/{invitationId}              - Get invitation by ID
DELETE /api/vortex/invitations/{invitationId}              - Revoke invitation
POST   /api/vortex/invitations/accept                      - Accept invitations
GET    /api/vortex/invitations/by-group/{type}/{id}        - Get group invitations
DELETE /api/vortex/invitations/by-group/{type}/{id}        - Delete group invitations
POST   /api/vortex/invitations/{invitationId}/reinvite     - Reinvite user
```

### Step 7: Add CORS Configuration (If Needed)

If your frontend is on a different domain:

```java
package com.yourapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfiguration {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin(System.getenv().getOrDefault("FRONTEND_URL", "http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/vortex/**", config);

        return new CorsFilter(source);
    }
}
```

## Build and Validation

### Build Your Application

```bash
# Maven
mvn clean package

# Gradle
./gradlew build
```

### Test the Integration

Start your server and test each endpoint:

```bash
# Start server
java -jar target/your-app.jar

# Test JWT endpoint
curl -X POST http://localhost:8080/api/vortex/jwt \
  -H "Authorization: Bearer YOUR_AUTH_TOKEN"

# Test get invitations
curl -X GET "http://localhost:8080/api/vortex/invitations?targetType=email&targetValue=user@example.com" \
  -H "Authorization: Bearer YOUR_AUTH_TOKEN"

# Test accept invitations
curl -X POST http://localhost:8080/api/vortex/invitations/accept \
  -H "Authorization: Bearer YOUR_AUTH_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "invitationIds": ["invitation-id-1"],
    "target": { "type": "email", "value": "user@example.com" }
  }'
```

### Validation Checklist

- [ ] SDK dependency added to pom.xml or build.gradle
- [ ] Environment variable `VORTEX_API_KEY` is set
- [ ] application.yml or application.properties configured
- [ ] VortexConfig implementation created with authenticateUser
- [ ] Custom accept invitations endpoint with database logic
- [ ] All endpoints return expected responses
- [ ] Authentication works correctly
- [ ] CORS configured (if frontend on different domain)
- [ ] Application builds without errors

## Implementation Report

After completing the integration, provide this summary:

```markdown
## Java SDK Integration Complete

### Files Modified/Created
- `pom.xml` - Added Vortex SDK dependency
- `src/main/resources/application.yml` - Configured Vortex API key
- `src/main/java/com/yourapp/config/VortexSecurityConfiguration.java` - VortexConfig implementation
- `src/main/java/com/yourapp/controller/CustomVortexController.java` - Custom accept invitations endpoint

### Endpoints Registered
- POST /api/vortex/jwt - Generate JWT for authenticated user
- GET /api/vortex/invitations - Get invitations by target
- GET /api/vortex/invitations/{id} - Get invitation by ID
- POST /api/vortex/invitations/accept - Accept invitations (custom logic)
- DELETE /api/vortex/invitations/{id} - Revoke invitation
- POST /api/vortex/invitations/{id}/reinvite - Resend invitation
- GET /api/vortex/invitations/by-group/{type}/{id} - Get invitations for group
- DELETE /api/vortex/invitations/by-group/{type}/{id} - Delete invitations for group

### Database Integration
- ORM: [Spring Data JPA/MyBatis/JdbcTemplate/etc.]
- Accept invitations adds users to: [WorkspaceMember entity/table]
- Group association field: [workspaceId/teamId/etc.]

### Authentication
- Framework: [Spring Security/Custom/etc.]
- User extraction: SecurityContextHolder.getContext().getAuthentication()
- Admin scope detection: [ROLE_ADMIN authority check]

### Next Steps for Frontend
The backend now exposes these endpoints for the frontend to consume:
1. Call POST /api/vortex/jwt to get JWT for Vortex widget
2. Pass JWT to Vortex widget component
3. Widget will handle invitation sending
4. Accept invitations via POST /api/vortex/invitations/accept
```

## Common Issues and Solutions

### Issue: "Could not find artifact com.vortexsoftware:vortex-java-sdk"
**Solution**: Ensure Maven Central is in your repositories and the version exists:
```xml
<repositories>
    <repository>
        <id>central</id>
        <url>https://repo.maven.apache.org/maven2</url>
    </repository>
</repositories>
```

### Issue: "VortexConfig bean not found"
**Solution**: Make sure you've created a @Bean for VortexConfig:
```java
@Bean
public VortexConfig vortexConfig() {
    return new VortexConfig() { /* implementation */ };
}
```

### Issue: "Authentication always returns null"
**Solution**: Ensure Spring Security is configured and authentication is set before Vortex endpoints are called.

### Issue: "VortexException: Invalid API key format"
**Solution**: Ensure your API key is in the correct format: `VRTX.base64id.key`

### Issue: "Accept invitations succeeds but user not added to database"
**Solution**: You must override the accept invitations endpoint with custom database logic (see Step 5).

### Issue: "CORS errors from frontend"
**Solution**: Add CORS configuration (see Step 7).

### Issue: "Endpoints not found (404)"
**Solution**: Ensure Spring Boot component scanning includes the SDK packages or use explicit @Import:
```java
@SpringBootApplication
@Import(VortexAutoConfiguration.class)
public class Application { }
```

## Best Practices

### 1. Environment Variables
Use Spring's property resolution:
```yaml
vortex:
  api:
    key: ${VORTEX_API_KEY:#{null}}
```

### 2. Exception Handling
Use @ExceptionHandler for VortexException:
```java
@RestControllerAdvice
public class VortexExceptionHandler {

    @ExceptionHandler(VortexException.class)
    public ResponseEntity<Map<String, String>> handleVortexException(VortexException e) {
        logger.error("Vortex error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Vortex operation failed", "code", "INTERNAL_ERROR"));
    }
}
```

### 3. Admin Scopes
Only grant autojoin to administrators:
```java
@Override
public VortexUser authenticateUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) return null;

    String userId = auth.getName();

    // Extract email from UserDetails
    String userEmail = userId + "@example.com"; // Adjust for your setup
    Object principal = auth.getPrincipal();
    if (principal instanceof UserDetails) {
        // Cast to your custom UserDetails with email
        // userEmail = ((MyUserDetails) principal).getEmail();
    }

    // Check admin role
    Boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

    return new VortexUser(userId, userEmail, isAdmin);
}
```

### 4. Database Transactions
Use @Transactional for accept invitations:
```java
@Transactional
@PostMapping("/invitations/accept")
public ResponseEntity<?> acceptInvitations(@RequestBody AcceptInvitationRequest request) {
    // Database operations will be rolled back if exception occurs
}
```

### 5. Resource Management
Close VortexClient when using manually:
```java
try (VortexClient client = new VortexClient(apiKey)) {
    String jwt = client.generateJwt(params);
    // Use jwt
}
```

### 6. Logging
Use SLF4J logging:
```java
private static final Logger logger = LoggerFactory.getLogger(YourClass.class);

logger.info("Generating JWT for user {}", userId);
logger.error("Failed to accept invitations", exception);
```

### 7. Testing
Write unit tests with Mockito:
```java
@Test
void testGenerateJwt() throws VortexException {
    VortexClient client = new VortexClient(TEST_API_KEY);

    User user = new User("test-123", "test@example.com");
    Map<String, Object> params = Map.of("user", user);

    String jwt = client.generateJwt(params);
    assertNotNull(jwt);
    assertTrue(jwt.startsWith("eyJ")); // JWT format
}
```

## Additional Resources

- [Java SDK Documentation](https://docs.vortexsoftware.com/sdks/java)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Vortex API Reference](https://api.vortexsoftware.com/api)
- [Integration Examples](https://github.com/teamvortexsoftware/vortex-examples)

## Support

For questions or issues:
- GitHub Issues: https://github.com/teamvortexsoftware/vortex-java-sdk/issues
- Email: support@vortexsoftware.com
- Documentation: https://docs.vortexsoftware.com
