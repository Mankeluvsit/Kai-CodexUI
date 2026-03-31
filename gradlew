#!/usr/bin/env sh
set -e

if [ -f "$(dirname "$0")/gradle/wrapper/gradle-wrapper.jar" ]; then
  JAVA_CMD=${JAVA_HOME:+$JAVA_HOME/bin/}java
  exec "$JAVA_CMD" -classpath "$(dirname "$0")/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
fi

if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

echo "Gradle wrapper JAR is missing and no 'gradle' executable is available on PATH." >&2
exit 1
