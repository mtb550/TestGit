package org.testin.pojo;

import org.testin.pojo.dto.TestCaseDto;

@FunctionalInterface
public interface ImportSetter {
    void accept(final TestCaseDto tc, final String value);
}
