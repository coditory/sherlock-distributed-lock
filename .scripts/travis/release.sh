#!/bin/bash -e

if [[ -z "$RELEASE" ]]; then
  echo "Exiting release: RELEASE env variable not found"
  exit 0
fi

RELEASE_TAG="$(git tag --points-at HEAD | grep -P "^release-\d+(\.\d+){0,2}$")"
if [[ -n "$RELEASE_TAG" ]]; then
  echo "Exiting release: Current commit is already tagged as $RELEASE_TAG"
  exit 0
fi

# Deduce release version from commit message
if [[ "$RELEASE" == "AUTO" ]]; then
  if echo "$TRAVIS_COMMIT_MESSAGE" | grep -P -q '^.*\[ *ci *release *skip *\].*$'; then
    echo "Exiting release: Commit message contains release skip command"
    exit 0
  elif echo "$TRAVIS_COMMIT_MESSAGE" | grep -P -q '^.*\[ *ci *release *docs *\].*$'; then
    RELEASE="DOCS"
  elif echo "$TRAVIS_COMMIT_MESSAGE" | grep -P -q '^.*\[ *ci *release *snapshot *\].*$'; then
    RELEASE="SNAPSHOT"
  elif echo "$TRAVIS_COMMIT_MESSAGE" | grep -P -q '^.*\[ *ci *release *\].*$'; then
    RELEASE="PATCH"
  elif echo "$TRAVIS_COMMIT_MESSAGE" | grep -P -q '^.*\[ *ci *release *minor *\].*$'; then
    RELEASE="MINOR"
  elif echo "$TRAVIS_COMMIT_MESSAGE" | grep -P -q '^.*\[ *ci *release *major *\].*$'; then
    RELEASE="MAJOR"
  elif echo "$TRAVIS_COMMIT_MESSAGE" | grep -P -q '^.*\[ *ci +release +\d+(\.\d+){0,2} *\].*$'; then
    RELEASE="$(echo "$TRAVIS_COMMIT_MESSAGE" | sed -nE 's|^.*\[ *ci +release +([0-9]+(\.[0-9]+){0,2}) *\].*$|\1|p')"
  elif [[ "$TRAVIS_BRANCH" != "master" ]]; then
    RELEASE="SNAPSHOT"
  else
    RELEASE="PATCH"
  fi
fi

if [[ "$TRAVIS_BRANCH" != "master" ]] && [[ "$RELEASE" != "SNAPSHOT" ]]; then
  echo "Exiting release: Non snapshot releases may be invoked on master branch only. Branch: $TRAVIS_BRANCH"
  exit 0
fi

: ${GPG_SECRET_KEY:?Exiting release: Missing GPG key}
export GPG_KEY_RING_FILE="$HOME/.gnupg/keyring.gpg"
mkdir -p "$HOME/.gnupg"
echo $GPG_SECRET_KEY | base64 --decode | gpg --dearmor > "$GPG_KEY_RING_FILE"

cleanup() {
  rm -rf "$GPG_KEY_RING_FILE"
}
trap cleanup EXIT INT TERM

git config --local user.name "Coditory CI"
git config --local user.email "ci@coditory.com"
git config --local user.signingkey 3943560B44D0A440
git config --local commit.gpgsign true
git checkout "$TRAVIS_BRANCH" > /dev/null 2>&1

.scripts/release.sh "$RELEASE"
