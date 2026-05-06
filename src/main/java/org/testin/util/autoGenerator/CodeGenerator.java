package org.testin.util.autoGenerator;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.JBUI;
import lombok.Getter;

import java.awt.*;

public class CodeGenerator extends JBCheckBox {
    // todo, put all stored props in separate class.
    private final String PROP_KEY = "testin.automation.generateCode";

    @Getter
    private int change; // todo, here to put the type of update -> group or priority so you can assign the proper update class based on that

    @Getter
    private int typeOfChange; // todo, here to put the type of update -> group or priority so you can assign the proper update class based on that

    public CodeGenerator(final GeneratorType generatorType) {
        if (generatorType != null)
            setToolTipText(generatorType.getTooltip());

        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(JBUI.Borders.emptyRight(8));

        final PropertiesComponent properties = PropertiesComponent.getInstance();
        setSelected(properties.getBoolean(PROP_KEY, true));

        addItemListener(e -> properties.setValue(PROP_KEY, isSelected(), true));
    }
}