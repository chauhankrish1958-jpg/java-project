#!/bin/bash
# ==============================================================================
# Run Script for ProTrack Online Project Management Tool (Java OOP)
# ==============================================================================

set -e

# Compile if bin directory does not exist or Main.class is missing
if [ ! -f "bin/com/protrack/Main.class" ]; then
    echo "Compiled classes not found. Compiling first..."
    ./compile.sh
fi

# Run Java Main
java -cp bin com.protrack.Main "$@"
