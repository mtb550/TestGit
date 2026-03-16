package testGit.ui;

import testGit.pojo.DirectoryType;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;

public class MenuOptions {
    private final DirectoryType[] availableItems = {
            DirectoryType.PA,
            DirectoryType.TS,
            DirectoryType.TR,
            DirectoryType.PR
    };

    private final Map<DirectoryType, Boolean> activeStates = new EnumMap<>(DirectoryType.class);

    public MenuOptions() {
        for (DirectoryType item : availableItems) {
            activeStates.put(item, true);
        }
    }

    public TypeConfigurator type(DirectoryType type) {
        return new TypeConfigurator(type);
    }

    public DirectoryType[] getItems() {
        return availableItems;
    }

    public Predicate<DirectoryType> getDisabledPredicate() {
        return type -> !activeStates.getOrDefault(type, true);
    }

    public class TypeConfigurator {
        private final DirectoryType currentType;

        public TypeConfigurator(DirectoryType currentType) {
            this.currentType = currentType;
        }

        public MenuOptions setActive() {
            activeStates.put(currentType, true);
            return MenuOptions.this;
        }

        public MenuOptions setInactive() {
            activeStates.put(currentType, false);
            return MenuOptions.this;
        }

        public MenuOptions setStatus(boolean status) {
            activeStates.put(currentType, status);
            return MenuOptions.this;
        }
    }
}
