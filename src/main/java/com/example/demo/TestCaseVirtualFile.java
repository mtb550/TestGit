package com.example.demo;

import com.example.pojo.TestCase;
import com.intellij.testFramework.LightVirtualFile;
import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Getter
public class TestCaseVirtualFile extends LightVirtualFile {
    private final String featurePath; // المسار الكامل للمجلد المعني
    private final List<TestCase> testCases;

    public TestCaseVirtualFile(String featurePath, List<TestCase> testCases) {
        // نستخدم اسم المجلد فقط كعنوان للتبويب ليظهر بشكل جميل للمستخدم
        super(extractFolderName(featurePath), TestCaseFileType.INSTANCE, "");
        this.featurePath = featurePath;
        this.testCases = testCases;
    }

    /**
     * دالة مساعدة لاستخراج اسم المجلد من المسار الكامل
     * مثال: /home/user/Test/Login -> يعيد Login
     */
    private static String extractFolderName(String path) {
        try {
            Path p = Paths.get(path);
            return p.getFileName().toString();
        } catch (Exception e) {
            return "Test Cases";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestCaseVirtualFile that)) return false;
        // المقارنة الآن تعتمد على المسار لضمان عدم تكرار فتح نفس المجلد
        return Objects.equals(featurePath, that.featurePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(featurePath);
    }
}