# Vortex Java SDK

A comprehensive Java SDK for integrating with the Vortex API, providing invitation management and JWT generation with full compatibility with React providers.

## Features

- 🔐 **JWT Generation**: Same algorithm as Node.js SDK for perfect compatibility
- 📧 **Invitation Management**: Complete CRUD operations for invitations
- 👥 **Group Operations**: Manage invitations by groups
- 🚀 **Spring Boot Integration**: Auto-configuration and ready-to-use controllers
- 🧪 **Comprehensive Testing**: Full test coverage with WireMock integration
- 📱 **React Provider Compatible**: Same route structure as other SDKs

### Invitation Delivery Types

Vortex supports multiple delivery methods for invitations:

- **`email`** - Email invitations sent by Vortex (includes reminders and nudges)
- **`phone`** - Phone invitations sent by the user/customer
- **`share`** - Shareable invitation links for social sharing
- **`internal`** - Internal invitations managed entirely by your application
  - No email/SMS communication triggered by Vortex
  - Target value can be any customer-defined identifier (UUID, string, number)
  - Useful for in-app invitation flows where you handle the delivery
  - Example use case: In-app notifications, dashboard invites, etc.

## Quick Start

### Maven Dependency

Add to your `pom.xml`:

```xml
<dependency>
    <groupId>com.vortexsoftware</groupId>
    <artifactId>vortex-java-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Basic Usage

```java
import com.vortexsoftware.sdk.VortexClient;
import com.vortexsoftware.sdk.types.*;
import java.util.Arrays;
import java.util.List;

// Create client
VortexClient client = new VortexClient("your-api-key-here");

// Generate JWT with user profile
User user = new User.Builder()
    .id("user-123")
    .email("user@example.com")
    .userName("Jane Doe")                                    // Optional: user's display name
    .userAvatarUrl("https://example.com/avatars/jane.jpg")  // Optional: user's avatar URL
    .adminScopes(Arrays.asList("autojoin"))             // Optional: grants autojoin admin privileges
    .build();

String jwt = client.generateJwt(user, null);

// Get invitations by target
List<InvitationResult> invitations = client.getInvitationsByTarget("email", "user@example.com");

// Close client when done
client.close();
```

### Spring Boot Integration

Add to your `application.yml`:

```yaml
vortex:
  api:
    key: your-api-key-here
    base-url: https://api.vortexsoftware.com # optional
```

The SDK will auto-configure and provide these endpoints:

- `POST /api/vortex/jwt` - Generate JWT
- `GET /api/vortex/invitations` - Get invitations by target
- `GET /api/vortex/invitations/{id}` - Get specific invitation
- `DELETE /api/vortex/invitations/{id}` - Revoke invitation
- `POST /api/vortex/invitations/accept` - Accept invitations
- `GET /api/vortex/invitations/by-group/{type}/{id}` - Get group invitations
- `DELETE /api/vortex/invitations/by-group/{type}/{id}` - Delete group invitations
- `POST /api/vortex/invitations/{id}/reinvite` - Reinvite user

### Custom Spring Configuration

```java
@Configuration
public class VortexConfiguration {

    @Bean
    public VortexConfig vortexConfig() {
        return new VortexConfig() {
            @Override
            public VortexUser authenticateUser() {
                // Extract user from security context
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth == null || !auth.isAuthenticated()) {
                    return null;
                }

                // Convert to VortexUser with new format
                return new VortexUser(
                    auth.getName(),
                    getUserEmail(auth),
                    isAutojoinAdmin(auth)
                );
            }

            @Override
            public boolean authorizeOperation(String operation, VortexUser user) {
                // Implement your authorization logic
                return user != null && hasPermission(user, operation);
            }
        };
    }
}
```

## API Reference

### VortexClient Methods

#### JWT Generation

```java
public String generateJwt(User user, Map<String, Object> extra) throws VortexException
```

Generates a JWT token with the following structure:

- User ID and email (required)
- Name and avatar URL (optional) - user profile information
- Admin scopes (optional) - full `adminScopes` array is included in JWT payload
- Additional properties from `extra` parameter
- Expiration (1 hour from generation)

Example:

```java
// Generate JWT with user profile
User user = new User.Builder()
    .id("user-123")
    .email("user@example.com")
    .userName("Jane Doe")                                    // Optional: max 200 chars
    .userAvatarUrl("https://example.com/avatars/jane.jpg")  // Optional: HTTPS URL, max 2000 chars
    .adminScopes(Arrays.asList("autojoin"))             // Optional: grants admin privileges
    .build();

String jwt = client.generateJwt(user, null);
```

Uses the same algorithm as other Vortex SDKs for perfect cross-platform compatibility.

#### Invitation Management

```java
// Get invitations by target
public List<InvitationResult> getInvitationsByTarget(String targetType, String targetValue)

