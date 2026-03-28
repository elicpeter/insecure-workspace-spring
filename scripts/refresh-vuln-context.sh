#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

files=(
  ".env.example"
  "src/main/resources/application.yml"
  "src/main/java/com/example/insecurecollab/config/WebConfig.java"
  "src/main/java/com/example/insecurecollab/service/AuthService.java"
  "src/main/java/com/example/insecurecollab/service/WorkspaceService.java"
  "src/main/java/com/example/insecurecollab/service/ProjectService.java"
  "src/main/java/com/example/insecurecollab/service/AttachmentService.java"
  "src/main/java/com/example/insecurecollab/service/ExportService.java"
  "src/main/java/com/example/insecurecollab/service/AdminService.java"
  "src/main/java/com/example/insecurecollab/service/DigestService.java"
  "src/main/java/com/example/insecurecollab/controller/AuthController.java"
  "src/main/java/com/example/insecurecollab/controller/DashboardController.java"
  "src/main/java/com/example/insecurecollab/controller/ProjectController.java"
  "src/main/java/com/example/insecurecollab/controller/AdminController.java"
  "src/main/java/com/example/insecurecollab/controller/ApiController.java"
  "src/main/java/com/example/insecurecollab/controller/SettingsController.java"
  "src/main/java/com/example/insecurecollab/util/InsecurePasswordUtil.java"
  "src/main/java/com/example/insecurecollab/util/InsecureHttpClient.java"
  "src/main/java/com/example/insecurecollab/util/InsecureTemplateRenderer.java"
  "src/main/java/com/example/insecurecollab/util/ShellReportRunner.java"
  "src/main/resources/templates/index.html"
  "src/main/resources/templates/project/detail.html"
  "src/main/resources/templates/settings/index.html"
  "src/main/resources/templates/shared/layout.html"
)

for file in "${files[@]}"; do
  echo
  echo "### $file"
  nl -ba "$ROOT/$file"
done
