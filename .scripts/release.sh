#!/bin/bash -e

VERSION="${1:-PATCH}"

publish() {
  if [[ "$VERSION" == "SNAPSHOT" ]]; then
    ./gradlew publishToNexus -Ppublish -Prelease.forceSnapshot
  else
    ./gradlew publishToNexus -Ppublish \
      && ./gradlew closeAndReleaseRepository -Ppublish
  fi
}

release() {
  local ARGS="$@";
  if [[ -n "$GITHUB_TOKEN" ]]; then
    ARGS="$ARGS -Prelease.customUsername=$GITHUB_TOKEN"
  fi
  if [[ "$VERSION" == "SNAPSHOT" ]]; then
    ARGS="$ARGS -Prelease.pushTagsOnly"
  fi
  ./gradlew release -Ppublish $ARGS
}

if [[ -z "$CI" ]]; then
  # CI servers split build and release
  ./gradlew build --scan \
    && ./gradlew coveralls
fi

echo "Releasing: $VERSION"
if [[ "$VERSION" = "SNAPSHOT" ]]; then
  publish
elif [[ "$VERSION" = "PATCH" ]]; then
  release && publish
elif [[ "$VERSION" = "MINOR" ]]; then
  release -Prelease.versionIncrementer=incrementMinor && publish
elif [[ "$VERSION" = "MAJOR" ]]; then
  release -Prelease.versionIncrementer=incrementMajor && publish
elif [[ "$VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  release -Prelease.forceVersion="$VERSION" && publish
else
  echo "Unrecognized version: $VERSION"
  echo "Expected one of: \"SNAPSHOT\", \"TRUE\", \"PATCH\", \"MINOR\", \"MAJOR\" or semver (eg 1.2.3)"
  exit 1;
fi
