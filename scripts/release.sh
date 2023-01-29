#!/usr/bin/env bash

EXISTING=${1}
NEW=${2}
if [[ -z "${EXISTING}"  || -z ${NEW} ]]; then
  echo "=== Please call with:"
  echo "   scripts/release.sh ExistingVersion NewVersion"
  exit 1
fi

if [[ -n $(git status -s) ]]; then
  echo "There are uncommitted changes, commit first"
  exit 1
fi


grep "<version>${EXISTING}-SNAPSHOT</version>" pom.xml > /dev/null

if [[ $? -ne 0 ]]; then
  echo "=== Version ${EXISTING} not found in pom.xml"
  exit 1
fi

mvn clean test
if [[ $? -ne 0 ]]; then
  echo "=== compilation or testing failed"
  exit 1
fi

# Stop on any error
set -e

mvn versions:set -DnewVersion="${EXISTING}"
git add .
git commit -m "Created version ${EXISTING}"
git push
git tag "javautils-${EXISTING}"
git push --tags
mvn versions:set -DnewVersion="${NEW}-SNAPSHOT"
git add .
git commit -m "Prepared version ${NEW}"
git push

