# Vortex Java SDK Implementation Guide

**Group ID:** `com.vortexsoftware`
**Artifact ID:** `vortex-java-sdk`
**Version:** `1.1.0+`
**Requires:** Java 17+

## Prerequisites
From integration contract you need: API endpoint prefix, scope entity, authentication pattern
From discovery data you need: Spring Boot version, database ORM (Spring Data JPA, MyBatis, etc.), auth framework

## Key Facts
- Spring Boot auto-configuration available (recommended)
- Implement `VortexConfig` interface for authentication
- All methods throw `VortexException` - handle errors
- Accept invitations requires custom database logic (must override)
- VortexClient implements AutoCloseable (use try-with-resources if manual)

---

## Step 1: Install

**Maven - add to `pom.xml`:**
```xml
<dependency>
    <groupId>com.vortexsoftware</groupId>
    <artifactId>vortex-java-sdk</artifactId>
    <version>1.1.0</version>
</dependency>
```

**Gradle - add to `build.gradle`:**
```gradle
dependencies {
    implementation 'com.vortexsoftware:vortex-java-sdk:1.1.0'
}
```

---

## Step 2: Set Environment Variable

```bash
VORTEX_API_KEY=VRTX.your-api-key-here.secret
```

**Never commit API key to version control.**

---

## Step 3: Configure Application Properties

Add to `src/main/resources/application.yml`:

```yaml
vortex:
  api:
    key: ${VORTEX_API_KEY}
```

Or `application.properties`:
```properties
vortex.api.key=${VORTEX_API_KEY}
```

---

## Step 4: Implement Authentication Configuration

Create `src/main/java/com/yourapp/config/VortexSecurityConfiguration.java`:

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
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                if (auth == null || !auth.isAuthenticated()) {
                    return null;
                }

                String userId = auth.getName();
                String userEmail = getUserEmail(auth); // Your method
                Boolean isAdmin = hasAdminRole(auth); // Your method

                return new VortexUser(userId, userEmail, isAdmin);
            }

            @Override
            public boolean authorizeOperation(String operation, VortexUser user) {
                if (user == null) return false;

                // Example: Only admins can revoke
                if ("REVOKE_INVITATION".equals(operation)) {
                    return user.getUserIsAutojoinAdmin() != null && user.getUserIsAutojoinAdmin();
                }

                return true; // Allow all other operations for authenticated users
            }
        };
    }

    private String getUserEmail(Authentication auth) {
        // Extract from your UserDetails implementation
        // return ((MyUserDetails) auth.getPrincipal()).getEmail();
        return auth.getName() + "@example.com"; // Adapt
    }

    private Boolean hasAdminRole(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}
```

**Adapt to their patterns:**
- Match their UserDetails implementation for email extraction
- Match their role/authority checking pattern
- Match their authentication framework (Spring Security, custom, etc.)

---

## Step 5: Override Accept Invitations Endpoint (CRITICAL)

Create `src/main/java/com/yourapp/controller/CustomVortexController.java`:

```java
package com.yourapp.controller;

