package testGit.pojo;

import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import lombok.Getter;

@Getter
public enum TestStatus {
    PASSED("008000", " [Passed]", new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.GREEN)),
    FAILED("FF0000", " [Failed]", new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.RED)),
    BLOCKED("FFA500", " [Blocked]", new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.ORANGE)),
    PENDING("808080", " [Pending]", SimpleTextAttributes.REGULAR_ATTRIBUTES);

    private final String hex;
    private final String displayText;
    private final SimpleTextAttributes style;

    TestStatus(final String hex, final String displayText, final SimpleTextAttributes style) {
        this.hex = hex;
        this.displayText = displayText;
        this.style = style;
    }
}