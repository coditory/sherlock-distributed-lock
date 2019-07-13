#!/bin/bash -e

VERSION="${1:-PATCH}"

publish() {
  if [[ "$1" == "SNAPSHOT" ]]; then
    ./gradlew publishToNexus -Ppublish -Prelease.forceSnapshot
  else
    ./gradlew publishToNexus -Ppublish \
      && ./gradlew closeAndReleaseRepository -Ppublish
  fi
}

release() {
  if [[ -n "$GITHUB_TOKEN" ]]; then
    ./gradlew release -Ppublish -Prelease.customUsername="$GITHUB_TOKEN" "$@" || exit 1
  else
    ./gradlew release -Ppublish "$@" || exit 1
  fi
}

echo "Releasing version: $VERSION"
if [[ -z "$CI" ]]; then
  # CI servers split build and release
  ./gradlew build --scan \
    && ./gradlew coveralls
fi

if [[ "$VERSION" = "SNAPSHOT" ]]; then
  echo "Releasing snapshot version"
  publish
elif [[ "$VERSION" = "PATCH" ]]; then
  echo "Releasing: Patch version"
  release && publish
elif [[ "$VERSION" = "MINOR" ]]; then
  echo "Releasing: Minor version"
  release -Prelease.versionIncrementer=incrementMinor && publish
elif [[ "$VERSION" = "MAJOR" ]]; then
  echo "Releasing: Major version"
  release -Prelease.versionIncrementer=incrementMajor && publish
elif [[ "$VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "Releasing: Version $VERSION"
  release -Prelease.forceVersion="$VERSION" && publish
else
  echo "Unrecognized version: $VERSION"
  echo "Expected one of: \"SNAPSHOT\", \"TRUE\", \"PATCH\", \"MINOR\", \"MAJOR\" or semver version number (eg 1.2.3)"
  exit 1;
fi
