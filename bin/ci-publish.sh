#!/usr/bin/env bash
set -eu

if [[ "$TRAVIS_SECURE_ENV_VARS" == true && "$CI_PUBLISH" == true ]]; then
  echo "Publishing..."
  git log | head -n 20
  if [ -n "$TRAVIS_TAG" ]; then
    echo "$PGP_SECRET" | base64 --decode | gpg --import
    echo "Tag push, publishing release to Sonatype."
    sbt "very publishSigned" sonatypeReleaseAll
  fi
else
  echo "Skipping publish, branch=$TRAVIS_BRANCH"
fi
