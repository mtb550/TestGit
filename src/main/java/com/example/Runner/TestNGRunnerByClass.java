package com.example.Runner;

import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.theoryinpractice.testng.configuration.TestNGConfiguration;
import com.theoryinpractice.testng.configuration.TestNGConfigurationType;

public class TestNGRunnerByClass {

    public static void runTestClass(Project project, String className) {
        ReadAction.nonBlocking(() -> {
            PsiClass psiClass = JavaPsiFacade.getInstance(project)
                    .findClass(className, GlobalSearchScope.allScope(project));

            if (psiClass == null) {
                System.err.println("Class not found: " + className);
                return;
            }

            TestNGConfigurationType configType = TestNGConfigurationType.getInstance();
            RunManager runManager = RunManager.getInstance(project);

            RunnerAndConfigurationSettings settings = runManager.createConfiguration(
                    "Run " + className, configType.getConfigurationFactories()[0]);
            TestNGConfiguration configuration = (TestNGConfiguration) settings.getConfiguration();

            configuration.setClassConfiguration(psiClass);

            runManager.addConfiguration(settings);
            runManager.setSelectedConfiguration(settings);

            ApplicationManager.getApplication().invokeLater(() ->
                    ProgramRunnerUtil.executeConfiguration(settings, DefaultRunExecutor.getRunExecutorInstance())
            );
        }).submit(AppExecutorUtil.getAppExecutorService());
    }
}