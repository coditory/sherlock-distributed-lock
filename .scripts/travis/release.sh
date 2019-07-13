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

if [[ "$RELEASE" = "TRUE" ]]; then
  RELEASE="SNAPSHOT"
fi

if [[ "$TRAVIS_BRANCH" != "master" ]] && [[ "$RELEASE" == "BRANCH_SNAPSHOT" ]]; then
  .scripts/release "SNAPSHOT"
  exit 0;
fi

if [[ "$TRAVIS_BRANCH" != "master" ]] && [[ -n "$TRAVIS_PULL_REQUEST_SHA" ]]; then
  echo "Exiting release: Release is enabled on master branch only"
  exit 0;
fi

.scripts/release.sh "$RELEASE"
