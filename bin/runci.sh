#!/bin/sh
set -eux
TEST=${1}

case "$TEST" in
  "cla" )
    ./bin/checkCLA.sh
    ;;
  "scalafmt" )
    ./bin/scalafmt --test
    ;;
  * )
    sbt $TEST
    ;;
esac

