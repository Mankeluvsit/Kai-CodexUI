#!/usr/bin/env sh
#
# Gradle start script for UN*X
#
# Set the GRADLE_HOME variable if it's not already set
if [ -z "${GRADLE_HOME}" ]; then
  DIR="$(cd "$(dirname "$0")/.." && pwd)"
  GRADLE_HOME="${DIR}"
fi

# Execute the Gradle command
exec "${GRADLE_HOME}/bin/gradle" "$@"