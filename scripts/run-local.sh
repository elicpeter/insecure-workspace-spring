#!/usr/bin/env bash
set -euo pipefail

echo "WARNING: starting an intentionally insecure local-only application."
echo "Do not deploy this software."

mvn spring-boot:run
