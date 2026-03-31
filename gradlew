#!/usr/bin/env sh
set -e

if [ -x "$(dirname "$0")/gradle/wrapper/gradle-wrapper.jar" ]; then
  :
fi

if [ -f "$(dirname "$0")/gradle/wrapper/gradle-wrapper.jar" ]; then
  exec java -classpath "$(dirname "$0")/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
fi

if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

echo "ERROR: Gradle wrapper JAR is missing and no 'gradle' binary was found in PATH." >&2
exit 1
