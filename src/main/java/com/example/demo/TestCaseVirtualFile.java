package com.example.demo;

import com.example.pojo.TestCase;
import com.intellij.testFramework.LightVirtualFile;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class TestCaseVirtualFile extends LightVirtualFile {
    private final int moduleId;
    private final List<TestCase> testCases;

    public TestCaseVirtualFile(int moduleId, List<TestCase> testCases) {
        super("Module " + moduleId, TestCaseFileType.INSTANCE, "");
        this.moduleId = moduleId;
        this.testCases = testCases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestCaseVirtualFile that)) return false;
        return moduleId == that.moduleId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleId);
    }
}
