package com.example.explorer.nodes;

import com.example.pojo.Directory;
import com.example.util.NodeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public final class DirectoryMapper {
    /**
     * Map file to Project Directory
     * Expected format: 0_100_ProjectName_1
     * Type_Id_Name_Active
     */
    @Nullable
    public static Directory mapProject(@NotNull final File file) {
        System.out.println("DirectoryMapper.mapProject(): " + file.getName());
        try {
            String[] parts = file.getName().split("_", 4);

            if (parts.length < 4) {
                System.err.println("Invalid project folder name format: " + file.getName());
                return createFallbackProject(file);
            }

            return new Directory()
                    .setFile(file)
                    .setFilePath(file.toPath())
                    .setFileName(file.getName())
                    .setType(Integer.parseInt(parts[0]))
                    .setId(Integer.parseInt(parts[1]))
                    .setName(parts[2])
                    .setActive(Integer.parseInt(parts[3]));

        } catch (Exception e) {
            System.err.println("Error mapping project: " + file.getName() + " - " + e.getMessage());
            return createFallbackProject(file);
        }
    }

    /**
     * Map file to Suite/Feature Directory
     * Expected format: 1_200_SuiteName or 2_300_FeatureName
     * Type_Id_Name
     */
    @Nullable
    public static Directory mapSuite(@NotNull final File file) {
        System.out.println("DirectoryMapper.mapSuite(): " + file.getName());

        try {
            String[] parts = file.getName().split("_", 3);

            if (parts.length < 3) {
                System.err.println("Invalid suite/feature folder name format: " + file.getName());
                return createFallbackSuite(file);
            }

            return new Directory()
                    .setFile(file)
                    .setFilePath(file.toPath())
                    .setFileName(file.getName())
                    .setType(Integer.parseInt(parts[0]))
                    .setId(Integer.parseInt(parts[1]))
                    .setName(parts[2]);

        } catch (Exception e) {
            System.err.println("Error mapping suite: " + file.getName() + " - " + e.getMessage());
            return createFallbackSuite(file);
        }

    }

    /**
     * Create fallback project directory when parsing fails
     */
    @NotNull
    private static Directory createFallbackProject(@NotNull final File file) {
        return new Directory()
                .setFile(file)
                .setFilePath(file.toPath())
                .setFileName(file.getName())
                .setType(NodeType.PROJECT.getCode())
                .setId(0)
                .setName(file.getName())
                .setActive(1);
    }

    /**
     * Create fallback suite directory when parsing fails
     */
    @NotNull
    private static Directory createFallbackSuite(@NotNull final File file) {
        return new Directory()
                .setFile(file)
                .setFilePath(file.toPath())
                .setFileName(file.getName())
                .setType(NodeType.SUITE.getCode())
                .setId(0)
                .setName(file.getName());
    }

}