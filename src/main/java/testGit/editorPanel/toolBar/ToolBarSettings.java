package testGit.editorPanel.toolBar;

import com.intellij.ide.util.PropertiesComponent;
import lombok.Getter;
import lombok.Setter;
import testGit.pojo.Groups;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class ToolBarSettings {
    private static final String KEY_SHOW_GROUPS = "testGit.showGroups";
    private static final String KEY_SHOW_PRIORITY = "testGit.showPriority";
    private static final String KEY_DETAILS = "testGit.selectedDetails";
    private static final String DEFAULT_DETAILS = "ID,Module,Expected Result,Steps,Automation Ref,Business Ref";

    private final Set<Groups> selectedGroups = new HashSet<>();
    private final Set<String> selectedDetails = new HashSet<>();

    @Setter
    private boolean showGroups;
    @Setter
    private boolean showPriority;

    public ToolBarSettings() {
        PropertiesComponent props = PropertiesComponent.getInstance();
        this.showGroups = props.getBoolean(KEY_SHOW_GROUPS, true);
        this.showPriority = props.getBoolean(KEY_SHOW_PRIORITY, true);

        String saved = props.getValue(KEY_DETAILS, DEFAULT_DETAILS);
        if (!saved.isEmpty()) {
            this.selectedDetails.addAll(List.of(saved.split(",")));
        }
    }

    public void save() {
        PropertiesComponent props = PropertiesComponent.getInstance();
        props.setValue(KEY_SHOW_GROUPS, showGroups, true);
        props.setValue(KEY_SHOW_PRIORITY, showPriority, true);
        props.setValue(KEY_DETAILS, String.join(",", selectedDetails));
    }

    public void resetFilters() {
        selectedGroups.clear();
        selectedDetails.clear();
    }
}