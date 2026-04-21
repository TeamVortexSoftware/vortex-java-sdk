#!/usr/bin/env python3
"""
Java SDK Manifest Extractor
Parses Javadoc comments to produce manifest.json

Supports @vortex.* tags in Javadoc:
  @vortex.category authentication
  @vortex.since 0.8.0
  @vortex.primary
  @vortex.deprecated Use generateToken instead
"""

import json
import re
import sys
from pathlib import Path

SDK_DIR = Path(__file__).parent.parent


def get_version():
    """Extract version from pom.xml"""
    pom = SDK_DIR / "pom.xml"
    if pom.exists():
        content = pom.read_text()
        # Find the first <version> that's a direct child (not in dependencies)
        # Look for version after <artifactId>vortex-java-sdk</artifactId>
        match = re.search(r'<artifactId>vortex-java-sdk</artifactId>\s*<version>([^<]+)</version>', content)
        if match:
            return match.group(1)
        # Fallback: first version tag
        match = re.search(r'<version>([^<]+)</version>', content)
        if match:
            return match.group(1)
    return "0.0.0"


def parse_javadoc(javadoc: str) -> dict:
    """Parse a Javadoc comment block"""
    result = {
        'description': '',
        'params': [],
        'returns': None,
        'throws': None,
        'example': None,
        'category': None,
        'since': None,
        'primary': False,
        'deprecated': None,
    }
    
    if not javadoc:
        return result
    
    # Remove /** and */ and leading asterisks
    lines = []
    for line in javadoc.split('\n'):
        line = line.strip()
        if line.startswith('/**'):
            line = line[3:]
        if line.endswith('*/'):
            line = line[:-2]
        if line.startswith('*'):
            line = line[1:].strip()
        if line:
            lines.append(line)
    
    description_lines = []
    example_lines = []
    in_example = False
    
    for line in lines:
        # @vortex.* tags
        if line.startswith('@vortex.category'):
            result['category'] = line.split(None, 1)[1] if len(line.split()) > 1 else None
        elif line.startswith('@vortex.since'):
            result['since'] = line.split(None, 1)[1] if len(line.split()) > 1 else None
        elif line.startswith('@vortex.primary'):
            result['primary'] = True
        elif line.startswith('@vortex.deprecated'):
            result['deprecated'] = line.split(None, 1)[1] if len(line.split()) > 1 else True
        # Standard Javadoc tags
        elif line.startswith('@param'):
            parts = line.split(None, 2)
            if len(parts) >= 2:
                param = {'name': parts[1], 'description': parts[2] if len(parts) > 2 else ''}
                result['params'].append(param)
        elif line.startswith('@return'):
            result['returns'] = line.split(None, 1)[1] if len(line.split()) > 1 else ''
        elif line.startswith('@throws') or line.startswith('@exception'):
            result['throws'] = line.split(None, 1)[1] if len(line.split()) > 1 else ''
        elif line.startswith('@example') or '<pre>' in line:
            in_example = True
        elif '</pre>' in line or (in_example and line.startswith('@')):
            in_example = False
        elif in_example:
            example_lines.append(line)
        elif not line.startswith('@') and not line.startswith('<p>') and not line.startswith('</p>'):
            description_lines.append(line)
    
    result['description'] = ' '.join(description_lines).strip()
    if example_lines:
        # Clean up Javadoc code block markers
        example_code = '\n'.join(example_lines)
        example_code = example_code.replace('{@code', '').strip()
        # Remove trailing } that's part of the Javadoc syntax
        if example_code.endswith('}'):
            example_code = example_code[:-1].rstrip()
        result['example'] = example_code
    
    return result


