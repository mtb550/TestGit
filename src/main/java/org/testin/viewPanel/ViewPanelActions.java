package org.testin.viewPanel;

import com.intellij.openapi.actionSystem.AnAction;
import org.testin.actions.NextTestCase;
import org.testin.actions.PreviousTestCase;

import javax.swing.*;
import java.util.List;

public class ViewPanelActions {

    public static List<AnAction> create(final ViewPagination page, final JComponent component) {
        return List.of(
                new PreviousTestCase(page, component),
                new NextTestCase(page, component)
        );
    }
}