// Get specific invitation
public InvitationResult getInvitation(String invitationId)

// Revoke invitation
public void revokeInvitation(String invitationId)

// Accept an invitation
public InvitationResult acceptInvitation(String invitationId, AcceptUser user)

// Reinvite user
public InvitationResult reinvite(String invitationId)
```

#### Group Operations

```java
// Get invitations by group
public List<InvitationResult> getInvitationsByGroup(String groupType, String groupId)

// Delete all invitations for a group
public void deleteInvitationsByGroup(String groupType, String groupId)
```

### Types

#### User

User data for JWT generation with id, email, and optional adminScopes.

#### InvitationResult

Complete invitation data with status, delivery information, and metadata.

#### InvitationTarget

Represents invitation targets (email, SMS, etc.).

#### InvitationGroup

Group membership information.

#### AcceptInvitationRequest

Request payload for accepting invitations.

## Route Compatibility

The Java SDK provides the exact same route structure as other SDKs:

| Route                                    | Method     | Purpose                        |
| ---------------------------------------- | ---------- | ------------------------------ |
| `/jwt`                                   | POST       | Generate JWT                   |
| `/invitations`                           | GET        | Get invitations by target      |
| `/invitations/{id}`                      | GET/DELETE | Get/revoke specific invitation |
| `/invitations/accept`                    | POST       | Accept invitations             |
| `/invitations/by-group/{type}/{groupId}` | GET/DELETE | Group operations               |
| `/invitations/{id}/reinvite`             | POST       | Reinvite user                  |

This ensures perfect compatibility with:

- React providers
- Frontend applications
- Cross-platform consistency

## Configuration

### Environment Variables

- `VORTEX_API_BASE_URL` - Custom API base URL (optional)

### Spring Boot Properties

```yaml
vortex:
  api:
    key: your-api-key-here # Required
    base-url: custom-url # Optional
```

## Error Handling

All SDK methods throw `VortexException` for API errors:

```java
try {
    String jwt = client.generateJWT(payload);
} catch (VortexException e) {
    logger.error("Failed to generate JWT", e);
    // Handle error
}
```

## Testing

The SDK includes comprehensive tests using JUnit 5 and WireMock:

```bash
mvn test
```

Test coverage includes:

- JWT generation algorithms
- All API methods
- Error handling
- Spring Boot integration
- Mock API responses

## JWT Algorithm Compatibility

The Java SDK uses the **exact same JWT generation algorithm** as the Node.js SDK:

1. Parse API key (`VRTX.encodedId.key`)
2. Derive signing key using HMAC-SHA256
3. Build header and payload with identical structure
4. Base64URL encode (without padding)
5. Sign with HMAC-SHA256

This ensures JWTs generated by Java are identical to those from Node.js, maintaining perfect compatibility with React providers and other frontend frameworks.

## Building from Source

```bash
# Clone the repository
git clone https://github.com/teamvortexsoftware/vortex-java-sdk.git

# Build with Maven
mvn clean install

# Run tests
mvn test

# Generate documentation
mvn javadoc:javadoc
```

## Examples

### Complete Spring Boot Example

```java
@RestController
@RequestMapping("/api/vortex")
public class MyVortexController {

    private final VortexClient vortexClient;

    public MyVortexController(VortexClient vortexClient) {
        this.vortexClient = vortexClient;
    }

    @PostMapping("/jwt")
    public ResponseEntity<Map<String, String>> generateJWT(HttpServletRequest request) {
        try {
            // Extract user from request
            String userId = getCurrentUserId(request);
            String userEmail = getCurrentUserEmail(request);

            // Build user with admin scopes if applicable
            User user = new User(userId, userEmail);
            if (userIsAutojoinAdmin(request)) {
                user.setAdminScopes(Arrays.asList("autojoin"));
            }

            String jwt = vortexClient.generateJwt(user, null);
            return ResponseEntity.ok(Map.of("jwt", jwt));

        } catch (VortexException e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to generate JWT"));
        }
    }
}
```

### Standalone Usage Example

```java
public class VortexExample {
    public static void main(String[] args) {
        try (VortexClient client = new VortexClient("VRTX.your.api.key")) {

            // Create user
            User user = new User("user-123", "user@example.com");
            user.setAdminScopes(Arrays.asList("autojoin"));

            // Generate JWT
            String jwt = client.generateJwt(user, null);
            System.out.println("Generated JWT: " + jwt);

            // Get invitations
            List<InvitationResult> invitations = client.getInvitationsByTarget(
                "email",
                "user@example.com"
            );
            System.out.println("Found " + invitations.size() + " invitations");

        } catch (VortexException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
```

## License

Licensed under the MIT License. See LICENSE file for details.

## Support

For support, please contact the Vortex team or create an issue in the repository.
