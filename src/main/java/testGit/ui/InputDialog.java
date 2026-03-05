package testGit.ui;

import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.components.fields.ExtendableTextComponent;
import com.intellij.ui.components.fields.ExtendableTextField;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import testGit.pojo.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class InputDialog {

    public static void show(String title, Icon icon, Consumer<String> onSelected) {
        ExtendableTextField textField = new ExtendableTextField();
        textField.getEmptyText().setText("Name");

        textField.setFont(JBFont.regular());
        textField.setPreferredSize(new Dimension(JBUI.scale(350), textField.getPreferredSize().height));

        textField.addExtension(ExtendableTextComponent.Extension.create(icon, "Icon", null));
        textField.setBorder(JBUI.Borders.empty(8, 12));
        textField.setBackground(UIUtil.getTextFieldBackground());

        JPanel panel = JBUI.Panels.simplePanel(textField);
        panel.setBorder(JBUI.Borders.customLine(JBUI.CurrentTheme.Popup.borderColor(true), 1));

        JBPopup popup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(panel, textField)
                .setTitle(title)
                .setRequestFocus(true)
                .setCancelOnClickOutside(true)
                .setMovable(false)
                .createPopup();

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String text = textField.getText().trim();
                    if (!text.isEmpty()) {
                        onSelected.accept(text);
                        popup.closeOk(null);
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    popup.cancel();
                }
            }
        });

        popup.showCenteredInCurrentWindow(Config.getProject());
    }
}