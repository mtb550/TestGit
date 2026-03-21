package testGit.editorPanel.listeners;

import com.intellij.ui.components.JBList;
import testGit.editorPanel.testCaseEditor.TestEditorUI;
import testGit.pojo.dto.TestCaseDto;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

///  implement this to set all shortcust keys like delete key, eescape key ..etc
/// add new shortcut, if click CTRL+C, then copy the test case title to the clipboard.
public class KeyListener extends KeyAdapter {

    private final JBList<TestCaseDto> list;
    private final TestEditorUI ui;

    public KeyListener(JBList<TestCaseDto> list, TestEditorUI ui) {
        this.list = list;
        this.ui = ui;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // نتحقق مما إذا كان الزر المضغوط هو Delete أو Backspace
        //if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {

        // جلب كل الاختبارات المحددة (يدعم الحذف المتعدد Multiple Selection)
        //List<TestCaseDto> selectedCases = list.getSelectedValuesList();

        //if (selectedCases != null && !selectedCases.isEmpty()) {

        // 1. حذف الاختبارات من القائمة الشاملة في الذاكرة
        //ui.getAllTestCaseDtos().removeAll(selectedCases);

        // 2. 🌟 تحذير هام: الحذف من الواجهة فقط لا يكفي! 🌟
        // هنا يجب أن تستدعي الكود الخاص بك الذي يحذف ملفات الـ JSON من الهارد ديسك فعلياً.
        // مثلاً:
        // for(TestCaseDto tc : selectedCases) {
        //     deleteFileFromDisk(tc);
        // }

        // 3. إعادة حساب ترتيب السلسلة (لإزالة الشارة الحمراء إذا لزم الأمر)
        // وإعادة رسم الواجهة والصفحات
        //ui.loadData(ui.getAllTestCaseDtos());
        //}
        //}
    }
}