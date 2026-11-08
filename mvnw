#!/bin/sh
# ProTrack Maven Wrapper
PRG="$0"
while [ -h "$PRG" ] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`"/$link"
  fi
done
DIR=`dirname "$PRG"`
DIR=`cd "$DIR" && pwd`

if [ -z "$JAVA_HOME" ]; then
  if [ -d "/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home" ]; then
    export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home"
  elif [ -x "/usr/libexec/java_home" ]; then
    export JAVA_HOME=`/usr/libexec/java_home 2>/dev/null`
  fi
fi

if [ -x "$DIR/maven/bin/mvn" ]; then
  exec "$DIR/maven/bin/mvn" "$@"
elif which mvn > /dev/null 2>&1; then
  exec mvn "$@"
else
  echo "Error: Neither local Maven in $DIR/maven nor system mvn was found."
  exit 1
fi
