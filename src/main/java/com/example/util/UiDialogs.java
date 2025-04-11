package com.example.util;

import javax.swing.*;

public class UiDialogs {

    public static boolean confirmDelete(JComponent parent, int count) {
        String message = "Are you sure you want to delete " + count + " node(s)?";
        return JOptionPane.showConfirmDialog(parent, message, "Confirm Delete", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    public static boolean confirmMove(JComponent parent, int count) {
        String message = "Are you sure you want to move " + count + " node(s)?";
        return JOptionPane.showConfirmDialog(parent, message, "Confirm Move", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }
}