def extract_methods(java_file: Path) -> dict:
    """Extract methods from a Java source file"""
    content = java_file.read_text()
    all_methods = []
    
    # Pattern to match Javadoc followed by optional @Deprecated and public method
    # The Javadoc must be immediately followed by the method (only whitespace allowed between them)
    pattern = r'/\*\*([^*]*(?:\*(?!/)[^*]*)*)\*/\s*(@Deprecated\s+)?public\s+(\S+)\s+(\w+)\s*\(([^)]*)\)'
    
    for match in re.finditer(pattern, content, re.DOTALL):
        javadoc = match.group(1)
        is_deprecated_annotation = match.group(2) is not None
        return_type = match.group(3)
        method_name = match.group(4)
        params_str = match.group(5)
        
        # Skip constructors, close(), internal methods, methods being removed, and @Deprecated methods
        if method_name in ('VortexClient', 'close', 'sign', 'createInvitation'):
            continue
        if is_deprecated_annotation:
            continue
        
        parsed = parse_javadoc(javadoc)
        
        # Skip if no category (not a public API method)
        if not parsed['category']:
            continue
        
        # Parse parameter types from signature
        params = []
        if params_str.strip():
            for param_part in params_str.split(','):
                param_part = param_part.strip()
                if param_part:
                    parts = param_part.split()
                    if len(parts) >= 2:
                        param_type = ' '.join(parts[:-1])
                        param_name = parts[-1]
                        # Find description from Javadoc
                        desc = ''
                        for p in parsed['params']:
                            if p['name'] == param_name:
                                desc = p['description']
                                break
                        params.append({
                            'name': param_name,
                            'type': param_type,
                            'required': True,
                            'description': desc
                        })
        
        # Build signature
        param_sig = ', '.join(f"{p['type']} {p['name']}" for p in params)
        signature = f"{method_name}({param_sig}): {return_type}"
        
        method = {
            'name': method_name,
            'category': parsed['category'] or 'uncategorized',
            'signature': signature,
            'params': params,
            'returns': {'type': return_type, 'description': parsed['returns'] or ''},
            'description': parsed['description'],
            'since': parsed['since'] or '0.1.0',
            'primary': parsed['primary'],
        }
        
        if parsed['example']:
            method['example'] = {'code': parsed['example']}
        
        if parsed['deprecated']:
            method['deprecated'] = True
            if isinstance(parsed['deprecated'], str):
                method['deprecationMessage'] = parsed['deprecated']
        
        all_methods.append(method)
    
    # Define method order to match Node SDK
    method_order = [
        # Primary (core) methods
        'generateToken', 'getInvitation', 'acceptInvitation',
        # Secondary methods in order
        'generateJwt', 'getInvitationsByTarget', 'revokeInvitation',
        'acceptInvitations', 'deleteInvitationsByGroup', 'getInvitationsByGroup',
        'deleteInvitationsByScope', 'getInvitationsByScope', 'reinvite',
        'getAutojoinDomains', 'configureAutojoin', 'syncInternalInvitation',
    ]
    
    def sort_key(m):
        name = m['name']
        try:
            return method_order.index(name)
        except ValueError:
            return len(method_order)
    
    all_methods.sort(key=sort_key)
    
    # Split into primary/secondary
    methods = {'primary': [], 'secondary': []}
    seen = set()
    for m in all_methods:
        if m['name'] in seen:
            continue
        seen.add(m['name'])
        is_primary = m.pop('primary', False)
        if is_primary:
            methods['primary'].append(m)
        else:
            methods['secondary'].append(m)
    
    return methods


