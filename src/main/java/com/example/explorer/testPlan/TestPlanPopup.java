package com.example.explorer.testPlan;

import com.example.pojo.TestPlan;

import javax.swing.*;

public class TestPlanPopup {
    public static void showFolderInfo(TestPlan plan, JComponent parent) {
        new TestPlanDialog(plan, parent).show();
    }
}