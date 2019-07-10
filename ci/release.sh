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
echo $GPG_SECRET_KEYS | base64 --decode | gpg --import
echo $GPG_OWNERTRUST | base64 --decode | gpg --import-ownertrust

if [[ "$RELEASE" = "SNAPSHOT" ]]; then
  echo "Releasing snapshot version"
  ./gradlew publish -Prelease.forceSnapshot
elif [[ "$RELEASE" = "TRUE" ]] || [[ "$RELEASE" = "PATCH" ]]; then
  echo "Releasing patch version"
  ./gradlew release -Prelease.customUsername="$GITHUB_TOKEN" \
  && ./gradlew publish
elif [[ "$RELEASE" = "MINOR" ]]; then
  echo "Releasing minor version"
  ./gradlew release -Prelease.versionIncrementer=incrementMinor -Prelease.customUsername="$GITHUB_TOKEN" \
  && ./gradlew publish
elif [[ "$RELEASE" = "MAJOR" ]]; then
  echo "Releasing major version"
  ./gradlew release -Prelease.versionIncrementer=incrementMajor -Prelease.customUsername="$GITHUB_TOKEN" \
  && ./gradlew publish
elif [[ "$RELEASE" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "Releasing version: $RELEASE"
  ./gradlew release -Prelease.forceVersion="$RELEASE" -Prelease.customUsername="$GITHUB_TOKEN" \
  && ./gradlew publish
else
  echo "Unrecognized RELEASE value: $RELEASE"
  echo "Expected one or: \"SNAPSHOT\", \"TRUE\", \"PATCH\", \"MINOR\", \"MAJOR\" or semver version number (eg 1.2.3)"
  exit 1;
fi
