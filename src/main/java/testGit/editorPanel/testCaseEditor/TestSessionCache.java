package testGit.editorPanel.testCaseEditor;

import com.intellij.openapi.application.ApplicationManager;
import lombok.Setter;
import testGit.pojo.Config;
import testGit.pojo.dto.TestCaseDto;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class TestSessionCache {

    private final Path directoryPath;
    private final List<TestCaseDto> loadedItems = Collections.synchronizedList(new ArrayList<>());
    @Setter
    private CacheListener listener;

    public TestSessionCache(final Path directoryPath) {
        this.directoryPath = directoryPath;
    }

    public List<TestCaseDto> getLoadedItems() {
        return new ArrayList<>(loadedItems);
    }

    public void startLoadingAsync() {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            List<TestCaseDto> batch = new ArrayList<>();
            final int BATCH_SIZE = 5;

            try (Stream<Path> paths = Files.list(directoryPath)) {
                paths.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".json"))
                        .forEach(filePath -> {
                            try {
                                TestCaseDto tc = Config.getMapper().readValue(filePath.toFile(), TestCaseDto.class);
                                if (tc != null) {
                                    loadedItems.add(tc);
                                    batch.add(tc);

                                    if (batch.size() >= BATCH_SIZE) {
                                        List<TestCaseDto> itemsToSend = new ArrayList<>(batch);
                                        batch.clear();

                                        if (listener != null) {
                                            ApplicationManager.getApplication().invokeLater(() ->
                                                    listener.onItemsLoaded(itemsToSend));
                                        }
                                    }
                                }
                            } catch (Exception ignored) {}
                        });

                if (!batch.isEmpty() && listener != null) {
                    ApplicationManager.getApplication().invokeLater(() ->
                            listener.onItemsLoaded(batch));
                }

            } catch (Exception e) {
                e.printStackTrace(System.out);
            }

            if (listener != null) {
                ApplicationManager.getApplication().invokeLater(() ->
                        listener.onLoadComplete(getLoadedItems()));
            }
        });
    }

    public interface CacheListener {
        void onItemsLoaded(List<TestCaseDto> items);
        void onLoadComplete(List<TestCaseDto> allItems);
    }
}