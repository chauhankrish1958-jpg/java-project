#!/bin/bash
# ==============================================================================
# Compile Script for ProTrack Online Project Management Tool (Java OOP)
# ==============================================================================

set -e

echo "Compiling ProTrack Java OOP Project..."
mkdir -p bin

javac -d bin \
    src/com/protrack/model/*.java \
    src/com/protrack/service/*.java \
    src/com/protrack/util/*.java \
    src/com/protrack/Main.java

echo "✔ Compilation completed successfully into bin/ directory."
