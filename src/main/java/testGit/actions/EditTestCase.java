package testGit.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import testGit.pojo.Config;
import testGit.pojo.Priority;
import testGit.pojo.dto.TestCaseDto;
import testGit.ui.GenericSelectionPopup;
import testGit.util.KeyboardSet;
import testGit.viewPanel.TestCaseDetailsPanel;
import testGit.viewPanel.ToolWindowFactoryImpl;
import testGit.viewPanel.ViewPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.List;

public class EditTestCase extends DumbAwareAction {

    private final JBList<TestCaseDto> list;
    private final Path path;
    private final TestCaseDetailsPanel panelContext;

    public EditTestCase(final JBList<TestCaseDto> list, final Path path) {
        super("Edit Test Case");
        this.list = list;
        this.path = path;
        this.panelContext = null;
        this.registerCustomShortcutSet(KeyboardSet.UpdateTestCase.getShortcut(), list);

        /// to be implemented. to update the test case from UI without use view panel
        //this.registerCustomShortcutSet(KeyboardSet.UpdateTestCaseFase.getShortcut(), list);
    }

    public EditTestCase(final TestCaseDetailsPanel panelContext, final JComponent targetComponent) {
        super("Edit Test Case");
        this.panelContext = panelContext;
        this.list = null;
        this.path = null;
        this.registerCustomShortcutSet(KeyboardSet.UpdateTestCase.getShortcut(), targetComponent);
    }

    @Override
    public void actionPerformed(final @NotNull AnActionEvent e) {
        // 1. التعديل من داخل نافذة التفاصيل
        if (panelContext != null) {
            if (!panelContext.isEditing()) {
                panelContext.toggleEditMode(true);
            }
            return;
        }

        // 2. التعديل من القائمة (JBList)
        if (list != null) {
            List<TestCaseDto> selectedItems = list.getSelectedValuesList();

            if (selectedItems.isEmpty()) return;

            // مسار الـ Multi-Edit (تحديد أكثر من عنصر)
            if (selectedItems.size() > 1) {
                showMultiEditPopup(selectedItems);
            }
            // مسار التعديل الفردي العادي
            else {
                TestCaseDto targetDto = selectedItems.get(0);
                ViewPanel.show(targetDto, path);

                SwingUtilities.invokeLater(() -> {
                    TestCaseDetailsPanel detailsPanel = ToolWindowFactoryImpl.getDetailsInstance();
                    if (detailsPanel != null && !detailsPanel.isEditing()) {
                        detailsPanel.toggleEditMode(true);
                    }
                });
            }
        }
    }

    // ==========================================================
    // منطق الـ Multi-Edit Popups
    // ==========================================================

    private void showMultiEditPopup(List<TestCaseDto> selectedItems) {
        GenericSelectionPopup.show(
                "Update " + selectedItems.size() + " Test Cases",
                UpdateField.values(),
                UpdateField::getLabel,
                UpdateField::getShortcut,
                selectedField -> {
                    if (selectedField == UpdateField.PRIORITY) {
                        showPrioritySelectionPopup(selectedItems);

                    } else if (selectedField == UpdateField.TITLE) {
                        showTitleBulkEditPopup(selectedItems); // 🌟 استدعاء شاشة العناوين

                    } else {
                        // سنكمل الباقي لاحقاً كما طلبت
                        System.out.println("Selected: " + selectedField.getLabel() + " (To be implemented)");
                    }
                }
        );
    }

    private void showPrioritySelectionPopup(List<TestCaseDto> selectedItems) {
        GenericSelectionPopup.show(
                "Select Priority",
                Priority.values(),
                Priority::name, // أو Priority::name حسب ما تفضله لعرض الاسم
                p -> p.name().charAt(0),  // أخذ أول حرف كاختصار (مثلاً H لـ High، M لـ Medium)
                selectedPriority -> {
                    // تطبيق الأولوية الجديدة على جميع العناصر المحددة
                    for (TestCaseDto tc : selectedItems) {
                        tc.setPriority(selectedPriority);
                    }

                    // تحديث القائمة في واجهة المستخدم
                    list.repaint();

                    // TODO: يمكنك هنا إضافة الاستدعاء الخاص بحفظ التعديلات في قاعدة البيانات
                    System.out.println("Priority updated to " + selectedPriority + " for " + selectedItems.size() + " test cases.");
                }
        );
    }

    // 🌟 الشاشة السحرية لتعديل العناوين بالـ Multi-Cursor (تصميم Buttonless)
    private void showTitleBulkEditPopup(List<TestCaseDto> selectedItems) {
        Project project = Config.getProject();
        if (project == null) return;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectedItems.size(); i++) {
            sb.append(selectedItems.get(i).getTitle());
            if (i < selectedItems.size() - 1) sb.append("\n");
        }

