#!/bin/sh
APP_HOME="$(cd "$(dirname "$0")" && pwd)"
exec java -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@" 2>/dev/null || \
  "$APP_HOME/gradle/wrapper/gradlew" "$@" 2>/dev/null || true
