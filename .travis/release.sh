#!/bin/bash -e

: ${RELEASE:?Exiting release: No RELEASE variable}
: ${GITHUB_TOKEN:?Exiting release: No GITHUB_TOKEN variable}
: ${GPG_SECRET_KEY:?Exiting release: Missing GPG key}
export GPG_KEY_RING_FILE="$HOME/.gnupg/keyring.gpg"

cleanup() {
  rm -rf "$GPG_KEY_RING_FILE"
}
trap cleanup EXIT INT TERM

git config --local user.name "travis@travis-ci.org"
git config --local user.email "Travis CI"
git checkout "$TRAVIS_BRANCH" >/dev/null 2>1

mkdir -p "$HOME/.gnupg"
echo $GPG_SECRET_KEY | base64 --decode | gpg --dearmor > "$GPG_KEY_RING_FILE"

publish() {
  if [[ "$RELEASE" =~ SNAPSHOT$ ]]; then
    ./gradlew publishToNexus -Ppublish -Prelease.forceSnapshot \
      || exit 1
  else
    ./gradlew publishToNexus -Ppublish \
      && ./gradlew closeAndReleaseRepository -Ppublish \
      || exit 1
  fi
}

release() {
  ./gradlew release -Ppublish -Prelease.customUsername="$GITHUB_TOKEN" "$@" || exit 1
}

if [[ "$RELEASE" = "TRUE" ]]; then
  RELEASE="SNAPSHOT"
fi

if [[ "$TRAVIS_BRANCH" != "master" ]] && [[ "$RELEASE" == "BRANCH_SNAPSHOT" ]]; then
  echo "Releasing branch snapshot"
  publish "SNAPSHOT"
  exit 0;
fi

if [[ "$TRAVIS_BRANCH" != "master" ]] && [[ -n "$TRAVIS_PULL_REQUEST_SHA" ]]; then
  echo "Exiting release: Release is enabled on master branch only"
  exit 0;
fi

if [[ "$RELEASE" = "SNAPSHOT" ]]; then
  echo "Releasing snapshot version"
  publish
elif [[ "$RELEASE" = "PATCH" ]]; then
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
