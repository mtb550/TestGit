package testGit.editorPanel.toolBar;

import com.intellij.ide.util.PropertiesComponent;
import lombok.Getter;
import testGit.pojo.Group;
import testGit.pojo.Priority;
import testGit.pojo.TestCaseAttributes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ToolBarSettings {
    private static final String KEY_DETAILS = "testGit.selectedDetails.v2";

    private static final String DEFAULT_DETAILS = Arrays.stream(TestCaseAttributes.values())
            .filter(TestCaseAttributes::isDefaultToolBarSelected)
            .map(Enum::name)
            .collect(Collectors.joining(","));

    private final Set<Group> selectedGroup = new HashSet<>();
    private final Set<Priority> selectedPriority = new HashSet<>();
    private final Set<String> selectedDetails = new HashSet<>();

    public ToolBarSettings() {
        final PropertiesComponent props = PropertiesComponent.getInstance();
        final String saved = props.getValue(KEY_DETAILS, DEFAULT_DETAILS);

        Arrays.stream(saved.split(",")).filter(s -> !s.isEmpty()).forEach(selectedDetails::add);
    }

    public void saveProps() {
        final PropertiesComponent props = PropertiesComponent.getInstance();
        props.setValue(KEY_DETAILS, String.join(",", selectedDetails));
    }

    public void resetFilters() {
        selectedGroup.clear();
        selectedPriority.clear();
    }
}