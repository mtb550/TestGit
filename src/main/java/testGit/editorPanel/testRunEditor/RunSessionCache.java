package testGit.editorPanel.testRunEditor;

import com.intellij.openapi.application.ApplicationManager;
import lombok.Setter;
import testGit.pojo.Config;
import testGit.pojo.dto.TestCaseDto;
import testGit.pojo.dto.TestRunDto;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class RunSessionCache {

    private final TestRunDto metadata;
    private final List<TestCaseDto> loadedItems = Collections.synchronizedList(new ArrayList<>());
    @Setter
    private CacheListener listener;

    public RunSessionCache(final TestRunDto metadata) {
        this.metadata = metadata;
    }

    public List<TestCaseDto> getLoadedItems() {
        return new ArrayList<>(loadedItems);
    }

    public void startLoadingAsync() {
        if (metadata == null || metadata.getTestCase() == null || metadata.getTestCase().isEmpty()) {
            if (listener != null) listener.onLoadComplete(Collections.emptyList());
            return;
        }

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            List<TestCaseDto> batch = new ArrayList<>();
            final int BATCH_SIZE = 5;

            for (TestRunDto.TestCase tcPathObj : metadata.getTestCase()) {
                Path dirPath = tcPathObj.getPath();
                List<UUID> targetIds = tcPathObj.getUuid();

                if (dirPath == null || !Files.exists(dirPath) || targetIds == null || targetIds.isEmpty()) {
                    continue;
                }

                Set<UUID> idsToFind = new HashSet<>(targetIds);

                try (Stream<Path> paths = Files.list(dirPath)) {
                    paths.filter(Files::isRegularFile)
                            .filter(p -> p.toString().endsWith(".json"))
                            .forEach(filePath -> {
                                try {
                                    TestCaseDto tc = Config.getMapper().readValue(filePath.toFile(), TestCaseDto.class);
                                    if (tc != null && tc.getId() != null && idsToFind.contains(tc.getId())) {
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
                                } catch (Exception ignored) {
                                }
                            });
                } catch (Exception e) {
                    System.err.println("Failed to load cases from: " + dirPath);
                }
            }

            if (!batch.isEmpty() && listener != null) {
                ApplicationManager.getApplication().invokeLater(() ->
                        listener.onItemsLoaded(batch));
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