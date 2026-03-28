package com.example.insecurecollab.util;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ShellReportRunner {

    public String buildArchive(String exportRoot, Long workspaceId, String fileName) {
        String outputPath = exportRoot + "/workspace-" + workspaceId + "-" + fileName + ".tar";
        String command = "mkdir -p " + exportRoot + " && tar -cf " + outputPath + " " + exportRoot;
        try {
            Process process = new ProcessBuilder("sh", "-c", command).start();
            process.waitFor();
            return outputPath;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Unable to build archive", e);
        }
    }
}
