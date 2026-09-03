#!/usr/bin/env sh
set -eu

MAVEN_VERSION=3.9.11
SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
MAVEN_DIR="$SCRIPT_DIR/.mvn/apache-maven-$MAVEN_VERSION"

if [ ! -x "$MAVEN_DIR/bin/mvn" ]; then
  ARCHIVE="$SCRIPT_DIR/.mvn/apache-maven-$MAVEN_VERSION-bin.tar.gz"
  mkdir -p "$SCRIPT_DIR/.mvn"
  echo "Downloading Apache Maven $MAVEN_VERSION..."
  curl -fL "https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz" -o "$ARCHIVE"
  tar -xzf "$ARCHIVE" -C "$SCRIPT_DIR/.mvn"
  rm "$ARCHIVE"
fi

exec "$MAVEN_DIR/bin/mvn" "$@"

