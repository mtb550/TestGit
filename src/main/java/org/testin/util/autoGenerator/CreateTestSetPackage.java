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

public class CreateTestSetPackage implements GeneratorAction {

    @Override
    public void execute(final @Nullable TestCaseDto tc, final @NotNull List<String> fqcn) {
        WriteAction.run(() -> {
            try {
                VirtualFile testSourceRoot = Tools.getInstance().getTestSourceRoot(Config.getProject());

                if (testSourceRoot != null) {
                    VirtualFile vf = VfsUtil.createDirectoryIfMissing(testSourceRoot, String.join("/", fqcn));
                    System.out.println("Package created physically at: " + vf.getPath());

                } else {
                    System.out.println("Could not find Main Source Root in the project modules.");
                }

            } catch (IOException ex) {
                System.out.println("Error creating package: " + ex.getMessage());
            }
        });
    }
}