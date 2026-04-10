package testGit.pojo;

import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import lombok.Getter;

import java.awt.*;

@Getter
public enum TestStatus {
    PASSED(
            "008000",
            " [Passed]",
            new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.GREEN),
            new HoverAction(
                    new JBColor(new Color(39, 174, 96, 40), new Color(46, 125, 50, 60)),
                    new JBColor(new Color(39, 174, 96), new Color(129, 199, 132))
            )
    ),
    FAILED(
            "FF0000",
            " [Failed]",
            new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.RED),
            new HoverAction(
                    new JBColor(new Color(192, 57, 43, 40), new Color(183, 28, 28, 60)),
                    new JBColor(new Color(192, 57, 43), new Color(229, 115, 115))
            )
    ),
    BLOCKED(
            "FFA500",
            " [Blocked]",
            new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.ORANGE),
            new HoverAction(
                    new JBColor(new Color(243, 156, 18, 40), new Color(237, 108, 2, 60)),
                    new JBColor(new Color(243, 156, 18), new Color(255, 183, 77))
            )
    ),
    PENDING(
            "808080",
            " [Pending]",
            SimpleTextAttributes.REGULAR_ATTRIBUTES,
            new HoverAction(
                    new JBColor(new Color(128, 128, 128, 40), new Color(128, 128, 128, 60)),
                    JBColor.GRAY
            )
    );

    private final String hex;
    private final String displayText;
    private final SimpleTextAttributes style;
    private final HoverAction hoverAction;

    TestStatus(String hex, String displayText, SimpleTextAttributes style, HoverAction hoverAction) {
        this.hex = hex;
        this.displayText = displayText;
        this.style = style;
        this.hoverAction = hoverAction;
    }
}