#!/bin/bash -e

if [[ "$TRAVIS_BRANCH" != "master" ]] && [[ -n "$TRAVIS_PULL_REQUEST_SHA" ]]; then
  echo "Exiting release. Release is enabled on master branch only."
  exit 0;
fi

if [[ -z "$RELEASE" ]]; then
  echo "Exiting release. No RELEASE variable"
  exit 0;
fi

if [[ -z "$GITHUB_TOKEN" ]]; then
  echo "Exiting release. No GITHUB_TOKEN variable"
  exit 0;
fi

if [[ -z "$GPG_SECRET_KEYS" ]] || [[ -z "$GPG_OWNERTRUST" ]]; then
  echo "Exiting release. Missing gpg keys."
  exit 0;
fi

if [[ "$(./gradlew currentVersion)" =~ "release-" ]]; then
  echo "Exiting release. Already released."
  exit 0;
fi

git config --local user.name "travis@travis-ci.org"
git config --local user.email "Travis CI"
git stash
git checkout "$TRAVIS_BRANCH"
git stash pop

RELEASE_ARGS="-Ppublish -Prelease.customUsername=\"$GITHUB_TOKEN\""
PUBLISH_ARGS="-Ppublish"

publish() {
  echo $GPG_SECRET_KEYS | base64 --decode > "$TRAVIS_BUILD_DIR/secring.gpg"
  GPG_KEY_RING_FILE="$TRAVIS_BUILD_DIR/secring.gpg" ./gradlew publishRelease -Ppublish
}

publishSnapshot() {
  echo $GPG_SECRET_KEYS | base64 --decode > "$TRAVIS_BUILD_DIR/secring.gpg"
  GPG_KEY_RING_FILE="$TRAVIS_BUILD_DIR/secring.gpg" ./gradlew publish -Ppublish -Prelease.forceSnapshot
}

release() {
  ./gradlew release -Ppublish -Prelease.customUsername=\"$GITHUB_TOKEN\" "$@"
}

if [[ "$RELEASE" = "SNAPSHOT" ]]; then
  echo "Releasing snapshot version"
  publishSnapshot
elif [[ "$RELEASE" = "TRUE" ]] || [[ "$RELEASE" = "PATCH" ]]; then
  echo "Releasing patch version"
  release && publish
elif [[ "$RELEASE" = "MINOR" ]]; then
  echo "Releasing minor version"
  release -Prelease.versionIncrementer=incrementMinor && publish
elif [[ "$RELEASE" = "MAJOR" ]]; then
  echo "Releasing major version"
  release -Prelease.versionIncrementer=incrementMajor && publish
elif [[ "$RELEASE" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "Releasing version: $RELEASE"
  release -Prelease.forceVersion="$RELEASE" && publish
else
  echo "Unrecognized RELEASE value: $RELEASE"
  echo "Expected one or: \"SNAPSHOT\", \"TRUE\", \"PATCH\", \"MINOR\", \"MAJOR\" or semver version number (eg 1.2.3)"
  exit 1;
fi
