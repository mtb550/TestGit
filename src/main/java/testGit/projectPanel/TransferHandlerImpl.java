package testGit.projectPanel;

import com.intellij.openapi.application.WriteAction;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.Directory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TransferHandlerImpl extends TransferHandler {
    private static final DataFlavor NODE_FLAVOR;

    static {
        try {
            NODE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + DefaultMutableTreeNode[].class.getName() + "\"");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to create custom DataFlavor", e);
        }
    }

    private final Set<DefaultMutableTreeNode> cutNodes = new HashSet<>();
    private final SimpleTree tree;

    public TransferHandlerImpl(SimpleTree tree) {
        this.tree = tree;
        tree.setDropMode(DropMode.ON);
        tree.setDragEnabled(true);
        tree.setTransferHandler(this);
    }


    @Override
    public int getSourceActions(JComponent c) {
        System.out.println("TransferHandlerImpl.getSourceActions " + c.getName());
        return COPY_OR_MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        System.out.println("TransferHandlerImpl.createTransferable. path: " + Arrays.toString(tree.getSelectionPaths()));

        TreePath[] paths = tree.getSelectionPaths();
        if (paths == null) return null;

        DefaultMutableTreeNode[] nodes = Arrays.stream(paths)
                .map(path -> (DefaultMutableTreeNode) path.getLastPathComponent())
                .toArray(DefaultMutableTreeNode[]::new);

        return new NodesTransferable(nodes);
    }

    @Override
    public boolean canImport(TransferSupport support) {
        // Always check if we support the data flavor
        if (!support.isDataFlavorSupported(NODE_FLAVOR)) return false;

        // Logic for Mouse Drop
        if (support.isDrop()) {
            TreePath dropPath = ((SimpleTree.DropLocation) support.getDropLocation()).getPath();
            if (dropPath == null) return false;
            DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) dropPath.getLastPathComponent();
            return isValidTarget(targetNode);
        }

        // Logic for Keyboard Paste (Ctrl+V)
        // We check if something is selected in the tree to act as the target
        return tree.getSelectionPath() != null;
    }

    // Helper to encapsulate your business rules (DRY principle)
    private boolean isValidTarget(DefaultMutableTreeNode targetNode) {
        if (!(targetNode.getUserObject() instanceof Directory targetDir)) return false;
        // Add your existing constraints here (e.g., TS/PR types)
        return true;
    }

    @Override
    public boolean importData(TransferSupport support) {
        System.out.println("TransferHandlerImpl.importData()");
        if (!canImport(support)) {
            System.out.println("can not import data");
            return false;
        }

        try {
            DefaultMutableTreeNode[] nodes = (DefaultMutableTreeNode[]) support.getTransferable().getTransferData(NODE_FLAVOR);

            DefaultMutableTreeNode targetNode;
            if (support.isDrop()) {
                targetNode = (DefaultMutableTreeNode) ((SimpleTree.DropLocation) support.getDropLocation()).getPath().getLastPathComponent();
            } else {
                targetNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            }

            if (targetNode == null) return false;

            // SAFE ACTION DETECTION
            // If it's a drop, get the actual drop action.
            // If it's a keyboard paste, we default to COPY (or MOVE if you prefer).
            int action = support.isDrop() ? support.getDropAction() : COPY;

            WriteAction.run(() -> {
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                for (DefaultMutableTreeNode node : nodes) {
                    if (action == MOVE) {
                        // 1. Move logic
                        model.removeNodeFromParent(node);
                        persistMove((Directory) node.getUserObject(), (Directory) targetNode.getUserObject());
                        // Insert the ORIGINAL node
                        model.insertNodeInto(node, targetNode, targetNode.getChildCount());
                    } else {
                        // 2. Copy logic
                        // Use the CLONED node for insertion
                        DefaultMutableTreeNode clone = cloneNode(node);
                        persistCopy((Directory) node.getUserObject(), (Directory) targetNode.getUserObject());
                        model.insertNodeInto(clone, targetNode, targetNode.getChildCount());
                    }
                }

                // Clear the visual "cut" state after operation completes
                cutNodes.clear();
                tree.repaint();
            });

            return true;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return false;
        }
    }

    private DefaultMutableTreeNode cloneNode(DefaultMutableTreeNode node) {
        Directory dir = (Directory) node.getUserObject();
        // Assuming your Directory POJO has a copy constructor or similar setters
        Directory newDir = new Directory()
                .setFile(dir.getFile()) // You may need to adjust this to the new path
                .setName(dir.getName())
                .setType(dir.getType());
        return new DefaultMutableTreeNode(newDir);
    }

    private void persistMove(Directory source, Directory target) {
        System.out.println("Persisting MOVE to disk: " + source.getFileName());
        com.intellij.openapi.vfs.VirtualFile vFile = com.intellij.openapi.vfs.LocalFileSystem.getInstance().findFileByIoFile(source.getFile());
        com.intellij.openapi.vfs.VirtualFile targetDir = com.intellij.openapi.vfs.LocalFileSystem.getInstance().findFileByIoFile(target.getFile());
        try {
            if (vFile != null && targetDir != null) vFile.move(this, targetDir);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void persistCopy(Directory source, Directory target) {
        System.out.println("Persisting COPY to disk: " + source.getFileName());
        com.intellij.openapi.vfs.VirtualFile vFile = com.intellij.openapi.vfs.LocalFileSystem.getInstance().findFileByIoFile(source.getFile());
        com.intellij.openapi.vfs.VirtualFile targetDir = com.intellij.openapi.vfs.LocalFileSystem.getInstance().findFileByIoFile(target.getFile());
        try {
            if (vFile != null && targetDir != null) vFile.copy(this, targetDir, vFile.getName());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        System.out.println("TransferHandlerImpl.exportDone()");
        if (action == MOVE) {
            try {
                DefaultMutableTreeNode[] nodes = (DefaultMutableTreeNode[]) data.getTransferData(NODE_FLAVOR);
                cutNodes.addAll(Arrays.asList(nodes));
                tree.repaint(); // Triggers the renderer to update colors
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
            System.out.println("Nodes moved successfully, cleaning up source...");
            // Here you would remove the nodes from the original parent
            // IF they weren't already moved by importData.
        }
    }

    private record NodesTransferable(DefaultMutableTreeNode[] nodes) implements Transferable {
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            System.out.println("TransferHandlerImpl.getTransferDataFlavors()");
            return new DataFlavor[]{NODE_FLAVOR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            System.out.println("TransferHandlerImpl.isDataFlavorSupported()");
            return NODE_FLAVOR.equals(flavor);
        }

        @Override
        public @NotNull Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            System.out.println("TransferHandlerImpl.getTransferData()");
            if (!isDataFlavorSupported(flavor)) throw new UnsupportedFlavorException(flavor);
            return nodes;
        }
    }
}