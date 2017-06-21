#!/bin/bash
set -xe

# You can run it from any directory.
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_DIR=$DIR/

GRADLE_ARGUMENTS="--no-daemon" # default gradle arguments

if [ "$1" != "" ]; then
    GRADLE_ARGUMENTS=$1 # use custom gradle arguments if provided
fi

# This will: compile the project, run lint, run tests under JVM, package apk, check the code quality and run tests on the device/emulator.
"$PROJECT_DIR"/gradlew $GRADLE_ARGUMENTS clean --stacktrace
"$PROJECT_DIR"/gradlew $GRADLE_ARGUMENTS build --stacktrace -Dscan
"$PROJECT_DIR"/gradlew $GRADLE_ARGUMENTS spoon --stacktrace
