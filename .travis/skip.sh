#!/bin/bash -e

if !git diff --name-only $TRAVIS_COMMIT_RANGE | grep -qvE '(\.md$)'; then
  echo "Only docs were updated, stopping build process."
  exit 1
fi
