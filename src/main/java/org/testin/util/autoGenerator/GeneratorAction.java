package org.testin.util.autoGenerator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testin.pojo.dto.TestCaseDto;

import java.util.List;

@FunctionalInterface
public interface GeneratorAction {
    void execute(final @Nullable TestCaseDto tc, final @NotNull List<String> fqcn);
}