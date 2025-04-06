package com.example.Runner;
/*
 * إذا كان هدفك هو تشغيل ملف TestNG Suite، فهذا هو النهج الأفضل.
 */

import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.project.Project;
import com.theoryinpractice.testng.configuration.TestNGConfiguration;
import com.theoryinpractice.testng.configuration.TestNGConfigurationType;
import com.theoryinpractice.testng.model.TestType;

public class TestNGRunnerBySuite {

    public static void runTestSuite(Project project, String suiteFilePath) {
        // التأكد من أن مسار ملف Suite ليس فارغاً
        if (suiteFilePath == null || suiteFilePath.trim().isEmpty()) {
            System.err.println("Suite file path is invalid.");
            return;
        }

        // الحصول على نوع تكوين TestNG
        TestNGConfigurationType configType = TestNGConfigurationType.getInstance();
        RunManager runManager = RunManager.getInstance(project);

        // إنشاء تكوين تشغيل جديد لملف TestNG Suite
        RunnerAndConfigurationSettings settings = runManager.createConfiguration(
                "Run TestNG Suite: " + suiteFilePath, configType.getConfigurationFactories()[0]);
        TestNGConfiguration configuration = (TestNGConfiguration) settings.getConfiguration();

        // تعيين تكوين Suite باستخدام مسار الملف المقدم
        configuration.getPersistantData().TEST_OBJECT = TestType.SUITE.getType();
        configuration.getPersistantData().SUITE_NAME = suiteFilePath;

        // مسح أي بيانات موجودة
        configuration.getPersistantData().getPatterns().clear();

        // إضافة واختيار التكوين الجديد
        runManager.addConfiguration(settings);
        runManager.setSelectedConfiguration(settings);

        // تنفيذ التكوين باستخدام منفذ التشغيل الافتراضي
        ProgramRunnerUtil.executeConfiguration(settings, DefaultRunExecutor.getRunExecutorInstance());
    }
}

