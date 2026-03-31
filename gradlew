#!/usr/bin/env sh
set -eu

SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
PROPS_FILE="$SCRIPT_DIR/gradle/wrapper/gradle-wrapper.properties"

if [ ! -f "$PROPS_FILE" ]; then
  echo "Missing $PROPS_FILE" >&2
  exit 1
fi

DIST_URL=$(sed -n 's/^distributionUrl=//p' "$PROPS_FILE" | sed 's#\\:#:#g')
DIST_NAME=$(basename "$DIST_URL")
DIST_DIR_NAME=$(echo "$DIST_NAME" | sed 's/\.zip$//')
EXTRACTED_DIR_NAME=$(echo "$DIST_DIR_NAME" | sed 's/-bin$//' | sed 's/-all$//')
CACHE_DIR="${GRADLE_USER_HOME:-$HOME/.gradle}/wrapper/dists/$DIST_DIR_NAME"
ZIP_PATH="$CACHE_DIR/$DIST_NAME"
GRADLE_BIN="$CACHE_DIR/$EXTRACTED_DIR_NAME/bin/gradle"

mkdir -p "$CACHE_DIR"

if [ ! -f "$ZIP_PATH" ]; then
  echo "Downloading Gradle distribution: $DIST_URL"
  curl -fsSL "$DIST_URL" -o "$ZIP_PATH"
fi

if [ ! -x "$GRADLE_BIN" ]; then
  unzip -qo "$ZIP_PATH" -d "$CACHE_DIR"
fi

if [ ! -x "$GRADLE_BIN" ]; then
  echo "Unable to locate Gradle executable at $GRADLE_BIN" >&2
  exit 1
fi

exec "$GRADLE_BIN" "$@"
