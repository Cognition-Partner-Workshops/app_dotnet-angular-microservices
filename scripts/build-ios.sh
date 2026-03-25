#!/usr/bin/env bash
set -euo pipefail

echo "==> Building iOS project..."
cd "$(dirname "$0")/../ios"

swift package resolve
swift build

echo "==> iOS build complete."
