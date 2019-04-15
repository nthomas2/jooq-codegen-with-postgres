#!/usr/bin/env bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
TARGET_JAR="$(find "${SCRIPT_DIR}/target" -name "*.jar" 2>/dev/null)"

if [ -z "${TARGET_JAR}" ] ; then
    echo "Could not find jar, did you run 'mvn clean install'?"
    exit 1;
fi

java -jar "${TARGET_JAR}"
