#!/bin/bash
set -e

echo "Building and signing artifacts..."
mvn clean verify -DskipTests

VERSION="1.0.1"
GROUP_ID_PATH="com/vortexsoftware"
ARTIFACT_ID="vortex-java-sdk"
TARGET_DIR="target"
BUNDLE_DIR="$TARGET_DIR/maven-central-bundle"
ARTIFACT_DIR="$BUNDLE_DIR/$GROUP_ID_PATH/$ARTIFACT_ID/$VERSION"

echo "Creating bundle directory structure..."
mkdir -p "$ARTIFACT_DIR"

echo "Copying artifacts to bundle directory..."
cp "$TARGET_DIR/$ARTIFACT_ID-$VERSION.jar" "$ARTIFACT_DIR/"
cp "$TARGET_DIR/$ARTIFACT_ID-$VERSION.jar.asc" "$ARTIFACT_DIR/"
cp "$TARGET_DIR/$ARTIFACT_ID-$VERSION-javadoc.jar" "$ARTIFACT_DIR/"
cp "$TARGET_DIR/$ARTIFACT_ID-$VERSION-javadoc.jar.asc" "$ARTIFACT_DIR/"
cp "$TARGET_DIR/$ARTIFACT_ID-$VERSION-sources.jar" "$ARTIFACT_DIR/"
cp "$TARGET_DIR/$ARTIFACT_ID-$VERSION-sources.jar.asc" "$ARTIFACT_DIR/"

echo "Copying and signing POM..."
cp "pom.xml" "$ARTIFACT_DIR/$ARTIFACT_ID-$VERSION.pom"
gpg --detach-sign --armor --pinentry-mode loopback "$ARTIFACT_DIR/$ARTIFACT_ID-$VERSION.pom"

# Generate checksums
echo "Generating checksums..."
cd "$ARTIFACT_DIR"
for file in *.jar *.pom *.asc; do
    if [ -f "$file" ]; then
        md5sum "$file" | awk '{print $1}' > "$file.md5"
        sha1sum "$file" | awk '{print $1}' > "$file.sha1"
        sha256sum "$file" | awk '{print $1}' > "$file.sha256"
        sha512sum "$file" | awk '{print $1}' > "$file.sha512"
    fi
done
cd -

# Create the bundle zip
echo "Creating bundle zip..."
cd "$BUNDLE_DIR"
zip -r "../maven-central-bundle.zip" .
cd -

echo ""
echo "Bundle created successfully at: $TARGET_DIR/maven-central-bundle.zip"
echo ""
echo "To upload manually:"
echo "1. Go to https://central.sonatype.com/publishing"
echo "2. Click 'Upload Bundle'"
echo "3. Select the file: $PWD/$TARGET_DIR/maven-central-bundle.zip"
echo "4. Follow the prompts to publish"
