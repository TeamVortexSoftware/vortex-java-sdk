# vortex-java-sdk

<!-- AUTO-GENERATED FROM SDK MANIFEST — DO NOT EDIT DIRECTLY -->

![Version](https://img.shields.io/badge/version-1.20.0-blue)
![Language](https://img.shields.io/badge/language-java-green)

**Invitation infrastructure for modern apps**

Vortex handles the complete invitation lifecycle — sending invites via email/SMS/share links, tracking clicks and conversions, managing referral programs, and optimizing your invitation flows with A/B testing.
[Learn more about Vortex →](https://tryvortex.com)

## Why This SDK?

This backend SDK securely signs user data for Vortex components. Your API key stays on your server, while the signed token is passed to the frontend where Vortex components render the invitation UI.

- Keep your API key secure — it never touches the browser
- Sign user identity for attribution — know who sent each invitation
- Control what data components can access via scoped tokens
- Verify webhook signatures for secure event handling

## How It Works

Vortex uses a split architecture: your backend signs tokens with the SDK, and your frontend renders components that use those tokens to securely interact with Vortex.

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Your Server   │     │  User Browser   │     │  Vortex Cloud   │
│    (this SDK)   │     │   (component)   │     │                 │
└────────┬────────┘     └────────┬────────┘     └────────┬────────┘
         │                       │                       │
         │  1. generateToken()   │                       │
         │◄──────────────────────│                       │
         │                       │                       │
         │  2. Return token      │                       │
         │──────────────────────►│                       │
         │                       │                       │
         │                       │  3. Component calls   │
         │                       │     API with token    │
         │                       │──────────────────────►│
         │                       │                       │
         │                       │  4. Render UI,        │
         │                       │     send invitations  │
         │                       │◄──────────────────────│
         │                       │                       │
```

### Integration Flow

**1. Install the backend SDK** `[backend]`

Add this SDK to your Java project

```java
<dependency>
  <groupId>com.vortexsoftware</groupId>
  <artifactId>vortex-java-sdk</artifactId>
</dependency>
```

**2. Initialize the client** `[backend]`

Create a Vortex client with your API key (keep this on the server!)

```java
import com.vortexsoftware.sdk.VortexClient;

VortexClient client = new VortexClient(System.getenv("VORTEX_API_KEY"));
```

**3. Generate a token for the current user** `[backend]`

When a user loads a page with a Vortex component, generate a signed token on your server

```java
String token = client.generateToken(
    new GenerateTokenPayload().setUser(
        new TokenUser().setId(currentUser.getId())
    )
);
```

**4. Pass the token to your frontend** `[backend]`

Include the token in your page response or API response

```java
response.put("vortexToken", token);
```

**5. Render a Vortex component with the token** `[frontend]`

Use the React/Angular/Web Component with the token

```java
import { VortexInvite } from "@teamvortexsoftware/vortex-react";

<VortexInvite token={vortexToken} />
```

**6. Vortex handles the rest** `[vortex]`

The component securely communicates with Vortex servers, displays the invitation UI, sends emails/SMS, tracks conversions, and reports analytics

### Security Model

> ⚠️ **Important:** Your Vortex API key is a secret that grants full access to your account. It must never be exposed to browsers or client-side code.

By signing tokens on your server, you:

- Keep your API key secret (it never leaves your server)
- Control exactly what user data is shared with components
- Ensure invitations are attributed to real, authenticated users
- Prevent abuse — users can only send invitations as themselves

#### When Signing is Optional

Token signing is controlled by your component configuration in the Vortex dashboard.

---

## Quick Start

Generate a secure token for Vortex components

```java
VortexClient client = new VortexClient(System.getenv("VORTEX_API_KEY"));

GenerateTokenPayload payload = new GenerateTokenPayload()
    .setUser(new TokenUser().setId("user-123").setEmail("user@example.com"));

String token = client.generateToken(payload);
```

## Installation

```bash
<dependency>
  <groupId>com.vortexsoftware</groupId>
  <artifactId>vortex-java-sdk</artifactId>
</dependency>
```

<details>
<summary>Other package managers</summary>

**gradle:**

```bash
implementation 'com.vortexsoftware:vortex-java-sdk'
```

</details>

## Initialization

```java
VortexClient client = new VortexClient(System.getenv("VORTEX_API_KEY"));
```

### Environment Variables

| Variable         | Required | Description         |
| ---------------- | -------- | ------------------- |
| `VORTEX_API_KEY` | ✓        | Your Vortex API key |

## Core Methods

These are the methods you'll use most often.

### `generateToken()`

Generate a signed token for use with Vortex widgets

**Signature:**

```java
generateToken(GenerateTokenPayload payload): String
```

**Parameters:**

| Name      | Type                   | Required | Description                                       |
| --------- | ---------------------- | -------- | ------------------------------------------------- |
| `payload` | `GenerateTokenPayload` | ✓        | Data to sign (user, component, scope, vars, etc.) |

**Returns:** `String`
— Signed JWT token string

_Added in v0.8.0_

---

### `getInvitation()`

Get a specific invitation by ID

**Signature:**

```java
getInvitation(String invitationId): InvitationResult
```

**Parameters:**

| Name           | Type     | Required | Description       |
| -------------- | -------- | -------- | ----------------- |
| `invitationId` | `String` | ✓        | The invitation ID |

**Returns:** `InvitationResult`
— The invitation details

**Example:**

```java
InvitationResult invitation = client.getInvitation("inv-123");
System.out.println("Status: " + invitation.getStatus());
```

_Added in v0.1.0_

---

### `acceptInvitation()`

Accept a single invitation (recommended method)

**Signature:**

```java
acceptInvitation(String invitationId, AcceptUser user): InvitationResult
```

**Parameters:**

| Name           | Type         | Required | Description                         |
| -------------- | ------------ | -------- | ----------------------------------- |
| `invitationId` | `String`     | ✓        | Single invitation ID to accept      |
| `user`         | `AcceptUser` | ✓        | User object with email and/or phone |

**Returns:** `InvitationResult`
— The accepted invitation result

**Example:**

```java
AcceptUser user = new AcceptUser()
.setEmail("user@example.com")
.setName("John Doe");
InvitationResult result = client.acceptInvitation("inv-123", user);
```

_Added in v0.6.0_

---

## All Methods

<details>
<summary>Click to expand full method reference</summary>

### `generateJwt()`

Generate a JWT using the same algorithm as the Node.js SDK

**Signature:**

```java
generateJwt(Object> params): String
```

**Parameters:**

| Name     | Type      | Required | Description                                |
| -------- | --------- | -------- | ------------------------------------------ |
| `params` | `Object>` | ✓        | Map containing "user" key with User object |

**Returns:** `String`
— JWT token string

_Added in v0.3.0_

---

### `getInvitationsByTarget()`

Get invitations by target (email, username, phoneNumber)

**Signature:**

```java
getInvitationsByTarget(String targetType, String targetValue): List<InvitationResult>
```

**Parameters:**

| Name          | Type     | Required | Description                         |
| ------------- | -------- | -------- | ----------------------------------- |
| `targetType`  | `String` | ✓        | Type of target (email, phone, etc.) |
| `targetValue` | `String` | ✓        | The target value                    |

**Returns:** `List<InvitationResult>`
— List of invitations

_Added in v0.1.0_

---

### `revokeInvitation()`

Revoke (delete) an invitation

**Signature:**

```java
revokeInvitation(String invitationId): void
```

**Parameters:**

| Name           | Type     | Required | Description                 |
| -------------- | -------- | -------- | --------------------------- |
| `invitationId` | `String` | ✓        | The invitation ID to revoke |

**Returns:** `void`

_Added in v0.1.0_

---

### `deleteInvitationsByScope()`

Delete all invitations for a specific scope

**Signature:**

```java
deleteInvitationsByScope(String scopeType, String scope): void
```

**Parameters:**

| Name        | Type     | Required | Description                               |
| ----------- | -------- | -------- | ----------------------------------------- |
| `scopeType` | `String` | ✓        | The scope type (organization, team, etc.) |
| `scope`     | `String` | ✓        | The scope identifier                      |

**Returns:** `void`

_Added in v0.4.0_

---

### `getInvitationsByScope()`

Get all invitations for a specific scope

**Signature:**

```java
getInvitationsByScope(String scopeType, String scope): List<InvitationResult>
```

**Parameters:**

| Name        | Type     | Required | Description                               |
| ----------- | -------- | -------- | ----------------------------------------- |
| `scopeType` | `String` | ✓        | The scope type (organization, team, etc.) |
| `scope`     | `String` | ✓        | The scope identifier                      |

**Returns:** `List<InvitationResult>`
— List of invitations for the scope

_Added in v0.4.0_

---

### `reinvite()`

Reinvite a user (send invitation again)

**Signature:**

```java
reinvite(String invitationId): InvitationResult
```

**Parameters:**

| Name           | Type     | Required | Description                   |
| -------------- | -------- | -------- | ----------------------------- |
| `invitationId` | `String` | ✓        | The invitation ID to reinvite |

**Returns:** `InvitationResult`
— The reinvited invitation result

_Added in v0.2.0_

---

### `getAutojoinDomains()`

Get autojoin domains configured for a specific scope

**Signature:**

```java
getAutojoinDomains(String scopeType, String scope): AutojoinDomainsResponse
```

**Parameters:**

| Name        | Type     | Required | Description                                                 |
| ----------- | -------- | -------- | ----------------------------------------------------------- |
| `scopeType` | `String` | ✓        | The type of scope (e.g., "organization", "team", "project") |
| `scope`     | `String` | ✓        | The scope identifier (customer's group ID)                  |

**Returns:** `AutojoinDomainsResponse`
— AutojoinDomainsResponse with autojoin domains and invitation

_Added in v0.6.0_

---

### `configureAutojoin()`

Configure autojoin domains for a specific scope

**Signature:**

```java
configureAutojoin(ConfigureAutojoinRequest request): AutojoinDomainsResponse
```

**Parameters:**

| Name      | Type                       | Required | Description                    |
| --------- | -------------------------- | -------- | ------------------------------ |
| `request` | `ConfigureAutojoinRequest` | ✓        | The configure autojoin request |

**Returns:** `AutojoinDomainsResponse`
— AutojoinDomainsResponse with updated autojoin domains

_Added in v0.6.0_

---

</details>

## Types

<details>
<summary>Click to expand type definitions</summary>

### `GenerateTokenPayload`

| Field       | Type                  | Required | Description                                                                                                                                                     |
| ----------- | --------------------- | -------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `user`      | `TokenUser`           |          | The authenticated user who will be using the Vortex component. Required for most operations to attribute invitations correctly.                                 |
| `component` | `String`              |          | Component ID to generate token for (from your Vortex dashboard). If not specified, uses the default component for your account.                                 |
| `trigger`   | `String`              |          | Trigger context for the invitation (e.g., "signup", "share-button", "referral-page"). Used for analytics to track which UI elements drive the most invitations. |
| `embed`     | `String`              |          | Embed mode identifier for embedded invitation widgets. Determines how the component renders in your UI.                                                         |
| `scope`     | `String`              |          | Scope identifier to restrict invitations to a specific team/org/workspace. Format: "scopeType:scopeId" (e.g., "team:team-123").                                 |
| `vars`      | `Map<String, Object>` |          | Custom variables to pass to the component for template rendering. These can be used in email templates and invitation messages.                                 |

### `TokenUser`

| Field                 | Type           | Required | Description                                                                                                                                                                                             |
| --------------------- | -------------- | -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `id`                  | `String`       | ✓        | Unique identifier for the user in your system. Used to attribute invitations and track referral chains.                                                                                                 |
| `name`                | `String`       |          | Display name shown to invitation recipients (e.g., "John invited you"). If not provided, falls back to email or a generic message.                                                                      |
| `email`               | `String`       |          | User's email address. Used for reply-to in invitation emails and shown to recipients so they know who invited them.                                                                                     |
| `avatarUrl`           | `String`       |          | URL to user's avatar image. Displayed in invitation emails and widgets to personalize the invitation experience.                                                                                        |
| `adminScopes`         | `List<String>` |          | List of scope IDs where this user has admin privileges. Admins can manage invitations and view analytics for these scopes. Format: ["scopeType:scopeId", ...] (e.g., ["team:team-123", "org:org-456"]). |
| `allowedEmailDomains` | `List<String>` |          | Restrict invitations to specific email domains. If set, users can only invite people with emails matching these domains. Useful for enterprise accounts (e.g., ["acme.com", "acme.co.uk"]).             |

### `AcceptUser`

| Field        | Type      | Required | Description                                                                                                                                                                                                  |
| ------------ | --------- | -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `email`      | `String`  |          | Email address of the user accepting the invitation. At least one of email or phone is required.                                                                                                              |
| `phone`      | `String`  |          | Phone number of the user accepting the invitation. At least one of email or phone is required. Include country code (e.g., "+1555123456").                                                                   |
| `name`       | `String`  |          | Display name of the accepting user. Used in notifications to the inviter (e.g., "John Doe accepted your invitation").                                                                                        |
| `isExisting` | `Boolean` |          | Whether the accepting user was already registered in your system. Set to true for existing users, false for new signups, null if unknown. Used for analytics to track new vs existing user conversion rates. |

### `InvitationResult`

| Field                     | Type                         | Required | Description                                                                                                            |
| ------------------------- | ---------------------------- | -------- | ---------------------------------------------------------------------------------------------------------------------- |
| `id`                      | `String`                     | ✓        | Unique identifier for this invitation                                                                                  |
| `accountId`               | `String`                     |          | Your Vortex account ID                                                                                                 |
| `clickThroughs`           | `int`                        | ✓        | Number of times the invitation link was clicked                                                                        |
| `formSubmissionData`      | `Map<String, Object>`        |          | Invitation form data submitted by the user, including invitee identifiers (such as email addresses, phone numbers, or internal IDs) and the values of any custom fields. |
| `configurationAttributes` | `Map<String, Object>`        |          |                                                                                                                        |
| `attributes`              | `Map<String, Object>`        |          | Custom attributes attached to this invitation                                                                          |
| `createdAt`               | `String`                     |          | ISO 8601 timestamp when the invitation was created                                                                     |
| `deactivated`             | `boolean`                    | ✓        | Whether this invitation has been revoked or expired                                                                    |
| `deliveryCount`           | `int`                        | ✓        | Number of times the invitation was sent (including reminders)                                                          |
| `deliveryTypes`           | `List<DeliveryType>`         |          | Channels used to deliver this invitation (email, sms, share link)                                                      |
| `foreignCreatorId`        | `String`                     |          | Your internal user ID for the person who created this invitation                                                       |
| `invitationType`          | `InvitationType`             |          | Type of invitation: PERSONAL (1:1) or BROADCAST (1:many)                                                               |
| `modifiedAt`              | `String`                     |          | ISO 8601 timestamp of last modification                                                                                |
| `status`                  | `InvitationStatus`           |          | Current status: CREATED, DELIVERED, CLICKED, ACCEPTED, or EXPIRED                                                      |
| `target`                  | `List<InvitationTarget>`     |          | List of invitation recipients with their contact info and status                                                       |
| `views`                   | `int`                        | ✓        | Number of times the invitation page was viewed                                                                         |
| `widgetConfigurationId`   | `String`                     |          | Widget configuration ID used for this invitation                                                                       |
| `deploymentId`            | `String`                     |          | Deployment ID this invitation belongs to                                                                               |
| `groups`                  | `List<InvitationScope>`      |          | Scopes (teams/orgs) this invitation grants access to                                                                   |
| `accepts`                 | `List<InvitationAcceptance>` |          | List of acceptance records if the invitation was accepted (optional, may be null)                                      |
| `scope`                   | `String`                     |          | Primary scope identifier (e.g., "team-123")                                                                            |
| `scopeType`               | `String`                     |          | Type of the primary scope (e.g., "team", "organization")                                                               |
| `expired`                 | `boolean`                    | ✓        | Whether this invitation has passed its expiration date                                                                 |
| `expires`                 | `String`                     |          | ISO 8601 timestamp when this invitation expires                                                                        |
| `metadata`                | `Map<String, Object>`        |          | Custom metadata attached to this invitation                                                                            |
| `passThrough`             | `String`                     |          | Pass-through data returned unchanged in webhooks and callbacks                                                         |
| `source`                  | `String`                     |          | Source identifier for tracking (e.g., "ios-app", "web-dashboard")                                                      |
| `subtype`                 | `String`                     |          | Subtype for analytics segmentation (e.g., "pymk", "find-friends")                                                      |
| `creatorName`             | `String`                     |          | Display name of the user who created this invitation                                                                   |
| `creatorAvatarUrl`        | `String`                     |          | Avatar URL of the user who created this invitation                                                                     |

### `InvitationTarget`

| Field       | Type                   | Required | Description                                                             |
| ----------- | ---------------------- | -------- | ----------------------------------------------------------------------- |
| `type`      | `InvitationTargetType` | ✓        | Delivery channel: EMAIL, PHONE, SHARE (link), or INTERNAL (in-app)      |
| `value`     | `String`               |          | Target address: email, phone number with country code, or share link ID |
| `name`      | `String`               |          | Display name of the recipient (e.g., "John Doe")                        |
| `avatarUrl` | `String`               |          | Avatar URL for the recipient, shown in invitation lists and widgets     |

### `InvitationScope`

Represents a group associated with an invitation. This matches the MemberGroups table structure from the API response.

| Field       | Type     | Required | Description                                          |
| ----------- | -------- | -------- | ---------------------------------------------------- |
| `id`        | `String` | ✓        | Vortex internal UUID                                 |
| `accountId` | `String` |          | Vortex account ID                                    |
| `groupId`   | `String` |          | Customer's group ID (the ID they provided to Vortex) |
| `type`      | `String` | ✓        | Group type (e.g., "workspace", "team")               |
| `name`      | `String` |          | Group name                                           |
| `createdAt` | `String` |          | ISO 8601 timestamp when the group was created        |

### `AutojoinDomain`

Represents an autojoin domain - users with matching email domains automatically join the scope

| Field    | Type     | Required | Description                                            |
| -------- | -------- | -------- | ------------------------------------------------------ |
| `id`     | `String` | ✓        | Unique identifier for this autojoin configuration      |
| `domain` | `String` |          | Email domain that triggers autojoin (e.g., "acme.com") |

### `CreateInvitationScope`

| Field     | Type     | Required | Description                                               |
| --------- | -------- | -------- | --------------------------------------------------------- |
| `type`    | `String` | ✓        | Scope type (e.g., "team", "organization", "workspace")    |
| `groupId` | `String` |          | Your internal scope/group identifier                      |
| `name`    | `String` |          | Display name shown to invitees (e.g., "Engineering Team") |

### `CreateInvitationTarget`

| Field       | Type                         | Required | Description                                                                  |
| ----------- | ---------------------------- | -------- | ---------------------------------------------------------------------------- |
| `type`      | `CreateInvitationTargetType` | ✓        | Delivery channel: EMAIL, PHONE, SHARE (link), or INTERNAL (in-app)           |
| `value`     | `String`                     |          | Target address: email, phone number (with country code), or internal user ID |
| `name`      | `String`                     |          | Display name of the recipient (shown in invitation emails and UI)            |
| `avatarUrl` | `String`                     |          | Avatar URL for the recipient (displayed in invitation lists)                 |

### `Group`

| Field   | Type     | Required | Description                                              |
| ------- | -------- | -------- | -------------------------------------------------------- |
| `type`  | `String` | ✓        | Scope type (e.g., "team", "organization", "workspace")   |
| `id`    | `String` | ✓        | Legacy scope identifier. Use scopeId instead.            |
| `scope` | `String` |          | Your internal scope/group identifier (preferred over id) |
| `name`  | `String` |          | Display name for the scope (e.g., "Engineering Team")    |

### `Identifier`

Represents an identifier for a user - used in JWT generation to link user across channels

| Field   | Type     | Required | Description                                                   |
| ------- | -------- | -------- | ------------------------------------------------------------- |
| `type`  | `String` | ✓        | Identifier type: "email", "phone", "username", or custom type |
| `value` | `String` |          | The identifier value (email address, phone number, etc.)      |

### `InvitationAcceptance`

| Field         | Type                  | Required | Description                                                        |
| ------------- | --------------------- | -------- | ------------------------------------------------------------------ |
| `id`          | `String`              | ✓        | Unique identifier for this acceptance record                       |
| `accountId`   | `String`              |          | Your Vortex account ID                                             |
| `acceptedAt`  | `String`              |          | ISO 8601 timestamp when the invitation was accepted                |
| `targetType`  | `String`              |          | How the recipient was identified: "email" or "phone"               |
| `targetValue` | `String`              |          | The email or phone number of the person who accepted               |
| `identifiers` | `Map<String, String>` |          | Additional identifiers for the accepting user (e.g., external IDs) |

### `Inviter`

| Field       | Type     | Required | Description                                                       |
| ----------- | -------- | -------- | ----------------------------------------------------------------- |
| `userId`    | `String` |          | Your internal user ID for the inviter (required for attribution)  |
| `userEmail` | `String` |          | Inviter's email address (used for reply-to and identification)    |
| `name`      | `String` |          | Display name shown to recipients (e.g., "John invited you to...") |
| `avatarUrl` | `String` |          | Avatar URL displayed in invitation emails and widgets             |

### `JWTPayload`

| Field                 | Type               | Required | Description                                                                  |
| --------------------- | ------------------ | -------- | ---------------------------------------------------------------------------- |
| `userId`              | `String`           |          | Your internal user ID (required - used for invitation attribution)           |
| `userEmail`           | `String`           |          | User's email address (preferred format for user identification)              |
| `userIsAutojoinAdmin` | `Boolean`          |          | Whether user can manage autojoin settings for their scopes                   |
| `identifiers`         | `List<Identifier>` |          | Legacy: List of user identifiers. Use userEmail instead.                     |
| `groups`              | `List<Group>`      |          | Legacy: List of groups/scopes. Use scope parameter in generateToken instead. |
| `role`                | `String`           |          | Legacy: User role. No longer used.                                           |

### `UnfurlConfig`

| Field         | Type     | Required | Description                                                           |
| ------------- | -------- | -------- | --------------------------------------------------------------------- |
| `title`       | `String` |          | The title shown in link previews (og:title)                           |
| `description` | `String` |          | The description shown in link previews (og:description)               |
| `image`       | `String` |          | The image URL shown in link previews (og:image) - must be HTTPS       |
| `type`        | `String` | ✓        | The Open Graph type (og:type) - e.g., 'website', 'article', 'product' |
| `siteName`    | `String` |          | The site name shown in link previews (og:site_name)                   |

### `User`

| Field                 | Type           | Required | Description                                                           |
| --------------------- | -------------- | -------- | --------------------------------------------------------------------- |
| `id`                  | `String`       | ✓        | Your internal user ID (required for invitation attribution)           |
| `email`               | `String`       |          | User's email address (used for identification and reply-to)           |
| `name`                | `String`       |          | Display name shown to recipients (e.g., "John invited you")           |
| `avatarUrl`           | `String`       |          | Avatar URL displayed in invitation emails and widgets (must be HTTPS) |
| `adminScopes`         | `List<String>` |          | List of scopes where user has admin privileges (e.g., ["autojoin"])   |
| `allowedEmailDomains` | `List<String>` |          | Restrict invitations to these email domains (e.g., ["acme.com"])      |

### `VortexAnalyticsEvent`

| Field                   | Type                  | Required | Description                                                       |
| ----------------------- | --------------------- | -------- | ----------------------------------------------------------------- |
| `id`                    | `String`              | ✓        | Unique identifier for this analytics event                        |
| `name`                  | `String`              |          | Event name (e.g., "widget.opened", "invite.sent", "link.clicked") |
| `accountId`             | `String`              |          | Your Vortex account ID                                            |
| `organizationId`        | `String`              |          | Organization ID if using multi-org setup                          |
| `projectId`             | `String`              |          | Project ID the event belongs to                                   |
| `environmentId`         | `String`              |          | Environment ID (production, staging, etc.)                        |
| `deploymentId`          | `String`              |          | Deployment ID the event is associated with                        |
| `widgetConfigurationId` | `String`              |          | Widget configuration ID that generated this event                 |
| `foreignUserId`         | `String`              |          | Your internal user ID who triggered the event                     |
| `sessionId`             | `String`              |          | Client session ID for grouping related events                     |
| `payload`               | `Map<String, Object>` |          | Event-specific payload data                                       |
| `platform`              | `String`              |          | Platform: "web", "ios", "android", "react-native"                 |
| `segmentation`          | `String`              |          | A/B test segmentation identifier                                  |
| `timestamp`             | `String`              |          | ISO 8601 timestamp when the event occurred                        |

### `VortexWebhookEvent`

| Field           | Type                  | Required | Description                                                |
| --------------- | --------------------- | -------- | ---------------------------------------------------------- |
| `id`            | `String`              | ✓        | Unique identifier for this webhook event                   |
| `type`          | `String`              | ✓        | Event type (e.g., "invitation.accepted", "member.created") |
| `timestamp`     | `String`              |          | ISO 8601 timestamp when the event occurred                 |
| `accountId`     | `String`              |          | Your Vortex account ID                                     |
| `environmentId` | `String`              |          | Environment ID (production, staging, etc.)                 |
| `sourceTable`   | `String`              |          | Internal: database table that triggered this event         |
| `operation`     | `String`              |          | Database operation: "INSERT", "UPDATE", or "DELETE"        |
| `data`          | `Map<String, Object>` |          | Event payload containing the relevant entity data          |

</details>

## Webhooks

Webhooks let your server receive real-time notifications when events happen in Vortex. Use them to sync invitation state with your database, trigger onboarding flows, update your CRM, or send internal notifications.

### Setup

1. Go to your Vortex dashboard → Integrations → Webhooks tab
2. Click "Add Webhook"
3. Enter your endpoint URL (must be HTTPS in production)
4. Copy the signing secret — you'll use this to verify webhook signatures
5. Select which events you want to receive

### Verifying Webhooks

Always verify webhook signatures using `VortexWebhooks.verifySignature()` to ensure requests are from Vortex.
The signature is sent in the `X-Vortex-Signature` header.

### Example: Spring Boot webhook handler

```java
import com.vortexsoftware.sdk.VortexWebhooks;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebhookController {
    private final VortexWebhooks webhooks = new VortexWebhooks(
        System.getenv("VORTEX_WEBHOOK_SECRET")
    );

    @PostMapping("/webhooks/vortex")
    public Map<String, Object> handleWebhook(
        @RequestBody String body,
        @RequestHeader("X-Vortex-Signature") String signature
    ) {
        try {
            // Verify the signature
            if (!webhooks.verifySignature(body, signature)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid signature");
            }

            // Parse the event
            WebhookEvent event = webhooks.parseEvent(body);

            switch (event.getType()) {
                case "invitation.accepted":
                    // User accepted an invitation — activate their account
                    System.out.println("Accepted: " + event.getData().getTargetEmail());
                    break;
                case "member.created":
                    // New member joined via invitation
                    System.out.println("New member: " + event.getData());
                    break;
            }

            return Map.of("received", true);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Webhook error");
        }
    }
}
```

### Common Use Cases

**Activate users on acceptance**

When invitation.accepted fires, mark the user as active in your database and trigger your onboarding flow.

**Track invitation performance**

Monitor email.delivered, email.opened, and link.clicked events to measure invitation funnel metrics.

**Sync team membership**

Use member.created and group.member.added to keep your internal membership records in sync.

**Alert on delivery issues**

Watch for email.bounced events to proactively reach out via alternative channels.

### Supported Events

| Event                        | Description                                          |
| ---------------------------- | ---------------------------------------------------- |
| `invitation.created`         | A new invitation was created                         |
| `invitation.accepted`        | An invitation was accepted by the recipient          |
| `invitation.deactivated`     | An invitation was deactivated (revoked or expired)   |
| `invitation.email.delivered` | Invitation email was successfully delivered          |
| `invitation.email.bounced`   | Invitation email bounced (invalid address)           |
| `invitation.email.opened`    | Recipient opened the invitation email                |
| `invitation.link.clicked`    | Recipient clicked the invitation link                |
| `invitation.reminder.sent`   | A reminder email was sent for a pending invitation   |
| `member.created`             | A new member was created from an accepted invitation |
| `group.member.added`         | A member was added to a scope/group                  |
| `deployment.created`         | A new deployment configuration was created           |
| `deployment.deactivated`     | A deployment was deactivated                         |
| `abtest.started`             | An A/B test was started                              |
| `abtest.winner_declared`     | An A/B test winner was declared                      |
| `email.complained`           | Recipient marked the email as spam                   |

## Error Handling

All SDK errors extend `VortexException`.

| Error                             | Description                                                                                                                     |
| --------------------------------- | ------------------------------------------------------------------------------------------------------------------------------- |
| `VortexWebhookSignatureException` | Thrown when webhook signature verification fails. Check that you are using the raw request body and the correct signing secret. |
| `VortexException`                 | Thrown for validation errors (e.g., missing API key, invalid parameters) or API failures                                        |

---

<!-- Generated from SDK v1.20.0 manifest -->
