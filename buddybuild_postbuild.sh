#!/usr/bin/env bash

$BUDDYBUILD_WORKSPACE/gradlew jacocoTestDebugUnitTestReport

cp -r --parents $BUDDYBUILD_WORKSPACE/**/build/reports/jacoco/* .

curl -s https://codecov.io/bash > .codecov
chmod +x .codecov
./.codecov