import com.vortexsoftware.sdk.VortexClient;
import com.vortexsoftware.sdk.VortexException;
import com.vortexsoftware.sdk.spring.VortexConfig;
import com.vortexsoftware.sdk.types.*;
import com.yourapp.entity.WorkspaceMember; // Your entity
import com.yourapp.repository.WorkspaceMemberRepository; // Your repository
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
    private final WorkspaceMemberRepository workspaceMemberRepository; // Adapt

    public CustomVortexController(
            VortexClient vortexClient,
            VortexConfig vortexConfig,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.vortexClient = vortexClient;
        this.vortexConfig = vortexConfig;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

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
                        .body(Map.of("error", "Not authorized", "code", "FORBIDDEN"));
            }

            // 2. Accept via Vortex API
            List<InvitationResult> results = vortexClient.acceptInvitations(
                    request.getInvitationIds(),
                    request.getUser()
            );

            // 3. Add user to database - adapt to your ORM
            // Spring Data JPA example:
            for (InvitationResult result : results) {
                for (InvitationGroup group : result.getGroups()) {
                    WorkspaceMember member = new WorkspaceMember();
                    member.setUserId(user.getUserId());
                    member.setWorkspaceId(group.getGroupId()); // Adapt field names
                    member.setRole("member");
                    member.setJoinedAt(LocalDateTime.now());

                    workspaceMemberRepository.save(member);
                }
            }

            // MyBatis example:
            // for (InvitationResult result : results) {
            //     for (InvitationGroup group : result.getGroups()) {
            //         WorkspaceMember member = new WorkspaceMember();
            //         member.setUserId(user.getUserId());
            //         member.setWorkspaceId(group.getGroupId());
            //         member.setRole("member");
            //         member.setJoinedAt(LocalDateTime.now());
            //         workspaceMemberMapper.insert(member);
            //     }
            // }

            // JdbcTemplate example:
            // for (InvitationResult result : results) {
            //     for (InvitationGroup group : result.getGroups()) {
            //         jdbcTemplate.update(
            //             "INSERT INTO workspace_members (user_id, workspace_id, role, joined_at) VALUES (?, ?, ?, ?)",
            //             user.getUserId(), group.getGroupId(), "member", LocalDateTime.now()
            //         );
            //     }
            // }

            return ResponseEntity.ok(results);

        } catch (VortexException e) {
            logger.error("Failed to accept invitations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to accept invitations", "code", "INTERNAL_ERROR"));
        }
    }
}
```

**Critical - Adapt database logic:**
- Use their actual entity/table names (from discovery)
- Use their actual field names
- Use their ORM/database library (Spring Data JPA, MyBatis, JdbcTemplate, etc.)
- Handle duplicate memberships if needed

---

## Step 6: Add CORS (If Needed)

If frontend on different domain, create `src/main/java/com/yourapp/config/CorsConfiguration.java`:

```java
package com.yourapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class WebCorsConfiguration {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin(System.getenv().getOrDefault("FRONTEND_URL", "http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/vortex/**", config);

        return new CorsFilter(source);
    }
}
```

---

## Step 7: Build and Test

```bash
# Maven
mvn clean package

# Gradle
./gradlew build

# Start
java -jar target/your-app.jar

# Test JWT endpoint
curl -X POST http://localhost:8080/api/vortex/jwt \
  -H "Authorization: Bearer YOUR_AUTH_TOKEN"
```

Expected response:
```json
{
  "jwt": "eyJhbGciOiJIUzI1NiIs..."
}
```

---

## Common Errors

**"Could not find artifact vortex-java-sdk"** → Ensure Maven Central in repositories and correct version

**"VortexConfig bean not found"** → Create @Bean for VortexConfig (see Step 4)

**"Authentication always returns null"** → Ensure Spring Security configured and auth set before Vortex endpoints called

**"Invalid API key format"** → Key must be: `VRTX.base64id.key`

**"User not added to database"** → Must override accept endpoint with custom DB logic (see Step 5)

**"Endpoints not found (404)"** → Ensure component scanning includes SDK or use `@Import(VortexAutoConfiguration.class)`

**CORS errors** → Add CORS configuration (see Step 6)

---

## After Implementation Report

List files created/modified:
- Dependency: pom.xml or build.gradle
- Configuration: src/main/resources/application.yml
- VortexConfig: src/main/java/com/yourapp/config/VortexSecurityConfiguration.java
- Controller: src/main/java/com/yourapp/controller/CustomVortexController.java
- Database: Accept endpoint creates memberships in [entity/table name]

Confirm:
- SDK dependency added
- VORTEX_API_KEY configured in application.yml
- VortexConfig bean created with authenticateUser
- Accept invitations endpoint overridden with DB logic
- JWT endpoint returns valid JWT
- Build succeeds with `mvn package` or `./gradlew build`

## Auto-Configured Endpoints

Spring Boot auto-configuration provides these endpoints (unless overridden):
- `POST /api/vortex/jwt` - Generate JWT for authenticated user
- `GET /api/vortex/invitations` - Get invitations by target
- `GET /api/vortex/invitations/{id}` - Get invitation by ID
- `POST /api/vortex/invitations/accept` - Accept invitations (must override with DB logic)
- `DELETE /api/vortex/invitations/{id}` - Revoke invitation
- `POST /api/vortex/invitations/{id}/reinvite` - Resend invitation
- `GET /api/vortex/invitations/by-group/{type}/{id}` - Get group invitations
- `DELETE /api/vortex/invitations/by-group/{type}/{id}` - Delete group invitations
