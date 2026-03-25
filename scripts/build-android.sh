#!/usr/bin/env bash
set -euo pipefail

echo "==> Building Android project..."
cd "$(dirname "$0")/../android"

./gradlew assembleDebug

echo "==> Android build complete."
