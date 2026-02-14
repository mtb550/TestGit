package testGit.editorPanel.testCaseEditor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.ImageUtil;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.TestCase;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class DragDropHandler extends TransferHandler {
    private static final DataFlavor TESTCASE_LIST_FLAVOR = new DataFlavor(List.class, "List of TestCase");

    private final CollectionListModel<TestCase> model;
    private final String featurePath;
    private int[] draggedIndices;
    private List<TestCase> draggedItems;

    public DragDropHandler(final String featurePath, final CollectionListModel<TestCase> model) {
        //System.out.println("ListItemReorderHandler.ListItemReorderHandler()");
        this.model = model;
        this.featurePath = featurePath;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        //System.out.println("ListItemReorderHandler.createTransferable()");

        @SuppressWarnings("unchecked")
        JBList<TestCase> list = (JBList<TestCase>) c;
        draggedIndices = list.getSelectedIndices();
        draggedItems = list.getSelectedValuesList();

        // --- create a ghost image of the first dragged card ---
        if (!draggedItems.isEmpty()) {
            TestCase tc = draggedItems.get(0);
            Component renderer = list.getCellRenderer()
                    .getListCellRendererComponent(list, tc, draggedIndices[0], true, false);
            Rectangle cellBounds = list.getCellBounds(draggedIndices[0], draggedIndices[0]);
            renderer.setSize(cellBounds.getSize());
            BufferedImage img = ImageUtil.createImage(
                    renderer.getWidth(), renderer.getHeight(),
                    BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g2 = img.createGraphics();
            renderer.paint(g2);
            g2.dispose();
            setDragImage(img);
            setDragImageOffset(new Point(0, 0));
        }

        return new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                //System.out.println("ListItemReorderHandler.getTransferDataFlavors()");
                return new DataFlavor[]{TESTCASE_LIST_FLAVOR};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                //System.out.println("ListItemReorderHandler.isDataFlavorSupported()");
                return TESTCASE_LIST_FLAVOR.equals(flavor);
            }

            @Override
            public @NotNull Object getTransferData(DataFlavor flavor)
                    throws UnsupportedFlavorException {
                if (!isDataFlavorSupported(flavor)) {
                    throw new UnsupportedFlavorException(flavor);
                }
                return draggedItems;
            }
        };
    }

    @Override
    public int getSourceActions(JComponent c) {
        //System.out.println("ListItemReorderHandler.getSourceActions()");
        return MOVE;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        //System.out.println("ListItemReorderHandler.canImport()");
        return support.isDrop() && support.isDataFlavorSupported(TESTCASE_LIST_FLAVOR);
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) return false;

        try {
            @SuppressWarnings("unchecked")
            List<TestCase> dropped = (List<TestCase>) support.getTransferable()
                    .getTransferData(TESTCASE_LIST_FLAVOR);
            JBList.DropLocation dl = (JBList.DropLocation) support.getDropLocation();
            int index = dl.getIndex();

            // 1) إزالة العناصر من موقعها القديم
            for (int i = draggedIndices.length - 1; i >= 0; i--) {
                model.remove(draggedIndices[i]);
            }

            // 2) حساب موقع الإدراج الجديد
            int removedBefore = 0;
            for (int idx : draggedIndices) {
                if (idx < index) removedBefore++;
            }
            int insertAt = index - removedBefore;

            // 3) إضافة العناصر المنقولة للموديل
            for (TestCase tc : dropped) {
                model.add(insertAt++, tc);
            }

            // --- منطق تحديث الروابط (UUID Chain Logic) ---

            // أ. تحديث العنصر الذي أصبح الآن في بداية القائمة (Index 0)
            if (model.getSize() > 0) {
                for (int i = 0; i < model.getSize(); i++) {
                    TestCase current = model.getElementAt(i);

                    // تحديد هل هو الرأس (Head)؟
                    current.setIsHead(i == 0);

                    // تحديد العنصر التالي (Next)
                    if (i < model.getSize() - 1) {
                        TestCase nextElement = model.getElementAt(i + 1);
                        // تعيين UUID الخاص بالعنصر التالي في حقل next الحالي
                        // ملاحظة: تأكد أن حقل id في TestCase هو الذي يحتوي على الـ UUID كـ String أو استخدم UUID.fromString
                        current.setNext(UUID.fromString(nextElement.getId()));
                    } else {
                        // آخر عنصر في القائمة
                        current.setNext(null);
                    }

                    // إخطار القائمة بتحديث البيانات للرسم
                    model.contentsChanged(current);
                }
            }

            // هنا يمكنك إضافة استدعاء لدالة حفظ الملفات JSON لتثبيت الترتيب الجديد
            saveAllTestCasesToJson(featurePath);

            return true;

        } catch (UnsupportedFlavorException | IOException ex) {
            ex.printStackTrace(System.err);
            return false;
        } finally {
            draggedIndices = null;
            draggedItems = null;
        }
    }

    private void saveAllTestCasesToJson(String featurePath) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        for (int i = 0; i < model.getSize(); i++) {
            TestCase tc = model.getElementAt(i);
            File file = new File(featurePath, tc.getId() + ".json");

            try {
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, tc);
                System.out.println("drag drop Updated: " + tc.getTitle());

            } catch (IOException e) {
                System.err.println("Failed to save TestCase: " + tc.getId());
                e.printStackTrace(System.err);
            }
        }
    }

}
