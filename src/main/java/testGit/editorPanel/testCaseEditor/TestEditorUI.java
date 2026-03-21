package testGit.editorPanel.testCaseEditor;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.Disposable;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import testGit.editorPanel.*;
import testGit.editorPanel.listeners.*;
import testGit.pojo.dto.TestCaseDto;
import testGit.viewPanel.ViewPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestEditorUI implements Disposable, ToolBar.Callbacks, BaseEditorUI {

    private final JBPanel<?> mainPanel;
    private final JBList<TestCaseDto> list;
    private final CollectionListModel<TestCaseDto> model;
    private final ModelSyncListener<TestCaseDto> syncListener;
    private final ToolBar toolBar;

    @Getter
    private final StatusBar statusBar;

    @Getter
    private List<TestCaseDto> allTestCaseDtos;

    @Getter
    private Set<String> unsortedIds = new HashSet<>();

    @Getter
    @Setter
    private int currentPage = 1;

    @Getter
    @Setter
    private int pageSize = 50;

    @Getter
    @Setter
    private int hoveredIndex = -1;

    @Getter
    @Setter
    private String hoveredIconAction = null;

    public TestEditorUI(@NotNull UnifiedVirtualFile vf) {
        this.allTestCaseDtos = new ArrayList<>(vf.getTestCaseDtos());
        sortAndIdentifyUnsorted();

        this.mainPanel = new JBPanel<>(new BorderLayout());

        pageSize = PropertiesComponent.getInstance().getInt("testGit.pageSize", 50);

        // Header — 'this' implements Callbacks so the header can notify us of changes
        this.toolBar = new ToolBar(this);
        mainPanel.add(toolBar, BorderLayout.NORTH);

        // Model + sync listener
        this.model = new CollectionListModel<>(new ArrayList<>());
        this.syncListener = new ModelSyncListener<>(allTestCaseDtos, model);
        this.syncListener.setOnUpdate(() -> {
            toolBar.resetFilters();
            sortAndIdentifyUnsorted();
            refreshView();
        });
        this.model.addListDataListener(syncListener);

        // List
        this.list = new JBList<>(model);
        list.getEmptyText().setText("No test cases found").appendLine("Press Ctrl+M to add");
        list.setOpaque(true);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setDragEnabled(true);
        list.setDropMode(DropMode.INSERT);

        EditorContextMenu editorContextMenu = new EditorContextMenu(vf.getTestSet(), list, model);
        TestMouseListener testMouseListener = new TestMouseListener(list, model, vf.getTestSet(), editorContextMenu);
        list.addMouseListener(testMouseListener);

        list.setTransferHandler(new TransferListener(vf.getTestSet(), model, syncListener));
        list.setCellRenderer(new TestListRenderer(this));

        EditorContextMenu.registerShortcuts(vf.getTestSet(), list, model);
        mainPanel.add(new JBScrollPane(list), BorderLayout.CENTER);

        // Status bar
        this.statusBar = new StatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);
        PaginationController.attach(this);
        list.addListSelectionListener(new SelectionListener(list, this));

        refreshView();

        //list.addKeyListener(new KeyListener(list, this));
        new TestFocusListener(this.list, vf).register(this);

        HoverListener hoverListener = new HoverListener(list, this);
        list.addMouseMotionListener(hoverListener);
        list.addMouseListener(hoverListener);


    }

    public int getTotalPageCount() {
        return getTotalPages(getFilteredList());
    }

    public @NotNull JComponent getComponent() {
        return mainPanel;
    }

    public @Nullable JComponent getPreferredFocusedComponent() {
        return list;
    }

    // -------------------------------------------------------------------------
    // EditorHeader.Callbacks implementation
    // -------------------------------------------------------------------------

    @Override
    public void onFilterChanged() {
        applyFilters();
    }

    @Override
    public void onDetailsChanged() {
        list.setFixedCellHeight(-1);
        list.setCellRenderer(new TestListRenderer(this));
        list.revalidate();
        list.repaint();
    }

    // -------------------------------------------------------------------------
    // Getters delegated to header — used by RendererImpl
    // -------------------------------------------------------------------------

    public boolean isShowGroups() {
        return toolBar.isShowGroups();
    }

    public boolean isShowPriority() {
        return toolBar.isShowPriority();
    }

    public Set<String> getSelectedDetails() {
        return toolBar.getSelectedDetails();
    }

    // -------------------------------------------------------------------------
    // Filtering and pagination
    // -------------------------------------------------------------------------

    public void applyFilters() {
        currentPage = 1;
        refreshView();
    }

    public void refreshView() {
        List<TestCaseDto> filtered = getFilteredList();
        int totalItems = filtered.size();
        int totalPages = getTotalPages(filtered);
        if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalItems);
        List<TestCaseDto> pageItems = startIndex < totalItems
                ? new ArrayList<>(filtered.subList(startIndex, endIndex))
                : new ArrayList<>();

        syncListener.pause();
        model.replaceAll(pageItems);
        syncListener.resume();

        statusBar.updatePaginationState(currentPage, totalPages, pageItems.size(), totalItems);
    }

    /// used with refresh button that will be added to tool bar later
    public void loadData(List<TestCaseDto> loadedData) {
        this.allTestCaseDtos = loadedData;
        sortAndIdentifyUnsorted();
        this.currentPage = 1;
        refreshView();
    }

    private List<TestCaseDto> getFilteredList() {
        String query = toolBar.getSearchQuery();
        return allTestCaseDtos.stream()
                .filter(tc -> {
                    boolean matchesSearch = query.isEmpty() ||
                            (tc.getTitle() != null && tc.getTitle().toLowerCase().contains(query));
                    boolean matchesGroup = toolBar.getSelectedGroups().isEmpty() ||
                            (tc.getGroups() != null && tc.getGroups().stream().anyMatch(toolBar.getSelectedGroups()::contains));
                    return matchesSearch && matchesGroup;
                })
                .collect(Collectors.toList());
    }

    private int getTotalPages(List<TestCaseDto> filtered) {
        return filtered.isEmpty() ? 1 : (int) Math.ceil((double) filtered.size() / pageSize);
    }

    private void sortAndIdentifyUnsorted() {
        if (allTestCaseDtos == null || allTestCaseDtos.isEmpty()) return;

        // 1. خريطة للبحث السريع
        java.util.Map<String, TestCaseDto> map = allTestCaseDtos.stream()
                .collect(Collectors.toMap(TestCaseDto::getId, tc -> tc));

        // 2. إيجاد نقطة البداية (isHead)
        TestCaseDto head = allTestCaseDtos.stream()
                .filter(tc -> Boolean.TRUE.equals(tc.getIsHead()))
                .findFirst()
                .orElse(null);

        List<TestCaseDto> sortedList = new ArrayList<>();
        Set<String> sortedIds = new HashSet<>(); // 🌟 جديد: قائمة التتبع السريع والآمن
        unsortedIds.clear();

        // 3. تتبع السلسلة السليمة
        TestCaseDto current = head;
        while (current != null && !sortedIds.contains(current.getId())) {
            sortedList.add(current);
            sortedIds.add(current.getId()); // 🌟 نضيفه هنا لنتذكره

            if (current.getNext() != null) {
                current = map.get(current.getNext().toString());
            } else {
                current = null;
            }
        }

        // 4. وضع ما تبقى (غير المرتب) في النهاية
        for (TestCaseDto tc : allTestCaseDtos) {
            if (!sortedIds.contains(tc.getId())) { // 🌟 الفحص أصبح O(1) سريع جداً
                sortedList.add(tc);
                unsortedIds.add(tc.getId());
            }
        }

        // 🌟 5. الإصلاح الحاسم: نقوم بتفريغ القائمة الأصلية وتعبئتها بدلاً من إنشاء واحدة جديدة
        // هذا يحافظ على الاتصال السليم مع (ModelSyncListener)
        this.allTestCaseDtos.clear();
        this.allTestCaseDtos.addAll(sortedList);
    }

    // -------------------------------------------------------------------------
    // Disposal & memory management
    // -------------------------------------------------------------------------
    @Override
    public void dispose() {
        System.out.println("Disposing TestCaseEditorUI...");

        TestCaseDto selectedInThisFile = list.getSelectedValue();
        ViewPanel.hideIfShowing(selectedInThisFile);

        if (model != null && syncListener != null) {
            model.removeListDataListener(syncListener);
        }

        if (model != null) {
            model.removeAll();
        }
        if (mainPanel != null) {
            mainPanel.removeAll();
        }
    }
}