def extract_types(types_dir: Path) -> list:
    """Extract type definitions from Java type classes"""
    types = []
    
    if not types_dir.exists():
        return types
    
    # Priority types that should appear first (most commonly used)
    priority_types = [
        'GenerateTokenPayload', 'TokenUser', 'AcceptUser', 'InvitationResult',
        'InvitationTarget', 'InvitationScope', 'InvitationGroup'
    ]
    
    for java_file in sorted(types_dir.glob("*.java")):
        content = java_file.read_text()
        class_name = java_file.stem
        
        # Skip enums and internal types
        if 'enum ' in content or class_name.endswith('Type') or class_name.endswith('Response') or class_name.endswith('Request'):
            continue
        
        # Extract class Javadoc
        class_doc_match = re.search(r'/\*\*([^*]*(?:\*(?!/)[^*]*)*)\*/\s*(?:@\w+\s*)*public\s+class', content)
        class_description = ''
        if class_doc_match:
            parsed = parse_javadoc(class_doc_match.group(1))
            class_description = parsed['description']
        
        # Extract fields with @JsonProperty
        fields = []
        field_pattern = r'(?:/\*\*([^*]*(?:\*(?!/)[^*]*)*)\*/\s*)?(?:@\w+(?:\([^)]*\))?\s*)*@JsonProperty\("(\w+)"\)\s*private\s+([\w<>, ]+?)\s+(\w+);'
        
        for match in re.finditer(field_pattern, content, re.DOTALL):
            field_doc = match.group(1) or ''
            json_name = match.group(2)
            field_type = match.group(3)
            field_var = match.group(4)
            
            # Parse field Javadoc for description
            field_desc = ''
            if field_doc:
                parsed = parse_javadoc(field_doc)
                field_desc = parsed['description']
            
            # Map Java types to more readable types
            type_map = {
                'String': 'String',
                'Boolean': 'Boolean',
                'boolean': 'boolean',
                'Integer': 'Integer',
                'int': 'int',
                'Long': 'Long',
                'long': 'long',
            }
            display_type = type_map.get(field_type, field_type)
            
            # Determine if required (heuristic: primitive types or non-null by convention)
            # Note: email is optional - only id is required for user identification
            required = field_type in ('int', 'long', 'boolean') or json_name in ('id', 'type')
            
            fields.append({
                'name': json_name,
                'type': display_type,
                'required': required,
                'description': field_desc
            })
        
        if fields:  # Only add types that have documented fields
            types.append({
                'name': class_name,
                'description': class_description,
                'fields': fields
            })
    
    # Sort with priority types first
    def sort_key(t):
        name = t['name']
        try:
            return priority_types.index(name)
        except ValueError:
            return len(priority_types) + 1
    
    types.sort(key=sort_key)
    
    return types


