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
CACHE_DIR="${GRADLE_USER_HOME:-$HOME/.gradle}/wrapper/dists/$DIST_DIR_NAME"

if [ ! -d "$CACHE_DIR" ]; then
  mkdir -p "$CACHE_DIR"
fi

ZIP_PATH="$CACHE_DIR/$DIST_NAME"
if [ ! -f "$ZIP_PATH" ]; then
  echo "Downloading Gradle distribution: $DIST_URL"
  curl -fsSL "$DIST_URL" -o "$ZIP_PATH"
fi

GRADLE_BIN=$(find "$CACHE_DIR" -type f -path '*/bin/gradle' | head -n 1 || true)
if [ -z "$GRADLE_BIN" ]; then
  unzip -qo "$ZIP_PATH" -d "$CACHE_DIR"
  GRADLE_BIN=$(find "$CACHE_DIR" -type f -path '*/bin/gradle' | head -n 1 || true)
fi

if [ -z "$GRADLE_BIN" ]; then
  echo "Unable to locate Gradle executable after extracting $ZIP_PATH" >&2
  exit 1
fi

exec "$GRADLE_BIN" "$@"
