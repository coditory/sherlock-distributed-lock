#!/bin/bash -e

publish() {
  echo $GPG_SECRET_KEY | base64 --decode | gpg --dearmor > "$TRAVIS_BUILD_DIR/secring.gpg"
  if [[ "$RELEASE" =~ SNAPSHOST$ ]]; then
    GPG_KEY_RING_FILE="$TRAVIS_BUILD_DIR/secring.gpg" ./gradlew publishToNexus -Ppublish -Prelease.forceSnapshot "$@" \
     || exit 1
  else
    GPG_KEY_RING_FILE="$TRAVIS_BUILD_DIR/secring.gpg" ./gradlew publishToNexus -Ppublish --stacktrace "$@" \
     && ./gradlew closeAndReleaseRepository -Ppublish "$@" \
     || exit 1
  fi
  rm -rf "$TRAVIS_BUILD_DIR/secring.gpg"
}

release() {
  ./gradlew release -Ppublish -Prelease.customUsername="$GITHUB_TOKEN" "$@" || exit 1
}

: ${RELEASE:?Exiting release: No RELEASE variable}
: ${GITHUB_TOKEN:?Exiting release: No GITHUB_TOKEN variable}
: ${GPG_SECRET_KEY:?Exiting release: Missing GPG key}

git config --local user.name "travis@travis-ci.org"
git config --local user.email "Travis CI"
git stash
git checkout "$TRAVIS_BRANCH"
git stash pop

if [[ "$TRAVIS_BRANCH" != "master" ]] && [[ "$RELEASE" == "BRANCH_SNAPSHOT" ]]; then
  echo "Releasing branch snapshot"
  publish
  exit 0;
fi

if [[ "$TRAVIS_BRANCH" != "master" ]] && [[ -n "$TRAVIS_PULL_REQUEST_SHA" ]]; then
  echo "Exiting release: Release is enabled on master branch only"
  exit 0;
fi

if [[ "$RELEASE" = "SNAPSHOT" ]]; then
  echo "Releasing snapshot version"
  publish
elif [[ "$RELEASE" = "TRUE" ]] || [[ "$RELEASE" = "PATCH" ]]; then
  echo "Releasing: Patch version"
  release && publish
elif [[ "$RELEASE" = "MINOR" ]]; then
  echo "Releasing: Minor version"
  release -Prelease.versionIncrementer=incrementMinor && publish
elif [[ "$RELEASE" = "MAJOR" ]]; then
  echo "Releasing: Major version"
  release -Prelease.versionIncrementer=incrementMajor && publish
elif [[ "$RELEASE" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "Releasing: Version $RELEASE"
  release -Prelease.forceVersion="$RELEASE" && publish
else
  echo "Unrecognized RELEASE value: $RELEASE"
  echo "Expected one or: \"SNAPSHOT\", \"TRUE\", \"PATCH\", \"MINOR\", \"MAJOR\" or semver version number (eg 1.2.3)"
  exit 1;
fi
