# Changelog

All notable changes to the Vortex Java SDK will be documented in this file.

## [1.0.1] - 2025-10-31

### Fixed
- **CRITICAL**: Fixed JWT generation to use LinkedHashMap for property order preservation
  - HashMap doesn't preserve insertion order, causing JWT signature mismatches
  - Changed to LinkedHashMap to ensure consistent property order matching Node.js SDK
- **CRITICAL**: Created separate `Identifier` and `Group` classes for JWT generation
  - Previously used `InvitationTarget` and `InvitationGroup` which had incorrect fields
  - JWT payload now only includes required fields: {type, id/groupId, name} for groups
  - JWT payload now only includes required fields: {type, value} for identifiers
- Updated `JWTPayload` to use correct types (`Identifier` and `Group`)
- Updated `VortexConfig.VortexUser` to use correct types
- Updated all tests to use new types

### Notes
- This update ensures Java SDK generates byte-for-byte identical JWTs to Node.js SDK
- Essential for cross-platform JWT compatibility with React providers

## [1.0.0] - 2025-01-30

### Added
- Initial release of Vortex Java SDK
- JWT generation compatible with Node.js SDK
- Full Vortex API integration
- Spring Boot integration support
