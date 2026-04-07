package testGit.ui.TestCase;

import testGit.pojo.dto.TestCaseDto;
import testGit.ui.TestCase.edit.EditTestCaseUI;
import testGit.ui.TestCase.edit.GenericSelectionPopup;
import testGit.ui.TestCase.edit.UpdateField;
import testGit.ui.TestCase.edit.bulk.ExpectedBulkEditor;
import testGit.ui.TestCase.edit.bulk.PriorityBulkEditor;
import testGit.ui.TestCase.edit.bulk.StepsBulkEditor;
import testGit.ui.TestCase.edit.bulk.TitleBulkEditor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class TestCaseEditMenu {
    public static void show(final List<TestCaseDto> selectedItems,
                            final Consumer<TestCaseDto> onSingleUpdate,
                            final Runnable onBulkUpdate,
                            final Set<String> uniqueStepsCache) {

        if (selectedItems == null || selectedItems.isEmpty()) {
            return;
        }

        if (selectedItems.size() == 1) {
            showSingle(selectedItems.getFirst(), onSingleUpdate, uniqueStepsCache);
        } else {
            showBulk(selectedItems, onBulkUpdate);
        }
    }

    private static void showSingle(final TestCaseDto existingDto, final Consumer<TestCaseDto> onUpdate, final Set<String> uniqueStepsCache) {
        showMenu("Edit Test Case", selectedField ->
                new EditTestCaseUI().show(existingDto, selectedField, onUpdate, uniqueStepsCache));
    }

    private static void showBulk(final List<TestCaseDto> selectedItems, final Runnable onUpdate) {
        String title = "Update " + selectedItems.size() + " Test Cases";

        showMenu(title, selectedField -> {
            switch (selectedField) {
                case PRIORITY -> PriorityBulkEditor.show(selectedItems, onUpdate);
                case TITLE -> TitleBulkEditor.show(selectedItems, onUpdate);
                case EXPECTED -> ExpectedBulkEditor.show(selectedItems, onUpdate);
                case STEPS -> StepsBulkEditor.show(selectedItems, onUpdate);
                default -> System.out.println("Selected: " + selectedField.getLabel() + " (Not supported for bulk)");
            }
        });
    }

    private static void showMenu(String title, Consumer<UpdateField> onSelection) {
        UpdateField[] editableFields = Arrays.stream(UpdateField.values())
                .filter(UpdateField::isEditMenuItem)
                .toArray(UpdateField[]::new);

        GenericSelectionPopup.show(
                title,
                editableFields,
                UpdateField::getLabel,
                field -> field.getShortcutText().isEmpty() ? ' ' : field.getShortcutText().charAt(0),
                UpdateField::getIcon,
                onSelection
        );
    }
}