        Document document = EditorFactory.getInstance().createDocument(sb.toString());
        Editor editor = EditorFactory.getInstance().createEditor(document, project);
        EditorSettings settings = editor.getSettings();
        settings.setLineNumbersShown(true);
        settings.setLineMarkerAreaShown(false);
        settings.setFoldingOutlineShown(false);
        settings.setVirtualSpace(true); // ضروري لدعم الـ Multi-Caret
        settings.setUseSoftWraps(false); // إجبار التمرير الأفقي (Horizontal Scroll) إذا كان النص طويلاً
        settings.setAdditionalLinesCount(0); // إزالة المساحة الفارغة في الأسفل
        settings.setAdditionalColumnsCount(5); // إعطاء مساحة أفقية مريحة للكتابة

        if (editor instanceof EditorEx) {
            ((EditorEx) editor).getGutterComponentEx().registerTextAnnotation(new TextAnnotationGutterProvider() {
                @Nullable
                @Override
                public String getLineText(int line, Editor editor) {
                    if (line >= 0 && line < selectedItems.size()) {
                        String orig = selectedItems.get(line).getTitle();
                        return orig.length() > 40 ? orig.substring(0, 37) + "..." : orig;
                    }
                    return null;
                }

                @Nullable
                @Override
                public String getToolTip(int line, Editor editor) {
                    return (line >= 0 && line < selectedItems.size()) ? selectedItems.get(line).getTitle() : null;
                }

                @Override
                public EditorFontType getStyle(int line, Editor editor) {
                    return EditorFontType.ITALIC;
                }

                @Nullable
                @Override
                public ColorKey getColor(int line, Editor editor) {
                    return EditorColors.ANNOTATIONS_COLOR;
                }

                @Nullable
                @Override
                public Color getBgColor(int line, Editor editor) {
                    return null;
                }

                @Override
                public List<AnAction> getPopupActions(int line, Editor editor) {
                    return null;
                }

                @Override
                public void gutterClosed() {
                }
            });

            ((EditorEx) editor).getGutterComponentEx().revalidateMarkup();
        }

        // تصميم الـ Buttonless (محرر فقط بدون نصوص أو أزرار)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(editor.getComponent(), BorderLayout.CENTER);
        mainPanel.setPreferredSize(new Dimension(JBUI.scale(700), JBUI.scale(250)));

        JBPopup popup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(mainPanel, editor.getContentComponent())
                .setTitle("Bulk Edit Titles")
                .setRequestFocus(true)
                .setCancelOnClickOutside(true) // السماح بالإغلاق عند النقر بالخارج
                .setMovable(true)
                .setResizable(true)
                .createPopup();

        // منطق الحفظ
        Runnable saveLogic = () -> {
            String[] newTitles = document.getText().split("\n");
            int limit = Math.min(newTitles.length, selectedItems.size());
            for (int i = 0; i < limit; i++) {
                if (!newTitles[i].trim().isEmpty()) {
                    selectedItems.get(i).setTitle(newTitles[i].trim());
                }
            }
            list.repaint();
            popup.closeOk(null);
        };

        // 🌟 منع إضافة أسطر جديدة واستخدام الـ Enter للحفظ مباشرة!
        new DumbAwareAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                saveLogic.run();
            }
        }.registerCustomShortcutSet(new CustomShortcutSet(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0)), editor.getContentComponent());

        popup.addListener(new JBPopupListener() {
            @Override
            public void onClosed(@NotNull LightweightWindowEvent event) {
                if (!editor.isDisposed()) {
                    EditorFactory.getInstance().releaseEditor(editor);
                }
            }
        });

        popup.showCenteredInCurrentWindow(project);
    }

    // ==========================================================
    // Enum لتحديد الحقول مع اختصاراتها الدقيقة
    // ==========================================================
    public enum UpdateField {
        TITLE("Title", 'T'),
        EXPECTED("Expected Results", 'E'),
        STEPS("Steps", 'S'),
        PRIORITY("Priority", 'P');
        //SEVERITY("Severity", 's'); // حرف صغير لتفريقه عن Steps /// not here. to be in test run editor

        private final String label;
        private final char shortcut;

        UpdateField(String label, char shortcut) {
            this.label = label;
            this.shortcut = shortcut;
        }

        public String getLabel() {
            return label;
        }

        public char getShortcut() {
            return shortcut;
        }
    }
}