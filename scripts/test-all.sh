#!/usr/bin/env bash
set -euo pipefail

echo "==> Running all tests..."

echo "--- iOS Tests ---"
cd "$(dirname "$0")/../ios"
swift test

echo "--- Android Tests ---"
cd "$(dirname "$0")/../android"
./gradlew testDebugUnitTest

echo "==> All tests complete."
