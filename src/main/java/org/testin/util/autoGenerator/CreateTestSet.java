package org.testin.util.autoGenerator;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testin.pojo.Config;
import org.testin.pojo.dto.TestCaseDto;
import org.testin.util.Tools;

import java.io.IOException;
import java.util.List;

public class CreateTestSet implements GeneratorAction {

    @Override
    public void execute(final @Nullable TestCaseDto tc, final @NotNull List<String> fqcn) {

        final String path = String.join(".", fqcn.subList(0, fqcn.size() - 1));
        final String className = fqcn.getLast();
        final String fileName = className + ".java";

        System.out.println("Ready to generate Test Class: " + className + " in package: " + fqcn);

        WriteAction.run(() -> {
            try {
                VirtualFile testSourceRoot = Tools.getInstance().getTestSourceRoot(Config.getProject());

                if (testSourceRoot != null) {
                    VirtualFile vf = VfsUtil.createDirectoryIfMissing(testSourceRoot, path.replace(".", "/"));

                    if (vf != null) {
                        VirtualFile existingFile = vf.findChild(fileName);

                        if (existingFile == null) {
                            VirtualFile javaFile = vf.createChildData(this, fileName);

                            String fileContent = "package " + path + ";\n\n" +
                                    "public class " + className + " {\n" +
                                    "    \n" +
                                    "}\n";

                            VfsUtil.saveText(javaFile, fileContent);
                            System.out.println("Test Class created physically at: " + javaFile.getPath());
                        } else {
                            System.out.println("File already exists: " + existingFile.getPath());
                        }
                    }
                } else {
                    System.out.println("Could not find Main Source Root in the project modules.");
                }

            } catch (IOException ex) {
                System.out.println("Error creating test class: " + ex.getMessage());
            }
        });
    }
}