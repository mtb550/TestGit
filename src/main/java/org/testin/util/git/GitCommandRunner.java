package org.testin.util.git;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class GitCommandRunner {

    public static String execute(Path workingDirectory, String... command) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workingDirectory.toFile());
        pb.redirectErrorStream(true);

        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String output = reader.lines().collect(Collectors.joining("\n"));
            int exitCode = process.waitFor();

            if (exitCode != 0 && !output.isEmpty()) {
                throw new RuntimeException("Git command failed: " + output);
            }
            return output;
        }
    }
}