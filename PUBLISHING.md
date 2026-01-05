# Publishing the Vortex Java SDK to Maven Central

This guide walks you through publishing the Vortex Java SDK to Maven Central so users can install it with Maven or Gradle.

## Overview

Maven Central is the primary repository for Java artifacts. Publishing requires:
- POM configuration with required metadata
- GPG signing of artifacts
- Sonatype account and verification
- Source and Javadoc JARs

## First time setup (don't need to do this)

### 2. GPG Key for Signing

Generate a GPG key to sign your artifacts:

```bash
# Generate key
gpg --gen-key

# List keys
gpg --list-keys

# Export public key to key server
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID

# Also send to other key servers
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
```

## Prerequisites

### 1. Sonatype Account

1. Create an account at [Sonatype JIRA](https://issues.sonatype.org/secure/Signup!default.jspa)
2. Create a ticket to claim your group ID (e.g., `com.vortexsoftware`)
   - Example: [OSSRH-XXXXX](https://issues.sonatype.org/browse/OSSRH)
   - Verify domain ownership or GitHub organization

### 2. Import existing GPG key for Signing

Dump the private key to a temporary file

```bash
echo "$(vortex secrets read -k ops/providers/maven/service-account/vortexsoftwareops -p privateKey)" > /tmp/gpg-private-key
```

Edit the private key so that 

`-----BEGIN PGP PRIVATE KEY BLOCK-----` and `-----END PGP PRIVATE KEY BLOCK-----` are on a separate line by themselves

Import the gpg key

```bash
gpg --import /tmp/gpg-private-key
```

### 3. Maven Configuration

Create or update `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>USER_TOKEN_USERNAME</username>
      <password>USER_TOKEN_PASSWORD</password>
    </server>
  </servers>

  <profiles>
    <profile>
      <id>gpg</id>
      <properties>
        <gpg.executable>gpg</gpg.executable>
        <gpg.passphrase>YOUR_GPG_PASSPHRASE</gpg.passphrase>
      </properties>
    </profile>
  </profiles>
</settings>
```

You can obtain the <server> sections using 

```bash
vortex secrets read -k ops/providers/maven/service-account/vortexsoftwareops -p userTokenSettingsXML | pbcopy
```

and YOUR_GPG_PASSPHRASE from 

```bash
vortex secrets read -k ops/providers/maven/service-account/vortexsoftwareops -p gpgpassphrase
```


**Security Note**: Use [encrypted passwords](https://maven.apache.org/guides/mini/guide-encryption.html) or environment variables.

### 4. Install Maven

```bash
# macOS
brew install maven

# Linux
sudo apt-get install maven

# Verify
mvn --version
```

## Publishing Process

### Step 1: Verify POM Configuration

The `pom.xml` has been updated with required metadata:
- ✅ License information
- ✅ Developer information
- ✅ SCM information
- ✅ Distribution management
- ✅ GPG signing plugin
- ✅ Source and Javadoc plugins

### Step 2: Update Version

Edit [pom.xml](pom.xml:10):
```xml
<version>1.0.0</version>
```

For SNAPSHOTs during development:
```xml
<version>1.0.0-SNAPSHOT</version>
```

### Step 3: Build and Test

```bash
cd sdks/vortex-java-sdk

# Clean previous builds
mvn clean

# Run tests
mvn test

# Build without signing (for testing)
mvn clean package -Dgpg.skip=true

# Verify Javadocs generate correctly
mvn javadoc:javadoc
```

### Step 4: Deploy to Maven Central

#### For SNAPSHOT Releases (Development)

```bash
mvn clean deploy -Dgpg.skip=true
```

SNAPSHOTs are deployed automatically and don't require manual release.

#### For Production Releases

```bash
# Build and sign artifacts
mvn clean deploy -P gpg

# Or if GPG is configured in settings.xml
mvn clean deploy
```

This will:
1. Build the JAR
2. Generate sources JAR
3. Generate Javadoc JAR
4. Sign all artifacts with GPG
5. Upload to Sonatype staging repository

### Step 5: Release on Sonatype

1. Log in to [Sonatype Nexus](https://central.sonatype.com/)
2. Click "Staging Repositories"
3. Find your uploaded artifacts (comvortexsoftware-XXXX)
4. Click "Close" to validate the artifacts
5. Wait for validation to complete
6. Click "Release" to publish to Maven Central

**Note**: The new Central Portal simplifies this process with automatic validation.

### Step 6: Verify Publication

After 15-30 minutes, verify your artifact is available:

- [Maven Central Search](https://search.maven.org/)
- Direct URL: `https://central.sonatype.com/artifact/com.vortexsoftware/vortex-java-sdk`

## Installation for Users

Once published, users can add the dependency:

### Maven

```xml
<dependency>
    <groupId>com.vortexsoftware</groupId>
    <artifactId>vortex-java-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
dependencies {
    implementation 'com.vortexsoftware:vortex-java-sdk:1.0.0'
}
```

## Automated Publishing with GitHub Actions

Create `.github/workflows/publish-java.yml`:

```yaml
name: Publish Java SDK

on:
  release:
    types: [published]
  workflow_dispatch:
    inputs:
      version:
        description: 'Version to publish'
        required: true

jobs:
  publish:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Set up Maven settings
        uses: s4u/maven-settings-action@v2
        with:
          servers: |
            [{
              "id": "central",
              "username": "${{ secrets.SONATYPE_USERNAME }}",
              "password": "${{ secrets.SONATYPE_PASSWORD }}"
            }]

      - name: Import GPG key
        uses: crazy-max/ghaction-import-gpg@v5
        with:
          gpg_private_key: ${{ secrets.GPG_PRIVATE_KEY }}
          passphrase: ${{ secrets.GPG_PASSPHRASE }}

      - name: Build and publish
        run: |
          cd sdks/vortex-java-sdk
          mvn clean deploy -P gpg
        env:
          GPG_PASSPHRASE: ${{ secrets.GPG_PASSPHRASE }}
```

### GitHub Secrets Setup

Add these secrets to your repository:

1. `SONATYPE_USERNAME` - Your Sonatype username
2. `SONATYPE_PASSWORD` - Your Sonatype password
3. `GPG_PRIVATE_KEY` - Your GPG private key (export with `gpg --export-secret-keys --armor YOUR_KEY_ID`)
4. `GPG_PASSPHRASE` - Your GPG passphrase

## Version Management

Follow [Semantic Versioning](https://semver.org/):

- **MAJOR** (1.0.0 → 2.0.0): Breaking API changes
- **MINOR** (1.0.0 → 1.1.0): New features, backward compatible
- **PATCH** (1.0.0 → 1.0.1): Bug fixes, backward compatible

### Development Versions

- Use `-SNAPSHOT` suffix: `1.0.0-SNAPSHOT`
- SNAPSHOTs are mutable and can be overwritten
- Use for development and testing

### Release Versions

- No suffix: `1.0.0`
- Immutable - once published, cannot be changed
- Create new version for fixes

## POM Requirements Checklist

Maven Central requires these elements in your POM:

- ✅ `groupId`, `artifactId`, `version`
- ✅ `name`, `description`, `url`
- ✅ `licenses` section
- ✅ `developers` section
- ✅ `scm` (source control) section
- ✅ `-sources.jar` artifact
- ✅ `-javadoc.jar` artifact
- ✅ GPG signatures for all artifacts

All requirements are now configured in the updated `pom.xml`.

## Troubleshooting

### GPG Signing Issues

```bash
# Test GPG signing
gpg --sign test.txt

# If "no secret key" error
gpg --list-secret-keys

# Verify key is on keyserver
gpg --keyserver keyserver.ubuntu.com --recv-keys YOUR_KEY_ID
```

### Deployment Failures

1. **401 Unauthorized**: Check Sonatype credentials in `settings.xml`
2. **Missing metadata**: Verify all required POM elements
3. **GPG errors**: Ensure key is generated and passphrase is correct
4. **Validation errors**: Check artifact signatures and checksums

### Build Issues

```bash
# Clean build
mvn clean install -U

# Skip tests temporarily
mvn clean install -DskipTests

# Verbose output
mvn -X clean deploy
```

## Best Practices

1. **Test thoroughly** before releasing
2. **Use SNAPSHOT versions** for development
3. **Version in sync**: Keep version in POM, README, and docs consistent
4. **Changelog**: Maintain a CHANGELOG.md
5. **Release notes**: Write detailed release notes
6. **Backward compatibility**: Avoid breaking changes in minor/patch releases
7. **Security**: Never commit credentials to Git
8. **Documentation**: Update documentation with each release

## Release Checklist

- [ ] Update version in `pom.xml`
- [ ] Update version in `README.md`
- [ ] Update `CHANGELOG.md`
- [ ] Run all tests: `mvn test`
- [ ] Build locally: `mvn clean package`
- [ ] Generate Javadocs: `mvn javadoc:javadoc`
- [ ] Commit version changes
- [ ] Create Git tag: `git tag v1.0.0`
- [ ] Push changes and tag
- [ ] Deploy to Maven Central: `mvn clean deploy`
- [ ] Release on Sonatype Nexus
- [ ] Verify on Maven Central (wait 15-30 min)
- [ ] Create GitHub release
- [ ] Announce release

## Alternative: Using Central Publisher API

The new Central Portal supports direct publishing without Nexus:

1. Create an account at [central.sonatype.com](https://central.sonatype.com/)
2. Generate a user token
3. Use the Central Publishing Maven Plugin (already configured in POM)

This simplifies the process by eliminating manual Nexus steps.

## Resources

- [Maven Central Publishing Guide](https://central.sonatype.org/publish/)
- [Sonatype OSSRH Guide](https://central.sonatype.org/publish/publish-guide/)
- [GPG Setup](https://central.sonatype.org/publish/requirements/gpg/)
- [Maven Settings Encryption](https://maven.apache.org/guides/mini/guide-encryption.html)
- [Central Portal](https://central.sonatype.com/)

## Support

For publishing issues:
- Check [Sonatype Status](https://status.maven.org/)
- Ask on [Sonatype Community](https://community.sonatype.com/)
- Review [OSSRH Guide](https://central.sonatype.org/publish/publish-guide/)

For SDK issues:
- Create an issue on GitHub
- Contact support@vortexsoftware.com