def build_manifest() -> dict:
    """Build the complete manifest"""
    version = get_version()
    
    # Find VortexClient.java
    client_file = SDK_DIR / "src/main/java/com/vortexsoftware/sdk/VortexClient.java"
    methods = extract_methods(client_file) if client_file.exists() else {'primary': [], 'secondary': []}
    
    # Extract types from types directory
    types_dir = SDK_DIR / "src/main/java/com/vortexsoftware/sdk/types"
    types = extract_types(types_dir)
    
    return {
        "sdk": {
            "name": "vortex-java-sdk",
            "language": "java",
            "version": version,
            "repository": "https://github.com/teamvortexsoftware/vortex-java-sdk",
            "package": {"name": "com.vortexsoftware:vortex-java-sdk", "registry": "maven"}
        },
        "overview": {
            "product": {
                "name": "Vortex",
                "tagline": "Invitation infrastructure for modern apps",
                "description": "Vortex handles the complete invitation lifecycle — sending invites via email/SMS/share links, tracking clicks and conversions, managing referral programs, and optimizing your invitation flows with A/B testing.",
                "learnMoreUrl": "https://tryvortex.com"
            },
            "sdkPurpose": {
                "summary": "This backend SDK securely signs user data for Vortex components. Your API key stays on your server, while the signed token is passed to the frontend where Vortex components render the invitation UI.",
                "keyBenefits": [
                    "Keep your API key secure — it never touches the browser",
                    "Sign user identity for attribution — know who sent each invitation",
                    "Control what data components can access via scoped tokens",
                    "Verify webhook signatures for secure event handling"
                ]
            },
            "architecture": {
                "summary": "Vortex uses a split architecture: your backend signs tokens with the SDK, and your frontend renders components that use those tokens to securely interact with Vortex.",
                "flow": [
                    {"step": "1. Install the backend SDK", "description": "Add this SDK to your Java project", "location": "backend", "code": "<dependency>\n  <groupId>com.vortexsoftware</groupId>\n  <artifactId>vortex-java-sdk</artifactId>\n</dependency>"},
                    {"step": "2. Initialize the client", "description": "Create a Vortex client with your API key (keep this on the server!)", "location": "backend", "code": 'import com.vortexsoftware.sdk.VortexClient;\n\nVortexClient client = new VortexClient(System.getenv("VORTEX_API_KEY"));'},
                    {"step": "3. Generate a token for the current user", "description": "When a user loads a page with a Vortex component, generate a signed token on your server", "location": "backend", "code": 'String token = client.generateToken(\n    new GenerateTokenPayload().setUser(\n        new TokenUser().setId(currentUser.getId())\n    )\n);'},
                    {"step": "4. Pass the token to your frontend", "description": "Include the token in your page response or API response", "location": "backend", "code": 'response.put("vortexToken", token);'},
                    {"step": "5. Render a Vortex component with the token", "description": "Use the React/Angular/Web Component with the token", "location": "frontend", "code": 'import { VortexInvite } from "@teamvortexsoftware/vortex-react";\n\n<VortexInvite token={vortexToken} />'},
                    {"step": "6. Vortex handles the rest", "description": "The component securely communicates with Vortex servers, displays the invitation UI, sends emails/SMS, tracks conversions, and reports analytics", "location": "vortex"}
                ],
                "diagram": """
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
"""
            },
            "security": {
                "summary": "Your Vortex API key is a secret that grants full access to your account. It must never be exposed to browsers or client-side code.",
                "whyBackendSigning": "By signing tokens on your server, you:\n\n- Keep your API key secret (it never leaves your server)\n- Control exactly what user data is shared with components\n- Ensure invitations are attributed to real, authenticated users\n- Prevent abuse — users can only send invitations as themselves",
                "optional": "Token signing is controlled by your component configuration in the Vortex dashboard."
            }
        },
        "install": {
            "command": "<dependency>\n  <groupId>com.vortexsoftware</groupId>\n  <artifactId>vortex-java-sdk</artifactId>\n</dependency>",
            "alternates": [{"tool": "gradle", "command": "implementation 'com.vortexsoftware:vortex-java-sdk'"}]
        },
        "quickstart": {
            "description": "Generate a secure token for Vortex components",
            "code": 'VortexClient client = new VortexClient(System.getenv("VORTEX_API_KEY"));\n\nGenerateTokenPayload payload = new GenerateTokenPayload()\n    .setUser(new TokenUser().setId("user-123").setEmail("user@example.com"));\n\nString token = client.generateToken(payload);'
        },
        "initialization": {
            "className": "VortexClient",
            "constructor": {
                "signature": "new VortexClient(String apiKey)",
                "params": [
                    {"name": "apiKey", "type": "String", "required": True, "description": "Your Vortex API key"}
                ],
                "example": 'VortexClient client = new VortexClient(System.getenv("VORTEX_API_KEY"));'
            },
            "envVars": [
                {"name": "VORTEX_API_KEY", "required": True, "description": "Your Vortex API key"},
            ]
        },
        "methods": methods,
        "types": types,
        "webhooks": {
            "supported": True,
            "description": "Webhooks let your server receive real-time notifications when events happen in Vortex. Use them to sync invitation state with your database, trigger onboarding flows, update your CRM, or send internal notifications.",
            "setup": [
                "1. Go to your Vortex dashboard → Integrations → Webhooks tab",
                "2. Click \"Add Webhook\"",
                "3. Enter your endpoint URL (must be HTTPS in production)",
                "4. Copy the signing secret — you'll use this to verify webhook signatures",
                "5. Select which events you want to receive"
            ],
            "verifyMethod": "VortexWebhooks.verifySignature",
            "signatureHeader": "X-Vortex-Signature",
            "example": {
                "description": "Spring Boot webhook handler",
                "code": """import com.vortexsoftware.sdk.VortexWebhooks;
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
}"""
            },
            "useCases": [
                {"title": "Activate users on acceptance", "description": "When invitation.accepted fires, mark the user as active in your database and trigger your onboarding flow."},
                {"title": "Track invitation performance", "description": "Monitor email.delivered, email.opened, and link.clicked events to measure invitation funnel metrics."},
                {"title": "Sync team membership", "description": "Use member.created and group.member.added to keep your internal membership records in sync."},
                {"title": "Alert on delivery issues", "description": "Watch for email.bounced events to proactively reach out via alternative channels."}
            ],
            "events": [
                {"name": "invitation.created", "description": "A new invitation was created"},
                {"name": "invitation.accepted", "description": "An invitation was accepted by the recipient"},
                {"name": "invitation.deactivated", "description": "An invitation was deactivated (revoked or expired)"},
                {"name": "invitation.email.delivered", "description": "Invitation email was successfully delivered"},
                {"name": "invitation.email.bounced", "description": "Invitation email bounced (invalid address)"},
                {"name": "invitation.email.opened", "description": "Recipient opened the invitation email"},
                {"name": "invitation.link.clicked", "description": "Recipient clicked the invitation link"},
                {"name": "invitation.reminder.sent", "description": "A reminder email was sent for a pending invitation"},
                {"name": "member.created", "description": "A new member was created from an accepted invitation"},
                {"name": "group.member.added", "description": "A member was added to a scope/group"},
                {"name": "deployment.created", "description": "A new deployment configuration was created"},
                {"name": "deployment.deactivated", "description": "A deployment was deactivated"},
                {"name": "abtest.started", "description": "An A/B test was started"},
                {"name": "abtest.winner_declared", "description": "An A/B test winner was declared"},
                {"name": "email.complained", "description": "Recipient marked the email as spam"}
            ],
            "methods": [
                {
                    "name": "verifySignature",
                    "signature": "boolean verifySignature(String payload, String signature)",
                    "description": "Verify the HMAC-SHA256 signature of an incoming webhook payload. Returns true if valid, false otherwise.",
                    "params": [
                        {"name": "payload", "type": "String", "description": "The raw request body"},
                        {"name": "signature", "type": "String", "description": "The value of the X-Vortex-Signature header"}
                    ],
                    "returns": "boolean"
                },
                {
                    "name": "parseEvent",
                    "signature": "WebhookEvent parseEvent(String payload)",
                    "description": "Parse a verified webhook payload into a WebhookEvent object. Call verifySignature first to ensure authenticity.",
                    "params": [
                        {"name": "payload", "type": "String", "description": "The raw request body (JSON)"}
                    ],
                    "returns": "WebhookEvent"
                },
                {
                    "name": "constructEvent",
                    "signature": "WebhookEvent constructEvent(String payload, String signature) throws VortexWebhookSignatureException",
                    "description": "Verify and parse an incoming webhook payload in one step. Throws VortexWebhookSignatureException if signature is invalid.",
                    "params": [
                        {"name": "payload", "type": "String", "description": "The raw request body"},
                        {"name": "signature", "type": "String", "description": "The value of the X-Vortex-Signature header"}
                    ],
                    "returns": "WebhookEvent",
                    "throws": "VortexWebhookSignatureException"
                }
            ]
        },
        "errors": {
            "baseException": "VortexException",
            "types": [
                {
                    "name": "VortexWebhookSignatureException",
                    "description": "Thrown when webhook signature verification fails. Check that you are using the raw request body and the correct signing secret.",
                    "thrownBy": ["VortexWebhooks.constructEvent"]
                },
                {"name": "VortexException", "description": "Thrown for validation errors (e.g., missing API key, invalid parameters) or API failures"}
            ]
        },
        "examples": {
            "install": {
                "maven": '''<dependency>
    <groupId>com.vortexsoftware</groupId>
    <artifactId>vortex-java-sdk</artifactId>
    <version>LATEST</version>
</dependency>''',
                "gradle": "implementation 'com.vortexsoftware:vortex-java-sdk:+'"
            },
            "import": '''import com.vortexsoftware.sdk.Vortex;

Vortex vortex = new Vortex(System.getenv("VORTEX_API_KEY"));''',
            "functions": {
                "generateToken": '''// Generate a signed token for Vortex components
String token = vortex.generateToken(new GenerateTokenPayload()
    .user(new TokenUser()
        .id("user-123")                                      // Required: user ID for attribution
        .email("user@example.com")                           // Optional: user's email
        .name("Jane Doe")                                    // Optional: user's display name
        .avatarUrl("https://example.com/avatars/jane.jpg")   // Optional: user's avatar URL
    )
    .scope("workspace-456")                                  // Optional: scope/workspace ID
    .vars(Map.of("company_name", "Acme Inc"))                // Optional: template variables
);

// Pass token to your frontend for use with Vortex components
return Map.of("token", token);''',
                "generateTokenParts": {
                    "beforeUser": '''// Generate a signed token for Vortex components
String token = vortex.generateToken(new GenerateTokenPayload()
    .user(new TokenUser()
        .id("user-123")                                      // Required: user ID for attribution
        .email("user@example.com")                           // Optional: user's email
        .name("Jane Doe")                                    // Optional: user's display name
        .avatarUrl("https://example.com/avatars/jane.jpg")   // Optional: user's avatar URL
    )''',
                    "scopeLine": '    .scope("workspace-456")                                  // Optional: scope/workspace ID',
                    "varsLine": '    .vars(Map.of("company_name", "Acme Inc"))                // Optional: template variables',
                    "afterUser": ''');

// Pass token to your frontend for use with Vortex components
return Map.of("token", token);'''
                },
                "generateJwt": '''// Generate JWT (legacy - prefer generateToken for new integrations)
String jwt = vortex.generateJwt(new User()
    .id("user-123")
    .email("user@example.com")
    .userName("Jane Doe")                                    // Optional: user's display name
    .userAvatarUrl("https://example.com/avatars/jane.jpg")  // Optional: user's avatar URL
);

System.out.println("JWT: " + jwt);''',
                "generateJwtParts": {
                    "beforeUser": '''// Generate JWT (legacy - prefer generateToken for new integrations)
String jwt = vortex.generateJwt(new User()
    .id("user-123")
    .email("user@example.com")
    .userName("Jane Doe")                                    // Optional: user's display name
    .userAvatarUrl("https://example.com/avatars/jane.jpg")  // Optional: user's avatar URL''',
                    "adminScopesLine": '    .adminScopes(Arrays.asList("autojoin"))              // Optional: grants autojoin admin privileges',
                    "allowedEmailDomainsLine": '    .allowedEmailDomains(Arrays.asList("example.com"))    // Optional: restrict by email domain',
                    "afterUser": ''');

System.out.println("JWT: " + jwt);'''
                },
                "acceptInvitations": '''// Accept one or more invitations for a user
InvitationResult result = vortex.acceptInvitations(
    Arrays.asList("invitation-id-1", "invitation-id-2"),
    new AcceptUser()
        .email("user@example.com")
        .name("John Doe")  // Optional
);

System.out.println("Accepted invitations: " + result);''',
                "getInvitations": '''// Get a single invitation by ID
InvitationResult invitation = vortex.getInvitation("invitation-id");

System.out.println("Invitation: " + invitation);''',
                "getInvitationsByTarget": '''// Get invitations by target
List<InvitationResult> invitations = vortex.getInvitationsByTarget("email", "user@example.com");

System.out.println("Invitations: " + invitations);'''
            }
        }
    }


if __name__ == "__main__":
    out_path = None
    if "--out" in sys.argv:
        idx = sys.argv.index("--out")
        out_path = sys.argv[idx + 1] if idx + 1 < len(sys.argv) else None
    
    manifest = build_manifest()
    json_str = json.dumps(manifest, indent=2)
    
    if out_path:
        Path(out_path).write_text(json_str)
        print(f"Manifest written to {out_path}")
    else:
        print(json_str)
